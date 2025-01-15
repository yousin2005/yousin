package kr.co.yousin.vo;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<UserToken, Long> {

    boolean existsByToken(String token);

    Optional<UserToken> findByToken(String token);

    @Modifying
    @Transactional
    @Query("UPDATE UserToken t SET t.initialAccessDate = :newInitialAccessDate WHERE t.token = :token")
    int updateInitialAccessDate(@Param("token") String token, @Param("newInitialAccessDate") LocalDateTime newInitialAccessDate);

    @Modifying
    @Transactional
    @Query("UPDATE UserToken t SET t.lastAccessDate = :newLastAccessDate WHERE t.token = :token")
    int updaetLastAccessDate(@Param("token") String token, @Param("newLastAccessDate") LocalDateTime newInitialAccessDate);

    Page<UserToken> findAllByOrderByCreatedDateDesc(Pageable pageable);

}
