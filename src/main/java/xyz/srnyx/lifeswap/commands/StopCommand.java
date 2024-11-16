package xyz.srnyx.lifeswap.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.command.AnnoyingCommand;
import xyz.srnyx.annoyingapi.command.AnnoyingSender;
import xyz.srnyx.annoyingapi.message.AnnoyingMessage;

import xyz.srnyx.lifeswap.LifeSwap;
import xyz.srnyx.lifeswap.Swap;

import java.util.HashSet;
import java.util.Set;


public class StopCommand extends AnnoyingCommand {
    @NotNull private final LifeSwap plugin;

    public StopCommand(@NotNull LifeSwap plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public LifeSwap getAnnoyingPlugin() {
        return plugin;
    }

    @Override @NotNull
    public String getName() {
        return "lifeswapstop";
    }

    @Override @NotNull
    public String getPermission() {
        return "lifeswap.stop";
    }

    @Override
    public void onCommand(@NotNull AnnoyingSender sender) {
        // Stop all swaps
        if (sender.args.length == 0) {
            for (final Swap swap : new HashSet<>(plugin.swaps)) swap.stop();
            new AnnoyingMessage(plugin, "command.stop.all").send(sender);
            return;
        }

        // Get & stop swap
        final Player player = sender.getArgumentOptional(0, Bukkit::getPlayer).orElse(null);
        if (player == null) return;
        final Swap swap = plugin.getSwap(player).orElse(null);
        if (swap == null) {
            sender.invalidArgumentByIndex(0);
            return;
        }
        swap.stop();

        // Send message
        final Player player2 = swap.getOther(player);
        new AnnoyingMessage(plugin, "command.stop.one")
                .replace("%player1%", player.getName())
                .replace("%player2%", player2 != null ? player2.getName() : "N/A")
                .send(sender);
    }

    @NotNull
    public Set<String> onTabComplete(@NotNull AnnoyingSender sender) {
        final Set<String> completions = new HashSet<>();
        for (final Swap swap : plugin.swaps) {
            final Player player1 = swap.getPlayer1();
            final Player player2 = swap.getPlayer2();
            if (player1 != null) completions.add(player1.getName());
            if (player2 != null) completions.add(player2.getName());
        }
        return completions;
    }
}
