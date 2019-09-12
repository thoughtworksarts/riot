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
        this.emotionsByActorId.get(actors[0]).put("dominantEmotions", new ArrayList<>());
        this.emotionsByActorId.get(actors[0]).put("scenesPlayed", new ArrayList<>());
        this.emotionsByActorId.put(actors[1], new HashMap<>());
        this.emotionsByActorId.get(actors[1]).put("dominantEmotions", new ArrayList<>());
        this.emotionsByActorId.get(actors[1]).put("scenesPlayed", new ArrayList<>());
    }


    @Override
    public Duration branchOnMediaEvent(MediaMarkerEvent arg) {
        log.info(arg.getMarker().getKey());
        String category = arg.getMarker().getKey().split(":")[0];
        switch (category) {
            case "intro": {
                return getFirstStory();
            }
            case "level": {
                addScenePlayed(arg);
                if(isIntro(getCurrentLevel(arg))) return getFirstLevelOfStory(getNextLevel(arg));
                if(isEndOfStoryOne(getCurrentLevel(arg))) return getIntroOfStoryTwo();
                if(isEndOfStoryTwo(getCurrentLevel(arg))) return getPlaybackVisualization();
                return getNextEmotionBranch(getNextLevel(arg));
            }
        }
        return new Duration(arg.getMarker().getValue().toMillis() + 1);
    }

    private Level getNextLevel(MediaMarkerEvent arg) {
        return levels[Integer.parseInt(arg.getMarker().getKey().split(":")[1])+1];
    }

    private void addScenePlayed(MediaMarkerEvent arg) {
        emotionsByActorId.get(actors[actorIndex]).get("scenesPlayed").add(arg.getMarker().getKey());
    }

    @Override
    public Duration getCreditDuration() {
        return translator.convertToDuration(credits[0].getStart());
    }

    private Duration getPlaybackVisualization() {
        getDominantEmotion();
        eyeTrackingClient.stopEyeTracking();
        visualizationClient.createVisualization(new ArrayList<>(Arrays.asList(actors)), emotionsByActorId);
        return null;
    }

    private int getCurrentLevel(MediaMarkerEvent arg) {
        return Integer.parseInt(arg.getMarker().getKey().split(":")[1]);
    }

    private Duration getFirstStory() {
        eyeTrackingClient.startEyeTracking();
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
        emotionsByActorId.get(actors[actorIndex]).get("dominantEmotions").add(dominantEmotion);
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
    public void recordMarkers(Map<String, Duration> markers) {
        addMarker(markers, "intro", String.valueOf(intros.length),
                intros[intros.length - 1].getEnd());
        addMarker(markers, "credit", String.valueOf(credits.length),
                credits[credits.length - 1].getEnd());

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
