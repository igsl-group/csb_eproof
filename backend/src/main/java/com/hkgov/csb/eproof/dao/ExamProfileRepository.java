package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.ExamProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExamProfileRepository extends JpaRepository<ExamProfile,String> {
    @Query("select u from ExamProfile u where u.serialNo = :serialNo")
    ExamProfile getinfoByNo(@Param("serialNo") String serialNo);

    @Query("select u from ExamProfile u where:keyword is null or (u.location like %:keyword%)")
    Page<ExamProfile> findPage(Pageable pageable, @Param("keyword") String keyWord);

    @Query("select u from ExamProfile u where u.isFreezed = false ")
    List<ExamProfile> dropDown();
    @Modifying
    @Query("delete from ExamProfile u where u.serialNo = :serialNo")
    Integer delExamProfile(@Param("serialNo") String serialNo);

    @Modifying
    @Query("update ExamProfile set examDate = :examDate,plannedEmailIssuanceDate = :plannedEmailIssuanceDate," +
            "location = :location,resultLetterDate = :resultLetterDate,effectiveDate = :effectiveDate where serialNo = :serialNo")
    Integer updateInfo(@Param("serialNo") String serialNo, @Param("examDate") LocalDate examDate
            , @Param("plannedEmailIssuanceDate") LocalDate plannedEmailIssuanceDate, @Param("location") String location,
                       @Param("resultLetterDate") LocalDate resultLetterDate,@Param("effectiveDate") LocalDate effectiveDate);


}
