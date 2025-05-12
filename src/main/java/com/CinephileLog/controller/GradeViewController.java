package com.CinephileLog.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class GradeViewController {

    @GetMapping("/gradeInfo")
    public String gradeDescView(@AuthenticationPrincipal OAuth2User user, Model model, HttpSession session) {
        //For header fragment
        if (user != null) {
            model.addAttribute("userId", user.getAttribute("userId"));
            model.addAttribute("nickname", session.getAttribute("nickname").toString());
            model.addAttribute("gradeName", session.getAttribute("gradeName").toString());
            model.addAttribute("roleName", session.getAttribute("roleName").toString());
        }
        model.addAttribute("showMenu", true);

        List<Map<String, String>> gradeList = new ArrayList<>();
        gradeList.add(createGrade("popcorn", " 팝콘 (리뷰 50개, 활동 지수 상위 5%) => 사이트 메인/추천탭 최상단 노출, 시사회나 이벤트 응모 우대 가능"));
        gradeList.add(createGrade("hotdog", "핫도그 (리뷰 50개, 활동 지수 상위 10%) => 영화 컬럼 작성 가능, 작성한 리뷰 \"에디터 추천\" 뱃지 부여"));
        gradeList.add(createGrade("nachos", "나쵸 (리뷰 30개, 좋아요 100개) => 채팅방 입장 가능"));
        gradeList.add(createGrade("coke", "콜라 (리뷰 10개) => 나만의 영화 리스트 작성 가능"));
        gradeList.add(createGrade("jelly", "젤리 (가입 ~ 리뷰 9개) => 리뷰 작성, 리뷰 추천 가능"));

        int centerX = 150 + 80;  //radius + roughly image width
        int centerY = 150 + 80;  //radius + image height
        int radiusX = 150;
        int radiusY = 180;

        List<Map<String, Object>> positionedGrades = new ArrayList<>();

        for (int i = 0; i < gradeList.size() ; i++) {
            Map<String, String> gradeMap = gradeList.get(i);

            double angle = Math.PI / 2 + (Math.PI * i / (gradeList.size() - 1));  //calculate half circle From π/2 (top) to 3π/2 (bottom)
            int x = (int) (Math.cos(angle) * radiusX + centerX);
            int y = (int) (Math.sin(angle) * radiusY + centerY);

            if (i == 0) {
                y += 30;
            }

            int descX = centerX + 50;
            int descY = centerY + 100;

            //grade data put in by descending order of original sequence in objects because position is in clockwise rotation
            Map<String, Object> map = new HashMap<>();
            map.put("gradeName", gradeMap.get("gradeName"));
            map.put("x", x);
            map.put("y", y);
            map.put("gradeDesc", gradeMap.get("gradeDesc"));
            map.put("descX", descX);
            map.put("descY", descY);
            positionedGrades.add(map);
        }

        model.addAttribute("grades", positionedGrades);
        return "gradeInfo";
    }

    private static Map<String, String> createGrade(String gradeName, String gradeDesc) {
        Map<String, String> gradeMap = new HashMap<>();
        gradeMap.put("gradeName", gradeName);
        gradeMap.put("gradeDesc", gradeDesc);
        return gradeMap;
    }
}
