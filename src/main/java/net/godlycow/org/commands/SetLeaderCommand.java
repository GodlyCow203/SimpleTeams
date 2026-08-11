package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import net.godlycow.org.team.TeamRank;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLeaderCommand extends TeamSubCommand {
   public SetLeaderCommand(SimpleTeams plugin) {
      super(plugin);
   }

   public String getName() {
      return "setleader";
   }

   public String getDescription() {
      return "Admin: transfer team leadership";
   }

   public String getUsage() {
      return "setleader <team> <player>";
   }

   public String getPermission() {
      return "simpleteams.admin.setleader";
   }

   public boolean isPlayerOnly() {
      return false;
   }

   public void execute(CommandSender sender, String[] args) {
      if (args.length < 3) {
         this.plugin.getMessageManager().send(sender, "usageSetleader");
      } else {
         Optional<Team> opt = this.plugin.getTeamManager().getTeamByName(args[1]);
         if (opt.isEmpty()) {
            this.plugin.getMessageManager().send(sender, "teamNotFound", "team", args[1]);
         } else {
            Team team = (Team)opt.get();
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
               this.plugin.getMessageManager().send(sender, "playerNotFound", "player", args[2]);
            } else if (!team.hasMember(target.getUniqueId())) {
               this.plugin.getMessageManager().send(sender, "targetNotInTeam", "player", target.getName());
            } else {
               UUID oldLeader = team.getLeader();
               if (oldLeader != null) {
                  team.setMemberRank(oldLeader, TeamRank.MEMBER);
               }

               team.setMemberRank(target.getUniqueId(), TeamRank.LEADER);
               this.plugin.getTeamManager().saveTeam(team);
               this.plugin.getMessageManager().send(sender, "setleaderSuccess", "team", team.getName(), "player", target.getName());
               this.broadcast(team, (UUID)null, "setleaderBroadcast", new String[]{"player", target.getName()});
            }
         }
      }
   }
}
