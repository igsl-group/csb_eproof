package com.hkgov.csb.eproof.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrepareEproofPdfRequest {
    private String eproofDataJson;
    private String signedProofValue;
    private String publicKey;
}
