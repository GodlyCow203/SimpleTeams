package net.godlycow.org.faststats;

import dev.faststats.bukkit.BukkitMetrics;
import dev.faststats.core.Metrics;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

// For anyone forking this: The faststats version this plugin
// is using is OUTDATED ! If you bump faststats, this will break.
// this can and will help: https://docs.faststats.dev/java/migration


public class FastStatsManager {
    private Metrics metrics;

    public void init(JavaPlugin plugin) {
        this.metrics = ((BukkitMetrics.Factory) BukkitMetrics.factory().token("4a317fe4fc6f87384dafaf566515757e")).create((Plugin)plugin); // BukkitMetrics.factory is outdated
        plugin.getLogger().info("FastStats enabled");
    }


}