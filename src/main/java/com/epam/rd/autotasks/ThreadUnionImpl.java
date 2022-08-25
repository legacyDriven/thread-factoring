package com.epam.rd.autotasks;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

class ThreadUnionImpl implements ThreadUnion {

    private final String threadUnionName;

    final List<Worker> unionWorkers;

    private volatile boolean isShutdownInitiated;

    private final Lock lock = new ReentrantLock();

    private final List<FinishedThreadResult> finishedThreadResults;

    CountDownLatch countDownLatch;

    ThreadUnionImpl(String name) {
        this.threadUnionName = name;
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

    //works, probably should try implementation with countdownlatch
    @Override
    public void awaitTermination() {
        isShutdownInitiated = true;
        try{
            lock.lock();
            while(!areWorkersDead()){
               sleep(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    //TODO implement with countdownlatch syncronization
//    public void awaitTermination(){
//        isShutdownInitiated = true;
//        countDownLatch = new CountDownLatch(this.activeSize());
//    }

    @Override
    public boolean isFinished() {
        return isShutdownInitiated && areWorkersDead();
    }

    private boolean areWorkersDead(){
        boolean result = true;
        for (Worker t : unionWorkers){
            if (t.isAlive()) {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public List<FinishedThreadResult> results() {
        return finishedThreadResults;
    }

    @Override
    public synchronized Thread newThread(Runnable r) {
        if(isShutdownInitiated) throw new IllegalStateException();
        Worker worker = new Worker(r, finishedThreadResults);
        worker.setName(threadUnionName + "-worker-" + unionWorkers.size());
        unionWorkers.add(worker);
        return worker;
    }
}
