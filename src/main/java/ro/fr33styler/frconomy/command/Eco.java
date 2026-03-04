package ro.fr33styler.frconomy.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ro.fr33styler.frconomy.FrConomy;
import ro.fr33styler.frconomy.util.ShortScaleUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Eco implements CommandExecutor, TabCompleter {

    private final String[] SUB_COMMANDS = { "give", "set", "take" };

    private final FrConomy plugin;

    public Eco(FrConomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        if (sender.hasPermission("frconomy.eco")) {
            if (args.length == 3) {
                double amount;

                try {
                    amount = ShortScaleUtil.fromShortScaleNotation(args[2]);
                } catch (NumberFormatException ignored) {
                    sender.sendMessage("§cThe amount must be a number!");
                    return false;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                performOperation(sender, args[0], target, amount);
            } else {
                sender.sendMessage("§2[§ffrConomy§2] Made by Fr33styler.");
                sender.sendMessage("§e - /eco give <name> <amount>");
                sender.sendMessage("§e - /eco set <name> <amount>");
                sender.sendMessage("§e - /eco take <name> <amount>");
            }
            return true;
        } else {
            sender.sendMessage(plugin.getMessages().getPermission());
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("frconomy.eco") && args.length > 0) {
            List<String> suggestions = new ArrayList<>();
            if (args.length == 1) {
                for (String subCommand : SUB_COMMANDS) {
                    if (subCommand.startsWith(args[0])) {
                        suggestions.add(subCommand);
                    }
                }
            } else if (isSubCommand(args[0]) && args.length <= 3) {
                if (args.length == 2) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player != null) {
                        suggestions.add(player.getName());
                    }
                } else {
                    suggestions.add("<amount>");
                }
            }
            return suggestions;
        }
        return Collections.emptyList();
    }

    private boolean isSubCommand(String argument) {
        for (String subCommand : SUB_COMMANDS) {
            if (subCommand.equalsIgnoreCase(argument)) {
                return true;
            }
        }
        return false;
    }

    private void performOperation(CommandSender sender, String argument, OfflinePlayer target, double amount) {
        plugin.getAccounts().loadAccount(target).thenAccept(account -> {
            if (account == null) {
                sender.sendMessage("§cThe account doesn't exists!");
                return;
            }
            if (!account.isLoaded()) {
                sender.sendMessage(plugin.getMessages().getAccountNotLoaded());
                return;
            }
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (argument.equalsIgnoreCase("give")) {
                    account.setBalance(account.getBalance() + amount);
                    sender.sendMessage("§2You gave §f" + plugin.getFormatter().formatCurrency(amount) + " §2to §f" + target.getName());
                } else if (argument.equalsIgnoreCase("take")) {
                    account.setBalance(account.getBalance() - amount);
                    sender.sendMessage("§2You took §f" + plugin.getFormatter().formatCurrency(amount) + " §2from §f" + target.getName());
                } else if (argument.equalsIgnoreCase("set")) {
                    account.setBalance(amount);
                    sender.sendMessage("§2You set §f" + target.getName() + " §2to §f" + plugin.getFormatter().formatCurrency(amount));
                }
                plugin.getSQLDatabase().updateAccount(account);
            });
        });
    }

}