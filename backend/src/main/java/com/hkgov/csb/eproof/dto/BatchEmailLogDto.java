package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BatchEmailLogDto {
    private Long id;
    private LocalDateTime dateTime;
    private String batchUploadRefNum;
    private String batchUploadStatus;
    private String scheduleJobId;
    private String scheduleJobStatus;
    private LocalDateTime scheduleEstEndTime;
    private LocalDateTime scheduleEstStartTime;
    private String examProfileSerialNo;
}
