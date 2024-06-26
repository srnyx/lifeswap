package xyz.srnyx.lifeswap.commands;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.command.AnnoyingCommand;
import xyz.srnyx.annoyingapi.command.AnnoyingSender;
import xyz.srnyx.annoyingapi.message.AnnoyingMessage;

import xyz.srnyx.lifeswap.LifeSwap;


public class ResetCommand extends AnnoyingCommand {
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
        plugin.reset();
        new AnnoyingMessage(plugin, "reset").send(sender);
    }
}
