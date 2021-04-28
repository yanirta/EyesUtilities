package com.yanirta.obj;

import com.yanirta.obj.Serialized.Admin.Account;
import com.yanirta.obj.Serialized.Admin.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AdminApi {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static final String GENERAL_API = "https://%s/api/admin/%s?format=json&apiKey=%s";
    private static final String ACCOUNTS_API = "orgs/%s/accounts/%s";
    private static final String USERS_API = "orgs/%s/users/%s";
    private static final String LOGIN_API = "https://%s/api/auth/login?format=json&username=%s&password=%s";
    private final String server_;
    private final String orgId_;
    private final String apiKey_;
    private User[] users;

    public AdminApi(String server, String orgId, String apiKey) {
        this.server_ = server;
        this.apiKey_ = apiKey;
        this.orgId_ = orgId;
    }

    public String getApiKey() {
        return apiKey_;
    }

    public String getServer() {
        return server_;
    }

    public String getOrgId() {
        return orgId_;
    }

    public Account[] getAccounts() throws IOException {
        String accounts = constructUrl(ACCOUNTS_API, orgId_, "");
        HttpResponse response = get(accounts).getResponse();
        Account[] retaccounts = mapper.readValue(response.getEntity().getContent(), Account[].class);
        for (Account acc : retaccounts)
            acc.setAdminApi(this);

        return retaccounts;
    }

    public Account getAccount(String accountId) throws IOException {
        Account[] accounts = getAccounts();
        Optional<Account> match = Arrays.stream(accounts)
                .filter(
                        (Account a) -> a.getId().compareTo(accountId) == 0)
                .findFirst();
        if (match.isPresent()) return match.get();
        return null;
    }

    public Account addAccount(Account account) throws IOException {
        String accounts = constructUrl(ACCOUNTS_API, orgId_, "");
        CloseableHttpResponse response = post(accounts, account);
        try {
            InputStream content = post(accounts, account).getEntity().getContent();
            return mapper.readValue(content, Account.class);
        } finally {
            response.close();
        }
    }

    public void updateAccount(Account account) throws IOException {
        String accounts = constructUrl(ACCOUNTS_API, orgId_, account.getId());
        put(accounts, account);
    }

    public void addUser(User user) throws IOException {
        String users = constructUrl(USERS_API, orgId_, user.getId());
        post(users, user);
    }

    public void RemoveUser(User user) throws IOException {
        removeUser(user.getId());
    }

    public void removeUser(String userId) throws IOException {
        String users = constructUrl(USERS_API, orgId_, userId);
        delete(users);
    }

    public User[] getUsers() throws IOException {
        if (this.users != null) return users;
        String users = constructUrl(USERS_API, orgId_, "");
        HttpResponse response = get(users).getResponse();
        this.users = mapper.readValue(response.getEntity().getContent(), User[].class);
        return this.users;
    }

    public User getUserById(String userId) throws IOException {
        User[] users = getUsers();
        Optional<User> match = Arrays.stream(users).filter(
                (User u) -> u.getId().compareTo(userId) == 0).findFirst();
        if (match.isPresent())
            return match.get();
        return null;
    }

    public User getUserByEmail(String userEmail) throws IOException {
        User[] users = getUsers();
        Optional<User> match = Arrays.stream(users).filter(
                (User u) -> u.getId().compareTo(userEmail) == 0).findFirst();
        if (match.isPresent())
            return match.get();
        return null;
    }

    public static void delete(String url) throws IOException {
        HttpDelete delete = new HttpDelete(url);
        request(delete);
    }

    public static HttpClientContext get(String url) throws IOException {
        HttpGet get = new HttpGet(url);
        HttpClientContext context = HttpClientContext.create();
        request(get, context);
        return context;
    }

    public static List<Cookie> getCookies(String url) throws IOException {
        HttpClientContext context = get(url);
        CookieStore cookieStore = context.getCookieStore();
        return cookieStore.getCookies();
    }

    public static CloseableHttpResponse post(String url, Object body) throws IOException {
        HttpPost post = new HttpPost(url);
        post.addHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(mapper.writeValueAsString(body)));
        return request(post);
    }

    public static void put(String url, Object body) throws IOException {
        HttpPut put = new HttpPut(url);
        put.addHeader("Content-Type", "application/json");
        put.setEntity(new StringEntity(mapper.writeValueAsString(body)));
        request(put);
    }

    public static CloseableHttpResponse request(HttpUriRequest request) throws IOException {
        return request(request, null);
    }

    public static CloseableHttpResponse request(HttpUriRequest request, HttpClientContext context) throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        try {
            if (context != null)
                response = client.execute(request, context);
            else
                response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != 200)
                throw new RuntimeException(response.getStatusLine().getReasonPhrase());
            return response;
        } catch (IOException e) {
            throw e;
        }
    }

    private String constructUrl(String api, String... args) {
        String retUrl = String.format(api, (Object[]) args);
        retUrl = String.format(GENERAL_API, server_, retUrl, this.apiKey_);
        return retUrl;
    }
}
