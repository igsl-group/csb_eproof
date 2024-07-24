package hk.gov.ogcio.eproof.controller;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import hk.gov.ogcio.eproof.controller.pdf.BasicSignerOptions;
import hk.gov.ogcio.eproof.controller.pdf.SignerLogic;
import hk.gov.ogcio.eproof.controller.pdf.ssl.SSLInitializer;
import hk.gov.ogcio.eproof.controller.pdf.types.CertificationLevel;
import hk.gov.ogcio.eproof.controller.pdf.types.HashAlgorithm;
import hk.gov.ogcio.eproof.controller.pdf.types.PDFEncryption;
import hk.gov.ogcio.eproof.controller.pdf.types.PrintRight;
import hk.gov.ogcio.eproof.model.EProof;
import hk.gov.ogcio.eproof.model.SysObj;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static hk.gov.ogcio.eproof.controller.CommonUtil.*;
import static hk.gov.ogcio.eproof.controller.SysUtil.getJSONFieldShortcut;

public final class Issuer {
    private static final Logger logger = LogManager.getLogger(Issuer.class);

    public static void listEProofTypes(SysObj sysObj) throws Exception {
        JSONObject jEproofTypeList = ApiUtil.getEProofType(sysObj);
        if (jEproofTypeList != null ){
            logger.debug("status=" + jEproofTypeList.getString("status") );

            JSONArray ja = jEproofTypeList.getJSONArray("data");
            int len = ja.length();
            ArrayList<String> files_names = new ArrayList<>();
            Boolean isValueFound = false;
            for(int j=0; j<len; j++){
                JSONObject json = ja.getJSONObject(j);
                logger.debug("id=" + json.getString("id") );
                logger.debug("code=" + json.getString("code") );
                logger.debug("description=" + json.getString("description") );
            }
        }
    }

