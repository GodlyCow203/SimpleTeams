package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import net.godlycow.org.util.TeamNameValidator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommand extends TeamSubCommand {
   private final TeamNameValidator nameValidator;

   public CreateCommand(SimpleTeams plugin) {
      super(plugin);
      this.nameValidator = new TeamNameValidator(plugin);
   }

   public String getName() {
      return "create";
   }

   public String getDescription() {
      return "Create a new team";
   }

   public String getUsage() {
      return "create <name>";
   }

   public String getPermission() {
      return "simpleteams.player.create";
   }

   public boolean isPlayerOnly() {
      return true;
   }

   public void execute(CommandSender sender, String[] args) {
      if (args.length < 2) {
         this.plugin.getMessageManager().send(sender, "usageCreate");
      } else {
         Player player = (Player)sender;
         String name = args[1];
         if (this.plugin.getTeamManager().hasTeam(player.getUniqueId())) {
            this.plugin.getMessageManager().send(sender, "alreadyInTeam");
         } else {
            TeamNameValidator.ValidationResult vr = this.nameValidator.validate(name);
            switch (vr) {
               case TOO_SHORT:
                  this.plugin.getMessageManager().send(sender, "nameTooShort", "min", String.valueOf(this.plugin.getConfigManager().getMinNameLength()));
                  return;
               case TOO_LONG:
                  this.plugin.getMessageManager().send(sender, "nameTooLong", "max", String.valueOf(this.plugin.getConfigManager().getMaxNameLength()));
                  return;
               case INVALID_CHARS:
                  this.plugin.getMessageManager().send(sender, "nameInvalidChars");
                  return;
               case BLACKLISTED:
                  this.plugin.getMessageManager().send(sender, "nameBlacklisted");
                  return;
               default:
                  if (this.plugin.getTeamManager().getTeamByName(name).isPresent()) {
                     this.plugin.getMessageManager().send(sender, "teamNameTaken", "team", name);
                  } else {
                     Team team = this.plugin.getTeamManager().createTeam(name, player.getUniqueId());
                     if (team == null) {
                        this.plugin.getMessageManager().send(sender, "teamLimitReached");
                     } else {
                        this.plugin.getMessageManager().send(sender, "teamCreated", "team", name);
                     }
                  }
            }
         }
      }
   }
}
