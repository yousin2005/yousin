package kr.co.yousin.info.controller;

import jakarta.servlet.http.HttpServletResponse;
import kr.co.yousin.info.service.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

@Controller
@RequestMapping("/info/")
public class InfoController {

    @Autowired
    private InfoService infoService;

    @GetMapping("today")
    public String todayPage(Model model) {

        return "info/today";
    }

    @GetMapping("yousionInfo")
    public String yousionInfoPage(Model model) {

        String userToken = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            userToken = authentication.getName(); // 사용자 아이디 (기본적으로 username 반환)
        }

        if(userToken == null || userToken.isEmpty()){
          return "code";
        }

        Map<String, Object> tokenDays = infoService.getTokenDays(userToken);

        model.addAttribute("TokenDays", tokenDays);
        model.addAttribute("PdfFile",infoService.getUserPdfFileList(userToken, (String)tokenDays.get("TokenType")));

        return "info/yousionInfo";
    }

    @GetMapping("downloadPdf")
    public ResponseEntity<Resource> downloadFile(@RequestParam String fileDay) {

        String userToken = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            userToken = authentication.getName(); // 사용자 아이디 (기본적으로 username 반환)
        }

        if(userToken != null && !userToken.isEmpty()){

            int checkNum = infoService.getUserFileInfo(userToken, fileDay);
            if (checkNum > 5) {
                return ResponseEntity.status(checkNum).body(null);
            }

            try {

                Path filePath = infoService.getFilePath(userToken, fileDay, checkNum);
                File file = filePath.toFile();
                Resource resource = new FileSystemResource(file);

                // 응답 헤더 설정
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDay + ".pdf\"");
                headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
                headers.setContentLength(file.length());

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
                /*
                // Replace this with the actual file path
                Path filePath = infoService.getFilePath(userToken, fileDay, checkNum);
                Resource resource = new UrlResource(filePath.toUri());

                long fileSize = Files.size(filePath);

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDay + ".pdf\"")
                        .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize))
                        .body(resource);

                 */
            } catch (Exception e) {
                return ResponseEntity.status(404).body(null);
            }
        }
        return ResponseEntity.status(404).body(null);
    }
}
