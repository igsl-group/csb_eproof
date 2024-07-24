package hk.gov.ogcio.eproof.controller;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hk.gov.ogcio.eproof.model.EProof;
import hk.gov.ogcio.eproof.model.SysObj;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static hk.gov.ogcio.eproof.controller.CommonUtil.gson;
import static hk.gov.ogcio.eproof.controller.SysUtil.getJSONFieldShortcut;

public final class ApiUtil {
    private static final Logger logger = LogManager.getLogger(ApiUtil.class);
    private static HttpUtil httpUtil = new HttpUtil();
    //private static CommonUtil commonUtil = new CommonUtil();
    //private static String accessToken = null;

    public static Boolean getHealthCheck(SysObj sysObj) throws Exception {
        Boolean ret = false;
        String url = getAPIUrl(sysObj, "/healthCheck");

        List<String> headersArray = new ArrayList<String>();
        headersArray.add("Content-Type");
        headersArray.add("application/json");
        String[] headers = headersArray.toArray(new String[0]);

        HttpResponse<String> response = httpUtil.get(url, headers);
        if (response.statusCode() == 200) {
            ret = true;
        }else{
            logger.error("Test Connection Error");
        }

        return ret;
    }

    public static Boolean getAccessTokenByClientCredentials(SysObj sysObj) throws Exception {
        // check if the token is valid
        Boolean ret = false;
        if ( sysObj.getAccessToken() == null || sysObj.getAccessToken().length()== 0 ) {
            if (!sysObj.getTESTMode()) {

                logger.debug("Empty token, try to get one");

                String url = getAPIUrl(sysObj, "/auth/token");

                List<String> headersArray = new ArrayList<String>();
                headersArray.add("Content-Type");
                headersArray.add("application/json");
                String[] headers = headersArray.toArray(new String[0]);

                HashMap jsonMap = new HashMap();
                jsonMap.put("grantType", "clientCredentials");
                jsonMap.put("clientId", sysObj.getBdConfig().getJSONObject("api_information").getString("clientId"));
                jsonMap.put("clientSecret", sysObj.getBdConfig().getJSONObject("api_information").getString("clientSecret"));

                String jsonRequestBody = gson.toJson(jsonMap);

                System.out.println("jsonRequestBody=" + jsonRequestBody);
                HttpResponse<String> response = httpUtil.post(url, headers, jsonRequestBody);
                //System.out.println(response.toString());
                //System.out.println(response.body().toString());
                //System.out.println(response.statusCode());

                if (response.statusCode() == 200) {
                    JsonElement jelement = new JsonParser().parse(response.body().toString());
                    JsonObject jobject = jelement.getAsJsonObject();
                    //accessToken = jobject.getAsJsonObject("data").get("accessToken").getAsString();

                    sysObj.setAccessToken(jobject.getAsJsonObject("data").get("accessToken").getAsString());
                    ret = true;
                } else {
                    sysObj.setAccessToken("");
                }

            }else{
                // Test Mode
                sysObj.setAccessToken("ACCESS TOKEN UNDER TEST MODE");
            }
        }else{
            logger.debug("Already a valid token");
            ret = true;
        }
        return ret;
    }

    // Louis
    // Issuer Related API
    public static JSONObject getEProofType(SysObj sysObj) throws Exception {
        JSONObject ret = null;
        //Check if need to get the correct token
        if (ApiUtil.getAccessTokenByClientCredentials(sysObj)) {
            String url = ApiUtil.getAPIUrl(sysObj, "/eProofType");
            List<String> headersArray = new ArrayList<String>();
            headersArray.add("Content-Type");
            headersArray.add("application/json");
            headersArray.add("Authorization");
            headersArray.add("Bearer " + sysObj.getAccessToken());
            String[] headers = headersArray.toArray(new String[0]);

            //String jsonRequestBody = gson.toJson(eProofTypeData);
            HttpResponse<String> response = httpUtil.get(url, headers);
            System.out.println(response.body().toString());

            if (response.statusCode() == 200) {
                logger.debug("response= " + response.toString());
                logger.debug("response.body= " + response.body().toString());
                logger.debug("response.statusCode= " + response.statusCode());
                //System.out.println(response.toString());
                //System.out.println(response.body().toString());
                //System.out.println(response.statusCode());

                //JsonElement jelement = new JsonParser().parse(response.body().toString());
                JSONObject jobject = new JSONObject(response.body().toString());// jelement.getAsJsonObject();

                ret = jobject;
            }else{
                logger.error("Error while calling the API");
            }
        }else{
            logger.debug("Cannot get correct token");
        }
        return ret;
    }

