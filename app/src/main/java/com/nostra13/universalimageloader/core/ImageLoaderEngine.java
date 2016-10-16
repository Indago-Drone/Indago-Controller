package com.nostra13.universalimageloader.core;

import android.widget.ImageView;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

class ImageLoaderEngine {
    private final Map<Integer, String> cacheKeysForImageViews;
    final ImageLoaderConfiguration configuration;
    private final AtomicBoolean networkDenied;
    private final AtomicBoolean paused;
    private final AtomicBoolean slowNetwork;
    private ExecutorService taskDistributor;
    private Executor taskExecutor;
    private Executor taskExecutorForCachedImages;
    private final Map<String, ReentrantLock> uriLocks;

    /* renamed from: com.nostra13.universalimageloader.core.ImageLoaderEngine.1 */
    class C01011 implements Runnable {
        final /* synthetic */ LoadAndDisplayImageTask val$task;

        C01011(LoadAndDisplayImageTask loadAndDisplayImageTask) {
            this.val$task = loadAndDisplayImageTask;
        }

        public void run() {
            boolean isImageCachedOnDisc = ImageLoaderEngine.this.configuration.discCache.get(this.val$task.getLoadingUri()).exists();
            ImageLoaderEngine.this.initExecutorsIfNeed();
            if (isImageCachedOnDisc) {
                ImageLoaderEngine.this.taskExecutorForCachedImages.execute(this.val$task);
            } else {
                ImageLoaderEngine.this.taskExecutor.execute(this.val$task);
            }
        }
    }

    ImageLoaderEngine(ImageLoaderConfiguration configuration) {
        this.cacheKeysForImageViews = Collections.synchronizedMap(new HashMap());
        this.uriLocks = new WeakHashMap();
        this.paused = new AtomicBoolean(false);
        this.networkDenied = new AtomicBoolean(false);
        this.slowNetwork = new AtomicBoolean(false);
        this.configuration = configuration;
        this.taskExecutor = configuration.taskExecutor;
        this.taskExecutorForCachedImages = configuration.taskExecutorForCachedImages;
        this.taskDistributor = Executors.newCachedThreadPool();
    }

    void submit(LoadAndDisplayImageTask task) {
        this.taskDistributor.execute(new C01011(task));
    }

    void submit(ProcessAndDisplayImageTask task) {
        initExecutorsIfNeed();
        this.taskExecutorForCachedImages.execute(task);
    }

    private void initExecutorsIfNeed() {
        if (this.taskExecutor == null) {
            this.taskExecutor = createTaskExecutor();
        }
        if (this.taskExecutorForCachedImages == null) {
            this.taskExecutorForCachedImages = createTaskExecutor();
        }
    }

    private Executor createTaskExecutor() {
        return DefaultConfigurationFactory.createExecutor(this.configuration.threadPoolSize, this.configuration.threadPriority, this.configuration.tasksProcessingType);
    }

    String getLoadingUriForView(ImageView imageView) {
        return (String) this.cacheKeysForImageViews.get(Integer.valueOf(imageView.hashCode()));
    }

    void prepareDisplayTaskFor(ImageView imageView, String memoryCacheKey) {
        this.cacheKeysForImageViews.put(Integer.valueOf(imageView.hashCode()), memoryCacheKey);
    }

    void cancelDisplayTaskFor(ImageView imageView) {
        this.cacheKeysForImageViews.remove(Integer.valueOf(imageView.hashCode()));
    }

    void denyNetworkDownloads(boolean denyNetworkDownloads) {
        this.networkDenied.set(denyNetworkDownloads);
    }

    void handleSlowNetwork(boolean handleSlowNetwork) {
        this.slowNetwork.set(handleSlowNetwork);
    }

    void pause() {
        this.paused.set(true);
    }

    void resume() {
        synchronized (this.paused) {
            this.paused.set(false);
            this.paused.notifyAll();
        }
    }

    void stop() {
        if (!this.configuration.customExecutor) {
            this.taskExecutor = null;
        }
        if (!this.configuration.customExecutorForCachedImages) {
            this.taskExecutorForCachedImages = null;
        }
        this.cacheKeysForImageViews.clear();
        this.uriLocks.clear();
    }

    ReentrantLock getLockForUri(String uri) {
        ReentrantLock lock = (ReentrantLock) this.uriLocks.get(uri);
        if (lock != null) {
            return lock;
        }
        lock = new ReentrantLock();
        this.uriLocks.put(uri, lock);
        return lock;
    }

    AtomicBoolean getPause() {
        return this.paused;
    }

    boolean isNetworkDenied() {
        return this.networkDenied.get();
    }

    boolean isSlowNetwork() {
        return this.slowNetwork.get();
    }
}
