package ro.fr33styler.frconomy.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ro.fr33styler.frconomy.FrConomy;
import ro.fr33styler.frconomy.account.Account;
import ro.fr33styler.frconomy.util.EconomyUtil;
import ro.fr33styler.frconomy.util.FrCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Pay implements FrCommand {

    private final FrConomy plugin;

    public Pay(FrConomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        if (!sender.hasPermission("frconomy.pay")) {
            sender.sendMessage(plugin.getMessages().getPermission());
        } else if (sender == Bukkit.getConsoleSender()) {
            sender.sendMessage("Can be used only by players!");
        } else if (args.length != 2) {
            sender.sendMessage("§cInvalid arguments! /pay <name> <amount>");
        } else {
            Player player = (Player) sender;
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (player.getUniqueId().equals(target.getUniqueId())) {
                sender.sendMessage(plugin.getMessages().getMoneyYourself());
            } else {
                try {
                    double amount = EconomyUtil.fromShortScaleNotation(args[1]);
                    if (amount < 0) {
                        sender.sendMessage(plugin.getMessages().getPositive());
                    } else if (!plugin.getAccounts().hasAccount(target)) {
                        sender.sendMessage(plugin.getMessages().getPayAccount());
                    } else {
                        Account playerAccount = plugin.getAccounts().getAccount(player);
                        if (plugin.getSettings().hasPayRequireConfirmation() &&
                                !Arrays.deepEquals(playerAccount.getConfirmation(), args)) {

                            playerAccount.setConfirmation(args);
                            String confirmationRequired = plugin.getMessages().getConfirmationRequired();
                            confirmationRequired = confirmationRequired.replace("%money%", plugin.getFormatter().formatCurrency(amount));
                            confirmationRequired = confirmationRequired.replace("%name%", target.getName());
                            sender.sendMessage(confirmationRequired);
                        } else if (playerAccount.getBalance() < amount) {
                            sender.sendMessage(plugin.getMessages().getNotEnough());
                        } else {
                            playerAccount.setConfirmation(null);
                            Account targetAccount = plugin.getAccounts().getAccount(target);
                            playerAccount.setBalance(playerAccount.getBalance() - amount);
                            targetAccount.setBalance(targetAccount.getBalance() + amount);
                            String sentTo = plugin.getMessages().getSentTo();
                            sentTo = sentTo.replace("%money%", plugin.getFormatter().formatCurrency(amount));
                            sentTo = sentTo.replace("%name%", target.getName());
                            sender.sendMessage(sentTo);
                            if (target.isOnline()) {
                                String receivedFrom = plugin.getMessages().getReceivedFrom();
                                receivedFrom = receivedFrom.replace("%money%", plugin.getFormatter().formatCurrency(amount));
                                receivedFrom = receivedFrom.replace("%name%", player.getName());
                                ((Player) target).sendMessage(receivedFrom);
                            }
                            return true;
                        }
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.getMessages().getPayNotNumber());
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("frconomy.pay")) {
            List<String> suggestions = new ArrayList<>();
            if (args.length == 1) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().startsWith(args[0])) {
                        suggestions.add(player.getName());
                    }
                }
            } else if (args.length == 2) {
                suggestions.add("<amount>");
            }
            return suggestions;
        }
        return Collections.emptyList();
    }
}
