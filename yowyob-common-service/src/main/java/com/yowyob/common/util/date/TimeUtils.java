package com.yowyob.common.util.date;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Utilitaires pour la manipulation du temps et des durées
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Permet de mesurer des durées avec haute précision
 * et de formater des durées de manière lisible
 */
@Slf4j
public final class TimeUtils {

    private TimeUtils() {
        throw new UnsupportedOperationException("Cette classe utilitaire ne peut pas être instanciée");
    }

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static long currentTimeNanos() {
        return System.nanoTime();
    }

    public static String formatDuration(Duration duration) {
        if (duration == null) {
            return "0ms";
        }

        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);

        if (absSeconds < 1) {
            return duration.toMillis() + "ms";
        } else if (absSeconds < 60) {
            return seconds + "s";
        } else if (absSeconds < 3600) {
            long minutes = absSeconds / 60;
            long remainingSeconds = absSeconds % 60;
            return String.format("%dmin %ds", minutes, remainingSeconds);
        } else {
            long hours = absSeconds / 3600;
            long remainingMinutes = (absSeconds % 3600) / 60;
            return String.format("%dh %dmin", hours, remainingMinutes);
        }
    }

    public static String formatMillis(long millis) {
        return formatDuration(Duration.ofMillis(millis));
    }

    public static long toMillis(long value, TimeUnit unit) {
        if (unit == null) {
            return value;
        }
        return unit.toMillis(value);
    }

    public static long toSeconds(long value, TimeUnit unit) {
        if (unit == null) {
            return value;
        }
        return unit.toSeconds(value);
    }

    public static Duration between(long startNanos, long endNanos) {
        return Duration.ofNanos(endNanos - startNanos);
    }

    public static long measureExecutionTime(Runnable task) {
        long start = currentTimeNanos();
        task.run();
        long end = currentTimeNanos();
        return TimeUnit.NANOSECONDS.toMillis(end - start);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interruption durant le sleep", e);
        }
    }
}