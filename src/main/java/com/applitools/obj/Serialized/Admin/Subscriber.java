package com.applitools.obj.Serialized.Admin;

public class Subscriber {
    private String name; //username
    private boolean isViewer;
    private boolean isAdmin;

    public Subscriber() {
    }

    public Subscriber(String name) {
        this(name, false, false);
    }

    public Subscriber(String name, boolean isViewer, boolean isAdmin) {
        this.name = name;
        this.isViewer = isViewer;
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsViewer() {
        return isViewer;
    }

    public void setIsViewer(boolean viewer) {
        isViewer = viewer;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean admin) {
        isAdmin = admin;
    }
}
