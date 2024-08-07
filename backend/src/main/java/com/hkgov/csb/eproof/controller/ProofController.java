package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.constants.enums.ExceptionEnums;
import com.hkgov.csb.eproof.dto.EproofDataDto;
import com.hkgov.csb.eproof.dto.EproofResponseDto;
import com.hkgov.csb.eproof.dto.ProofDto;
import com.hkgov.csb.eproof.exception.GenericException;
import com.hkgov.csb.eproof.service.ProofService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProofController {
    private final ProofService proofService;
    @PostMapping("/sendOTP")
    public EproofResponseDto sendOTP(@RequestBody ProofDto requestDto){
        EproofResponseDto responseDto = proofService.sendOTP(requestDto);
        return responseDto;
    }
    @PostMapping("/eProof-data")
    public EproofResponseDto getData(@RequestBody EproofDataDto requestDto){
        try{
            EproofResponseDto responseDto = proofService.getData(requestDto);
            return responseDto;
        }catch (Exception e){
            if(e instanceof GenericException){
                throw new GenericException(((GenericException) e).getCode(),e.getMessage());
            }
            throw new GenericException(ExceptionEnums.E_PROOF_SYSTEM_ERROR);
        }
    }

    @PostMapping("/eProof-pdf")
    public EproofResponseDto getPdf(@RequestBody EproofDataDto requestDto){
        try{
            EproofResponseDto responseDto = proofService.getPdf(requestDto);
            return responseDto;
        }catch (Exception e){
            if(e instanceof GenericException){
                throw new GenericException(((GenericException) e).getCode(),e.getMessage());
            }
            throw new GenericException(ExceptionEnums.E_PROOF_SYSTEM_ERROR);
        }
    }
}
