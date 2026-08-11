package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ListCommand extends TeamSubCommand {
   public ListCommand(SimpleTeams plugin) {
      super(plugin);
   }

   public String getName() {
      return "list";
   }

   public String getDescription() {
      return "List all teams";
   }

   public String getUsage() {
      return "list";
   }

   public String getPermission() {
      return "simpleteams.player.list";
   }

   public boolean isPlayerOnly() {
      return false;
   }

   public void execute(CommandSender sender, String[] args) {
      Set<Team> all = this.plugin.getTeamManager().getAllTeams();
      this.plugin.getMessageManager().sendRaw(sender, "listHeader", "count", String.valueOf(all.size()));
      if (all.isEmpty()) {
         this.plugin.getMessageManager().sendRaw(sender, "listEmpty");
      } else {
         all.stream().sorted(Comparator.comparing(Team::getName)).forEach((t) -> {
            String leader = (String)Optional.ofNullable(Bukkit.getOfflinePlayer(t.getLeader()).getName()).orElse("Unknown");
            this.plugin.getMessageManager().sendRaw(sender, "listEntry", "team", t.getName(), "leader", leader, "members", String.valueOf(t.getMemberCount()));
         });
         this.plugin.getMessageManager().sendRaw(sender, "listFooter");
      }
   }
}
