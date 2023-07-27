package ro.fr33styler.frconomy.database.sqlite;

import ro.fr33styler.frconomy.database.Database;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite extends Database {

    public SQLite(File dataFolder) throws SQLException {
        super(DriverManager.getConnection("jdbc:sqlite:" + new File(dataFolder, "database.db")), "frConomy");
    }

}
