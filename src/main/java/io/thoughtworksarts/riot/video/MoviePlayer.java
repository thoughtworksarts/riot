package io.thoughtworksarts.riot.video;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MoviePlayer {

    private final Stage primaryStage;
    public MediaControl mediaControl;

    public MoviePlayer(Stage primaryStage, MediaControl mediaControl) {
        this.primaryStage = primaryStage;
        this.mediaControl = mediaControl;
    }

    public void initialise() {
        primaryStage.setTitle("RIOT");
        Scene scene = new Scene(new Group(), 1200, 800);
        scene.setRoot(mediaControl);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public Stage getStage()
    {
        return this.primaryStage;
    }
}
