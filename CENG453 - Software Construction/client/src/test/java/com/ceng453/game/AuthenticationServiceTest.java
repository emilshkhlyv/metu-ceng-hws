package com.ceng453.game;

import com.ceng453.game.constants.Constants;
import com.ceng453.game.gameObjects.Player;
import com.ceng453.game.service.AuthenticationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthenticationServiceTest {
    static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void loginSuccessTest(){
        Constants.jwtToken = null;
        Constants.player = null;
        String username = "sedaui";
        String password = "seda";

        String res = AuthenticationService.login(username,password);

        Assertions.assertEquals(res,"");
        Assertions.assertNotNull(Constants.jwtToken);
        Assertions.assertNotNull(Constants.player);
    }

    @Test
    public void loginWrongInfoTest(){
        Constants.jwtToken = null;
        Constants.player = null;
        String username = "sedaui";
        String password = "se";

        String res = AuthenticationService.login(username,password);

        Assertions.assertEquals(res,"Username or password is incorrect.");
        Assertions.assertNull(Constants.jwtToken);
        Assertions.assertNull(Constants.player);
    }

    @Test
    public void loginEmptyPasswordTest(){
        String username = "sedaui";
        String password = "";

        String res = AuthenticationService.login(username,password);
        Assertions.assertEquals(res,"Password can't be empty.");
    }

    @Test
    public void loginEmptyUsernameTest(){
        String username = "";
        String password = "seda";

        String res = AuthenticationService.login(username,password);
        Assertions.assertEquals(res,"Username can't be empty.");
    }

    @Test
    public void loginGetPlayerInfoTest(){
        String username = "sedaui";
        String password = "seda";
        String email = "sedaui@seda.com";
        Long id = 32L;

        AuthenticationService.login(username,password);

        Assertions.assertEquals(Constants.player.getUsername(),username);
        Assertions.assertNotEquals(Constants.player.getPassword(),password);
        Assertions.assertEquals(Constants.player.getEmail(),email);
        Assertions.assertEquals(Constants.player.getId(),id);
    }

    @Test
    public void registerSuccessTest(){
        String username = "uitest";
        String password = "uitest";
        String email = "ui@test.com";

        String res = AuthenticationService.register(username,email,password);
        Assertions.assertEquals(res,"");
        AuthenticationService.login(username,password);
        AuthenticationService.deletePlayer(Constants.player.getId());

    }

    @Test
    public void registerEmptyUsernameTest(){
        String username = "";
        String password = "test";
        String email = "ui@test.com";

        String res = AuthenticationService.register(username,email,password);
        Assertions.assertEquals(res,"Username can't be empty.");
    }

    @Test
    public void registerEmptyPasswordTest(){
        String username = "uitest";
        String password = "";
        String email = "ui@test.com";

        String res = AuthenticationService.register(username,email,password);
        Assertions.assertEquals(res,"Password can't be empty.");
    }

    @Test
    public void registerEmptyEmailTest(){
        String username = "uitest";
        String password = "uitest";
        String email = "";

        String res = AuthenticationService.register(username,email,password);
        Assertions.assertEquals(res,"E-mail can't be empty.");
    }

    @Test
    public void registerAlreadyTakenUsernameTest(){
        String username = "sedaui";
        String password = "seda";
        String email = "sedaui@seda.com";

        String res = AuthenticationService.register(username,email,password);
        Assertions.assertEquals(res,"Try a different username.");
    }

    @Test
    public void getPlayerInfoSuccessTest() {
        String username = "sedaui";
        String email = "sedaui@seda.com";
        String password = "seda";
        AuthenticationService.login(username,password);

        try {
            Constants.player = objectMapper.readValue(AuthenticationService.getPlayerInfo(username), Player.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(Constants.player.getId(),32L);
        Assertions.assertEquals(Constants.player.getUsername(),username);
        Assertions.assertEquals(Constants.player.getEmail(),email);
    }

    @Test
    public void getPlayerInfoFailTest(){
        String username = "notExists";
        AuthenticationService.login("sedaui","seda");

        String res = AuthenticationService.getPlayerInfo(username);
        Assertions.assertEquals(res,"User isn't found");
    }

    @Test
    public void deletePlayerSuccessTest(){
        String username = "delete";
        String password = "delete";
        String email = "delete@test.com";

        AuthenticationService.register(username,email,password);
        AuthenticationService.login(username,password);
        String res = AuthenticationService.deletePlayer(Constants.player.getId());

        Assertions.assertEquals(res,"Player is deleted successfully");
    }

    @Test
    public void deletePlayerFailTest(){
        AuthenticationService.login("sedaui","seda");
        String res =  AuthenticationService.deletePlayer(-1L);
        Assertions.assertEquals(res,"Player isn't found.");
    }
}
