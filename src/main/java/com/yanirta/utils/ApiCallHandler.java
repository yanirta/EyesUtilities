package com.yanirta.utils;

import com.yanirta.obj.Contexts.Context;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public abstract class ApiCallHandler {
    private static final long INTERVAL_MULTIPLIER = 2;
    private static final int POLLING_RETRIES = 10;
    private static final String LOCATION_HEADER = "Location";

    private static final CloseableHttpClient client = HttpClientBuilder.create().build();

    public static CloseableHttpResponse sendGetRequest(String uri, Context ctx) throws InterruptedException, IOException {
        HttpGet get = new HttpGet(uri);
        return runLongTask(get, ctx);
    }

    public static CloseableHttpResponse sendPostRequest(String uri, StringEntity entity, Context ctx) throws InterruptedException, IOException {
        HttpPost post = new HttpPost(uri);
        post.addHeader("Content-Type", "application/json");
        post.setEntity(entity);
        return runLongTask(post, ctx);
    }

    public static CloseableHttpResponse sendPutRequest(String uri, StringEntity entity, Context ctx) throws InterruptedException, IOException {
        HttpPut put = new HttpPut(uri);
        put.addHeader("Content-Type", "application/json");
        put.setEntity(entity);
        return runLongTask(put, ctx);
    }

    private static CloseableHttpResponse runLongTask(HttpRequestBase request, Context ctx) throws InterruptedException, IOException {
        String location;
        CloseableHttpResponse firstResponse = sendRequest(request);
        if (firstResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) return firstResponse;

        if (firstResponse.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED)
            throwUnexpectedResponse(firstResponse.getStatusLine());

        location = firstResponse.getFirstHeader(LOCATION_HEADER).getValue();
        firstResponse.close();

        HttpGet get = new HttpGet(ctx.decorateLocation(location));
        try (CloseableHttpResponse response = pollStatus(get, POLLING_RETRIES)) {

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED)
                throwUnexpectedResponse(response.getStatusLine());

            location = response.getFirstHeader(LOCATION_HEADER).getValue();
        }
        HttpDelete del = new HttpDelete(ctx.decorateLocation(location));
        return sendRequest(del);
    }

    private static CloseableHttpResponse sendRequest(HttpRequestBase request) {
        try {
            return client.execute(request);
        } catch (Exception e) {
            throw new Error("Error message: " + e.getMessage());
        }
    }

    private static CloseableHttpResponse pollStatus(HttpGet req, int retry) throws InterruptedException, IOException {
        long interval = 500; //MSec
        Thread.sleep(interval);

        while (retry > 0) {
            Thread.sleep(interval);
            try (CloseableHttpResponse response = sendRequest(req)) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED)
                    return response;
                interval = Math.min(INTERVAL_MULTIPLIER * interval, 10000);
                retry--;
            }
        }
        throw new Error("Error message: Failed to get response");
    }

    private static void throwUnexpectedResponse(StatusLine statusLine) {
        throw new RuntimeException(String.format("Unexpected response from request, code: %s, message: %s \n",
                statusLine.getStatusCode(),
                statusLine.getReasonPhrase()));
    }
}
