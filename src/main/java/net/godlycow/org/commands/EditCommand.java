package net.godlycow.org.commands;

import net.godlycow.org.SimpleTeams;
import net.godlycow.org.team.Team;
import net.godlycow.org.util.TeamNameValidator;
import java.util.Arrays;
import java.util.List;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditCommand extends TeamSubCommand {

    public EditCommand(SimpleTeams plugin) {
        super(plugin);
    }

    public String getName() {
        return "edit";
    }

    public String getDescription() {
        return "Edit team settings";
    }

    public String getUsage() {
        return "edit <prefix|name|description|color|motd> <value>";
    }

    public String getPermission() {
        return "simpleteams.player.edit";
    }

    public boolean isPlayerOnly() {
        return true;
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            plugin.getMessageManager().send(sender, "usageEdit");
            return;
        }

        Player player = (Player) sender;
        Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());

        if (team == null) {
            plugin.getMessageManager().send(sender, "notInTeam");
            return;
        }
        if (!team.isLeader(player.getUniqueId())) {
            plugin.getMessageManager().send(sender, "notEnoughPermissionInTeam");
            return;
        }

        String action = args[1].toLowerCase();
        String value = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        switch (action) {
            case "prefix" -> {
                String stripped = MiniMessage.miniMessage().stripTags(value);
                int maxLen = plugin.getConfigManager().getMaxPrefixLength();
                if (stripped.length() > maxLen) {
                    plugin.getMessageManager().send(sender, "prefixTooLong", "max", String.valueOf(maxLen));
                    return;
                }
                boolean hasFormatting = !value.equals(stripped);
                if (hasFormatting && !plugin.getConfigManager().isColoredPrefixAllowed()) {
                    plugin.getMessageManager().send(sender, "coloredPrefixNotAllowed");
                    return;
                }
                team.setPrefix(value);
                plugin.getTeamManager().saveTeam(team);
                plugin.getMessageManager().send(sender, "prefixChanged", "prefix", value);
            }
            case "name" -> {
                TeamNameValidator nameValidator = new TeamNameValidator(plugin);
                TeamNameValidator.ValidationResult vr = nameValidator.validate(value);
                switch (vr) {
                    case TOO_SHORT -> {
                        plugin.getMessageManager().send(sender, "nameTooShort", "min",
                                String.valueOf(plugin.getConfigManager().getMinNameLength()));
                        return;
                    }
                    case TOO_LONG -> {
                        plugin.getMessageManager().send(sender, "nameTooLong", "max",
                                String.valueOf(plugin.getConfigManager().getMaxNameLength()));
                        return;
                    }
                    case INVALID_CHARS -> {
                        plugin.getMessageManager().send(sender, "nameInvalidChars");
                        return;
                    }
                    case BLACKLISTED -> {
                        plugin.getMessageManager().send(sender, "nameBlacklisted");
                        return;
                    }
                    default -> {
                        if (plugin.getTeamManager().getTeamByName(value).isPresent()) {
                            plugin.getMessageManager().send(sender, "teamNameTaken", "team", value);
                            return;
                        }
                        team.setName(value);
                        plugin.getTeamManager().saveTeam(team);
                        plugin.getMessageManager().send(sender, "nameChanged", "name", value);
                    }
                }
            }
            case "description" -> {
                int maxDesc = plugin.getConfigManager().getMaxDescriptionLength();
                if (value.length() > maxDesc) {
                    plugin.getMessageManager().send(sender, "descriptionTooLong", "max", String.valueOf(maxDesc));
                    return;
                }
                team.setDescription(value);
                plugin.getTeamManager().saveTeam(team);
                plugin.getMessageManager().send(sender, "descriptionChanged", "description", value);
            }
            case "color" -> {
                team.setColor(value);
                plugin.getTeamManager().saveTeam(team);
                plugin.getMessageManager().send(sender, "colorChanged", "color", value);
            }
            case "motd" -> {
                int maxMotd = plugin.getConfigManager().getMaxMotdLength();
                if (value.length() > maxMotd) {
                    plugin.getMessageManager().send(sender, "motdTooLong", "max", String.valueOf(maxMotd));
                    return;
                }
                team.setMotd(value);
                plugin.getTeamManager().saveTeam(team);
                plugin.getMessageManager().send(sender, "motdChanged", "motd", value);
            }
            default -> plugin.getMessageManager().send(sender, "usageEdit");
        }
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return args.length == 2
                ? List.of("prefix", "name", "description", "color", "motd")
                : List.of();
    }
}
