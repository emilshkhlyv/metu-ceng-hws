package com.ceng453.game.controller;

import com.ceng453.game.gameObjects.RecordDTO;
import com.ceng453.game.service.LeaderboardService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller class for Leaderboard page, implements Initializable, make it component
 */
public class LeaderboardController implements Initializable {

    @FXML
    TableView<RecordDTO> weeklyTable;

    @FXML
    TableView<RecordDTO> monthlyTable;

    @FXML
    TableView<RecordDTO> allRecordTable;

    /**
     * Implement initialize method in interface, initialize snackbar
     * Sets leaderboard contents for weekly, monthly and all records
     * @param location url of location
     * @param resources resourceBundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<RecordDTO> recordDTOWeekly = FXCollections.observableArrayList(LeaderboardService.listLeaderboardWeekly());
        weeklyTable.setItems(recordDTOWeekly);
        ObservableList<RecordDTO> recordDTOMonthly = FXCollections.observableArrayList(LeaderboardService.listLeaderboardMonthly());
        monthlyTable.setItems(recordDTOMonthly);
        ObservableList<RecordDTO> recordDTOAll = FXCollections.observableArrayList(LeaderboardService.listLeaderboard());
        allRecordTable.setItems(recordDTOAll);
    }

    /**
     * This method is called when close button on leaderboard page is clicked.
     * It loads dashboard page.
     * @param mouseEvent mouseEvent
     * @throws IOException IOException
     */
    @FXML
    public void onClickCloseLeaderboard(MouseEvent mouseEvent) throws IOException {
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        Parent dashboardPage = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/dashboard.fxml")));
        Scene scene = new Scene(dashboardPage, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}