    //eProof Issuance

    public static HttpResponse<String>  issueEproof(SysObj sysObj, EProof eProof) throws Exception {
        HttpResponse<String> ret = null;
        if (ApiUtil.getAccessTokenByClientCredentials(sysObj)) {
            String url = getAPIUrl(sysObj, "/eProofMetadata");

            //Request Body
            HashMap requestBodyJsonMap = new HashMap<>();
            requestBodyJsonMap.put("eProofId", eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("eProofId"));
            requestBodyJsonMap.put("eProofTypeId", eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("eProofTypeId"));
            requestBodyJsonMap.put("templateCode", eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("templateCode"));
            requestBodyJsonMap.put("hkicHash", eProof.getHkicBase64Hash());
            requestBodyJsonMap.put("expiryDate", eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("expiryDate"));
            requestBodyJsonMap.put("issuanceDate", (new JSONObject(eProof.geteProofDataOutput()))
                    .getJSONObject("credentialSubject")
                    .getJSONObject("display")
                    .getString("issue_date"));
            requestBodyJsonMap.put("dataHash", eProof.getVcBase64Hash());
            //requestBodyJsonMap.put("pdfHash", eProof.getPdfBase64Hash());
            requestBodyJsonMap.put("authMethod", eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("authMethod"));
            requestBodyJsonMap.put("dataUrl", eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("dataUrl"));
            //requestBodyJsonMap.put("pdfUrl", eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("expiryDate"));
            requestBodyJsonMap.put("otpUrl", eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("otpUrl"));
            requestBodyJsonMap.put("downloadMaxCount", eProof.getjEProofConfig().getJSONObject("eproofMeta").getInt("downloadMaxCount"));
            requestBodyJsonMap.put("downloadExpiryDate", eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("downloadExpiryDate"));

            //Request Headers
            List<String> headersArray = new ArrayList<String>();
            headersArray.add("Content-Type");
            headersArray.add("application/json");
            headersArray.add("Authorization");
            headersArray.add("Bearer " + sysObj.getAccessToken());
            String[] headers = headersArray.toArray(new String[0]);

            String jsonRequestBody = gson.toJson(requestBodyJsonMap);
            logger.debug("POST /eProofMetadata Request Body");
            logger.debug(jsonRequestBody);

            HttpResponse<String> response = httpUtil.post(url, headers, jsonRequestBody);
            ret = response;
            if (response.statusCode() != 200) {
                logger.error("http return code ["+response.statusCode()+"] " + response.body().toString());
            }
        }else{
            logger.error("Invalid Access Token");
        }
        return ret;
    }


