package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import net.godlycow.org.team.TeamRank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PromoteCommand extends TeamSubCommand {
   public PromoteCommand(SimpleTeams plugin) {
      super(plugin);
   }

   public String getName() {
      return "promote";
   }

   public String getDescription() {
      return "Promote a team member";
   }

   public String getUsage() {
      return "promote <player>";
   }

   public String getPermission() {
      return "simpleteams.player.promote";
   }

   public boolean isPlayerOnly() {
      return true;
   }

   public void execute(CommandSender sender, String[] args) {
      if (args.length < 2) {
         this.plugin.getMessageManager().send(sender, "usagePromote");
      } else {
         Player promoter = (Player)sender;
         Team team = this.plugin.getTeamManager().getPlayerTeam(promoter.getUniqueId());
         if (team == null) {
            this.plugin.getMessageManager().send(sender, "notInTeam");
         } else if (!team.isLeader(promoter.getUniqueId())) {
            this.plugin.getMessageManager().send(sender, "notEnoughPermissionInTeam");
         } else {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
               this.plugin.getMessageManager().send(sender, "playerNotFound", "player", args[1]);
            } else if (!team.hasMember(target.getUniqueId())) {
               this.plugin.getMessageManager().send(sender, "targetNotInTeam", "player", target.getName());
            } else if (target.getUniqueId().equals(promoter.getUniqueId())) {
               this.plugin.getMessageManager().send(sender, "cannotTargetSelf");
            } else {
               TeamRank current = team.getMemberRank(target.getUniqueId());
               TeamRank next = current.next();
               if (next == null) {
                  this.plugin.getMessageManager().send(sender, "cannotPromoteLeader");
               } else {
                  team.setMemberRank(target.getUniqueId(), next);
                  this.plugin.getTeamManager().saveTeam(team);
                  this.plugin.getMessageManager().send(sender, "promoted", "player", target.getName(), "rank", next.getDisplayName());
                  this.plugin.getMessageManager().send(target, "promotedTarget", "rank", next.getDisplayName(), "team", team.getName());
               }
            }
         }
      }
   }
}
