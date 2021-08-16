package com.ceng453.game.repository;

import com.ceng453.game.model.SessionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Session Record Repository Interface
 */
public interface SessionRecordRepository extends JpaRepository<SessionRecord, Long> {
    /**
     * This method retrieves all session records that has the date information after the given date
     * @param date which is beginning date of the session record list
     * @return SessionRecord list
     */
    List<SessionRecord> findAllByDateAfter(LocalDateTime date);
}
