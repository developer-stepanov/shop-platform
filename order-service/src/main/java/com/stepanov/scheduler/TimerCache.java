package com.stepanov.scheduler;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TimerCache {

    private final Map<UUID, Entry> map = new ConcurrentHashMap<>();

    public void put(UUID orderId, Instant payUntil) {
        map.put(orderId, new Entry(payUntil));
    }

    public void remove(UUID orderId) {
        map.remove(orderId);
    }

    public Iterable<Map.Entry<UUID, Entry>> entries() {
        return map.entrySet();
    }

    public record Entry(Instant payUntil) {}
}
