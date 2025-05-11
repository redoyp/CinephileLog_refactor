package com.CinephileLog.controller;

import com.CinephileLog.domain.User;
import com.CinephileLog.dto.AddUserRequest;
import com.CinephileLog.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("api/user/{id}")        // 업데이트
    public ResponseEntity<Void> updateUser(@PathVariable("id") long userId, @RequestBody AddUserRequest addUserRequest, HttpSession session) {
        userService.updateUserById(userId, addUserRequest);
        session.setAttribute("nickname", addUserRequest.getNickname());
        return ResponseEntity.ok().build();
    }

    @PostMapping("api/checkNickname")
    public boolean checkNickname(@RequestBody String nickname) {
        User user = userService.getUserByNickname(nickname);

        if (user == null) {
            return true;
        } else {
            return false;
        }
    }

}
