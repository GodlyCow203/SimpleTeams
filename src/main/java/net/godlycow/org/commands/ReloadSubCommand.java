package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand extends TeamSubCommand {
   public ReloadSubCommand(SimpleTeams plugin) {
      super(plugin);
   }

   public String getName() {
      return "reload";
   }

   public String getDescription() {
      return "Reload config and messages";
   }

   public String getUsage() {
      return "reload";
   }

   public String getPermission() {
      return "simpleteams.admin.reload";
   }

   public boolean isPlayerOnly() {
      return false;
   }

    public void execute(CommandSender sender, String[] args) {
      try {
         this.plugin.reload();
      } catch (Exception e) {
         this.plugin.getLogger().severe("Reload failed: " + e.getMessage());
         this.plugin.getMessageManager().send(sender, "reloadFailed");
         return;
      }
      this.plugin.getMessageManager().send(sender, "reloadSuccess");
   }
}
