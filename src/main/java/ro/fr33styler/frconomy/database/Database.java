package ro.fr33styler.frconomy.database;

import org.bukkit.OfflinePlayer;
import ro.fr33styler.frconomy.account.Account;
import ro.fr33styler.frconomy.database.mysql.MySQL;
import ro.fr33styler.frconomy.util.TopBalance;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Database {

    private final String table;
    protected final Logger logger;
    protected final Connection connection;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    protected Database(String table, Logger logger, Connection connection) {
        this.table = table;
        this.logger = logger;
        this.connection = connection;
    }

    public void createTable() {
        upgradeTable();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + table + " (uuid CHAR(36) UNIQUE, name VARCHAR(16), balance DOUBLE);");
        } catch (SQLException exception) {
            logger.log(Level.WARNING, exception.getMessage(), exception);
        }
    }

    public CompletableFuture<Account> getAccount(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> getAccountSync(player), executor);
    }

    public Account getAccountSync(OfflinePlayer player) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT balance FROM " + table + " WHERE uuid = ? LIMIT 1;")) {
            statement.setString(1, player.getUniqueId().toString());
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    Account account = new Account(player.getUniqueId(), player.getName());
                    account.setBalance(result.getDouble("balance"));
                    account.setLoaded(true);
                    return account;
                }
            }
        } catch (SQLException exception) {
            logger.log(Level.WARNING, exception.getMessage(), exception);
        }
        return null;
    }

    public void updateAccount(Account account) {
        executor.submit(() -> updateAccountSync(account));
    }

    public void updateAccountSync(Account account) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE " + table + " SET name = ?, balance = ? WHERE uuid = ?;")) {
            statement.setString(1, account.getName());
            statement.setDouble(2, account.getBalance());
            statement.setString(3, account.getUUID().toString());
            statement.executeUpdate();
        } catch (SQLException exception) {
            logger.log(Level.WARNING, exception.getMessage(), exception);
        }
    }

    public void synchronizeOrInsertAccount(Account account) {
        executor.submit(() -> synchronizeOrInsertAccountSync(account));
    }

    public void synchronizeOrInsertAccountSync(Account account) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT balance FROM " + table + " WHERE uuid = ? LIMIT 1;")) {
            statement.setString(1, account.getUUID().toString());
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    account.setBalance(result.getDouble("balance"));
                } else {
                    insertAccountSync(account);
                }
                account.setLoaded(true);
            }
        } catch (SQLException exception) {
            logger.log(Level.WARNING, exception.getMessage(), exception);
        }
    }

    private void insertAccountSync(Account account) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + table + " VALUES (?, ?, ?);")) {
            statement.setString(1, account.getUUID().toString());
            statement.setString(2, account.getName());
            statement.setDouble(3, account.getBalance());
            statement.executeUpdate();
        } catch (SQLException exception) {
            logger.log(Level.WARNING, exception.getMessage(), exception);
        }
    }

    public CompletableFuture<TopBalance> getTop(int index) {
        return CompletableFuture.supplyAsync(() -> {
            List<Account> top = new ArrayList<>(10);
            BigDecimal totalBalance = BigDecimal.ZERO;
            try (Statement statement = connection.createStatement()) {
                try (ResultSet result = statement.executeQuery("SELECT name, balance FROM " + table + " ORDER BY balance DESC LIMIT 10 OFFSET " + index + ";")) {
                    while (result.next()) {
                        String name = result.getString("name");
                        double balance = result.getDouble("balance");
                        if (name != null && !name.isEmpty()) {
                            Account account = new Account(null, name);
                            account.setBalance(balance);
                            top.add(account);
                        }
                    }
                }
                try (ResultSet result = statement.executeQuery("SELECT SUM(balance) AS total_balance FROM " + table + ";")) {
                    if (result.next()) {
                        totalBalance = result.getBigDecimal("total_balance");
                    }
                }
            } catch (SQLException exception) {
                logger.log(Level.WARNING, exception.getMessage(), exception);
                return null;
            }
            return new TopBalance(top, totalBalance);
        }, executor);
    }

    public abstract boolean hasColumn(String table, String column);

    private void upgradeTable() {
        if (hasColumn(table, "BALANCE") && !hasColumn(table, "balance")) {
            logger.log(Level.WARNING, "Upgrading " + table + " to the newer format. Please don't stop the server!");

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + table + "_new (uuid CHAR(36) UNIQUE, name VARCHAR(16), balance DOUBLE);");
            } catch (SQLException exception) {
                logger.log(Level.WARNING, exception.getMessage(), exception);
            }

            transferData(table);
            renameTable();
        }
    }

    private void renameTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS " + table + ";");

            if (this instanceof MySQL) {
                statement.executeUpdate("RENAME TABLE " + table + "_new TO " + table + ";");
            } else {
                statement.executeUpdate("ALTER TABLE " + table + "_new RENAME TO " + table + ";");
            }
        } catch (SQLException exception) {
            logger.log(Level.WARNING, exception.getMessage(), exception);
        }
    }

    private void transferData(String table) {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet result = statement.executeQuery("SELECT * from " + table + ";")) {
                String uuid = result.getString("UUID");
                String name = result.getString("NAME");
                double balance = result.getDouble("BALANCE");
                insertIntoTable(uuid, name, balance);
            }
        } catch (SQLException exception) {
            logger.log(Level.WARNING, exception.getMessage(), exception);
        }
    }

    private void insertIntoTable(String uuid, String name, double balance) {
        String databaseSpecific = this instanceof MySQL ? "IGNORE" : "OR IGNORE";
        try (PreparedStatement statement = connection.prepareStatement("INSERT " + databaseSpecific + " INTO " + table + "_new VALUES (?, ?, ?)")) {
            statement.setString(1, uuid);
            statement.setString(2, name);
            statement.setDouble(3, balance);
            statement.executeUpdate();
        } catch (SQLException exception) {
            logger.log(Level.WARNING, exception.getMessage(), exception);
        }
    }

    public void close() {
        try {
            List<Runnable> remaining = executor.shutdownNow();
            if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
                logger.log(Level.WARNING, "Executor did not terminate properly");
            }
            remaining.forEach(Runnable::run);
            connection.close();
        } catch (SQLException | InterruptedException exception) {
            logger.log(Level.WARNING, exception.getMessage(), exception);
        }
    }

}
