package com.hkgov.csb.eproof.entity;

import com.hkgov.csb.eproof.entity.enums.CertStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "combined_historical_result_before_2024_approve")
@Getter
@Setter
public class CombinedHisResultBefApprove extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "historical_result_id")
    private Long historicalResultId;

    @Column(name = "old_bl_void")
    private Boolean oldBlVoid;

    @Column(name = "new_bl_void")
    private Boolean newBlVoid;

    @Column(name = "old_ue_void")
    private Boolean oldUeVoid;

    @Column(name = "new_ue_void")
    private Boolean newUeVoid;

    @Column(name = "old_uc_void")
    private Boolean oldUcVoid;

    @Column(name = "new_uc_void")
    private Boolean newUcVoid;

    @Column(name = "old_at_void")
    private Boolean oldAtVoid;

    @Column(name = "new_at_void")
    private Boolean newAtVoid;

    @Column(name = "old_valid")
    private Boolean oldValid;

    @Column(name = "new_valid")
    private Boolean newValid;

    @Column(name = "remark")
    private String remark;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CertStatus status;

    @ManyToOne
    @JoinColumn(name = "historical_result_id", insertable = false, updatable = false)
    private CombinedHistoricalResultBefore historicalResult;
}
