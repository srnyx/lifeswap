package xyz.srnyx.lifeswap;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class SwapManager {
    public static final List<Player> swap = new ArrayList<>();
    public static final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    public static final List<Player> reset = new ArrayList<>();

    /**
     * Teleports {@code player} to {@code target}'s location and vice versa
     *
     * @param   player  the player to teleport
     * @param   target  the target to teleport
     */
    public void swapPlayers(Player player, Player target) {
        // Teleport
        final Location pLocation = player.getLocation();
        final Location tLocation = target.getLocation();
        player.teleport(tLocation);
        target.teleport(pLocation);

        // Messages
        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "SWAPPED!");
        target.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "SWAPPED!");
        for (final Player online : Bukkit.getOnlinePlayers()) if (online != player && online != target) {
            online.sendMessage(ChatColor.DARK_RED + player.getName() + ChatColor.RED + " swapped with " + ChatColor.DARK_RED + target.getName() + ChatColor.RED + "!");
        }

        // Titles
        player.sendTitle(ChatColor.RED + "SWAPPED!", "", 10, 30, 10);
        target.sendTitle(ChatColor.RED + "SWAPPED!", "", 10, 30, 10);

        // Add to swap list
        swap.add(player);
        swap.add(target);

        // 30 second death check
        new BukkitRunnable() {
            public void run() {
                if (swap.contains(player)) modifyHealth(player, 2);
                if (swap.contains(target)) modifyHealth(target, 2);
            }
        }.runTaskLater(Main.plugin, 600);
    }

    /**
     * Player loses all their max health
     *
     * @param   player  the loser
     */
    public void death(Player player) {
        Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), ChatColor.RED + "You died!", null, null);
        player.kickPlayer(ChatColor.RED + "You died!");
        Bukkit.broadcastMessage(ChatColor.DARK_RED + player.getName() + ChatColor.RED + " has died and been banned!");
        swap.clear();
    }

    /**
     * Changes the max health of {@code player} by {@code amount}
     *
     * @param   player  the player to modify
     * @param   amount  the amount to modify
     */
    public void modifyHealth(Player player, double amount) {
        swap.remove(player);

        // Set max health
        final AttributeInstance health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (health == null) return;
        final double oldHealth = health.getValue();
        health.setBaseValue(oldHealth + amount);
        final double newHealth = health.getValue();

        // Check if dead
        if (oldHealth + amount <= 0) {
            death(player);
            return;
        }

        // Send message
        ChatColor color1 = ChatColor.GREEN;
        ChatColor color2 = ChatColor.DARK_GREEN;
        if (oldHealth > newHealth) {
            color1 = ChatColor.RED;
            color2 = ChatColor.DARK_RED;
        }
        player.sendMessage(color1 + "Your max health is now " + color2 + new DecimalFormat("#").format(newHealth / 2) + color1 + "/10!");
    }

    /**
     * Sets the max health of {@code player} to {@code value}
     *
     * @param   player  the player to modify
     * @param   value   the value to set
     */
    public void setHealth(Player player, double value) {
        final AttributeInstance health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (health == null) return;
        health.setBaseValue(value);
        if (health.getValue() == 0) death(player);
    }

    /**
     * Unbans all players and sets their max health to 20
     */
    public void resetGame() {
        swap.clear();

        // Unban all players
        for (final BanEntry banned : Bukkit.getBanList(BanList.Type.NAME).getBanEntries()) Bukkit.getBanList(BanList.Type.NAME).pardon(banned.getTarget());

        // Set all players' max health to 20
        for (final Player online : Bukkit.getOnlinePlayers()) setHealth(online, 20);
    }
}
