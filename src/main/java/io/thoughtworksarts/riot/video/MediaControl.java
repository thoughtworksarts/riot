package io.thoughtworksarts.riot.video;

import io.thoughtworksarts.riot.audio.AudioPlayer;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import io.thoughtworksarts.riot.branching.ConfigRoot;
import io.thoughtworksarts.riot.facialrecognition.DummyFacialRecognitionAPI;
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

    public static final String PATH_TO_CONFIG = "src/main/resources/config.json";
    public static final String DRIVER_NAME = "ASIO4ALL v2";

    private BranchingLogic branchingLogic;
    private AudioPlayer audioPlayer;
    private DummyFacialRecognitionAPI facialRecognition;

    private MediaView mediaView;
    private MediaPlayer filmPlayer;
    private Pane pane;
    private Media media;
    private ConfigRoot root;

    public MediaControl(BranchingLogic branchingLogic, AudioPlayer audioPlayer, DummyFacialRecognitionAPI facialRecognition) throws Exception {
        this.branchingLogic = branchingLogic;
        this.audioPlayer = audioPlayer;
        this.facialRecognition = facialRecognition;

        root = branchingLogic.createLogicTree(PATH_TO_CONFIG);
        String pathToFilm = new File(String.valueOf(root.getVideo())).toURI().toURL().toString();

        media = new Media(pathToFilm);
        this.filmPlayer = new MediaPlayer(media);
        this.mediaView = new MediaView(filmPlayer);
        this.pane = new Pane();

    }

    public void initialise() throws Exception {
        branchingLogic.recordMarkers(media.getMarkers());
        audioPlayer.initialise(DRIVER_NAME, root.getAudio());
        facialRecognition.initialise();

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
            String key = arg.getMarker().getKey();
            if( key.contains("level") ){
                log.info("Level Marker: " + key);
                //get dominate emotion
                //get level object
                //get dominate emotion object
                //seek to the start of that emotion

            } else if (key.contains("emotion")){
                log.info("Emotion Marker: " + key);
                //get level object
                //get dominate emotion object
                //get outcome number
                //get level object of the outcome number
                //get start of outcome level object
                //seek to the start of it

            }
        });
    }

    public void pause(){
        audioPlayer.pause();
        filmPlayer.pause();
        log.info("Paused at: " + filmPlayer.getCurrentTime().toString());
    }

    public void play(){
        audioPlayer.resume();
        filmPlayer.play();
//        seek(new Duration(120000));
        log.info("Play at: " + filmPlayer.getCurrentTime().toString());
    }

    public void seek(Duration duration){
        audioPlayer.seek(duration.toSeconds());
        filmPlayer.seek(duration);
    }

    public void shutdown(){
        audioPlayer.shutdown();
        filmPlayer.stop();
    }
}