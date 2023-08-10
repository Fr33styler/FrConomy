package ro.fr33styler.frconomy.util;

import ro.fr33styler.frconomy.FrConomy;

public class Formatter {

    private final FrConomy plugin;

    public Formatter(FrConomy plugin) {
        this.plugin = plugin;
    }

    private int currencyMajor(double balance) {
        return (int) balance;
    }

    private int currencyMinor(double balance) {
        return (int) (balance * 100) % 100;
    }

    public String getCurrency(double balance) {
        if (plugin.getSettings().useMinorIfLessThanOne() && balance < 1) {
            return getCurrencyMinor(balance);
        }
        return getCurrencyMajor(balance);
    }

    public String getCurrencyMinor(double balance) {
        int minor = currencyMinor(balance);
        return minor == 1 ? plugin.getMessages().getCurrencyMinorSingular() : plugin.getMessages().getCurrencyMinorPlural();
    }

    public String getCurrencyMajor(double balance) {
        int major = currencyMajor(balance);
        return major == 1 ? plugin.getMessages().getCurrencyMajorSingular() : plugin.getMessages().getCurrencyMajorPlural();
    }

    public String formatCurrency(double balance) {
        String currencyFormat = plugin.getMessages().getCurrencyFormat();
        if (plugin.getSettings().hasShortScaleNotation()) {
            currencyFormat = currencyFormat.replace("%money%", EconomyUtil.toShortScaleNotation(balance, false));
            currencyFormat = currencyFormat.replace("%money_commas%", EconomyUtil.toShortScaleNotation(balance, true));
        } else {
            currencyFormat = currencyFormat.replace("%money%", EconomyUtil.getDecimalFormat().format(balance));
            currencyFormat = currencyFormat.replace("%money_commas%", EconomyUtil.getDecimalFormatCommas().format(balance));
        }
        currencyFormat = currencyFormat.replace("%money_major%", EconomyUtil.getWholeFormat().format(currencyMajor(balance)));
        currencyFormat = currencyFormat.replace("%money_major_commas%", EconomyUtil.getWholeFormatCommas().format(currencyMajor(balance)));
        currencyFormat = currencyFormat.replace("%money_minor%", String.valueOf(currencyMinor(balance)));
        currencyFormat = currencyFormat.replace("%currency%", getCurrency(balance));
        currencyFormat = currencyFormat.replace("%currency_major%", getCurrencyMajor(balance));
        currencyFormat = currencyFormat.replace("%currency_minor%", getCurrencyMinor(balance));
        return currencyFormat;
    }

}
