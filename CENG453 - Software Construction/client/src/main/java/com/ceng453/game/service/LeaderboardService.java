package com.ceng453.game.service;

import com.ceng453.game.constants.Constants;
import com.ceng453.game.gameObjects.RecordDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * This class contains service methods for leaderboard operations for weekly, monthly and all records
 */
@Service
public class LeaderboardService {
    static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * This method sends get request to API.
     * If operation is successful, it returns record lists for last week
     * Otherwise, it returns null
     * @return record list
     */
    public static List<RecordDTO> listLeaderboardWeekly(){
        try{
            HttpResponse<String> res = Unirest.get(Constants.API+"sessionRecord/listLeaderboardWeekly")
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .header("Authorization", "Bearer "+Constants.jwtToken).asString();
            List<RecordDTO> recordDTOList = Arrays.asList(objectMapper.readValue(res.getBody(),RecordDTO[].class));
            if (res.getStatus()== HttpStatus.SC_OK){
                return recordDTOList;
            }
            else return null;
        }
        catch (Exception e){
            return null;
        }
    }

    /**
     * This method sends get request to API.
     * If operation is successful, it returns record lists for last month
     * Otherwise, it returns null
     * @return record list
     */
    public static List<RecordDTO> listLeaderboardMonthly(){
        try{
            HttpResponse<String> res = Unirest.get(Constants.API+"sessionRecord/listLeaderboardMonthly")
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .header("Authorization", "Bearer "+Constants.jwtToken).asString();
            List<RecordDTO> recordDTOList = Arrays.asList(objectMapper.readValue(res.getBody(),RecordDTO[].class));
            if (res.getStatus()== HttpStatus.SC_OK){
                return recordDTOList;
            }
            else return null;
        }
        catch (Exception e){
            return null;
        }
    }

    /**
     * This method sends get request to API.
     * If operation is successful, it returns record lists for all times
     * Otherwise, it returns null
     * @return record list
     */
    public static List<RecordDTO> listLeaderboard(){
        try{
            HttpResponse<String> res = Unirest.get(Constants.API+"sessionRecord/listLeaderboard")
                    .header("Content-Type","application/json")
                    .header("accept","application/json")
                    .header("Authorization", "Bearer "+Constants.jwtToken).asString();
            List<RecordDTO> recordDTOList = Arrays.asList(objectMapper.readValue(res.getBody(),RecordDTO[].class));
            if (res.getStatus()== HttpStatus.SC_OK){
                return recordDTOList;
            }
            else return null;
        }
        catch (Exception e){
            return null;
        }
    }
}
