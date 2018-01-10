package uk.co.riot;

import java.util.ArrayList;

import com.facialrecognition.IFacialRecognitionAPI;

public class FacialRecognitionInterface implements EmotionDetectorInterface {

	private IFacialRecognitionAPI mFacialRecognitionAPI;
	private ArrayList<EmotionsRecord> mEmotionsRecord;
	private Thread mThread;
	private boolean mMeasuring;

	public FacialRecognitionInterface(IFacialRecognitionAPI api) {
		this.mFacialRecognitionAPI = api;
		mEmotionsRecord = new ArrayList<EmotionsRecord>();
	}

	@Override
	public void startMeasure() {
		mEmotionsRecord.clear();
		mMeasuring = true;
		if (mThread != null && mThread.isAlive())
			mThread.interrupt();
		mThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (mMeasuring) {
					EmotionsRecord record = measure();
					ApplicationData.getSingleton().addEmotionsRecord(record);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		mThread.start();
	}

	public void stopMeasure() {
		mMeasuring = false;
	}

	protected float getTotalAnger() {
		float totalAnger = 0;
		for (EmotionsRecord record : mEmotionsRecord) {
			totalAnger += record.getAnger();
		}
		return totalAnger;
	}

	protected float getTotalFear() {
		float totalFear = 0;
		for (EmotionsRecord record : mEmotionsRecord) {
			totalFear += record.getFear();
		}
		return totalFear;
	}

	protected float getTotalCalm() {
		float totalCalm = 0;
		for (EmotionsRecord record : mEmotionsRecord) {
			totalCalm += record.getCalm();
		}
		return totalCalm;
	}

	@Override
	public String getEmotion() {
		float totalAnger = getTotalAnger();
		float totalFear = getTotalFear();
		float totalCalm = getTotalCalm();
		System.out.println("Determining dominant emotion over last measure.");
		System.out.println("Fear: " + totalFear + "\nAnger: " + totalAnger + "\nCalm: " + totalCalm);

		if (totalAnger > totalFear) {
			if (totalAnger > totalCalm) {
				System.out.println("Dominant emotion was ANGER");
				return "anger";
			}
			System.out.println("Dominant emotion was CALM");
			return "calm";
		}
		if (totalFear > totalCalm) {
			System.out.println("Dominant emotion was FEAR");
			return "fear";
		}
		System.out.println("Dominant emotion was CALM");
		return "calm";
	}

	protected EmotionsRecord measure() {
		mFacialRecognitionAPI.CaptureImage();
		EmotionsRecord record = new EmotionsRecord(mFacialRecognitionAPI.GetAnger(), mFacialRecognitionAPI.GetFear(),
				mFacialRecognitionAPI.GetCalm());
		System.out
				.println("Anger: " + record.getAnger() + "\nFear: " + record.getFear() + "\nCalm: " + record.getCalm());
		mEmotionsRecord.add(record);
		return record;
	}
}
