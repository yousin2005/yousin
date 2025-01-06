package kr.co.yousin.vo;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserPdfInfoRepository extends JpaRepository<UserPdfInfo, Long> {

    int findShowCntByTokenAndFileDay(String token, String fileDay);

    @Query(value = "SELECT pf.fileDay, IFNULL(upi.showCnt, 0) AS showCnt FROM PdfFile pf LEFT JOIN UserPdfInfo upi ON upi.fileDay = pf.fileDay and upi.token = :token WHERE pf.delYn = 'N' ")
    List<Object[]> findPdfFileWithUserPdfInfo(String token);

    @Query(value = "SELECT pf.fileDay, IFNULL(upi.showCnt, 0) AS showCnt FROM PdfFile pf LEFT JOIN UserPdfInfo upi ON upi.fileDay = pf.fileDay and upi.token = :token WHERE pf.delYn = 'N' AND pf.fileDay = :fileDay ")
    Object[][] findPdfFileWithUserPdfInfoByTokenAndFileDay(String token, String fileDay);

    @Modifying
    @Transactional
    @Query("UPDATE UserPdfInfo t SET t.showCnt = (t.showCnt + 1) WHERE t.token = :token and t.fileDay = :fileDay")
    int updateShowCnt(@Param("token") String token, @Param("fileDay") String fileDay);
}
