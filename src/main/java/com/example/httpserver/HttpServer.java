package com.example.httpserver;

import com.example.httpserver.workers.HttpServerDelegate;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class HttpServer {

    private static final int port = 8080;
    private static final String webRoot = "web/";
    private final static Logger logger = Logger.getLogger(HttpServer.class.getName());

    protected static ExecutorService threadPoolOne =
            Executors.newSingleThreadExecutor();

    private static  boolean running = true;

    public static void main(String[] args){
        logger.info("server starting on port "+port);

        HttpServerDelegate delegate = null;
        try {
            delegate = new HttpServerDelegate(port,webRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }

        threadPoolOne.execute(delegate);
        //ToDo enhance later take command from console.
        /*while(running) {
            Console console = System.console();
            String command = console.readLine("server: ");
            logger.info("command" + command);
            if(command.equalsIgnoreCase("stop")){
                running=false;
               // delegate.stop();
            }
        }*/
    }
}
