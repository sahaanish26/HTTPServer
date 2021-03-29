package com.example.httpserver.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ContentType {



    /**
     * User readable Content descriptions
     */
    public static Map<String, String> contentTypes;

    /**
     * Initialization of the read-only {@link #reasons} {@link Map}
     *  //https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
     */
    static {
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("CSS", "text/css");
        typeMap.put("GIF", "image/gif");
        typeMap.put("HTM", "text/html");
        typeMap.put("HTML", "text/html");
        typeMap.put("ICO", "text/gif");
        typeMap.put("JPG", "image/jpeg");
        typeMap.put("JPEG", "image/jpeg");
        typeMap.put("PNG", "image/png");
        typeMap.put("TXT", "text/plain");
        typeMap.put("XML", "text/xml");
        typeMap.put("JSON", "application/json");
        typeMap.put("PDF", "application/pdf");
        contentTypes = Collections.unmodifiableMap(typeMap);
    }
}
