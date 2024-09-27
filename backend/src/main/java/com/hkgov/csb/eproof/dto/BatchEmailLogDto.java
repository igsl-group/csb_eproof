package com.hkgov.csb.eproof.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.hkgov.csb.eproof.constants.Constants.DATE_TIME_PATTERN;

@Getter
@Setter
public class BatchEmailLogDto {
    private Long id;
    private LocalDateTime dateTime;
    private String batchUploadRefNum;
    private String batchUploadStatus;
    private String scheduleJobId;
    private String scheduleJobStatus;
    private String scheduleEstEndTime;
    private String scheduleEstStartTime;
    private String examProfileSerialNo;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime createdDate;
}
