package com.hkgov.csb.eproof.entity;

import com.hkgov.csb.eproof.entity.enums.CertStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "cert_action")
@Getter
@Setter
public class CertAction extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "type")
    private String type;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CertStatus status;

    @Column(name = "approver")
    private Long approver;

    @Column(name = "remark",columnDefinition = "varchar(1000)")
    private String remark;

    @Column(name = "can_email_content" ,columnDefinition="TEXT")
    private String canEmailContent;

    @Column(name = "can_email_address")
    private String canEmailAddress;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "action_target",
            joinColumns = @JoinColumn(name = "action_id"),
            inverseJoinColumns = @JoinColumn(name = "target_cert_info_id")
    )
    private List<CertInfo> certInfos;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "approver", insertable = false, updatable = false)
    private User user;
}
