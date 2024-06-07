package com.hkgov.csb.eproof.util.EProof;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EProofConfig {
	public void init(String issuerDID, String url, String pdfUrl, String otpUrl, String dataUrl, String clientId, String clientSecret) {
		this.setIssuerDID(issuerDID);
		this.setUrl(url);
		this.setPdfUrl(pdfUrl);
		this.setOtpUrl(otpUrl);
		this.setDataUrl(dataUrl);
		this.setClientId(clientId);
		this.setClientSecret(clientSecret);
	}

	String issuerDID;

	String url;
	String pdfUrl;
	String otpUrl;
	String dataUrl;

	String clientId;
	String clientSecret;

	String accessToken;
}
