package com.example.httpserver.application;

import com.example.httpserver.http.HttpRequest;
import com.example.httpserver.http.HttpResponse;

public interface WebApplication {


    public HttpResponse handle(HttpRequest request);
}
