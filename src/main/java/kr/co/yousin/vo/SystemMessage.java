package kr.co.yousin.vo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class SystemMessage {

    @Id
    private int messageId;
    private String periodDate;
    private String message;
    private LocalDateTime createdDate;
    private String createIp;
    private String delYn;

    public int getMessageId() { return messageId; }

    public void setMessageId(int messageId) { this.messageId = messageId; }

    public void setPeriodDate(String periodDate) { this.periodDate = periodDate; }

    public String getPeriodDate() { return periodDate; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getDelYn() { return delYn; }

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
        return "{\"messageId\":\""+messageId+"\", \"periodDate\":\""+periodDate+"\", \"message\":\""+message+"\", \"delYn\":\""+ delYn +"\", \"createdDate\":\""+createdDate+"\", \"createIP\":\""+createIp+"\"}";
    }
}
