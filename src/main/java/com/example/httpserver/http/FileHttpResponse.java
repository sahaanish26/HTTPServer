package com.example.httpserver.http;

//import com.example.httpserver.Logger;

import com.example.httpserver.helper.Utility;
import com.example.httpserver.workers.HttpServerDelegate;



import java.io.*;
import java.nio.file.Files;

import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HttpResponse extensions that sends a file to the client (e. g. html)
 */
public class FileHttpResponse extends HttpResponse {


    /**
     * File to be sent to the user.
     */
    private File inputFile;
    /**
     * path to the File to be sent to the user.
     */
    private String uri;

    private final static Logger logger = Logger.getLogger(FileHttpResponse.class.getName());

    public FileHttpResponse(int statusCode, File inputFile, String uri) {
        super();

        this.statusCode = statusCode;
        this.inputFile = inputFile;
        this.uri=uri;

        try {

            this.setEtag();
            this.setLastModified();
            this.setCacheControl();
            this.setContentLength();
            this.setContentType();
            this.setAcceptRanges();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
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

            if (inputFile != null) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
                char[] buffer = new char[2048];
                int read;
                int count=0;
                while ( (read = reader.read(buffer)) != -1 ) {
                    writer.write(buffer, 0, read);
                    count=count+read;

                }

                reader.close();


            }

            writer.flush();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }



    }







    /**
     * This function determines the content type.
     *  Content Type is derived based on file extension.Could not use Files.probeContentType since it does not support
     *   all OS types(does not work in mac OS) https://bugs.java.com/bugdatabase/view_bug.do?bug_id=8129632.So either
     *   will upgrade to jdk9 or use https://github.com/j256/simplemagic
     */

    private void setContentType() throws IOException {
            String ext = uri.substring(this.uri.indexOf(".") + 1);
            String type = ContentType.contentTypes.get(ext.toUpperCase());

        if (type != null) {
            headers.put("Content-Type",type);
        }else{
            headers.put("Content-Type","application/octet-stream");
        }

    }

    private void setContentLength() throws IOException {
        System.out.println("insode setContentLength");
        this.getHeaders().put("Content-Length", String.valueOf(this.inputFile.length()));
    }


    private void setEtag() throws IOException {
        headers.put("Etag", Utility.checksum(this.inputFile));
    }


    private void setLastModified() throws IOException {
        FileTime fileTime = Files.getLastModifiedTime(Paths.get(String.valueOf(this.inputFile.getPath())));
        Date dt = new Date(fileTime.toMillis());
        headers.put("Last-Modified", Utility.formatToRFC1123(dt));
    }


    private void setCacheControl() throws IOException {
        StringBuilder sbCache = new StringBuilder();
        sbCache.append("public");
        sbCache.append(';');
        //keeping it hardcoded for now , logic can be built later on
        int maxAge = 90;
        sbCache.append("max-age="+String.valueOf(maxAge));
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();

        cal.add(Calendar.SECOND, maxAge);
        Date later = cal.getTime();


        headers.put("Cache-Control", sbCache.toString());
        headers.put("Date",Utility.formatToRFC1123(now));
        headers.put("Expires",Utility.formatToRFC1123(later));
    }




    private void setAcceptRanges()  {

        headers.put("Accept-Ranges","bytes");

    }



    @Override
    public String toString() {
        return "FileHttpResponse{" +
                "protocol='" + protocol + '\'' +
                ", statusCode=" + statusCode +
                ", headers=" + headers +
                '}';
    }
}

