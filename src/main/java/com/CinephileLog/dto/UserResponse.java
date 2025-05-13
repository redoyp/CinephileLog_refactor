package com.CinephileLog.dto;

import com.CinephileLog.domain.Role;
import com.CinephileLog.domain.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String provider;
    private String email;
    private String nickname;
    private Role role;
    private String isActive;
    private Long point;
    private Long gradeId;
    private String gradeName;
    private int gradeLevel;
    private LocalDateTime registerDate;
    private LocalDateTime updatedDate;

    public UserResponse(User user) {
        this.userId = user.getUserId();
        this.provider = user.getProvider();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.role = user.getRole();
        this.isActive = user.getIsActive();
        this.point = user.getPoint();
        this.gradeId = user.getGrade().getGradeId();
        this.gradeName = user.getGrade().getGradeName();
        this.gradeLevel = user.getGrade().getGradeId().intValue();
        this.registerDate = user.getRegisterDate();
        this.updatedDate = user.getUpdatedDate();
    }
}
