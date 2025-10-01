package kr.co.yousin.login.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.yousin.login.service.LoginService;

import kr.co.yousin.vo.SystemMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping("code")
    public String indexPage(HttpServletRequest request, HttpServletResponse response, Model model) {

        model.addAttribute("error", request.getParameter("error"));

        // 한국 시간대 설정
        ZoneId koreaZone = ZoneId.of("Asia/Seoul");
        LocalDateTime now = LocalDateTime.now(koreaZone);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

              Object[] msgInfo = loginService.getSystemMessage(now.format(formatter));

        // 시스템 메세지
              if(!"Y".equals(request.getParameter("ADMIN")) && msgInfo != null && msgInfo.length > 0){
              
                  try {
                // 요일/시간 조건에 맞지 않을 경우 경고 메시지와 리다이렉트
                      response.setContentType("text/html; charset=UTF-8");
                      response.getWriter().write("<script>alert('"+msgInfo[1]+"'); location.href='/';</script>");
                      response.getWriter().flush();
                  }catch (IOException e){
                      e.printStackTrace();
                  }
              
                  return null;
              }

        // 요일 및 시간 정보 가져오기
        //주석      DayOfWeek day = now.getDayOfWeek(); // 목요일은 THURSDAY
        //주석      int hour = now.getHour();
             
        // 허용된 요일과 시간 (목요일 12시 이후 ~ 일요일 )
        //주석      boolean isShow = (day == DayOfWeek.THURSDAY && hour >= 12) ||
        //주석             (day == DayOfWeek.FRIDAY) ||
        //주석             (day == DayOfWeek.SATURDAY) ||
        //주석             (day == DayOfWeek.SUNDAY);
             
        //주석     if("Y".equals(request.getParameter("ADMIN"))){
        //주석         isShow = true;
        //주석     }

        //주석      if (!isShow) {
        //주석          try {
        //주석        // 요일/시간 조건에 맞지 않을 경우 경고 메시지와 리다이렉트
        //주석              response.setContentType("text/html; charset=UTF-8");
        //주석              response.getWriter().write("<script>alert('서비스 이용시간이 아닙니다. \\n(서비스 시간 : 목요일 12시 ~ 일요일 24시)'); location.href='/';</script>");
        //주석              response.getWriter().flush();
        //주석          }catch (IOException e){
        //주석              e.printStackTrace();
        //주석          }
              
        //주석          return null;
        //주석      }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null){
            if(authentication.isAuthenticated() &&
                    !(authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()) )){
                //return "/info/today";
                return "redirect:/info/yousionInfo";
            }
        }

        return "code";
    }
}
