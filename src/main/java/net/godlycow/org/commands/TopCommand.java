package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import java.util.List;
import org.bukkit.command.CommandSender;

public class TopCommand extends TeamSubCommand {
   public TopCommand(SimpleTeams plugin) {
      super(plugin);
   }

   public String getName() {
      return "top";
   }

   public String getDescription() {
      return "Top teams by member count";
   }

   public String getUsage() {
      return "top";
   }

   public String getPermission() {
      return "simpleteams.player.top";
   }

   public boolean isPlayerOnly() {
      return false;
   }

   public void execute(CommandSender sender, String[] args) {
      int limit = this.plugin.getConfigManager().getTopEntries();
      List<Team> sorted = this.plugin.getTeamManager().getAllTeams().stream().sorted((a, b) -> Integer.compare(b.getMemberCount(), a.getMemberCount())).limit((long)limit).toList();
      this.plugin.getMessageManager().sendRaw(sender, "topHeader");

      for(int i = 0; i < sorted.size(); ++i) {
         Team t = (Team)sorted.get(i);
         this.plugin.getMessageManager().sendRaw(sender, "topEntry", "rank", String.valueOf(i + 1), "team", t.getName(), "members", String.valueOf(t.getMemberCount()));
      }

      this.plugin.getMessageManager().sendRaw(sender, "topFooter");
   }
}
