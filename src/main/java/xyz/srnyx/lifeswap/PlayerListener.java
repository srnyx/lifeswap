package xyz.srnyx.lifeswap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import org.bukkit.event.player.PlayerJoinEvent;


public class PlayerListener implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if (SwapManager.swap.contains(player)) new SwapManager().modifyHealth(player, -2);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (SwapManager.reset.contains(player)) {
            new SwapManager().setHealth(player, 20);
            SwapManager.reset.remove(player);
        }
    }
}
