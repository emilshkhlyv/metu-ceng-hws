package com.ceng453.game.repository;

import com.ceng453.game.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Player Repository Interface
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player,Long> {
    Optional<Player> findByUsername(String username);

    @Transactional
    void deletePlayerById(Long id);

    Optional<Player> findPlayerByEmail(String email);

}
