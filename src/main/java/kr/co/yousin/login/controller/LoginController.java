package kr.co.yousin.login.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.yousin.login.service.LoginService;
import kr.co.yousin.make.service.MakeService;
import kr.co.yousin.util.Util;
import kr.co.yousin.vo.UserToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping("code")
    public String indexPage(HttpServletRequest request, Model model) {
        model.addAttribute("error", request.getParameter("error"));

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
