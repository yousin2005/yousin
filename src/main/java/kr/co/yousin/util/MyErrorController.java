package kr.co.yousin.util;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MyErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        // 커스텀 에러 페이지 반환
        return "redirect:/";
    }

}
