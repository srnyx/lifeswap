package xyz.srnyx.lifeswap.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.lifeswap.managers.SwapManager;

import java.util.Collections;
import java.util.List;


public class ResetCommand implements TabExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        new SwapManager().reset();
        sender.sendMessage(ChatColor.GREEN + "Reset the game!");
        return true;
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
