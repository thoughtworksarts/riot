package io.thoughtworksarts.riot.video;

import io.thoughtworksarts.riot.audio.AudioPlayer;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class MediaControl extends BorderPane {

    public static final String DRIVER_NAME = "ASIO4ALL v2";

    private BranchingLogic branchingLogic;
    private AudioPlayer audioPlayer;

    private MediaView mediaView;
    private MediaPlayer filmPlayer;
    private Pane pane;
    private Media media;

    public MediaControl(BranchingLogic branchingLogic, AudioPlayer audioPlayer) throws Exception {
        this.branchingLogic = branchingLogic;
        this.audioPlayer = audioPlayer;

        String filmPath = branchingLogic.getFilmPath();
        String audioPath = branchingLogic.getAudioPath();

        audioPlayer.initialise(DRIVER_NAME, audioPath);

        String pathToFilm = new File(String.valueOf(filmPath)).toURI().toURL().toString();
        media = new Media(pathToFilm);
        branchingLogic.recordMarkers(media.getMarkers());
        this.filmPlayer = new MediaPlayer(media);
        this.mediaView = new MediaView(filmPlayer);
        this.pane = new Pane();

    }

    public void initialise() {
        setUpPane();
        setUpFilmPlayer();
    }

    private void setUpPane() {
        pane.getChildren().add(mediaView);
        pane.setStyle("-fx-background-color: black;");
        setCenter(pane);
    }

    private void setUpFilmPlayer() {
        filmPlayer.setAutoPlay(false);
        filmPlayer.setOnMarker(arg -> {
            Duration duration = branchingLogic.branchOnMediaEvent(arg);
            if (duration.toMillis() == 0.0) {
                pause();
            } else {
                seek(duration);
            }
        });
    }

    public void pause() {
        log.info("Pause");
        audioPlayer.pause();
        filmPlayer.pause();
    }

    public void play() {
        log.info("Play");
        audioPlayer.resume();
        filmPlayer.play();
    }

    public void seek(Duration duration) {
        audioPlayer.seek(duration.toSeconds());
        filmPlayer.seek(duration);
    }

    public void shutdown() {
        log.info("Shutting Down");
        audioPlayer.shutdown();
        filmPlayer.stop();
    }
}