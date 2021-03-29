package com.example.httpserver.workers;

import com.example.httpserver.application.WebApplication;
import com.example.httpserver.exceptions.BadRequestException;
import com.example.httpserver.exceptions.ConnectionClosedException;
import com.example.httpserver.http.HttpRequest;
import com.example.httpserver.http.HttpResponse;
import com.example.httpserver.http.RawHttpRequest;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{

    private WebApplication app;
    private Socket connection;
    private InputStream in;
    private OutputStream out;



    private final static Logger logger = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket socket, WebApplication app) {
        this.connection = socket;
        this.app = app;
    }


    @Override
    public void run() {
        try {
            logger.info("Ruuning in  thread: " + Thread.currentThread().getName());

            in = connection.getInputStream();
            out = connection.getOutputStream();

            HttpRequest request = HttpRequest.parse(in);

            if (request != null) {
                logger.info(request.getRequestLine() +
                        " from "
                        + connection.getInetAddress()
                        + ":"
                        + connection.getPort());

                HttpResponse response = app.handle(request);

                response.getHeaders().put("Server", "HTTP Server"); // TODO Add Constant class
               // response.getHeaders().put("Date", Calendar.getInstance().getTime().toString());
                response.getHeaders().put("Connection", "close"); // TODO Add keep-alive connections?

                response.write(out);
                System.out.println("response"+response);
            } else {
                logger.log(Level.WARNING,"Server accepts only HTTP protocol.");
                new RawHttpRequest(501, "Server only accepts HTTP protocol").write(out);
            }
        } catch (BadRequestException e) {
            logger.log(Level.SEVERE, "Error occur in RequestHandler.", e);

            new RawHttpRequest(400, "Server only accepts HTTP protocol").write(out);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error in client's IO.", e);

        }  catch (ConnectionClosedException e) {
            logger.log(Level.SEVERE, "Connection closed by client.", e);


        } finally {
            try {
                in.close();
            } catch (IOException e) {
                logger.info("Error while closing input stream.");
            }
            try {
                out.close();
            } catch (IOException e) {

                logger.log(Level.SEVERE, "Error while closing output stream.", e);

            }
            try {
                connection.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error while closing client socket.", e);

            }
        }
    }
}
