package com.ceng453.game.service;

import com.ceng453.game.constants.Constants;
import com.ceng453.game.gameObjects.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;

/**
 * This class contains service methods for authentication operations login,register,forgot password
 */
@Service
public class AuthenticationService {

    /**
     * Object mapper to map JSON to a game object
     */
    static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * This method sends HTTP post request to API.
     * If result's status is OK, it initializes global jwtToken and player value with user's token and player info. Returns empty string.
     * Otherwise, it returns error message
     * @param username username of the player
     * @param password password of the player
     * @return empty or error message
     */
    public static String login(String username, String password) {
        try{
            HttpResponse<String> res = Unirest.post(Constants.API+"player/login")
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .body("{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}").asString();
            if (res.getStatus()== HttpStatus.SC_OK){
                Constants.jwtToken = res.getBody();
                Constants.player = objectMapper.readValue(getPlayerInfo(username), Player.class);
                return "";
            }
            else if(res.getStatus()==HttpStatus.SC_FORBIDDEN){
                return "Username or password is incorrect.";
            }
            else if(res.getStatus()==HttpStatus.SC_BAD_REQUEST){
                return res.getBody();
            }
            else return res.getBody();
        }
        catch (Exception e){
            return Constants.CON_ERROR;
        }

    }
    /**
     * This method sends HTTP post request to API.
     * If result's status is OK, it returns empty string.
     * Otherwise, it returns error message
     * @param username username of the player
     * @param email e-mail of the player
     * @param password password of the player
     * @return empty or error message
     */
    public static String register(String username, String email, String password) {
        try{
            HttpResponse<String> res = Unirest.post(Constants.API+"player/register")
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .body("{\"username\":\"" + username + "\", \"password\":\"" + password + "\", \"email\":\""+email+"\"}").asString();
            if(res.getStatus()==HttpStatus.SC_OK){
                return "";
            }
            else return res.getBody();
        }
        catch (Exception e){
            return Constants.CON_ERROR;
        }
    }
    /**
     * This method sends HTTP post request to API.
     * If result's status is OK, it returns empty string.
     * Otherwise, it returns error message
     * @param email email of the player
     * @return empty or error message
     */
    public static String forgetPasswordSendCode(String email) {
        try{
            HttpResponse<String> res = Unirest.post(Constants.API+"player/forgetPassword")
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .body("{\"email\":\"" + email +"\"}").asString();
            if(res.getStatus()==HttpStatus.SC_OK){
                return "";
            }
            else return res.getBody();
        }
        catch (Exception e){
            return Constants.CON_ERROR;
        }
    }

    /**
     * This method sends HTTP post request to API.
     * If result's status is OK, it returns empty string.
     * Otherwise, it returns error message
     * @param username username of the player
     * @param code that is sent via email to the player
     * @param newPassword new password of the player
     * @return empty or error message
     */
    public static String resetPassword(String username, String code, String newPassword){
        try{
            HttpResponse<String> res = Unirest.post(Constants.API+"resetPassword/changePassword")
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .queryString("username",username)
                    .queryString("code",code)
                    .queryString("newPassword",newPassword)
                    .asString();
            if(res.getStatus()==HttpStatus.SC_OK){
                return "";
            }
            else return res.getBody();
        }
        catch (Exception e){
            return Constants.CON_ERROR;
        }
    }
    /**
     * This method sends HTTP get request to API.
     * If result's status is OK, it returns player information.
     * Otherwise, it returns error message
     * @param username username of the player
     * @return empty or error message
     */
    public static String getPlayerInfo(String username){
        try{
            HttpResponse<String> res = Unirest.get(Constants.API+"player/getPlayer")
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .header("Authorization","Bearer "+Constants.jwtToken)
                    .queryString("username",username).asString();
            return res.getBody();
        }
        catch (Exception e){
            return Constants.CON_ERROR;
        }
    }

    /**
     * This method sends HTTP delete request to API.
     * If result's status is OK, it returns success message.
     * Otherwise, it returns error message
     * @param id id of the player
     * @return success or error message
     */
    public static String deletePlayer(Long id){
        try{
            HttpResponse<String> res = Unirest.delete(Constants.API+"player/delete")
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .header("Authorization","Bearer "+Constants.jwtToken)
                    .queryString("id",id).asString();
            return res.getBody();
        }
        catch (Exception e){
            return Constants.CON_ERROR;
        }
    }
}
