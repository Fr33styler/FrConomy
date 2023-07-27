package ro.fr33styler.frconomy.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ro.fr33styler.frconomy.FrConomy;

public class SessionEvents implements Listener {

    private final FrConomy plugin;

    public SessionEvents(FrConomy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getAccounts().add(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getAccounts().remove(event.getPlayer());
    }

}
