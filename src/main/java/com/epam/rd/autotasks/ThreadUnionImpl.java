package com.epam.rd.autotasks;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ThreadUnionImpl implements ThreadUnion {

    private final String threadUnionName;

    private final AtomicInteger workerNumber;

    private final List<Thread> unionWorkers;

    private volatile boolean isShutdownInitiated;

    private final Lock lock = new ReentrantLock();

    ThreadUnionImpl(String name) {
        this.threadUnionName = name;
        this.workerNumber = new AtomicInteger(0);
        this.unionWorkers = new CopyOnWriteArrayList<>();
        this.isShutdownInitiated = false;
        }

    @Override
    public int totalSize() {
        return unionWorkers.size();
    }

    @Override
    public int activeSize() {
        return Math.toIntExact(unionWorkers.stream().filter(Thread::isAlive).count());
    }

    private void initiateShutdown(){
        try{
            lock.lock();
            isShutdownInitiated = false;
            for(Thread t : unionWorkers){
                t.interrupt();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void shutdown() {
        initiateShutdown();
    }

    @Override
    public boolean isShutdown() {
        return false;
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

    @Override
    public Thread newThread(Runnable r) {
        return null;
    }
}
