package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;

public abstract class TeamSubCommand implements SubCommand {
   protected final SimpleTeams plugin;

   protected TeamSubCommand(SimpleTeams plugin) {
      this.plugin = plugin;
   }

   protected void broadcast(Team team, UUID exclude, String messageKey, String... placeholders) {
      team.getMembers().keySet().stream().filter((uuid) -> !uuid.equals(exclude)).map(Bukkit::getPlayer).filter(Objects::nonNull).forEach((p) -> this.plugin.getMessageManager().send(p, messageKey, placeholders));
   }
}
