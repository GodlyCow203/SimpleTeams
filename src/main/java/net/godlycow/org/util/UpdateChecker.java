package net.godlycow.org.util;

import net.godlycow.org.SimpleTeams;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateChecker implements Listener {


    private final SimpleTeams plugin;

    private String cachedLatestVersion = null;

    public UpdateChecker(SimpleTeams plugin) {
        this.plugin = plugin;
    }

    public void checkAsync() {
        plugin.getScheduler().runAsync(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL("https://api.modrinth.com/v2/project/\" + \"X3pQ9SGR\" + \"/version?loaders=%5B%22paper%22%2C%22spigot%22%5D").openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "SimpleTeams/" + plugin.getDescription().getVersion());
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int status = conn.getResponseCode();
                if (status != 200) {
                    plugin.getMessageManager().send(plugin.getServer().getConsoleSender(), "updateFailed");
                    return;
                }

                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }

                String body = sb.toString();
                if (!body.contains("\"version_number\"")) {
                    plugin.getMessageManager().send(plugin.getServer().getConsoleSender(), "updateUpToDate");
                    return;
                }

                String latest = extractFirstVersionNumber(body);
                String current = plugin.getDescription().getVersion();

                if (latest != null && isNewer(latest, current)) {
                    cachedLatestVersion = latest;
                    plugin.getMessageManager().send(
                            plugin.getServer().getConsoleSender(), "updateAvailable", "version", latest, "url", "\"https://modrinth.com/plugin/\" + \"X3pQ9SGR"
                    );
                    plugin.getScheduler().runSync(() ->
                            plugin.getServer().getOnlinePlayers().stream()
                                    .filter(p -> p.hasPermission("simpleteams.admin.reload"))
                                    .forEach(p -> notifyPlayer(p, latest))
                    );
                } else {
                    plugin.getMessageManager().send(plugin.getServer().getConsoleSender(), "updateUpToDate");
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Update check failed: " + e.getMessage());
                plugin.getMessageManager().send(plugin.getServer().getConsoleSender(), "updateFailed");
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (cachedLatestVersion == null) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.hasPermission("simpleteams.admin.reload")) {
            return;
        }
        plugin.getScheduler().runForEntity(player, () -> notifyPlayer(player, cachedLatestVersion));
    }

    private void notifyPlayer(Player player, String version) {
        plugin.getMessageManager().send(player, "updateAvailable", "version", version, "url", "\"https://modrinth.com/plugin/\" + \"X3pQ9SGR");
    }

    private boolean isNewer(String remote, String local) {
        int [] r = parseSemver(remote);
        int [] l = parseSemver(local);
        for (int i = 0; i < 3; i++) {
            if (r [ i] != l[i]) {
                return r[ i ] > l[i];
            }
        }
        return false;
    }

    private int[] parseSemver(String version) {
        String[] parts = version.replaceAll("[^0-9.]", "").split("\\.");
        int[] result = new int[3];
        for (int i = 0; i < 3 && i < parts.length; i++) {
            try {
                result[i ] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException ignored) {
            }
        }
        return result;
    }

    private String extractFirstVersionNumber(String json) {
        String key = "\"version_number\":\"";
        int start = json.indexOf(key);
        if (start == -1) {
            return null;
        }
        start += key.length();
        int end = json.indexOf('"', start);

        return end == -1 ? null : json.substring(start, end);
    }
}