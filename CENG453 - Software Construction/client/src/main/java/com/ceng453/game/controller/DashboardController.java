package com.ceng453.game.controller;

import com.ceng453.game.constants.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    ImageView playImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * This method is called when profile button on dashboard page is clicked.
     * It loads profile page.
     * @param actionEvent mouseEvent
     * @throws IOException IOException
     */
    @FXML
    public void onClickProfile(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Parent profilePane = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/profile.fxml")));
        Scene scene = new Scene(profilePane, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method is called when leaderboard button on dashboard page is clicked.
     * It loads leaderboard page.
     * @param actionEvent mouseEvent
     * @throws IOException IOException
     */
    @FXML
    public void onClickLeaderboard(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Parent leaderboardPane = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/leaderboard.fxml")));
        Scene scene = new Scene(leaderboardPane, 800, 600);
        stage.setScene(scene);
        stage.show();

    }
    @FXML
    public void onClickPlay(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent leaderboardPane = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/level1.fxml")));
        Scene scene = new Scene(leaderboardPane, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method is called when logout button on dashboard page is clicked.
     * It clears jwtToken and player cookie. It loads login page.
     * @param mouseEvent mouseEvent
     * @throws IOException IOException
     */
    @FXML
    public void onClickLogout(MouseEvent mouseEvent) throws IOException {
        Constants.jwtToken = "";
        Constants.player = null;
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        Parent leaderboardPane = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/login.fxml")));
        Scene scene = new Scene(leaderboardPane, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}
