package ro.fr33styler.frconomy.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import ro.fr33styler.frconomy.FrConomy;
import ro.fr33styler.frconomy.account.Account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Money implements CommandExecutor, TabCompleter {

    private final FrConomy plugin;

    public Money(FrConomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        if (args.length == 0) {
            if (sender.hasPermission("frconomy.money")) {
                sender.sendMessage(plugin.getMessages().getPermission());
            } else if (!(sender instanceof Player)) {
                sender.sendMessage("§cInvalid arguments! Please use /money <name>");
            } else {
                Account account = plugin.getAccounts().getCachedAccount((Player) sender);
                if (account == null || !account.isLoaded()) {
                    sender.sendMessage(plugin.getMessages().getAccountNotLoaded());
                } else {
                    sender.sendMessage(plugin.getMessages().getMoney().replace("%money%",
                            plugin.getFormatter().formatCurrency(account.getBalance())));
                }
                return true;
            }
        } else if (args.length == 1) {
            if (!sender.hasPermission("frconomy.money.name")) {
                sender.sendMessage(plugin.getMessages().getPermission());
            } else {
                plugin.getAccounts().getAccount(Bukkit.getOfflinePlayer(args[0])).thenAccept(account -> {
                    if (account == null) {
                        sender.sendMessage("§cThe account was not found!");
                    } else {
                        sender.sendMessage(plugin.getMessages().getMoneyPlayer()
                                .replace("%money%", plugin.getFormatter().formatCurrency(account.getBalance()))
                                .replace("%name%", account.getName()));
                    }
                });
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && sender.hasPermission("frconomy.money.name")) {
            List<String> suggestions = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().startsWith(args[0])) {
                    suggestions.add(player.getName());
                }
            }
            return suggestions;
        }
        return Collections.emptyList();
    }
}