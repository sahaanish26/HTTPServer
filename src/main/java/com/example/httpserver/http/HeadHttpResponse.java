package com.example.httpserver.http;

//import com.example.httpserver.;

import java.io.*;

/**
 * HttpResponse extension that only writes headers.
 */
public class HeadHttpResponse extends FileHttpResponse {


    /**
     * File to be sent to the user.
     */
   // private File inputFile;

    public HeadHttpResponse(int statusCode, File inputFile, String uri) {
        super(statusCode, inputFile, uri);
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

            writer.flush();
        } catch (IOException e) {
            //Logger.error(TAG, e.getMessage());
        }
    }
}

