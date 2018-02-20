package io.thoughtworksarts.riot.video;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MediaControl extends BorderPane {

    private MediaView mediaView;
    private MediaPlayer player;
    private Pane pane;
    private final boolean repeat = false;
    private Duration duration;

    public MediaControl(MediaPlayer player) {
        this.player = player;
        this.mediaView = new MediaView(player);
        this.pane = new Pane();
        setUpPane();
        setUpPlayer();
    }

    private void setUpPane() {
        pane.getChildren().add(mediaView);
        pane.setStyle("-fx-background-color: black;");
        setCenter(pane);
    }

    private void setUpPlayer() {
        player.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        player.setOnReady(() -> duration = player.getMedia().getDuration());
        player.setOnMarker(arg -> {
            log.info("Marker :" +arg.getMarker().getKey());
            pause();
        });
    }

    public void pause(){
        player.pause();
        log.info("Paused at :" + player.getCurrentTime().toString());
    }

    public void play(){
        player.play();
        log.info("Play at :" + player.getCurrentTime().toString());
    }

    public void seek(Duration duration){
        player.seek(duration);
    }

    public void shutdown(){
        player.stop();
    }
}