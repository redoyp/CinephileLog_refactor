package com.CinephileLog.service;

import com.CinephileLog.domain.User;
import com.CinephileLog.mapper.UserScoreMapper;
import com.CinephileLog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AdminUserManagementService {

    private final UserRepository userRepository;
    private final UserScoreMapper userScoreMapper;

    @Autowired
    public AdminUserManagementService(UserRepository userRepository, UserScoreMapper userScoreMapper) {
        this.userRepository = userRepository;
        this.userScoreMapper = userScoreMapper;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));
    }

    @Transactional
    public void updateUser(Long userId, User updatedUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        existingUser.setNickname(updatedUser.getNickname());
        existingUser.setGrade(updatedUser.getGrade());
        existingUser.setPoint(updatedUser.getPoint());
        existingUser.setIsActive(updatedUser.getIsActive());
        existingUser.setUpdatedDate(LocalDateTime.now());
        existingUser.setRole(updatedUser.getRole());

        userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userScoreMapper.deleteUserById(userId);
        userRepository.deleteById(userId);
    }

    @Transactional
    public void deactivateUser(Long userId) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setIsActive("N");
        user.setNickname("비활성화 처리된 회원입니다");

        userRepository.save(user);
    }

    public List<User> searchUsersByKeyword(String keyword) {
        return userRepository.findByNicknameContaining(keyword);
    }
}