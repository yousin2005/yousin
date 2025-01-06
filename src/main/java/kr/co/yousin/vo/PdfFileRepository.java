package kr.co.yousin.vo;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PdfFileRepository extends JpaRepository<PdfFile, Long> {

    Optional<PdfFile> findByFileDay(String fileDay);

    List<PdfFile> findByDelYn(String delYn);

    @Modifying
    @Transactional
    @Query("UPDATE PdfFile t SET t.delYn = :delYn WHERE t.fileDay = :fileDay")
    int updateDelYn(@Param("fileDay") String fileDay, @Param("delYn") String delYn);

}
