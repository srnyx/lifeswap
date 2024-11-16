package xyz.srnyx.lifeswap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.message.AnnoyingMessage;
import xyz.srnyx.annoyingapi.message.DefaultReplaceType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class Swap {
    @NotNull private final LifeSwap plugin;
    @NotNull private final UUID player1Uuid;
    @NotNull private final UUID player2Uuid;
    @Nullable public BukkitTask task;

    public Swap(@NotNull LifeSwap plugin, @NotNull Player player1, @NotNull Player player2) {
        this.plugin = plugin;
        this.player1Uuid = player1.getUniqueId();
        this.player2Uuid = player2.getUniqueId();
    }

    @Nullable
    public Player getPlayer1() {
        return Bukkit.getPlayer(player1Uuid);
    }

    @Nullable
    public Player getPlayer2() {
        return Bukkit.getPlayer(player2Uuid);
    }

    public boolean contains(@NotNull UUID player) {
        return player1Uuid.equals(player) || player2Uuid.equals(player);
    }

    @Nullable
    public Player getOther(@NotNull Player player) {
        return player.getUniqueId().equals(player1Uuid) ? getPlayer2() : getPlayer1();
    }

    @NotNull
    public Set<Player> getUninvolved() {
        final Set<Player> players = new HashSet<>(Bukkit.getOnlinePlayers());
        players.remove(getPlayer1());
        players.remove(getPlayer2());
        return players;
    }

    public void cancelTask() {
        if (task != null) task.cancel();
    }

    public void stop() {
        cancelTask();
        plugin.swaps.remove(this);

        // Reset health
        final Player player1 = getPlayer1();
        final Player player2 = getPlayer2();
        if (player1 != null) player1.setMaxHealth(20);
        if (player2 != null) player2.setMaxHealth(20);
    }

    public void startSwap() {
        cancelTask();

        // Get players
        final Player player1 = getPlayer1();
        final Player player2 = getPlayer2();
        if (player1 == null || player2 == null) return;
        final String player1Name = player1.getName();
        final String player2Name = player2.getName();
        final int delay = plugin.config.delay.getRandomDelay();

        // Messages
        final AnnoyingMessage message = new AnnoyingMessage(plugin, "game.start").replace("%time%", delay * 1000, DefaultReplaceType.TIME);
        new AnnoyingMessage(message)
                .replace("%player%", player2Name)
                .send(player1);
        new AnnoyingMessage(message)
                .replace("%player%", player1Name)
                .send(player2);

        // Start task
        task = new BukkitRunnable() {
            int time = delay;
            public void run() {
                // Decrease time
                time--;

                // Countdown
                if (time <= plugin.config.countdown.startAt) {
                    // Title
                    final String title = new AnnoyingMessage(plugin, "countdown")
                            .replace("%time%", time * 1000, DefaultReplaceType.TIME)
                            .toString();
                    player1.sendTitle(title, "");
                    player2.sendTitle(title, "");

                    // Sound
                    if (plugin.config.countdown.sound != null) {
                        plugin.config.countdown.sound.play(player1);
                        plugin.config.countdown.sound.play(player2);
                    }
                }
                if (time > 0) return;

                // Involved message
                final AnnoyingMessage involved = new AnnoyingMessage(plugin, "on-swap.message");
                involved.send(player1);
                involved.send(player2);

                // Broadcast
                final String broadcast = new AnnoyingMessage(plugin, "on-swap.broadcast")
                        .replace("%player1%", player1Name)
                        .replace("%player2%", player2Name)
                        .toString();
                for (final Player player : getUninvolved()) player.sendMessage(broadcast);

                // Title
                final String title = new AnnoyingMessage(plugin, "on-swap.title").toString();
                player1.sendTitle(title, "");
                player2.sendTitle(title, "");

                // Start next swap task
                startSwap();

                final Location location1a = player1.getLocation();
                final Location location2a = player2.getLocation();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        final Location location1b = player1.getLocation();
                        final Location location2b = player2.getLocation();

                        // Get velocities by comparing location after 1 tick
                        final Vector velocity1 = location1b.toVector().subtract(location1a.toVector());
                        final Vector velocity2 = location2b.toVector().subtract(location2a.toVector());

                        // Teleport
                        player1.teleport(location2b);
                        player2.teleport(location1b);

                        // Set velocities
                        player1.setVelocity(velocity2);
                        player2.setVelocity(velocity1);
                    }
                }.runTaskLater(plugin, 1);
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}
