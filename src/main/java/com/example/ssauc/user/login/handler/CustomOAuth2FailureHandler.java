package com.example.ssauc.user.login.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomOAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        System.out.println("OAuth2 인증 실패: " + exception.getMessage());

        String msg = exception.getMessage();
        // "additional_info_required:이메일:닉네임" 형식인지 확인
        if (msg != null && msg.startsWith("additional_info_required:")) {
            String[] data = msg.split(":");
            if (data.length >= 3) {
                // 한글 및 특수문자가 포함될 수 있으므로 URL 인코딩 처리
                String email = URLEncoder.encode(data[1], StandardCharsets.UTF_8);
                String nickname = URLEncoder.encode(data[2], StandardCharsets.UTF_8);
                // signup.html로 이동할 때, 인코딩된 이메일과 닉네임을 쿼리 파라미터로 전송
                response.sendRedirect("/signup?email=" + email + "&nickname=" + nickname);
                return;
            }
        }
        // 그 외 OAuth2 로그인 실패 시 기본 실패 핸들러 동작
        super.onAuthenticationFailure(request, response, exception);
    }
}
