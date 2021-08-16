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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller class for Reset Password page, implements Initializable, make it component
 */
public class ResetPasswordController implements Initializable {

    private JFXSnackbar snackbar;

    @FXML
    AnchorPane forgetPasswordPane;

    @FXML
    AnchorPane forgetInnerPane;

    @FXML
    TextField forgetEmailField;

    @FXML
    Button sendCodeButton;

    @FXML
    JFXSpinner forgetLoadingSpinner;

    @FXML
    Text forgetSnackbarText;

    @FXML
    AnchorPane resetPane;

    @FXML
    AnchorPane resetInnerPane;

    @FXML
    TextField resetUsernameField;

    @FXML
    TextField resetCodeField;

    @FXML
    PasswordField resetPasswordField;

    @FXML
    Button resetButton;

    /**
     * Implement initialize method in interface, initialize snackbar
     * @param location url of location
     * @param resources resourceBundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        snackbar = new JFXSnackbar(forgetPasswordPane);
    }

    /**
     * This method is called when send code button is pressed.
     * If email field is empty, it shows an error message in snackbar.
     * Otherwise, it makes a post request to API.
     * If verification mail is successfully sent, it navigated through the reset password page.
     * Otherwise it shows an error message in snackbar.
     * @param actionEvent ActionEvent
     * @throws IOException for FXMLLoader
     */
    @FXML
    public void onClickSendCode(ActionEvent actionEvent) throws IOException {
        forgetInnerPane.setDisable(true);
        forgetLoadingSpinner.setVisible(true);
        if(forgetEmailField.getText().isBlank()){
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent("E-mail can't be empty!"));
        }
        else{
            String res = AuthenticationService.forgetPasswordSendCode(forgetEmailField.getText());
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(res));
            if(res.isEmpty()){
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Parent loginPage = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/resetPassword.fxml")));
                Scene scene = new Scene(loginPage,800,600);
                stage.setScene(scene);
                stage.show();
            }
        }
        forgetInnerPane.setDisable(false);
        forgetLoadingSpinner.setVisible(false);
    }

    /**
     * This method is called when reset password button is pressed.
     * If username, verification code or new password field is empty, it shows an error message in snackbar.
     * Otherwise, it makes a post request to API.
     * If reset password operation is successful, it navigated through the login page.
     * Otherwise it shows an error message in snackbar.
     * @param actionEvent ActionEvent
     * @throws IOException for FXMLLoader
     */
    @FXML
    public void onClickResetPassword(ActionEvent actionEvent) throws IOException {
        snackbar = new JFXSnackbar(resetPane);
        resetInnerPane.setDisable(true);
        forgetLoadingSpinner.setVisible(true);
        if(resetUsernameField.getText().isBlank()){
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent("Username can't be empty!"));
        }
        else if(resetCodeField.getText().isBlank()){
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent("Code can't be empty!"));
        }
        else if(resetPasswordField.getText().isBlank()){
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent("Password can't be empty!"));
        }
        else{
            String res = AuthenticationService.resetPassword(resetUsernameField.getText(),resetCodeField.getText(),resetPasswordField.getText());
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(res));
            if(res.isEmpty()){
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Parent loginPage = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/login.fxml")));
                Scene scene = new Scene(loginPage,800,600);
                stage.setScene(scene);
                stage.show();
            }
        }
        resetInnerPane.setDisable(false);
        forgetLoadingSpinner.setVisible(false);
    }

    /**
     * This method is called when back button on forget password page is clicked.
     * It loads login page.
     * @param mouseEvent mouseEvent
     * @throws IOException IOException
     */
    @FXML
    public void onClickBackButton(MouseEvent mouseEvent) throws IOException {
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        Parent leaderboardPane = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/login.fxml")));
        Scene scene = new Scene(leaderboardPane, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}
