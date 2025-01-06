package kr.co.yousin.vo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class PdfFile {

    @Id
    private String fileDay;
    private String delYn;
    private LocalDateTime createdDate;
    private String createIp;

    public String getFileDay() {
        return fileDay;
    }

    public void setFileDay(String fileDay) {
        this.fileDay = fileDay;
    }

    public String getDelYn() {
        return delYn;
    }

    public void setDelYn(String delYn) {
        this.delYn = delYn;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreateIp() {
        return createIp;
    }

    public void setCreateIp(String createIp) {
        this.createIp = createIp;
    }

    public String getString(){
        return "{\"fileDay\":\""+fileDay+"\", \"delYn\":\""+ delYn +"\", \"createdDate\":\""+createdDate+"\", \"createIP\":\""+createIp+"\"}";
    }
}
