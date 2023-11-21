package com.example.security01.config;

import com.example.security01.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 1. 코드받기(인증) , 2.엑세스토큰(권한)
// 3. 사용자 프로필 정보를 가져오고 4-1. 그정보를 토대로 회원가입을 자동으로 진행시키도 함
// 4-2 (이메일 전화번호, 이름 아이디, ) 쇼핑몰 -> (집주소) 백화점몰 -> (vip등급, 일반등급)

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터 체인에 등록이 됩니다.
@EnableGlobalMethodSecurity( securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;


    @Override
    protected void configure(HttpSecurity http) throws Exception  {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login") // /login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해 줍니다.
                .defaultSuccessUrl("/")
                .and()
                .oauth2Login()
                .loginPage("/loginForm") // 구글 로그인 완료되고 후처리가 필요함. Tip 코드X,(엑세스 토큰 + 사용자 프로필 정보 0)
                .userInfoEndpoint()
                .userService(principalOauth2UserService)
        ;

    }





}
