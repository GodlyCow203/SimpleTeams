package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import net.godlycow.org.team.TeamRank;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CloseCommand extends TeamSubCommand {
   public CloseCommand(SimpleTeams plugin) {
      super(plugin);
   }

   public String getName() {
      return "close";
   }

   public String getDescription() {
      return "Close team from public joining";
   }

   public String getUsage() {
      return "close";
   }

   public String getPermission() {
      return "simpleteams.player.close";
   }

   public boolean isPlayerOnly() {
      return true;
   }

   public void execute(CommandSender sender, String[] args) {
      Player player = (Player)sender;
      Team team = this.plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
      if (team == null) {
         this.plugin.getMessageManager().send(sender, "notInTeam");
      } else if (!team.getMemberRank(player.getUniqueId()).isAtLeast(TeamRank.MODERATOR)) {
         this.plugin.getMessageManager().send(sender, "notEnoughPermissionInTeam");
      } else {
         team.setOpen(false);
         this.plugin.getTeamManager().saveTeam(team);
         this.plugin.getMessageManager().send(sender, "teamClosed");
      }
   }
}
