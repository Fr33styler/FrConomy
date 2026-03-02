package ro.fr33styler.frconomy.database.mysql;

import org.apache.commons.lang.mutable.MutableBoolean;
import ro.fr33styler.frconomy.database.Database;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQL extends Database {

    public MySQL(Logger logger, MySQLData data) throws SQLException {
        super(data.getTable(), logger, DriverManager.getConnection("jdbc:mysql://" + data.getHost() + ":" + data.getPort() + "/" +
                data.getDatabase(), data.getProperties()));
    }

    @Override
    public boolean hasColumn(String table, String column) {
        MutableBoolean hasColumn = new MutableBoolean(false);
        try (Statement statement = connection.createStatement()) {
            try (ResultSet result = statement.executeQuery("SHOW COLUMNS FROM " + table + " LIKE '" + column + "';")) {
                hasColumn.setValue(result.next());
            }
        } catch (SQLException exception) {
            logger.log(Level.WARNING, exception.getMessage(), exception);
        }
        return hasColumn.booleanValue();
    }

}
