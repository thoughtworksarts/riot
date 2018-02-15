package io.thoughtworksarts.riot.video;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;

public class EmbeddedMediaPlayer extends Application {

    @Override
    public void start(Stage primaryStage) throws MalformedURLException {

        primaryStage.setTitle("RIOT");
        Group root = new Group();
        Scene scene = new Scene(root, 1200, 800);

        // create media player
        String pathToFilm = new File("src/main/resources/video/film.m4v").toURI().toURL().toString();

        Media media = new Media (pathToFilm);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);

        MediaControl mediaControl = new MediaControl(mediaPlayer);
        scene.setRoot(mediaControl);

        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
}
