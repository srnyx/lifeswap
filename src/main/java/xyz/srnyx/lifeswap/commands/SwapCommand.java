package xyz.srnyx.lifeswap.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.lifeswap.Main;
import xyz.srnyx.lifeswap.SwapManager;

import java.util.concurrent.TimeUnit;


public class SwapCommand implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        // Not a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return true;
        }

        // Invalid arguments
        if (args.length != 1) return false;

        // Invalid player
        final Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.DARK_RED + args[0] + ChatColor.RED + " is not a valid player!");
            return true;
        }

        // Player already swapped
        if (SwapManager.swap.contains(player) || SwapManager.swap.contains(target)) {
            sender.sendMessage(ChatColor.RED + "You/target already swapped recently!");
            return true;
        }

        // On cooldown
        final Long cooldown = SwapManager.cooldowns.get(player.getUniqueId());
        if (cooldown != null && cooldown - System.currentTimeMillis() > 0) {
            final String message = ChatColor.translateAlternateColorCodes('&', "&cYou can't swap for another &4%time%&c minutes!");
            sender.sendMessage(message.replace("%time%", String.valueOf(TimeUnit.MILLISECONDS.toMinutes(cooldown - System.currentTimeMillis()))));
            return true;
        }

        // Messages
        final String message = ChatColor.translateAlternateColorCodes('&', "&4&l! &cSwapping with &4%player% &cin &45 &cseconds &4&l!");
        player.sendMessage(message.replace("%player%", target.getName()));
        target.sendMessage(message.replace("%player%", player.getName()));

        // Swap
        new BukkitRunnable() {
            public void run() {
                new SwapManager().swapPlayers(player, target);
            }
        }.runTaskLater(Main.plugin, 100);

        // Cooldowns
        SwapManager.cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30));
        SwapManager.cooldowns.put(target.getUniqueId(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30));

        return true;
    }
}
