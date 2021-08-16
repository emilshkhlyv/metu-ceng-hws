package com.ceng453.game.pageTests;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.util.Objects;

public class LoginPageTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws IOException {
        Parent mainNode =FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/login.fxml")));
        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
    }

    @Test
    public void loginSuccessfullTest() {
        clickOn("#usernameField");
        write("sedaui");
        clickOn("#passwordField");
        write("seda");
        clickOn("#loginButton");
        FxAssert.verifyThat("#dashboardPane", Node::isVisible);
    }

    @Test
    public void emptyUsernameLoginTest(){
        clickOn("#usernameField");
        write("");
        clickOn("#passwordField");
        write("seda");
        clickOn("#loginButton");
        FxAssert.verifyThat("#loginPane",Node::isVisible);
    }

    @Test
    public void emptyPasswordLoginTest(){
        clickOn("#usernameField");
        write("sedaui");
        clickOn("#passwordField");
        write("");
        clickOn("#loginButton");
        FxAssert.verifyThat("#loginPane",Node::isVisible);
    }

    @Test
    public void clickRegisterTest(){
        clickOn("#registerLink");
        FxAssert.verifyThat("#registerPane",Node::isVisible);
    }

    @Test
    public void clickForgetPasswordTest(){
        clickOn("#forgetPasswordLink");
        FxAssert.verifyThat("#forgetPasswordPane",Node::isVisible);
    }

    @Test
    public void goBackFromForgetPasswordTest(){
        clickOn("#forgetPasswordLink");
        clickOn("#backButton");
        FxAssert.verifyThat("#loginPane",Node::isVisible);
    }


}
