package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.manager.TeamManager;
import net.godlycow.org.team.Team;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand extends TeamSubCommand {
   public JoinCommand(SimpleTeams plugin) {
      super(plugin);
   }

   public String getName() {
      return "join";
   }

   public String getDescription() {
      return "Join an open team";
   }

   public String getUsage() {
      return "join <team>";
   }

   public String getPermission() {
      return "simpleteams.player.join";
   }

   public boolean isPlayerOnly() {
      return true;
   }

   public void execute(CommandSender sender, String[] args) {
      if (args.length < 2) {
         this.plugin.getMessageManager().send(sender, "usageJoin");
      } else {
         Player player = (Player)sender;
         Optional<Team> opt = this.plugin.getTeamManager().getTeamByName(args[1]);
         if (opt.isEmpty()) {
            this.plugin.getMessageManager().send(sender, "teamNotFound", "team", args[1]);
         } else {
            Team team = (Team)opt.get();
            TeamManager.JoinResult result = this.plugin.getTeamManager().joinTeam(player, team);
            switch (result) {
               case SUCCESS:
                  this.plugin.getMessageManager().send(sender, "teamJoined", "team", team.getName());
                  this.broadcast(team, player.getUniqueId(), "teamJoinedBroadcast", new String[]{"player", player.getName()});
                  break;
               case ALREADY_IN_TEAM:
                  this.plugin.getMessageManager().send(sender, "alreadyInTeam");
                  break;
               case BANNED:
                  this.plugin.getMessageManager().send(sender, "bannedFromTeam", "team", team.getName());
                  break;
               case CLOSED:
                  this.plugin.getMessageManager().send(sender, "teamIsClosed");
                  break;
               case FULL:
                  this.plugin.getMessageManager().send(sender, "teamFull", "count", String.valueOf(team.getMemberCount()), "max", String.valueOf(this.plugin.getConfigManager().getMaxMembers()));
            }

         }
      }
   }

   public List<String> tabComplete(CommandSender sender, String[] args) {
      return args.length != 2 ? List.of() : (List)this.plugin.getTeamManager().getOpenTeams().stream().map(Team::getName).filter((n) -> n.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
   }
}
