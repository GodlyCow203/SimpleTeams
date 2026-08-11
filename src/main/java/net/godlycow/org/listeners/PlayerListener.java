package net.godlycow.org.listeners;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final SimpleTeams plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public PlayerListener(SimpleTeams plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.getConfigManager().isMotdOnJoinEnabled()) {
            return;
        }
        Team team = plugin.getTeamManager().getPlayerTeam(event.getPlayer().getUniqueId());
        if (team == null) {
            return;
        }
        String motd = team.getMotd();
        if (motd == null || motd.isBlank()) {
            return;
        }
        plugin.getScheduler().runForEntity(event.getPlayer(), () ->
                event.getPlayer().sendMessage(miniMessage.deserialize(
                        plugin.getMessageManager().getRaw("motdHeader", "team", team.getName())
                        + "\n" + motd // send team motd
                ))
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getTeamManager().disableTeamChat(event.getPlayer().getUniqueId());
    }
}
