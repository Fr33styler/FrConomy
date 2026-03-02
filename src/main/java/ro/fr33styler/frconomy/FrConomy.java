package ro.fr33styler.frconomy;

import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import ro.fr33styler.frconomy.hooks.BlockingVault;
import ro.fr33styler.frconomy.hooks.ConfirmVault;
import ro.fr33styler.frconomy.hooks.OnlineOnlyVault;
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

import java.sql.SQLException;

public class FrConomy extends JavaPlugin {

    private Metrics metrics;
    private Database database;
    private Settings settings;
    private Messages messages;
    private Economy vaultHook;
    private Formatter formatter;
    private BukkitTask updateTask;
    private final Accounts accounts = new Accounts(this);

    public void onEnable() {
        saveDefaultConfig();
        ConsoleCommandSender console = getServer().getConsoleSender();
        console.sendMessage("§a=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        console.sendMessage("§a" + getName() + " plugin is loading... ");

        console.sendMessage("§a - Loading config...");
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();
        settings = new Settings(config.getConfigurationSection("settings"));
        messages = new Messages(config.getConfigurationSection("messages"));

        formatter = new Formatter(this);

        console.sendMessage("§a - Loading database...");
        try {
            loadDatabase(config.getConfigurationSection("mysql"));
        } catch (SQLException exception) {
            console.sendMessage("§cThe database has failed to load: " + exception.getMessage());
            setEnabled(false);
            return;
        }

        console.sendMessage("§a - Hooking into Vault...");
        if (settings.getVaultMethod().equalsIgnoreCase("confirm")) {
            vaultHook = new ConfirmVault(this);
        } else if (settings.getVaultMethod().equalsIgnoreCase("blocking")) {
            vaultHook = new BlockingVault(this);
        } else {
            vaultHook = new OnlineOnlyVault(this);
        }
        getServer().getServicesManager().register(Economy.class, vaultHook, this, ServicePriority.High);

        console.sendMessage("§a - Loading metrics...");
        metrics = new Metrics(this, 19272);

        console.sendMessage("§a - Loading events...");
        getServer().getPluginManager().registerEvents(new SessionEvents(this), this);

        console.sendMessage("§a - Loading commands...");
        registerCommand("pay", new Pay(this));
        registerCommand("eco", new Eco(this));
        registerCommand("balTop", new Top(this));
        registerCommand("money", new Money(this));

        updateTask = Bukkit.getScheduler().runTaskTimer(this, accounts::tick, 0, 1);

        Bukkit.getOnlinePlayers().forEach(accounts::add);
        console.sendMessage("§a" + getName() + " has been loaded!");
        console.sendMessage("§a=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
    }

    public void onDisable() {
        if (database != null) {
            metrics.shutdown();
            Bukkit.getOnlinePlayers().forEach(accounts::remove);

            getServer().getServicesManager().unregister(vaultHook);

            if (updateTask != null) {
                updateTask.cancel();
            }

            database.close();
        }
    }

    private void loadDatabase(ConfigurationSection section) throws SQLException {
        if (section != null && section.getBoolean("enabled")) {
            database = new MySQL(getLogger(), new MySQLData(section));
        } else {
            database = new SQLite(getLogger(), getDataFolder());
        }
        database.createTable();
    }

    private void registerCommand(String command, CommandExecutor executor) {
        PluginCommand pluginCommand = getCommand(command);
        pluginCommand.setExecutor(executor);
        if (executor instanceof TabCompleter) {
            pluginCommand.setTabCompleter((TabCompleter) executor);
        }
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