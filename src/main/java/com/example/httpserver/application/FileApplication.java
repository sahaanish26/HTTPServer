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

import com.example.httpserver.helper.Utility;
import com.example.httpserver.http.*;




/**
 * A static file server application.
 */
public class FileApplication implements WebApplication {

    private final String documentRoot;

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
                       /*     response = new FileHttpResponse(HttpStatus.OK,
                                    new File(Paths.get(documentRoot, path).toString()));*/
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

    private HttpResponse returnResponse(HttpRequest request)  {
        HttpResponse response = null;
        if(request.getHeaders().containsKey("If-None-Match") || request.getHeaders().containsKey("If-Modified-Since")){

            if( request.getHeaders().containsKey("If-None-Match") ){
            String expectedTag = request.getHeaders().get("If-None-Match");
            System.out.println("expectedTag"+expectedTag);
            String actualTag = Utility.checksum(new File(Paths.get(documentRoot, request.getPath()).toString()));
                System.out.println("actualTag"+actualTag);
            if(expectedTag.trim().equalsIgnoreCase(actualTag)){
                System.out.println("actualTag-->"+actualTag);
                return new FileCacheHttpResponse(HttpStatus.NOT_MODIFIED,actualTag) ;

            }
            }
            else{
                            String browserDateString = request.getHeaders().get("If-Modified-Since");
                            System.out.println("browserDateString"+browserDateString);

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
            //handling valid scenarios for now exception scenario to be taken care later
            String range = request.getHeaders().get("Range").trim();
            /*if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
                   return;
            }*/
            String[] arr = range.substring(6).split(",");
            System.out.println("arr"+ Arrays.toString(arr));
            List<List<Integer>> listIntervals = new ArrayList<>();
            for(String eachRange: arr){
                String[] intervals = eachRange.trim().split("-");
                System.out.println("intervals"+ Arrays.toString(intervals));
                List<Integer> interval = new ArrayList<>();
                interval.add(Integer.parseInt(intervals[0]));
                interval.add(Integer.parseInt(intervals[1]));
                listIntervals.add(interval);
            }
           // validate(listIntervals)

            System.out.println("listIntervals"+ listIntervals);
            return new FilePartialHttpResponse(HttpStatus.PARTIAL_CONTENT,new File(Paths.get(documentRoot, request.getPath()).toString()),listIntervals,request.getUri()) ;



        }

        response = new FileHttpResponse(HttpStatus.OK,
                    new File(Paths.get(documentRoot, request.getPath()).toString()),request.getUri());


        return response;
    }
}

