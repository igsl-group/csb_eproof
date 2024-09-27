package com.hkgov.csb.eproof.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "gcis_batch_email")
@Getter
@Setter
public class GcisBatchEmail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email_template_id")
    private Long emailTemplateId;

    @Column(name = "schedule_datetime")
    private LocalDateTime scheduleDatetime;

    @Column(name = "xml", columnDefinition = "text")
    private String xml;

    @Column(name = "status")
    private String status;

    @Column(name = "gcis_noti_list_name")
    private String gcisNotiListName;

    @Column(name = "gcis_template_name")
    private String gcisTemplateName;

    @Column(name = "batch_upload_ref_num")
    private String batchUploadRefNum;

    @Column(name = "batch_upload_status")
    private String batchUploadStatus;

    @Column(name = "batch_upload_remark")
    private String batchUploadRemark;

    @Column(name = "schedule_job_id")
    private String scheduleJobId;

    @Column(name = "schedule_job_status")
    private String scheduleJobStatus;

    @Column(name = "schedule_job_remark")
    private String scheduleJobRemark;

    @Column(name = "schedule_est_start_time")
    private String scheduleEstStartTime;

    @Column(name = "schedule_est_end_time")
    private String scheduleEstEndTime;


    @OneToMany(mappedBy = "gcisBatchEmailId", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<CertInfo> certInfoList;

    @Transient
    private String examProfileSerialNo;

    public String getExamProfileSerialNo() {
        for(CertInfo certInfo : certInfoList){
            return certInfo.getExamProfileSerialNo();
        }
        return null;
    }

}