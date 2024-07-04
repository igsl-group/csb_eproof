package com.hkgov.csb.eproof.controller;

import com.hkgov.csb.eproof.dto.EproofResponseDto;
import com.hkgov.csb.eproof.dto.ProofDto;
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
}
