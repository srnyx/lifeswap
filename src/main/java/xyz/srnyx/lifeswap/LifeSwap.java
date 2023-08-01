package xyz.srnyx.lifeswap;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.PluginPlatform;


public class LifeSwap extends AnnoyingPlugin {
    @NotNull public final SwapManager swapManager = new SwapManager(this);

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
}