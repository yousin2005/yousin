package kr.co.yousin.vo;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Embeddable
public class UserPdfInfoID implements Serializable {

    private String token;
    private String fileDay;

    // 기본 생성자
    public UserPdfInfoID() {}

    public UserPdfInfoID(String token, String fileDay) {
        this.token = token;
        this.fileDay = fileDay;
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, fileDay);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserPdfInfo that = (UserPdfInfo) obj;
        return Objects.equals(token, that.getToken()) && Objects.equals(fileDay, that.getFileDay());
    }

}
