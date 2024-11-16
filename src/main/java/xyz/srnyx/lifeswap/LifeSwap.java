package xyz.srnyx.lifeswap;

import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.PluginPlatform;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


public class LifeSwap extends AnnoyingPlugin {
    @NotNull public LifeConfig config = new LifeConfig(this);
    @NotNull public final Set<Swap> swaps = new HashSet<>();

    public LifeSwap() {
        options
                .pluginOptions(pluginOptions -> pluginOptions.updatePlatforms(
                        PluginPlatform.modrinth("lifeswap"),
                        PluginPlatform.hangar(this, "srnyx"),
                        PluginPlatform.spigot("105208")))
                .bStatsOptions(bStatsOptions -> bStatsOptions.id(18873))
                .registrationOptions.automaticRegistration.packages(
                        "xyz.srnyx.lifeswap.commands",
                        "xyz.srnyx.lifeswap.listeners");
    }

    @Override
    public void reload() {
        config = new LifeConfig(this);
    }

    @NotNull
    public Optional<Swap> getSwap(@NotNull Player player) {
        final UUID uuid = player.getUniqueId();
        return swaps.stream().filter(swap -> swap.contains(uuid)).findFirst();
    }
}
