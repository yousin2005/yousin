package kr.co.yousin.info.controller;

import jakarta.servlet.http.HttpServletResponse;
import kr.co.yousin.info.service.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

        model.addAttribute("TokenDays",infoService.getTokenDays(userToken));
        model.addAttribute("PdfFile",infoService.getUserPdfFileList(userToken));

        return "info/yousionInfo";
    }

    @GetMapping("downloadPdf")
    public ResponseEntity<Resource> downloadFile(HttpServletResponse response, @RequestParam String fileDay) {

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

                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileDay + ".pdf\"");
                response.setContentLengthLong(file.length());

                try (InputStream inputStream = new FileInputStream(file);
                     OutputStream outputStream = response.getOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                }
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
