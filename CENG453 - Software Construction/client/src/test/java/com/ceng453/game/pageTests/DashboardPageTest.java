package com.ceng453.game.pageTests;

import com.ceng453.game.constants.Constants;
import com.ceng453.game.service.AuthenticationService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.util.Objects;

public class DashboardPageTest extends ApplicationTest {
    @Override
    public void start(Stage stage) throws IOException {
        Parent mainNode = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/dashboard.fxml")));
        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
        String username = "sedaui";
        String password = "seda";
        AuthenticationService.login(username,password);
    }

    @After
    public void afterTest(){
        Constants.jwtToken = null;
        Constants.player = null;
    }

    @Test
    public void clickToProfileTest(){
        clickOn("#profileButton");
        FxAssert.verifyThat("#profilePane", Node::isVisible);
    }
    @Test
    public void clickToLeaderBoardTest(){
        clickOn("#leaderboardButton");
        FxAssert.verifyThat("#leaderboardPane", Node::isVisible);
    }
    @Test
    public void clickToPlayTest(){
        clickOn("#playImage");
        FxAssert.verifyThat("#level1pane", Node::isVisible);
    }

    @Test
    public void clickToLogoutTest(){
        clickOn("#logoutButton");
        FxAssert.verifyThat("#loginPane", Node::isVisible);
    }
}