    //SEQ-ISS-003
    public static void issueEProof(SysObj sysObj,String configfile) throws Exception {
        Boolean isEproofValid = true;
        HttpResponse<String> httpResponse = null;
        logger.info("Config file in:" + configfile);

        String configfileFullPath = sysObj.getRootPath()
                + File.separator
                + getJSONFieldShortcut(sysObj.getSysConfig(),"subfolder_structure.eproof").getString("foldername")
                + File.separator
                + configfile
                ;
        logger.info("configfileFullPath= " + configfileFullPath);
        EProof eProof = new EProof();
        JSONObject jConfig = SysUtil.loadJSONFile(configfileFullPath);
        eProof.setjEProofConfig(jConfig);
        logger.info("jConfig.eProofTypeId= " + eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("eProofTypeId"));


        validateConfig(sysObj,eProof);

            // ISSUE eProof  (POST /eProofMetadata
        // return the URL if OTP is used
        calcHKICHash(sysObj,eProof);
        constructeProofDataAndCalcVCHash(sysObj,eProof);

        if (sysObj.getTESTMode()){
            eProof.setUuid("UUID-FOR-TESTING");
            eProof.setVersion(1);
        }else {
            httpResponse = ApiUtil.issueEproof(sysObj,eProof);
            if (httpResponse!=null && httpResponse.statusCode() == 200) {
                JSONObject jret = new JSONObject(httpResponse.body().toString());
                if (jret.getString("status").equals("Successful")){
                    eProof.setUuid(jret.getJSONObject("data").getString("id"));
                    eProof.setVersion(jret.getJSONObject("data").getInt("version"));
                    ApiUtil.setDownloadUrl(sysObj,eProof);
                }
            }
        }

        //Write the eProof data file out
        String eproofDataOutputPath = sysObj.getRootPath()
                + File.separator
                + getJSONFieldShortcut(sysObj.getSysConfig(),"subfolder_structure.eproof").getString("foldername")
                + File.separator
                //+ eProof.getjEProofConfig().getString("eProofData")
                + eProof.getjEProofConfig().getJSONObject("eProofData").getString("eProofOutputDataStoreOnBDSystem")
                ;
        eproofDataOutputPath = eproofDataOutputPath.replace("[[UUID]]",eProof.getUuid());
        eProof.seteProofDataOutputLocation(eproofDataOutputPath);

        BufferedWriter bw = new BufferedWriter(new FileWriter(eproofDataOutputPath));
        bw.write(eProof.geteProofDataOutput());
        bw.close();
        logger.info("UUID= " + eProof.getUuid());

        if(eProof.getjEProofConfig().getJSONObject("eproofPDF").getBoolean("hasEProofPDF")) {
            //Must Get the QR Code as it will embedded in the PDF
            encryptEProofData(sysObj,eProof);
            if (sysObj.getTESTMode()){
                eProof.setQrCodeToken(UUID.randomUUID().toString());
                logger.info("token= " + eProof.getQrCodeToken());
            }else {
                httpResponse = ApiUtil.shareEProof(sysObj,eProof);
                if (httpResponse != null && httpResponse.statusCode() == 200) {
                    logger.info("Generate QR Code Success");
                    JSONObject jret = new JSONObject(httpResponse.body().toString());
                    eProof.setQrCodeToken(jret.getJSONObject("data").getString("token"));
                }
            }

            constructQRCodeString(sysObj,eProof);
            JSONObject eproofPDF = eProof.getjEProofConfig().getJSONObject("eproofPDF");
            if (eproofPDF.has("qrCodeWidth") && eproofPDF.has("qrCodeHeight")
                    && !eproofPDF.isNull("qrCodeWidth") && !eproofPDF.isNull("qrCodeHeight")){
                eProof.setQrCodeWidth(eProof.getjEProofConfig().getJSONObject("eproofPDF").getInt("qrCodeWidth"));
                eProof.setQrCodeHeight(eProof.getjEProofConfig().getJSONObject("eproofPDF").getInt("qrCodeHeight"));
            }
            getPdfOutputNameStg(sysObj,eProof);
            // PUT the pdf (PUT /eProofMetadata/pdfHash
            // Return the URL for downloading
            pdfGeneratePDF(sysObj,eProof);
            pdfAddMetaData(sysObj,eProof);
            pdfSigning(sysObj,eProof);
            calcPDFHash(sysObj,eProof);
            deleteStg();
            if(!sysObj.getTESTMode()){
                httpResponse = ApiUtil.issueEproofAddPDF(sysObj,eProof);
                if (httpResponse.statusCode() == 200) {
                    logger.info("Write PDF URL success");

                    httpResponse = ApiUtil.issueEproofUpdatePDFHash(sysObj,eProof);
                    if (httpResponse.statusCode() == 200) {
                        logger.info("Write PDF Hash success");
                    }
                }else{
                    logger.error("Error while writing the PDF hash. Error code["
                            + httpResponse.statusCode() + "] " + httpResponse.body().toString());
                }
            }
        }
        logger.info("==================================");
        logger.info("ISSUE-EProof Success");
        logger.info("eProofTypeId=" + eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("eProofTypeId"));
        logger.info("templateCode=" + eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("templateCode"));
        logger.info("eProofId=" + eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("eProofId"));
        logger.info("UUID=" + eProof.getUuid());
        logger.info("eProofOutput Path=" + eProof.geteProofDataOutputLocation());
        logger.info("QR Code String = " + eProof.getQrCodeString());
        if (eProof.getjEProofConfig().getJSONObject("eproofPDF").getBoolean("hasEProofPDF")){
            logger.info("Generated PDF=" + eProof.getPdflocation());
        }
        if(!sysObj.getTESTMode()){
            //Validate the eProof
            httpResponse = ApiUtil.getEProof(sysObj,eProof.getUuid());
            if (httpResponse.statusCode() == 200) {
                logger.debug("Read EProof Success. Data as ");
                logger.debug(httpResponse.body().toString());
                logger.info("Download URL- EN=" + eProof.getDownloadURLen());
                logger.info("Download URL- TC=" + eProof.getDownloadURLtc());
                logger.info("Download URL- SC=" + eProof.getDownloadURLsc());
            }else{
                logger.error("Error while reading EProof["
                        + httpResponse.statusCode() + "] " + httpResponse.body().toString());
            }
        }
    }

