package ro.fr33styler.frconomy.database.mysql;

import ro.fr33styler.frconomy.database.Database;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL extends Database {

    public MySQL(MySQLData data) throws SQLException {
        super(DriverManager.getConnection("jdbc:mysql://" + data.getHost() + ":" + data.getPort() + "/" +
                data.getDatabase(), data.getProperties()));
    }

}
