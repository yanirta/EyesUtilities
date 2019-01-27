package com.applitools.obj.Serialized.Admin;

public class User {
    String id;
    String email;
    String fullName;
    String sessionId;
    String lastSessionStartedAt;
    String addedAt;

    public User() {
    }

    public User(String id, String email, String fullName) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getLastSessionStartedAt() {
        return lastSessionStartedAt;
    }

    public void setLastSessionStartedAt(String lastSessionStartedAt) {
        this.lastSessionStartedAt = lastSessionStartedAt;
    }

    public String getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(String addedAt) {
        this.addedAt = addedAt;
    }

}
