package com.ceng453.game.controller;

import com.ceng453.game.constants.Constants;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller class for Profile page, implements Initializable, make it component
 */
public class ProfileController implements Initializable {

    @FXML
    Label usernameLabel;

    @FXML
    Label emailLabel;

    /**
     * Implement initialize method in interface, initialize snackbar
     * Sets username and password labels with player information
     * @param location url of location
     * @param resources resourceBundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usernameLabel.setText(usernameLabel.getText()+Constants.player.getUsername());
        emailLabel.setText(emailLabel.getText()+Constants.player.getEmail());
    }

    /**
     * This method is called when close button on profile page is clicked.
     * It loads dashboard page.
     * @param mouseEvent mouseEvent
     * @throws IOException IOException
     */
    @FXML
    public void onClickCloseProfile(MouseEvent mouseEvent) throws IOException {
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        Parent dashboardPage = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/dashboard.fxml")));
        Scene scene = new Scene(dashboardPage, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}
