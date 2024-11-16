package xyz.srnyx.lifeswap.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.command.AnnoyingCommand;
import xyz.srnyx.annoyingapi.command.AnnoyingSender;
import xyz.srnyx.annoyingapi.message.AnnoyingMessage;
import xyz.srnyx.annoyingapi.utility.BukkitUtility;

import xyz.srnyx.lifeswap.LifeSwap;
import xyz.srnyx.lifeswap.Swap;

import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;


public class StartCommand extends AnnoyingCommand {
    @NotNull private final LifeSwap plugin;

    public StartCommand(@NotNull LifeSwap plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public LifeSwap getAnnoyingPlugin() {
        return plugin;
    }

    @Override @NotNull
    public String getName() {
        return "lifeswapstart";
    }

    @Override @NotNull
    public String getPermission() {
        return "lifeswap.start";
    }

    @Override @NotNull
    public Predicate<String[]> getArgsPredicate() {
        return args -> args.length != 0;
    }

    @Override
    public void onCommand(@NotNull AnnoyingSender sender) {
        // Get players
        final Player player1;
        final Player player2;
        if (sender.args.length == 2) {
            player1 = sender.getArgument(0, Bukkit::getPlayer);
            if (player1 == null) return;
            player2 = sender.getArgument(1, Bukkit::getPlayer);
        } else {
            if (!sender.checkPlayer()) return;
            player1 = sender.getPlayer();
            player2 = sender.getArgument(0, Bukkit::getPlayer);
        }
        if (player2 == null) return;

        // Already swapping
        final UUID player1Uuid = player1.getUniqueId();
        final UUID player2Uuid = player2.getUniqueId();
        if (plugin.swaps.stream().anyMatch(swap -> swap.contains(player1Uuid) || swap.contains(player2Uuid))) {
            new AnnoyingMessage(plugin, "command.already-swapping").send(sender);
            return;
        }

        // Create swap
        final Swap swap = new Swap(plugin, player1, player2);
        plugin.swaps.add(swap);

        // Reset health
        player1.setMaxHealth(20);
        player2.setMaxHealth(20);

        // Start game
        swap.startSwap();
    }

    @Override @NotNull
    public Set<String> onTabComplete(@NotNull AnnoyingSender sender) {
        final Set<String> players = BukkitUtility.getOnlinePlayerNames();
        sender.getPlayerOptional().ifPresent(value -> players.remove(value.getName()));
        return players;
    }
}
