package com.example.httpserver.workers;

import com.example.httpserver.application.FileApplication;
import com.example.httpserver.workerName.NamingThreadFactory;
import com.example.httpserver.workerName.RejectedExecutionHandlerImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpServerDelegate implements Runnable{


    private final static Logger logger = Logger.getLogger(HttpServerDelegate.class.getName());
    private int port;
    private String webroot;
    private ServerSocket serverSocket;
    //creating a blocking implementation with task count of 200
    private final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(200);
    private RejectedExecutionHandlerImpl rejectionHandler = new RejectedExecutionHandlerImpl();
    protected ExecutorService threadPool = new ThreadPoolExecutor(8, 8,
        0L, TimeUnit.MILLISECONDS,
                                             queue,new NamingThreadFactory(null, "RequestHandlerPool"),rejectionHandler);
    //protected  ExecutorService threadPool = Executors.newFixedThreadPool(8, new NamingThreadFactory(null, "RequestHandlerPool") );

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
