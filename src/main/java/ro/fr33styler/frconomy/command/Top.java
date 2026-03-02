package ro.fr33styler.frconomy.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.command.TabCompleter;
import ro.fr33styler.frconomy.FrConomy;
import ro.fr33styler.frconomy.account.Account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Top implements CommandExecutor, TabCompleter {

    private final FrConomy plugin;

    public Top(FrConomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        if (!sender.hasPermission("frconomy.top") || (args.length == 1 && !sender.hasPermission("frconomy.top.pages"))) {
            sender.sendMessage(plugin.getMessages().getPermission());
        } else if (args.length == 0) {
            sendTop(sender, 0);
            return true;
        } else if (args.length == 1) {
            try {
                int page = Integer.parseInt(args[0]) - 1;
                if (page < 0) {
                    sender.sendMessage(plugin.getMessages().getPositive());
                } else {
                    sendTop(sender, page * 10);
                    return true;
                }
            } catch (NumberFormatException exception) {
                sender.sendMessage(plugin.getMessages().getNotValid());
            }
        } else {
            sender.sendMessage("§cInvalid arguments! Please use /baltop <page>");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && sender.hasPermission("frconomy.top.pages")) {
            List<String> suggestions = new ArrayList<>();
            suggestions.add("<page>");
            return suggestions;
        }
        return Collections.emptyList();
    }

    private void sendTop(CommandSender sender, int index) {
        plugin.getSQLDatabase().getTop(index).thenAccept(topBalance -> {
            if (topBalance == null || topBalance.getTop().isEmpty()) {
                sender.sendMessage(plugin.getMessages().getTopNotFound());
                return;
            }

            int rank = index;
            sender.sendMessage(plugin.getMessages().getTop());
            sender.sendMessage(" ");
            for (Account account : topBalance.getTop()) {
                sender.sendMessage(plugin.getMessages().getTopRank()
                        .replace("%name%", account.getName())
                        .replace("%rank%", String.valueOf(++rank))
                        .replace("%money%", plugin.getFormatter().formatCurrency(account.getBalance())));
            }
            if (rank <= 10) {
                sender.sendMessage(" ");
                sender.sendMessage(plugin.getMessages().getTopAggregatedMoney()
                        .replace("%aggregated_money%", plugin.getFormatter().formatCurrency(topBalance.getTotalBalance())));
            }
        });
    }

}