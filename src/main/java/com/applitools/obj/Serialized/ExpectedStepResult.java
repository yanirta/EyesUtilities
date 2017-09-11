package com.applitools.obj.Serialized;

import java.util.HashMap;

public class ExpectedStepResult {
    private String Tag;
    private HashMap Image;
    private Object LegacyMatchSettings;
    private Object Annotations;
    private Object LegacyAppEnvironment;
    private HashMap Thumbprint;
    private Object matchSettings;
    private String OccurredAt;
    private Object appEnvironment;

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public Object getImage() {
        return Image;
    }

    public void setImage(HashMap image) {
        Image = image;
    }

    public Object getLegacyMatchSettings() {
        return LegacyMatchSettings;
    }

    public void setLegacyMatchSettings(Object legacyMatchSettings) {
        LegacyMatchSettings = legacyMatchSettings;
    }

    public Object getAnnotations() {
        return Annotations;
    }

    public void setAnnotations(Object annotations) {
        Annotations = annotations;
    }

    public Object getLegacyAppEnvironment() {
        return LegacyAppEnvironment;
    }

    public void setLegacyAppEnvironment(Object legacyAppEnvironment) {
        LegacyAppEnvironment = legacyAppEnvironment;
    }

    public HashMap getThumbprint() {
        return Thumbprint;
    }

    public void setThumbprint(HashMap thumbprint) {
        this.Thumbprint = thumbprint;
    }

    public Object getMatchSettings() {
        return matchSettings;
    }

    public void setMatchSettings(Object matchSettings) {
        this.matchSettings = matchSettings;
    }

    public String getOccurredAt() {
        return OccurredAt;
    }

    public void setOccurredAt(String occurredAt) {
        OccurredAt = occurredAt;
    }

    public Object getAppEnvironment() {
        return appEnvironment;
    }

    public void setAppEnvironment(Object appEnvironment) {
        this.appEnvironment = appEnvironment;
    }

    public String getImageId() {
        return getImageId(Image);
    }

    public String getThumbprintId() {
        return getImageId(Thumbprint);
    }

    private String getImageId(HashMap imageRecord) {
        return imageRecord.get("id").toString();
    }
}
