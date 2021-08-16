package com.ceng453.game.api;

import com.ceng453.game.model.Player;
import com.ceng453.game.service.PlayerServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class of Player related end-points
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/player")
@Api(value = "Player API Documentation")
public class PlayerController {

    /**
     * Injection of PlayerServiceImpl
     */
    private final PlayerServiceImpl playerServiceImpl;

    /**
     * This method handles the POST request to register.
     * If it is a valid request, player is added to database.
     * Otherwise, it returns error message.
     * @param player that contains player information
     * @return Response message
     */
    @ApiOperation(  value = "New player registering method",
                    notes = "Username, e-mail and password are needed",
                    response = ResponseEntity.class)
    @PostMapping("/register")
    public ResponseEntity<String> register (@ApiParam(value = "Player information that is player's username,e-mail and password")
                                        @RequestBody Player player){
        return playerServiceImpl.register(player);
    }

    /**
     * This method handles the POST request to login.
     * If it is a valid request, player is successfully login and jwt token is retrieved.
     * Otherwise, it returns error message.
     * @param player that contains username and email information
     * @return Response message
     */
    @ApiOperation(  value = "Player login method",
                    notes = "Username and password needed",
                    response = ResponseEntity.class)
    @PostMapping("/login")
    public ResponseEntity<?> login (@ApiParam(value = "Player information that is player's username and password")
                                        @RequestBody Player  player){
        return playerServiceImpl.login(player);
    }

    /**
     * This method handles GET request to get all players
     * @return List of all players
     */
    @ApiOperation(  value = "Retrieve all player saved to database",
                    notes = "No parameter needed",
                    response = ResponseEntity.class)
    @GetMapping("/getAll")
    public ResponseEntity<List<Player>> getAllPlayers(){
        return playerServiceImpl.getAllPlayers();
    }

    /**
     * This method handles GET request to get a player's information
     * @param username of the wanted player
     * @return Player information or response message
     */
    @ApiOperation(  value = "Retrieve the player's information",
                    notes = "Username as a path variable is needed",
                    response = ResponseEntity.class
    )
    @GetMapping("/getPlayer")
    public ResponseEntity<?> getPlayer(@ApiParam(value = "Username of the player")
                                            @RequestParam String username){
        return playerServiceImpl.getPlayer(username);
    }

    /**
     * This method handles PUT request to update a player's information
     * @param player that contains player's new information
     * @param id of the player
     * @return Response message
     */
    @ApiOperation(  value = "Update the player's information",
                    notes = "Player information and id is needed",
                    response = ResponseEntity.class
    )
    @PutMapping("/update")
    public ResponseEntity<?> updatePlayer(@ApiParam(value = "Player's new information") @RequestBody Player player,
                                          @ApiParam(value = "Player id of the player that will be updated") @RequestParam(value = "id") Long id){
        return playerServiceImpl.updatePlayer(player,id);
    }

    /**
     * This method handles DELETE request to delete the player with given id
     * @param id of the player to be deleted
     * @return Response message
     */
    @ApiOperation(  value = "Delete the player with given id",
                    notes = "Player id is needed",
                    response = ResponseEntity.class)
    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePlayer(@ApiParam(value = "Id of the player that will be deleted") @RequestParam(value = "id") Long id){
        return playerServiceImpl.deletePlayer(id);
    }

    /**
     * This methods handles POST request to reset password and send new password via e-mail when
     * user forgets the password
     * @param emailJson that is the e-mail address of the player
     * @return Response message
     * @throws JSONException when an exception caught
     */
    @ApiOperation(  value = "Send new password via e-mail when user forgets the password",
                    notes = "E-mail is needed",
                    response = ResponseEntity.class)
    @PostMapping("/forgetPassword")
    public ResponseEntity<?> forgetPassword(@RequestBody String emailJson) throws JSONException {
        return playerServiceImpl.forgetPassword(emailJson);
    }
}
