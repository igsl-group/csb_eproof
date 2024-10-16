package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CertEproofRenewDto implements Serializable {
    private String token;
    private String url;
}
