package kr.co.yousin.setting;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String errorMessage = "NotFound";

        switch (exception.getMessage()){

            case "Locked" :
                errorMessage = "Locked";
                break;
            case "AccountExpired" :
                errorMessage = "AccountExpired";
                break;
        }

        // 오류 메시지를 URL 파라미터로 전달
        response.sendRedirect("/code?errorCode=" + errorMessage);
    }
}
