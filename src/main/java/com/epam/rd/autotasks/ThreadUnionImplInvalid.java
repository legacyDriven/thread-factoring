package com.epam.rd.autotasks;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadUnionImplInvalid implements ThreadUnion {

    boolean isShutdown;

    private AtomicBoolean shutdownInitiated;

    private final AtomicInteger workercount;

    private final String unionName;

    private final AtomicBoolean shutdownRequested = new AtomicBoolean(false);

    private final Set <Thread> instanceThreads;

    ThreadUnionImplInvalid(String unionName) {
        this.shutdownInitiated = new AtomicBoolean(false);
        this.unionName = unionName;
        instanceThreads = new HashSet<>();
        workercount = new AtomicInteger(0);
    }

    @Override
    public int totalSize() {
        return instanceThreads.size();
    }

    @Override
    public synchronized int activeSize() {
        return Math.toIntExact(instanceThreads.stream().filter(Thread::isAlive).count());
    }

    @Override
    public void shutdown() {
        shutdownRequested.compareAndSet(false, true);
    }

    @Override
    public boolean isShutdown() {
        shutdownRequested.compareAndSet(false, true);
        return shutdownRequested.get();
    }

    @Override
    public void awaitTermination() {

    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public List<FinishedThreadResult> results() {
        return null;
    }

    //Creates and registers a new Thread. Name of the thread should be "<thread-union-name>-worker-n",
    // where n is a number of a thread. A ThreadUnion must monitor execution of a created thread - refer to results() method.
    @Override
    public synchronized Thread newThread(Runnable r) {
        Thread created = new Thread(r);
        created.setName(unionName + "-worker-" + workercount.getAndIncrement());
        instanceThreads.add(created);
        //created.start();
        return created;
    }

    public static ThreadUnionImplInvalid newInstance(String name){
        return new ThreadUnionImplInvalid(name);
    }
}
