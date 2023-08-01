package xyz.srnyx.lifeswap.commands;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.command.AnnoyingCommand;
import xyz.srnyx.annoyingapi.command.AnnoyingSender;
import xyz.srnyx.annoyingapi.message.AnnoyingMessage;

import xyz.srnyx.lifeswap.LifeSwap;
import xyz.srnyx.lifeswap.SwapManager;


public class ResetCommand implements AnnoyingCommand {
    @NotNull private final LifeSwap plugin;

    public ResetCommand(@NotNull LifeSwap plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public LifeSwap getAnnoyingPlugin() {
        return plugin;
    }

    @Override @NotNull
    public String getPermission() {
        return "lifeswap.reset";
    }

    @Override
    public void onCommand(@NotNull AnnoyingSender sender) {
        new SwapManager(plugin).reset();
        new AnnoyingMessage(plugin, "reset").send(sender);
    }
}
