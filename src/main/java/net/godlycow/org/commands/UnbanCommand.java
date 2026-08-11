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

public class UnbanCommand extends TeamSubCommand {

    public UnbanCommand(SimpleTeams plugin) {
        super(plugin);
    }

    public String getName() {
        return "unban";
    }

    public String getDescription() {
        return "Unban a player from your team";
    }

    public String getUsage() {
        return "unban <player>";
    }

    public String getPermission() {
        return "simpleteams.player.unban";
    }

    public boolean isPlayerOnly() {
        return true;
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            plugin.getMessageManager().send(sender, "usageUnban");
            return;
        }

        Player unbanner = (Player) sender;
        Team team = plugin.getTeamManager().getPlayerTeam(unbanner.getUniqueId());

        if (team == null) {
            plugin.getMessageManager().send(sender, "notInTeam");
            return;
        }

        if (!team.getMemberRank(unbanner.getUniqueId()).isAtLeast(TeamRank.MODERATOR)) {
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

        if (!team.isBanned(targetId)) {
            plugin.getMessageManager().send(sender, "notBanned", "player", resolvedName);
            return;
        }

        plugin.getTeamManager().unbanMember(targetId, team);
        plugin.getMessageManager().send(sender, "playerUnbanned", "player", resolvedName);
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
