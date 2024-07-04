package com.hkgov.csb.eproof.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cert_eproof")
@Getter
@Setter
public class CertEproof {
    @Id
    @Column(name = "cert_info_id")
    private Long certInfoId;
    @Column(name = "eproof_id")
    private String eproofId;
    @Column(name = "key_name")
    private String keyName;
    @Column(name = "uuid")
    private String uuid;
    @Column(name = "version")
    private Integer version;
    @Column(name = "token")
    private String token;
    @Column(name = "e_wallet_json")
    private String eWalletJson;
    @Column(name = "e_cert_html")
    private String eCertHtml;
    @Column(name = "url")
    private String url;
}
