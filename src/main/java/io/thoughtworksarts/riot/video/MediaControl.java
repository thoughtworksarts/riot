package io.thoughtworksarts.riot.video;

import io.thoughtworksarts.riot.branching.BranchingConfigurationLoader;
import io.thoughtworksarts.riot.eyetracking.EyeTrackingClient;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import io.thoughtworksarts.riot.branching.JsonTranslator;
import io.thoughtworksarts.riot.branching.PerceptionBranchingLogic;
import io.thoughtworksarts.riot.facialrecognition.FacialEmotionRecognitionAPI;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaMarkerEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class MediaControl extends BorderPane {

    private BranchingLogic branchingLogic;
    private MediaPlayer filmPlayer;
    private MediaPlayer playbackPlayer;
    private String playbackPath;

    private BranchingConfigurationLoader branchingConfigurationLoader;
    private JsonTranslator jsonTranslator;
    private FacialEmotionRecognitionAPI facialRecognition;

    private MediaView mediaView;
    private Pane pane;
    private MoviePlayer moviePlayer;

    private EyeTrackingClient eyeTrackingClient;

    public MediaControl(Duration videoStartTime, String filmPath, String playbackPath, FacialEmotionRecognitionAPI facialRecognition, JsonTranslator jsonTranslator) throws Exception {
        this.facialRecognition = facialRecognition;
        this.jsonTranslator = jsonTranslator;
        this.branchingConfigurationLoader = new BranchingConfigurationLoader(jsonTranslator);
        this.eyeTrackingClient = new EyeTrackingClient(this);
        //Video relate
        String pathToFilm = new File(String.valueOf(filmPath)).toURI().toURL().toString();
        this.playbackPath = new File(String.valueOf(playbackPath)).toURI().toURL().toString();

        setUpFilmPlayer(pathToFilm, videoStartTime);
        setUpPane(filmPlayer);
        filmPlayer.setMute(true);
        loadNextConfiguration();
    }

    private void loadNextConfiguration() {
        branchingLogic = new PerceptionBranchingLogic(facialRecognition, jsonTranslator, branchingConfigurationLoader.getNextConfiguration(), eyeTrackingClient);
        branchingLogic.recordMarkers(filmPlayer.getMedia().getMarkers());
    }

    public void startExperience() {
        filmPlayer.seek(branchingLogic.getIntro());
    }

    private boolean isPaused = false;

    private void setUpPane(MediaPlayer mediaPlayer) {
        mediaView = new MediaView(mediaPlayer);
        mediaView.setOnMouseClicked(event -> {
            if(isPaused) {
                filmPlayer.play();
                isPaused = false;
            }
            else {
                isPaused = true;
                filmPlayer.pause();
            }
        });

        final DoubleProperty width = mediaView.fitWidthProperty();
        final DoubleProperty height = mediaView.fitHeightProperty();
        width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
        mediaView.setPreserveRatio(true);

        pane = new Pane();
        pane.getChildren().add(mediaView);
        pane.setStyle("-fx-background-color: black;");
        setCenter(pane);
    }

    private void setPane() {
        pane.getChildren().clear();
        pane.getChildren().add(mediaView);
        pane.setStyle("-fx-background-color: black;");
        setCenter(pane);
    }

    private void setUpFilmPlayer(String pathToFilm, Duration startTime) {
        Media media = new Media(pathToFilm);
//        branchingLogic.recordMarkers(media.getMarkers());
        filmPlayer = new MediaPlayer(media);

        filmPlayer.setAutoPlay(false);

        filmPlayer.setOnMarker(arg -> {
            Duration duration = branchingLogic.branchOnMediaEvent(arg);

            String category = arg.getMarker().getKey().split(":")[0];

            if(duration == null)
            {
                setUpPlaybackPlayer(this.playbackPath);
                filmPlayer.stop();
                mediaView.setMediaPlayer(playbackPlayer);
                setPane();
                playbackPlayer.play();
            }

            else {
                seek(duration);
            }
        });

        filmPlayer.setOnEndOfMedia(() -> {
            filmPlayer.getMedia().getMarkers().clear();
            loadNextConfiguration();
            seek(branchingLogic.getLoop());
            moviePlayer.activateSpacebarEventHandler();
        });


    }

    private void setUpPlaybackPlayer(String pathToFilm) {

        Media media = new Media(pathToFilm);
        playbackPlayer = new MediaPlayer(media);
        //media
        playbackPlayer.setOnEndOfMedia(() -> {
            playbackPlayer.pause();
            mediaView.setMediaPlayer(filmPlayer);
            setPane();
            seek(this.branchingLogic.getCreditDuration());
            filmPlayer.play();
        });
    }

    public void play() {
        filmPlayer.play();
    }
    public void pause(){
        filmPlayer.pause();
    }

    public void seek(Duration duration) {
        filmPlayer.seek(duration);

    }

    public void setMoviePlayer(MoviePlayer moviePlayer) {
        this.moviePlayer = moviePlayer;
    }
}
