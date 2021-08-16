package com.ceng453.game.service;

import com.ceng453.game.dto.SessionRecordDTO;
import com.ceng453.game.model.Player;
import com.ceng453.game.model.SessionRecord;
import com.ceng453.game.repository.PlayerRepository;
import com.ceng453.game.repository.SessionRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementation of Session Record Service Interface
 */
@Service
@RequiredArgsConstructor
public class SessionRecordServiceImpl implements SessionRecordService {

    /**
     * PlayerRepository Injection
     */
    private final PlayerRepository playerRepository;

    /**
     * SessionRecordRepository Injection
     */
    private final SessionRecordRepository sessionRecordRepository;

    private Queue<String> playerQueue = new LinkedList<>();

    /**
     * This method stores the new session to database with given Player id.
     * If there's no player with the given id, function will return error message.
     * @param id Player's id
     * @return Response message
     */
    @Override
    public ResponseEntity<?> addSession(Long id) {
        Optional<Player> player = playerRepository.findById(id);
        if(player.isPresent()){
            Player currentPlayer = player.get();
            SessionRecord sessionRecord = new SessionRecord();
            sessionRecord.setPlayer(currentPlayer);
            sessionRecord.setScore(0L);
            sessionRecord.setDate(LocalDateTime.now());
            sessionRecordRepository.save(sessionRecord);
            return ResponseEntity.ok().body(sessionRecord);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User isn't found");
    }

    /**
     * This method updates the current session's score.
     * If there's no session with the given id, function will return error message.
     * @param id of the current session
     * @param score of the current session
     * @return Response message
     */
    @Override
    public ResponseEntity<?> updateSession(Long id, Long score) {
        Optional<SessionRecord> optionalSessionRecord = sessionRecordRepository.findById(id);
        if(optionalSessionRecord.isPresent()){
            SessionRecord sessionRecord = optionalSessionRecord.get();
            sessionRecord.setScore(score);
            sessionRecord.setDate(LocalDateTime.now());
            sessionRecordRepository.save(sessionRecord);
            return ResponseEntity.ok().body(sessionRecord);
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Session isn't found.");
        }
    }

    /**
     * This method retrieves all records that are saved on database
     * @return list of all records
     */
    @Override
    public ResponseEntity<List<SessionRecordDTO>> listLeaderboard() {
        List<SessionRecordDTO> sessionRecordDTOList = new ArrayList<>();
        List<SessionRecord> sessionRecordList = sessionRecordRepository.findAll();
        sessionRecordList.forEach(e -> sessionRecordDTOList.add(new SessionRecordDTO(e.getPlayer().getUsername(), e.getScore(), e.getDate())));
        return ResponseEntity.ok().body(sessionRecordDTOList);
    }


    /**
     * This method retrieves all records within a month
     * @return list of all records within a month
     */
    @Override
    public ResponseEntity<List<SessionRecordDTO>> listLeaderboardMonthly(){
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusDays(30);
        return getListAfterDate(oneMonthAgo);
    }

    /**
     * This method retrieves all records within a week
     * @return list of all records within a week
     */
    @Override
    public ResponseEntity<List<SessionRecordDTO>> listLeaderboardWeekly(){
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        return getListAfterDate(oneWeekAgo);
    }

    /**
     * This method retrieves waiting queue for  multiplayer part
     * @param playerId of the player
     * @param remoteAddr of the player
     * @return player info in queue or waiting message
     */
    @Override
    public ResponseEntity<String> matchMake(Long playerId, String remoteAddr) {
        Optional<Player> player = playerRepository.findById(playerId);
        if(player.isPresent()){
            if(playerQueue.isEmpty()){
                playerQueue.add(remoteAddr);
                return ResponseEntity.ok().body("Waiting for matchmaking...");
            }
            else{
                String matchPlayer = playerQueue.remove();
                return ResponseEntity.ok().body(matchPlayer);
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User isn't found.");
        }
    }

    private ResponseEntity<List<SessionRecordDTO>> getListAfterDate(LocalDateTime date) {
        List<SessionRecordDTO> sessionRecordDTOListMonthly = new ArrayList<>();
        List<SessionRecord> sessionRecordList = sessionRecordRepository.findAllByDateAfter(date);
        sessionRecordList.forEach(e -> sessionRecordDTOListMonthly.add(new SessionRecordDTO(e.getPlayer().getUsername(), e.getScore(), e.getDate())));
        return ResponseEntity.ok().body(sessionRecordDTOListMonthly);
    }
}
