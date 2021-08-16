package com.ceng453.game.controller;

import com.ceng453.game.service.AuthenticationService;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller class for Login page, implements Initializable, make it component
 */
@Component
public class LoginController implements Initializable {

    private JFXSnackbar snackbar;
    @FXML
    AnchorPane loginPane;
    @FXML
    AnchorPane innerPane;
    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    Button loginButton;
    @FXML
    Text snackbarText;
    @FXML
    public JFXSpinner loadingSpinner;
    @FXML
    Hyperlink registerLink;
    @FXML
    Hyperlink forgetPasswordLink;

    /**
     * Implement initialize method in interface, initialize snackbar
     * @param location url of location
     * @param resources resourceBundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        snackbar = new JFXSnackbar(loginPane);
        usernameField.setLayoutX(48);
        usernameField.setLayoutY(131);
        passwordField.setLayoutX(48);
        passwordField.setLayoutY(192);
        loginButton.setLayoutX(134);
        loginButton.setLayoutY(264);
        registerLink.setLayoutX(88);
        registerLink.setLayoutY(325);
        forgetPasswordLink.setLayoutX(104);
        forgetPasswordLink.setLayoutY(355);
    }

    /**
     * This method is called when login button is pressed.
     * If username or password field is empty, it shows an error message in snackbar.
     * Otherwise, it makes a post request to API.
     * If credentials are true, it navigated through the dashboard.
     * Otherwise it shows an error message in snackbar.
     * @param event ActionEvent
     * @throws IOException for FXMLLoader
     */
    @FXML
    public void onClickLogin(ActionEvent event) throws IOException {
        innerPane.setDisable(true);
        loadingSpinner.setVisible(true);
        if(usernameField.getText().isBlank()){
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent("Username can't be empty!"));
        }
        else if(passwordField.getText().isBlank()){
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent("Password can't be empty!"));
        }
        else{
            String res = AuthenticationService.login(usernameField.getText(),passwordField.getText());
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(res));
            if(res.isEmpty()){
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Parent dashboardPage = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/dashboard.fxml")));
                Scene scene = new Scene(dashboardPage,800,600);
                stage.setScene(scene);
                stage.show();
            }
        }
        innerPane.setDisable(false);
        loadingSpinner.setVisible(false);
    }

    /**
     * This method is called when register hyperlink on login page is clicked.
     * It loads register page.
     * @param actionEvent actionEvent
     * @throws IOException IOException
     */
    @FXML
    public void onClickLoginToRegister(ActionEvent actionEvent) throws IOException {
        innerPane.setDisable(true);
        loadingSpinner.setVisible(true);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Parent registerPage = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/register.fxml")));
        Scene scene = new Scene(registerPage, 800, 600);
        stage.setScene(scene);
        stage.show();
        innerPane.setDisable(false);
        loadingSpinner.setVisible(false);
    }

    /**
     * This method is called when forget password hyperlink on login page is clicked.
     * It loads forget password page.
     * @param actionEvent actionEvent
     * @throws IOException IOException
     */
    @FXML
    public void onClickForgetPassword(ActionEvent actionEvent) throws IOException {
        innerPane.setDisable(true);
        loadingSpinner.setVisible(true);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Parent registerPage = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/forgetPassword.fxml")));
        Scene scene = new Scene(registerPage, 800, 600);
        stage.setScene(scene);
        stage.show();
        innerPane.setDisable(false);
        loadingSpinner.setVisible(false);
    }
}
