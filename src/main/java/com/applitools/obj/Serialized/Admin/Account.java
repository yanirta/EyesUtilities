package com.applitools.obj.Serialized.Admin;


import com.applitools.obj.AdminApi;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.IOException;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {
    private static final String ACCOUNT_API = "accounts/%s";

    private String id;
    private String name;
    private HashMap<String, Subscriber> members;

    private AdminApi apiref_;

    public Account() {
    }

    public Account(String name) {
        this(name, null);
    }


    public Account(String name, AdminApi api) {
        this.name = name;
        this.apiref_ = api;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Subscriber> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, Subscriber> members) {
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public void setAdminApi(AdminApi api) {
        this.apiref_ = api;
    }

    public void add(User user, boolean isViewer, boolean isAdmin) throws IOException {
        if (members.containsKey(user.id))
            throw new RuntimeException("The user is already part of the team!");

        Subscriber subscriber = new Subscriber(user.id, isViewer, isAdmin);

        String account = String.format(ACCOUNT_API, id);
        account = String.format(AdminApi.GENERAL_API,
                apiref_.getServer(),
                account,
                apiref_.getUsername(),
                apiref_.getUserId());

        members.put(user.id, subscriber);

        try {
            AdminApi.put(account, this);
        } catch (IOException e) {
            members.remove(user.id);
            throw e;
        }
    }

    public void remove(Subscriber subscriber) throws IOException {
        remove(subscriber.getName());
    }

    public void remove(String username) throws IOException {
        if (!members.containsKey(username))
            throw new RuntimeException("The user is not part of the team!");
        Subscriber revert = members.get(username);
        members.remove(username);

        String account = String.format(ACCOUNT_API, id);
        account = String.format(AdminApi.GENERAL_API,
                apiref_.getServer(),
                account,
                apiref_.getUsername(),
                apiref_.getUserId());
        try {
            AdminApi.put(account, this);
        } catch (IOException e) {
            members.put(username, revert);
            throw e;
        }
    }

    public void update() throws IOException {
        String account = String.format(ACCOUNT_API, id);
        account = String.format(AdminApi.GENERAL_API,
                apiref_.getServer(),
                account,
                apiref_.getUsername(),
                apiref_.getUserId());

        AdminApi.put(account, this);
    }
}
