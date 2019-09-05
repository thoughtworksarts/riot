package io.thoughtworksarts.riot.branching;

import io.thoughtworksarts.riot.branching.model.*;
import io.thoughtworksarts.riot.facialrecognition.FacialEmotionRecognitionAPI;
import javafx.util.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
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
    private HashMap<String, Duration> markers;


    @BeforeEach
    void setUp() throws Exception {
        initMocks(this);

        Level[] levels = {createLevel(0), createLevel(1)};
        Intro[] intros = {createIntro(0, start, end), createIntro(1, start, end), createIntro(2, start, end)};
        Credits[] credits = {createCredit(0), createCredit(1)};
        markers = new HashMap<>();

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

    // TODO: Change signature to accept an emotion, start, and end so that any level can be generated
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
        branchingLogic.recordMarkers(markers);
        assertEquals(markers.get("level:1"), endDuration);
    }

    @Test
    void recordMarkersShouldAddEndTimesOfEmotionBranchesToTheMap() {
        branchingLogic.recordMarkers(markers);
        assertEquals(markers.get("emotion:1:calm"), endDuration);
    }

    @Test
    void shouldHandleOneIntro() {
        Intro[] intros = {createIntro(0, start, end)};
        when(root.getIntros()).thenReturn(intros);
        assertEquals(1, root.getIntros().length);
        assertEquals(start, root.getIntros()[0].getStart());
        assertEquals(end, root.getIntros()[0].getEnd());
    }

    @Test
    void shouldHandleFiveIntros() {
        String start0 = "00:00.000";
        String end0 = "01:00.000";
        String start1 = "01:01.000";
        String end1 = "02:00.000";
        String start2 = "02:01.000";
        String end2 = "03:00.000";
        String start3 = "03:01.000";
        String end3 = "04:00.000";
        String start4 = "04:01.000";
        String end4 = "05:00.000";

        Intro[] intros = {
                createIntro(0, start0, end0),
                createIntro(1, start1, end1),
                createIntro(2, start2, end2),
                createIntro(3, start3, end3),
                createIntro(4, start4, end4)
        };
        when(root.getIntros()).thenReturn(intros);
        assertEquals(5, root.getIntros().length);
        String firstIntroStart = root.getIntros()[0].getStart();
        assertEquals(start0, firstIntroStart);
        String firstIntroEnd = root.getIntros()[0].getEnd();
        assertEquals(end0, firstIntroEnd);
        String lastIntroStart = root.getIntros()[4].getStart();
        assertEquals(start4, lastIntroStart);
        String lastIntroEnd = root.getIntros()[4].getEnd();
        assertEquals(end4, lastIntroEnd);
    }
}
