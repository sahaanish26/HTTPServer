package com.example.httpserver.http;

//import com.example.httpserver.Logger;

import com.example.httpserver.helper.Utility;
import com.example.httpserver.workers.HttpServerDelegate;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
            // Logger.error(TAG, e.getMessage());
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








    private void setContentType() throws IOException {
        /*Path source = Paths.get(this.inputFile.toURI());
        System.out.println("source"+source);
        String contentType = Files.probeContentType(source);
        System.out.println("contentType"+contentType);
       // headers.put("Content-Type", "application/octet-stream");
        if (contentType != null) {
            headers.put("Content-Type", contentType);
        }
        MimetypesFileTypeMap m = new MimetypesFileTypeMap(source.toString());
        System.out.println( m.getContentType(this.inputFile) );
      *//*  String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", this.inputFile.getName());
        headers.put(headerKey,headerValue);
  *//*     // response.setHeader(headerKey, headerValue);

       // System.out.println("mimeType"+mimeType);


        ContentInfoUtil util = new ContentInfoUtil();
// if you want to use a different config file(s), you can load them by hand:
// ContentInfoUtil util = new ContentInfoUtil("/etc/magic");
// ...
        ContentInfo info = util.findMatch(String.valueOf(source));
       // System.out.println(info.getContentType().getMimeType());
        System.out.println(info);
*/


            String ext = uri.substring(this.uri.indexOf(".") + 1);
            String type = ContentType.contentTypes.get(ext.toUpperCase());
        String disposition = "inline";
        if (type != null) {
            headers.put("Content-Type",type);
        }else{
            headers.put("Content-Type","application/octet-stream");
        }
       // headers.put("Content-Disposition", disposition + ";filename=\"" + inputFile.getName() + "\"");






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

