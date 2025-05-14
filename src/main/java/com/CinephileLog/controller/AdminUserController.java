package com.CinephileLog.controller;

import com.CinephileLog.domain.Grade;
import com.CinephileLog.domain.Role;
import com.CinephileLog.domain.User;
import com.CinephileLog.service.AdminUserManagementService;
import com.CinephileLog.service.GradeService;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/users")     // 유저 관리
public class AdminUserController {

    private final AdminUserManagementService adminUserManagementService;
    private final GradeService gradeService;

    @Autowired
    public AdminUserController(AdminUserManagementService adminUserManagementService, GradeService gradeService) {
        this.adminUserManagementService = adminUserManagementService;
        this.gradeService = gradeService;
    }

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = adminUserManagementService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/user/list";
    }

    @GetMapping("/edit/{userId}")
    public String editUserForm(@PathVariable Long userId, Model model) {
        User user = adminUserManagementService.getUserById(userId);
        if (user == null) {
            return "redirect:/admin/users";
        }
        List<Grade> grades = gradeService.getAllGrades();
        List<Role> roles = Arrays.asList(Role.values());

        model.addAttribute("user", user);
        model.addAttribute("grades", grades);
        model.addAttribute("roles", roles);
        return "admin/user/edit";
    }

    @PostMapping("/update/{userId}")
    public String updateUser(@PathVariable Long userId, @ModelAttribute User updatedUser, @RequestParam("role") Role role) {
        User existingUser = adminUserManagementService.getUserById(userId);
        if (existingUser == null) {
            return "redirect:/admin/users";
        }
        updatedUser.setUserId(userId);
        updatedUser.setRole(role);

        Grade grade = gradeService.getGradeById(updatedUser.getGrade().getGradeId()).orElse(null);
        updatedUser.setGrade(grade);

        adminUserManagementService.updateUser(userId, updatedUser);
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{userId}")
    public String deactivateUser(@PathVariable Long userId) throws NotFoundException {
        adminUserManagementService.deactivateUser(userId);
        return "redirect:/admin/users";
    }


    @GetMapping("/search")
    public String searchUsers(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<User> searchResults;
        if (keyword != null && !keyword.trim().isEmpty()) {
            searchResults = adminUserManagementService.searchUsersByKeyword(keyword);
        } else {
            searchResults = adminUserManagementService.getAllUsers();
        }
        model.addAttribute("users", searchResults);
        model.addAttribute("keyword", keyword);
        return "admin/user/list";
    }
}
