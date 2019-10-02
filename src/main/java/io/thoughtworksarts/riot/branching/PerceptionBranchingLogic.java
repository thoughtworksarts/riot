package io.thoughtworksarts.riot.branching;

import io.thoughtworks.riot.featuretoggle.FeatureToggle;
import io.thoughtworksarts.riot.branching.model.*;
import io.thoughtworksarts.riot.eyetracking.EyeTrackingClient;
import io.thoughtworksarts.riot.facialrecognition.FacialEmotionRecognitionAPI;
import io.thoughtworksarts.riot.logger.PerceptionLogger;
import io.thoughtworksarts.riot.visualization.VisualizationClient;
import javafx.scene.media.MediaMarkerEvent;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
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
    private Map<String, Map<String, ArrayList>> emotionsByActorId;
    private Credits[] credits;
    private String[] actors;
    private int actorIndex;
    private EyeTrackingClient eyeTrackingClient;
    private VisualizationClient visualizationClient;
    private static final String SCENES_PLAYED_KEY = "scenesPlayed";
    private static final String DOMINANT_EMOTIONS_KEY = "dominantEmotions";
    final static private String PLAYBACK_BASE_PATH = "/Users/Kiosk/riot/";
    private FeatureToggle featureToggle;
    private PerceptionLogger logger;

    public PerceptionBranchingLogic(FacialEmotionRecognitionAPI facialRecognition, JsonTranslator translator, ConfigRoot configRoot, EyeTrackingClient eyeTrackingClient) {
        this.facialRecognition = facialRecognition;
        this.translator = translator;
        this.eyeTrackingClient = eyeTrackingClient;
        this.visualizationClient = new VisualizationClient();
        this.actorIndex = 0;
        this.featureToggle = new FeatureToggle();

        loadConfiguration(configRoot);
        initalizeEmotionsByActorIdMap();
        deletePlaybackFiles();
        this.logger = new PerceptionLogger("PerceptionBranchingLogic");

    }

    private void loadConfiguration(ConfigRoot configRoot) {
        this.levels = configRoot.getLevels();
        this.intros = configRoot.getIntros();
        this.credits = configRoot.getCredits();
        this.actors = configRoot.getActors();
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
        logger.log(java.util.logging.Level.INFO, "branchOnMediaEvent",
                    "Branching event occurred",
                    new String[]{"Loop Marker: " + arg.getMarker(),
                            "Current Event Category: " + arg.getMarker().getKey(),
                            "Current Timestamp: " + arg.getMarker().getValue().toMinutes(),
                            "ActorId: " + actors[actorIndex],
                            "EmotionsByActorId: " + this.emotionsByActorId.get(actors[actorIndex])});

        String category = arg.getMarker().getKey().split(":")[0];
        switch (category) {
            case "loop": {
                return getLoop();
            }
            case "welcome": {
                eyeTrackingClient.calibrate();
                break;
            }
            case "countdown": {
                return translator.convertToDuration(intros[3].getStart());
            }
            case "calibrating": {
                return translator.convertToDuration(intros[4].getStart());
            }
            case "story-one": {
                return getStoryOne();
            }
            case "story-two": {
                return getStoryTwo();
            }
            case "level": {
                if (isIntro(getCurrentLevel(arg))) return getInteractiveMode();
                else if (isFirstLevel(getCurrentLevel(arg))) addScenePlayed(arg);

                if (isEndOfStoryOne(getCurrentLevel(arg))) {
                    getDominantEmotion();

                    if (featureToggle.eyeTrackingOn()) {
                        eyeTrackingClient.stopEyeTracking();
                        visualizationClient.createVisualization(actors[actorIndex], emotionsByActorId.get(actors[actorIndex]).get(DOMINANT_EMOTIONS_KEY), emotionsByActorId.get(actors[actorIndex]).get(SCENES_PLAYED_KEY));
                    }

                    return getSecondStoryIntro();
                }

                if (isEndOfStoryTwo(getCurrentLevel(arg))) {
                    getDominantEmotion();

                    if (featureToggle.eyeTrackingOn()) {
                        eyeTrackingClient.stopEyeTracking();
                        visualizationClient.createVisualization(actors[actorIndex], emotionsByActorId.get(actors[actorIndex]).get(DOMINANT_EMOTIONS_KEY), emotionsByActorId.get(actors[actorIndex]).get(SCENES_PLAYED_KEY));
                    }


                    if (featureToggle.eyeTrackingOn()) {
                        return getVisualizationProcessing();
                    } else {
                        return translator.convertToDuration(credits[2].getStart());
                    }

                }

                return getNextEmotionBranch(getNextLevel(arg));
            }
            case "interactive": {
                restartFacialRecognition();

                if (featureToggle.eyeTrackingOn()) {
                    eyeTrackingClient.startEyeTracking();
                }

                if (actorIndex == 0) return translator.convertToDuration(levels[1].getStart());
                else return translator.convertToDuration(levels[7].getStart());
            }
            case "visualization-processing": {
                return getVisualizationPlayback();
            }
            case "delete-playback": {
                deletePlaybackFiles();
            }
        }
        return new Duration(arg.getMarker().getValue().toMillis() + 1);
    }

    private boolean isFirstLevel(int level) {
        return !(level == 1 || level == 7);
    }

    private void deletePlaybackFiles() {
        if (getFirstPlaybackFile().exists() && getFirstPlaybackFile().delete()) {
            log.info("Successfully deleted playback one.");
        }
        if (getSecondPlaybackFile().exists() && getSecondPlaybackFile().delete()) {
            log.info("Successfully deleted playback two.");
        }
    }

    private File getSecondPlaybackFile() {
        return new File(PLAYBACK_BASE_PATH + actors[1] + "-playback.mp4");
    }

    private void restartFacialRecognition() {
        facialRecognition.getDominantEmotion();
    }

    public Duration getLoop() {
        return translator.convertToDuration(intros[0].getStart());
    }

    private Duration getSecondStoryIntro() {
        return translator.convertToDuration(intros[4].getStart());
    }

    private Duration getVisualizationProcessing() {
        return translator.convertToDuration(credits[1].getStart());
    }

    private Level getNextLevel(MediaMarkerEvent arg) {
        return levels[Integer.parseInt(arg.getMarker().getKey().split(":")[1]) + 1];
    }

    private void addScenePlayed(MediaMarkerEvent arg) {
        emotionsByActorId.get(actors[actorIndex]).get(SCENES_PLAYED_KEY).add(getSceneName(arg));
    }

    private String getSceneName(MediaMarkerEvent arg) {
        String[] keys = arg.getMarker().getKey().split(":");
        return keys[0] + ":" + String.valueOf(Integer.parseInt(keys[1]) % 6) + ":" + keys[2];
    }

    @Override
    public Duration getCreditDuration() {

        return translator.convertToDuration(credits[2].getStart());
    }

    private Duration getVisualizationPlayback() {
        File f = getFirstPlaybackFile();
        File g = getSecondPlaybackFile();
        if (f.exists() && !f.isDirectory() && g.exists() && !g.isDirectory()) {
            return null;
        }
        return translator.convertToDuration(credits[1].getStart());
    }

    private File getFirstPlaybackFile() {
        return new File(PLAYBACK_BASE_PATH + actors[0] + "-playback.mp4");
    }

    private int getCurrentLevel(MediaMarkerEvent arg) {
        return Integer.parseInt(arg.getMarker().getKey().split(":")[1]);
    }

    public Duration getIntro() {
        return translator.convertToDuration(intros[1].getStart());
    }

    public Duration getStoryOne() {
        return translator.convertToDuration(levels[0].getStart());
    }

    private Duration getNextEmotionBranch(Level level) {
        return translator.convertToDuration(getEmotionBranch(level));
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

    private Duration getStoryTwo() {
        getDominantEmotion();
        actorIndex++;
        return translator.convertToDuration(levels[6].getStart());
    }

    private Duration getInteractiveMode() {
        return translator.convertToDuration(intros[5].getStart());
    }

    @Override
    public void recordMarkers(Map<String, Duration> markers) {
        addMarker(markers, "loop", String.valueOf(intros.length),
                intros[0].getEnd());

        if (featureToggle.eyeTrackingOn()) {
            addMarker(markers, "welcome", String.valueOf(intros.length), intros[1].getEnd());
        }

        addMarker(markers, "countdown", String.valueOf(intros.length),
                intros[2].getEnd());
        addMarker(markers, "story-one", String.valueOf(intros.length),
                intros[3].getEnd());
        addMarker(markers, "story-two", String.valueOf(intros.length),
                intros[4].getEnd());
        addMarker(markers, "interactive", String.valueOf(intros.length),
                intros[5].getEnd());

        if (featureToggle.eyeTrackingOn()) {
            addMarker(markers, "visualization-processing", String.valueOf(credits.length),
                    credits[1].getEnd());


            addMarker(markers, "delete-playback", String.valueOf(3), credits[2].getStart());
        }

        addMarker(markers, "calibrating", String.valueOf(1), credits[0].getEnd());

        addMarker(markers, "level", String.valueOf(levels[0].getLevel()), levels[0].getEnd());
        addMarker(markers, "level", String.valueOf(levels[1].getLevel()), levels[1].getEnd());

        for (Level level : Arrays.copyOfRange(levels, 1, 6)) {
            level.getBranch().forEach((s, emotionBranch) ->
                    addMarker(markers, "level:" + String.valueOf(level.getLevel()), s, emotionBranch.getEnd()));
        }

        addMarker(markers, "level", String.valueOf(levels[6].getLevel()), levels[6].getEnd());
        addMarker(markers, "level", String.valueOf(levels[7].getLevel()), levels[7].getEnd());

        for (Level level : Arrays.copyOfRange(levels, 6, 12)) {
            level.getBranch().forEach((s, emotionBranch) ->
                    addMarker(markers, "level:" + String.valueOf(level.getLevel()), s, emotionBranch.getEnd()));
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
