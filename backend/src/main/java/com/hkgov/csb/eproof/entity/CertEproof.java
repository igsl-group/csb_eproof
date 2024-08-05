package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cert_eproof")
@Getter
@Setter
public class CertEproof {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

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
    @Column(name = "e_wallet_json",columnDefinition = "varchar(4000)")
    private String eWalletJson;
    @Column(name = "e_cert_html")
    private String eCertHtml;
    @Column(name = "url")
    private String url;


    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cert_info_id", insertable = false, updatable = false)
    private CertInfo certInfo;

}
