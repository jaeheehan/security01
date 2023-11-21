package com.example.security01.config.oauth;

import com.example.security01.config.auth.PrincipalDetails;
import com.example.security01.model.User;
import com.example.security01.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    // 후처리 함수
    // 구글로 받은 userRequest 데이터에 대한 후처리되는 함수
    @Override
    public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("getClientRegistration : " + userRequest.getClientRegistration()); // registertaionId 로 어떤 OAuth 로 로그인
        System.out.println("getAccessToken : " + userRequest.getAccessToken().getTokenValue());


        OAuth2User oauth2User = super.loadUser(userRequest);
        // 구글로그인 버튼 클릭 -> 구글로그인창 -> 로그인 완료 -> code 를 리턴(OAuth-Client 라이브러리) -> AccessToken 요청
        // userRequest 정보 -> loadUser 함수 호출 ->  회원프로필 받아야함
        System.out.println("getAttributes : " + super.loadUser(userRequest).getAttributes());

        String provider = userRequest.getClientRegistration().getClientId(); // google
        String providerId = oauth2User.getAttribute("sub"); // google 의 기본키와 같은 값
        String username = provider + "_" + providerId;
        String password = bCryptPasswordEncoder.encode("겟인데어");
        String email = oauth2User.getAttribute("email");
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);
        if(userEntity == null) {
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }

        // 회원가입을 강제로 진행해볼 예정
        return new PrincipalDetails(userEntity, oauth2User.getAttributes());
    }
}
