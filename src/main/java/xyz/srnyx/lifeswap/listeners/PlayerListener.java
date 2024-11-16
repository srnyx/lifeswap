package xyz.srnyx.lifeswap.listeners;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.AnnoyingListener;
import xyz.srnyx.annoyingapi.message.AnnoyingMessage;
import xyz.srnyx.annoyingapi.message.BroadcastType;
import xyz.srnyx.annoyingapi.message.DefaultReplaceType;

import xyz.srnyx.lifeswap.LifeSwap;
import xyz.srnyx.lifeswap.Swap;


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
        // Get swap information
        final Player player = event.getEntity();
        final Swap swap = plugin.getSwap(player).orElse(null);
        if (swap == null) return;
        final Player other = swap.getOther(player);
        if (other == null) return;
        final String playerName = player.getName();
        final String otherName = other.getName();

        // End game if player's health will be <= 0
        if (player.getMaxHealth() - plugin.config.healthTransfer <= 0) {
            swap.stop();
            // Send message
            new AnnoyingMessage(plugin, "game.end")
                    .replace("%loser%", playerName)
                    .replace("%winner%", otherName)
                    .broadcast(BroadcastType.CHAT);
            // Run commands
            final ConsoleCommandSender console = Bukkit.getConsoleSender();
            for (final String command : plugin.config.getCommands(other, player)) Bukkit.dispatchCommand(console, command);
            return;
        }

        // Decrease loser health
        modifyHealth(player, -plugin.config.healthTransfer);
        // Increase winner health
        modifyHealth(other, plugin.config.healthTransfer);
        // Broadcast
        if (plugin.config.broadcast) {
            final String broadcast = new AnnoyingMessage(plugin, "health.broadcast")
                    .replace("%loser%", playerName)
                    .replace("%gainer%", otherName)
                    .replace("%health%", plugin.config.healthTransfer, DefaultReplaceType.NUMBER)
                    .toString();
            for (final Player uninvolved : swap.getUninvolved()) uninvolved.sendMessage(broadcast);
        }
    }

    private void modifyHealth(@NotNull Player player, double amount) {
        // Set health
        final double newHealth = player.getMaxHealth() + amount;
        player.setMaxHealth(newHealth);
        // Send message
        new AnnoyingMessage(plugin, "health." + (amount > 0 ? "gain" : "lose"))
                .replace("%health%", newHealth, DefaultReplaceType.NUMBER)
                .send(player);
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        plugin.getSwap(event.getPlayer()).ifPresent(Swap::startSwap);
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        plugin.getSwap(event.getPlayer()).ifPresent(Swap::cancelTask);
    }
}
