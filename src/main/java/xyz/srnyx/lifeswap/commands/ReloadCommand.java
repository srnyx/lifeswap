package xyz.srnyx.lifeswap.commands;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.command.AnnoyingCommand;
import xyz.srnyx.annoyingapi.command.AnnoyingSender;
import xyz.srnyx.annoyingapi.message.AnnoyingMessage;

import xyz.srnyx.lifeswap.LifeSwap;


public class ReloadCommand extends AnnoyingCommand {
    @NotNull private final LifeSwap plugin;

    public ReloadCommand(@NotNull LifeSwap plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public LifeSwap getAnnoyingPlugin() {
        return plugin;
    }

    @Override @NotNull
    public String getName() {
        return "lifeswapreload";
    }

    @Override @NotNull
    public String getPermission() {
        return "lifeswap.reload";
    }

    @Override
    public void onCommand(@NotNull AnnoyingSender sender) {
        plugin.reloadPlugin();
        new AnnoyingMessage(plugin, "command.reload").send(sender);
    }
}
