package com.example.ssauc.user.login.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class CustomOAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        log.warn("소셜 로그인 실패: {}", exception.getMessage());

        // OAuth2AuthenticationException 일 경우, description에서 데이터 추출
        if (exception instanceof OAuth2AuthenticationException oauthEx) {
            String desc = oauthEx.getError().getDescription();
            // ex: "inactive|test@example.com|홍길동"
            if (desc != null && desc.startsWith("inactive|")) {
                String[] parts = desc.split("\\|");
                if (parts.length >= 3) {
                    // parts[1] -> email, parts[2] -> nickname
                    String email = parts[1];
                    String nickname = parts[2];

                    // URL 인코딩 (한글 닉네임 등)
                    String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
                    String encodedNickname = URLEncoder.encode(nickname, StandardCharsets.UTF_8);

                    // signup 페이지로 리다이렉트, 쿼리 파라미터에 이메일/닉네임 전달
                    String redirectUrl = "/signup?inactive=true&email=" + encodedEmail + "&nickname=" + encodedNickname;
                    response.sendRedirect(redirectUrl);
                    return; // 메서드 종료
                }
            }
        }

        // 그 외 일반적인 로그인 실패 처리
        response.sendRedirect("/login?error=true");
    }
}
