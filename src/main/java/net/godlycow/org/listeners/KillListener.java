package net.godlycow.org.listeners;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.manager.TeamManager;
import net.godlycow.org.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillListener implements Listener {

    private final SimpleTeams plugin;

    public KillListener(SimpleTeams plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) {
            return;
        }

        TeamManager manager = plugin.getTeamManager();

        Team killerTeam = manager.getPlayerTeam(killer.getUniqueId());
        if (killerTeam != null) {
            killerTeam.incrementKills();
            manager.saveTeam(killerTeam);
        }

        Team victimTeam = manager.getPlayerTeam(victim.getUniqueId());
        if (victimTeam != null) {
            victimTeam.incrementDeaths();
            manager.saveTeam(victimTeam);
        }
    }
}