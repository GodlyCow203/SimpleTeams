package net.godlycow.org.storage;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class YamlStorage implements Storage {
   private final SimpleTeams plugin;
   private final File teamsFile;
   private FileConfiguration teamsConfig;

   public YamlStorage(SimpleTeams plugin) {
      this.plugin = plugin;
      this.teamsFile = new File(plugin.getDataFolder(), "teams.yml");
      this.load();
   }

   private void load() {
      if (!this.teamsFile.exists()) {
         try {
            this.teamsFile.getParentFile().mkdirs();
            this.teamsFile.createNewFile();
         } catch (IOException e) {
            this.plugin.getLogger().severe("Could not create teams.yml!");
            e.printStackTrace();
         }
      }

      this.teamsConfig = YamlConfiguration.loadConfiguration(this.teamsFile);
   }

   public void saveTeam(Team team) {
      String path = "teams." + String.valueOf(team.getId());
      this.teamsConfig.set(path, (Object)null);

      team.save(this.teamsConfig.createSection(path));
      this.saveFile();
   }

   public void deleteTeam(UUID teamId) {
      this.teamsConfig.set("teams." + String.valueOf(teamId), (Object)null);
      this.saveFile();
   }

   public Map<UUID, Team> loadAllTeams() {
      Map<UUID, Team> result = new HashMap();
      ConfigurationSection section = this.teamsConfig.getConfigurationSection("teams");
      if (section == null) {
         return result;
      } else {
         for(String key : section.getKeys(false)) {
            try {
               UUID id = UUID.fromString(key);
               Team team = new Team(id, section.getConfigurationSection(key));
               result.put(id, team);
            } catch (Exception e) {
               this.plugin.getLogger().warning("Failed to load team: " + key + " — " + e.getMessage());
            }
         }

         return result;
      }
   }

   public void saveAllTeams(Map<UUID, Team> teams) {
      this.teamsConfig.set("teams", (Object)null);

      for(Team team : teams.values()) {
         String path = "teams." + String.valueOf(team.getId());
         team.save(this.teamsConfig.createSection(path));
      }

      this.saveFile();
   }

   private void saveFile() {
      try {
         this.teamsConfig.save(this.teamsFile);
      } catch (IOException e) {
         this.plugin.getLogger().severe("Could not save teams.yml!");
         e.printStackTrace();
      }

   }
}
