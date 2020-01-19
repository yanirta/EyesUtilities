package com.applitools.Commands;

import com.applitools.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public abstract class LongTaskAPI extends CommandBase {
    private static final String LOCATION_HEADER = "Location";
    protected static ObjectMapper mapper = new ObjectMapper();

    public void run() throws Exception {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = sendTask(client);
        try {
            switch (response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_OK:
                    parseResponse(response.getEntity());
                    System.out.printf("Finished\n");
                    return; //Success
                case HttpStatus.SC_CONFLICT:
                    System.out.printf("Conflict!");
                    return;
                case HttpStatus.SC_ACCEPTED:
                    if (!response.containsHeader("Location"))
                        throw new RuntimeException("Error, No Location header found on continuous task!");
                    System.out.printf("This task might take time, lay back...\n");
                    Header delete = waitForFinish(response.getFirstHeader(LOCATION_HEADER), client);
                    deleteLongTask(delete, client);
                    System.out.printf("Finished\n");
                default:
                    throwUnexpectedResponse(response.getStatusLine());
            }
        } finally {
            response.close();
            client.close();
        }
    }

    protected abstract void parseResponse(HttpEntity entity) throws IOException;

    @JsonIgnore
    public abstract String getTaskUrl();

    private CloseableHttpResponse sendTask(CloseableHttpClient client) throws IOException {
        HttpPost post = new HttpPost(getTaskUrl());
        String json = mapper.writeValueAsString(this);
        StringEntity entity = new StringEntity(json);
        post.setEntity(entity);
        setHeaders(post);
        return client.execute(post);
    }

    private static void setHeaders(HttpRequestBase post) {
        post.addHeader("Content-Type", "application/json");
        post.addHeader("Eyes-Expect", "202+location");
        post.addHeader("Eyes-Date", Utils.getRFC1123Date());
    }


    private static void throwUnexpectedResponse(StatusLine statusLine) {
        throw new RuntimeException(String.format("Unexpected response from request, code: %s, message: %s \n",
                statusLine.getStatusCode(),
                statusLine.getReasonPhrase()));
    }

    private static Header waitForFinish(Header location, CloseableHttpClient client) throws IOException, InterruptedException {
        if (location == null || StringUtils.isEmpty(location.getValue()))
            throw new RuntimeException("Error, Invalid location header!");
        HttpGet status = new HttpGet(location.getValue());
        setHeaders(status);
        long interval = 500; //MSec
        do {
            CloseableHttpResponse response = client.execute(status);
            try {
                switch (response.getStatusLine().getStatusCode()) {
                    case HttpStatus.SC_OK:
                        Thread.sleep(interval);
                        interval = Math.min(2 * interval, 10000);
                        break;
                    case HttpStatus.SC_ACCEPTED:
                        if (!response.containsHeader(LOCATION_HEADER))
                            throw new RuntimeException("Error, No Location header found on continuous task!");
                        return response.getLastHeader(LOCATION_HEADER);
                    default:
                        throwUnexpectedResponse(response.getStatusLine());
                }
            } finally {
                response.close();
            }
        } while (true);
    }

    private static void deleteLongTask(Header location, CloseableHttpClient client) throws IOException {
        if (location == null || StringUtils.isEmpty(location.getValue()))
            throw new RuntimeException("Error, Invalid location header!");
        HttpDelete delete = new HttpDelete(location.getValue());
        CloseableHttpResponse response = client.execute(delete);
        try {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
                throwUnexpectedResponse(response.getStatusLine());
        } finally {
            response.close();
        }
    }
}
