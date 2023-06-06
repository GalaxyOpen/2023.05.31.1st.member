package com.example.member.Config;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

// 이 클래스의 목적은, 로그인 여부를 확인하고 로그인 상태라면 사용자가 요청한 주소로 보내고
// 로그인하지 않은 상태라면 컨트롤러의 로그인 요청 주소로 넘김.
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws IOException {
        // Tomcat(WAS)이 HttpSevletRequest, HttpsevletRespose 객체를 만들어줌.
        // 사용자가 요청한 주소 확인
        String requestURI = request.getRequestURI();
        System.out.println("requestURI = " + requestURI);

        // 세션 객체 생성
        HttpSession session = request.getSession();

        // 세션에 저장된 로그인 정보 확인
        if(session.getAttribute("loginEmail")==null){
            // 로그인하지 않았다면 로그인페이지로 보내면서
            // 요청한 주소값도 같이 보냄
            response.sendRedirect("/member/login?redirectURI="+requestURI);
            // 즉, 이 때 로그인을 다시 하면 원래 요청했던 주소값으로 감.
            return false;
        }else{
            //로그인 상태라면 요청한 페이지로 보냄
            return true;
        }
    }
}
