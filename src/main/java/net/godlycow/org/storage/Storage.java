package net.godlycow.org.storage;

import net.godlycow.org.team.Team;
import java.util.Map;
import java.util.UUID;

public interface Storage {
   void saveTeam(Team var1);

   void deleteTeam(UUID var1);

   Map<UUID, Team> loadAllTeams();

   void saveAllTeams(Map<UUID, Team> var1);
}
