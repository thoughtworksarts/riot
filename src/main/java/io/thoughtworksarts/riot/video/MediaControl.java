package io.thoughtworksarts.riot.video;

import com.google.common.base.Throwables;
import io.thoughtworksarts.riot.audio.RiotAudioPlayer;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import javafx.concurrent.Service;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class MediaControl extends BorderPane {

    public static final String DRIVER_NAME = "ASIO4ALL v2";

    private BranchingLogic branchingLogic;
    private RiotAudioPlayer audioPlayer;
    private MediaPlayer filmPlayer;

    public MediaControl(BranchingLogic branchingLogic, RiotAudioPlayer audioPlayer, Duration startTime) throws Exception {
        this.branchingLogic = branchingLogic;
        //Video relate
        String filmPath = this.branchingLogic.getFilmPath();
        String pathToFilm = new File(String.valueOf(filmPath)).toURI().toURL().toString();
        setUpFilmPlayer(pathToFilm, startTime);
        setUpPane();
        //Audio related
        this.audioPlayer = audioPlayer;
        this.audioPlayer.initialise(DRIVER_NAME,this.branchingLogic.getAudioPath());
    }

    private void setUpPane() {
        MediaView mediaView = new MediaView(filmPlayer);
        Pane pane = new Pane();
        pane.getChildren().add(mediaView);
        pane.setStyle("-fx-background-color: black;");
        setCenter(pane);
    }

    private void setUpFilmPlayer(String pathToFilm,Duration startTime) {
        Media media = new Media(pathToFilm);
        branchingLogic.recordMarkers(media.getMarkers());
        filmPlayer = new MediaPlayer(media);
        filmPlayer.setAutoPlay(false);
        filmPlayer.setOnMarker(arg -> {
            Duration duration = branchingLogic.branchOnMediaEvent(arg);
            if (duration.toMillis() == 0.0) {
                pause();
            } else {
                seek(duration);
            }
        });
        filmPlayer.setOnReady(() -> {
                    filmPlayer.seek(startTime);
                    audioPlayer.seek(startTime.toSeconds()-1.05);
                }
        );
    }

    public void pause() {
        log.info("Pause");
        filmPlayer.pause();
    }

    public void play() {
        log.info("Play");
        filmPlayer.play();
        audioPlayer.resume();
    }

    public void seek(Duration duration) {
        filmPlayer.seek(duration);
        audioPlayer.seek(duration.toSeconds());

    }

    public void shutdown() {
        log.info("Shutting Down");
        filmPlayer.stop();
        audioPlayer.shutdown();
    }
}