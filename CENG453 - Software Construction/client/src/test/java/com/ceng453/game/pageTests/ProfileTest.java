package com.ceng453.game.pageTests;

import com.ceng453.game.constants.Constants;
import com.ceng453.game.service.AuthenticationService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;
import java.util.Objects;

import static org.testfx.matcher.control.TextMatchers.hasText;

public class ProfileTest extends ApplicationTest {
    @Override
    public void start(Stage stage) throws IOException {
        String username = "sedaui";
        String password = "seda";
        AuthenticationService.login(username,password);
        Parent mainNode = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/profile.fxml")));
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
    public void getUsernameTest(){
        FxAssert.verifyThat("#usernameLabel", LabeledMatchers.hasText("Username: sedaui"));
    }

    @Test
    public void getEmailTest(){
        FxAssert.verifyThat("#emailLabel",LabeledMatchers.hasText("E-mail: sedaui@seda.com"));
    }

    @Test
    public void clickCloseProfileTest(){
        clickOn("#onClickCloseProfile");
        FxAssert.verifyThat("#dashboardPane", Node::isVisible);
    }
}
