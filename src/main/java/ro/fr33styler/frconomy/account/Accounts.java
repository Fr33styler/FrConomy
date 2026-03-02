package ro.fr33styler.frconomy.account;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import ro.fr33styler.frconomy.FrConomy;

public class Accounts {

    private final FrConomy plugin;
    private final Map<UUID, Account> cached = new LinkedHashMap<>();

    public Accounts(FrConomy plugin) {
        this.plugin = plugin;
    }

    public void add(Player player) {
        Account account = getCachedAccount(player);
        if (account == null) {
            account = new Account(player.getUniqueId(), player.getName());
            account.setBalance(plugin.getSettings().getDefaultMoney());
            cached.put(player.getUniqueId(), account);

            plugin.getSQLDatabase().synchronizeOrInsertAccount(account);
        } else {
            account.setEliminationCountdown(0);
        }
    }

    public void remove(Player player) {
        cached.remove(player.getUniqueId());
    }

    public Account getCachedAccount(OfflinePlayer player) {
        return cached.get(player.getUniqueId());
    }

    public CompletableFuture<Account> getAccount(OfflinePlayer player) {
        Account account = cached.get(player.getUniqueId());

        if (account == null || !account.isLoaded()) {
            CompletableFuture<Account> futureAccount = plugin.getSQLDatabase().getAccount(player);
            futureAccount.thenAccept(newAccount -> Bukkit.getScheduler().runTask(plugin, () -> {
                cached.put(newAccount.getUUID(), newAccount);
                if (!player.isOnline()) newAccount.setEliminationCountdown(20);
            }));

            Account newAccount = new Account(player.getUniqueId(), player.getName());
            newAccount.setEliminationCountdown(20);
            cached.put(player.getUniqueId(), newAccount);

            return futureAccount;
        }

        return CompletableFuture.completedFuture(account);
    }

    public void tick() {
        Iterator<Account> iterator = cached.values().iterator();
        while (iterator.hasNext()) {
            Account account = iterator.next();
            if (Bukkit.getPlayer(account.getUUID()) == null) {
                if (account.getEliminationCountdown() > 0) {
                    account.setEliminationCountdown(account.getEliminationCountdown() - 1);
                } else {
                    iterator.remove();
                }
            }
        }
    }

}