    public static void issueEProof2(SysObj sysObj,String configfile) throws Exception {
        Boolean isEproofValid = true;
        HttpResponse<String> httpResponse = null;
        logger.info("Config file in:" + configfile);

        String configfileFullPath = sysObj.getRootPath()
                + File.separator
                + getJSONFieldShortcut(sysObj.getSysConfig(),"subfolder_structure.eproof").getString("foldername")
                + File.separator
                + configfile
                ;
        logger.info("configfileFullPath= " + configfileFullPath);
        EProof eProof = new EProof();
        JSONObject jConfig = SysUtil.loadJSONFile(configfileFullPath);
        eProof.setjEProofConfig(jConfig);
        logger.info("jConfig.eProofTypeId= " + eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("eProofTypeId"));


        validateConfig(sysObj,eProof);

        // ISSUE eProof  (POST /eProofMetadata
        // return the URL if OTP is used
        calcHKICHash(sysObj,eProof);
        constructeProofDataAndCalcVCHash(sysObj,eProof);

        if (sysObj.getTESTMode()){
            eProof.setUuid("UUID-FOR-TESTING");
            eProof.setVersion(1);
        }else {
            httpResponse = ApiUtil.issueEproof(sysObj,eProof);
            if (httpResponse!=null && httpResponse.statusCode() == 200) {
                JSONObject jret = new JSONObject(httpResponse.body().toString());
                if (jret.getString("status").equals("Successful")){
                    eProof.setUuid(jret.getJSONObject("data").getString("id"));
                    eProof.setVersion(jret.getJSONObject("data").getInt("version"));
                    ApiUtil.setDownloadUrl(sysObj,eProof);
                }
            }
        }

        //Write the eProof data file out
        String eproofDataOutputPath = sysObj.getRootPath()
                + File.separator
                + getJSONFieldShortcut(sysObj.getSysConfig(),"subfolder_structure.eproof").getString("foldername")
                + File.separator
                //+ eProof.getjEProofConfig().getString("eProofData")
                + eProof.getjEProofConfig().getJSONObject("eProofData").getString("eProofOutputDataStoreOnBDSystem")
                ;
        eproofDataOutputPath = eproofDataOutputPath.replace("[[UUID]]",eProof.getUuid());
        eProof.seteProofDataOutputLocation(eproofDataOutputPath);

        BufferedWriter bw = new BufferedWriter(new FileWriter(eproofDataOutputPath));
        bw.write(eProof.geteProofDataOutput());
        bw.close();
        logger.info("UUID= " + eProof.getUuid());

        if(eProof.getjEProofConfig().getJSONObject("eproofPDF").getBoolean("hasEProofPDF")) {
            //Must Get the QR Code as it will embedded in the PDF
            encryptEProofData(sysObj,eProof);
            if (sysObj.getTESTMode()){
                eProof.setQrCodeToken(UUID.randomUUID().toString());
                logger.info("token= " + eProof.getQrCodeToken());
            }else {
                httpResponse = ApiUtil.shareEProof(sysObj,eProof);
                if (httpResponse != null && httpResponse.statusCode() == 200) {
                    logger.info("Generate QR Code Success");
                    JSONObject jret = new JSONObject(httpResponse.body().toString());
                    eProof.setQrCodeToken(jret.getJSONObject("data").getString("token"));
                }
            }

            constructQRCodeString(sysObj,eProof);
            JSONObject eproofPDF = eProof.getjEProofConfig().getJSONObject("eproofPDF");
            if (eproofPDF.has("qrCodeWidth") && eproofPDF.has("qrCodeHeight")
                    && !eproofPDF.isNull("qrCodeWidth") && !eproofPDF.isNull("qrCodeHeight")){
                eProof.setQrCodeWidth(eProof.getjEProofConfig().getJSONObject("eproofPDF").getInt("qrCodeWidth"));
                eProof.setQrCodeHeight(eProof.getjEProofConfig().getJSONObject("eproofPDF").getInt("qrCodeHeight"));
            }
            getPdfOutputNameStg(sysObj,eProof);
            // PUT the pdf (PUT /eProofMetadata/pdfHash
            // Return the URL for downloading
            pdfGeneratePDF(sysObj,eProof);
            pdfAddMetaData(sysObj,eProof);
            pdfSigning(sysObj,eProof);
            calcPDFHash(sysObj,eProof);
            deleteStg();
            if(!sysObj.getTESTMode()){
                httpResponse = ApiUtil.issueEproofAddPDF(sysObj,eProof);
                if (httpResponse.statusCode() == 200) {
                    logger.info("Write PDF URL success");

                    httpResponse = ApiUtil.issueEproofUpdatePDFHash(sysObj,eProof);
                    if (httpResponse.statusCode() == 200) {
                        logger.info("Write PDF Hash success");
                    }
                }else{
                    logger.error("Error while writing the PDF hash. Error code["
                            + httpResponse.statusCode() + "] " + httpResponse.body().toString());
                }
            }
        }
        logger.info("==================================");
        logger.info("ISSUE-EProof Success");
        logger.info("eProofTypeId=" + eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("eProofTypeId"));
        logger.info("templateCode=" + eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("templateCode"));
        logger.info("eProofId=" + eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("eProofId"));
        logger.info("UUID=" + eProof.getUuid());
        logger.info("eProofOutput Path=" + eProof.geteProofDataOutputLocation());
        logger.info("QR Code String = " + eProof.getQrCodeString());
        if (eProof.getjEProofConfig().getJSONObject("eproofPDF").getBoolean("hasEProofPDF")){
            logger.info("Generated PDF=" + eProof.getPdflocation());
        }
        if(!sysObj.getTESTMode()){
            //Validate the eProof
            httpResponse = ApiUtil.getEProof(sysObj,eProof.getUuid());
            if (httpResponse.statusCode() == 200) {
                logger.debug("Read EProof Success. Data as ");
                logger.debug(httpResponse.body().toString());
                logger.info("Download URL- EN=" + eProof.getDownloadURLen());
                logger.info("Download URL- TC=" + eProof.getDownloadURLtc());
                logger.info("Download URL- SC=" + eProof.getDownloadURLsc());
            }else{
                logger.error("Error while reading EProof["
                        + httpResponse.statusCode() + "] " + httpResponse.body().toString());
            }
        }
    }


