package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cert_eproof_renew")
@Getter
@Setter
public class CertEproofRenew {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cert_info_renew_id")
    private Long certInfoRenewId;

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
    @Column(name = "e_wallet_json",columnDefinition = "text")
    private String eWalletJson;
    @Column(name = "e_cert_html")
    private String eCertHtml;
    @Column(name = "url")
    private String url;
    @Column(name = "qr_code_string",columnDefinition = "text")
    private String qrCodeString;


    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cert_info_renew_id", insertable = false, updatable = false)
    private CertInfoRenew certInfoRenew;

}
