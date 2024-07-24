package hk.gov.ogcio.eproof.model;

import java.io.File;

import org.json.JSONObject;

public final class SysObj {
    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        String t_rootPath = rootPath.trim();
        if (t_rootPath.endsWith(File.separator)) {
            t_rootPath = t_rootPath.substring(0, t_rootPath.length() - File.separator.length());
        }
        this.rootPath = t_rootPath;
    }

    public JSONObject getSysConfig() {
        return sysConfig;
    }

    public void setSysConfig(JSONObject sysConfig) {
        this.sysConfig = sysConfig;
    }

    public JSONObject getBdConfig() {
        return bdConfig;
    }

    public void setBdConfig(JSONObject bdConfig) {
        this.bdConfig = bdConfig;
    }

    public Boolean getBdConfigCheck() {
        return bdConfigCheck;
    }

    public void setBdConfigCheck(Boolean bdConfigCheck) {
        this.bdConfigCheck = bdConfigCheck;
    }

    public String getBdConfigCheckMessage() {
        return bdConfigCheckMessage;
    }

    public void setBdConfigCheckMessage(String bdConfigCheckMessage) {
        this.bdConfigCheckMessage = bdConfigCheckMessage;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    String rootPath = "";   //System Root Path
    JSONObject sysConfig;   //Object to cache the system config JSON
    JSONObject bdConfig;   //Object to cache the BD config JSON
    String accessToken;   //Object to cache the API Access Token

    public String getApiURL() {
        return apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }

    String apiURL;   //Object to cache the API Access Token

    Boolean bdConfigCheck;

    String bdConfigCheckMessage;


    public Boolean getIDEMode() {
        return isIDEMode;
    }

    public void setIDEMode(Boolean IDEMode) {
        isIDEMode = IDEMode;
    }

    Boolean isIDEMode;

    public Boolean getTESTMode() {
        return isTESTMode;
    }

    public void setTESTMode(Boolean TESTMode) {
        isTESTMode = TESTMode;
    }

    Boolean isTESTMode;


}
