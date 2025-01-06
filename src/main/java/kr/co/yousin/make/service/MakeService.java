package kr.co.yousin.make.service;

import kr.co.yousin.vo.PdfFile;
import kr.co.yousin.vo.PdfFileRepository;
import kr.co.yousin.vo.UserToken;
import kr.co.yousin.vo.TokenRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class MakeService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 16;

    @Autowired
    private PdfFileRepository pdfFileRepository;

    @Autowired
    private TokenRepository tokenRepository;

    public UserToken makeToken(String userIP, int periodValidity){
        String randomString = generateRandomString(LENGTH);

        UserToken token = new UserToken();

        token.setToken(randomString);
        token.setPeriodValidity(periodValidity);
        token.setCreateIP(userIP);
        token.setCreatedDate(LocalDateTime.now());

        return tokenRepository.save(token);
    }

    public String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        if(tokenRepository.existsByToken(sb.toString())){
            return generateRandomString(length);
        }

        return sb.toString();
    }

    public List<PdfFile> getPdfFileList() {
        return pdfFileRepository.findByDelYn("N");
    }

    public String deletePdfFile(String deletePdfDate) {
        if(deletePdfDate != null && !deletePdfDate.isEmpty()){
            try {
                String pdfUploadDir = File.separator + (new File(".")).getCanonicalPath() + File.separator + "tomcat" + File.separator + "temp";
                File pdfFile = new File(pdfUploadDir + File.separator + "yousin_" + deletePdfDate + ".pdf");

                if(pdfFile.isFile()){
                    pdfFile.delete();
                }

                pdfFileRepository.updateDelYn(deletePdfDate, "Y");
            } catch (IOException e) {
                return "PDF File upload failed.";
            }

            return "OK";
        }else{
            return "No Date";
        }

    }

    public ResponseEntity<String> saveUploadPdfFile(String savePdfDate, MultipartFile pdfUploadFile, String userIP) {

        try {

            // 파일을 지정된 경로에 저장
            String pdfUploadDir = File.separator + (new File(".")).getCanonicalPath() + File.separator + "tomcat" + File.separator + "temp";

            File folderFile = new File(pdfUploadDir);

            if( !folderFile.isDirectory() ){
                folderFile.mkdirs();
            }

            File pdfFile = new File(pdfUploadDir + File.separator + "yousin_" + savePdfDate + ".pdf");

            // 기존 파일 삭제
            if(pdfFile.isFile()){
                if(!pdfFile.delete()){
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Before File Delete failed.");
                }
            }

            pdfUploadFile.transferTo(pdfFile);

            Optional<PdfFile> _PdfFile = pdfFileRepository.findByFileDay(savePdfDate);
            if (_PdfFile.isEmpty()) {
                PdfFile pdfFileInfo = new PdfFile();
                pdfFileInfo.setFileDay(savePdfDate);
                pdfFileInfo.setDelYn("N");
                pdfFileInfo.setCreatedDate(LocalDateTime.now());
                pdfFileInfo.setCreateIp(userIP);

                pdfFileRepository.save(pdfFileInfo);
            }else{
                pdfFileRepository.updateDelYn(savePdfDate, "N");
            }

            return ResponseEntity.ok("File uploaded successfully: " + pdfUploadFile.getOriginalFilename());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("PDF File upload failed.");
        }
    }

    public ResponseEntity<String> saveUploadFile(String saveDate, MultipartFile uploadFile) {

        try {
            // 파일을 지정된 경로에 저장
            String uploadDir = File.separator + (new File(".")).getCanonicalPath() + File.separator + "tomcat" + File.separator + "temp";

            File folderFile = new File(uploadDir);

            if( !folderFile.isDirectory() ){
                folderFile.mkdirs();
            }

            File destinationFile = new File(uploadDir + File.separator + "excel.xlsx");

            // 기존 파일 삭제
            if(destinationFile.isFile()){
                if(!destinationFile.delete()){
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Before File Delete failed.");
                }
            }

            uploadFile.transferTo(destinationFile);

            new Thread(() -> makeHtmlFile(saveDate)).start();

            return ResponseEntity.ok("File uploaded successfully: " + uploadFile.getOriginalFilename());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
        }
    }

    public void makeHtmlFile(String makeDay){

    }

    public static void main(String[] args) {

        String makeDay = "2024-11-22";
        String tmpTodayHtml = "<!-- RACE_DATE --> \n  <!-- RACE_NO_LIST -->  \n\n <!-- RACE_CONTENT -->";
   // }

  //  public void makeHtmlFile(String makeDay){

        if(makeDay == null){
            return;
        }
        
        // 날짜 생성
        LocalDate date = LocalDate.parse(makeDay);
        DayOfWeek dayOfWeek = date.getDayOfWeek();    // 요일 계산
        String makeDateText = makeDay.replaceAll("-", ".") + " (" + getKoreanDayOfWeek(dayOfWeek) + ")";

        tmpTodayHtml = tmpTodayHtml.replace("<!-- RACE_DATE -->", makeDateText);

        String uploadFilePath = "";
        String htmlPath = "";

        try {
            String rootPath = (new File(".")).getCanonicalPath();

            System.out.println("getCanonicalPath : " + (new File(".")).getCanonicalPath());
            System.out.println("getAbsolutePath : " + (new File(".")).getAbsolutePath());
            System.out.println("getParentFile : " + (new File(".")).getParentFile());
            System.out.println("getPath : " + (new File(".")).getPath());
            System.out.println("getParent : " + (new File(".")).getParent());


            htmlPath = rootPath + "/tomcat/webapps/ROOT/WEB-INF/classes/templates/info/";
            uploadFilePath = File.separator + rootPath + File.separator + "tomcat" + File.separator + "temp";
        }catch (IOException e){
            e.printStackTrace();
            uploadFilePath = "D:/yousin/yousin/tomcat/temp/excel.xlsx";
            htmlPath = "/jangseongin83/tomcat/webapps/ROOT/WEB-INF/classes/templates/info/";
        }

        File folderFile = new File(uploadFilePath);

        File destinationFile = new File("D:/yousin/yousin/tomcat/temp/excel.xlsx");

        System.out.println("uploadFilePath : " + uploadFilePath);
        System.out.println("htmlPath : " + htmlPath);
        System.out.println();

        int[] meets = {1, 2, 3};
        String[] meetNames = {"서울", "제주", "부경"};
        String[] krs = {"seoul", "cheju", "pusan"};
        File excelFile = null;
        StringBuilder sb = new StringBuilder();
//        Workbook workbook = null;

        ArrayList<String> headerList = new ArrayList<>();

        //String title = "<div class=\"tab-slider-nav-item active\" data-tab=\"1\">서울1</div>";
        //String title = "<div class=\"tab-slider-nav-item active\" data-tab=\"1\">서울1</div>";
        File tmpFile = new File("./");
 //       String nowFilePath = tmpFile.getAbsolutePath().substring(0, tmpFile.getAbsolutePath().length() - 1);
 //       String nowDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
//        String startFilePath = nowDateTime + "_Start.txt";

 //       String raceExcelFilePath = nowDateTime + "_Race.xlsx";

   //     try {
  //          FileWriter startFileWriter = new FileWriter(nowFilePath + startFilePath);
  //          startFileWriter.write("START");

    //        startFileWriter.close();
    //    } catch (Exception e) {
   //         e.printStackTrace();
   //     }

        try {

  //          excelFile = new File(nowFilePath + raceExcelFilePath);
  //          workbook = new XSSFWorkbook();
  //          Sheet sheet = workbook.createSheet("race");

            //if(excelFile.isFile()) {
            //    excelFile.delete();
            //}

            int rowNum = 0;
            int raceListNum = 0;

            for(int meet : meets) {

                String beforeRcDate = "";

                Document list = Jsoup.connect("https://race.kra.co.kr/chulmainfo/ChulmaDetailInfoList.do?Act=02&Sub=1&meet=" + meet).get();

                Elements tableEl = list.select("div.tableType2");
                Elements listTrs = tableEl.select("tbody tr");

                Elements aTageEls = tableEl.select("a[href=#KRA]");



                for (int ri = 0; ri <  aTageEls.size(); ri++) {

                    Element aTageEl = aTageEls.get(ri);
                    Element listTr = listTrs.get(ri);
                    Elements listTd =listTr.select("td");

                    String rcNo = listTd.get(2).text(); // 경주
                    String raceSize = listTd.get(4).text(); // 거리
                    String raceName = listTd.get(7).text();  // 경주명
                    String raceTime = listTd.get(8).text();  // 출발시간
                    String clickOpt = aTageEl.attr("onclick").replace("javascript:goChulmapyo(", "").replace(")", "").replace("\"", "");
                    String[] optArr = clickOpt.split(",");

                    if(!(makeDay.replaceAll("-", "")).equals(optArr[1])){
                        continue;
                    }

                    raceListNum++;

                    String rcDate = optArr[1];

                    headerList.add("<div class='tab-slider-nav-item"+ (raceListNum == 1 ? " active" : "") +"' data-tab='"+ raceListNum +"' time='" + raceTime.replaceAll(":", "") + "'>"+ meetNames[meet - 1] + optArr[2] +"</div>");

                    StringBuilder contentSB = new StringBuilder();

                    try {
                        Random random = new Random();
                        // 1000부터 2000까지의 범위 지정
                        long min = 1000;
                        long max = 2000;

                        // 1000부터 2000까지의 난수 생성
                        long randomNumber = min + ((long) (random.nextDouble() * (max - min)));

                        Thread.sleep(randomNumber);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Document chulmaInfo = Jsoup.connect("https://race.kra.co.kr/chulmainfo/chulmaDetailInfoChulmapyo.do?Sub=1&Act=02&meet="+ meet + "&rcDate=" + rcDate + "&rcNo=" + rcNo).get();

                    Elements chulmaInfoTrs = chulmaInfo.select("div.normal_title tbody tr");

                    Document krInfo = Jsoup.connect("http://race.krj.co.kr/DBInfor/participate/raceinfo.phtml?frame=abilscore&racearea=" + krs[meet - 1] + "&racedate=" + rcDate + "&racenum=" + rcNo).get();

                    Elements krjTrs = krInfo.select("tr[onmouseout=\"this.style.backgroundColor='#ffffff'\"]");


                    Elements alignCTrs = chulmaInfo.select("tr.alignC");

                    Element alignCTr = alignCTrs.get(1);

                    Elements alignCTds = alignCTr.select("td");

                    String raceLevel = alignCTds.get(0).text().replace(raceSize, "").replace("  ", " ");

                    // 경기 날짜 출력
                    if(ri == 0 || beforeRcDate == null || !beforeRcDate.equals(rcDate)) {
                        rowNum++;
                        rowNum++;

    //                    Row raceRow = sheet.createRow(rowNum);
    //                    Cell iitleCell = raceRow.createCell(0);
    //                    iitleCell.setCellType(CellType.STRING);
     //                   iitleCell.setCellValue(rcDate);

                        beforeRcDate = rcDate;
                    }


                    rowNum++;

                    // 엑셀출력
     //               Row raceRow = sheet.createRow(rowNum);
     //               Cell iitleCell = raceRow.createCell(0);
     //               iitleCell.setCellType(CellType.STRING);
     //               iitleCell.setCellValue(meetNames[meet - 1] + " 제 " + rcNo + "경주  " + raceLevel + " " +raceName + " " + raceSize + " [출발 " + raceTime + "]");

                    contentSB.append("<div class='tab-slider-content-item"+ (raceListNum == 1 ? " active" : "") +"' data-content='"+ raceListNum +"'>");
                    contentSB.append("  <table border=\"1\" style=\"border: 0;\">");
                    contentSB.append("      <thead>");
                    contentSB.append("      <tr>");
                    contentSB.append("          <th colspan=\"12\" style=\"color:#fff;font-size:30px;background:#000;font-weight: 900;padding: 3vw 0;letter-spacing: -1px;\">");
                    contentSB.append(meetNames[meet - 1] + optArr[2] + " " + raceSize.replaceAll("M", "") + " " + raceTime);
                    contentSB.append("          </th>");
                    contentSB.append("      </tr>");
                    contentSB.append("      <tr>");
                    contentSB.append("          <th colspan='12' style='font-size:18px;border: 0;padding: 3vw 2vw;background: #FFF;'>" + meetNames[meet - 1] + " 제 " + rcNo + "경주  " + raceLevel + " " +raceName + " " + raceSize + " [출발 " + raceTime + "]" + "</th>");
                    contentSB.append("      </tr>");
                    contentSB.append("  </thead>");
                    contentSB.append("");
                    contentSB.append("");

                    for(int ci = 0 ; chulmaInfoTrs.size() > ci ; ci++) {

                        rowNum++;

                        String krjTds3 = ""; // 질주 숩성
                        String krjTds8 = ""; // 훈련 상태
                        String krjTds10 = ""; // 능력
                        String krjTds11 = ""; // 우승

                        if(krjTrs.size() > ci) {
                            Element krjTr = krjTrs.get(ci);
                            Elements krjTds = krjTr.select("td");

                            krjTds3 = krjTds.get(3).text();
                            if("★".equals(krjTds.get(8).text())) {
                                krjTds8 = "우수";
                            }else if( "◎".equals(krjTds.get(8).text()) || "○".equals(krjTds.get(8).text())) {
                                krjTds8 = "양호";
                            }else if( "△".equals(krjTds.get(8).text()) || "※".equals(krjTds.get(8).text())) {
                                krjTds8 = "보통";
                            }

                            krjTds10 = krjTds.get(10).text();
                            krjTds11 = krjTds.get(11).text();

                            if(krjTds11.length() > 1) {
                                krjTds11 = krjTds11.substring(0, 2).replaceAll("\\.", "");
                            }

                        }else {
                            krjTds3 = "경마문화 정보 없음";
                        }

                        Element chulmaInfoTr = chulmaInfoTrs.get(ci);
                        Elements trList = chulmaInfoTr.select("td");
                        String noInfo = trList.get(0).text(); // 번호
                        String nameInfo = trList.get(1).text(); // 마명
                        String sexInfo = trList.get(3).text(); // 성별
                        String ageInfo = trList.get(4).text(); // 나이
                        String weithInfo = trList.get(6).text(); // 중량
                        String weithGapInfo = trList.get(7).text(); // 증감
                        String driverInfo = trList.get(8).text(); // 기수
                        String trainerInfo = trList.get(9).text(); // 조교
                        String lastRode = ""; // 직전기록
                        String last1No = ""; // 최근 경주 순위 1
                        String last2No = ""; // 최근 경주 순위 2
                        String last3No = ""; // 최근 경주 순위 3
                        String last4No = ""; // 최근 경주 순위 4
                        String lastG3F = ""; // 직전 G3F

                        String nameKeyInfo = trList.get(1).select("a").toString();
                        String[] nameKeyInfos = nameKeyInfo.split("\\(");

                        if(nameKeyInfos.length == 2) {
                            nameKeyInfo = nameKeyInfos[1].split("\\)")[0].replaceAll("'", "");
                            nameKeyInfos = nameKeyInfo.split(",");
                            String hrNo = nameKeyInfos[0].trim();
                            String hrMeet = nameKeyInfos[1].trim();
                            Document hrInfo = Jsoup.connect("https://race.kra.co.kr/racehorse/profileRaceScore.do?Sub=1&Act=02&meet="+ hrMeet + "&hrNo=" + hrNo).get();

                            Elements hrInfoTrs = hrInfo.select("div.tableType2 tbody tr");

                            int raseCnt = 0;

                            for(int hi = 0 ; hrInfoTrs.size() > hi ; hi++) {
                                Element hrInfoTr = hrInfoTrs.get(hi);

                                Elements hrInfoTrTds = hrInfoTr.select("td");
                                String hrInfoTrTd1 =  hrInfoTrTds.get(1).toString(); // 경기일자

                                String[] hrInfoTrTd1s = hrInfoTrTd1.split("\\(");

                                if(hrInfoTrTd1s.length > 1) {

                                    String[] hrInfoTrTd1Times = hrInfoTrTd1s[1].split("\\)")[0].split(",");

                                    if(hrInfoTrTd1Times.length > 1 ) {
                                        String hrInfoTrTd1Meet = hrInfoTrTd1Times[0].replaceAll("&quot;", "").replaceAll(" ", "");
                                        String hrInfoTrTd1Time = hrInfoTrTd1Times[1].replaceAll("&quot;", "").replaceAll(" ", "");
                                        String hrInfoTrTd1Trno = hrInfoTrTd1Times[2].replaceAll("&quot;", "").replaceAll(" ", "");

                                        String hrRaceInfoUrl = "https://race.kra.co.kr/raceScore/ScoretableDetailList.do?meet=" + hrInfoTrTd1Meet + "&realRcDate=" + hrInfoTrTd1Time + "&realRcNo=" + hrInfoTrTd1Trno;

                                        if(hrInfoTrTd1s[0].contains("goCheck")) {
                                            hrRaceInfoUrl = "https://race.kra.co.kr/referee/RacingTrainCheckScoreTable.do?meet=" + hrInfoTrTd1Meet + "&date=" + hrInfoTrTd1Time + "&trno=" + hrInfoTrTd1Trno;
                                        }

                                        String hrInfoTrTd5 =  hrInfoTrTds.get(5).text(); // 등급

                                        if( Integer.parseInt(rcDate) > Integer.parseInt(hrInfoTrTd1Time) && !"".equals(hrInfoTrTd5)) {

                                            String hrInfoTrTd4 =  hrInfoTrTds.get(4).text(); // 거리

                                            String hrInfoTrTd2 =  hrInfoTrTds.get(2).text(); // 마번
                                            String hrInfoTrTd6 =  hrInfoTrTds.get(6).text(); // 순위
                                            String hrInfoTrTd10 =  hrInfoTrTds.get(10).text(); // 경주 기록

                                            if(hrInfoTrTd10.contains("출전")) {
                                                hrInfoTrTd10 = " ";
                                            }

                                            String[] hrInfoTrTd6s = hrInfoTrTd6.split("/");

                                            if( raceSize.replaceAll("M", "").equals(hrInfoTrTd4) ) {

                                                if("".equals(lastRode)) {

                                                    Document hrRaceInfo = Jsoup.connect(hrRaceInfoUrl).get();

                                                    Elements hrRaceInfoTrs = hrRaceInfo.select("div.tableType2").get(1).select("tbody tr");
                                                    for(int hrii = 0 ; hrRaceInfoTrs.size() > hrii ; hrii++) {

                                                        if(hrInfoTrTd2.equals(hrRaceInfoTrs.get(hrii).select("td").get(1).text())) {
                                                            lastG3F = hrRaceInfoTrs.get(hrii).select("td").get(7).text();
                                                            break;
                                                        }
                                                    }
                                                    lastRode = hrInfoTrTd10; // 마지막 경주 기록

                                                }

                                            }

                                            if("".equals(last1No)) { // 1번경주
                                                last1No = hrInfoTrTd6s[0];
                                            }else if("".equals(last2No)) {
                                                last2No = hrInfoTrTd6s[0];
                                            }else if("".equals(last3No)) {
                                                last3No = hrInfoTrTd6s[0];
                                            }else if("".equals(last4No)) {
                                                last4No = hrInfoTrTd6s[0];
                                            }
                                        }


                                    }


                                }




                            }

                        }


                        if(!"".equals(weithGapInfo)) {
                            if( !weithGapInfo.contains(".") ) {
                                weithGapInfo = weithGapInfo + ".0";
                            }
                            if( !weithGapInfo.contains("-") ) {
                                weithGapInfo = "+" + weithGapInfo;
                            }
                        }

                        if("+0.0".equals(weithGapInfo)) {
                            weithGapInfo = "";
                        }

 //                       Row row = sheet.createRow(rowNum);

//                        Cell cell1 = row.createCell(0);
//                        cell1.setCellType(CellType.STRING);
 //                       cell1.setCellValue(noInfo);

 //                       Cell cell2 = row.createCell(1);
 //                       cell2.setCellType(CellType.STRING);

                        if(nameInfo.indexOf("]") > 0) {
                            nameInfo = nameInfo.split("]")[1];
                        }

                        if(nameInfo.length() > 4) {
   //                         cell2.setCellValue(nameInfo.substring(0, 4));
                        }else {
    //                        cell2.setCellValue(nameInfo);
                        }

 //                       Cell cell3 = row.createCell(2);
//                        cell3.setCellType(CellType.STRING);
//                        cell3.setCellValue(sexInfo);

  //                      Cell cell4 = row.createCell(3);
 //                       cell4.setCellType(CellType.STRING);
  //                      cell4.setCellValue(ageInfo);

    //                    Cell cell5 = row.createCell(4);
    //                    cell5.setCellType(CellType.STRING);
    //                    cell5.setCellValue(weithInfo.replaceAll("\\*", ""));

     //                   Cell cell6 = row.createCell(5);
     //                   cell6.setCellType(CellType.STRING);
     //                   cell6.setCellValue(weithGapInfo);

     //                   Cell cell7 = row.createCell(6);
     //                   cell7.setCellType(CellType.STRING);
     //                   cell7.setCellValue( driverInfo.substring(driverInfo.length()-2, driverInfo.length() ) );

      //                  Cell cell8 = row.createCell(7);
      //                  cell8.setCellType(CellType.STRING);
      //                  cell8.setCellValue(trainerInfo);

     //                   Cell cell9 = row.createCell(8); // 직전 g3
     //                   cell9.setCellType(CellType.STRING);
     //                   cell9.setCellValue(lastG3F);

      //                  Cell cell10 = row.createCell(9); // 직전 기록
      //                  cell10.setCellType(CellType.STRING);
       //                 cell10.setCellValue(lastRode);

      //                  Cell cell16 = row.createCell(15); // 승부
      //                  cell16.setCellType(CellType.STRING);
      //                  cell16.setCellValue(krjTds10);

       //                 Cell cell17 = row.createCell(16); // 승부
       //                 cell17.setCellType(CellType.STRING);
      //                  cell17.setCellValue(krjTds11);

     //                   Cell cell18 = row.createCell(17); // 질수습성
  //                      cell18.setCellType(CellType.STRING);
  //                      cell18.setCellValue(krjTds3);

   //                     Cell cell19 = row.createCell(18); // 훈련
   //                     cell19.setCellType(CellType.STRING);
   //                     cell19.setCellValue(krjTds8);

   //                     Cell cell20 = row.createCell(19); // 이력 1
   //                     cell20.setCellType(CellType.STRING);
    //                    cell20.setCellValue(last1No);

     //                   Cell cell21 = row.createCell(20); // 이력 2
     //                   cell21.setCellType(CellType.STRING);
     //                   cell21.setCellValue(last2No);

    //                    Cell cell22 = row.createCell(21); // 이력 3
    //                    cell22.setCellType(CellType.STRING);
    //                    cell22.setCellValue(last3No);

      //                  Cell cell23 = row.createCell(22); // 이력 4
      //                  cell23.setCellType(CellType.STRING);
      //                  cell23.setCellValue(last4No);

                        System.out.print(" /  noInfo :" + noInfo);

                        if(nameInfo.length() > 4) {
                            System.out.print(" /  nameInfo :" + nameInfo.substring(0, 4));
                            //                         cell2.setCellValue(nameInfo.substring(0, 4));
                        }else {
                            System.out.print(" /  nameInfo :" + nameInfo);
                            //                        cell2.setCellValue(nameInfo);
                        }
                        System.out.println(noInfo);
                        System.out.print(" /  ageInfo :" + ageInfo);
                        System.out.print(" /  weithInfo :" + weithInfo.replaceAll("\\*", ""));
                        System.out.print(" /  weithGapInfo :" + weithGapInfo);
                        System.out.print(" /  driverInfo :" +  driverInfo.substring(driverInfo.length()-2, driverInfo.length() ) );
                        System.out.print(" /  trainerInfo :" + trainerInfo);
                        System.out.print(" /  lastG3F :" + lastG3F);
                        System.out.print(" /  lastRode :" + lastRode);
                        System.out.print(" /  krjTds10 :" + krjTds10);
                        System.out.print(" /  krjTds11 :" + krjTds11);
                        System.out.print(" /  krjTds3 :" + krjTds3);
                        System.out.print(" /  krjTds8  :" + krjTds8 );
                        System.out.print(" /  last1No :" + last1No);
                        System.out.print(" /  last2No :" + last2No);
                        System.out.print(" /  last3No :" + last3No);
                        System.out.print(" /  last4No :" + last4No);
                        System.out.println();
                        System.out.println();

                        // tmpTodayHtml = "<!-- RACE_DATE --> \n  <!-- RACE_NO_LIST -->  \n\n <!-- RACE_CONTENT -->";

                    }

                }

            }

            // 파일 저장
    //        FileOutputStream fileOutputStream = new FileOutputStream(nowFilePath + raceExcelFilePath);
     //       workbook.write(fileOutputStream);
       //     fileOutputStream.close();


            // 정규식 패턴 정의 (test 속성 값 추출)
            Pattern pattern = Pattern.compile("time='(\\d+)'");

            String sortedHeaderListStr = headerList.stream()
                    .sorted(Comparator.comparingInt(item -> {
                        Matcher matcher = pattern.matcher(item);
                        if (matcher.find()) {
                            return Integer.parseInt(matcher.group(1));
                        }
                        return Integer.MAX_VALUE; // 정규식 매칭 안 되는 경우
                    }))
                    .collect(Collectors.joining("")); // 문자열로 결합

            tmpTodayHtml = tmpTodayHtml.replace("<!-- RACE_NO_LIST -->", sortedHeaderListStr.toString());



            System.out.println(tmpTodayHtml);



        } catch (IOException e) {
            e.printStackTrace();

            try {
      //          FileWriter errFileWriter = new FileWriter(nowFilePath + nowDateTime + "_Err.txt");
       //         errFileWriter.write("ERR : " + e.getMessage());

        //        errFileWriter.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }

        }
    }

    public static String getKoreanDayOfWeek(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return "월요일";
            case TUESDAY: return "화요일";
            case WEDNESDAY: return "수요일";
            case THURSDAY: return "목요일";
            case FRIDAY: return "금요일";
            case SATURDAY: return "토요일";
            case SUNDAY: return "일요일";
            default: throw new IllegalArgumentException("Invalid day of the week: " + dayOfWeek);
        }
    }

}
