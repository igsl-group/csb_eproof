package com.hkgov.ceo.pms.dao;

import com.hkgov.ceo.pms.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.loginId = :loginId")
    Optional<User> findByLoginId(@Param("loginId") String loginId);

    @Query("select u from User u where u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("select u from User u where :keyword is null or (u.email like %:keyword% or u.name like %:keyword%)")
    Page<User> findByEmailOrName(Pageable pageable, @Param("keyword") String keyword);

    @Modifying
    @Query("update User u set u.loginAttempt = IFNULL(u.loginAttempt, 0) + 1 where u.loginId = :loginId")
    void increaseLoginAttempt(@Param("loginId") String loginId);

    @Modifying
    @Query("update User u set u.status = com.hkgov.ceo.pms.config.Constants.BLOCKED where u.loginId = :loginId")
    void blockUser(@Param("loginId") String loginId);

    @Query(value = """
            select u.*
            from user u
                     left join (select user_id, MAX(create_date) as max_create_date
                                from password
                                group by user_id) as lastest_password on u.user_id = lastest_password.user_id
            where lastest_password.max_create_date < DATE_SUB(NOW(), INTERVAL :expiryDay DAY)
            and u.password_remind != true
            """, nativeQuery = true)
    List<User> findPasswordExpireUser(@Param("expiryDay") int expiryDay);

    @Query(value = """
            SELECT DATEDIFF(MAX(create_date), CURDATE()) AS days_diff
            FROM password where user_id = :userId
                        """, nativeQuery = true)
    Integer findDayAfterChangePassword(@Param("userId") Long userId);

    @Query("select (count(u) > 0) from User u where u.loginId = :loginId")
    boolean existsByLoginId(@Param("loginId") String loginId);

    @Query("select (count(u) > 0) from User u where u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("select (count(u) > 0) from User u where u.email = :email and u.userId != :userId")
    boolean existsByEmailAndUserId(@Param("email") String email, @Param("userId") Long userId);


}
