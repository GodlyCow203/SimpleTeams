package net.godlycow.org.manager;

import net.godlycow.org.SimpleTeams;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessageManager {

    private final SimpleTeams plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private FileConfiguration messages;
    private File messagesFile;

    public MessageManager(SimpleTeams plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = new YamlConfiguration();

        try {
            messages.load(messagesFile);
        } catch (Exception e) {
            plugin.getLogger().severe("Could not parse messages.yml: " + e.getMessage()
                    + " - falling back to bundled defaults.");
        }

        // Always fall back to the bundled messages so missing/outdated keys still resolve.
        InputStream defaultsStream = plugin.getResource("messages.yml");
        if (defaultsStream != null) {
            messages.setDefaults(YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultsStream, StandardCharsets.UTF_8)));
        }
    }

    public void reload() {
        load();
    }

    private String getPrefix() {
        return plugin.getConfig().getString("prefix", "<dark_gray>[<aqua>SimpleTeams</aqua>]</dark_gray> ");
    }

    public Component parse(String key, String... placeholders) {
        String raw = messages.getString(key, "<red>Missing message: " + key + "</red>");
        raw = applyPlaceholders(raw, placeholders);
        String withPrefix = getPrefix() +  raw;

        return miniMessage.deserialize(withPrefix); // return parsed component
    }

    public Component parseRaw(String key, String... placeholders) {
        String raw = messages.getString(key, "<red>Missing message: " + key + "</red>");
        raw = applyPlaceholders(raw, placeholders);
        return miniMessage.deserialize(raw);
    }

    public void send(CommandSender sender, String key, String... placeholders) {
        sender.sendMessage(parse(key, placeholders));
    }

    public void sendRaw(CommandSender sender, String key, String... placeholders) {
        sender.sendMessage(parseRaw(key, placeholders));
    }

//    public void sendList(CommandSender sender, String key) { // keep sendList for future changes
//        for (String line : messages.getStringList(key)) {
//            sender.sendMessage(miniMessage.deserialize(line));
//        }
//    }

    public String getRaw(String key, String... placeholders) {
        String raw = messages.getString(key, "");
        return applyPlaceholders(raw, placeholders);
    }

    private String applyPlaceholders(String text, String[] placeholders) {
        for (int i = 0; i + 1 < placeholders.length; i += 2) {
            text = text.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
        }
        return text;
    }

//    public String stripTags(String miniMessageString) {
//        return MiniMessage.miniMessage().stripTags(miniMessageString);
//    }
}
