package com.hkgov.csb.eproof.util.EProof;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "eproof-config")
public class EProofConfigProperties {

	String issuerDid;
	String clientId;
	String clientSecret;
	String url;
	String pdfUrl;
	String otpUrl;
	String dataUrl;
	String issuerNameEn;
	String issuerNameTc;
	String passTemplateTypeId;
	String failTemplateTypeId;
	String downloadUrlPrefix;
	String hkidSaltSdid;
	String hkidSaltUuid;
	String hkidSaltValue;
	String accessToken;
}
