package com.minipay.config.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class RequestLogInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLogInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException {
        logger.info("➡️ Request URI: {}", request.getURI());
        logger.info("➡️ Method: {}", request.getMethod());
        logger.info("➡️ Headers: {}", request.getHeaders());
        logger.info("➡️ Body: {}", new String(body, "UTF-8"));
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        String responseBody = new BufferedReader(new InputStreamReader(response.getBody()))
                .lines()
                .collect(Collectors.joining("\n"));

        logger.info("⬅️ Status Code: {}", response.getStatusCode());
        logger.info("⬅️ Headers: {}", response.getHeaders());
        logger.info("⬅️ Body: {}", responseBody);
    }
}
