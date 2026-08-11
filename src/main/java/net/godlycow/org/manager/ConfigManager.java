package net.godlycow.org.manager;

import net.godlycow.org.SimpleTeams;
import java.util.List;

public class ConfigManager {

    private final SimpleTeams plugin;

    public ConfigManager(SimpleTeams plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
    }

    public int getMaxTeams() {
        return plugin.getConfig().getInt("limits.max-teams", 100);
    }

    public int getMaxMembers() {
        return plugin.getConfig().getInt("limits.max-members-per-team", 20);
    }

    public int getMaxNameLength() {
        return plugin.getConfig().getInt("limits.max-name-length", 24);
    }

    public int getMinNameLength() {
        return plugin.getConfig().getInt("limits.min-name-length", 3);
    }

    public int getMaxPrefixLength() {
        return plugin.getConfig().getInt("limits.max-prefix-length", 16);
    }

    public int getMaxDescriptionLength() {
        return plugin.getConfig().getInt("limits.max-description-length", 128);
    }

    public int getMaxMotdLength() {
        return plugin.getConfig().getInt("limits.max-motd-length", 128);
    }

    public boolean isColoredPrefixAllowed() {
        return plugin.getConfig().getBoolean("limits.allow-colored-prefix", true);
    }

    public String getDefaultPrefixFormat() {
        return plugin.getConfig().getString("limits.default-prefix-format", "<dark_gray>[<white>{name}</white>]</dark_gray>");
    }

    public String getAllowedNameChars() {
        return plugin.getConfig().getString("validation.allowed-name-chars", "a-zA-Z0-9_-");
    }

    public List<String> getBlacklistedNames() {
        return plugin.getConfig().getStringList("validation.blacklisted-names");
    }

    public boolean isChatFormatEnabled() {
        return plugin.getConfig().getBoolean("chat.enabled", true);
    }

    public String getChatFormat() {
        return plugin.getConfig().getString("chat.format", "{team_prefix}<white>{player}</white><dark_gray>:</dark_gray> <gray>{message}</gray>");
    }

    public boolean isInvitesEnabled() {
        return plugin.getConfig().getBoolean("invites.enabled", true);
    }

    public int getInviteExpireSeconds() {
        return plugin.getConfig().getInt("invites.expire-seconds", 60);
    }

    public boolean isTeamChatEnabled() {
        return plugin.getConfig().getBoolean("team-chat.enabled", true);
    }



    public String getTeamChatFormat() {
        return plugin.getConfig().getString("team-chat.format", "<dark_gray>[<aqua>Team Chat</aqua>]</dark_gray> <gray>{rank} {player}:</gray> <white>{message}</white>");
    }

    public int getTopEntries() {
        return plugin.getConfig().getInt("top.entries", 10);
    }

    public boolean isUpdateCheckerEnabled() {
        return plugin.getConfig().getBoolean("update-checker.enabled", true);
    }

    public boolean isBStatsEnabled() {
        return plugin.getConfig().getBoolean("bstats.enabled", true);
    }

    public boolean isMotdOnJoinEnabled() {
        return plugin.getConfig().getBoolean("motd.show-on-join", true);
    }
}
