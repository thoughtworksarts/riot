package io.thoughtworksarts.riot.branching;

import io.thoughtworksarts.riot.branching.model.*;
import io.thoughtworksarts.riot.eyetracking.EyeTrackingClient;
import io.thoughtworksarts.riot.facialrecognition.FacialEmotionRecognitionAPI;
import io.thoughtworksarts.riot.visualization.VisualizationClient;
import javafx.scene.media.MediaMarkerEvent;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PerceptionBranchingLogic implements BranchingLogic {

    private JsonTranslator translator;
    private FacialEmotionRecognitionAPI facialRecognition;
    private Level[] levels;
    private Intro[] intros;
    private Map<String, Map<String, ArrayList<String>>> emotionsByActorId;
    private Credits[] credits;
    private String[] actors;
    private int actorIndex;
    private EyeTrackingClient eyeTrackingClient;
    private VisualizationClient visualizationClient;
    private static final String SCENES_PLAYED_KEY = "scenesPlayed";
    private static final String DOMINANT_EMOTIONS_KEY = "dominantEmotions";;

    public PerceptionBranchingLogic(FacialEmotionRecognitionAPI facialRecognition, JsonTranslator translator, ConfigRoot configRoot) {
        this.facialRecognition = facialRecognition;
        this.translator = translator;
        this.levels = configRoot.getLevels();
        this.intros = configRoot.getIntros();
        this.credits = configRoot.getCredits();
        this.actors = configRoot.getActors();
        this.eyeTrackingClient = new EyeTrackingClient();
        this.visualizationClient = new VisualizationClient();
        this.actorIndex = 0;
        initalizeEmotionsByActorIdMap();

    }

    private void initalizeEmotionsByActorIdMap() {
        this.emotionsByActorId = new HashMap<>();
        this.emotionsByActorId.put(actors[0], new HashMap<>());
        this.emotionsByActorId.get(actors[0]).put(DOMINANT_EMOTIONS_KEY, new ArrayList<>());
        this.emotionsByActorId.get(actors[0]).put(SCENES_PLAYED_KEY, new ArrayList<>());
        this.emotionsByActorId.put(actors[1], new HashMap<>());
        this.emotionsByActorId.get(actors[1]).put(DOMINANT_EMOTIONS_KEY, new ArrayList<>());
        this.emotionsByActorId.get(actors[1]).put(SCENES_PLAYED_KEY, new ArrayList<>());
    }


    @Override
    public Duration branchOnMediaEvent(MediaMarkerEvent arg) {
        log.info(arg.getMarker().getKey());
        String category = arg.getMarker().getKey().split(":")[0];
        switch (category) {
            case "loop": {
                return translator.convertToDuration(intros[0].getStart());
            }
            case "intro": {
                return getFirstStory();
            }
            case "level": {
                addScenePlayed(arg);
                if(isIntro(getCurrentLevel(arg))) return getFirstLevelOfStory(getNextLevel(arg));
                if(isEndOfStoryOne(getCurrentLevel(arg))) return getCalibratingGraphic();
                if(isEndOfStoryTwo(getCurrentLevel(arg))) return getVisualizationProcessing();
                return getNextEmotionBranch(getNextLevel(arg));
            }
            case "calibrating": {
                return getIntroOfStoryTwo();
            }
            case "visualization-processing": {
                return getVisualizationPlayback();
            }
//            case "credit": {
//
//            }
        }
        return new Duration(arg.getMarker().getValue().toMillis() + 1);
    }

    private Duration getCalibratingGraphic() {
        return translator.convertToDuration(credits[0].getStart());
    }

    private Duration getVisualizationProcessing() {
        return translator.convertToDuration(credits[1].getStart());
    }

    private Level getNextLevel(MediaMarkerEvent arg) {
        return levels[Integer.parseInt(arg.getMarker().getKey().split(":")[1])+1];
    }

    private void addScenePlayed(MediaMarkerEvent arg) {
        emotionsByActorId.get(actors[actorIndex]).get(SCENES_PLAYED_KEY).add(arg.getMarker().getKey());
    }

    @Override
    public Duration getCreditDuration() {
        return translator.convertToDuration(credits[2].getStart());
    }

    private Duration getVisualizationPlayback() {
        getDominantEmotion();
//        eyeTrackingClient.stopEyeTracking();
//        visualizationClient.createVisualization(new ArrayList<>(Arrays.asList(actors)), emotionsByActorId);
        return getVisualizationProcessing();
    }

    private int getCurrentLevel(MediaMarkerEvent arg) {
        return Integer.parseInt(arg.getMarker().getKey().split(":")[1]);
    }

    public Duration getIntro() {
        return translator.convertToDuration(intros[1].getStart());
    }

    public Duration getFirstStory() {
//        eyeTrackingClient.startEyeTracking();
        return translator.convertToDuration(levels[0].getStart());
    }

    private Duration getNextEmotionBranch(Level level) {
        return translator.convertToDuration(getEmotionBranch(level));
    }

    private Duration getFirstLevelOfStory(Level level) {
        facialRecognition.getDominantEmotion(); // this restarts the accumulating data for the emotion detection
        return translator.convertToDuration(level.getStart());
    }

    private boolean isIntro(int level) {
        return level == 0 || level == 6;
    }

    private String getEmotionBranch(Level level) {
        return level.getBranch().get(getDominantEmotion()).getStart();
    }

    private String getDominantEmotion() {
        String dominantEmotion = facialRecognition.getDominantEmotion().name().toLowerCase();
        emotionsByActorId.get(actors[actorIndex]).get(DOMINANT_EMOTIONS_KEY).add(dominantEmotion);
        return dominantEmotion;
    }

    private boolean isEndOfStoryTwo(int level) {
        return level == 11;
    }

    private boolean isEndOfStoryOne(int level) {
        return level == 5;
    }

    private Duration getIntroOfStoryTwo() {
        getDominantEmotion();
        actorIndex++;
        return translator.convertToDuration(levels[6].getStart());
    }

    @Override
    public Duration getEndOfIntro() {
        return translator.convertToDuration(intros[0].getEnd());
    }

    @Override
    public void recordMarkers(Map<String, Duration> markers) {
        addMarker(markers, "loop", String.valueOf(intros.length),
                intros[0].getEnd());
        addMarker(markers, "intro", String.valueOf(intros.length),
                intros[intros.length - 1].getEnd());
        addMarker(markers, "visualization-processing", String.valueOf(credits.length),
                credits[1].getEnd());

        addMarker(markers, "level", String.valueOf(levels[0].getLevel()), levels[0].getEnd());
        addMarker(markers, "level", String.valueOf(levels[1].getLevel()), levels[1].getEnd());

        for (Level level : Arrays.copyOfRange(levels, 1, 6)) {
            level.getBranch().forEach((s, emotionBranch) ->
                    addMarker(markers, "level:"+String.valueOf(level.getLevel()), s, emotionBranch.getEnd()));
        }

        addMarker(markers, "level", String.valueOf(levels[6].getLevel()), levels[6].getEnd());
        addMarker(markers, "level", String.valueOf(levels[7].getLevel()), levels[7].getEnd());

        for (Level level : Arrays.copyOfRange(levels, 6, 12)) {
            level.getBranch().forEach((s, emotionBranch) ->
                    addMarker(markers, "level:"+String.valueOf(level.getLevel()), s, emotionBranch.getEnd()));
        }

        addMarker(markers, "calibrating", String.valueOf(1), credits[0].getEnd());

    }

    public void addMarker(Map<String, Duration> markers, String nameForMarker, String index, String time) {
        String markerNameWithColon = nameForMarker + ":" + index;
        markers.put(markerNameWithColon, translator.convertToDuration(time));
    }

    @Override
    public Duration getClickSeekTime(Duration currentTime) {
        return null;
    }
}
