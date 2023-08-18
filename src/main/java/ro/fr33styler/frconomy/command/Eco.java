package ro.fr33styler.frconomy.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import ro.fr33styler.frconomy.FrConomy;
import ro.fr33styler.frconomy.account.Account;
import ro.fr33styler.frconomy.util.ShortScaleUtil;
import ro.fr33styler.frconomy.util.FrCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Eco implements FrCommand {

    private final FrConomy plugin;

    public Eco(FrConomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        if (sender.hasPermission("frconomy.eco")) {
            if (args.length == 0) {
                sender.sendMessage("§2[§ffrConomy§2] Made by Fr33styler.");
                sender.sendMessage("§e - /eco give <name> <amount>");
                sender.sendMessage("§e - /eco set <name> <amount>");
                sender.sendMessage("§e - /eco take <name> <amount>");
                return true;
            } else if (args.length == 3) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                try {
                    double amount = ShortScaleUtil.fromShortScaleNotation(args[2]);
                    if (args[0].equals("give")) {
                        if (plugin.getAccounts().hasAccount(target)) {
                            Account account = plugin.getAccounts().getAccount(target);
                            account.setBalance(account.getBalance() + amount);
                            plugin.getSQLDatabase().updateAccount(account);
                            sender.sendMessage("§2You gave §f" + plugin.getFormatter().formatCurrency(amount) + " §2to §f" + target.getName());
                        } else {
                            sender.sendMessage("§cThe account doesn't exists!");
                        }
                        return true;
                    } else if (args[0].equals("set")) {
                        if (plugin.getAccounts().hasAccount(target)) {
                            Account account = plugin.getAccounts().getAccount(target);
                            account.setBalance(amount);
                            plugin.getSQLDatabase().updateAccount(account);
                            sender.sendMessage("§2You set §f" + target.getName() + " §2to §f"  + plugin.getFormatter().formatCurrency(amount));
                        } else {
                            sender.sendMessage("§cThe account doesn't exists!");
                        }
                        return true;
                    } else if (args[0].equals("take")) {
                        if (plugin.getAccounts().hasAccount(target)) {
                            Account account = plugin.getAccounts().getAccount(target);
                            if (account.getBalance() < amount) {
                                sender.sendMessage("§cNot enough funds!");
                            } else {
                                account.setBalance(account.getBalance() - amount);
                                plugin.getSQLDatabase().updateAccount(account);
                                sender.sendMessage("§2You took §f" + plugin.getFormatter().formatCurrency(amount) + " §2from §f" + target.getName());
                            }
                        } else {
                            sender.sendMessage("§cThe account doesn't exists!");
                        }
                        return true;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cThe amount must be a number!");
                }
            }
            sender.sendMessage("§cInvalid arguments! Type /eco for available commands!");
        } else {
            sender.sendMessage(plugin.getMessages().getPermission());
        }
        return false;
    }

    private final String[] subCommands = { "give", "set", "take" };

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("frconomy.eco")) {
            List<String> suggestions = new ArrayList<>();
            if (args.length == 1) {
                for (String subCommand : subCommands) {
                    if (subCommand.startsWith(args[0])) {
                        suggestions.add(subCommand);
                    }
                }
            } else if (args.length == 2) {
                for (String subCommand : subCommands) {
                    if (subCommand.equalsIgnoreCase(args[0])) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.getName().startsWith(args[1])) {
                                suggestions.add(player.getName());
                            }
                        }
                        break;
                    }
                }
            } else if (args.length == 3) {
                for (String subCommand : subCommands) {
                    if (subCommand.equalsIgnoreCase(args[0])) {
                        suggestions.add("<amount>");
                        break;
                    }
                }
            }
            return suggestions;
        }
        return Collections.emptyList();
    }

}