    public static void calcPDFHash(SysObj sysObj, EProof eProof) throws Exception {
        String strPDFOut = sysObj.getRootPath()
                + File.separator
                + getJSONFieldShortcut(sysObj.getSysConfig(),"subfolder_structure.eproof").getString("foldername")
                + File.separator
                + eProof.getjEProofConfig().getJSONObject("eproofPDF").getString("pdfOutputName");
        if(eProof.getUuid() != null) {
            strPDFOut = strPDFOut.replace("[[UUID]]", eProof.getUuid());
        }
        logger.debug("pdfAddMetaData from -> " + strPDFOut);

        File pdfFile = new File(strPDFOut);
        byte[] pdfBytes = readBytesFromFile(pdfFile, MessageDigest.getInstance("SHA-256"));
        String pdfBase64Hash = base64Encode(pdfBytes);
        eProof.setPdfBase64Hash(pdfBase64Hash);
    }
    public static void calcHKICHash(SysObj sysObj, EProof eProof) throws Exception {
        String hkicAndSalt = eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("hkic")
                +eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("salt");
        byte[] hkicBytes = readBytesFromString(hkicAndSalt, MessageDigest.getInstance("SHA-256"));
        String hkicBase64Hash = base64Encode(hkicBytes);
        eProof.setHkicBase64Hash(hkicBase64Hash);
    }

