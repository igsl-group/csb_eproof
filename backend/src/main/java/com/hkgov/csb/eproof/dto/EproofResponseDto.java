package com.hkgov.csb.eproof.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EproofResponseDto {
    private String status;
    private String message;
}
