package net.tee7even.presents;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import net.tee7even.presents.utils.Message;
import net.tee7even.presents.utils.TextTagManager;

/**
 * @author Tee7even
 */
public class Presents extends PluginBase {
    private static Presents plugin;

    public static Presents getPlugin() {
        return plugin;
    }

    public Presents() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);
        saveResource("weights.yml", false);

        checkConfigVersion();

        Message.init();
        ChestManager.init();

        this.getServer().getPluginManager().registerEvents(new TextTagManager(), this);
        this.getServer().getPluginManager().registerEvents(new ChestListener(), this);
    }

    @Override
    public void onDisable() {
        ChestManager.close();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            return false;
        } else if (command.getName().equals("placechest")) {
            ChestManager.place((Player) sender);
        }

        return true;
    }

    private void checkConfigVersion() {
        Config internalConfig = new Config();
        internalConfig.load(getResource("config.yml"));
        if (getConfig().getInt("version", -1) != internalConfig.getInt("version")) {
            getLogger().warning("Your config.yml is out of date. New features are added.\n" +
                        "Back it up, if needed, and delete. It will be replaced by a new one on start.");
        }
    }
}
