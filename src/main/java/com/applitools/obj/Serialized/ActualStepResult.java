package com.applitools.obj.Serialized;

import java.util.HashMap;

/**
 * Created by yanir on 10/04/2017.
 */
public class ActualStepResult {
    private HashMap Image;
    private HashMap Thumbprint;
    private Object ImageMatchSettings;
    private Boolean IgnoreExpectedOutputSettings;
    private Boolean IsMatching;
    private Boolean AreImagesMatching;
    private String OccurredAt;
    private Object UserInputs;
    private String Tag;
    private String windowTitle;
    private Boolean IsPrimary;
    private String ExpectedImageId;
    private String expectedThumbprintId;
    private Boolean wasDomUsed;

    public Boolean getIsPrimary() {
        return IsPrimary;
    }

    public void setIsPrimary(Boolean primary) {
        IsPrimary = primary;
    }

    public Object getImage() {
        return Image;
    }

    public void setImage(HashMap image) {
        Image = image;
    }

    public Object getThumbprint() {
        return Thumbprint;
    }

    public void setThumbprint(HashMap thumbprint) {
        Thumbprint = thumbprint;
    }

    public Object getImageMatchSettings() {
        return ImageMatchSettings;
    }

    public void setImageMatchSettings(Object imageMatchSettings) {
        ImageMatchSettings = imageMatchSettings;
    }

    public Boolean getIgnoreExpectedOutputSettings() {
        return IgnoreExpectedOutputSettings;
    }

    public void setIgnoreExpectedOutputSettings(Boolean ignoreExpectedOutputSettings) {
        IgnoreExpectedOutputSettings = ignoreExpectedOutputSettings;
    }

    public Boolean getIsMatching() {
        return IsMatching;
    }

    public void setIsMatching(Boolean matching) {
        IsMatching = matching;
    }

    public Boolean getAreImagesMatching() {
        return AreImagesMatching;
    }

    public void setAreImagesMatching(Boolean areImagesMatching) {
        AreImagesMatching = areImagesMatching;
    }

    public String getOccurredAt() {
        return OccurredAt;
    }

    public void setOccurredAt(String occurredAt) {
        OccurredAt = occurredAt;
    }

    public Object getUserInputs() {
        return UserInputs;
    }

    public void setUserInputs(Object userInputs) {
        UserInputs = userInputs;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    public String getExpectedImageId() {
        return ExpectedImageId;
    }

    public void setExpectedImageId(String expectedImageId) {
        ExpectedImageId = expectedImageId;
    }

    public String getExpectedThumbprintId() {
        return expectedThumbprintId;
    }

    public void setExpectedThumbprintId(String expectedThumbprintId) {
        this.expectedThumbprintId = expectedThumbprintId;
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

    public Boolean getWasDomUsed() {
        return wasDomUsed;
    }

    public void setWasDomUsed(Boolean wasDomUsed) {
        this.wasDomUsed = wasDomUsed;
    }
}
