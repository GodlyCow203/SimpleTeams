package net.godlycow.org.util;

import net.godlycow.org.SimpleTeams;

public class TeamNameValidator {

    private final SimpleTeams plugin;

    public TeamNameValidator(SimpleTeams plugin) {
        this.plugin = plugin;
    }

    public ValidationResult validate(String name) {
        int min = plugin.getConfigManager().getMinNameLength();
        int max = plugin.getConfigManager().getMaxNameLength();

        if (name.length() < min) {

            return ValidationResult.TOO_SHORT;
        }
        if (name.length() > max) {

            return ValidationResult.TOO_LONG;
        }

        String allowed = plugin.getConfigManager().getAllowedNameChars();
        if (!name.matches("[" + allowed + "]+")) {

            return ValidationResult.INVALID_CHARS;
        }

        for (String blacklisted : plugin.getConfigManager().getBlacklistedNames()) {
            if (name.equalsIgnoreCase(blacklisted)) {


                return ValidationResult.BLACKLISTED;
            }
        }

        return ValidationResult.VALID;
    }

    public enum ValidationResult {
        VALID,
        TOO_SHORT,
        TOO_LONG,
        INVALID_CHARS,
        BLACKLISTED
    }
}
