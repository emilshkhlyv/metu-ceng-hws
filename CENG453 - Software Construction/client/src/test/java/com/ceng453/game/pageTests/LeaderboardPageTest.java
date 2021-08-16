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

public class LeaderboardPageTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws IOException {
        String username = "sedaui";
        String password = "seda";
        AuthenticationService.login(username,password);
        Parent mainNode = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/leaderboard.fxml")));
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
    public void showWeeklyLeaderboardTest(){
        clickOn("#weekly");
        FxAssert.verifyThat("#weeklyPane",Node::isVisible);
    }

    @Test
    public void showMonthlyLeaderboardTest(){
        clickOn("#monthly");
        FxAssert.verifyThat("#monthlyPane",Node::isVisible);
    }

    @Test
    public void showAllRecordsLeaderboardTest(){
        clickOn("#allRecords");
        FxAssert.verifyThat("#allRecordsPane",Node::isVisible);
    }

    @Test
    public void closeLeaderBoardTest(){
        clickOn("#closeLeaderboard");
        FxAssert.verifyThat("#dashboardPane",Node::isVisible);
    }

}
