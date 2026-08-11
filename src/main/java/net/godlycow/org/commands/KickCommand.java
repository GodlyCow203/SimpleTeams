package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import net.godlycow.org.team.TeamRank;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand extends TeamSubCommand {
   public KickCommand(SimpleTeams plugin) {
      super(plugin);
   }

   public String getName() {
      return "kick";
   }

   public String getDescription() {
      return "Kick a member from your team";
   }

   public String getUsage() {
      return "kick <player>";
   }

   public String getPermission() {
      return "simpleteams.player.kick";
   }

   public boolean isPlayerOnly() {
      return true;
   }

   public void execute(CommandSender sender, String[] args) {
      if (args.length < 2) {
         this.plugin.getMessageManager().send(sender, "usageKick");
      } else {
         Player kicker = (Player)sender;
         Team team = this.plugin.getTeamManager().getPlayerTeam(kicker.getUniqueId());
         if (team == null) {
            this.plugin.getMessageManager().send(sender, "notInTeam");
         } else {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
               this.plugin.getMessageManager().send(sender, "playerNotFound", "player", args[1]);
            } else if (target.getUniqueId().equals(kicker.getUniqueId())) {
               this.plugin.getMessageManager().send(sender, "cannotTargetSelf");
            } else if (!team.hasMember(target.getUniqueId())) {
               this.plugin.getMessageManager().send(sender, "targetNotInTeam", "player", target.getName());
            } else {
               TeamRank kickerRank = team.getMemberRank(kicker.getUniqueId());
               TeamRank targetRank = team.getMemberRank(target.getUniqueId());
               if (kickerRank.isAtLeast(TeamRank.MODERATOR) && kickerRank.getPriority() > targetRank.getPriority()) {
                  this.plugin.getTeamManager().kickMember(target.getUniqueId(), team);
                  this.plugin.getMessageManager().send(target, "playerKickedTarget", "team", team.getName());
                  this.broadcast(team, target.getUniqueId(), "playerKickedBroadcast", new String[]{"player", target.getName()});
               } else {
                  this.plugin.getMessageManager().send(sender, "cannotKickHigherRank");
               }
            }
         }
      }
   }

   public List<String> tabComplete(CommandSender sender, String[] args) {
      if (args.length == 2 && sender instanceof Player p) {
         Team team = this.plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
         return team == null ? List.of() : (List)team.getMembers().keySet().stream().map((uuid) -> (String)Optional.ofNullable(Bukkit.getOfflinePlayer(uuid).getName()).orElse("")).filter((n) -> !n.isEmpty() && n.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
      } else {
         return List.of();
      }
   }
}
