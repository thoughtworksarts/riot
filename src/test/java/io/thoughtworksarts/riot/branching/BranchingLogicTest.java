package io.thoughtworksarts.riot.branching;

import io.thoughtworksarts.riot.branching.model.*;
import io.thoughtworksarts.riot.facialrecognition.FacialEmotionRecognitionAPI;
import javafx.util.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class BranchingLogicTest {

    @Mock private FacialEmotionRecognitionAPI facialRecognition;
    @Mock private JsonTranslator translator;
    @Mock private ConfigRoot root;

    private BranchingLogic branchingLogic;
    private String start = "00:00.000";
    private String end = "01:00.000";
    private Duration endDuration = new Duration(123000);

    @BeforeEach
    void setUp() throws Exception {
        initMocks(this);

        Level[] levels = {createLevel(0), createLevel(1)};
        Intro[] intros = {createIntro(0, start, end), createIntro(1, start, end), createIntro(2, start, end)};
        Credits[] credits = {createCredit(0), createCredit(1)};


        when(root.getLevels()).thenReturn(levels);
        when(root.getIntros()).thenReturn(intros);
        when(root.getCredits()).thenReturn(credits);
        when(translator.convertToDuration(end)).thenReturn(endDuration);
        branchingLogic = new BranchingLogic(facialRecognition, translator,root);
    }

    private Credits createCredit(int index) {
        Credits credit = new Credits();
        credit.setCredit(index);
        credit.setStart(start);
        credit.setEnd(start);

        return credit;
    }

    private Intro createIntro(int index, String start, String end) {
        Intro intro = new Intro();
        intro.setIntro(index);
        intro.setStart(start);
        intro.setEnd(end);

        return intro;

    }

    private Level createLevel(int index) {
        HashMap<String, EmotionBranch> emotionMap = new HashMap<>();
        emotionMap.put("calm", createEmotionBranch());

        Level level = new Level();
        level.setLevel(index);
        level.setStart(start);
        level.setEnd(end);
        level.setBranch(emotionMap);
        return level;
    }

    private EmotionBranch createEmotionBranch() {
        EmotionBranch emotionBranch = new EmotionBranch();
        emotionBranch.setStart(start);
        emotionBranch.setEnd(end);
        emotionBranch.setOutcome(2);
        return emotionBranch;

    }

    @Test
    void recordMarkersShouldAddEndTimesOfLevelsToTheMap() {
        HashMap<String, Duration> markers = new HashMap<>();
        branchingLogic.recordMarkers(markers);

        assertEquals(markers.get("level:1"), endDuration);
    }

    @Test
    void recordMarkersShouldAddEndTimesOfEmotionBranchesToTheMap() {
        HashMap<String, Duration> markers = new HashMap<>();
        branchingLogic.recordMarkers(markers);

        assertEquals(markers.get("emotion:1:calm"), endDuration);

    }
}