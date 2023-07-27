package ro.fr33styler.frconomy.database;

import ro.fr33styler.frconomy.account.Account;
import ro.fr33styler.frconomy.util.Top;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Database {

    private final Connection connection;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final String table;

    protected Database(Connection connection, String table) {
        this.connection = connection;
        this.table = table;
    }

    public void createTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + table + " (UUID VARCHAR(36) UNIQUE, NAME VARCHAR(60), BALANCE DOUBLE(64,2));");
        }
    }

    public boolean createAccount(Account account) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + table + " VALUES (?, ?, ?);")) {
            statement.setString(1, account.getUUID().toString());
            statement.setString(2, account.getName());
            statement.setDouble(3, account.getBalance());
            statement.executeUpdate();
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public boolean hasAccount(Account account) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT UUID, BALANCE FROM " + table + " WHERE UUID = ? LIMIT 1;")) {
            statement.setString(1, account.getUUID().toString());
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public void updateAccount(Account account) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE " + table + " SET UUID = ?, NAME = ?, BALANCE = ? WHERE UUID = ?;")) {
            statement.setString(1, account.getUUID().toString());
            statement.setString(2, account.getName());
            statement.setDouble(3, account.getBalance());
            statement.setString(4, account.getUUID().toString());
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void getAccount(Account account) {
        getAccount(account, null);
    }

    public void getAccount(Account account, Runnable ifFailed) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT BALANCE FROM " + table + " WHERE UUID = ? LIMIT 1;")) {
            statement.setString(1, account.getUUID().toString());
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    account.setBalance(result.getDouble(1));
                } else if (ifFailed != null) {
                    ifFailed.run();
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void getTopAsync(int position, Top consumer) {
        executor.execute(() -> {
            try (PreparedStatement statement = connection.prepareStatement("SELECT NAME, BALANCE FROM " + table + " ORDER BY BALANCE DESC;")) {
                try (ResultSet result = statement.executeQuery()) {
                    double congregatedBalances = 0;
                    List<Account> top = new ArrayList<>(10);
                    for (int i = 0; result.next(); i++) {
                        double balance = result.getDouble(2);
                        if (position <= i && position + 10 > i) {
                            String name = result.getString(1);
                            if (name != null && name.length() > 0) {
                                Account account = new Account(null, name);
                                account.setBalance(balance);
                                top.add(account);
                            }
                        }
                        congregatedBalances += balance;
                    }
                    consumer.accept(congregatedBalances, top);
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void close() {
        executor.shutdownNow().forEach(Runnable::run);
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
