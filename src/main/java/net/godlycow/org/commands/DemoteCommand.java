package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import net.godlycow.org.team.TeamRank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DemoteCommand extends TeamSubCommand {
   public DemoteCommand(SimpleTeams plugin) {
      super(plugin);
   }

   public String getName() {
      return "demote";
   }

   public String getDescription() {
      return "Demote a team member";
   }

   public String getUsage() {
      return "demote <player>";
   }

   public String getPermission() {
      return "simpleteams.player.demote";
   }

   public boolean isPlayerOnly() {
      return true;
   }

   public void execute(CommandSender sender, String[] args) {
      if (args.length < 2) {
         this.plugin.getMessageManager().send(sender, "usageDemote");
      } else {
         Player demoter = (Player)sender;
         Team team = this.plugin.getTeamManager().getPlayerTeam(demoter.getUniqueId());
         if (team == null) {
            this.plugin.getMessageManager().send(sender, "notInTeam");
         } else if (!team.isLeader(demoter.getUniqueId())) {
            this.plugin.getMessageManager().send(sender, "notEnoughPermissionInTeam");
         } else {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
               this.plugin.getMessageManager().send(sender, "playerNotFound", "player", args[1]);
            } else if (!team.hasMember(target.getUniqueId())) {
               this.plugin.getMessageManager().send(sender, "targetNotInTeam", "player", target.getName());
            } else if (target.getUniqueId().equals(demoter.getUniqueId())) {
               this.plugin.getMessageManager().send(sender, "cannotTargetSelf");
            } else {
               TeamRank current = team.getMemberRank(target.getUniqueId());
               TeamRank prev = current.previous();
               if (prev == null) {
                  this.plugin.getMessageManager().send(sender, "cannotDemote", "player", target.getName());
               } else {
                  team.setMemberRank(target.getUniqueId(), prev);
                  this.plugin.getTeamManager().saveTeam(team);
                  this.plugin.getMessageManager().send(sender, "demoted", "player", target.getName(), "rank", prev.getDisplayName());
                  this.plugin.getMessageManager().send(target, "demotedTarget", "rank", prev.getDisplayName(), "team", team.getName());
               }
            }
         }
      }
   }
}
