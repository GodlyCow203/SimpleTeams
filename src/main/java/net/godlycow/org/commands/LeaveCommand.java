package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand extends TeamSubCommand {
   public LeaveCommand(SimpleTeams plugin) {
      super(plugin);
   }

   public String getName() {
      return "leave";
   }

   public String getDescription() {
      return "Leave your current team";
   }

   public String getUsage() {
      return "leave";
   }

   public String getPermission() {
      return "simpleteams.player.leave";
   }

   public boolean isPlayerOnly() {
      return true;
   }

   public void execute(CommandSender sender, String[] args) {
      Player player = (Player)sender;
      Team team = this.plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
      if (team == null) {
         this.plugin.getMessageManager().send(sender, "notInTeam");
      } else {
         String teamName = team.getName();
         boolean wasLeader = team.isLeader(player.getUniqueId());
         if (wasLeader) {
            this.broadcast(team, (UUID)null, "playerDisbandedBroadcast", new String[]{"player", player.getName()});
            this.plugin.getTeamManager().leaveTeam(player);
            this.plugin.getMessageManager().send(sender, "leaderLeftDisbanded", "team", teamName);
         } else {
            this.broadcast(team, player.getUniqueId(), "playerLeftBroadcast", new String[]{"player", player.getName()});
            this.plugin.getTeamManager().leaveTeam(player);
            this.plugin.getMessageManager().send(sender, "teamLeft", "team", teamName);
         }

      }
   }
}
