package com.CinephileLog.mapper;

import com.CinephileLog.dto.UserScoreDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserScoreMapper {
    List<UserScoreDTO> selectEligibleUsers();
    void upsertUserScore(UserScoreDTO dto);

    void deleteUserById(Long userId);
}