    public static HttpResponse<String> issueEproofAddPDF(SysObj sysObj, EProof eProof) throws Exception {
        HttpResponse<String> ret = null;
        if (ApiUtil.getAccessTokenByClientCredentials(sysObj)) {
            String url = getAPIUrl(sysObj, "/eProofMetadata");

            //Request Body
            HashMap requestBodyJsonMap = new HashMap<>();
            requestBodyJsonMap.put("id", eProof.getUuid());
            //requestBodyJsonMap.put("pdfHash", eProof.getPdfBase64Hash());
            requestBodyJsonMap.put("pdfUrl", eProof.getjEProofConfig().getJSONObject("eproofPDF").getString("pdfUrl"));

            //Request Headers
            List<String> headersArray = new ArrayList<String>();
            headersArray.add("Content-Type");
            headersArray.add("application/json");
            headersArray.add("Authorization");
            headersArray.add("Bearer " + sysObj.getAccessToken());
            String[] headers = headersArray.toArray(new String[0]);

            String jsonRequestBody = gson.toJson(requestBodyJsonMap);
            logger.debug("PUT /eProofMetadata Request Body");
            logger.debug(jsonRequestBody);

            HttpResponse<String> response = httpUtil.put(url, headers, jsonRequestBody);
            ret = response;
            if (response.statusCode() != 200) {
                logger.error("http return code ["+response.statusCode()+"] " + response.body().toString());
            }
        }else{
            logger.error("Invalid Access Token");
        }
        return ret;
    }
    public static HttpResponse<String> issueEproofUpdatePDFHash(SysObj sysObj, EProof eProof) throws Exception {
        HttpResponse<String> ret = null;
        if (ApiUtil.getAccessTokenByClientCredentials(sysObj)) {
            String url = getAPIUrl(sysObj, "/eProofMetadata/pdfHash");

            //Request Body
            HashMap requestBodyJsonMap = new HashMap<>();
            requestBodyJsonMap.put("id", eProof.getUuid());
            requestBodyJsonMap.put("pdfHash", eProof.getPdfBase64Hash());
            //requestBodyJsonMap.put("pdfUrl", eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("expiryDate"));

            //Request Headers
            List<String> headersArray = new ArrayList<String>();
            headersArray.add("Content-Type");
            headersArray.add("application/json");
            headersArray.add("Authorization");
            headersArray.add("Bearer " + sysObj.getAccessToken());
            String[] headers = headersArray.toArray(new String[0]);

            String jsonRequestBody = gson.toJson(requestBodyJsonMap);
            logger.debug("PUT /eProofMetadata/pdfHash Request Body");
            logger.debug(jsonRequestBody);

            HttpResponse<String> response = httpUtil.put(url, headers, jsonRequestBody);
            ret = response;
            if (response.statusCode() != 200) {
                logger.error("http return code ["+response.statusCode()+"] " + response.body().toString());
            }
        }else{
            logger.error("Invalid Access Token");
        }
        return ret;
    }

    public static HttpResponse<String> getEProof(SysObj sysObj, String uuid) throws Exception {
        //JSONObject ret = null;
        HttpResponse<String> ret = null;

        //Check if need to get the correct token
        if (ApiUtil.getAccessTokenByClientCredentials(sysObj)) {
            String url = ApiUtil.getAPIUrl(sysObj, "/eProofMetadata/" + uuid);
            List<String> headersArray = new ArrayList<String>();
            headersArray.add("Content-Type");
            headersArray.add("application/json");
            headersArray.add("Authorization");
            headersArray.add("Bearer " + sysObj.getAccessToken());
            String[] headers = headersArray.toArray(new String[0]);

            HttpResponse<String> response = httpUtil.get(url, headers);
            ret = response;
        }else{
            logger.debug("Cannot get correct token");
        }
        return ret;
    }

    public static HttpResponse<String> shareEProof(SysObj sysObj, EProof eProof) throws Exception {
        HttpResponse<String> ret = null;
        if (ApiUtil.getAccessTokenByClientCredentials(sysObj)) {
            String url = getAPIUrl(sysObj, "/sharedEProof");

            //Request Body
            HashMap requestBodyJsonMap = new HashMap<>();
            requestBodyJsonMap.put("id", eProof.getUuid());
            requestBodyJsonMap.put("version", eProof.getVersion());
            requestBodyJsonMap.put("accessExpiryDate", eProof.getjEProofConfig().getJSONObject("eProofData").getString("expirationDate"));
            requestBodyJsonMap.put("maxAccessCount", eProof.getjEProofConfig().getJSONObject("eproofPDF").getInt("qrcodeMaxAccessCount"));
            requestBodyJsonMap.put("eProof", eProof.getQrCodeEncryptedString());

            //Request Headers
            List<String> headersArray = new ArrayList<String>();
            headersArray.add("Content-Type");
            headersArray.add("application/json");
            headersArray.add("Authorization");
            headersArray.add("Bearer " + sysObj.getAccessToken());
            String[] headers = headersArray.toArray(new String[0]);

            String jsonRequestBody = gson.toJson(requestBodyJsonMap);
            logger.debug("POST /sharedEProof Request Body");
            //logger.debug(jsonRequestBody);

            HttpResponse<String> response = httpUtil.post(url, headers, jsonRequestBody);
            ret = response;
            if (response.statusCode() != 200) {
                logger.error("http return code ["+response.statusCode()+"] " + response.body().toString());
            }
        }else{
            logger.error("Invalid Access Token");
        }
        return ret;
    }

