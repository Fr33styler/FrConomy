package ro.fr33styler.frconomy.database.sqlite;

import org.apache.commons.lang.mutable.MutableBoolean;
import ro.fr33styler.frconomy.database.Database;

import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLite extends Database {

    public SQLite(Logger logger, File dataFolder) throws SQLException {
        super("frConomy", logger, DriverManager.getConnection("jdbc:sqlite:" + new File(dataFolder, "database.db")));
    }

    @Override
    public boolean hasColumn(String table, String column) {
        MutableBoolean hasColumn = new MutableBoolean(false);

        try (Statement statement = connection.createStatement()) {
            try (ResultSet result = statement.executeQuery("PRAGMA table_info(\"" + table + "\");")) {
                while (result.next()) {
                    if (result.getString("name").equals(column)) {
                        hasColumn.setValue(true);
                        break;
                    }
                }
            }
        } catch (SQLException exception) {
            logger.log(Level.WARNING, exception.getMessage(), exception);
        }
        return hasColumn.booleanValue();
    }

}
