package com.applitools.obj;

import com.applitools.obj.Serialized.Admin.Account;
import com.applitools.obj.Serialized.Admin.User;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AdminApi {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static final String GENERAL_API = "https://%s/api/admin/%s?format=json&userName=%s&userId=%s";
    private static final String ACCOUNTS_API = "orgs/%s/accounts";
    private static final String USERS_API = "orgs/%s/users/%s";
    private static final String LOGIN_API = "https://%s/api/auth/login?format=json&username=%s&password=%s";
    private final String server_;
    private final String username_;
    private final String userId_;
    private final String orgId_;

    private User[] users;


    public String getServer() {
        return server_;
    }

    public String getUsername() {
        return username_;
    }

    public String getUserId() {
        return userId_;
    }

    public String getOrgId() {
        return orgId_;
    }

    public AdminApi(String orgId, String username, String userId) throws MalformedURLException {
        this("https://eyes.applitools.com", orgId, username, userId);
    }

    public AdminApi(String server, String orgId, String username, String userId) {
        this.server_ = server;
        this.username_ = username;
        this.userId_ = userId;
        this.orgId_ = orgId;
    }

    public static String getUserId(String server, String username, String password) throws IOException {
        String login = String.format(LOGIN_API, server, username, password);
        List<Cookie> cookies = get(login);
        for (Cookie cookie : cookies)
            if (cookie.getName().compareTo("user-id") == 0)
                return cookie.getValue();
        return null;
    }

    public Account[] getAccounts() throws IOException {
        String accounts = String.format(ACCOUNTS_API, orgId_);
        accounts = String.format(GENERAL_API, server_, accounts, username_, userId_);
        Account[] retaccounts = mapper.readValue(new URL(accounts), Account[].class);
        for (Account acc : retaccounts) {
            acc.setAdminApi(this);
        }
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
        String accounts = String.format(ACCOUNTS_API, orgId_);
        accounts = String.format(GENERAL_API, server_, accounts, username_, userId_);
        CloseableHttpResponse response = post(accounts, account);
        try {
            InputStream content = post(accounts, account).getEntity().getContent();
            return mapper.readValue(content, Account.class);
        } finally {
            response.close();
        }
    }

    public void addUser(User user) throws IOException {
        String users = String.format(USERS_API, orgId_, user.getId());
        users = String.format(GENERAL_API, server_, users, username_, userId_);
        post(users, user);
    }

    public void RemoveUser(User user) throws IOException {
        removeUser(user.getId());
    }

    public void removeUser(String userId) throws IOException {
        String users = String.format(USERS_API, orgId_, userId);
        users = String.format(GENERAL_API, server_, users, username_, userId_);
        delete(users);
    }

    public User[] getUsers() throws IOException {
        if (this.users != null) return users;
        String users = String.format(USERS_API, orgId_, "");
        users = String.format(GENERAL_API, server_, users, username_, userId_);
        this.users = mapper.readValue(new URL(users), User[].class);
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

    public static List<Cookie> get(String url) throws IOException {
        HttpGet get = new HttpGet(url);
        HttpClientContext context = HttpClientContext.create();
        request(get, context);
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
}