    //------------------
    // Private methods
    public static String getAPIUrl(SysObj sysObj,String str_subpath) throws Exception {
        String str_returl= "";
        String str_env= sysObj.getBdConfig().getJSONObject("api_information").getString("environment");
        switch (str_env){
            case "dev":
                str_returl = getJSONFieldShortcut(sysObj.getSysConfig(),"eproofAPI.env-dev").getString("url");
                break;
            case "uat":
                str_returl = getJSONFieldShortcut(sysObj.getSysConfig(),"eproofAPI.env-uat").getString("url");
                break;
            case "prod":
                str_returl = getJSONFieldShortcut(sysObj.getSysConfig(),"eproofAPI.env-prod").getString("url");
                break;
            default:
                logger.error("Invalid Environment "+str_env+". Please input dev/uat/prod");
                str_returl = "";
                break;
        }
        if (str_subpath.trim().startsWith("/")){
            str_returl = str_returl+ str_subpath.trim();
        }else{
            str_returl = str_returl+ "/" + str_subpath.trim();
        }
        return str_returl;
    }

    public static void setDownloadUrl(SysObj sysObj,EProof eProof ) throws Exception {
        String str_returl_en= "";
        String str_returl_tc= "";
        String str_returl_sc= "";

       String str_env= sysObj.getBdConfig().getJSONObject("api_information").getString("environment");
        switch (str_env){
            case "dev":
                str_returl_en = getJSONFieldShortcut(sysObj.getSysConfig(),"eproofAPI.env-dev").getString("urldownloadeProofen");
                str_returl_tc = getJSONFieldShortcut(sysObj.getSysConfig(),"eproofAPI.env-dev").getString("urldownloadeProoftc");
                str_returl_sc = getJSONFieldShortcut(sysObj.getSysConfig(),"eproofAPI.env-dev").getString("urldownloadeProofsc");
                break;
            case "uat":
                //str_returl = getJSONFieldShortcut(sysObj.getSysConfig(),"eproofAPI.env-uat").getString("urldownloadeProof");
                str_returl_en = getJSONFieldShortcut(sysObj.getSysConfig(),"eproofAPI.env-uat").getString("urldownloadeProofen");
                str_returl_tc = getJSONFieldShortcut(sysObj.getSysConfig(),"eproofAPI.env-uat").getString("urldownloadeProoftc");
                str_returl_sc = getJSONFieldShortcut(sysObj.getSysConfig(),"eproofAPI.env-uat").getString("urldownloadeProofsc");
                break;
            case "prod":
                //str_returl = getJSONFieldShortcut(sysObj.getSysConfig(),"eproofAPI.env-prod").getString("urldownloadeProof");
                str_returl_en = getJSONFieldShortcut(sysObj.getSysConfig(),"eproofAPI.env-prod").getString("urldownloadeProofen");
                str_returl_tc = getJSONFieldShortcut(sysObj.getSysConfig(),"eproofAPI.env-prod").getString("urldownloadeProoftc");
                str_returl_sc = getJSONFieldShortcut(sysObj.getSysConfig(),"eproofAPI.env-prod").getString("urldownloadeProofsc");
                break;
            default:
                logger.error("Invalid Environment "+str_env+". Please input dev/uat/prod");
                str_returl_en = "";
                str_returl_tc = "";
                str_returl_sc = "";
                break;
        }

        eProof.setDownloadURLen(str_returl_en.trim().replace("[UUID]",eProof.getUuid()).replace("[VERSION]",String.valueOf(eProof.getVersion())));
        eProof.setDownloadURLtc(str_returl_tc.trim().replace("[UUID]",eProof.getUuid()).replace("[VERSION]",String.valueOf(eProof.getVersion())));
        eProof.setDownloadURLsc(str_returl_sc.trim().replace("[UUID]",eProof.getUuid()).replace("[VERSION]",String.valueOf(eProof.getVersion())));
        //return str_returl;
    }
}
