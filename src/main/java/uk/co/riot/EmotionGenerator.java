package uk.co.riot;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class EmotionGenerator implements EmotionDetectorInterface {
	
	private String mEmotion = "calm";

	@Override
	public void startMeasure() {
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true);
			}
		});
		t.start();
		
		new Timer().schedule(new TimerTask() {
	
			@Override
			public void run() {
				t.interrupt();			
			}
			
		}, 3000);
	}

	@Override
	public String getEmotion() {
//		String[] emotions = {"anger", "fear", "calm", "focus"};
//		int min = 0;
//		int max = emotions.length - 1;
//	    int randomIndex = ThreadLocalRandom.current().nextInt(min, max + 1);
//		return emotions[randomIndex];
		return mEmotion;
	}

	@Override
	public void stopMeasure() {
		// TODO Auto-generated method stub
		
	}
}
