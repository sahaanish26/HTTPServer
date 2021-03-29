package com.example.httpserver.application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.example.httpserver.HttpServer;
import com.example.httpserver.helper.Utility;
import com.example.httpserver.http.*;




/**
 * A static file server application.
 */
public class FileApplication implements WebApplication {

    private final String documentRoot;
    private final static Logger logger = Logger.getLogger(FileApplication.class.getName());


    public FileApplication(String documentRoot) {
        this.documentRoot = documentRoot;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {

        String path = request.getPath();


        HttpResponse response;

        switch (request.getMethod()) {
            case HttpMethod.GET:
                Path requestedFile = Paths.get(documentRoot, path);
                if (requestedFile.normalize().startsWith(Paths.get(documentRoot).normalize())) {
                    if (Files.exists(requestedFile)) {
                        if (Files.isDirectory(requestedFile)) {
                            response = new EmptyHttpResponse(HttpStatus.FORBIDDEN);
                        } else {
                            response = returnResponse(request);
                                              }
                    } else {
                        response = new EmptyHttpResponse(HttpStatus.NOT_FOUND);
                    }
                } else {
                    response = new EmptyHttpResponse(HttpStatus.FORBIDDEN);
                }
                break;
            case HttpMethod.TRACE:
                response = new StreamHttpResponse(HttpStatus.OK, request.getInputStream());
                break;
            case HttpMethod.HEAD:
                if (Files.exists(Paths.get(documentRoot, path))) {
                    response = new HeadHttpResponse(HttpStatus.OK,
                            new File(Paths.get(documentRoot, path).toString()),request.getUri());
                } else {
                    response = new EmptyHttpResponse(HttpStatus.NOT_FOUND);
                }
                break;
            default:
                response = new EmptyHttpResponse(HttpStatus.NOT_IMPLEMENTED);
                break;
        }

        return response;
    }

    /**
     * helper method to determine correct response type and return an instance
     * @param  @HttpRequest request
     * @return HttpResponse
     */
    private HttpResponse returnResponse(HttpRequest request)  {
        HttpResponse response = null;
        // Check for ETAG/If-Modified-Since
        if(request.getHeaders().containsKey("If-None-Match") || request.getHeaders().containsKey("If-Modified-Since")){

            if( request.getHeaders().containsKey("If-None-Match") ){
            String expectedTag = request.getHeaders().get("If-None-Match");

            String actualTag = Utility.checksum(new File(Paths.get(documentRoot, request.getPath()).toString()));
                           if(expectedTag.trim().equalsIgnoreCase(actualTag)){
                logger.info("actualTag-->"+actualTag);
                return new FileCacheHttpResponse(HttpStatus.NOT_MODIFIED,actualTag) ;
            }
            }
            else{
                            String browserDateString = request.getHeaders().get("If-Modified-Since");
                logger.info("browserDateString"+browserDateString);

                            FileTime fileTime = null;
                            Date browserDate = null;
                            Date fileDate = null;
                            try {
                                Path requestedFile = Paths.get(documentRoot, request.getPath());
                                fileTime = Files.getLastModifiedTime(requestedFile);
                                browserDate = Utility.RFC1123ToDate(browserDateString);
                                fileDate = new Date(fileTime.toMillis());
                            } catch (IOException | ParseException e) {
                                e.printStackTrace();
                            }


                if((browserDate!=null) && fileDate.compareTo(browserDate)<=0){

                    return new FileCacheHttpResponse(HttpStatus.NOT_MODIFIED,fileDate) ;

                }
            }
        }


        if(request.getHeaders().containsKey("Range") ){
             String range = request.getHeaders().get("Range").trim();
            //TODO handling valid scenarios for now exception scenario to be taken care later
            /*if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
                   return;
            }*/
            String[] arr = range.substring(6).split(",");
            logger.info("arr"+ Arrays.toString(arr));
            List<List<Integer>> listIntervals = new ArrayList<>();
            for(String eachRange: arr){
                String[] intervals = eachRange.trim().split("-");
                List<Integer> interval = new ArrayList<>();
                interval.add(Integer.parseInt(intervals[0]));
                interval.add(Integer.parseInt(intervals[1]));
                listIntervals.add(interval);
            }
            //TODO handling valid scenarios for now exception/overlapping scenario to be taken care later
           // validate(listIntervals)

            logger.info("listIntervals"+ listIntervals);
            return new FilePartialHttpResponse(HttpStatus.PARTIAL_CONTENT,new File(Paths.get(documentRoot, request.getPath()).toString()),listIntervals,request.getUri()) ;



        }
        //Default case return full response
        response = new FileHttpResponse(HttpStatus.OK,
                    new File(Paths.get(documentRoot, request.getPath()).toString()),request.getUri());


        return response;
    }
}

