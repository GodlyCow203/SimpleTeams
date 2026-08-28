package net.godlycow.org.placeholder;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import net.godlycow.org.team.TeamRank;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.OfflinePlayer;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleTeamsExpansion extends PlaceholderExpansion {

    private static final String NO_TEAM = "";

    private final SimpleTeams plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder()
            .character(LegacyComponentSerializer.SECTION_CHAR)
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    public SimpleTeamsExpansion(SimpleTeams plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "simpleteams";
    }

    @Override
    public String getAuthor() {
        return "_GodlyCow";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player,String params) {
        if (player == null) {
            return NO_TEAM;
        }

        Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());

        switch (params.toLowerCase()) {
            case "has_team":
                return team != null ? "true" : "false";

            case "team":
                return team != null ? team.getName() : NO_TEAM;

            case "team_plain":
                return team != null ? stripToPlain(team.getPrefix()) : NO_TEAM;

            case "team_formatted":
                return team != null ? toLegacy(team.getPrefix()) : NO_TEAM;

            case "prefix":
                return team != null ? toLegacy(team.getPrefix()) : NO_TEAM;

            case "prefix_plain":
                return team != null ? stripToPlain(team.getPrefix()) : NO_TEAM;

            case "prefix_formatted":
                return team != null ? toLegacy(team.getPrefix()) : NO_TEAM;

            case "rank":
                if (team == null) {
                    return NO_TEAM;
                }

                TeamRank rank = team.getMemberRank(player.getUniqueId());
                return rank != null ? rank.getDisplayName() : NO_TEAM;
            case "rank_name":
                if (team == null) {
                    return NO_TEAM;
                }

                TeamRank rawRank = team.getMemberRank(player.getUniqueId());
                return rawRank != null ? rawRank.name() : NO_TEAM;
            case "leader":
                if (team == null) {
                    return NO_TEAM;
                }

                java.util.UUID leaderId = team.getLeader();
                if (leaderId == null) {
                    return NO_TEAM;
                }
                String leaderName = plugin.getServer().getOfflinePlayer(leaderId).getName();

                return leaderName != null ? leaderName : NO_TEAM;

            case "size":
                return team != null ? String.valueOf(team.getMemberCount()) : "0";
            case "kills":
                return team != null ? String.valueOf(team.getKills()) : "0";
            case "deaths":
                return team != null ? String.valueOf(team.getDeaths()) : "0";
            case "kd":
            case "kdr":
                return team != null ? formatKd(team) : "0.00";
            case "max_size":

                return String.valueOf(plugin.getConfigManager().getMaxMembers());
            case "description":

                return team != null ? team.getDescription() : NO_TEAM;
            case "color":

                return team != null ? team.getColor() : NO_TEAM;

            case "status":
                if (team == null) {
                    return NO_TEAM;
                }
                return team.isOpen() ? "Open" : "Closed";
            case "motd":
                return team != null ? team.getMotd() : NO_TEAM;
            case "created":
                if (team == null) {
                    return NO_TEAM;
                }
                return new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(team.getCreatedAt()));
            default:
                return resolveLeaderboard(params);
        }
    }

    private String resolveLeaderboard(String params) {
        String lower = params.toLowerCase();

        Matcher topMatch = Pattern.compile("^(kills|deaths|members|size|kd|kdr|kdratio)_top_(\\d+)$").matcher(lower);
        if (topMatch.matches()) {
            return teamNameAtStatRank(normalizeStat(topMatch.group(1)), parseRank(topMatch.group(2)));
        }

        topMatch = Pattern.compile("^top_(kills|deaths|members|size|kd|kdr|kdratio)_(\\d+)$").matcher(lower);
        if (topMatch.matches()) {
            return teamNameAtStatRank(normalizeStat(topMatch.group(1)), parseRank(topMatch.group(2)));
        }

        Matcher rankMatch = Pattern.compile("^(kills|deaths|members|size|kd|kdr|kdratio)_rank_(.+)$").matcher(lower);
        if (rankMatch.matches()) {
            return teamRankForStat(normalizeStat(rankMatch.group(1)), rankMatch.group(2));
        }

        Matcher valueMatch = Pattern.compile("^(kills|deaths|members|size|kd|kdr|kdratio)_(.+)$").matcher(lower);
        if (valueMatch.matches()) {
            return teamStatValue(normalizeStat(valueMatch.group(1)), valueMatch.group(2));
        }

        return null;
    }

    private String normalizeStat(String stat) {
        switch (stat) {
            case "size":
                return "members";
            case "kdr":
            case "kdratio":
                return "kd";
            default:
                return stat;
        }
    }

    private double kdValue(Team team) {
        if (team.getDeaths() == 0L) {
            return team.getKills();
        }
        return team.getKills() / (double) team.getDeaths();
    }

    private String formatKd(Team team) {
        return String.format(java.util.Locale.US, "%.2f", kdValue(team));
    }

    private int parseRank(String rank) {
        try {
            return Integer.parseInt(rank);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private Comparator<Team> statComparator(String stat) {

        switch (stat) {
            case "kills":
                return Comparator.comparingLong(Team::getKills).reversed();
            case "deaths":
                return Comparator.comparingLong(Team::getDeaths).reversed();
            case "kd":
                return Comparator.comparingDouble(this::kdValue).reversed();
            default:
                return Comparator.comparingInt(Team::getMemberCount).reversed();
        }
    }

    private List<Team> sortedTeams(String stat) {
        return plugin.getTeamManager().getAllTeams().stream()
                .sorted(statComparator(stat))
                .toList();
    }

    private String teamNameAtStatRank(String stat, int rank) {
        if (rank < 1) {
            return null;
        }
        List<Team> sorted = sortedTeams(stat);
        if (rank > sorted.size()) {
            return null;
        }
        return sorted.get(rank - 1).getName();
    }

    private String teamRankForStat(String stat, String teamName) {
        Team target = plugin.getTeamManager().getTeamByName(teamName).orElse(null);
        if (target == null) {
            return null;
        }
        List<Team> sorted = sortedTeams(stat);
        int rank = sorted.indexOf(target);
        return rank < 0 ? null : String.valueOf(rank + 1);
    }

    private String teamStatValue(String stat, String teamName) {
        Team target = plugin.getTeamManager().getTeamByName(teamName).orElse(null);
        if (target == null) {
            return null;
        }
        switch (stat) {
            case "kills":
                return String.valueOf(target.getKills());
            case "deaths":
                return String.valueOf(target.getDeaths());
            case "kd":
                return formatKd(target);
            default:
                return String.valueOf(target.getMemberCount());
        }
    }

    private String stripToPlain(String miniMessageStr) {
        try {
            return PlainTextComponentSerializer.plainText().serialize(
                    miniMessage.deserialize(miniMessageStr)
            );
        } catch (Exception e) {
            return miniMessageStr;
        }
    }

    private String toLegacy(String miniMessageStr) {
        try {
            Component component = miniMessage.deserialize(miniMessageStr);
            return legacySerializer.serialize(component);
        } catch (Exception e) {
            return miniMessageStr;
        }
    }
}