    public static void constructeProofDataAndCalcVCHash(SysObj sysObj, EProof eProof) throws Exception {
        //String
        String eproofDatafileFullPath = sysObj.getRootPath()
                + File.separator
                + getJSONFieldShortcut(sysObj.getSysConfig(),"subfolder_structure.eproof").getString("foldername")
                + File.separator
                //+ eProof.getjEProofConfig().getString("eProofData")
                + eProof.getjEProofConfig().getJSONObject("eProofData").getString("eProofSourceData")
                ;

        String eProofDataJson = CommonUtil.readFileAsString(eproofDatafileFullPath);
        TreeMap eProofData = gson.fromJson(eProofDataJson, TreeMap.class);

        Map systemJsonMap = new TreeMap<>();
        systemJsonMap.put("hkicHash", eProof.getHkicBase64Hash()); //hkicBase64Hash);
        systemJsonMap.put("expirationDate", eProof.getjEProofConfig().getJSONObject("eProofData").getString("expirationDate"));

        Map credentialSubjectJsonMap = new TreeMap<>();
        credentialSubjectJsonMap.put("display", eProofData);
        credentialSubjectJsonMap.put("system", systemJsonMap);

        //vc.json
        Map vcJsonMap = new TreeMap<>();
        vcJsonMap.put("@context",new ArrayList<>(Arrays.asList("https://www.w3.org/2018/credentials/v1")) );
        vcJsonMap.put("type", new ArrayList<>(Arrays.asList("VerifiableCredential")) );
        //vcJsonMap.put("issuer", "did:eproof:75ed43c1-d1f2-4cf2-9b72-5e7792989d46" );
        vcJsonMap.put("issuer", sysObj.getBdConfig().getJSONObject("bd_informantion").getString("issuerDID") );
        //vcJsonMap.put("issuanceDate", "2010-01-01T19:23:24Z" );
        //LocalDateTime now = LocalDateTime.now();
        //String formattedDate = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        vcJsonMap.put("issuanceDate", eProof.getjEProofConfig().getJSONObject("eProofData").getString("issuanceDate") ); // Update as UTC format
        vcJsonMap.put("credentialSubject",credentialSubjectJsonMap);

        //vc proof
        Map vcProofJsonMap = new TreeMap<>();
        vcProofJsonMap.put(  "type", "SHA256withRSA");
        //vcProofJsonMap.put(  "created", "2023-04-20T00:15:39.910Z");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String formattedDate = now.format(formatter);
        vcProofJsonMap.put(  "created", formattedDate);
        //vcProofJsonMap.put(  "verificationMethod", "did:eproof:75ed43c1-d1f2-4cf2-9b72-5e7792989d46#key-1");
        vcProofJsonMap.put(  "verificationMethod", sysObj.getBdConfig().getJSONObject("bd_informantion").getString("verificationMethod"));
        vcProofJsonMap.put(  "proofPurpose", "assertionMethod");
//        String proofValue = signatureWithPrivateKey(gson.toJson(vcJsonMap),"C:\\Users\\user\\Desktop\\OGCIO\\pdf-utilities\\src\\test\\java\\resources\\privateKey.pem");
        //Louis Comment, for debug
        // String privateKeyPath =ApiUtil.class.getClassLoader().getResource("privateKey.pem").getPath().substring(1);

        //logger.debug("vcJsonMap=");
        //logger.debug(gson.toJson(vcJsonMap));

        String privateKeyPath = sysObj.getRootPath()
                + File.separator
                + getJSONFieldShortcut(sysObj.getSysConfig(),"subfolder_structure.bdconfig").getString("foldername")
                + File.separator
                + sysObj.getBdConfig().getJSONObject("bd_informantion").getString("privateKey");
                //+ "privateKey.pem";

        String proofValue = signatureWithPrivateKey(gson.toJson(vcJsonMap),privateKeyPath);
        vcProofJsonMap.put(  "proofValue", proofValue);

        vcJsonMap.put("proof", vcProofJsonMap);

        //hash vc
        String vcString = gson.toJson(vcJsonMap);
        byte[] vcBytes = readBytesFromString(vcString, MessageDigest.getInstance("SHA-256"));
        String vcBase64Hash = base64Encode(vcBytes);

        eProof.seteProofDataOutput(vcString);
        eProof.setVcBase64Hash(vcBase64Hash);
    }

    public static void encryptEProofData(SysObj sysObj, EProof eProof) throws Exception {
        logger.debug("pdfGenerateQRCode");
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[256 / 8]; //Key in 256bit
        byte[] ivBytes = new byte[128 / 8]; //IV in 128bit
        random.nextBytes(keyBytes);
        random.nextBytes(ivBytes);

        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

        byte[] encrypted = cipher.doFinal(eProof.geteProofDataOutput().getBytes());
        String encryptedBase64 = base64Encode(encrypted);

        eProof.setQrCodeEncryptedString(encryptedBase64);
        eProof.setQrCodeEncryptKey(Base64.getEncoder().encodeToString(keyBytes));
        eProof.setQrCodeEncryptInitVector(Base64.getEncoder().encodeToString(ivBytes));

    }

