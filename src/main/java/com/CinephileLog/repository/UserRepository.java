package com.CinephileLog.repository;

import com.CinephileLog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailAndProviderAndIsActive(String email, String provider, String isActive);
    List<User> findByNicknameContaining(String keyword);
    User findByNickname(String nickname);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.grade.gradeId = :gradeId WHERE u.userId = :userId")
    void updateGrade(Long userId, Long gradeId);

    User findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.point = :point WHERE u.userId = :userId")
    void updateUserPoint(Long userId, Long point);
}