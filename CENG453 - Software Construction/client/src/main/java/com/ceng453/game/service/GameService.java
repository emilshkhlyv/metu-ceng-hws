package com.ceng453.game.service;

import com.ceng453.game.constants.Constants;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * This class contains service methods for game session operations like adding new session and updating score
 */
@Service
public class GameService {

    /**
     * This method sends post request to API.
     * If operation is successful, it  adds a new session record to the database and return sessionId.
     * SessionId is stored as cookie.
     * If operation fails, it returns error message.
     * @return success or fail message.
     */
    public static String startGame(){
        Long id = Constants.player.getId();
        try{
            HttpResponse<String> res = Unirest.post(Constants.API+"sessionRecord/addSession")
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .header("Authorization", "Bearer "+Constants.jwtToken)
                    .queryString("id",id).asString();
            if (res.getStatus()== HttpStatus.SC_OK){
                JSONObject json = new JSONObject(res.getBody());
                Constants.sessionId = json.getLong("id");
                return "Game is started.";
            }
            else {
                return res.getBody() ;
            }
        }
        catch (Exception e){
            return Constants.CON_ERROR;
        }
    }

    /**
     * This method sends put request to API.
     * If operation is successful, it updates the score of current session.
     * If operation fails, it returns error message.
     * @return success or fail message.
     */
    public static String updateScore(Long score){
        try{
            HttpResponse<String> res = Unirest.put(Constants.API+"sessionRecord/updateSession")
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .header("Authorization", "Bearer "+Constants.jwtToken)
                    .queryString("id",Constants.sessionId)
                    .queryString("score",score).asString();
            if(res.getStatus()==HttpStatus.SC_OK){
                return "Score is updated.";
            }
            else return res.getBody();
        }
        catch (Exception e){
            return Constants.CON_ERROR;
        }
    }

    public static String matchMake() {
        try{
            HttpResponse<String> res = Unirest.get(Constants.API + "sessionRecord/matchMake")
                    .header("Content-Type", "application/json")
                    .header("accept", "application/json")
                    .header("Authorization", "Bearer " + Constants.jwtToken)
                    .queryString("playerId", Constants.player.getId())
                    .asString();
            if (res.getStatus() == HttpStatus.SC_OK) {
                return res.getBody();
            }
            return "";
        }
        catch (Exception e){
            return Constants.CON_ERROR;
        }

    }
}
