package net.godlycow.org.faststats;

import dev.faststats.ErrorTracker;
import dev.faststats.bukkit.BukkitContext;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.AccessDeniedException;
import java.lang.reflect.InvocationTargetException;

public class FastStatsManager {

    public static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware()
            .ignoreError(InvocationTargetException.class, "Expected .* but got .*")
            .ignoreError(AccessDeniedException.class);

    private BukkitContext context;

    public void init(JavaPlugin plugin) {
        this.context = new BukkitContext.Factory(plugin, "4a317fe4fc6f87384dafaf566515757e")
                .errorTrackerService(ERROR_TRACKER)
                .create();

        this.context.ready();
        plugin.getLogger().info("FastStats enabled (metrics + error tracking)");
    }

    public void shutdown() {
        if (context != null) {
            context.shutdown();
        }
    }

}
