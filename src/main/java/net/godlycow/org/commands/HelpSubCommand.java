package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import java.util.Map;
import org.bukkit.command.CommandSender;

public class HelpSubCommand extends TeamSubCommand {
   private final Map<String, SubCommand> subCommands;

   public HelpSubCommand(SimpleTeams plugin, Map<String, SubCommand> subCommands) {
      super(plugin);
      this.subCommands = subCommands;
   }

   public String getName() {
      return "help";
   }

   public String getDescription() {
      return "Show this help menu";
   }

   public String getUsage() {
      return "help";
   }

   public String getPermission() {
      return "simpleteams.player.help";
   }

   public boolean isPlayerOnly() {
      return false;
   }

   public void execute(CommandSender sender, String[] args) {
      this.plugin.getMessageManager().sendRaw(sender, "helpHeader");

      for(SubCommand sub : this.subCommands.values()) {
         if (sender.hasPermission(sub.getPermission())) {
            this.plugin.getMessageManager().sendRaw(sender, "helpEntry", "usage", sub.getUsage(), "description", sub.getDescription());
         }
      }

      this.plugin.getMessageManager().sendRaw(sender, "helpFooter");
   }
}
