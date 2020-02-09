package com.applitools.utils;

import com.applitools.obj.Contexts.ResultsAPIContext;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class ApiCallHandler {
    private static final long INTERVAL_MULTIPLIER = 2;
    private static final int POLLING_RETRIES = 10;
    private static final String LOCATION_HEADER = "Location";

    private static final CloseableHttpClient client = HttpClientBuilder.create().build();

    public static CloseableHttpResponse sendGetRequest(String uri, ResultsAPIContext ctx) throws InterruptedException {
        HttpGet req = new HttpGet(uri);
        CloseableHttpResponse response = sendRequest(req);
        String location;
        while (response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK) {
            switch (response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_ACCEPTED: //202
                    location = response.getFirstHeader(LOCATION_HEADER).getValue();
                    req = new HttpGet(ctx.decorateLocation(location));
                    response = pollStatus(req, POLLING_RETRIES);
                    break;
                case HttpStatus.SC_CREATED: //201
                    location = response.getFirstHeader(LOCATION_HEADER).getValue();
                    HttpDelete del = new HttpDelete(ctx.decorateLocation(location));
                    response = sendRequest(del);
                    break;
                default:
                    throwUnexpectedResponse(response.getStatusLine());
            }
        }
        return response;
    }

    private static CloseableHttpResponse sendRequest(HttpRequestBase request){
        try {
//            System.out.println(request.toString());
            return client.execute(request);
        }catch (Exception e) {
            throw new Error("Error message: " + e.getMessage());
        }
    }

    private static CloseableHttpResponse pollStatus(HttpGet req, int retry) throws InterruptedException {
        long interval = 500; //MSec
        Thread.sleep(interval);

        CloseableHttpResponse response;
        while(retry>0) {
            Thread.sleep(interval);
            response = sendRequest(req);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED)
                return response;
            interval = Math.min(INTERVAL_MULTIPLIER * interval, 10000);
            retry--;
        }
        throw new Error("Error message: Failed to get response");
    }

    private static void throwUnexpectedResponse(StatusLine statusLine) {
        throw new RuntimeException(String.format("Unexpected response from request, code: %s, message: %s \n",
                statusLine.getStatusCode(),
                statusLine.getReasonPhrase()));
    }
}
