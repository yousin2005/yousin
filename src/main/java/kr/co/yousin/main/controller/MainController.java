package kr.co.yousin.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class MainController {

//주석    @GetMapping("/")
//주석    public String indexPage(Model model) {
//주석        model.addAttribute("aaa", "b");
//주석        return "index";
//주석    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("aaa", "b");
        return "redirect:/";
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("aaa", "b");
        return "redirect:/admin/makePage";
    }
}
