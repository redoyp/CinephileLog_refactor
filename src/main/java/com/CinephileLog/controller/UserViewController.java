package com.CinephileLog.controller;

import com.CinephileLog.domain.User;
import com.CinephileLog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Controller
public class UserViewController {

    private final UserService userService;

    public UserViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String logInView(Model model) {
        model.addAttribute("showMenu", false);
        return "logIn";
    }

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/postLogout")
    public String postLogout() {
        return "home";
    }

    @GetMapping("/userLogout")
    public void logOutProcess(HttpServletRequest request,
                       HttpServletResponse response,
                       @AuthenticationPrincipal OAuth2User user,
                       Authentication authentication) throws IOException, InterruptedException {
        if (user == null) {
            response.sendRedirect("/login"); //If the user is not authenticated (OAuth2User is null), redirect to login page
            return;
        }

        // The user is authenticated, so we can proceed with logout logic
        new SecurityContextLogoutHandler().logout(request, response, null); // Log out the user internally from Spring

        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            String provider = oauth2Token.getAuthorizedClientRegistrationId();
            String clientId = clientRegistrationRepository.findByRegistrationId(provider).getClientId();

            String logoutRedirectUri = "http://localhost:8080/postLogout";
            String encodedLogoutRedirectUri = URLEncoder.encode(logoutRedirectUri, StandardCharsets.UTF_8);

            //kakao need to redirect to its logout uri to fully log out
            if (provider.equals("kakao")) {
                logoutRedirectUri = "https://kauth.kakao.com/oauth/logout?client_id=" + clientId + "&logout_redirect_uri=" + encodedLogoutRedirectUri;
            }

            //Log out google via API - to log out from this service only, not all google accounts in the browser
            if (provider.equals("google")) {
                OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                        provider, oauth2Token.getName());

                if (client != null) {
                    String accessToken = client.getAccessToken().getTokenValue();

                    //Revoke token
                    HttpClient httpClient = HttpClient.newHttpClient();
                    HttpRequest revoke = HttpRequest.newBuilder()
                            .uri(URI.create("https://oauth2.googleapis.com/revoke?token=" + accessToken))
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .POST(HttpRequest.BodyPublishers.noBody())
                            .build();
                    httpClient.send(revoke, HttpResponse.BodyHandlers.discarding());
                }
            }

            response.sendRedirect(logoutRedirectUri);
        }
    }

    @GetMapping("/checkNickname")
    public String checkNickname(@AuthenticationPrincipal OAuth2User user, HttpSession session) {
        User userInfo = userService.getUserById(user.getAttribute("userId"));

        session.setAttribute("userId", user.getAttribute("userId"));
        session.setAttribute("gradeName", userInfo.getGrade().getGradeName());
        session.setAttribute("roleName", userInfo.getRole());

        if (userInfo.getNickname() == null) {
            return "redirect:/setUpNickname";   //redirect to set up nickname page if user hasn't set up
        } else {
            session.setAttribute("nickname", userInfo.getNickname());
            return "redirect:/home";
        }
    }

    @GetMapping("/setUpNickname")
    public String setUpNickname(@AuthenticationPrincipal OAuth2User user, Model model) {
        Long userId = user.getAttribute("userId");
        model.addAttribute("userId", userId);
        model.addAttribute("showMenu", false);
        return "setUpNickname";
    }

    @GetMapping("/profile")
    public String myProfileView(@AuthenticationPrincipal OAuth2User user, Model model, HttpSession session) {
        //For data - get user info from DB
        User userInfo = userService.getUserById(user.getAttribute("userId"));
        model.addAttribute("user", userInfo);

        //For header fragment - get user info from http session
        model.addAttribute("userId",user.getAttribute("userId"));
        model.addAttribute("nickname",session.getAttribute("nickname").toString());
        model.addAttribute("gradeName",session.getAttribute("gradeName").toString());
        model.addAttribute("roleName",session.getAttribute("roleName").toString());
        model.addAttribute("showMenu", true);

        return "myProfile";
    }
}
