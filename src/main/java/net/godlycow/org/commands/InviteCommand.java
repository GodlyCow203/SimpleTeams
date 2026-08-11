package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import net.godlycow.org.team.TeamRank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InviteCommand extends TeamSubCommand {
   public InviteCommand(SimpleTeams plugin) {
      super(plugin);
   }

   public String getName() {
      return "invite";
   }

   public String getDescription() {
      return "Invite a player to your team";
   }

   public String getUsage() {
      return "invite <player>";
   }

   public String getPermission() {
      return "simpleteams.player.invite";
   }

   public boolean isPlayerOnly() {
      return true;
   }

   public void execute(CommandSender sender, String[] args) {
      if (!this.plugin.getConfigManager().isInvitesEnabled()) {
         this.plugin.getMessageManager().send(sender, "inviteDisabled");
      } else if (args.length < 2) {
         this.plugin.getMessageManager().send(sender, "usageInvite");
      } else {
         Player inviter = (Player)sender;
         Team team = this.plugin.getTeamManager().getPlayerTeam(inviter.getUniqueId());
         if (team == null) {
            this.plugin.getMessageManager().send(sender, "notInTeam");
         } else if (!team.getMemberRank(inviter.getUniqueId()).isAtLeast(TeamRank.MODERATOR)) {
            this.plugin.getMessageManager().send(sender, "notEnoughPermissionInTeam");
         } else {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
               this.plugin.getMessageManager().send(sender, "playerNotFound", "player", args[1]);
            } else if (this.plugin.getTeamManager().hasTeam(target.getUniqueId())) {
               this.plugin.getMessageManager().send(sender, "invitePlayerHasTeam", "player", target.getName());
            } else if (this.plugin.getTeamManager().hasOutgoingInvite(team.getId(), target.getUniqueId())) {
               this.plugin.getMessageManager().send(sender, "inviteAlreadySent", "player", target.getName());
            } else if (team.getMemberCount() >= this.plugin.getConfigManager().getMaxMembers()) {
               this.plugin.getMessageManager().send(sender, "teamFull", "count", String.valueOf(team.getMemberCount()), "max", String.valueOf(this.plugin.getConfigManager().getMaxMembers()));
            } else {
               this.plugin.getTeamManager().sendInvite(target.getUniqueId(), team.getId(), inviter.getUniqueId());
               this.plugin.getMessageManager().send(sender, "inviteSent", "player", target.getName(), "seconds", String.valueOf(this.plugin.getConfigManager().getInviteExpireSeconds()));
               this.plugin.getMessageManager().send(target, "inviteReceived", "player", inviter.getName(), "team", team.getName());
               this.plugin.getMessageManager().send(target, "inviteReceivedHint");
            }
         }
      }
   }
}
