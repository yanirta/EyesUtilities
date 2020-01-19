package com.applitools.obj.Serialized;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BranchInfo {
    private String id;
    private String name;
    private HashMap<Object, Object> updateInfo = new HashMap<>();
    private boolean isDeleted;

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<Object, Object> getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(HashMap<Object, Object> updateInfo) {
        this.updateInfo = updateInfo;
    }
}
