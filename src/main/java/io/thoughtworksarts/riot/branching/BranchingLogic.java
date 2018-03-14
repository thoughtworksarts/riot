package io.thoughtworksarts.riot.branching;

import io.thoughtworksarts.riot.branching.model.*;
import io.thoughtworksarts.riot.facialrecognition.FacialEmotionRecognitionAPI;
import javafx.application.Platform;
import javafx.scene.media.MediaMarkerEvent;
import javafx.util.Duration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class BranchingLogic {

    public static final String PATH_TO_CONFIG = "src/main/resources/config.json";

    private Map<String, List<Duration>> markersWithSeekTimes;
    private JsonTranslator translator;
    private FacialEmotionRecognitionAPI facialRecognition;
    private Level[] levels;
    private Intro[] intros;
    private Credits[] credits;
    @Getter
    private String filmPath;
    @Getter
    private String audioPath;

    public BranchingLogic(FacialEmotionRecognitionAPI facialRecognition, JsonTranslator translator) throws Exception {
        this.facialRecognition = facialRecognition;
        this.translator = translator;
        initialise();
    }

    private void initialise() throws Exception {
        ConfigRoot root = translator.populateModelsFromJson(PATH_TO_CONFIG);
        levels = root.getLevels();
        filmPath = root.getMedia().getVideo();
        audioPath = root.getMedia().getAudio();
        intros = root.getIntros();
        credits = root.getCredits();
        markersWithSeekTimes = new HashMap<String, List<Duration>>();
    }

    public Duration branchOnMediaEvent(MediaMarkerEvent arg) {
        String markerKey = arg.getMarker().getKey();
        String[] split = markerKey.split(":");
        String category = split[0];

        if(markersWithSeekTimes.containsKey(markerKey)){
            //get possible duration list
            List<Duration> markerSeekTimes = markersWithSeekTimes.get(markerKey);
            if(category.equals("level")) {
                log.info("Level Marker: " + markerKey);
                int emotion = facialRecognition.getDominantEmotion().getNumber();
                return markerSeekTimes.get(emotion);
            }
            //handle every marker with multiple seek times in an else if format
            else{
                return markerSeekTimes.get(0);
            }
        }
        //handle every marker without a seek time here
        else{
            if(category.equals("credit end")){
                log.info("Shutting down webcam: ");
                facialRecognition.endImageCapture();
                log.info("Exiting application: ");
                Platform.exit();
            }
        }

        double currentTime = arg.getMarker().getValue().toMillis() + 1;
        return new Duration(currentTime);
    }

    //add a marker with a single time to seek to
    public void addMarkerWithSeekTime(Map<String, Duration> markers, String nameForMarker, String index, String time, Duration seekTime){
        String markerNameWithColon = nameForMarker + ":" + index;
        if(markersWithSeekTimes.containsKey(markerNameWithColon)) {
            return;
        }
        markers.put(markerNameWithColon, translator.convertToDuration(time));
        List<Duration> singleSeekTimeList = new ArrayList<Duration>();
        singleSeekTimeList.add(seekTime);
        markersWithSeekTimes.put(markerNameWithColon, singleSeekTimeList);
    }

    //add marker with multiple times to seek to
    //IMPORTANT NOTE: multiple seek times must be handled in method BranchOnMediaEvent
    private void addMarkerWithMultipleSeekTimes(Map<String, Duration> markers, String nameForMarker, String index, String time, List<Duration> seekTimes) {
        String markerNameWithColon = nameForMarker + ":" + index;
        if(markersWithSeekTimes.containsKey(markerNameWithColon)) {
            log.info("This marker "+ markerNameWithColon + " has not been added because it already seeks to a time.");
            return;
        }
        markers.put(markerNameWithColon, translator.convertToDuration(time));
        markersWithSeekTimes.put(markerNameWithColon, seekTimes);
    }

    //add markers for other reasons which can be added in method BranchOnMediaEvent
    private void addMarker(Map<String, Duration> markers, String nameForMarker, String index, String time){
        String markerNameWithColon = nameForMarker + ":" + index;
        if(markers.containsKey(markerNameWithColon)) {
            log.info("This marker "+ markerNameWithColon + " has not been added because it already seeks to a time.");
            return;
        }
        markers.put(markerNameWithColon, translator.convertToDuration(time));
    }

    //get a particular marker
    public void getMarker(Map<String, Duration[]> markers, String nameForMarker) {
        markers.get(nameForMarker);
    }

    //default markers used for branching in main
    public void recordMarkers(Map<String, Duration> markers) {
        //Add seek time + marker for end of intro
        Duration introend = translator.convertToDuration("00:00.000");
        addMarkerWithSeekTime(markers, "intro", "3", intros[2].getEnd(), introend);

        //Add marker for end of credits
        addMarker(markers, "credit end", "2", credits[1].getEnd());


        //Add seek times + markers related to level and emotion ends
        for (Level level : levels) {
            //get branches for each level
            Map<String, EmotionBranch> branch = level.getBranch();

            //Add seek times + markers for end of each level scene
            List<Duration> levelEnds = new ArrayList<Duration>();
            branch.forEach((branchName, emotionBranch) -> levelEnds.add(translator.convertToDuration(emotionBranch.getStart())));
            //level: x
            addMarkerWithMultipleSeekTimes(markers, "level", String.valueOf(level.getLevel()), level.getEnd(), levelEnds);

            //Add seek times + markers for end of each emotion scene
            for (Map.Entry<String, EmotionBranch> pair : branch.entrySet()){
                Duration emotionEnd;
                int outcome = pair.getValue().getOutcome();
                //if user "loses" level, add beginning of credit
                if(outcome == 0){
                    emotionEnd = translator.convertToDuration(credits[0].getStart());
                    addMarkerWithSeekTime(markers, "emotion:" + level.getLevel(), pair.getKey(), pair.getValue().getEnd(), emotionEnd);
                }
                //if user stays calm, add beginning of next level
                else{
                    Level nextLevel = levels[outcome - 1];
                    emotionEnd = translator.convertToDuration(nextLevel.getStart());
                    addMarkerWithSeekTime(markers, "emotion:" + level.getLevel(), pair.getKey(), pair.getValue().getEnd(), emotionEnd);
                }
            }
        }
    }

    //get the duration of the intro
    public Duration getProperIntroDuration(Duration currentTime) {
        Duration beginningOfIntro = translator.convertToDuration(intros[0].getStart());
        Duration secondIntroStart = translator.convertToDuration(intros[1].getStart());
        Duration thirdIntroStart = translator.convertToDuration(intros[2].getStart());
        Duration endOfIntro = translator.convertToDuration(intros[2].getEnd());

        Duration[] durations = new Duration[]{secondIntroStart, thirdIntroStart, endOfIntro};

        for (Duration duration : durations) {
            if (currentTime.lessThan(duration) && currentTime.greaterThan(beginningOfIntro)) {
                return duration;
            }
        }
        return null;
    }
}
