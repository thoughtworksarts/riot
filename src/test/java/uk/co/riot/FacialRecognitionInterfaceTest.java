package uk.co.riot;

import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;

import com.facialrecognition.MockFacialRecognitionAPI;

public class FacialRecognitionInterfaceTest {

	@Test
	public void shouldProvideTotalCalmAfterOneMeasurement() {
		FacialRecognitionInterface frInterface = new FacialRecognitionInterface(new MockFacialRecognitionAPI(1,5,3));	
		frInterface.measure();
		assertEquals(1,frInterface.getTotalCalm(),0.001);
	}

	@Test
	public void shouldProvideTotalFearAfterOneMeasurement() {
		FacialRecognitionInterface frInterface = new FacialRecognitionInterface(new MockFacialRecognitionAPI(1,5,3));	
		frInterface.measure();
		assertEquals(5,frInterface.getTotalFear(),0.001);
	}

	@Test
	public void shouldProvideTotalAngerAfterOneMeasurement() {
		FacialRecognitionInterface frInterface = new FacialRecognitionInterface(new MockFacialRecognitionAPI(1,5,3));	
		frInterface.measure();
		assertEquals(3,frInterface.getTotalAnger(),0.001);
	}

}
