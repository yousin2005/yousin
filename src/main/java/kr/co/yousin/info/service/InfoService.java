package kr.co.yousin.info.service;

import kr.co.yousin.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class InfoService {

    @Autowired
    private UserPdfInfoRepository userPdfInfoRepository;

    @Autowired
    private TokenRepository tokenRepository;

    public Map<String, Object> getTokenDays(String token) {
        Map<String, Object> userInfoMap = new HashMap<>();

        Optional<UserToken> _userToken = tokenRepository.findByToken(token);

        if(_userToken != null && _userToken.isPresent()){
            UserToken userToken = _userToken.get();

            LocalDateTime startDate = userToken.getInitialAccessDate();
            LocalDateTime endDay = startDate.plusDays(userToken.getPeriodValidity());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

            userInfoMap.put("StartDay", startDate.format(formatter));
            userInfoMap.put("EndDay", endDay.format(formatter));

            String tokenType = userToken.getTokenType();

            userInfoMap.put("TokenType", tokenType);
        }

        return userInfoMap;
    }

    public Map<String, Object> getUserPdfFileList(String token, String tokenType) {

        Map<String, Object> userPdfFileMap = new HashMap<>();

        List<Object[]> userList = userPdfInfoRepository.findPdfFileWithUserPdfInfo(token);

        // 현재 날짜 가져오기
        LocalDate today = LocalDate.now();

        LocalDate friday = today.with(DayOfWeek.FRIDAY);
        LocalDate saturday = today.with(DayOfWeek.SATURDAY);
        LocalDate sunday = today.with(DayOfWeek.SUNDAY);

        // 날짜 포맷터
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 포맷된 날짜 출력
        String formattedFriday = friday.format(formatter);
        String formattedSaturday = saturday.format(formatter);
        String formattedSunday = sunday.format(formatter);

        DateTimeFormatter showFormatter = DateTimeFormatter.ofPattern("MM.dd");

        for(Object[] obj : userList){

            if( tokenType.contains("4") && formattedFriday.equals(obj[0]) ){
                userPdfFileMap.put("FridayCnt",obj[1]);
                userPdfFileMap.put("FridayFileDay", formattedFriday);
                userPdfFileMap.put("Friday", friday.format(showFormatter));
            }
            if( tokenType.contains("5") && formattedSaturday.equals(obj[0]) ){
                userPdfFileMap.put("SaturdayCnt",obj[1]);
                userPdfFileMap.put("SaturdayFileDay", formattedSaturday);
                userPdfFileMap.put("Saturday", saturday.format(showFormatter));
            }
            if( tokenType.contains("6") && formattedSunday.equals(obj[0]) ){
                userPdfFileMap.put("SundayCnt",obj[1]);
                userPdfFileMap.put("SundayFileDay", formattedSunday);
                userPdfFileMap.put("Sunday", sunday.format(showFormatter));
            }
        }

        return userPdfFileMap;
    }

    public int getUserFileInfo(String token, String fileDay){

        Object[][] pdfFileInfo = userPdfInfoRepository.findPdfFileWithUserPdfInfoByTokenAndFileDay(token, fileDay);

        if(pdfFileInfo == null){
            return 404; // 파일 없음
        }

        if((int)pdfFileInfo[0][1] >= 5){
            return 403; // 초과
        }

        return (int)pdfFileInfo[0][1];
    }

    public Path getFilePath(String token, String fileDay, int fileCnt){
        try {

            String currentDir = new File(".").getCanonicalPath();


            // 업로드 디렉토리 경로 생성
            //Path uploadDir = Paths.get(new File(currentDir).getParent(), "temp");

            System.out.println(currentDir);


            currentDir = "/jangseongin83/tomcat/temp";

            // 업로드 디렉토리 경로 생성
            Path uploadDir = Paths.get(currentDir);

            // 파일 경로 생성
            Path filePath = uploadDir.resolve("yousin_" + fileDay + ".pdf");
            File file = filePath.toFile();

            if(file.isFile()){
                // 파일 조회 카운트 증가
                if(fileCnt == 0){
                    UserPdfInfo userPdfInfo = new UserPdfInfo();
                    userPdfInfo.setToken(token);
                    userPdfInfo.setFileDay(fileDay);
                    userPdfInfo.setShowCnt(1);
                    userPdfInfo.setCreatedDate(LocalDateTime.now());

                    userPdfInfoRepository.save(userPdfInfo);

                    //1회권 확인
                    Optional<UserToken> _userToken = tokenRepository.findByToken(token);

                    if(_userToken != null && _userToken.isPresent()){
                        UserToken userToken = _userToken.get();

                        int periodValidity = userToken.getPeriodValidity();

                        // 1회 권인 경우 다른 날짜 차단
                        if(periodValidity == 1){
                            // 현재 날짜 가져오기
                            LocalDate today = LocalDate.now();

                            LocalDate friday = today.with(DayOfWeek.FRIDAY);
                            LocalDate saturday = today.with(DayOfWeek.SATURDAY);
                            LocalDate sunday = today.with(DayOfWeek.SUNDAY);

                            // 날짜 포맷터
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                            // 포맷된 날짜 출력
                            String formattedFriday = friday.format(formatter);
                            String formattedSaturday = saturday.format(formatter);
                            String formattedSunday = sunday.format(formatter);

                            userPdfInfo.setShowCnt(5);

                            if(!fileDay.equals(formattedFriday)){
                                userPdfInfo.setFileDay(formattedFriday);
                                userPdfInfoRepository.save(userPdfInfo);
                            }
                            if(!fileDay.equals(formattedSaturday)){
                                userPdfInfo.setFileDay(formattedSaturday);
                                userPdfInfoRepository.save(userPdfInfo);
                            }
                            if(!fileDay.equals(formattedSunday)){
                                userPdfInfo.setFileDay(formattedSunday);
                                userPdfInfoRepository.save(userPdfInfo);
                            }
                        }
                    }
                   
                }else{
                    userPdfInfoRepository.updateShowCnt(token, fileDay);
                }

                


                return filePath;
            }else{
                return null;
            }

            

        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

}
