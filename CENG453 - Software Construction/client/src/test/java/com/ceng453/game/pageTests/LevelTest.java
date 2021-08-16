package com.ceng453.game.pageTests;

import com.ceng453.game.constants.Constants;
import com.ceng453.game.service.AuthenticationService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.util.Objects;

public class LevelTest extends ApplicationTest {
    @Override
    public void start(Stage stage) throws IOException {
        String username = "sedaui";
        String password = "seda";
        AuthenticationService.login(username,password);
        Parent mainNode = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/level1.fxml")));
        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
    }

    @After
    public void afterTest(){
        Constants.jwtToken = null;
        Constants.player = null;
    }

    @Test
    public void Level1CheatImplementedTest(){
        press(KeyCode.CONTROL);
        press(KeyCode.DIGIT9);
        release(KeyCode.CONTROL);
        release(KeyCode.DIGIT9);
    }
    @Test
    public void Level2CheatImplementedTest(){
        press(KeyCode.CONTROL);
        press(KeyCode.DIGIT9);
        release(KeyCode.CONTROL);
        release(KeyCode.DIGIT9);
    }
    @Test
    public void Level3CheatImplementedTest(){
        press(KeyCode.CONTROL);
        press(KeyCode.DIGIT9);
        release(KeyCode.CONTROL);
        release(KeyCode.DIGIT9);
    }
}
