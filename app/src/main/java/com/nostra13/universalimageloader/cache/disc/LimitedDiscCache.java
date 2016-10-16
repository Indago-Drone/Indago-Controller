package com.nostra13.universalimageloader.cache.disc;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class LimitedDiscCache extends BaseDiscCache {
    private final AtomicInteger cacheSize;
    private final Map<File, Long> lastUsageDates;
    private final int sizeLimit;

    /* renamed from: com.nostra13.universalimageloader.cache.disc.LimitedDiscCache.1 */
    class C00981 implements Runnable {
        C00981() {
        }

        public void run() {
            int size = 0;
            File[] cachedFiles = LimitedDiscCache.this.cacheDir.listFiles();
            if (cachedFiles != null) {
                for (File cachedFile : cachedFiles) {
                    size += LimitedDiscCache.this.getSize(cachedFile);
                    LimitedDiscCache.this.lastUsageDates.put(cachedFile, Long.valueOf(cachedFile.lastModified()));
                }
                LimitedDiscCache.this.cacheSize.set(size);
            }
        }
    }

    protected abstract int getSize(File file);

    public LimitedDiscCache(File cacheDir, int sizeLimit) {
        this(cacheDir, DefaultConfigurationFactory.createFileNameGenerator(), sizeLimit);
    }

    public LimitedDiscCache(File cacheDir, FileNameGenerator fileNameGenerator, int sizeLimit) {
        super(cacheDir, fileNameGenerator);
        this.lastUsageDates = Collections.synchronizedMap(new HashMap());
        this.sizeLimit = sizeLimit;
        this.cacheSize = new AtomicInteger();
        calculateCacheSizeAndFillUsageMap();
    }

    private void calculateCacheSizeAndFillUsageMap() {
        new Thread(new C00981()).start();
    }

    public void put(String key, File file) {
        int valueSize = getSize(file);
        int curCacheSize = this.cacheSize.get();
        while (curCacheSize + valueSize > this.sizeLimit) {
            int freedSize = removeNext();
            if (freedSize == 0) {
                break;
            }
            curCacheSize = this.cacheSize.addAndGet(-freedSize);
        }
        this.cacheSize.addAndGet(valueSize);
        Long currentTime = Long.valueOf(System.currentTimeMillis());
        file.setLastModified(currentTime.longValue());
        this.lastUsageDates.put(file, currentTime);
    }

    public File get(String key) {
        File file = super.get(key);
        Long currentTime = Long.valueOf(System.currentTimeMillis());
        file.setLastModified(currentTime.longValue());
        this.lastUsageDates.put(file, currentTime);
        return file;
    }

    public void clear() {
        this.lastUsageDates.clear();
        this.cacheSize.set(0);
        super.clear();
    }

    private int removeNext() {
        if (this.lastUsageDates.isEmpty()) {
            return 0;
        }
        Long oldestUsage = null;
        File mostLongUsedFile = null;
        Set<Entry<File, Long>> entries = this.lastUsageDates.entrySet();
        synchronized (this.lastUsageDates) {
            for (Entry<File, Long> entry : entries) {
                if (mostLongUsedFile == null) {
                    mostLongUsedFile = (File) entry.getKey();
                    oldestUsage = (Long) entry.getValue();
                } else {
                    Long lastValueUsage = (Long) entry.getValue();
                    if (lastValueUsage.longValue() < oldestUsage.longValue()) {
                        oldestUsage = lastValueUsage;
                        mostLongUsedFile = (File) entry.getKey();
                    }
                }
            }
        }
        int fileSize = getSize(mostLongUsedFile);
        if (!mostLongUsedFile.delete()) {
            return fileSize;
        }
        this.lastUsageDates.remove(mostLongUsedFile);
        return fileSize;
    }
}
