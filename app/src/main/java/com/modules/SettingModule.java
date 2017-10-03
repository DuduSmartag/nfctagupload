package com.modules;

/**
 * Created by Dudu on 10/1/2017.
 */

public class SettingModule {
    private String logoPath;
    private Boolean needToUpdateNFCFile;

    public SettingModule(String logoPath, Boolean needToUpdateNFCFile) {
        this.logoPath = logoPath;
        this.needToUpdateNFCFile = needToUpdateNFCFile;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public Boolean getNeedToUpdateNFCFile() {
        return needToUpdateNFCFile;
    }

    public void setNeedToUpdateNFCFile(Boolean needToUpdateNFCFile) {
        this.needToUpdateNFCFile = needToUpdateNFCFile;
    }
}
