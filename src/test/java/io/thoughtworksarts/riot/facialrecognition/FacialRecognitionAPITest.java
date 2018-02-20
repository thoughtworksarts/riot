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
    }

    @Test
    void shouldGetCalmProbabilityValueBetweenZeroAndOne() {
        facialRecognitionAPI.initialise();

        float calmValue = facialRecognitionAPI.getCalm();

        assertTrue(calmValue >= 0 && calmValue <= 1);
    }
}
