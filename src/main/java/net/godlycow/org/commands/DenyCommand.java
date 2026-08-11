package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import net.godlycow.org.team.TeamInvite;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DenyCommand extends TeamSubCommand {
   public DenyCommand(SimpleTeams plugin) {
      super(plugin);
   }

   public String getName() {
      return "deny";
   }

   public String getDescription() {
      return "Deny a pending team invite";
   }

   public String getUsage() {
      return "deny";
   }

   public String getPermission() {
      return "simpleteams.player.deny";
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
         String teamName = team != null ? team.getName() : "?";
         this.plugin.getTeamManager().clearInvite(player.getUniqueId());
         this.plugin.getMessageManager().send(sender, "inviteDenied", "team", teamName);
         if (team != null) {
            Player inviter = Bukkit.getPlayer(invite.getInviterUUID());
            if (inviter != null) {
               this.plugin.getMessageManager().send(inviter, "inviteDeniedBroadcast", "player", player.getName());
            }
         }

      }
   }
}
