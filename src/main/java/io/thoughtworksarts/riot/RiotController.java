package io.thoughtworksarts.riot;

import io.thoughtworksarts.riot.branching.BranchingLogic;
import io.thoughtworksarts.riot.branching.ConfigRoot;
import io.thoughtworksarts.riot.facialrecognition.FacialRecognitionV2API;
import io.thoughtworksarts.riot.sound.AudioPlayer;

import java.util.Timer;

public class RiotController {

    public static final String PATH_TO_CONFIG = "src/main/resources/config.json";
    public static final String DRIVER_NAME = "ASIO4ALL v2";

    private AudioPlayer audioPlayer;
    private BranchingLogic branchingLogic;
    private ConfigRoot root;
    private FacialRecognitionV2API facialRecognition;
    private Timer timer;


    public RiotController(AudioPlayer audioPlayer,
                          BranchingLogic branchingLogic,
                          FacialRecognitionV2API facialRecognition,
                          Timer timer) {
        this.audioPlayer = audioPlayer;
        this.branchingLogic = branchingLogic;
        this.facialRecognition = facialRecognition;
        this.timer = timer;
    }

    public void initRiot() throws Exception {
        root = branchingLogic.createLogicTree(PATH_TO_CONFIG);
        audioPlayer.initialise(DRIVER_NAME, root.getAudio());
        facialRecognition.Initialise();
        //init movie
    }


    public void runRiot(){
        //play root portion of audio
        audioPlayer.resume();

        //play root portion of film
        //read emotion
        //based on emotion pick the next branch - repeat.
    }


    public void pauseRiot(){
        audioPlayer.pause();

    }

    public void resumeRiot(){
        audioPlayer.resume();

    }

    public void repeatRiot(){
        audioPlayer.seek(0.0);

    }

    public void endRiot(){
        audioPlayer.shutdown();
    }


}
