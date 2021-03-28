package com.example.httpserver.workers;

import com.example.httpserver.application.FileApplication;
import com.example.httpserver.workerName.NamingThreadFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpServerDelegate implements Runnable{


    private final static Logger logger = Logger.getLogger(HttpServerDelegate.class.getName());
    private int port;
    private String webroot;
    private ServerSocket serverSocket;

    protected  ExecutorService threadPool = Executors.newFixedThreadPool(10, new NamingThreadFactory(null, "RequestHandlerPool") );
    public HttpServerDelegate(int port, String webroot) throws IOException {
        this.port = port;
        this.webroot = webroot;
        this.serverSocket = new ServerSocket(this.port);
    }

    @Override
    public void run() {


        try {
            logger.info("running in : " + Thread.currentThread().getName());

            while (!Thread.currentThread().isInterrupted() && serverSocket.isBound() && !serverSocket.isClosed()) {
                logger.info("running in $ : " + Thread.currentThread().getName());

                Socket socket = serverSocket.accept();

                logger.info("Connection accepted: " + socket.getInetAddress());

                RequestHandler workerThread = new RequestHandler(socket,new FileApplication(this.webroot));
                this.threadPool.execute(workerThread);
                /*new Thread(
                        workerThread
                ).start();*/
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE,"Problem with setting socket.");
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
