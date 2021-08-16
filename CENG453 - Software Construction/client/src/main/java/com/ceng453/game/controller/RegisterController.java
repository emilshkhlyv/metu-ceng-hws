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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller class for Register page, implements Initializable, make it component
 */
@Component
public class RegisterController implements Initializable {

    private JFXSnackbar snackbar;

    @FXML
    AnchorPane registerPane;

    @FXML
    AnchorPane registerInnerPane;

    @FXML
    TextField registerUsernameField;

    @FXML
    TextField registerEmailField;

    @FXML
    PasswordField registerPasswordField;

    @FXML
    Button registerButton;

    @FXML
    JFXSpinner registerLoadingSpinner;

    @FXML
    Text registerSnackbarText;

    /**
     * Implement initialize method in interface, initialize snackbar
     * @param location url of location
     * @param resources resourceBundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        snackbar = new JFXSnackbar(registerPane);
    }

    /**
     * This method is called when sign up button is pressed.
     * If username, email or password field is empty, it shows an error message in snackbar.
     * Otherwise, it makes a post request to API.
     * If user successfully registered, it navigated through the login page.
     * Otherwise it shows an error message in snackbar.
     * @param actionEvent ActionEvent
     * @throws IOException for FXMLLoader
     */
    @FXML
    public void onClickRegister(ActionEvent actionEvent) throws IOException {
        registerInnerPane.setDisable(true);
        registerLoadingSpinner.setVisible(true);
        if(registerUsernameField.getText().isBlank()){
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent("Username can't be empty!"));
        }
        else if(registerEmailField.getText().isBlank()){
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent("E-mail can't be empty!"));
        }
        else if(registerPasswordField.getText().isBlank()){
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent("Password can't be empty!"));
        }
        else{
            String res = AuthenticationService.register(registerUsernameField.getText(),registerEmailField.getText(),registerPasswordField.getText());
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(res));
            if(res.isEmpty()){
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Parent loginPage = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/login.fxml")));
                Scene scene = new Scene(loginPage,800,600);
                stage.setScene(scene);
                stage.show();
            }
        }
        registerInnerPane.setDisable(false);
        registerLoadingSpinner.setVisible(false);

    }

    /**
     * This method is called when login hyperlink on register page is clicked.
     * It loads login page.
     * @param actionEvent actionEvent
     * @throws IOException IOException
     */
    @FXML
    public void onClickRegisterToLogin(ActionEvent actionEvent) throws IOException {
        registerInnerPane.setDisable(true);
        registerLoadingSpinner.setVisible(true);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Parent registerPage = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/login.fxml")));
        Scene scene = new Scene(registerPage, 800, 600);
        stage.setScene(scene);
        stage.show();
        registerInnerPane.setDisable(false);
        registerLoadingSpinner.setVisible(false);
    }
}
