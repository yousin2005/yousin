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

    @GetMapping("/")
    public String indexPage(Model model) {
        model.addAttribute("aaa", "b");
        return "index";
    }

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
