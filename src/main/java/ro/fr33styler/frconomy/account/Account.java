package ro.fr33styler.frconomy.account;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

public class Account {

    private final UUID uuid;
    private final String name;
    private double balance = 0.00;
    private String[] confirmation = null;

    public Account(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public Account(OfflinePlayer player) {
        name = player.getName();
        uuid = player.getUniqueId();
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String[] getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String[] confirmation) {
        this.confirmation = confirmation;
    }

}
