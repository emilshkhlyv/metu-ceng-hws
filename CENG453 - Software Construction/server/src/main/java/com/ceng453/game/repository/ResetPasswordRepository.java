package com.ceng453.game.repository;

import com.ceng453.game.model.Player;
import com.ceng453.game.model.ResetCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Reset Password Repository Interface
 */
public interface ResetPasswordRepository extends JpaRepository<ResetCode, Long> {
    Optional<ResetCode> findByPlayer(Player player);
}
