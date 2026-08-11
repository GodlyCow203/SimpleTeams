package net.godlycow.org.scheduler;

import net.godlycow.org.SimpleTeams;
import org.bukkit.entity.Entity;

public final class FoliaScheduler {

    private static final boolean FOLIA;

    static {
        boolean folia;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer"); //check if folia
            folia = true;
        } catch (ClassNotFoundException e) {
            folia = false;
        }
        FOLIA = folia;
    }

    private final SimpleTeams plugin;

    public FoliaScheduler(SimpleTeams plugin) {
        this.plugin = plugin;
    }

    public boolean isFolia() {
        return FOLIA;
    }

    public void runAsync(Runnable task) {
        if (FOLIA) {
            plugin.getServer().getAsyncScheduler().runNow(plugin, t -> task.run());
        } else {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    public void runAsyncLater(Runnable task, long delayTicks) {
        if (FOLIA) {
            long millis = delayTicks * 50L;
            plugin.getServer().getAsyncScheduler().runDelayed(plugin, t -> task.run(), millis, java.util.concurrent.TimeUnit.MILLISECONDS);
        } else {
            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, task, delayTicks);
        }
    }

    public void runSync(Runnable task) {
        if (FOLIA) {
            plugin.getServer().getGlobalRegionScheduler().run(plugin, t -> task.run());
        } else {
            plugin.getServer().getScheduler().runTask(plugin, task);
        }
    }

    public void runForEntity(Entity entity, Runnable task) {
        if (FOLIA) {
            entity.getScheduler().run(plugin, t -> task.run(), null);
        } else {
            plugin.getServer().getScheduler().runTask(plugin, task);
        }
    }
}
