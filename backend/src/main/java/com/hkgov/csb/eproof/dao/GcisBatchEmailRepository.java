package com.hkgov.csb.eproof.dao;

import com.hkgov.csb.eproof.entity.GcisBatchEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GcisBatchEmailRepository extends JpaRepository<GcisBatchEmail,Long> {


    @Query("""
    SELECT gbe FROM GcisBatchEmail gbe 
        WHERE DATE(gbe.scheduleDatetime) = :queryDate 
        AND gbe.batchUploadStatus IS NULL
        AND gbe.batchUploadRefNum IS NULL
""")
    List<GcisBatchEmail> findPendingUploadBatchEmailByDate(@Param("queryDate") LocalDate queryDate);


    @Query("""
    SELECT gbe FROM GcisBatchEmail gbe 
        WHERE DATE(gbe.scheduleDatetime) = :queryDate 
        AND gbe.scheduleJobId IS NULL
""")
    List<GcisBatchEmail> findPendingScheduleBatchEmailByDate(@Param("queryDate") LocalDate queryDate);


    @Query("""
    SELECT gbe FROM GcisBatchEmail gbe 
        WHERE DATE(gbe.scheduleDatetime) = :queryDate 
        AND (gbe.scheduleJobStatus != 'COMPLETED' or gbe.scheduleJobStatus is null)
""")
    List<GcisBatchEmail> findPendingEnquireBatchEmailByDate(@Param("queryDate") LocalDate queryDate);

    @Query("""
            SELECT gbe
            FROM GcisBatchEmail gbe
            JOIN gbe.certInfoList certInfo
            WHERE certInfo.examProfileSerialNo = :examProfileSerialNo
            AND gbe.scheduleDatetime >= :tomorrow
            """)
    List<GcisBatchEmail> findToBeDeleteBatchEmail(String examProfileSerialNo, LocalDateTime tomorrow);
}
