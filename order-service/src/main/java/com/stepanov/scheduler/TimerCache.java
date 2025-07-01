package com.stepanov.scheduler;

import lombok.NonNull;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The TimerCache class provides a fast, thread-safe way to track per-order payment deadlines in memory.
 * This enables the backend to support countdown timers in the UI,
 * letting users see exactly how much time they have left to pay for their order.
 */
public class TimerCache {

    private final Map<UUID, Entry> map = new ConcurrentHashMap<>();

    public void put(@NonNull UUID orderId, @NonNull Instant payUntil) {
        map.put(orderId, new Entry(payUntil));
    }

    public void remove(@NonNull UUID orderId) {
        map.remove(orderId);
    }

    public Iterable<Map.Entry<UUID, Entry>> entries() {
        return map.entrySet();
    }

    public record Entry(Instant payUntil) {}
}
