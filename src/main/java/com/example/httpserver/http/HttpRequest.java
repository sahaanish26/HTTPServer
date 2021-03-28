package com.example.httpserver.http;

import com.example.httpserver.exceptions.BadRequestException;
import com.example.httpserver.exceptions.ConnectionClosedException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Contains all the information included in a request.
 * Contains a static method {@link #parse(InputStream)} that creates an {@link com.example.httpserver.http.HttpRequest} by reading
 * and parsing the data from the socket.
 */
public class HttpRequest {

    private static final String TAG = "me.homework.server.http.HttpRequest";

    /**
     * Input stream associated with the source socket
     */
    private InputStream inputStream;

    /**
     * Request line containing protocol version, URI, and request method
     */
    private String requestLine;

    private String method;
    private String uri;
    private String version;

    /**
     * Request headers
     */
    private HashMap<String, String> headers;

    /**
     * Query parameters
     */
    private HashMap<String, String> params;

    /**
     * URL path part
     */
    private String path;

    /**
     * URL query part
     */
    private String query;

    /**
     * Flag for keep-alive requests
     */
    private boolean keepAlive = false;


    public HttpRequest() {
        headers = new HashMap<>();
        params = new HashMap<>();
    }

    /**
     * This method creates a new {@link com.example.httpserver.http.HttpRequest} with the data read form the {@link #inputStream}.
     * Will throw exceptions in case of errors.
     *
     * @param inputStream The stream to read from.
     * @return A new instance of {@link com.example.httpserver.http.HttpRequest} containing information from the request
     * @throws BadRequestException If the request is not properly formatted
     * @throws ConnectionClosedException If there is an error while reading data
     */
    public static HttpRequest parse(InputStream inputStream) throws BadRequestException, ConnectionClosedException {
        try {
            HttpRequest request = new HttpRequest();
            request.inputStream = inputStream;

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            request.requestLine = reader.readLine();
            System.out.println( request.requestLine);
            if (request.requestLine == null) {
                throw new ConnectionClosedException();
            }

            String[] requestLineParts = request.requestLine.split(" ", 3);
            request.method = requestLineParts[0];
            request.uri = requestLineParts[1];
            request.version = requestLineParts[2];

            String line = reader.readLine();

            while (!line.equals("")) {
                System.out.println( line);
                String[] lineParts = line.split(":", 2);
                if (lineParts.length == 2) {
                    request.headers.put(lineParts[0], lineParts[1]);
                }
                line = reader.readLine();
            }

            String[] uriParts = request.uri.split("\\?", 2);
            if (uriParts.length == 2) {
                request.path = uriParts[0];
                request.query = uriParts[1];

                String[] keyValuePairs = request.query.split("&");
                for (String keyValuePair : keyValuePairs) {
                    String[] keyValue = keyValuePair.split("=", 2);
                    if (keyValue.length == 2) {
                        request.params.put(keyValue[0], keyValue[1]);
                    }
                }
            } else {
                request.path = request.uri;
                request.query = "";
            }

            if (request.headers.getOrDefault("Connection", "close").equalsIgnoreCase("keep-alive")) {
                request.keepAlive = true;
            }
            System.out.println( request.headers);
            return request;
        } catch(ConnectionClosedException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException();
        }
    }


    public String getRequestLine() {
        return requestLine;
    }

    public void setRequestLine(String requestLine) {
        this.requestLine = requestLine;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }
}

