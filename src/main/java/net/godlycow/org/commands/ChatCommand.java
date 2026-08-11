package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommand extends TeamSubCommand {
   public ChatCommand(SimpleTeams plugin) {
      super(plugin);
   }

   public String getName() {
      return "chat";
   }

   public String getDescription() {
      return "Toggle team-only chat mode";
   }

   public String getUsage() {
      return "chat";
   }

   public String getPermission() {
      return "simpleteams.player.chat";
   }

   public boolean isPlayerOnly() {
      return true;
   }

   public void execute(CommandSender sender, String[] args) {
      if (!this.plugin.getConfigManager().isTeamChatEnabled()) {
         this.plugin.getMessageManager().send(sender, "teamChatDisabledConfig");
      } else {
         Player player = (Player)sender;
         if (!this.plugin.getTeamManager().hasTeam(player.getUniqueId())) {
            this.plugin.getMessageManager().send(sender, "notInTeam");
         } else {
            boolean nowEnabled = this.plugin.getTeamManager().toggleTeamChat(player.getUniqueId());
            this.plugin.getMessageManager().send(sender, nowEnabled ? "teamChatEnabled" : "teamChatDisabled");
         }
      }
   }
}
