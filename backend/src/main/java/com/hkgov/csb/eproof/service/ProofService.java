package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.dto.EproofResponseDto;
import com.hkgov.csb.eproof.dto.ProofDto;

public interface ProofService {
    EproofResponseDto sendOTP(ProofDto requestDto);
}
