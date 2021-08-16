package com.ceng453.game;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * StageInitializer implements ApplicationListener for listening StageReadyEvent.
 * Login page will be rendered first.
 */
@Component
@RequiredArgsConstructor
public class StageInitializer implements ApplicationListener<FxApplication.StageReadyEvent> {
    @Value("classpath:/fxml/login.fxml")
    private Resource loginResource;
    private final ApplicationContext applicationContext;
    public static Stage applicationStage;

    /**
     * Need to implement the method onApplicationEvent.The onApplicationEvent takes a StageReadyEvent.
     * Call getStage on the event and assign the result to a Stage local variable.
     * @param stageReadyEvent stage ready event
     */
    @Override
    public void onApplicationEvent(FxApplication.StageReadyEvent stageReadyEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(loginResource.getURL());
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Stage stage = stageReadyEvent.getStage();
            applicationStage = stage;
            Scene firstScene = new Scene(fxmlLoader.load(),800,600);
            stage.setScene(firstScene);
            stage.setTitle("Play Pişti");
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setMaxWidth(800);
            stage.setMaxHeight(600);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
