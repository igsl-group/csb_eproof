package com.hkgov.csb.eproof.util.EProof;

import cn.hutool.core.codec.Base64Encoder;
import com.google.gson.Gson;
import com.hkgov.csb.eproof.util.CommonUtil;
import io.micrometer.common.util.StringUtils;
import okhttp3.*;
import org.apache.logging.log4j.*;
import org.json.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

@Component
public class EProofUtil {
	private static final Logger logger = LogManager.getLogger(EProofUtil.class);
	private static final Gson GSON = CommonUtil.gson;


	private static EProofConfigProperties config;

	public EProofUtil(EProofConfigProperties config){
		EProofUtil.config = config;
    }


	public enum type{
		personal, organisational
	}

	public static boolean simulation = false;
	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
	// Define a DateTimeFormatter for ISO 8601 format
//	static DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	/*public static void init(String issuerDID, String url, String pdfUrl, String otpUrl, String dataUrl, String clientId, String clientSecret) {
		config = new EProofConfigProperties();
		config.init(issuerDID,  url,  pdfUrl, otpUrl, dataUrl, clientId, clientSecret);
	}*/

	/** This is a demo method to show the usage of eproof module **/
	public static void main(String[] args) throws Exception {
		//example();
		System.out.println(decryptEProofData(
				"P6dsjyF6K0D9/jmnyjW+zGQY0MvzCYRs2bDTppT6Hg4x6+WIazL7LJkLnTnB9GmHByKZ24aUKzrm8X5tQZejfcd+nOhyUMtZglO7pIqgYVvWyIPDTHvPR9TqwoJpBkSMgGrKTkOiGp9R3TICLEbsdjMa1xqMb9HCUP9/F8vcY7RWqXRb69gP7Eckjq5tGVl6f+InUPmCYEAhahC2q5Ft89pN96l3DclF3UeGrYTeDrZ1jEfJywtEbHCmEiJOwt5ZfkNoVcIwiY31qDojiOS0NpA7Xi+Z0T7ZBXaDZhlAhrB9UCIZi2vO1qJy/y+y2hlqgE360oFtG/FQrDYMUwIf1/kTh/YWp1OCTL50Wkzs66PqM9C/UDnJhkReEFJQHmuRawA63cOLgo6tqeCcJVHSZ5ZD1lbjN1jLIRs/tOQRJ7k1XkCbLbmV9TPsSpeGRt7XFz0Yo6077SkiQ9iHbxRFvWTx3P2BMbuMpQWeoa1Z2TMnzMCzOnQc7OuApuKUfqfemE+9OGjAEkawWRHgMOPa0dMpG7BoaM9tNFxcZ5ZDf+mKcOWgRQO9qYH49+PGfrJHDePDAAUnZKwaMOXiTmyLyIUYnhRcXdo4L+o2I2DvASZxvKsRokMj562KkePF11OkSAQH3JNIZs2G9CeZAPyoYZYMZPlyexx++K0doY2VGgXQaMXT2634eAYkBtlN5k+5TuhWa3+cNSf+R7auAnjp2QX3Sqo/pwwe3HtBfoC3Rh3BraDyFKfcgDzvy5Pl+qnz+7Fs0jl2wvtlc73pl3xaOwcY63M21J6BDp3cSfdxDfM2JEIGl54FZSh29Ppu6rZC5A0T1//Afggz1krriFmLVg8VBHIl9Tv5sBsxhFL0xKp0dDXOuD0oPZCpSVUVLtQJi98LLZ9FGIK+auWYBmxMxb113VJE+cRbZh4bT/FVPKOtFzvF/xsSadvWsyMov9S6Q8NgNQ/5E7ieZ1fMdB5mJ3EFYENkP0SGW+qnmHxlB3GGGj6ssA5wIds4tmRASMsUJrU4yQNUOT2XnsTSvxmnSCUGhBPTu6s+fH2LPg9QuyGn/qRIMgt4FOOCqY2fAdzxsBTadbQ8aOrfj28EFYG205xHCTR7/1BMIrwOHgshdqfka+PdcLn7WUbSU86fl3j0km7VNAdNIexXllBmGSxSb6ZvuF1YJVhE0fEiOCq5TUrsi5DcICwo4cYQeOiJkEoQPrQBpliHdG7ptrALNt5OVdppUc8pISp/TiNQyr+oOxj69ReMe7oJbgoUpp9bLR4dqH+T1XFwnaCGKX21OENumdldiY+Ltshn/vhipERgXsctupOHRs3vOXfsQpZuUoTN9jHKN1xaLYv60yHeCRwrvINuVCY6ReV3b2apbTpzOJSeob5+syqSqr7zqSxhdyy301luWJKk/d2Wq1J9TiZ2m8LqIttpmrNyk4EKe39djuJXvSZf6PRSIJ4bmTfNZB6Jvj6zPVexS8sYAEdZ3mOngG7MXsVsjWhdZV+pjqA5VbmjIvdR8cVfjMB1YmxwEHFZh3WdmIQQkP+HXMdOiA3j3AlPWEXnHopPmDucUwfHeSSkiRDd3R+oLal7YDDEFiT1EyuR3ht1oqsMpmTdMeu7eAYrTRablCjopeWf9gBsIcNTWaw5avpeoMCyWxD0Wala9kh0sAWrGK+vGIj6KDPVEKCmyIweEo1MSylM0iqdNfC2g02TaFCsFLOSM+XOvuCCdWoJJoKM7TCNb+6oB3OHxg==",
				"OITjOAL9+6qLhxdzABxlQA==",
				"Cii5Na58AxLYwLBAruVP7xImOnWnT9w4Xtc8SOF0/Z4="));
	}

	private static void example() throws Exception {

		EProofUtil.simulation = false;

//		EProofUtil.init(
//				"did:eproof:7f9fd645-c24e-40db-9b1a-2de920823403",
//				"https://10.148.10.143/api",
//				"http://192.168.8.6:9010/eProof/748de272-40e5-40a8-bd5a-b93c98fe15bd/version/2/otp",
//				"http://192.168.8.6:9010/eProof/748de272-40e5-40a8-bd5a-b93c98fe15bd/version/2/otp",
//				"http://192.168.8.6:9010/eProof/748de272-40e5-40a8-bd5a-b93c98fe15bd/version/2/otp",
//				"FSD001",
//				"bkAGCE@X8s9w!90hPjXq");

		Map<String, String> extraInfo = new HashMap<>();
		extraInfo.put("document_title_en", "document_title_en");
		extraInfo.put("document_title_tc", "document_title_tc");
		extraInfo.put("document_title_sc", "document_title_sc");
		extraInfo.put("eproof_id", "TEST123HAHAHA");
		extraInfo.put("doc_no", "3310");
		extraInfo.put("issue_to_en", "issue_to_en");
		extraInfo.put("issue_to_tc", "issue_to_tc");
		extraInfo.put("issue_to_sc", "issue_to_sc");
		extraInfo.put("premises_addr_en", "premises_addr_en");
		extraInfo.put("premises_addr_tc", "premises_addr_tc");
		extraInfo.put("premises_addr_sc", "premises_addr_sc");
		extraInfo.put("valid_from", "23/1/2024");
		extraInfo.put("valid_to", "23/6/2099");

		String unsignedJson = getUnsignedEproofJson(
				LocalDateTime.now().plusYears(10),
				LocalDateTime.now(),
				"TEST123HAHAHA",
				"FS163",
				1,
				"TEST123HAHAHA","\u9673\u5927\u6587","23/6/2099",
				"TEST123HAHAHA","\u9648\u5927\u6587","23/6/2099",
				"TEST123HAHAHA","Chan Tai Man","23/6/2099",
				extraInfo,
				type.personal
		);

		//TODO sign the unsigned json. if pass as string use gson.toJson
		Scanner scanner = new Scanner(System.in);
		String line = scanner.nextLine();
		String signedJson = "";

		Map registerResult = registerEproof(
				unsignedJson,
				line,
				"igsltkn1",
				"6d9aadd6-5e95-4177-a6bf-263b30663abe",
				1100,
				null,
				""
		);

		String qrCodeString = getQrCodeString(
				(String) registerResult.get("eProofJson"),
				(String) registerResult.get("uuid"),
				(int) registerResult.get("version"),
				LocalDateTime.now(),
				11000
		);


		System.out.println("gson.toJson(registerResult): " + GSON.toJson(registerResult));

		JSONObject qrCodeJson = new JSONObject(qrCodeString);
		System.out.println("qrCodeString: " + qrCodeString);
		System.out.println("qrCodeJson: " + qrCodeJson);

		System.out.println("Keyword: " + getPdfKeyword((String) registerResult.get("uuid"), (int) registerResult.get("version"), "igsltkn1",
				qrCodeString));

		line = scanner.nextLine();

//		System.out.println("Add keyword to metadata and sign the PDF and place to signed.pdf to continue..");
//		Scanner scanner = new Scanner(System.in);
//		String line = scanner.nextLine();

		//String pdfhash = calcPdfHash(new File("blank.pdf"));
		//issueDocument((String) registerResult.get("uuid"), pdfhash);
		issuePdf((String) registerResult.get("uuid"), new File("blank.pdf"));
	}

	public static String calcPdfHash(File file) throws Exception {
		byte[] pdfBytes = CommonUtil.readBytesFromFile(file, MessageDigest.getInstance("SHA-256"));
		return CommonUtil.base64Encode(pdfBytes);
	}

	public static String calcPdfHash(byte[] pdfBytes) throws Exception {
//		byte[] pdfBytes = CommonUtil.readBytesFromFile(file, MessageDigest.getInstance("SHA-256"));
		byte[] updatedBytes = CommonUtil.updateDigestWithByteArray(pdfBytes,MessageDigest.getInstance("SHA-256"));
		return CommonUtil.base64Encode(updatedBytes);
	}

	public static String getPdfKeyword(String uuid, int version, String keyName){
		//Compile Keywords
		Map vcJsonMap = new TreeMap<>();
		vcJsonMap.put("uuid", uuid);
		vcJsonMap.put("version", version);
		vcJsonMap.put("verificationMethodId", config.getIssuerDid() + "#" + keyName);

		String vcString = GSON.toJson(vcJsonMap);
		return Base64Encoder.encode(vcString); //JSON String
	}

	public static String getPdfKeyword(String uuid, int version, String keyName, String qrCode){
		//Compile Keywords
		Map vcJsonMap = new TreeMap<>();
		vcJsonMap.put("uuid", uuid);
		vcJsonMap.put("version", version);
		vcJsonMap.put("verificationMethodId", config.getIssuerDid() + "#" + keyName);
		if(qrCode != null){
			vcJsonMap.put("qrCode", CommonUtil.toMap(new JSONObject(qrCode)));
		}

		String vcString = GSON.toJson(vcJsonMap);
		logger.debug("EProofUtil - getPdfKeyword - vcStirng:" + vcString);
		return Base64Encoder.encode(vcString); //JSON String
	}

	public static void addSigner(String keyName, String publicKeyCert) throws Exception {
		if (simulation) {
			logger.info("This is simulation of addSigner");
			return;
		}
		try (Response httpResponse = ApiUtil.addSigner(config, keyName, publicKeyCert)) {
			checkResponse(httpResponse);
			logger.info("Add Signer success");
		}
	}

	public static Map<String, Object> encryptEProofData(String eProofDataOutput) throws Exception {
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

		byte[] encrypted = cipher.doFinal(eProofDataOutput.getBytes());
		String encryptedBase64 = Base64Encoder.encode(encrypted);

		Map<String, Object> out = new HashMap<>();
		out.put("qrCodeEncryptedString", encryptedBase64);
		out.put("qrCodeEncryptKey", Base64.getEncoder().encodeToString(keyBytes));
		out.put("qrCodeEncryptInitVector", Base64.getEncoder().encodeToString(ivBytes));

		String decrypted = decryptEProofData(encryptedBase64, (String) out.get("qrCodeEncryptInitVector"), (String) out.get("qrCodeEncryptKey"));
		logger.debug("[decrypted]" + decrypted);
		logger.debug("[matched]" + eProofDataOutput.equals(decrypted));

		return out;
	}

	public static String decryptEProofData(String base64Encrypted, String base64Iv, String base64Key) throws Exception {
		logger.debug("pdfGenerateQRCode");

		IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(base64Iv));
		SecretKeySpec skeySpec = new SecretKeySpec(Base64.getDecoder().decode(base64Key), "AES");

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

		return new String(cipher.doFinal(Base64.getDecoder().decode(base64Encrypted)));
	}

	public static String constructQRCodeString(String uuid, String qrCodeEncryptKey, String qrCodeEncryptInitVector, String qrCodeToken) throws Exception {
		Map vcJsonMapData = new TreeMap<>();
		vcJsonMapData.put("shared_eproof_uuid", uuid);
		vcJsonMapData.put("key", qrCodeEncryptKey);
		vcJsonMapData.put("initVector", qrCodeEncryptInitVector);
		vcJsonMapData.put("jwt", qrCodeToken);

		Map vcJsonMap = new TreeMap<>();
		vcJsonMap.put("type_id", "2c"); // 1: Download     2a: Verification (time-limited)   2b: Verification (face-to-face) 2c: Verification (PDF)
		vcJsonMap.put("data", vcJsonMapData);
		String vcString = GSON.toJson(vcJsonMap);
		return vcString;
	}

	public static String getUnsignedEproofJson(
			LocalDateTime expiryDate,
			LocalDateTime issuranceDate,
			String eproofId,
			String eproofTemplateCode,
			int majorVersion,
			String tc1, String tc2, String tc3,
			String sc1, String sc2, String sc3,
			String en1, String en2, String en3,
			Map<String, String> extraInfo,
			type eproofType
	) {

		//TreeMap eProofData = gson.fromJson(eproofData, TreeMap.class);

		TreeMap eProofData = new TreeMap();

		eProofData.put("eproof_id", eproofId);
		eProofData.put("template_code", String.format("%s-%s-%d", config.getClientId(), eproofTemplateCode, majorVersion));
		eProofData.put("issue_date", issuranceDate.minusHours(8).format(formatter));
		if (expiryDate != null)
			eProofData.put("expire_date", expiryDate.minusHours(8).format(formatter));
		else{
			// Empty string means neven expire
			eProofData.put("expire_date", "9999-12-31T00:00:00Z");
		}
		eProofData.put("schema", "1.0");

		if (eproofType == type.personal)
			eProofData.put("type", "personal");
		else if (eproofType == type.organisational)
			eProofData.put("type", "organisational");

		eProofData.put("en_mobile_card_line1", en1);
		eProofData.put("en_mobile_card_line2", en2);
		eProofData.put("en_mobile_card_line3", en3);
		eProofData.put("tc_mobile_card_line1", tc1);
		eProofData.put("tc_mobile_card_line2", tc2);
		eProofData.put("tc_mobile_card_line3", tc3);
		eProofData.put("sc_mobile_card_line1", sc1);
		eProofData.put("sc_mobile_card_line2", sc2);
		eProofData.put("sc_mobile_card_line3", sc3);

		if (extraInfo != null) {
			for (Map.Entry<String, String> info : extraInfo.entrySet()) {
				eProofData.put(info.getKey(), info.getValue());
			}
		}

		Map systemJsonMap = new TreeMap<>();
		if (expiryDate != null){
			systemJsonMap.put("expirationDate", expiryDate.minusHours(8).format(formatter));
		} else{
			// Empty string means neven expire
			systemJsonMap.put("expirationDate", "9999-12-31T00:00:00Z");
		}

		Map credentialSubjectJsonMap = new TreeMap<>();
		credentialSubjectJsonMap.put("display", eProofData);
		credentialSubjectJsonMap.put("system", systemJsonMap);
		credentialSubjectJsonMap.put("version", "1.0");

		//vc.json
		Map vcJsonMap = new TreeMap<>();
		vcJsonMap.put("@context", new ArrayList<>(Arrays.asList("https://www.w3.org/2018/credentials/v1")));
		vcJsonMap.put("type", new ArrayList<>(Arrays.asList("VerifiableCredential")));
		//vcJsonMap.put("issuer", "did:eproof:75ed43c1-d1f2-4cf2-9b72-5e7792989d46" );
		vcJsonMap.put("issuer", config.getIssuerDid());
		//vcJsonMap.put("issuanceDate", "2010-01-01T19:23:24Z" );
		//LocalDateTime now = LocalDateTime.now();
		//String formattedDate = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		vcJsonMap.put("issuanceDate", issuranceDate.minusHours(8).format(formatter)); // Update as UTC format
		vcJsonMap.put("credentialSubject", credentialSubjectJsonMap);

		logger.debug("EProofUtil - getUnsignedEproofJson - gson.toJson(vcJsonMap): " + GSON.toJson(vcJsonMap));
		return GSON.toJson(vcJsonMap);
	}

	public static Map<String, Object> updateEproof(String uuid, String unsignedMap, String proofValue, String keyName,
												   String eproofTypeId,
												   int downloadMaxCount, LocalDateTime downloadExpiryDate, String hkid
	) throws Exception {
		return registerOrUpdateEproof(uuid, unsignedMap,  proofValue,  keyName,
				eproofTypeId,
				downloadMaxCount,  downloadExpiryDate, null, hkid);  //TODO
	}

	public static Map<String, Object> registerEproof(String unsignedMap, String proofValue, String keyName,
													 String eproofTypeId,
													 int downloadMaxCount, LocalDateTime downloadExpiryDate, String hkid
	) throws Exception {
		return registerOrUpdateEproof(null, unsignedMap,  proofValue,  keyName,
				eproofTypeId,
				downloadMaxCount,  downloadExpiryDate, "", hkid);
	}


	public static Map<String, Object> registerOrUpdateEproof(String uuid, String unsignedMap, String proofValue, String keyName,
															 String eproofTypeId,
															 int downloadMaxCount, LocalDateTime downloadExpiryDate,
															 String formattedPublishDate,
															 String hkid
	) throws Exception {
//		if (simulation) {
//			logger.debug("This is simulation of registerOrUpdateEproof");
//			Map out = new HashMap();
//			out.put("status", "Successful");
//			out.put("uuid", "00000000-0000-0000-0000-000000000000");
//			out.put("version", Strings.isEmpty(uuid) ? 1 : 2);
//			out.put("token", "ThisIsASimulatedDownloadToken");
//			return out;
//		}
		String hkidHash = null;
		String sdidHash = null;
		if (StringUtils.isNotEmpty(hkid)) {
			hkidHash  = computeHash(hkid, config.getHkidSaltValue());
			sdidHash = computeHash(hkid, config.getHkidSaltSdid());
		}

		Map vcJsonMap = GSON.fromJson(unsignedMap, Map.class);
		logger.debug("EProffUtil - registerOrUpdateEproof - vcJsonMap: " + vcJsonMap);
		//vc proof
		Map vcProofJsonMap = new TreeMap<>();
		vcProofJsonMap.put("type", "SHA256withRSA");
		vcProofJsonMap.put("created", LocalDateTime.now().minusHours(8).format(formatter));
		vcProofJsonMap.put("verificationMethod", config.getIssuerDid() + "#" + keyName);
		vcProofJsonMap.put("proofPurpose", "assertionMethod");
		vcProofJsonMap.put("proofValue", proofValue);

		vcJsonMap.put("proof", vcProofJsonMap);

		//hash vc
		String vcString = GSON.toJson(vcJsonMap);
		byte[] vcBytes = CommonUtil.readBytesFromString(vcString, MessageDigest.getInstance("SHA-256"));
		String vcBase64Hash = Base64Encoder.encode(vcBytes);

		Map<String, Object> out = new HashMap<>();
		out.put("eProofJson", vcString);
		/*try (Response httpResponse = ApiUtil.registerEproof(uuid,
				config, (String) ((Map)((Map)vcJsonMap.get("credentialSubject")).get("display")).get("eproof_id"),
				eproofTypeId,
				(String) ((Map)((Map)vcJsonMap.get("credentialSubject")).get("display")).get("template_code"),
				(String) ((Map)((Map)vcJsonMap.get("credentialSubject")).get("display")).get("expire_date"),
				(String) ((Map)((Map)vcJsonMap.get("credentialSubject")).get("display")).get("issue_date"),
				vcBase64Hash, downloadMaxCount, downloadExpiryDate!=null
						?downloadExpiryDate.minusHours(8).format(formatter)
						:null,
				formattedPublishDate,
				hkidHash,
				config.getHkidSaltUuid(),
				sdidHash

		)) {
			checkResponse(httpResponse);
			JSONObject jret = new JSONObject(httpResponse.body().string());
			logger.info("status= " + jret.getString("status").equals("Successful"));
			if (jret.getString("status").equals("Successful")) {
				out.put("status", "Successful");
				out.put("uuid", jret.getJSONObject("data").getString("id"));
				out.put("version", jret.getJSONObject("data").getInt("version"));
				out.put("token", jret.getJSONObject("data").getString("token"));
			}
		}*/

		int currentTrialTimes= 1;

		while(currentTrialTimes < config.getRegisterTrialTimes()){
			try (Response httpResponse = ApiUtil.registerEproof(uuid,
					config, (String) ((Map)((Map)vcJsonMap.get("credentialSubject")).get("display")).get("eproof_id"),
					eproofTypeId,
					(String) ((Map)((Map)vcJsonMap.get("credentialSubject")).get("display")).get("template_code"),
					(String) ((Map)((Map)vcJsonMap.get("credentialSubject")).get("display")).get("expire_date"),
					(String) ((Map)((Map)vcJsonMap.get("credentialSubject")).get("display")).get("issue_date"),
					vcBase64Hash, downloadMaxCount, downloadExpiryDate!=null
							?downloadExpiryDate.minusHours(8).format(formatter)
							:null,
					formattedPublishDate,
					hkidHash,
					config.getHkidSaltUuid(),
					sdidHash
			)) {
				checkResponse(httpResponse);
				JSONObject jret = new JSONObject(httpResponse.body().string());
				logger.info("status= " + jret.getString("status").equals("Successful"));
				if (jret.getString("status").equals("Successful")) {
					out.put("status", "Successful");
					out.put("uuid", jret.getJSONObject("data").getString("id"));
					out.put("version", jret.getJSONObject("data").getInt("version"));
					out.put("token", jret.getJSONObject("data").getString("token"));
				}
				break;
			}
			catch(Exception e){
				if(currentTrialTimes >= config.getRegisterTrialTimes()){
					// Already used all chances to try. If still got exception , throw it out
					throw e;
				}
			}finally{
				currentTrialTimes ++;
			}
		}


		return out;
	}

	public static String getQrCodeString(String eProoJsonString, String uuid, int version, LocalDateTime accessExpirationDate,
										 int qrCodeMaxCount
	) throws Exception {

		/*if (simulation) {
			logger.debug("This is simulation of getQrCodeString");
			return "{\"data\":{\"initVector\":\"OITjOAL9+6qLhxdzABxlQA==\",\"jwt\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MTA3ODQ5OTQsInR5cGUiOiIwNyIsImlhdCI6MTcxMDc1NjE5NCwic3ViIjoiODFkZjk2ODctMzQ4MS00MzMyLWE4ZGEtM2RmZjQzMDg3MzdkIn0.LCXmO4lbshb6D4pu8cbpszsAl99e1_2wjRD0ej4o0qw\",\"key\":\"Cii5Na58AxLYwLBAruVP7xImOnWnT9w4Xtc8SOF0/Z4=\",\"shared_eproof_uuid\":\"81df9687-3481-4332-a8da-3dff4308737d\"},\"type_id\":\"2c\"}";
		}*/

		Map<String, Object> qrCodeInfo = encryptEProofData(eProoJsonString);
		logger.debug("EProofUtil - getQrCodeString - eProofJsonString: " + eProoJsonString);

		String out = null;
		try (Response httpResponse = ApiUtil.shareEProof(config, uuid, version, accessExpirationDate,
				qrCodeMaxCount, (String) qrCodeInfo.get("qrCodeEncryptedString"))){
			checkResponse(httpResponse);
			JSONObject jret = new JSONObject(httpResponse.body().string());
			logger.info("status= " + jret.getString("status").equals("Successful"));
			if (jret.getString("status").equals("Successful")) {
				String qrCode = constructQRCodeString((String) jret.getJSONObject("data").getString("sharedEProofId"), (String) qrCodeInfo.get("qrCodeEncryptKey"),
						(String) qrCodeInfo.get("qrCodeEncryptInitVector"),
						jret.getJSONObject("data").getString("token"));
				out = qrCode;
//					out.put("qrCodeToken", jret.getJSONObject("data").getString("token"));
//					out.put("qrCodeEncryptedString", qrCodeInfo.get("qrCodeEncryptedString"));
//					out.put("qrCodeEncryptKey", qrCodeInfo.get("qrCodeEncryptKey"));
//					out.put("qrCodeEncryptInitVector", qrCodeInfo.get("qrCodeEncryptInitVector"));
			}
		}

		return out;
	}


	//below is no qr code version
	public static Map<String, Object> registerDocument(
			String eproofData,
			LocalDateTime expirationDate,
			LocalDateTime expiryDate,
			LocalDateTime issuranceDate,
			String keyName,
			String eproofId,
			String eproofTypeId,
			String eproofTypeCode,
			int majorVersion,
			int downloadMaxCount,
			LocalDateTime downloadExpiryDate
	) throws Exception {
		Map<String, Object> out = new HashMap<>();

		if (simulation) {
			logger.debug("This is simulation of registerDocument");
			out.put("status", "Successful");
			out.put("uuid", "00000000-0000-0000-0000-000000000000");
			out.put("version", 1);
			out.put("token", "ThisIsASimulatedDownloadToken");
			return out;
		}

		TreeMap eProofData = GSON.fromJson(eproofData, TreeMap.class);

		Map systemJsonMap = new TreeMap<>();
		if (expirationDate != null)
			systemJsonMap.put("expirationDate", expirationDate.minusHours(8).format(formatter));

		Map credentialSubjectJsonMap = new TreeMap<>();
		credentialSubjectJsonMap.put("display", eProofData);
		credentialSubjectJsonMap.put("system", systemJsonMap);

		//vc.json
		Map vcJsonMap = new TreeMap<>();
		vcJsonMap.put("@context", new ArrayList<>(Arrays.asList("https://www.w3.org/2018/credentials/v1")));
		vcJsonMap.put("type", new ArrayList<>(Arrays.asList("VerifiableCredential")));
		//vcJsonMap.put("issuer", "did:eproof:75ed43c1-d1f2-4cf2-9b72-5e7792989d46" );
		vcJsonMap.put("issuer", config.getIssuerDid());
		//vcJsonMap.put("issuanceDate", "2010-01-01T19:23:24Z" );
		//LocalDateTime now = LocalDateTime.now();
		//String formattedDate = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		vcJsonMap.put("issuanceDate", issuranceDate.minusHours(8).format(formatter)); // Update as UTC format
		vcJsonMap.put("credentialSubject", credentialSubjectJsonMap);

		//vc proof
		Map vcProofJsonMap = new TreeMap<>();
		vcProofJsonMap.put("type", "SHA256withRSA");
		//vcProofJsonMap.put(  "created", "2023-04-20T00:15:39.910Z");
		LocalDateTime now = LocalDateTime.now();
		String formattedDate = now.minusHours(8).format(formatter);
		vcProofJsonMap.put("created", formattedDate);
		//vcProofJsonMap.put(  "verificationMethod", "did:eproof:75ed43c1-d1f2-4cf2-9b72-5e7792989d46#key-1");
		vcProofJsonMap.put("verificationMethod", config.getIssuerDid() + "#" + keyName);
		vcProofJsonMap.put("proofPurpose", "assertionMethod");

		//String proofValue = signatureWithPrivateKey(gson.toJson(vcJsonMap),privateKeyPath);
		String proofValue = ""; //TODO sign eproof data string

		//String proofValue = signatureWithToken(gson.toJson(vcJsonMap));
		vcProofJsonMap.put("proofValue", proofValue);

		vcJsonMap.put("proof", vcProofJsonMap);

		//hash vc
		String vcString = GSON .toJson(vcJsonMap);
		byte[] vcBytes = CommonUtil.readBytesFromString(vcString, MessageDigest.getInstance("SHA-256"));
		String vcBase64Hash = CommonUtil.base64Encode(vcBytes);
//
//		eproof.setEProofDataOutput(vcString);
//		eproof.setVcBase64Hash(vcBase64Hash);

		String templateCode = String.format("%s-%s-%d", config.getClientId(), eproofTypeCode, majorVersion);

		try (Response httpResponse = ApiUtil.registerEproof(
				null, config, eproofId, eproofTypeId, templateCode,
				(expiryDate == null) ? null : expiryDate.minusHours(8).format(formatter),
				issuranceDate.minusHours(8).format(formatter), vcBase64Hash, downloadMaxCount,
				(downloadExpiryDate == null) ? null : downloadExpiryDate.minusHours(8).format(formatter),
				"", "", "", ""
		)) {
			checkResponse(httpResponse);
			JSONObject jret = new JSONObject(httpResponse.body().string());
			logger.info("status= " + jret.getString("status").equals("Successful"));
			if (jret.getString("status").equals("Successful")) {
				out.put("status", "Successful");
				out.put("uuid", jret.getJSONObject("data").getString("id"));
				out.put("version", jret.getJSONObject("data").getInt("version"));
				out.put("token", jret.getJSONObject("data").getString("token"));
			}
		}
		logger.info("Register Document success");
		return out;
	}

	public static void issuePdf(String uuid, File pdfFile) throws Exception {
		issuePdf(uuid, calcPdfHash(pdfFile));
	}

	public static void issuePdf(String uuid, String pdfHash) throws Exception {
		logger.info("Pdf hash: "+pdfHash);


		if (simulation) {
			logger.debug("This is simulation of issueDocument");
			return;
		}

		/*try (Response httpResponse = ApiUtil.issueEproofAddPDF(config, uuid)){
			checkResponse(httpResponse);
			logger.info("Write PDF URL success");
		}

		try (Response httpResponse = ApiUtil.issueEproofUpdatePDFHash(config, uuid, pdfHash)) {
			checkResponse(httpResponse);
			logger.info("Write PDF Hash success");
		}*/

		int part1TrialTimes = 1;
		while(part1TrialTimes < config.getIssueEproofPart1TrialTimes()){
			try (Response httpResponse = ApiUtil.issueEproofAddPDF(config, uuid)){
				checkResponse(httpResponse);
				logger.info("Write PDF URL success");
				break;
			}catch(Exception e){
				if(part1TrialTimes >= config.getIssueEproofPart1TrialTimes()){
					// Already used all chances to try. If still got exception , throw it out
					throw e;
				}
			}
			finally{
				part1TrialTimes ++;
			}
		}


		int part2TrialTimes = 1;
		while(part2TrialTimes < config.getIssueEproofPart2TrialTimes()){
			try (Response httpResponse = ApiUtil.issueEproofUpdatePDFHash(config, uuid, pdfHash)) {
				checkResponse(httpResponse);
				logger.info("Write PDF Hash success");
				break;
			}
			catch(Exception e){
				if(part2TrialTimes >= config.getIssueEproofPart2TrialTimes()){
					// Already used all chances to try. If still got exception , throw it out
					throw e;
				}
			}
			finally{
				part2TrialTimes ++;
			}
		}


		//Validate the eProof
		try (Response httpResponse = ApiUtil.getEProof(config, uuid)) {
			checkResponse(httpResponse);
			logger.debug("Read EProof Success");
		}
	}

	public static void revokeEproof(String uuid) throws Exception{
		revocation(uuid, true, false);
	}

	public static void withdrawEproof(String uuid) throws Exception{
		revocation(uuid, false, true);
	}

	public static void revocation(String uuid, boolean isRevoked, boolean isWithdrawn) throws Exception {
		if (simulation) {
			logger.debug("This is simulation of revocation");
			return;
		}

		try (Response httpResponse = ApiUtil.revocation(config, uuid, isRevoked, isWithdrawn)){
			checkResponse(httpResponse);
			logger.info("Revocation success");
		}
	}

	private static void checkResponse(Response httpResponse) throws IOException {
		if (httpResponse.code() != 200) {
			String errorMessage = httpResponse.body().string();
			JSONObject jret = new JSONObject(errorMessage);
			logger.debug("http return code [" + httpResponse.code()+ "] " + errorMessage);
			if (httpResponse.code() == 400 && jret.getString("message").equals("updates not found"))
				logger.debug("updates not found error can be ignored");
			else
				throw new RuntimeException(errorMessage);
		}
	}

	// Method to compute the hash with a given salt
	public static String computeHash(String hkidNumber, String salt) throws NoSuchAlgorithmException {
		// Remove check digit (last character)
		String cleanHkid = removeCheckDigit(hkidNumber);

		// Concatenate HKIC number with the salt
		String input = cleanHkid + salt;

		// Perform SHA-256 hashing
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

		// Encode the hash in Base64
		return Base64.getEncoder().encodeToString(hashBytes);
	}

	private static String removeCheckDigit(String hkidNumber) {
		if (hkidNumber != null && hkidNumber.length() > 1) {
			// Remove the last character, which is the check digit
			return hkidNumber.substring(0, hkidNumber.length() - 1);
		}
		// Handle invalid HKID format
		throw new IllegalArgumentException("Invalid HKIC number: " + hkidNumber);
	}
}