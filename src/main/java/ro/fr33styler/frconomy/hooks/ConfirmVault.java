package ro.fr33styler.frconomy.hooks;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import ro.fr33styler.frconomy.FrConomy;
import ro.fr33styler.frconomy.account.Account;

public class ConfirmVault extends AbstractEconomy {

    private final EconomyResponse isLoadingResponse;
    private final EconomyResponse depositNegativeResponse;
    private final EconomyResponse withdrawNegativeResponse;
    private final EconomyResponse insufficientFundsResponse;

    public ConfirmVault(FrConomy plugin) {
        super(plugin);

        isLoadingResponse = new EconomyResponse(0, 0,
                EconomyResponse.ResponseType.FAILURE, plugin.getMessages().getAccountIsLoading());
        depositNegativeResponse = new EconomyResponse(0, 0,
                EconomyResponse.ResponseType.FAILURE, plugin.getMessages().getDepositNegative());
        withdrawNegativeResponse = new EconomyResponse(0, 0,
                EconomyResponse.ResponseType.FAILURE, plugin.getMessages().getWithdrawNegative());
        insufficientFundsResponse = new EconomyResponse(0, 0,
                EconomyResponse.ResponseType.FAILURE, plugin.getMessages().getNotEnough());
    }

    @Override
    public EconomyResponse withdrawPlayer(String name, double amount) {
        return withdrawPlayer(Bukkit.getOfflinePlayer(name), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String name, String worldName, double amount) {
        return withdrawPlayer(name, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return withdrawNegativeResponse;
        Account account = plugin.getAccounts().getCachedAccount(player);

        if (account == null) {
            plugin.getAccounts().loadAccount(player);
            return isLoadingResponse;
        } else if (!account.isLoaded()) {
            return isLoadingResponse;
        }

        if (account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount);
            plugin.getSQLDatabase().updateAccount(account);
            return new EconomyResponse(amount, account.getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
        }
        return insufficientFundsResponse;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String name, double amount) {
        return depositPlayer(Bukkit.getOfflinePlayer(name), amount);
    }

    @Override
    public EconomyResponse depositPlayer(String name, String worldName, double amount) {
        return depositPlayer(name, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return depositNegativeResponse;
        Account account = plugin.getAccounts().getCachedAccount(player);

        if (account == null) {
            plugin.getAccounts().loadAccount(player);
            return isLoadingResponse;
        } else if (!account.isLoaded()) {
            return isLoadingResponse;
        }

        account.setBalance(account.getBalance() + amount);
        plugin.getSQLDatabase().updateAccount(account);
        return new EconomyResponse(amount, account.getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public double getBalance(String name) {
        return getBalance(Bukkit.getOfflinePlayer(name));
    }

    @Override
    public double getBalance(String name, String worldName) {
        return getBalance(name);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        Account account = plugin.getAccounts().getCachedAccount(player);

        if (account == null) {
            plugin.getAccounts().loadAccount(player);
            return 0;
        } else if (!account.isLoaded()) {
            return 0;
        }

        return account.getBalance();
    }

    @Override
    public double getBalance(OfflinePlayer player, String worldName) {
        return getBalance(player);
    }

    @Override
    public boolean has(String name, double amount) {
        return has(Bukkit.getOfflinePlayer(name), amount);
    }

    @Override
    public boolean has(String name, String worldName, double amount) {
        return has(name, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        Account account = plugin.getAccounts().getCachedAccount(player);

        if (account == null) {
            plugin.getAccounts().loadAccount(player);
            return false;
        } else if (!account.isLoaded()) {
            return false;
        }

        return account.getBalance() >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public boolean hasAccount(String name) {
        return hasAccount(Bukkit.getOfflinePlayer(name));
    }

    @Override
    public boolean hasAccount(String name, String worldName) {
        return hasAccount(name);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        Account account = plugin.getAccounts().getCachedAccount(player);

        if (account == null) {
            plugin.getAccounts().loadAccount(player);
            return false;
        } else return account.isLoaded();
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

}
