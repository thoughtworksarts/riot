package io.thoughtworksarts.riot.video;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class MediaControl extends BorderPane {

    private MediaView mediaView;
    private MediaPlayer player;
    private Pane pane;
    private final boolean repeat = false;

    public MediaControl(MediaPlayer player) {
        this.player = player;
        this.mediaView = new MediaView(player);
        this.pane = new Pane();
        setUpPane();
    }

    private void setUpPane() {
        pane.getChildren().add(mediaView);
        pane.setStyle("-fx-background-color: black;");
        setCenter(pane);

        player.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
    }
}