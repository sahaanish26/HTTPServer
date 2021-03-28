package com.example.httpserver;

import com.example.httpserver.workers.HttpServerDelegate;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class HttpServer {

 private static final int port = 8000;
    private final static Logger logger = Logger.getLogger(HttpServer.class.getName());

    protected static ExecutorService threadPoolOne =
            Executors.newSingleThreadExecutor();

    public static void main(String[] args){
        logger.info("server starting");

        HttpServerDelegate delegate = null;
        try {
            delegate = new HttpServerDelegate(port,"web/");
        } catch (IOException e) {
            e.printStackTrace();
        }
       // new Thread(delegate).start();
        threadPoolOne.execute(delegate);

    }
}