    public static void constructQRCodeString(SysObj sysObj, EProof eProof) throws Exception {

        Map vcJsonMapData = new TreeMap<>();
        vcJsonMapData.put("shared_eproof_uuid", eProof.getUuid());
        vcJsonMapData.put("key", eProof.getQrCodeEncryptKey());
        vcJsonMapData.put("initVector", eProof.getQrCodeEncryptInitVector());
        vcJsonMapData.put("jwt", eProof.getQrCodeToken());


        Map vcJsonMap = new TreeMap<>();
        vcJsonMap.put("type_id", "2c"); // 1: Download     2a: Verification (time-limited)   2b: Verification (face-to-face) 2c: Verification (PDF)
        vcJsonMap.put("data", vcJsonMapData);
        String vcString = gson.toJson(vcJsonMap);
        eProof.setQrCodeString(vcString);
    }
    private static String PDF_OUTPUT_NAME_STG1;
    private static String PDF_OUTPUT_NAME_STG2;
    private static void getPdfOutputNameStg(SysObj sysObj,EProof eProof) throws Exception{
        PDF_OUTPUT_NAME_STG1 = sysObj.getRootPath()
                + File.separator
                + getJSONFieldShortcut(sysObj.getSysConfig(),"subfolder_structure.eproof").getString("foldername")
                + File.separator
                + eProof.getjEProofConfig().getJSONObject("eproofPDF").getString("pdfOutputName");
        if(eProof.getUuid() != null) {
            PDF_OUTPUT_NAME_STG1 = PDF_OUTPUT_NAME_STG1.replace("[[UUID]]", eProof.getUuid() + "_stgA");
        }
        PDF_OUTPUT_NAME_STG2 = sysObj.getRootPath()
                + File.separator
                + getJSONFieldShortcut(sysObj.getSysConfig(),"subfolder_structure.eproof").getString("foldername")
                + File.separator
                + eProof.getjEProofConfig().getJSONObject("eproofPDF").getString("pdfOutputName");
        if(eProof.getUuid() != null) {
            PDF_OUTPUT_NAME_STG2 = PDF_OUTPUT_NAME_STG2.replace("[[UUID]]", eProof.getUuid() + "_stgB");
        }
    }
    public static void pdfGeneratePDF(SysObj sysObj, EProof eProof) throws Exception {
        logger.debug("pdfGeneratePDF-> Hello World");
        Document document = new Document();
        try {
            logger.debug("pdfGeneratePDF to -> " + PDF_OUTPUT_NAME_STG1);

            String strDocxIn = sysObj.getRootPath()
                    + File.separator
                    + eProof.getjEProofConfig().getJSONObject("eproofPDF").getString("wordTemplatePath");

            PDFUtil.convertDocxToPdf(strDocxIn,PDF_OUTPUT_NAME_STG1,eProof);
        } catch (DocumentException de) {
            System.err.println(de.getMessage());
        }
        document.close();
    }

    public static void pdfAddMetaData(SysObj sysObj, EProof eProof) throws Exception {
        //try {
            logger.debug("pdfAddMetaData from -> " + PDF_OUTPUT_NAME_STG1);
            logger.debug("pdfAddMetaData from -> " + PDF_OUTPUT_NAME_STG2);

            // Open the existing PDF file
            PdfReader reader = new PdfReader(PDF_OUTPUT_NAME_STG1);

            // Create a PdfStamper object to modify the PDF
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(PDF_OUTPUT_NAME_STG2));

            // Get the document information dictionary
            HashMap info = new HashMap<>(reader.getInfo());

            // Set the metadata fields
            info.put("Title", eProof.getjEProofConfig().getJSONObject("eproofPDF").getString("title"));
            info.put("Author", sysObj.getBdConfig().getJSONObject("bd_informantion").getString("en_name"));
            //info.put("Subject", "PDF Metadata Example");
            //info.put("Keywords", "PDF, Metadata, Example");
            info.put("Creator", sysObj.getBdConfig().getJSONObject("bd_informantion").getString("en_name"));
            info.put("Producer", sysObj.getBdConfig().getJSONObject("bd_informantion").getString("en_name"));

