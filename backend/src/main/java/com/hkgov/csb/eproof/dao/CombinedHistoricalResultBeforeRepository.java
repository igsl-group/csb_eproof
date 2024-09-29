package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.dto.HistoricalSearchDto;
import com.hkgov.csb.eproof.entity.CombinedHistoricalResultBefore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CombinedHistoricalResultBeforeRepository extends JpaRepository<CombinedHistoricalResultBefore,Long> {
    @Query("select c from CombinedHistoricalResultBefore c where c.hkid in :hkids or c.passport in :passports order by c.hkid,c.passport")
    List<CombinedHistoricalResultBefore> findByHkidIn(List<String> hkids, List<String> passports);

    @Query(nativeQuery = true,value = """
        SELECT * FROM combined_historical_result_before_2024 c WHERE
        (
            1=1
        )
        AND
        (
            ( ?#{#searchDto.hkid} IS null OR c.hkid IS NULL OR c.hkid = '' OR c.hkid LIKE %?#{#searchDto.hkid}% ) AND
            ( ?#{#searchDto.passport} IS null OR c.passport like %?#{#searchDto.passport}% ) AND
            ( ?#{#searchDto.name} IS null OR c.name like %?#{#searchDto.name}% ) AND
            ( ?#{#searchDto.email} IS null OR c.email like %?#{#searchDto.email}% ) AND
            ( ?#{#searchDto.valid} IS null OR c.valid = ?#{#searchDto.valid} )
        )
""",countQuery = """
 SELECT count(*) FROM combined_historical_result_before_2024 c WHERE
        (
            1=1
        )
        AND
        (
            ( ?#{#searchDto.hkid} IS null OR c.hkid IS NULL OR c.hkid = '' OR c.hkid LIKE %?#{#searchDto.hkid}% ) AND
            ( ?#{#searchDto.passport} IS null OR c.passport like %?#{#searchDto.passport}% ) AND
            ( ?#{#searchDto.name} IS null OR c.name like %?#{#searchDto.name}% ) AND
            ( ?#{#searchDto.email} IS null OR c.email like %?#{#searchDto.email}% )AND
            ( ?#{#searchDto.valid} IS null OR c.valid = ?#{#searchDto.valid} )
        )
""")
    Page<CombinedHistoricalResultBefore> findPage(Pageable pageable, @Param("searchDto") HistoricalSearchDto searchDto);
}