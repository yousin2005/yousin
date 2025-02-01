package kr.co.yousin.vo;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SystemMessageRepository extends JpaRepository<SystemMessage, Long> {

    @Query(value = "select sm.periodDate, sm.message from SystemMessage sm WHERE sm.periodDate >= :checkDate AND sm.delYn = 'N' ORDER BY sm.createdDate LIMIT 1 ")
    Object[][] findInitSystemMessageInfo(String checkDate);

    @Modifying
    @Transactional
    @Query("UPDATE SystemMessage sm SET sm.delYn = :delYn WHERE sm.messageId = :messageId")
    int updateDelYn(@Param("messageId") String messageId, @Param("delYn") String delYn);

    Page<SystemMessage> findAllByOrderByCreatedDateDesc(Pageable pageable);
}
