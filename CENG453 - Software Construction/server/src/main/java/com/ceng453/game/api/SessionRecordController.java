package com.ceng453.game.api;

import com.ceng453.game.dto.SessionRecordDTO;
import com.ceng453.game.service.SessionRecordServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Controller class of Session Record related end-points
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sessionRecord")
@Api(value = "Session Record API Documentation")
public class SessionRecordController {

    /**
     * Injection of SessionRecordServiceImpl
     */
    private final SessionRecordServiceImpl sessionRecordServiceImpl;

    /**
     * This method handles the POST request to add new session.
     * If it is a valid request, new session will be successfully created and stored.
     * Otherwise, it returns error message.
     * @param id of the current Player
     * @return Response message
     */
    @ApiOperation(  value = "Create Session method",
                    notes = "Player's ID is needed",
                    response = ResponseEntity.class)
    @PostMapping("/addSession")
    public ResponseEntity<?> addSession (@ApiParam(value = "Player's id information")
                                        @RequestParam Long  id){
        return sessionRecordServiceImpl.addSession(id);
    }


    /**
     * This method handles the PUT request to update session.
     * If it is a valid request, current session's score and date will be successfully updated.
     * Otherwise, it returns an error message.
     * @param id of the current session
     * @param score of the current session
     * @return Response message
     */
    @ApiOperation(  value = "Update Session method",
                    notes = "Session Id and new score are needed",
                    response = ResponseEntity.class)
    @PutMapping("/updateSession")
    public ResponseEntity<?> updateSession (@ApiParam(value = "Session Id") @RequestParam Long id,
                                            @ApiParam(value = "New session score") @RequestParam Long score){
        return sessionRecordServiceImpl.updateSession(id, score);
    }

    /**
     * This method handles GET request to get all records
     * @return List of all records
     */
    @ApiOperation(  value = "Retrieve all records saved to database",
            notes = "No parameter needed",
            response = ResponseEntity.class)
    @GetMapping("/listLeaderboard")
    public ResponseEntity<List<SessionRecordDTO>> listLeaderboard(){
        return sessionRecordServiceImpl.listLeaderboard();
    }


    /**
     * This method handles GET request to get all records within a month
     * @return List of all records within a month
     */
    @ApiOperation(value = "Retrieve all records within a month",
                    notes = "No parameter needed",
                    response = ResponseEntity.class)
    @GetMapping("/listLeaderboardMonthly")
    public ResponseEntity<List<SessionRecordDTO>> listLeaderboardMonthly(){
        return sessionRecordServiceImpl.listLeaderboardMonthly();
    }

    /**
     * This method handles GET request to get all records within a week
     * @return List of all records within a week
     */
    @ApiOperation(  value = "Retrieve all records within a week",
                    notes = "No parameter needed",
                    response = ResponseEntity.class)
    @GetMapping("/listLeaderboardWeekly")
    public ResponseEntity<List<SessionRecordDTO>> listLeaderboardWeekly(){
        return sessionRecordServiceImpl.listLeaderboardWeekly();
    }

    /**
     * This method handles GET request to get IPAddress of a player if waiting for opponent.
     * @param playerId
     * @param httpServletRequest
     * @return IPAddress or waiting message
     */
    @ApiOperation(  value = "Retrieve waiting queue for multiplayer",
                    notes = "PlayerId needed",
                    response = ResponseEntity.class)
    @GetMapping("/matchMake")
    public ResponseEntity<String> matchMake(@RequestParam Long playerId, HttpServletRequest httpServletRequest){
        return sessionRecordServiceImpl.matchMake(playerId,httpServletRequest.getRemoteAddr());
    }
}
