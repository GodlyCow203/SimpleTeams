package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminKickCommand extends TeamSubCommand {
   public AdminKickCommand(SimpleTeams plugin) {
      super(plugin);
   }

   public String getName() {
      return "adminkick";
   }

   public String getDescription() {
      return "Admin: kick player from any team";
   }

   public String getUsage() {
      return "adminkick <player>";
   }

   public String getPermission() {
      return "simpleteams.admin.kick";
   }

   public boolean isPlayerOnly() {
      return false;
   }

   public void execute(CommandSender sender, String[] args) {
      if (args.length < 2) {
         this.plugin.getMessageManager().send(sender, "usageAdminkick");
      } else {
         Player target = Bukkit.getPlayer(args[1]);
         if (target == null) {
            this.plugin.getMessageManager().send(sender, "playerNotFound", "player", args[1]);
         } else {
            Team team = this.plugin.getTeamManager().getPlayerTeam(target.getUniqueId());
            if (team == null) {
               this.plugin.getMessageManager().send(sender, "notInTeam");
            } else {
               this.plugin.getTeamManager().kickMember(target.getUniqueId(), team);
               this.plugin.getMessageManager().send(sender, "playerKickedBroadcast", "player", target.getName());
               this.plugin.getMessageManager().send(target, "playerKickedTarget", "team", team.getName());
            }
         }
      }
   }
}
