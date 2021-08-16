package com.ceng453.game;

import com.ceng453.game.constants.Constants;
import com.ceng453.game.service.AuthenticationService;
import com.ceng453.game.service.GameService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GameServiceTest {

    @Test
    public void startGameSuccessTest(){
        AuthenticationService.login("sedaui","seda");
        GameService.startGame();

        Assertions.assertEquals(Constants.playerScore,0L);
        Assertions.assertNotNull(Constants.sessionId);
    }

    @Test
    public void startGameFailTest(){
        AuthenticationService.login("sedaui","seda");
        Constants.player.setId(-1L);

        String res = GameService.startGame();

        Assertions.assertEquals(res,"User isn't found");
    }

    @Test
    public void updateScoreTest(){
        AuthenticationService.login("sedaui","seda");
        GameService.startGame();

        String res = GameService.updateScore(151L);

        Assertions.assertEquals(res,"Score is updated.");

    }

    @Test
    public void updateScoreFailTest(){
        AuthenticationService.login("sedaui","seda");
        Constants.sessionId = -1L;

        String res = GameService.updateScore(151L);

        Assertions.assertEquals(res,"Session isn't found.");
    }
}

