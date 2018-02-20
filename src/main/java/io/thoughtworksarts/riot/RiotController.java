package io.thoughtworksarts.riot;

import io.thoughtworksarts.riot.audio.AudioPlayer;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import io.thoughtworksarts.riot.branching.ConfigRoot;
import io.thoughtworksarts.riot.facialrecognition.FacialRecognitionAPI;
import io.thoughtworksarts.riot.video.MediaControl;
import io.thoughtworksarts.riot.video.MoviePlayer;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RiotController {

    public static final String PATH_TO_CONFIG = "src/main/resources/config.json";
    public static final String DRIVER_NAME = "ASIO4ALL v2";

    private AudioPlayer audioPlayer;
    private MoviePlayer moviePlayer;
    private BranchingLogic branchingLogic;
    private ConfigRoot root;
    private FacialRecognitionAPI facialRecognition;
    private MediaControl mediaControl;


    public RiotController(AudioPlayer audioPlayer,
                          MoviePlayer moviePlayer,
                          BranchingLogic branchingLogic,
                          FacialRecognitionAPI facialRecognition) {
        this.audioPlayer = audioPlayer;
        this.moviePlayer = moviePlayer;
        this.branchingLogic = branchingLogic;
        this.facialRecognition = facialRecognition;
        this.mediaControl = moviePlayer.getMediaControl();
    }

    public void initRiot() throws Exception {
        root = branchingLogic.createLogicTree(PATH_TO_CONFIG);
        branchingLogic.recordMarkers(moviePlayer.getMarkers());
        audioPlayer.initialise(DRIVER_NAME, root.getAudio());
        facialRecognition.initialise();
    }

    public void runRiot(){
        audioPlayer.resume();
        mediaControl.play();
        mediaControl.seek(new Duration(120000));
    }


    public void pauseRiot(){
        audioPlayer.pause();
        mediaControl.pause();
    }

    public void resumeRiot(){
        audioPlayer.resume();
        mediaControl.play();
    }

    public void repeatRiot(){
        audioPlayer.seek(0.0);
        mediaControl.seek(new Duration(0.0));
    }

    public void endRiot(){
        audioPlayer.shutdown();
        mediaControl.shutdown();
    }

}
