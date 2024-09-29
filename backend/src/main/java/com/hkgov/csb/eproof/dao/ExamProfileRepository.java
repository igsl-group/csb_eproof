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
    @Query("""
        select u
        from ExamProfile u 
        where u.serialNo = :serialNo
    """)
    ExamProfile getinfoByNo(@Param("serialNo") String serialNo);

    @Query("""
        select 
        MIN(c.actualEmailSendTime) AS actualEmailSendDateFrom,
        MAX(c.actualEmailSendTime) AS actualEmailSendDateTo
        from ExamProfile u 
        Join CertInfo c ON u.serialNo = c.examProfileSerialNo
        where u.serialNo = :serialNo
        GROUP BY u
    """)
    List<Object[]> getEmailSendTime(@Param("serialNo") String serialNo);

    @Query(nativeQuery = true, value =
            """
             select u.* from exam_profile u
             where :keyword is null
             or (
                 u.location like %:keyword% or
                 u.serial_no like %:keyword% or
                 u.exam_date LIKE %:keyword% or
                 u.result_letter_date LIKE %:keyword% or
                 u.effective_date LIKE %:keyword% or
                 u.planned_email_issuance_date LIKE %:keyword%
             )
             """,
            countQuery = """
    select count(*) from exam_profile u
    where :keyword is null
    or (
        u.location like %:keyword% or
        u.serial_no like %:keyword% or
        u.exam_date LIKE %:keyword% or
        u.result_letter_date LIKE %:keyword% or
        u.effective_date LIKE %:keyword% or
        u.planned_email_issuance_date LIKE %:keyword%
    )
    """
    )
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


    @Query("SELECT MAX(e.serialNo) FROM ExamProfile e WHERE e.serialNo LIKE ?1%")
    String findMaxSerialNoByPrefix(String prefix);
}
