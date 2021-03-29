package com.example.httpserver.workerName;


import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RejectedExecutionHandlerImpl implements RejectedExecutionHandler {
    private final static Logger logger = Logger.getLogger(RejectedExecutionHandlerImpl.class.getName());
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        logger.log(Level.WARNING,r.toString() + " is rejected"+ "executor task count"+executor.getTaskCount());
    }
}
