package ro.fr33styler.frconomy.hooks;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import ro.fr33styler.frconomy.FrConomy;

import java.util.Collections;
import java.util.List;

public abstract class AbstractEconomy implements Economy {

    private static final EconomyResponse BANK_UNSUPPORTED = new EconomyResponse(0, 0,
            EconomyResponse.ResponseType.FAILURE, "The bank is not supported!");

    protected final FrConomy plugin;

    public AbstractEconomy(FrConomy plugin) {
        this.plugin = plugin;
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

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return false;
    }

    @Override
    public EconomyResponse isBankMember(String name, String worldName) {
        return BANK_UNSUPPORTED;
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return BANK_UNSUPPORTED;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String worldName) {
        return BANK_UNSUPPORTED;
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
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

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return BANK_UNSUPPORTED;
    }

}
