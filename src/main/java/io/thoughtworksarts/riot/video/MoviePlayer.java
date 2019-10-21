package io.thoughtworksarts.riot.video;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class MoviePlayer {

    private final Stage primaryStage;
    private boolean isLooping;
    public MediaControl mediaControl;
    private EventHandler<KeyEvent> spacebarEventHandler;
    private Scene scene;
    private Runnable setEventHandler;

    public MoviePlayer(Stage primaryStage, MediaControl mediaControl, Scene scene) {
        this.primaryStage = primaryStage;
        this.mediaControl = mediaControl;
        isLooping = true;
        mediaControl.setMoviePlayer(this);
        this.scene = scene;
    }

    public void initialise() {
        primaryStage.setTitle("Perception");

        this.scene.setRoot(mediaControl);
        primaryStage.setScene(this.scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
        mediaControl.play();

        this.scene.addEventHandler((KeyEvent.KEY_PRESSED), (key) -> {
            if (key.getCode() == KeyCode.SPACE && isLooping) {
                isLooping = false;
                mediaControl.startExperience();
            }
            else if(key.getCode() == KeyCode.SPACE && !isLooping) {
                mediaControl.startLooping();
                isLooping = true;
            }
        });
    }

    public void activateSpacebarEventHandler() {
        isLooping = true;
    }
}
