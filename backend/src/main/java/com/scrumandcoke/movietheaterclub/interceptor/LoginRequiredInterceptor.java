package com.scrumandcoke.movietheaterclub.interceptor;

import com.scrumandcoke.movietheaterclub.annotation.LoginRequired;
import com.scrumandcoke.movietheaterclub.dto.UserSessionDetail;
import com.scrumandcoke.movietheaterclub.service.IAMService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.Objects;


@AllArgsConstructor
public class LoginRequiredInterceptor implements HandlerInterceptor {

    private IAMService iamService;

    @Override
    public boolean preHandle(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }


        Method method = handlerMethod.getMethod();
        LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);

        if (Objects.isNull(loginRequired)) {
            return true;
        }

        Cookie[] cookies = httpRequest.getCookies();
        String sessionId = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sid".equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    break;
                }
            }
        }

        if (sessionId == null && Objects.nonNull(httpRequest.getHeader("x-session-id"))) {
            sessionId = httpRequest.getHeader("x-session-id");
        }

        if (sessionId == null) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        UserSessionDetail user = loadUser(sessionId);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, null);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return true;
    }

    private UserSessionDetail loadUser(String sessionId) {
        return iamService.isAuthenticated(sessionId);
    }
}
