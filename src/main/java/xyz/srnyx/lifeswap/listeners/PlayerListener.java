package xyz.srnyx.lifeswap.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.AnnoyingListener;

import xyz.srnyx.lifeswap.LifeSwap;
import xyz.srnyx.lifeswap.SwapManager;

import java.util.UUID;


public class PlayerListener extends AnnoyingListener {
    @NotNull private final LifeSwap plugin;

    public PlayerListener(@NotNull LifeSwap plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public LifeSwap getAnnoyingPlugin() {
        return plugin;
    }

    @EventHandler
    public void onDeath(@NotNull PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final UUID uuid = player.getUniqueId();
        if (plugin.swapManager.swap.containsKey(uuid) || plugin.swapManager.swap.containsValue(uuid)) new SwapManager(plugin).modifyHealth(player, -2);
    }
}
