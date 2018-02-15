package io.thoughtworksarts.riot.video;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class MediaControl extends BorderPane {

    private MediaView mediaView;
    private final boolean repeat = false;

    public MediaControl(final MediaPlayer mp) {
        mediaView = new MediaView(mp);
        Pane mvPane = new Pane();
        mvPane.getChildren().add(mediaView);
        mvPane.setStyle("-fx-background-color: black;");
        setCenter(mvPane);

        mp.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
    }
}