package com.hkgov.csb.eproof.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EproofResponseDto {
    private String status;
    private String message;
    private String data;

    public EproofResponseDto(String responseStatus, String message) {
        this.status = responseStatus;
        this.message = message;
    }
    public EproofResponseDto(String responseStatus, String message, String data) {
        this.status = responseStatus;
        this.message = message;
        this.data = data;
    }
}
