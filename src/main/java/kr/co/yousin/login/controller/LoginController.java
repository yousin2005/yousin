package kr.co.yousin.login.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.yousin.login.service.LoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/code")
    public String indexPage(HttpServletRequest request, HttpServletResponse response, Model model) {

        // 뷰에서 사용할 오류 메시지 전달(필요 시)
        //model.addAttribute("error", request.getParameter("error"));

        // [선택] 시스템 메시지 차단 로직을 쓰려면 주석을 해제하세요.
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        // String nowStr = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter);
        // Object[] msgInfo = loginService.getSystemMessage(nowStr);
        // if (!"Y".equals(request.getParameter("ADMIN")) && msgInfo != null && msgInfo.length > 0) {
        //     try {
        //         response.setContentType("text/html; charset=UTF-8");
        //         response.getWriter().write("<script>alert('"+msgInfo[1]+"'); location.href='/';</script>");
        //         response.getWriter().flush();
        //     } catch (IOException e) {
        //         e.printStackTrace();
        //     }
        //     return null;
        // }

        // 시간/요일 제한 완전 제거: 언제든 접근 허용

        // 로그인되어 있으면 정보 페이지로 리다이렉트
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String
                     && "anonymousUser".equals(authentication.getPrincipal()))) {
            return "redirect:/info/yousionInfo";
        }

        // 비로그인: code 뷰 표시
        return "code";
    }
}
