package ro.fr33styler.frconomy.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ro.fr33styler.frconomy.FrConomy;
import ro.fr33styler.frconomy.account.Account;
import ro.fr33styler.frconomy.util.FrCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Money implements FrCommand {

    private final FrConomy plugin;

    public Money(FrConomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        if (args.length == 0) {
            if (sender.hasPermission("frconomy.money")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    Account account = plugin.getAccounts().getAccount(player);
                    String money = plugin.getMessages().getMoney();
                    money = money.replace("%money%", plugin.getFormatter().formatCurrency(account.getBalance()));
                    player.sendMessage(money);
                    return true;
                } else {
                    sender.sendMessage("§cInvalid arguments! Please use /money <name>");
                }
            } else {
                sender.sendMessage(plugin.getMessages().getPermission());
            }
        } else if (args.length == 1) {
            if (sender.hasPermission("frconomy.money.name")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                if (plugin.getAccounts().hasAccount(target)) {
                    Account account = plugin.getAccounts().getAccount(target);
                    String moneyPlayer = plugin.getMessages().getMoneyPlayer();
                    moneyPlayer = moneyPlayer.replace("%money%", plugin.getFormatter().formatCurrency(account.getBalance()));
                    moneyPlayer = moneyPlayer.replace("%name%", target.getName());
                    sender.sendMessage(moneyPlayer);
                    return true;
                } else {
                    sender.sendMessage("§cThe account was not found!");
                }
            } else {
                sender.sendMessage(plugin.getMessages().getPermission());
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