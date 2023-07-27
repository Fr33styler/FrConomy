package ro.fr33styler.frconomy.hooks;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import ro.fr33styler.frconomy.FrConomy;
import ro.fr33styler.frconomy.account.Account;

import java.util.Collections;
import java.util.List;

public class VaultHook extends AbstractEconomy {

    private final FrConomy plugin;

    public VaultHook(FrConomy main) {
        this.plugin = main;
    }

    @Override
    public String getName() {
        return plugin.getDescription().getName();
    }

    @Override
    public boolean isEnabled() {
        return plugin.isEnabled();
    }

    @Override
    public String currencyNamePlural() {
        return plugin.getMessages().getCurrencyMajorPlural();
    }

    @Override
    public String currencyNameSingular() {
        return plugin.getMessages().getCurrencyMajorSingular();
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double balance) {
        return plugin.getFormatter().formatCurrency(balance);
    }

    private static final EconomyResponse NEGATIVE_FOUND = new EconomyResponse(0, 0,
            ResponseType.FAILURE, "Cannot deposit negative funds");;

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return NEGATIVE_FOUND;
        Account account = plugin.getAccounts().getAccount(player);
        if (account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount);
            return new EconomyResponse(amount, account.getBalance(), ResponseType.SUCCESS, null);
        }
        return new EconomyResponse(0, account.getBalance(), ResponseType.FAILURE, "Insufficient funds");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return NEGATIVE_FOUND;
        Account account = plugin.getAccounts().getAccount(player);
        account.setBalance(account.getBalance() + amount);
        plugin.getSQLDatabase().updateAccount(account);
        return new EconomyResponse(amount, account.getBalance(), ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return plugin.getAccounts().createAccount(player);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return plugin.getAccounts().getAccount(player).getBalance();
    }

    @Override
    public double getBalance(OfflinePlayer player, String worldName) {
        return getBalance(player);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return plugin.getAccounts().getAccount(player).getBalance() >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return plugin.getAccounts().hasAccount(player);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public boolean createPlayerAccount(String name) {
        return createPlayerAccount(Bukkit.getOfflinePlayer(name));
    }

    @Override
    public boolean createPlayerAccount(String name, String worldName) {
        return createPlayerAccount(name);
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
    public double getBalance(String name) {
        return getBalance(Bukkit.getOfflinePlayer(name));
    }

    @Override
    public double getBalance(String name, String worldName) {
        return getBalance(name);
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
    public boolean hasAccount(String name) {
        return hasAccount(Bukkit.getOfflinePlayer(name));
    }

    @Override
    public boolean hasAccount(String name, String worldName) {
        return hasAccount(name);
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
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    private static final EconomyResponse BANK_UNSUPPORTED = new EconomyResponse(0.0, 0.0,
            ResponseType.FAILURE, "The bank is not supported!");

    @Override
    public EconomyResponse isBankMember(String name, String worldName) {
        return BANK_UNSUPPORTED;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String worldName) {
        return BANK_UNSUPPORTED;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return BANK_UNSUPPORTED;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return BANK_UNSUPPORTED;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return BANK_UNSUPPORTED;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return BANK_UNSUPPORTED;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return BANK_UNSUPPORTED;
    }

    @Override
    public EconomyResponse createBank(String name, String worldName) {
        return BANK_UNSUPPORTED;
    }

}
