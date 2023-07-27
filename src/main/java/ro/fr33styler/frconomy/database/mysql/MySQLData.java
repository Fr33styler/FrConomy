package ro.fr33styler.frconomy.database.mysql;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Properties;

public class MySQLData {

    private final String host;
    private final String database;
    private final int port;
    private final Properties properties = new Properties();

    public MySQLData(ConfigurationSection section) {
        host = section.getString("host");
        database = section.getString("database");
        port = section.getInt("port");

        properties.clear();
        properties.put("user", section.getString("username"));
        properties.put("password", section.getString("password"));
        ConfigurationSection propertiesSection = section.getConfigurationSection("properties");
        if (propertiesSection != null) {
            for (String key : propertiesSection.getKeys(false)) {
                properties.put(key, propertiesSection.getString(key));
            }
        }
    }

    public String getHost() {
        return host;
    }

    public String getDatabase() {
        return database;
    }

    public int getPort() {
        return port;
    }

    public Properties getProperties() {
        return properties;
    }

}
