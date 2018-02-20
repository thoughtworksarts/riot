package io.thoughtworksarts.riot;

import io.thoughtworksarts.riot.audio.AudioPlayer;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import io.thoughtworksarts.riot.branching.ConfigRoot;
import io.thoughtworksarts.riot.facialrecognition.FacialRecognitionAPI;
import io.thoughtworksarts.riot.video.MoviePlayer;

public class RiotController {

    public static final String PATH_TO_CONFIG = "src/main/resources/config.json";
    public static final String DRIVER_NAME = "ASIO4ALL v2";

    private AudioPlayer audioPlayer;
    private MoviePlayer moviePlayer;
    private BranchingLogic branchingLogic;
    private ConfigRoot root;
    private FacialRecognitionAPI facialRecognition;

    public RiotController(AudioPlayer audioPlayer,
                          MoviePlayer moviePlayer,
                          BranchingLogic branchingLogic,
                          FacialRecognitionAPI facialRecognition) {
        this.audioPlayer = audioPlayer;
        this.moviePlayer = moviePlayer;
        this.branchingLogic = branchingLogic;
        this.facialRecognition = facialRecognition;
    }

    public void initRiot() throws Exception {
        root = branchingLogic.createLogicTree(PATH_TO_CONFIG);
        audioPlayer.initialise(DRIVER_NAME, root.getAudio());
        facialRecognition.initialise();
        //init movie
    }

    public void runRiot(){
        audioPlayer.resume();
        moviePlayer.play();
    }


    public void pauseRiot(){
        audioPlayer.pause();
        moviePlayer.pause();
    }

    public void resumeRiot(){
        audioPlayer.resume();
        moviePlayer.play();
    }

    public void repeatRiot(){
        audioPlayer.seek(0.0);
        moviePlayer.seek(0.0);
    }

    public void endRiot(){
        audioPlayer.shutdown();
        moviePlayer.shutdown();
    }

}
