package com.ceng453.game;

import com.ceng453.game.gameObjects.RecordDTO;
import com.ceng453.game.service.AuthenticationService;
import com.ceng453.game.service.LeaderboardService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class LeaderboardServiceTest {

    @Test
    public void listLeaderboardWeeklyTest(){
        AuthenticationService.login("sedaui","seda");
        List<RecordDTO> weeklyLeaderboard = LeaderboardService.listLeaderboardWeekly();
        Assertions.assertNotNull(weeklyLeaderboard);
    }

    @Test
    public void listLeaderboardMonthlyTest(){
        AuthenticationService.login("sedaui","seda");
        List<RecordDTO> monthlyLeaderboard = LeaderboardService.listLeaderboardMonthly();
        Assertions.assertNotNull(monthlyLeaderboard);
    }

    @Test
    public void listLeaderboardAllTest(){
        AuthenticationService.login("sedaui","seda");
        List<RecordDTO> leaderboard = LeaderboardService.listLeaderboard();
        Assertions.assertNotNull(leaderboard);
    }


}
