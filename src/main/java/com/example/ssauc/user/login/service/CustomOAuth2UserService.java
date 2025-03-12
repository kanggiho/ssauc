package com.example.ssauc.user.login.service;


import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Lazy  // 클래스 전체에 지연 초기화 적용
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UsersRepository userRepository;
    private final @Lazy PasswordEncoder passwordEncoder; // 이제 EncoderConfig에서 주입받음
    private final UsersRepository usersRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        return processOAuth2User(userRequest, oauth2User);
    }

    @SuppressWarnings("unchecked")
    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = null;
        String nickname = null;

        if ("google".equals(registrationId)) {
            email = (String) attributes.get("email");
            nickname = (String) attributes.get("name");
        } else if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            if (response != null) {
                email = (String) response.get("email");
                nickname = (String) response.get("name");
            }
        } else if ("kakao".equals(registrationId)) {
            String kakaoId = String.valueOf(attributes.get("id"));
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount != null) {
                email = (String) kakaoAccount.get("email");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                if (profile != null) {
                    nickname = (String) profile.get("nickname");
                }
            }
            if (email == null) {
                email = kakaoId + "@kakao.com";
            }
            if (nickname == null) {
                nickname = "KakaoUser_" + kakaoId;
            }
        }

        if (email == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_request", "이메일을 가져올 수 없습니다.", null));
        }
        if (nickname == null) {
            nickname = "SocialUser";
        }

        Optional<Users> userOptional = usersRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            OAuth2Error oauth2Error = new OAuth2Error(
                    "additional_info_required",
                    "additional_info_required:" + email + ":" + nickname,
                    null
            );
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.getDescription());
        }

        Map<String, Object> modifiableAttributes = new HashMap<>(attributes);
        modifiableAttributes.put("email", email);

        return new DefaultOAuth2User(
                Collections.singleton(() -> "ROLE_USER"),
                modifiableAttributes,
                "email"
        );
    }
}
