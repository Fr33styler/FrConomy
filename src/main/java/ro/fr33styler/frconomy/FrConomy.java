package ro.fr33styler.frconomy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import ro.fr33styler.frconomy.util.Formatter;
import ro.fr33styler.frconomy.account.Accounts;
import ro.fr33styler.frconomy.command.*;
import ro.fr33styler.frconomy.config.Messages;
import ro.fr33styler.frconomy.config.Settings;
import ro.fr33styler.frconomy.database.Database;
import ro.fr33styler.frconomy.database.mysql.MySQL;
import ro.fr33styler.frconomy.database.mysql.MySQLData;
import ro.fr33styler.frconomy.database.sqlite.SQLite;
import ro.fr33styler.frconomy.events.SessionEvents;
import ro.fr33styler.frconomy.hooks.VaultHook;
import ro.fr33styler.frconomy.util.FrCommand;

import java.sql.SQLException;

public class FrConomy extends JavaPlugin {

    private Database database;
    private Settings settings;
    private Messages messages;
    private VaultHook vaultHook;
    private final Accounts accounts = new Accounts(this);
    private final Formatter formatter = new Formatter(this);

    public void onEnable() {
        saveDefaultConfig();
        ConsoleCommandSender console = getServer().getConsoleSender();
        console.sendMessage("§a=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        console.sendMessage("§a" + getName() + " plugin is loading... ");

        console.sendMessage("§a - Loading config...");
        FileConfiguration config = getConfig();
        settings = new Settings(config.getConfigurationSection("settings"));
        messages = new Messages(config.getConfigurationSection("messages"));

        console.sendMessage("§a - Loading database...");
        try {
            loadDatabase(config.getConfigurationSection("mysql"));
        } catch (SQLException exception) {
            console.sendMessage("§cThe database has failed to load: " + exception.getMessage());
            setEnabled(false);
            return;
        }

        console.sendMessage("§a - Hooking into Vault...");
        getServer().getServicesManager().register(Economy.class, vaultHook = new VaultHook(this), this, ServicePriority.High);

        console.sendMessage("§a - Loading events...");
        getServer().getPluginManager().registerEvents(new SessionEvents(this), this);

        console.sendMessage("§a - Loading commands...");
        registerCommand("pay", new Pay(this));
        registerCommand("eco", new Eco(this));
        registerCommand("balTop", new Top(this));
        registerCommand("money", new Money(this));

        Bukkit.getOnlinePlayers().forEach(accounts::add);
        console.sendMessage("§a" + getName() + " has been loaded!");
        console.sendMessage("§a=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
    }

    public void onDisable() {
        if (database != null) {
            Bukkit.getOnlinePlayers().forEach(accounts::remove);

            getServer().getServicesManager().unregister(vaultHook);

            database.close();
        }
    }

    private void loadDatabase(ConfigurationSection section) throws SQLException {
        if (section != null && section.getBoolean("enabled")) {
            database = new MySQL(new MySQLData(section));
        } else {
            database = new SQLite(getDataFolder());
        }
        database.createTable();
    }

    private void registerCommand(String command, FrCommand FrCommand) {
        PluginCommand pluginCommand = getCommand(command);
        pluginCommand.setExecutor(FrCommand);
        pluginCommand.setTabCompleter(FrCommand);
    }

    public Settings getSettings() {
        return settings;
    }

    public Messages getMessages() {
        return messages;
    }

    public Accounts getAccounts() {
        return accounts;
    }

    public Formatter getFormatter() {
        return formatter;
    }

    public Database getSQLDatabase() {
        return database;
    }

}