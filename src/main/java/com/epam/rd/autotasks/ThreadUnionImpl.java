package com.epam.rd.autotasks;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ThreadUnionImpl implements ThreadUnion {

    private final String threadUnionName;

    private final AtomicInteger workerNumber;

    private final List<Thread> unionWorkers;

    private volatile boolean isShutdownInitiated;

    private final Lock lock = new ReentrantLock();

    private List<FinishedThreadResult> finishedThreadResults;

    ThreadUnionImpl(String name) {
        this.threadUnionName = name;
        this.workerNumber = new AtomicInteger(0);
        this.unionWorkers = new CopyOnWriteArrayList<>();
        this.isShutdownInitiated = false;
        this.finishedThreadResults = new CopyOnWriteArrayList<>();
        }

    @Override
    public int totalSize() {
        return unionWorkers.size();
    }

    @Override
    public int activeSize() {
        return Math.toIntExact(unionWorkers.stream().filter(Thread::isAlive).count());
    }

    private void blockNewThreadCreation(){
        isShutdownInitiated=true;
    }

    @Override
    public void shutdown() {
        blockNewThreadCreation();
        killAllThreads();
    }

    private void killAllThreads() {
        for(Thread t : unionWorkers){
            t.interrupt();
        }
    }

    @Override
    public boolean isShutdown() {
        return isShutdownInitiated;
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
        return finishedThreadResults;
    }

    @Override
    public synchronized Thread newThread(Runnable r) {
        Thread worker = new Worker(r);
        worker.setName(threadUnionName + "-worker-" + unionWorkers.size());
        unionWorkers.add(worker);
        return worker;
    }
}
