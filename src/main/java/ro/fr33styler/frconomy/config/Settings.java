package ro.fr33styler.frconomy.config;

import org.bukkit.configuration.ConfigurationSection;

public class Settings {

    private final double defaultMoney;
    private final boolean useMinorIfLessThanOne;

    public Settings(ConfigurationSection section) {
        defaultMoney = section.getDouble("default-money");
        useMinorIfLessThanOne = section.getBoolean("use-minor-if-less-than-one");
    }

    public double getDefaultMoney() {
        return defaultMoney;
    }

    public boolean useMinorIfLessThanOne() {
        return useMinorIfLessThanOne;
    }
}
