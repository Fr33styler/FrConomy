package ro.fr33styler.frconomy.config;

import org.bukkit.configuration.ConfigurationSection;

public class Messages {

    private final String currencyMajorPlural;
    private final String currencyMajorSingular;
    private final String currencyMinorPlural;
    private final String currencyMinorSingular;
    private final String currencyFormat;

    private final String money;
    private final String moneyPlayer;
    private final String confirmationRequired;
    private final String sentTo;
    private final String receivedFrom;
    private final String notEnough;
    private final String permission;
    private final String top;
    private final String topRank;
    private final String topNotFound;
    private final String positive;
    private final String payAmountArgument;
    private final String payAccount;
    private final String payNotNumber;
    private final String moneyYourself;
    private final String topCongregatedMoney;

    public Messages(ConfigurationSection section) {
        currencyMajorPlural = get(section, "currency-major-plural");
        currencyMajorSingular = get(section, "currency-major-singular");
        currencyMinorPlural = get(section, "currency-minor-plural");
        currencyMinorSingular = get(section, "currency-minor-singular");
        currencyFormat = get(section, "currency-format");
        receivedFrom = get(section, "received-from");
        moneyPlayer = get(section, "money-player");
        confirmationRequired = get(section, "confirmation-required");
        notEnough = get(section, "not-enough");
        sentTo = get(section, "sent-to");
        money = get(section, "money");
        permission = get(section, "no-permission");
        moneyYourself = get(section, "money-yourself");
        payAmountArgument = get(section, "pay-amount-argument");
        payAccount = get(section, "pay-account");
        payNotNumber = get(section, "pay-not-number");
        top = get(section, "top");
        topRank = get(section, "top-rank");
        topNotFound = get(section, "top-not-found");
        topCongregatedMoney = get(section, "top-congregated-money");
        positive = get(section, "positive");
    }

    public String getCurrencyMajorPlural() {
        return currencyMajorPlural;
    }

    public String getCurrencyMajorSingular() {
        return currencyMajorSingular;
    }

    public String getCurrencyMinorPlural() {
        return currencyMinorPlural;
    }

    public String getCurrencyMinorSingular() {
        return currencyMinorSingular;
    }

    public String getCurrencyFormat() {
        return currencyFormat;
    }

    public String getMoney() {
        return money;
    }

    public String getMoneyPlayer() {
        return moneyPlayer;
    }

    public String getConfirmationRequired() {
        return confirmationRequired;
    }

    public String getPermission() {
        return permission;
    }

    public String getNotEnough() {
        return notEnough;
    }

    public String getSentTo() {
        return sentTo;
    }

    public String getReceivedFrom() {
        return receivedFrom;
    }

    public String getTop() {
        return top;
    }

    public String getTopRank() {
        return topRank;
    }

    public String getTopNotFound() {
        return topNotFound;
    }

    public String getTopCongregatedMoney() {
        return topCongregatedMoney;
    }

    public String getPositive() {
        return positive;
    }

    public String getPayAmountArgument() {
        return payAmountArgument;
    }

    public String getPayAccount() {
        return payAccount;
    }

    public String getPayNotNumber() {
        return payNotNumber;
    }

    public String getMoneyYourself() {
        return moneyYourself;
    }
    
    private String get(ConfigurationSection section, String path) {
        return section.getString(path).replace('&', '§');
    }


}
