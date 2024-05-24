package xyz.srnyx.lifeswap.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.AnnoyingListener;

import xyz.srnyx.lifeswap.LifeSwap;

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
        if (plugin.swap.containsKey(uuid) || plugin.swap.containsValue(uuid)) plugin.modifyHealth(player, -2);
    }
}
