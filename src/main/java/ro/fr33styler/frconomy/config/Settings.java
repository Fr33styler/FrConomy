package ro.fr33styler.frconomy.config;

import org.bukkit.configuration.ConfigurationSection;

public class Settings {

    private final double defaultMoney;
    private final boolean useMinorIfLessThanOne;
    private final boolean shortScaleNotation;
    private final boolean payRequireConfirmation;
    private final boolean commasForGrouping;

    public Settings(ConfigurationSection section) {
        defaultMoney = section.getDouble("default-money");
        useMinorIfLessThanOne = section.getBoolean("use-minor-if-less-than-one");
        shortScaleNotation = section.getBoolean("short-scale-notation");
        payRequireConfirmation = section.getBoolean("pay-require-confirmation");
        commasForGrouping = section.getBoolean("commas-for-grouping");
    }

    public double getDefaultMoney() {
        return defaultMoney;
    }

    public boolean useMinorIfLessThanOne() {
        return useMinorIfLessThanOne;
    }

    public boolean hasShortScaleNotation() {
        return shortScaleNotation;
    }

    public boolean hasPayRequireConfirmation() {
        return payRequireConfirmation;
    }

    public boolean hasCommasForGrouping() {
        return commasForGrouping;
    }

}
