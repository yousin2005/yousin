package kr.co.yousin.vo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class UserToken {

    @Id
    private String token;
    private int periodValidity;
    private String tokenType;
    private LocalDateTime initialAccessDate;
    private LocalDateTime lastAccessDate;
    private LocalDateTime createdDate;
    private String createIP;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getPeriodValidity() {
        return periodValidity;
    }

    public void setPeriodValidity(int periodValidity) {
        this.periodValidity = periodValidity;
    }

    public LocalDateTime getInitialAccessDate() {
        return initialAccessDate;
    }

    public String getTokenType() {
        if(tokenType == null || tokenType.isEmpty()){
            return "4|5|6";
        }
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setInitialAccessDate(LocalDateTime initialAccessDate) {
        this.initialAccessDate = initialAccessDate;
    }

    public LocalDateTime getLastAccessDate() {
        return lastAccessDate;
    }

    public void setLastAccessDate(LocalDateTime lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreateIP() {
        return createIP;
    }

    public void setCreateIP(String createIP) {
        this.createIP = createIP;
    }

    public UserRole getRole() {
        //TODO DB 관리자 권한으로 처리
        if("ADMIN01088410814".equals(this.token) || "ADMIN01031938511".equals(this.token)){
            return UserRole.ROLE_ADMIN;
        }

        return UserRole.ROLE_USER;
    }

    public String getString(){
        return "{\"token\":\""+token+"\", \"periodValidity\":\""+ periodValidity +"\", \"lastAccessDate\":\""+lastAccessDate+"\", \"createdDate\":\""+createdDate+"\", \"createIP\":\""+createIP+"\"}";
    }
}
