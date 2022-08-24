package com.epam.rd.autotasks;

public class Worker extends Thread{

    private final Runnable passedRun;

    private String workerName;

    public Worker(Runnable runnable) {
        this.passedRun = runnable;
    }

    @Override
    public void run() {
        Throwable argument = null;
        try{
            passedRun.run();
        } catch (Throwable e){ argument = e;
    } finally {
         return new FinishedThreadResult(workerName, argument);
        }
    }


}
