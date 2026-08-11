package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import net.godlycow.org.team.TeamRank;
import java.util.Arrays;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanCommand extends TeamSubCommand {

    public BanCommand(SimpleTeams plugin) {
        super(plugin);
    }

    public String getName() {
        return "ban";
    }

    public String getDescription() {
        return "Ban a player from your team";
    }

    public String getUsage() {
        return "ban <player>";
    }

    public String getPermission() {
        return "simpleteams.player.ban";
    }

    public boolean isPlayerOnly() {
        return true;
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            plugin.getMessageManager().send(sender, "usageBan");
            return;
        }

        Player banner = (Player) sender;
        Team team = plugin.getTeamManager().getPlayerTeam(banner.getUniqueId());

        if (team == null) {
            plugin.getMessageManager().send(sender, "notInTeam");
            return;
        }

        if (!team.getMemberRank(banner.getUniqueId()).isAtLeast(TeamRank.MODERATOR)) {
            plugin.getMessageManager().send(sender, "notEnoughPermissionInTeam");
            return;
        }

        String targetName = args[1];
        OfflinePlayer offlineTarget = resolveOfflinePlayer(targetName);

        if (offlineTarget == null) {
            plugin.getMessageManager().send(sender, "playerOfflineOrNotFound", "player", targetName);
            return;
        }

        UUID targetId = offlineTarget.getUniqueId();
        String resolvedName = offlineTarget.getName() != null ? offlineTarget.getName() : targetName;

        if (targetId.equals(banner.getUniqueId())) {
            plugin.getMessageManager().send(sender, "cannotTargetSelf");
            return;
        }

        if (team.isBanned(targetId)) {
            plugin.getMessageManager().send(sender, "alreadyBanned", "player", resolvedName);
            return;
        }

        if (team.hasMember(targetId)) {
            TeamRank bannerRank = team.getMemberRank(banner.getUniqueId());
            TeamRank targetRank = team.getMemberRank(targetId);
            if (bannerRank.getPriority() <= targetRank.getPriority()) {
                plugin.getMessageManager().send(sender, "cannotBanHigherRank");
                return;
            }
        }

        plugin.getTeamManager().banMember(targetId, team);
        plugin.getMessageManager().send(sender, "playerBanned", "player", resolvedName);

        Player onlineTarget = Bukkit.getPlayer(targetId);
        if (onlineTarget != null) {
            plugin.getMessageManager().send(onlineTarget, "playerBannedTarget", "team", team.getName());
        }
    }

    @SuppressWarnings("deprecation")
    private OfflinePlayer resolveOfflinePlayer(String name) {
        Player online = Bukkit.getPlayer(name);
        if (online != null) {
            return online;
        }
        OfflinePlayer offline = Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(p -> name.equalsIgnoreCase(p.getName()))
                .findFirst()
                .orElse(null);
        return (offline != null && offline.hasPlayedBefore()) ? offline : null;
    }
}
