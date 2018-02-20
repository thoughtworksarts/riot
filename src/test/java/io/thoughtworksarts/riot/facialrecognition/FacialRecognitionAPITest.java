package io.thoughtworksarts.riot.facialrecognition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class FacialRecognitionAPITest {

    private MockFacialRecognitionAPI facialRecognitionAPI;

    @Mock private DeepLearningProcessor deepLearningProcessor;
    @Mock private ImageProcessor imageProcessor;

    @BeforeEach
    void setUp() {
        initMocks(this);
        facialRecognitionAPI = new MockFacialRecognitionAPI(deepLearningProcessor, imageProcessor);
        facialRecognitionAPI.initialise();
    }

    @Test
    void shouldGetCalmProbabilityValueBetweenZeroAndOne() {
        float calmValue = facialRecognitionAPI.getCalm();
        assertTrue(calmValue >= 0 && calmValue <= 1);
    }

    @Test
    void shouldGetFearProbabilityValueBetweenZeroAndOne() {
        float fearValue = facialRecognitionAPI.getFear();
        assertTrue(fearValue >= 0 && fearValue <= 1);
    }

    @Test
    void shouldGetAngerProbabilityValueBetweenZeroAndOne() {
        float angerValue = facialRecognitionAPI.getAnger();
        assertTrue(angerValue >= 0 && angerValue <= 1);
    }
}
