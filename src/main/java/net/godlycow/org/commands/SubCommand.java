package net.godlycow.org.commands;

import java.util.List;
import org.bukkit.command.CommandSender;

public interface SubCommand {
   String getName();

   String getDescription();

   String getUsage();

   String getPermission();

   boolean isPlayerOnly();

   void execute(CommandSender var1, String[] var2);

   default List<String> tabComplete(CommandSender sender, String[] args) {
      return List.of();
   }
}
