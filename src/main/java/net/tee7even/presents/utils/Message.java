package net.tee7even.presents.utils;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import net.tee7even.presents.Presents;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tee7even
 */
public class Message {
    private static Map<String, String> messages = new HashMap<>();

    public static void init() {
        Config messagesConfig = new Config(new File(Presents.getPlugin().getDataFolder(), "messages.yml"), Config.YAML);
        checkVersion(messagesConfig);

        for (String key : messagesConfig.getKeys(true)) { //todo skip all nested contents
            messages.put(key, TextFormat.colorize(messagesConfig.getString(key)));
        }
    }

    private static void checkVersion(Config messagesConfig) {
        Config internalMessages = new Config();
        internalMessages.load(Message.class.getResourceAsStream("/messages.yml"));
        if (messagesConfig.getInt("version", -1) != internalMessages.getInt("version")) {
            if (Presents.getPlugin().getConfig().getBoolean("messages-autoupdate", false)) {
                Presents.getPlugin().saveResource("messages.yml", true);
                messagesConfig.reload();
            } else {
                Presents.getPlugin().getLogger().warning("Your messages.yml is out of date. It can cause some troubles with messages.\n" +
                        "Back it up, if needed, and delete. It will be replaced by a new one on start.");
            }
        }
    }

    public static String get(String key, String... replacements) {
        if (messages.containsKey(key)) {
            String message = messages.get(key);
            for (int i = 0; i < replacements.length; i++) {
                message = message.replace("{%" + i + "}", replacements[i]);
            }
            return message;
        } else {
            return key;
        }
    }

    public static void send(CommandSender sender, String key, String... replacements) {
        sender.sendMessage(get(key, replacements));
    }
}
