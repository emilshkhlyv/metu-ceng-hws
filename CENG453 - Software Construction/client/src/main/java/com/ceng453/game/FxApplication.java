package com.ceng453.game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * JavaFX application class
 */
public class FxApplication extends Application {
    private ConfigurableApplicationContext applicationContext;

    /**
     * Initialize the Spring Boot applicationContext using ClientApplication
     */
    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(ClientApplication.class).run();
    }

    /**
     * Called when the stage is ready to be used
     * @param stage stage
     */
    @Override
    public void start(Stage stage) {
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    /**
     * Call to stop application context
     */
    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }

    /**
     * Create StageReadyEvent, call super in constructor
     */
    static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return ((Stage) getSource());
        }
    }
}
