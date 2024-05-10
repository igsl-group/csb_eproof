package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.ExamProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExamProfileRepository extends JpaRepository<ExamProfile,Long> {
    @Query("select u from ExamProfile u where u.serialNo = :serialNo")
    ExamProfile getinfoByNo(@Param("serialNo") String serialNo);

    @Query("update ExamProfile u set u.isFreezed = true where u.serialNo = :serialNo")
    Boolean updateIsFreezed(@Param("serialNo") String serialNo);

    @Query("select u from ExamProfile u where:keyword is null or (u.location like %:keyword%)")
    Page<ExamProfile> findPage(Pageable pageable, @Param("keyword") String keyWord);

    @Query("select u from ExamProfile u where u.isFreezed = false ")
    List<ExamProfile> dropDown();
}
