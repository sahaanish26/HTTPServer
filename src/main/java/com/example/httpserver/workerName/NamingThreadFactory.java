package com.example.httpserver.workerName;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class NamingThreadFactory  implements ThreadFactory {



    private ThreadFactory threadFactory;
    private String nameSuffix;

    public NamingThreadFactory (ThreadFactory threadFactory, String nameSuffix) {
        this.threadFactory = threadFactory!=null ? threadFactory : Executors.defaultThreadFactory();
        this.nameSuffix = nameSuffix;
    }

    @Override public Thread newThread(Runnable task) {
        // default "pool-1-thread-1" to "pool-1-thread-1-myapp-MagicTask"
        Thread thread=threadFactory.newThread(task);
        thread.setName(nameSuffix+"-"+thread.getName());
        return thread;
    }
}
