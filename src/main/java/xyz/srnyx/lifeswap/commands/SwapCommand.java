package xyz.srnyx.lifeswap.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.command.AnnoyingCommand;
import xyz.srnyx.annoyingapi.command.AnnoyingSender;
import xyz.srnyx.annoyingapi.cooldown.AnnoyingCooldown;
import xyz.srnyx.annoyingapi.cooldown.CooldownType;
import xyz.srnyx.annoyingapi.message.AnnoyingMessage;
import xyz.srnyx.annoyingapi.message.DefaultReplaceType;

import xyz.srnyx.lifeswap.LifeSwap;

import java.util.UUID;
import java.util.function.Predicate;


public class SwapCommand extends AnnoyingCommand {
    @NotNull private static final CooldownType COOLDOWN = () -> 1800000L;

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
        final UUID playerUuid = player.getUniqueId();
        final UUID targetUuid = target.getUniqueId();

        // Player/target already swapping
        if (plugin.swap.containsKey(playerUuid) || plugin.swap.containsKey(targetUuid)) {
            new AnnoyingMessage(plugin, "swap.command.already-in").send(sender);
            return;
        }

        // Player cooldown
        final AnnoyingCooldown playerCooldown = new AnnoyingCooldown(plugin, playerUuid.toString(), COOLDOWN);
        if (playerCooldown.isOnCooldown()) {
            new AnnoyingMessage(plugin, "swap.command.cooldown")
                    .replace("%cooldown%", playerCooldown.getRemaining(), DefaultReplaceType.TIME)
                    .send(sender);
            return;
        }

        // Target cooldown
        final AnnoyingCooldown targetCooldown = new AnnoyingCooldown(plugin, targetUuid.toString(), COOLDOWN);
        if (targetCooldown.isOnCooldown()) {
            new AnnoyingMessage(plugin, "swap.command.cooldown")
                    .replace("%cooldown%", targetCooldown.getRemaining(), DefaultReplaceType.TIME)
                    .send(sender);
            return;
        }

        // Start cooldowns
        playerCooldown.start();
        targetCooldown.start();

        // Messages
        new AnnoyingMessage(plugin, "swap.command.success")
                .replace("%player%", target.getName())
                .send(sender);
        new AnnoyingMessage(plugin, "swap.command.success")
                .replace("%player%", player.getName())
                .send(target);

        // Swap
        new BukkitRunnable() {
            public void run() {
                plugin.swapPlayers(player, target);
            }
        }.runTaskLater(plugin, plugin.config.interval.getInterval());
    }
}
