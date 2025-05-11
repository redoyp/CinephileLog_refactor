package com.CinephileLog.controller;

import com.CinephileLog.dto.CustomOAuth2User;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")       // 관리자 홈 (localhost:8080/admin)
public class AdminHomeController {

    @GetMapping
    public String home(@AuthenticationPrincipal CustomOAuth2User principal, Model model, HttpSession session) {
        if (principal != null) {
            model.addAttribute("email", principal.getAttribute("email"));
            model.addAttribute("userId", principal.getAttribute("userId"));
            model.addAttribute("role", principal.getRole().toString());

            if (principal.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                model.addAttribute("isAdmin", true);
            } else {
                model.addAttribute("isAdmin", false);
            }

            //For header fragment - additional attribute
            model.addAttribute("nickname",session.getAttribute("nickname").toString());
            model.addAttribute("gradeName",session.getAttribute("gradeName").toString());
            model.addAttribute("roleName",session.getAttribute("roleName").toString());
        }
        model.addAttribute("showMenu", true);
        return "admin/home";
    }
}