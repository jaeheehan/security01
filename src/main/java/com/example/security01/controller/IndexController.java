package com.example.security01.controller;

import com.example.security01.config.auth.PrincipalDetails;
import com.example.security01.model.User;
import com.example.security01.repository.UserRepository;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication,
        @AuthenticationPrincipal PrincipalDetails userDetails
    ){
        System.out.println(authentication);
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication : " + principal.getUser());
        System.out.println("UserDetails : " + userDetails.getUser());
        return "home";
    }

    @GetMapping("/test/oauth/login")
    public @ResponseBody String testLogin(Authentication authentication,
        @AuthenticationPrincipal OAuth2User oauth
    ){
        System.out.println(authentication);
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication : " + oauth2User.getAttributes());
        System.out.println("oauth2User : " + oauth.getAttributes());
        return "OAuth 세션 정보 확인하기";
    }

    @GetMapping({"","/"})
    public String index(){
        return "index";
    }

    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails){
        System.out.println("principalDetails : " + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin(){
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager(){
        return "manager";
    }

    @GetMapping("/loginForm")
    public String login(){
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(){
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user){
        System.out.println(user);
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encPassword = encoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user);
        return "redirect:/loginForm";
    }

    @GetMapping("/joinProc")
    public @ResponseBody String joinProc(){
        return "회원가입 완료됨!";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/data")
    public @ResponseBody String data(){
        return "데이터 정보";
    }
}
