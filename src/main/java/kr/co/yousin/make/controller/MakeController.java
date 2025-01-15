package kr.co.yousin.make.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.yousin.make.service.MakeService;
import kr.co.yousin.util.Util;
import kr.co.yousin.vo.UserToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequestMapping("/admin/")
public class MakeController {

    @Autowired
    private MakeService makeService;

    @GetMapping("makePage")
    public String makePage(Model model) {

        model.addAttribute("PdfFileList",makeService.getPdfFileList());

        return "admin/makePage";
    }

    @PostMapping("makeToken")
    @ResponseBody
    public String makeTest(HttpServletRequest request, @RequestBody Map<String, Object> params)  {

        String userIP = Util.getClientIp(request);
        int day = Integer.parseInt((String)params.get("day"));
        UserToken token = makeService.makeToken(userIP, day);


        return token.getString();
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("saveDate") String saveDate, @RequestParam("file") MultipartFile uploadFile) {

        if(uploadFile == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NO File.");
        }

        String orgFileName = uploadFile.getOriginalFilename();
        if (orgFileName == null || !orgFileName.toLowerCase().endsWith(".xlsx")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NO  File.");
        }

        return makeService.saveUploadFile(saveDate, uploadFile);
    }

    @PostMapping("/uploadpdf")
    public ResponseEntity<String> handlePdfFileUpload(HttpServletRequest request, @RequestParam("savePdfDate") String saveDate, @RequestParam("PdfFile") MultipartFile uploadFile) {

        if(uploadFile == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NO File.");
        }

        String orgFileName = uploadFile.getOriginalFilename();
        if (orgFileName == null || !orgFileName.toLowerCase().endsWith(".pdf")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NO  File.");
        }

        return makeService.saveUploadPdfFile(saveDate, uploadFile, Util.getClientIp(request));
    }

    @PostMapping("users")
    public ResponseEntity<Page<UserToken>> getUsers(@RequestBody Map<String, Object> params) {
        int page = 0;
        if(params.get("page") != null){
            page = (int)params.get("page");
        }
        int size = 10;
        if(params.get("size") != null){
            size = (int)params.get("size");
        }

        Page<UserToken> users = makeService.getUsers(page, size);
        return ResponseEntity.ok(users); // JSON 형식으로 반환
    }

    @PostMapping("deletePdf")
    @ResponseBody
    public String handlePdfFileDelete(@RequestBody Map<String, Object> params)  {

        return makeService.deletePdfFile((String)params.get("deletePdfDate"));
    }

}
