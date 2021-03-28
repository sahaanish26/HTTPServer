package com.example.httpserver.workerName;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class NamingThreadFactory  implements ThreadFactory {



    private ThreadFactory tf;
    private String nameSuffix;

    public NamingThreadFactory (ThreadFactory tf, String nameSuffix) {
        this.tf = tf!=null ? tf : Executors.defaultThreadFactory();
        this.nameSuffix = nameSuffix;
    }

    @Override public Thread newThread(Runnable task) {
        // default "pool-1-thread-1" to "pool-1-thread-1-myapp-MagicTask"
        Thread thread=tf.newThread(task);
        thread.setName(nameSuffix+"-"+thread.getName());
        return thread;
    }
}
