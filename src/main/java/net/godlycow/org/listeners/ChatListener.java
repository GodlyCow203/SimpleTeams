package net.godlycow.org.listeners;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    private final SimpleTeams plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ChatListener(SimpleTeams plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
        String plainMessage = PlainTextComponentSerializer.plainText().serialize(event.message());

        if (plugin.getConfigManager().isTeamChatEnabled()
                && plugin.getTeamManager().isTeamChatEnabled(player.getUniqueId())) {
            if (team != null) {
                event.setCancelled(true);
                String format = plugin.getConfigManager().getTeamChatFormat()
                        .replace("{team_prefix}", team.getPrefix())
                        .replace("{rank}", team.getMemberRank(player.getUniqueId()).getDisplayName()).replace("{player}", player.getName()).replace("{message}", plainMessage); //parse plh
                Component formatted = mm.deserialize(format);
                team.getMembers().keySet().stream()
                        .map(plugin.getServer()::getPlayer)
                        .filter(Objects::nonNull)
                        .forEach(p -> p.sendMessage(formatted));
                return;
            }
            plugin.getTeamManager().disableTeamChat(player.getUniqueId());
        }

        if (plugin.getConfigManager().isChatFormatEnabled() && team != null) {
            String format = plugin.getConfigManager().getChatFormat()
                    .replace("{team_prefix}", team.getPrefix())
                    .replace("{player}", player.getName())
                    .replace("{message}", plainMessage);
            event.renderer((source, sourceDisplayName, message, viewer) -> mm.deserialize(format));
        }
    }
}
