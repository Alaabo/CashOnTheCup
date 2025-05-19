package dz.alaabo.core;

import dz.alaabo.encoding.CompressedString;
import dz.alaabo.encoding.DictionaryManager;

import java.io.IOException;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;

public class Cache {
    private final ConcurrentMap<String, CacheEntry> store;
    private final DictionaryManager dict;
    private final int maxSize;
    private final Deque<String> accessOrder;
    private final String dictPersistencePath = "dict.ser";

    public Cache(int maxSize) {
        this.store = new ConcurrentHashMap<>();
        this.accessOrder = new ConcurrentLinkedDeque<>();
        this.maxSize = maxSize;
        this.dict = loadOrCreateDict();
    }

    private DictionaryManager loadOrCreateDict() {
        try {
            return DictionaryManager.loadFromFile(dictPersistencePath);
        } catch (IOException | ClassNotFoundException e) {
            return new DictionaryManager();
        }
    }

    public void put(String key, String value, long ttlMillis) {
        byte[] encoded = dict.encodeString(value);
        CompressedString cs = new CompressedString(encoded);
        CacheEntry entry = new CacheEntry(cs, ttlMillis);

        store.put(key, entry);
        updateAccessOrder(key);
        evictIfNeeded();
        persistDictionary();
    }

    public String get(String key) {
        CacheEntry entry = store.get(key);
        if (entry == null || entry.isExpired()) {
            store.remove(key);
            accessOrder.remove(key);
            return null;
        }
        updateAccessOrder(key);
        return dict.decodeString(entry.getValue().getEncoded());
    }

    public void remove(String key) {
        store.remove(key);
        accessOrder.remove(key);
    }

    private synchronized void updateAccessOrder(String key) {
        accessOrder.remove(key);
        accessOrder.addLast(key);
    }

    private synchronized void evictIfNeeded() {
        while (store.size() > maxSize) {
            String eldestKey = accessOrder.pollFirst();
            if (eldestKey != null) {
                store.remove(eldestKey);
            }
        }
    }

    private void persistDictionary() {
        try {
            dict.saveToFile(dictPersistencePath);
        } catch (IOException e) {
            System.err.println("[WARN] Failed to save dictionary: " + e.getMessage());
        }
    }
}
