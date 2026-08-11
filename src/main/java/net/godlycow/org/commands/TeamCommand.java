package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class TeamCommand implements TabExecutor {
   private final SimpleTeams plugin;
   private final Map<String, SubCommand> subCommands = new LinkedHashMap();

   public TeamCommand(SimpleTeams plugin) {
      this.plugin = plugin;
      this.register();
   }

   private void register() { // add subcommands
      this.add(new CreateCommand(this.plugin));
      this.add(new JoinCommand(this.plugin));
      this.add(new LeaveCommand(this.plugin));
      this.add(new InfoCommand(this.plugin));
      this.add(new ListCommand(this.plugin));
      this.add(new TopCommand(this.plugin));
      this.add(new OpenCommand(this.plugin));
      this.add(new CloseCommand(this.plugin));
      this.add(new KickCommand(this.plugin));
      this.add(new PromoteCommand(this.plugin));
      this.add(new DemoteCommand(this.plugin));
      this.add(new DisbandCommand(this.plugin));
      this.add(new AdminKickCommand(this.plugin));
      this.add(new BanCommand(this.plugin));
      this.add(new UnbanCommand(this.plugin));
      this.add(new EditCommand(this.plugin));
      this.add(new InviteCommand(this.plugin));
      this.add(new AcceptCommand(this.plugin));
      this.add(new DenyCommand(this.plugin));
      this.add(new ChatCommand(this.plugin));
      this.add(new SetLeaderCommand(this.plugin));
      this.add(new ReloadSubCommand(this.plugin));
      this.add(new HelpSubCommand(this.plugin, this.subCommands));
   }

   private void add(SubCommand cmd) {
      this.subCommands.put(cmd.getName().toLowerCase(), cmd);
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (args.length == 0) {
         this.plugin.getMessageManager().sendRaw(sender, "helpHeader");

         for(SubCommand sub : this.subCommands.values()) {
            if (sender.hasPermission(sub.getPermission())) {
               this.plugin.getMessageManager().sendRaw(sender, "helpEntry", "usage", sub.getUsage(), "description", sub.getDescription());
            }
         }

         this.plugin.getMessageManager().sendRaw(sender, "helpFooter");

         return true;
      } else {
         SubCommand sub = (SubCommand)this.subCommands.get(args[0].toLowerCase());
         if (sub == null) {
            this.plugin.getMessageManager().send(sender, "unknownCommand");
            return true;
         } else if (!sender.hasPermission(sub.getPermission())) {
            this.plugin.getMessageManager().send(sender, "noPermission");
            return true;
         } else if (sub.isPlayerOnly() && !(sender instanceof Player)) {
            this.plugin.getMessageManager().send(sender, "playerOnly");
            return true;
         } else {
            sub.execute(sender, args);
            return true;
         }
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
      if (args.length == 1) {
         return this.subCommands.entrySet().stream().filter((e) -> sender.hasPermission(((SubCommand)e.getValue()).getPermission())).map(Map.Entry::getKey).filter((n) -> n.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
      } else {
         SubCommand sub = (SubCommand)this.subCommands.get(args[0].toLowerCase());
         return sub == null ? List.of() : sub.tabComplete(sender, args);
      }
   }
}
