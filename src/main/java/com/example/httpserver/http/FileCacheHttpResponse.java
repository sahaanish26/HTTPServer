package com.example.httpserver.http;

import com.example.httpserver.helper.Utility;

import java.io.*;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

//RFC 7232 §4.1 :
public class FileCacheHttpResponse  extends HttpResponse {
    private String etag;
    private Date lastModified;

    private final static Logger logger = Logger.getLogger(FileCacheHttpResponse.class.getName());

    public FileCacheHttpResponse(int statusCode, String etag) {
        super();

        this.statusCode = statusCode;
        this.etag = etag;


        try {

            this.setEtag();
            this.setCacheControl();
            this.setAcceptRanges();

        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

    }
    public FileCacheHttpResponse(int statusCode,  Date lastModified) {
        super();

        this.statusCode = statusCode;
        this.lastModified = lastModified;

        try {


            this.setLastModified();
            this.setCacheControl();
            this.setAcceptRanges();

        } catch (IOException e) {
             logger.log(Level.SEVERE, e.getMessage());
        }

    }

    @Override
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


    private void setEtag() throws IOException {
        headers.put("Etag",this.etag);
    }

    private void setLastModified() throws IOException {

        headers.put("Last-Modified", Utility.formatToRFC1123(this.lastModified));
    }

    private void setAcceptRanges()  {

        headers.put("Accept-Ranges","bytes");

    }

}
