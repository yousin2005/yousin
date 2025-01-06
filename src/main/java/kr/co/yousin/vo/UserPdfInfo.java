package kr.co.yousin.vo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@IdClass(UserPdfInfoID.class)
public class UserPdfInfo {

    @Id
    private String token;
    @Id
    private String fileDay;
    private int showCnt;
    private LocalDateTime createdDate;

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

    public String getFileDay() {
        return fileDay;
    }

    public void setFileDay(String fileDay) {
        this.fileDay = fileDay;
    }

    public int getShowCnt() { return showCnt;  }

    public void setShowCnt(int showCnt) {this.showCnt = showCnt; }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getString(){
        return "{\"token\":\""+token+"\", \"fileDay\":\""+fileDay+"\", \"createdDate\":\""+createdDate+"\"}";
    }
}
