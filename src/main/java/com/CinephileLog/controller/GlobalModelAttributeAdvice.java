package com.CinephileLog.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalModelAttributeAdvice {

    @ModelAttribute
    public void addCommonAttributes(Model model, HttpSession session) {
        model.addAttribute("showMenu", true);

        Object userId = session.getAttribute("userId");
        Object nickname = session.getAttribute("nickname");
        Object gradeName = session.getAttribute("gradeName");
        Object roleName = session.getAttribute("roleName");

        model.addAttribute("userId", userId);
        model.addAttribute("nickname", nickname);
        model.addAttribute("gradeName", gradeName);
        model.addAttribute("roleName", roleName);
    }
}
