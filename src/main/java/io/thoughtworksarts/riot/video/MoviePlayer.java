package io.thoughtworksarts.riot.video;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class MoviePlayer {

    private final Stage primaryStage;
    public MediaControl mediaControl;
    private boolean isIntroLoop = true;

    public MoviePlayer(Stage primaryStage, MediaControl mediaControl) {
        this.primaryStage = primaryStage;
        this.mediaControl = mediaControl;
    }

    public void initialise() {
        primaryStage.setTitle("Perception");
        Scene scene = new Scene(new Group(), 1200, 800);
        scene.addEventHandler((KeyEvent.KEY_PRESSED), getSpacebarEventHandler(scene));

        scene.setRoot(mediaControl);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
        mediaControl.play();
    }

    private EventHandler<KeyEvent> getSpacebarEventHandler(Scene scene) {
        return (key) -> {
            if(key.getCode()== KeyCode.SPACE && isIntroLoop) {
                isIntroLoop = false;
                mediaControl.startExperience();
            }
        };
    }

    public Stage getStage()
    {
        return this.primaryStage;
    }
}
