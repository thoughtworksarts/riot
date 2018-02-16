package io.thoughtworksarts.riot.video;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.MalformedURLException;

public class MoviePlayer {

    private MediaPlayer mediaPlayer;
    private String pathToFilm;

    public MoviePlayer(Stage primaryStage) throws MalformedURLException {
        init(primaryStage);
    }

    private void init(Stage primaryStage) throws MalformedURLException {
        pathToFilm = new File("src/main/resources/video/film.m4v").toURI().toURL().toString();

        primaryStage.setTitle("RIOT");
        Group root = new Group();
        Scene scene = new Scene(root, 1200, 800);

        Media media = new Media (pathToFilm);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(false);

        MediaControl mediaControl = new MediaControl(mediaPlayer);
        scene.setRoot(mediaControl);

        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public void play(){
        mediaPlayer.play();
    }

    public void pause(){
        mediaPlayer.pause();
    }

    public void shutdown(){
        mediaPlayer.stop();
    }

    public void seek(double time) {
        mediaPlayer.seek(Duration.millis(time));
    }
}
