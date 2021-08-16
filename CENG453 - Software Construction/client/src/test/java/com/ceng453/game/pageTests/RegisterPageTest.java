package com.ceng453.game.pageTests;

import com.ceng453.game.constants.Constants;
import com.ceng453.game.service.AuthenticationService;
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

import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

public class RegisterPageTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws IOException {
        Parent mainNode = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/register.fxml")));
        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
    }

    @Test
    public void registerInputFieldsTest(){
        clickOn("#registerUsernameField").write("seda");
        clickOn("#registerEmailField").write("seda@seda.com");
        clickOn("#registerPasswordField").write("seda");
        FxAssert.verifyThat("#registerUsernameField",hasText("seda"));
        FxAssert.verifyThat("#registerEmailField",hasText("seda@seda.com"));
        FxAssert.verifyThat("#registerPasswordField",hasText("seda"));
    }

    @Test
    public void registerSuccessfullTest(){
        String username = "uitest";
        String password = "uitest";
        String email = "ui@test.com";
        clickOn("#registerUsernameField").write(username);
        clickOn("#registerEmailField").write(email);
        clickOn("#registerPasswordField").write(password);
        clickOn("#registerButton");
        FxAssert.verifyThat("#loginPane", Node::isVisible);
        AuthenticationService.login(username,password);
        AuthenticationService.deletePlayer(Constants.player.getId());
    }

    @Test
    public void emptyUsernameRegisterTest(){
        String username = "";
        String password = "uitest";
        String email = "ui@test.com";
        clickOn("#registerUsernameField").write(username);
        clickOn("#registerEmailField").write(email);
        clickOn("#registerPasswordField").write(password);
        clickOn("#registerButton");
        FxAssert.verifyThat("#registerPane", Node::isVisible);
    }

    @Test
    public void emptyPasswordRegisterTest(){
        String username = "uitest";
        String password = "";
        String email = "ui@test.com";
        clickOn("#registerUsernameField").write(username);
        clickOn("#registerEmailField").write(email);
        clickOn("#registerPasswordField").write(password);
        clickOn("#registerButton");
        FxAssert.verifyThat("#registerPane", Node::isVisible);
    }

    @Test
    public void emptyEmailRegisterTest(){
        String username = "uitest";
        String password = "uitest";
        String email = "";
        clickOn("#registerUsernameField").write(username);
        clickOn("#registerEmailField").write(email);
        clickOn("#registerPasswordField").write(password);
        clickOn("#registerButton");
        FxAssert.verifyThat("#registerPane", Node::isVisible);
    }

    @Test
    public void clickBackToLoginTest(){
        clickOn("#backToLogin");
        FxAssert.verifyThat("#loginPane", Node::isVisible);
    }

}
