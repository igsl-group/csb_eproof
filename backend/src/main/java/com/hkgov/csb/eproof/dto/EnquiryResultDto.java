package com.hkgov.csb.eproof.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnquiryResultDto {
    @CsvBindByName(column = "HKID/Passport")
    private String number= "";
}
