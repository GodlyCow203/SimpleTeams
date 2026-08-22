package net.godlycow.org;

import net.godlycow.org.commands.TeamCommand;
import net.godlycow.org.faststats.FastStatsManager;
import net.godlycow.org.listeners.ChatListener;
import net.godlycow.org.listeners.KillListener;
import net.godlycow.org.listeners.PlayerListener;
import net.godlycow.org.manager.ConfigManager;
import net.godlycow.org.manager.MessageManager;
import net.godlycow.org.manager.TeamManager;
import net.godlycow.org.placeholder.SimpleTeamsExpansion;
import net.godlycow.org.scheduler.FoliaScheduler;
import net.godlycow.org.storage.YamlStorage;
import net.godlycow.org.util.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleTeams extends JavaPlugin {

    private static final int BSTATS_ID = 30889; // useless to make it static but im too lazy to change it

    private static SimpleTeams instance;
    private TeamManager teamManager;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private FoliaScheduler scheduler;
    private FastStatsManager fastStatsManager;

    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        scheduler = new FoliaScheduler(this);
        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        teamManager = new TeamManager(this, new YamlStorage(this));

        TeamCommand teamCommand = new TeamCommand(this);

        getCommand("team").setExecutor(teamCommand);
        getCommand("team").setTabCompleter(teamCommand);

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new KillListener(this), this);

        if (configManager.isBStatsEnabled()) {
            setupBStats();
        }

        fastStatsManager = new FastStatsManager();
        fastStatsManager.init(this);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new SimpleTeamsExpansion(this).register();
            getLogger().info("PlaceholderAPI hooked successfully.");
        }

        if (configManager.isUpdateCheckerEnabled()) {
            new UpdateChecker(this).checkAsync();
        }

        getLogger().info("SimpleTeams v" + getDescription().getVersion() + " has been enabled!");

        if (scheduler.isFolia()) {
            getLogger().info("Folia detected - using region-aware scheduling.");
        }
    }

    public void onDisable() {
        if (teamManager != null) {
            teamManager.saveAllTeams();
        }
        getLogger().info("SimpleTeams has been disabled!");
    }

    private void setupBStats() {
        Metrics metrics = new Metrics(this, BSTATS_ID);
        metrics.addCustomChart(new SingleLineChart("total_teams", () -> teamManager.getAllTeams().size()));
    }

    public void reload() {
        reloadConfig();
        configManager.reload();
        messageManager.reload();
        getLogger().info("Configuration and messages reloaded.");
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }


    public FoliaScheduler getScheduler() {
        return scheduler;
    }

}