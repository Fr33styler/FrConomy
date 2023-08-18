package ro.fr33styler.frconomy.util;

import ro.fr33styler.frconomy.FrConomy;
import ro.fr33styler.frconomy.config.Settings;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Formatter {

    private final FrConomy plugin;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

    public Formatter(FrConomy plugin) {
        this.plugin = plugin;
        loadFormat(plugin.getSettings());
    }

    private void loadFormat(Settings settings) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        if (settings.hasCommasForGrouping()) {
            symbols.setDecimalSeparator('.');
            symbols.setGroupingSeparator(',');
        } else {
            symbols.setDecimalSeparator(',');
            symbols.setGroupingSeparator('.');
        }
        decimalFormat.setDecimalFormatSymbols(symbols);
        decimalFormat.setMinimumFractionDigits(0);
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

    public String getCurrencyMajor(double balance) {
        int major = currencyMajor(balance);
        return major == 1 ? plugin.getMessages().getCurrencyMajorSingular() : plugin.getMessages().getCurrencyMajorPlural();
    }

    public String getCurrencyMinor(double balance) {
        int minor = currencyMinor(balance);
        return minor == 1 ? plugin.getMessages().getCurrencyMinorSingular() : plugin.getMessages().getCurrencyMinorPlural();
    }

    public String formatCurrency(double balance) {
        String currencyFormat = plugin.getMessages().getCurrencyFormat();
        if (plugin.getSettings().hasShortScaleNotation()) {
            currencyFormat = currencyFormat.replace("%money%", ShortScaleUtil.toShortScaleNotation(decimalFormat, balance));
        } else {
            currencyFormat = currencyFormat.replace("%money%", decimalFormat.format(balance));
        }
        currencyFormat = currencyFormat.replace("%money_major%", decimalFormat.format(currencyMajor(balance)));
        currencyFormat = currencyFormat.replace("%money_minor%", String.valueOf(currencyMinor(balance)));
        currencyFormat = currencyFormat.replace("%currency%", getCurrency(balance));
        currencyFormat = currencyFormat.replace("%currency_major%", getCurrencyMajor(balance));
        currencyFormat = currencyFormat.replace("%currency_minor%", getCurrencyMinor(balance));
        return currencyFormat;
    }

}
