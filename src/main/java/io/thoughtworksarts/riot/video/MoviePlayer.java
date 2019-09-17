package io.thoughtworksarts.riot.video;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class MoviePlayer {

    private final Stage primaryStage;
    private boolean isLooping;
    public MediaControl mediaControl;
    private EventHandler<KeyEvent> spacebarEventHandler;
    private Scene scene;
    private Runnable setEventHandler;

    public MoviePlayer(Stage primaryStage, MediaControl mediaControl) {
        this.primaryStage = primaryStage;
        this.mediaControl = mediaControl;
        isLooping = true;
        mediaControl.setMoviePlayer(this);
    }

    public void initialise() {
        primaryStage.setTitle("Perception");
        scene = new Scene(new Group(), 1200, 800);

        scene.setRoot(mediaControl);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
        mediaControl.play();
        scene.addEventHandler((KeyEvent.KEY_PRESSED), (key) -> {
            if (key.getCode() == KeyCode.SPACE && isLooping) {
                isLooping = false;
                mediaControl.startExperience();
            }
        });
    }

    public void activateSpacebarEventHandler() {
        isLooping = true;
    }
}
