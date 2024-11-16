package xyz.srnyx.lifeswap;

import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.file.AnnoyingResource;
import xyz.srnyx.annoyingapi.file.PlayableSound;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class LifeConfig {
    @NotNull private static final Random RANDOM = new Random();

    @NotNull private final AnnoyingResource config;

    @NotNull public final Delay delay;
    public final int healthTransfer;
    @NotNull public final Set<String> commands;
    @NotNull public final Countdown countdown;
    public final boolean broadcast;

    public LifeConfig(@NotNull AnnoyingPlugin plugin) {
        config = new AnnoyingResource(plugin, "config.yml");
        delay = new Delay();
        healthTransfer = config.getInt("health-transfer", 2);
        commands = new HashSet<>(config.getStringList("commands"));
        countdown = new Countdown();
        broadcast = config.getBoolean("broadcast", true);
    }

    public class Delay {
        /**
         * seconds
         */
        private final int min = config.getInt("delay.min", 10);
        /**
         * seconds
         */
        private final int max = config.getInt("delay.max", 30);

        public Delay() {
            if (min > max) throw new IllegalArgumentException("Minimum delay cannot be greater than maximum delay!");
        }

        /**
         * seconds
         */
        public int getRandomDelay() {
            return min + RANDOM.nextInt(max - min);
        }
    }

    @NotNull
    public Set<String> getCommands(@Nullable Player winner, @NotNull Player loser) {
        final Set<String> result = new HashSet<>();
        for (String command : commands) {
            if (winner != null) command = command.replace("%winner%", winner.getName());
            result.add(command.replace("%loser%", loser.getName()));
        }
        return result;
    }

    public class Countdown {
        public final int startAt = config.getInt("countdown.startAt", 5);
        @Nullable public final PlayableSound sound = config.getBoolean("countdown.sound.enabled", true) ? config.getPlayableSound("countdown.sound").orElse(null) : null;
    }
}
