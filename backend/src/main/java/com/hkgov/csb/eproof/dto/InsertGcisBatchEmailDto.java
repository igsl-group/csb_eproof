package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class InsertGcisBatchEmailDto {
    private LocalDate scheduledTime;
}
