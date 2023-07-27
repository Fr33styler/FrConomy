package ro.fr33styler.frconomy.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import ro.fr33styler.frconomy.FrConomy;
import ro.fr33styler.frconomy.account.Account;
import ro.fr33styler.frconomy.util.FrCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Top implements FrCommand {

    private final FrConomy plugin;

    public Top(FrConomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        int[] page = { 0 };
        if (!sender.hasPermission("frconomy.top")) {
            sender.sendMessage(plugin.getMessages().getPermission());
            return true;
        }
        if (args.length == 1) {
            if (!sender.hasPermission("frconomy.top.pages")) {
                sender.sendMessage(plugin.getMessages().getPermission());
                return true;
            }
            try {
                page[0] = Integer.parseInt(args[0]) - 1;
                if (page[0] < 0) {
                    sender.sendMessage(plugin.getMessages().getPositive());
                    return true;
                }
                page[0] *= 10;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (args.length > 1) {
            sender.sendMessage("§cInvalid arguments! Please use /baltop <page>");
            return false;
        }
        plugin.getSQLDatabase().getTopAsync(page[0], (congregatedBalances, list) -> {
            if (list.size() == 0) {
                sender.sendMessage(plugin.getMessages().getTopNotFound());
                return;
            }
            sender.sendMessage(plugin.getMessages().getTop());
            sender.sendMessage(" ");
            for (Account account : list) {
                page[0]++;
                String topRank = plugin.getMessages().getTopRank();
                topRank = topRank.replace("%money%", plugin.getFormatter().formatCurrency(account.getBalance()));
                topRank = topRank.replace("%name%", account.getName());
                topRank = topRank.replace("%rank%", String.valueOf(page[0]));
                sender.sendMessage(topRank);
            }
            if (page[0] <= 10) {
                sender.sendMessage(" ");
                String congregatedMoney = plugin.getMessages().getTopCongregatedMoney();
                congregatedMoney = congregatedMoney.replace("%congregated_money%", plugin.getFormatter().formatCurrency(congregatedBalances));
                sender.sendMessage(congregatedMoney);
            }
        });
        return true;
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

}