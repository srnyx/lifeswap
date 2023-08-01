package xyz.srnyx.lifeswap;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.message.AnnoyingMessage;
import xyz.srnyx.annoyingapi.message.BroadcastType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class SwapManager {
    @NotNull private final LifeSwap plugin;
    /**
     * player, target
     */
    @NotNull public final Map<UUID, UUID> swap = new HashMap<>();

    /**
     * Use {@link LifeSwap#swapManager} instead
     *
     * @param   plugin  the plugin
     */
    public SwapManager(@NotNull LifeSwap plugin) {
        this.plugin = plugin;
    }

    /**
     * Teleports {@code player} to {@code target}'s location and vice versa
     *
     * @param   player  the player to teleport
     * @param   target  the target to teleport
     */
    public void swapPlayers(@NotNull Player player, @NotNull Player target) {
        // Teleport
        final Location playerLocation = player.getLocation();
        player.teleport(target.getLocation());
        target.teleport(playerLocation);

        // Messages
        final AnnoyingMessage involved = new AnnoyingMessage(plugin, "swap.message.involved");
        final AnnoyingMessage others = new AnnoyingMessage(plugin, "swap.message.others");
        Bukkit.getOnlinePlayers().forEach(online -> {
            if (online == player || online == target) {
                involved.send(online);
                return;
            }
            others.send(online);
        });

        // Titles
        final String title = new AnnoyingMessage(plugin, "swap.title").toString();
        player.sendTitle(title, "");
        target.sendTitle(title, "");

        // Add to swap list
        final UUID playerUuid = player.getUniqueId();
        final UUID targetUuid = target.getUniqueId();
        swap.put(playerUuid, targetUuid);
        swap.put(targetUuid, playerUuid);

        // 30 second death check
        new BukkitRunnable() {
            public void run() {
                if (swap.containsKey(playerUuid)) modifyHealth(player, 2);
                if (swap.containsKey(targetUuid)) modifyHealth(target, 2);
            }
        }.runTaskLater(plugin, 600);
    }

    /**
     * Changes the max health of {@code player} by {@code amount}
     *
     * @param   player  the player to modify
     * @param   amount  the amount to modify
     */
    public void modifyHealth(@NotNull Player player, double amount) {
        removeSwap(player);

        // Set max health
        final double oldHealth = player.getMaxHealth();
        final double newHealth = oldHealth + amount;
        setHealth(player, newHealth);

        // Send message
        ChatColor color1 = ChatColor.GREEN;
        ChatColor color2 = ChatColor.DARK_GREEN;
        if (oldHealth > newHealth) {
            color1 = ChatColor.RED;
            color2 = ChatColor.DARK_RED;
        }
        new AnnoyingMessage(plugin, "health")
                .replace("%color1%", color1)
                .replace("%color2%", color2)
                .replace("%health%", newHealth)
                .send(player);
    }

    /**
     * Sets the max health of {@code player} to {@code value}
     *
     * @param   player  the player to modify
     * @param   value   the value to set
     */
    public void setHealth(@NotNull Player player, double value) {
        // Death
        if (player.getMaxHealth() - value <= 0) {
            final String ban = new AnnoyingMessage(plugin, "death.ban").toString();
            Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), ban, null, null);
            player.kickPlayer(ban);
            new AnnoyingMessage(plugin, "death.broadcast")
                    .replace("%player%", player.getName())
                    .broadcast(BroadcastType.CHAT);
            removeSwap(player);
            return;
        }

        player.setMaxHealth(value);
    }

    public void removeSwap(@NotNull Player player) {
        final UUID target = swap.remove(player.getUniqueId());
        if (target != null) swap.remove(target);
    }

    /**
     * Unbans all players and sets their max health to 20
     */
    public void reset() {
        swap.clear();
        // Unban all players
        final BanList banList = Bukkit.getBanList(BanList.Type.NAME);
        banList.getBanEntries().forEach(ban -> banList.pardon(ban.getTarget()));
        // Set all players' max health to 20
        Bukkit.getOnlinePlayers().forEach(player -> setHealth(player, 20));
    }
}
