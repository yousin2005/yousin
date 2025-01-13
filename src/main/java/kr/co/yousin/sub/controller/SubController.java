package kr.co.yousin.sub.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.yousin.make.service.MakeService;
import kr.co.yousin.util.Util;
import kr.co.yousin.vo.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
public class SubController {

    @GetMapping("/sub/{page}")
    public String dynamicTestPage(@PathVariable String page) {
        return "sub/" + page;
    }

}
