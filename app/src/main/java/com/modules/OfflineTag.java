package com.modules;

/**
 * Created by Dudu on 17/10/02.
 */

public class OfflineTag {
    private String tagID;
    private String timeStampStr;
    private String terminalID;

    public OfflineTag(String tagID, String timeStampStr, String terminalID) {
        this.tagID = tagID;
        this.timeStampStr = timeStampStr;
        this.terminalID = terminalID;
    }

    public String getTagID() {
        return tagID;
    }

    public void setTagID(String tagID) {
        this.tagID = tagID;
    }

    public String getTimeStampStr() {
        return timeStampStr;
    }

    public void setTimeStampStr(String timeStampStr) {
        this.timeStampStr = timeStampStr;
    }

    public String getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }
}
