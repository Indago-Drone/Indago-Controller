package com.nostra13.universalimageloader.cache.memory.impl;

import android.graphics.Bitmap;
import com.nostra13.universalimageloader.cache.memory.LimitedMemoryCache;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LRULimitedMemoryCache extends LimitedMemoryCache<String, Bitmap> {
    private static final int INITIAL_CAPACITY = 10;
    private static final float LOAD_FACTOR = 1.1f;
    private final Map<String, Bitmap> lruCache;

    public LRULimitedMemoryCache(int maxSize) {
        super(maxSize);
        this.lruCache = Collections.synchronizedMap(new LinkedHashMap(INITIAL_CAPACITY, LOAD_FACTOR, true));
    }

    public boolean put(String key, Bitmap value) {
        if (!super.put(key, value)) {
            return false;
        }
        this.lruCache.put(key, value);
        return true;
    }

    public Bitmap get(String key) {
        this.lruCache.get(key);
        return (Bitmap) super.get(key);
    }

    public void remove(String key) {
        this.lruCache.remove(key);
        super.remove(key);
    }

    public void clear() {
        this.lruCache.clear();
        super.clear();
    }

    protected int getSize(Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    protected Bitmap removeNext() {
        Bitmap mostLongUsedValue = null;
        synchronized (this.lruCache) {
            Iterator<Entry<String, Bitmap>> it = this.lruCache.entrySet().iterator();
            if (it.hasNext()) {
                mostLongUsedValue = (Bitmap) ((Entry) it.next()).getValue();
                it.remove();
            }
        }
        return mostLongUsedValue;
    }

    protected Reference<Bitmap> createReference(Bitmap value) {
        return new WeakReference(value);
    }
}
