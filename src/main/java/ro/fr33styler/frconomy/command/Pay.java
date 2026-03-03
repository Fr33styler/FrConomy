package ro.fr33styler.frconomy.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import ro.fr33styler.frconomy.FrConomy;
import ro.fr33styler.frconomy.account.Account;
import ro.fr33styler.frconomy.util.ShortScaleUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Pay implements CommandExecutor, TabCompleter {

    private final FrConomy plugin;

    public Pay(FrConomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String name, String[] args) {
        if (!commandSender.hasPermission("frconomy.pay")) {
            commandSender.sendMessage(plugin.getMessages().getPermission());
        } else if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Can be used only by players!");
        } else if (args.length != 2) {
            commandSender.sendMessage("§cInvalid arguments! /pay <name> <amount>");
        } else {
            Player sender = (Player) commandSender;
            OfflinePlayer receiver = Bukkit.getOfflinePlayer(args[0]);
            if (sender.getUniqueId().equals(receiver.getUniqueId())) {
                commandSender.sendMessage(plugin.getMessages().getMoneyYourself());
                return false;
            }

            double amount;
            
            try {
                amount = ShortScaleUtil.fromShortScaleNotation(args[1]);
            } catch (NumberFormatException ignored) {
                commandSender.sendMessage(plugin.getMessages().getPayNotNumber());
                return false;
            }

            if (amount <= 0) {
                commandSender.sendMessage(plugin.getMessages().getPositive());
                return false;
            }

            Account senderAccount = plugin.getAccounts().getCachedAccount(sender);
            if (senderAccount == null || !senderAccount.isLoaded()) {
                commandSender.sendMessage(plugin.getMessages().getAccountNotLoaded());
                return false;
            }

            if (senderAccount.getBalance() < amount) {
                commandSender.sendMessage(plugin.getMessages().getNotEnough());
                return false;
            }

            plugin.getAccounts().getAccount(receiver).thenAccept(receiverAccount -> {
                if (receiverAccount == null) {
                    commandSender.sendMessage(plugin.getMessages().getAccountNotValid());
                    return;
                }
                if (plugin.getSettings().hasPayRequireConfirmation() && !Arrays.deepEquals(receiverAccount.getConfirmation(), args)) {
                    receiverAccount.setConfirmation(args);
                    commandSender.sendMessage(plugin.getMessages().getConfirmationRequired()
                            .replace("%money%", plugin.getFormatter().formatCurrency(amount))
                            .replace("%name%", receiver.getName()));
                } else {
                    receiverAccount.setConfirmation(null);

                    Bukkit.getScheduler().runTask(plugin, () -> {
                        senderAccount.setBalance(senderAccount.getBalance() - amount);
                        receiverAccount.setBalance(receiverAccount.getBalance() + amount);
                        plugin.getSQLDatabase().updateAccount(senderAccount);
                        plugin.getSQLDatabase().updateAccount(receiverAccount);

                        commandSender.sendMessage(plugin.getMessages().getSentTo()
                                .replace("%money%", plugin.getFormatter().formatCurrency(amount))
                                .replace("%name%", receiver.getName()));
                        if (receiver.isOnline()) {
                            receiver.getPlayer().sendMessage(plugin.getMessages().getReceivedFrom()
                                    .replace("%money%", plugin.getFormatter().formatCurrency(amount))
                                    .replace("%name%", sender.getName()));
                        }
                    });
                }
            });
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
                suggestions.add(plugin.getMessages().getPayAmountArgument());
            }
            return suggestions;
        }
        return Collections.emptyList();
    }
}
