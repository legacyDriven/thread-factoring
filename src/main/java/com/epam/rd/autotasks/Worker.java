package com.epam.rd.autotasks;

import java.util.List;

public class Worker extends Thread{

    private final Runnable passedRun;

    private String workerName;

    private final List<FinishedThreadResult> resultRepository;

    public Worker(Runnable runnable, List<FinishedThreadResult> resultRepository) {
        this.passedRun = runnable;
        this.resultRepository = resultRepository;
    }

    @Override
    public void run() {
        Throwable argument = null;
        try{
            passedRun.run();
        } catch (Throwable e){ argument = e;
    } finally {
            resultRepository.add(new FinishedThreadResult(workerName, argument));
        }
    }
}