            //Compile Keywords
            Map vcJsonMap = new TreeMap<>();
            vcJsonMap.put("uuid", eProof.getUuid());
            vcJsonMap.put("version", eProof.getVersion());
            vcJsonMap.put("qrCode", CommonUtil.toMap(new JSONObject(eProof.getQrCodeString())));
            vcJsonMap.put("verificationMethodId", sysObj.getBdConfig().getJSONObject("bd_informantion").getString("issuerDID"));
            //vcJsonMap.put("token", eProof.getQrCodeToken());
            //vcJsonMap.put("encryptionkey", eProof.getQrCodeEncryptKey());
            //vcJsonMap.put("encryptionkiv", eProof.getQrCodeEncryptInitVector());
            String vcString = gson.toJson(vcJsonMap);
            info.put("Keywords", CommonUtil.base64Encode(vcString));
            stamper.setMoreInfo(info);
            // Close the PdfStamper object
            stamper.close();
            reader.close();
        //} catch (IOException | DocumentException e) {
        //    e.printStackTrace();
        //}

    }

    public static void pdfSigning(SysObj sysObj, EProof eProof) throws Exception {
        String certPath = sysObj.getRootPath()
                + File.separator
                + getJSONFieldShortcut(sysObj.getSysConfig(),"subfolder_structure.bdconfig").getString("foldername")
                + File.separator
                + sysObj.getBdConfig().getJSONObject("bd_informantion").getString("p12certfilename");

        logger.debug("pdfAddMetaData from -> " + PDF_OUTPUT_NAME_STG2);


        String strPDFOut = sysObj.getRootPath()
                + File.separator
                + getJSONFieldShortcut(sysObj.getSysConfig(),"subfolder_structure.eproof").getString("foldername")
                + File.separator
                + eProof.getjEProofConfig().getJSONObject("eproofPDF").getString("pdfOutputName");
        if(eProof.getUuid() != null) {
            strPDFOut = strPDFOut.replace("[[UUID]]", eProof.getUuid());
        }
        logger.debug("pdfAddMetaData from -> " + strPDFOut);


        BasicSignerOptions options = new BasicSignerOptions();

        try {
            SSLInitializer.init();
        } catch (Exception e) {
            logger.warn("Unable to re-configure SSL layer", e);
        }

        options.setKsType("PKCS12");
        options.setAdvanced(true);
        //options.setKsFile("C:\\Users\\user\\Desktop\\OGCIO\\cert\\server.p12");
        options.setKsFile(certPath);
        options.setKsPasswd(sysObj.getBdConfig().getJSONObject("bd_informantion").getString("p12certSecret"));
        options.setStorePasswords(false);
        //options.setInFile("C:\\Users\\user\\Desktop\\OGCIO\\pdf-utilities\\src\\test\\java\\resources\\HK_Registered_Gas_Installer_Card_Desktop_Demo_v1.pdf");
        options.setInFile(PDF_OUTPUT_NAME_STG2);
        options.setPdfEncryption(PDFEncryption.PASSWORD);
        //options.setPdfOwnerPwd("abc123");
        if (eProof.getjEProofConfig().getJSONObject("eproofPDF").getString("pdfEncryptionOwnerPassword").length()>0) {
            options.setPdfOwnerPwd(eProof.getjEProofConfig().getJSONObject("eproofPDF").getString("pdfEncryptionOwnerPassword"));
        }else{
            options.setPdfOwnerPwd("");
        }

        //if (eProof.getjEProofConfig().getJSONObject("eproofPDF").getString("pdfEncryptionUserPassword").length()>0) {
        //    options.setPdfUserPwd(eProof.getjEProofConfig().getJSONObject("eproofPDF").getString("pdfEncryptionUserPassword"));
        //}else {
            options.setPdfUserPwd(""); //Must be Empty. Otherwise cannot validate the PDF
        //}
        options.setPdfEncryptionCertFile("");
        //options.setOutFile("C:\\Users\\user\\Desktop\\OGCIO\\pdf-utilities\\src\\test\\java\\resources\\HK_Registered_Gas_Installer_Card_Desktop_Demo_v1_signed.pdf");
        options.setOutFile(strPDFOut);
        options.setCertLevel(CertificationLevel.CERTIFIED_NO_CHANGES_ALLOWED);
        options.setHashAlgorithm(HashAlgorithm.SHA256);
        options.setAppend(false);
        options.setRightPrinting(PrintRight.ALLOW_PRINTING);
        options.setRightCopy(false);
        options.setRightAssembly(false);
        options.setRightFillIn(false);
        options.setRightScreenReaders(false);
        options.setRightModifyAnnotations(false);
        options.setRightModifyContents(false);
        options.setVisible(false);
        SignerLogic signerLogic = new SignerLogic(options);
        boolean success = signerLogic.signFile();

        eProof.setPdflocation(strPDFOut);
    }

    private static void deleteStg(){
        File stg1 = new File(PDF_OUTPUT_NAME_STG1);
        File stg2 = new File(PDF_OUTPUT_NAME_STG2);
        if(stg1.exists()){
            stg1.delete();
        }else {
            throw new IllegalArgumentException("File "+stg1+" is not exists");
        }
        if(stg2.exists()){
            stg2.delete();
        }else {
            throw new IllegalArgumentException("File "+stg2+" is not exists");
        }
    }

    // Local function
    public static void validateConfig(SysObj sysObj,EProof eProof) throws Exception {
        Boolean isEproofValid = true;
        //mandatory fields
        validateConfigField(eProof,"eProofId");
        validateConfigField(eProof,"eProofTypeId");
        validateConfigField(eProof,"templateCode");
        validateConfigField(eProof,"dataUrl");
        validateConfigField(eProof,"downloadExpiryDate");
        validateConfigField(eProof,"salt");

        //Validation of the input json
        if (("01").equals(eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("authMethod"))){
            //OTP Mode
            validateConfigField(eProof,"otpUrl");
        }else if (("02").equals(eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("authMethod")) ){
            //iAM Smart Mode
            validateConfigField(eProof,"hkic");
        }else if (("03").equals(eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("authMethod")) ){
            //iAM Smart + OTP Mode
            validateConfigField(eProof,"otpUrl");
            validateConfigField(eProof,"hkic");
        }else{
            throw new IllegalArgumentException("Invalid authMethod: "
                    + eProof.getjEProofConfig().getJSONObject("eproofMeta").getString("authMethod"));
        }
    }

    public static void validateConfigField(EProof eProof, String fieldname) throws Exception {
        if (eProof.getjEProofConfig().getJSONObject("eproofMeta").getString(fieldname) == null){
            throw new IllegalArgumentException("Field "+fieldname+" Cannot be null");
        }else if (eProof.getjEProofConfig().getJSONObject("eproofMeta").getString(fieldname).length() == 0){
            throw new IllegalArgumentException("Field "+fieldname+" Cannot be empty");
        }
    }

    //--------------------------------------------
    //Debug function
    public static void testconfig(SysObj sysObj) throws Exception {

        String str_result = "";

        str_result += "------------------------------------- \n";
        str_result += "1) B/D Config File (" + getJSONFieldShortcut(sysObj.getSysConfig(),"subfolder_structure.bdconfig.files[name=bdconfigfile]")
                .getString("filename") + ") \n";
        str_result += "------------------------------------- \n";

        //str_result += "B/D Name (English): " + sysObj.getBdConfig().getJSONObject("bd_informantion").getString("en_name") + "\n";
        //str_result += "B/D Name (Trad Chinese): " + sysObj.getBdConfig().getJSONObject("bd_informantion").getString("zh_name") + "\n";
        //str_result += "B/D Name (Simplified Chinese): " + sysObj.getBdConfig().getJSONObject("bd_informantion").getString("sc_name") + "\n";//

        str_result += "environment: " + sysObj.getBdConfig().getJSONObject("api_information").getString("environment") + "\n";
        str_result += "clientId: " + sysObj.getBdConfig().getJSONObject("api_information").getString("clientId") + "\n";
        str_result += "clientSecret: " + sysObj.getBdConfig().getJSONObject("api_information").getString("clientSecret") + "\n";

        str_result += "------------------------------------- \n";
        str_result += "2) Test Access Token ";
        str_result += "------------------------------------- \n";
        if (ApiUtil.getAccessTokenByClientCredentials(sysObj)){
            str_result += "Success";
        }else{
            str_result += "Fail";
        }
        logger.info(str_result);
    }
}
