package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DisbandCommand extends TeamSubCommand {
   public DisbandCommand(SimpleTeams plugin) {
      super(plugin);
   }

   public String getName() {
      return "disband";
   }

   public String getDescription() {
      return "Disband your team";
   }

   public String getUsage() {
      return "disband [team]";
   }

   public String getPermission() {
      return "simpleteams.player.disband";
   }

   public boolean isPlayerOnly() {
      return false;
   }

   public void execute(CommandSender sender, String[] args) {
      boolean admin = args.length > 1 && sender.hasPermission("simpleteams.admin.disband");
      Team team;
      if (admin) {
         Optional<Team> opt = this.plugin.getTeamManager().getTeamByName(args[1]);
         if (opt.isEmpty()) {
            this.plugin.getMessageManager().send(sender, "teamNotFound", "team", args[1]);
            return;
         }

         team = (Team)opt.get();
      } else {
         if (!(sender instanceof Player)) {
            this.plugin.getMessageManager().send(sender, "playerOnly");
            return;
         }

         Player p = (Player)sender;
         team = this.plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
         if (team == null) {
            this.plugin.getMessageManager().send(sender, "notInTeam");
            return;
         }

         if (!team.isLeader(p.getUniqueId())) {
            this.plugin.getMessageManager().send(sender, "notEnoughPermissionInTeam");
            return;
         }
      }

      String teamName = team.getName();
      String disbandKey = admin ? "teamDisbandedAdmin" : "teamDisbanded";
      team.getMembers().keySet().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).filter((px) -> {
         boolean var10000;
         if (sender instanceof Player sp) {
            if (px.getUniqueId().equals(sp.getUniqueId())) {
               var10000 = false;
               return var10000;
            }
         }

         var10000 = true;
         return var10000;
      }).forEach((px) -> this.plugin.getMessageManager().send(px, "teamDisbanded", "team", teamName));
      this.plugin.getTeamManager().disbandTeam(team.getId());
      this.plugin.getMessageManager().send(sender, disbandKey, "team", teamName);
   }
}
