package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "action_target")
@Getter
@Setter
public class ActionTarget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "action_id")
    private Long actionId;

    @Column(name = "target_cert_info_id")
    private Long targetCertInfoId;

    @ManyToOne
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "action_id", insertable = false, updatable = false)
    private CertAction certAction;

    @ManyToOne
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "target_cert_info_id", insertable = false, updatable = false)
    private CertInfo certInfo;


}
