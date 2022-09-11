package xyz.srnyx.lifeswap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.srnyx.lifeswap.commands.ResetCommand;
import xyz.srnyx.lifeswap.commands.SwapCommand;


public class Main extends JavaPlugin {
    public static Main plugin;

    /**
     * Everything done when the plugin is enabling
     */
    @Override
    public void onEnable() {
        plugin = this;

        // Start messages
        final StringBuilder authors = new StringBuilder();
        for (final String author : getDescription().getAuthors()) {
            if (author.equalsIgnoreCase(getDescription().getAuthors().get(0))) {
                authors.append(author);
                continue;
            }
            authors.append(", ").append(author);
        }
        getLogger().info(ChatColor.GOLD + "---------------------");
        getLogger().info(ChatColor.YELLOW + "  " + getName() + " v" + getDescription().getVersion());
        getLogger().info(ChatColor.YELLOW + "  Created by " + authors);
        getLogger().info(ChatColor.GOLD + "---------------------");

        // Commands and listeners
        command("reset", new ResetCommand());
        command("swap", new SwapCommand());
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }

    /**
     * Registers a command
     *
     * @param   name        the name of the command
     * @param   executor    the executor of the command
     */
    private void command(String name, CommandExecutor executor) {
        final PluginCommand command = Bukkit.getPluginCommand(name);
        if (command != null) command.setExecutor(executor);
    }
}