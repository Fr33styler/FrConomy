package ro.fr33styler.frconomy.account;

import java.util.UUID;

public class Account {

    private final UUID uuid;
    private final String name;
    private double balance = 0.00;
    private boolean loaded = false;
    private int eliminationCountdown;
    private String[] confirmation = null;

    public Account(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getEliminationCountdown() {
        return eliminationCountdown;
    }

    public void setEliminationCountdown(int eliminationCountdown) {
        this.eliminationCountdown = eliminationCountdown;
    }

    public String[] getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String[] confirmation) {
        this.confirmation = confirmation;
    }

}
