package net.godlycow.org.team;

import net.godlycow.org.manager.ConfigManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;

public class Team {

    private final UUID id;
    private String name;
    private String prefix;
    private String description;
    private String color;
    private String motd;
    private final Map<UUID, TeamRank> members;
    private final Set<UUID> bannedPlayers;
    private boolean open;
    private long createdAt;
    private long kills;
    private long deaths;

    public Team(String name, UUID leader, ConfigManager configManager) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.prefix = configManager.getDefaultPrefixFormat().replace("{name}", name);
        this.description = "";
        this.color = "<white>";
        this.motd = "";
        this.members = new HashMap<>();
        this.bannedPlayers = new HashSet<>();
        this.open = false;
        this.createdAt = System.currentTimeMillis();
        this.kills = 0L;
        this.deaths = 0L;
        this.members.put(leader, TeamRank.LEADER);
    }

    public Team(UUID id, ConfigurationSection section) {
        this.id = id;
        this.name = section.getString("name", "Unknown");
        this.prefix = section.getString("prefix", "<dark_gray>[<white>" + this.name + "</white>]</dark_gray>");
        this.description = section.getString("description", "");
        this.color = section.getString("color", "<white>");
        this.motd = section.getString("motd", "");
        this.open = section.getBoolean("open", false);
        this.createdAt = section.getLong("createdAt", System.currentTimeMillis());
        this.kills = section.getLong("kills", 0L);
        this.deaths = section.getLong("deaths", 0L);
        this.members = new HashMap<>();
        this.bannedPlayers = new HashSet<>();

        ConfigurationSection membersSection = section.getConfigurationSection("members");
        if (membersSection != null) {
            for (String uuidStr : membersSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    TeamRank rank = TeamRank.valueOf(membersSection.getString(uuidStr, "MEMBER"));
                    this.members.put(uuid, rank);
                } catch (Exception ignored) {
                }
            }
        }

        for (String uuidStr : section.getStringList("banned")) {
            try {
                this.bannedPlayers.add(UUID.fromString(uuidStr));
            } catch (Exception ignored) {
            }
        }
    }

    public void save(ConfigurationSection section) {
        section.set("name", this.name);
        section.set("prefix", this.prefix);
        section.set("description", this.description);
        section.set("color", this.color);
        section.set("motd", this.motd);
        section.set("open", this.open);
        section.set("createdAt", this.createdAt);
        section.set("kills", this.kills);
        section.set("deaths", this.deaths);

        ConfigurationSection membersSection = section.createSection("members");
        for (Map.Entry<UUID, TeamRank> entry : this.members.entrySet()) {
            membersSection.set(entry.getKey().toString(), entry.getValue().name());
        }

        section.set("banned", this.bannedPlayers.stream().map(UUID::toString).toList());
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public Map<UUID, TeamRank> getMembers() {
        return new HashMap<>(this.members);
    }

    public boolean hasMember(UUID uuid) {
        return this.members.containsKey(uuid);
    }

    public TeamRank getMemberRank(UUID uuid) {
        return this.members.get(uuid);
    }

    public void setMemberRank(UUID uuid, TeamRank rank) {
        this.members.put(uuid, rank);
    }

    public void removeMember(UUID uuid) {
        this.members.remove(uuid);
    }

    public int getMemberCount() {
        return this.members.size();
    }

    public boolean isBanned(UUID uuid) {
        return this.bannedPlayers.contains(uuid);
    }

    public void banPlayer(UUID uuid) {
        this.bannedPlayers.add(uuid);
        this.members.remove(uuid);
    }

    public void unbanPlayer(UUID uuid) {
        this.bannedPlayers.remove(uuid);
    }

    public Set<UUID> getBannedPlayers() {
        return new HashSet<>(this.bannedPlayers);
    }

    public boolean isLeader(UUID uuid) {
        return this.members.get(uuid) == TeamRank.LEADER;
    }

    public UUID getLeader() {
        return this.members.entrySet().stream()
                .filter(e -> e.getValue() == TeamRank.LEADER)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public long getKills() {
        return kills;
    }

    public void incrementKills() {
        this.kills++;
    }

    public long getDeaths() {
        return deaths;
    }

    public void incrementDeaths() {
        this.deaths++;
    }
}
