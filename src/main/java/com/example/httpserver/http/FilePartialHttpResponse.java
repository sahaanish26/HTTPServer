package com.example.httpserver.http;

import com.example.httpserver.helper.Utility;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class FilePartialHttpResponse extends HttpResponse {

    /**
     * part of File to be sent to the user.
     */
    private File inputFile;

    /**
     * Interval List of bytes that need to be sent from file.
     */
    private List<List<Integer>> intervalList;

    /**
     * path to the File part of whcihc to be sent to the user.
     */
    private String uri;
    /**
     * content length of response body that need to be sent from file.
     */
    private int contentLength;


    private final static Logger logger = Logger.getLogger(FilePartialHttpResponse.class.getName());


    public FilePartialHttpResponse(int statusCode, File inputFile, List<List<Integer>> listIntervals, String uri) {
        super();
        this.statusCode = statusCode;
        this.inputFile=inputFile;
        /**Not setting directly OR using in built cloning but implementing defensive copying*/
        intervalList = new ArrayList<>();
        for(List<Integer> inputList:listIntervals ){
            contentLength = contentLength + inputList.get(1) - inputList.get(0)+1;
            List<Integer> innerList = new ArrayList<>();
            for(int k : inputList){
                innerList.add(k);
            }
            intervalList.add(innerList);
        }
             this.uri=uri;

        try {
            this.setContentLength();
            this.setCacheControl();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setContentType();
        this.setAcceptRanges();

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
            //setting arb numbers ..to be taken from ranges.

            for ( List<Integer> eachInterval : intervalList) {
                int start=eachInterval.get(0);
                int end=eachInterval.get(1) ;
                writeRange(start, end, out, writer);

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void writeRange(int start, int end, OutputStream out, BufferedWriter writer) {
        logger.info("Start"+start);
        logger.info("end"+end);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
            int read;
            int total = 0;
            int max = end - start +1;
            reader.skip(start);
            char[] buffer = new char[1024];
            while ((read = reader.read(buffer)) != -1) {
                logger.info("read" + read);
                total = total + read;
                logger.info("total" + total);
                if (total >= max) {
                    int extra = total - max;
                    read = read - extra;
                    logger.info("read inside" + read);
                    writer.write(buffer, 0, read);
                    break;
                }
                writer.write(buffer, 0, read);

            }
            reader.close();

            writer.flush();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private void setContentLength() throws IOException {
        this.getHeaders().put("Content-Length", String.valueOf(this.contentLength));
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
        headers.put("Date", Utility.formatToRFC1123(now));
        headers.put("Expires",Utility.formatToRFC1123(later));
    }




    private void setAcceptRanges()  {

        headers.put("Accept-Ranges","bytes");

    }


    /**
     * This function determines the content type.
     *  Content Type is derived based on file extension.Could not use Files.probeContentType since it does not support
     *   all OS types(does not work in mac OS) https://bugs.java.com/bugdatabase/view_bug.do?bug_id=8129632.So either
     *   will upgrade to jdk9 or use https://github.com/j256/simplemagic
     */
    private void setContentType()  {


        String ext = uri.substring(this.uri.indexOf(".") + 1);
        String type = ContentType.contentTypes.get(ext.toUpperCase());
        String disposition = "inline";
        if (type != null) {
            headers.put("Content-Type",type);
        }else{
            headers.put("Content-Type","application/octet-stream");
        }

    }

}
