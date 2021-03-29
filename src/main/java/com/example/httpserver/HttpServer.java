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

    private static  boolean running = true;

    public static void main(String[] args){
        logger.info("server starting");

        HttpServerDelegate delegate = null;
        try {
            delegate = new HttpServerDelegate(port,"web/");
        } catch (IOException e) {
            e.printStackTrace();
        }

        threadPoolOne.execute(delegate);
        while(running) {
            Console console = System.console();
            String command = console.readLine("server: ");
            System.out.println("command" + command);
            if(command.equalsIgnoreCase("stop")){
                running=false;
               // delegate.stop();
            }
        }
    }
}
