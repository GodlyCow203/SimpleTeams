package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import net.godlycow.org.team.TeamRank;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand extends TeamSubCommand {
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

   public InfoCommand(SimpleTeams plugin) {
      super(plugin);
   }

   public String getName() {
      return "info";
   }

   public String getDescription() {
      return "View team information";
   }

   public String getUsage() {
      return "info [team]";
   }

   public String getPermission() {
      return "simpleteams.player.info";
   }

   public boolean isPlayerOnly() {
      return false;
   }

   public void execute(CommandSender sender, String[] args) {
      Team team;
      if (args.length > 1) {
         Optional<Team> opt = this.plugin.getTeamManager().getTeamByName(args[1]);
         if (opt.isEmpty()) {
            this.plugin.getMessageManager().send(sender, "teamNotFound", "team", args[1]);
            return;
         }

         team = (Team)opt.get();
      } else {
         if (!(sender instanceof Player)) {
            this.plugin.getMessageManager().send(sender, "usageJoin");
            return;
         }

         Player p = (Player)sender;
         team = this.plugin.getTeamManager().getPlayerTeam(p.getUniqueId());
         if (team == null) {
            this.plugin.getMessageManager().send(sender, "notInTeam");
            return;
         }
      }

      this.plugin.getMessageManager().sendRaw(sender, "infoHeader", "team", team.getName());
      this.plugin.getMessageManager().sendRaw(sender, "infoPrefix", "prefix", team.getPrefix());
      String statusKey = team.isOpen() ? "infoStatusOpen" : "infoStatusClosed";
      String statusStr = this.plugin.getMessageManager().getRaw(statusKey);
      this.plugin.getMessageManager().sendRaw(sender, "infoStatus", "status", statusStr);
      this.plugin.getMessageManager().sendRaw(sender, "infoMembers", "count", String.valueOf(team.getMemberCount()), "max", String.valueOf(this.plugin.getConfigManager().getMaxMembers()));
      this.plugin.getMessageManager().sendRaw(sender, "infoCreated", "date", DATE_FORMAT.format(new Date(team.getCreatedAt())));
      team.getMembers().entrySet().stream().sorted((a, b) -> Integer.compare(((TeamRank)b.getValue()).getPriority(), ((TeamRank)a.getValue()).getPriority())).forEach((entry) -> {
         String playerName = (String)Optional.ofNullable(Bukkit.getOfflinePlayer((UUID)entry.getKey()).getName()).orElse("Unknown");
         this.plugin.getMessageManager().sendRaw(sender, "infoMemberEntry", "player", playerName, "rank", ((TeamRank)entry.getValue()).getDisplayName());
      });
      this.plugin.getMessageManager().sendRaw(sender, "infoFooter");
   }

   public List<String> tabComplete(CommandSender sender, String[] args) {
      return args.length != 2 ? List.of() : (List)this.plugin.getTeamManager().getAllTeams().stream().map(Team::getName).filter((n) -> n.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
   }
}
