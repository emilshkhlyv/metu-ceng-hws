package com.ceng453.game.service;

import com.ceng453.game.dto.SessionRecordDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Session Record Service Interface
 */
public interface SessionRecordService {
    ResponseEntity<?> addSession(Long id);

    ResponseEntity<?> updateSession(Long id, Long score);

    ResponseEntity<List<SessionRecordDTO>> listLeaderboard();

    ResponseEntity<List<SessionRecordDTO>> listLeaderboardMonthly();

    ResponseEntity<List<SessionRecordDTO>> listLeaderboardWeekly();

    ResponseEntity<String> matchMake(Long playerId, String remoteAddr);
}
