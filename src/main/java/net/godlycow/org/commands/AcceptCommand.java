package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.manager.TeamManager;
import net.godlycow.org.team.Team;
import net.godlycow.org.team.TeamInvite;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptCommand extends TeamSubCommand {
   public AcceptCommand(SimpleTeams plugin) {
      super(plugin);
   }

   public String getName() {
      return "accept";
   }

   public String getDescription() {
      return "Accept a pending team invite";
   }

   public String getUsage() {
      return "accept";
   }

   public String getPermission() {
      return "simpleteams.player.accept";
   }

   public boolean isPlayerOnly() {
      return true;
   }

   public void execute(CommandSender sender, String[] args) {
      Player player = (Player)sender;
      TeamInvite invite = this.plugin.getTeamManager().getPendingInvite(player.getUniqueId());
      if (invite == null) {
         this.plugin.getMessageManager().send(sender, "inviteNoPending");
      } else {
         Team team = this.plugin.getTeamManager().getTeam(invite.getTeamId());
         if (team == null) {
            this.plugin.getMessageManager().send(sender, "teamNotFound", "team", "?");
         } else {
            TeamManager.JoinResult result = this.plugin.getTeamManager().joinTeam(player, team);
            if (result == TeamManager.JoinResult.SUCCESS) {
               this.plugin.getTeamManager().clearInvite(player.getUniqueId());
               this.plugin.getMessageManager().send(sender, "inviteAccepted", "team", team.getName());
               this.broadcast(team, player.getUniqueId(), "inviteAcceptedBroadcast", new String[]{"player", player.getName()});
            } else {
               this.plugin.getTeamManager().clearInvite(player.getUniqueId());
               this.plugin.getMessageManager().send(sender, resultMessage(result, team), resultPlaceholders(result, team));
            }
         }
      }
   }

   private String resultMessage(TeamManager.JoinResult result, Team team) {
      return switch (result) {
         case BANNED -> "bannedFromTeam";
         case CLOSED -> "teamIsClosed";
         case FULL -> "teamFull";
         default -> "alreadyInTeam";
      };
   }

   private String[] resultPlaceholders(TeamManager.JoinResult result, Team team) {
      return switch (result) {
         case BANNED -> new String[]{"team", team.getName()};
         case FULL -> new String[]{"count", String.valueOf(team.getMemberCount()), "max", String.valueOf(this.plugin.getConfigManager().getMaxMembers())};
         default -> new String[0];
      };
   }
}
