package com.hkgov.csb.eproof.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DocumentScoreDto {
    private String scoreType;
    private String score;
}
