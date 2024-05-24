package xyz.srnyx.lifeswap;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.file.AnnoyingResource;

import java.util.Random;


public class LifeConfig {
    @NotNull private static final Random RANDOM = new Random();

    @NotNull private final AnnoyingResource config;

    @NotNull public final Interval interval;

    public LifeConfig(@NotNull AnnoyingPlugin plugin) {
        this.config = new AnnoyingResource(plugin, "config.yml");
        this.interval = new Interval();
    }

    public class Interval {
        private final int min = config.getInt("interval.min", 10) * 20;
        private final int max = config.getInt("interval.max", 30) * 20;

        public Interval() {
            if (min > max) throw new IllegalArgumentException("Minimum interval cannot be greater than maximum interval!");
        }

        public long getInterval() {
            return min + RANDOM.nextInt(max - min);
        }
    }
}
