package com.example.httpserver.http;

import com.example.httpserver.workers.RequestHandler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HTTP Response class used in communication with the client.
 * This class contains the data that will be sent to the client,
 * including status line, headers, and response body.
 */
public class RawHttpRequest extends HttpResponse {

    private final static Logger logger = Logger.getLogger(RawHttpRequest.class.getName());


    private String content;

    public RawHttpRequest(int statusCode, String content) {
        super();

        this.statusCode = statusCode;
        this.content = content;
    }

    /**
     * This function writes the HTTP response to an output stream.
     *
     * @param out the target {@link OutputStream} for writing
     */
    public void write(OutputStream out) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(getResponseLine());
            writer.write("\r\n");

            for (String key: headers.keySet()) {
                writer.write(key + ":" + headers.get(key));
                writer.write("\r\n");
            }
            writer.write("\r\n");

            writer.write(content);

            writer.flush();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

}

