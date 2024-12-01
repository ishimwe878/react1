package com.Webteck.student.managmment.system.repository;
import com.Webteck.student.managmment.system.model.ResetToken;
import com.Webteck.student.managmment.system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {
    void deleteByToken(String token);
    Optional<ResetToken> findByUser(User user);
    Optional<ResetToken> findByToken(String token);
}