package ro.fr33styler.frconomy.account;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import ro.fr33styler.frconomy.FrConomy;

public class Accounts {

    private final FrConomy plugin;
    private final Map<UUID, Account> cached = new HashMap<>();

    public Accounts(FrConomy plugin) {
        this.plugin = plugin;
    }

    public void add(Player player) {
        Account account = new Account(player.getUniqueId(), player.getName());
        account.setBalance(plugin.getSettings().getDefaultMoney());
        cached.put(player.getUniqueId(), account);
        plugin.getSQLDatabase().getAccount(account, () -> plugin.getSQLDatabase().createAccount(account));
    }

    public void remove(Player player) {
        Account account = cached.remove(player.getUniqueId());
        if (account != null) {
            plugin.getSQLDatabase().updateAccount(account);
        }
    }

    public boolean createAccount(OfflinePlayer player) {
        Account account = cached.get(player.getUniqueId());
        if (account == null) {
            account = new Account(player);
            account.setBalance(plugin.getSettings().getDefaultMoney());
            if (!plugin.getSQLDatabase().hasAccount(account)) {
                return plugin.getSQLDatabase().createAccount(account);
            }
        }
        return false;
    }

    public Account getAccount(OfflinePlayer player) {
        Account account = cached.get(player.getUniqueId());
        if (account == null) {
            account = new Account(player);
            plugin.getSQLDatabase().getAccount(account);
        }
        return account;
    }

    public boolean hasAccount(OfflinePlayer player) {
        Account account = cached.get(player.getUniqueId());
        if (account == null) {
            return plugin.getSQLDatabase().hasAccount(new Account(player));
        }
        return true;
    }

}
