package io.thoughtworksarts.riot.video;

import javafx.collections.ObservableMap;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.MalformedURLException;

@Slf4j
public class MoviePlayer {

    public MediaControl mediaControl;
    private String pathToFilm;
    private Media media;

    public MoviePlayer(Stage primaryStage) throws MalformedURLException {
        init(primaryStage);
    }

    private void init(Stage primaryStage) throws MalformedURLException {
        pathToFilm = new File("src/main/resources/video/film.m4v").toURI().toURL().toString();

        primaryStage.setTitle("RIOT");
        Group root = new Group();
        Scene scene = new Scene(root, 1200, 800);

        media = new Media(pathToFilm);


        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(false);

        mediaControl = new MediaControl(mediaPlayer);
        scene.setRoot(mediaControl);

        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public ObservableMap<String,Duration> getMarkers() {
        return media.getMarkers();
    }

    public MediaControl getMediaControl() {
        return mediaControl;
    }
}
