package xyz.srnyx.lifeswap.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.AnnoyingCooldown;
import xyz.srnyx.annoyingapi.command.AnnoyingCommand;
import xyz.srnyx.annoyingapi.command.AnnoyingSender;
import xyz.srnyx.annoyingapi.message.AnnoyingMessage;
import xyz.srnyx.annoyingapi.message.DefaultReplaceType;

import xyz.srnyx.lifeswap.LifeSwap;
import xyz.srnyx.lifeswap.SwapManager;

import java.util.function.Predicate;


public class SwapCommand implements AnnoyingCommand {
    @NotNull private static final AnnoyingCooldown.CooldownType COOLDOWN = () -> 1800000L;

    @NotNull private final LifeSwap plugin;

    public SwapCommand(@NotNull LifeSwap plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public LifeSwap getAnnoyingPlugin() {
        return plugin;
    }

    @Override @NotNull
    public String getPermission() {
        return "lifeswap.swap";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override @NotNull
    public Predicate<String[]> getArgsPredicate() {
        return args -> args.length == 1;
    }

    @Override
    public void onCommand(@NotNull AnnoyingSender sender) {
        // Get target
        final String[] args = sender.args;
        final Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.invalidArgument(args[0]);
            return;
        }
        final Player player = sender.getPlayer();

        // Player/target already swapping
        if (plugin.swapManager.swap.containsKey(player.getUniqueId()) || plugin.swapManager.swap.containsKey(target.getUniqueId())) {
            new AnnoyingMessage(plugin, "swap.command.already-in").send(sender);
            return;
        }

        // Player cooldown
        final AnnoyingCooldown playerCooldown = new AnnoyingCooldown(plugin, player.getUniqueId(), COOLDOWN);
        if (playerCooldown.isOnCooldown()) {
            new AnnoyingMessage(plugin, "swap.command.cooldown")
                    .replace("%cooldown%", playerCooldown.getRemaining(), DefaultReplaceType.TIME)
                    .send(sender);
            return;
        }

        // Target cooldown
        final AnnoyingCooldown targetCooldown = new AnnoyingCooldown(plugin, target.getUniqueId(), COOLDOWN);
        if (targetCooldown.isOnCooldown()) {
            new AnnoyingMessage(plugin, "swap.command.cooldown")
                    .replace("%cooldown%", targetCooldown.getRemaining(), DefaultReplaceType.TIME)
                    .send(sender);
            return;
        }

        // Messages
        new AnnoyingMessage(plugin, "swap.command.success")
                .replace("%player%", target.getName())
                .send(sender);
        new AnnoyingMessage(plugin, "swap.command.success")
                .replace("%player%", player.getName())
                .send(target);

        // Start cooldowns
        playerCooldown.start();
        targetCooldown.start();

        // Swap
        new BukkitRunnable() {
            public void run() {
                new SwapManager(plugin).swapPlayers(player, target);
            }
        }.runTaskLater(plugin, 100);
    }
}
