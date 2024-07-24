package hk.gov.ogcio.eproof.model;

import org.json.JSONObject;

public final class EProof {
    JSONObject jEProofConfig;

    String hkicBase64Hash;


    String eProofDataOutput;


    String eProofDataOutputLocation;
    String vcBase64Hash;
    String pdfBase64Hash;

    String uuid;
    int version;

    String downloadURLen;
    String downloadURLtc;
    String downloadURLsc;
    String pdflocation;

    String qrCodeEncryptKey;

    String qrCodeEncryptInitVector;
    String qrCodeEncryptedString;

    String qrCodeToken;

    String qrCodeString;

    int qrCodeWidth;
    int qrCodeHeight;

    public int getQrCodeWidth() {
        return qrCodeWidth;
    }

    public void setQrCodeWidth(int qrCodeWidth) {
        this.qrCodeWidth = qrCodeWidth;
    }

    public int getQrCodeHeight() {
        return qrCodeHeight;
    }

    public void setQrCodeHeight(int qrCodeHeight) {
        this.qrCodeHeight = qrCodeHeight;
    }

    public String getQrCodeString() {
        return qrCodeString;
    }

    public void setQrCodeString(String qrCodeString) {
        this.qrCodeString = qrCodeString;
    }

    public String getQrCodeToken() {
        return qrCodeToken;
    }

    public void setQrCodeToken(String qrCodeToken) {
        this.qrCodeToken = qrCodeToken;
    }


    public String getQrCodeEncryptKey() {
        return qrCodeEncryptKey;
    }

    public void setQrCodeEncryptKey(String qrCodeEncryptKey) {
        this.qrCodeEncryptKey = qrCodeEncryptKey;
    }

    public String getQrCodeEncryptInitVector() {
        return qrCodeEncryptInitVector;
    }

    public void setQrCodeEncryptInitVector(String qrCodeEncryptInitVector) {
        this.qrCodeEncryptInitVector = qrCodeEncryptInitVector;
    }

    public String getQrCodeEncryptedString() {
        return qrCodeEncryptedString;
    }

    public void setQrCodeEncryptedString(String qrCodeEncryptedString) {
        this.qrCodeEncryptedString = qrCodeEncryptedString;
    }

    public String geteProofDataOutputLocation() {
        return eProofDataOutputLocation;
    }

    public void seteProofDataOutputLocation(String eProofDataOutputLocation) {
        this.eProofDataOutputLocation = eProofDataOutputLocation;
    }
    public String geteProofDataOutput() {
        return eProofDataOutput;
    }

    public void seteProofDataOutput(String eProofDataOutput) {
        this.eProofDataOutput = eProofDataOutput;
    }
    public String getDownloadURLen() {
        return downloadURLen;
    }

    public void setDownloadURLen(String downloadURLen) {
        this.downloadURLen = downloadURLen;
    }

    public String getDownloadURLtc() {
        return downloadURLtc;
    }

    public void setDownloadURLtc(String downloadURLtc) {
        this.downloadURLtc = downloadURLtc;
    }

    public String getDownloadURLsc() {
        return downloadURLsc;
    }

    public void setDownloadURLsc(String downloadURLsc) {
        this.downloadURLsc = downloadURLsc;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getPdflocation() {
        return pdflocation;
    }

    public void setPdflocation(String pdflocation) {
        this.pdflocation = pdflocation;
    }

    /*
        "id": "b5e1f0c5-9306-421b-ac12-6401bf668805",
        "version": 1,
        "eProofId": "AA0001",
        "eProofTypeId": "e1ca8fe0-00d8-4034-9978-08e8f92a1cf5",
        "templateCode": "TD-TT1-1",
        "hkicHash": "bbR/cJKXDBjpUR9oMaSf0Ee7f+624OIAI9GUzWlWkTU=",
        "expiryDate": "2025-11-13T18:19:39.000Z",
        "dataHash": "aG7Gtmt+Qs2eHOErURd6G7IOE1DRKhz1nA/KGqb0dbQ=",
        "pdfHash": "hWvrrBL0ZBFgx/3d/EY6v2WA5q7qljWsdiFs11oSv3E=",
        "authMethod": "03",
        "dataUrl": "http://192.168.8.6:9010/eProof/b5e1f0c5-9306-421b-ac12-6401bf668805/version/1/data",
        "pdfUrl": "http://192.168.8.6:9010/eProof/b5e1f0c5-9306-421b-ac12-6401bf668805/version/1/pdf",
        "otpUrl": "http://192.168.8.6:9010/eProof/b5e1f0c5-9306-421b-ac12-6401bf668805/version/1/otp",
        "downloadMaxCount": 1000,
        "downloadExpiryDate": "2030-04-28T09:46:33.000Z",
        "isRevoked": false,
        "isWithdrawn": false
     */

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public JSONObject getjEProofConfig() {
        return jEProofConfig;
    }

    public void setjEProofConfig(JSONObject jEProofConfig) {
        this.jEProofConfig = jEProofConfig;
    }



    public String getHkicBase64Hash() {
        return hkicBase64Hash;
    }

    public void setHkicBase64Hash(String hkicBase64Hash) {
        this.hkicBase64Hash = hkicBase64Hash;
    }

    public String getVcBase64Hash() {
        return vcBase64Hash;
    }

    public void setVcBase64Hash(String vcBase64Hash) {
        this.vcBase64Hash = vcBase64Hash;
    }

    public String getPdfBase64Hash() {
        return pdfBase64Hash;
    }

    public void setPdfBase64Hash(String pdfBase64Hash) {
        this.pdfBase64Hash = pdfBase64Hash;
    }
}
