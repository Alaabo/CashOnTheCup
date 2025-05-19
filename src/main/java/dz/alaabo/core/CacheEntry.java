package dz.alaabo.core;

import dz.alaabo.encoding.CompressedString;

import java.io.Serializable;

public class CacheEntry implements Serializable {
    private final CompressedString value;
    private final long expiryTime;

    public CacheEntry(CompressedString value, long ttlMillis) {
        this.value = value;
        this.expiryTime = System.currentTimeMillis() + ttlMillis;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }

    public CompressedString getValue() {
        return value;
    }
}
