package uk.co.riot;
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License"). You
 * may not use this file except in compliance with the License. You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */ 

import java.net.URL;
import java.util.ResourceBundle;
import com.facialrecognition.FacialRecognitionWindowsAPI;
import com.facialrecognition.IFacialRecognitionAPI;
import com.synthbot.jasiohost.SimpleAudioPlayer;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaMarkerEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Angie
 */
public class FilmScreenController implements Initializable, ControlledScreen {    
	
	MainContainer myController;
    @FXML
    private MediaView mMediaView;
    @FXML
    private VBox mPauseMenu;
        
    private EmotionDetectorInterface mEmotionDetectorInterface;
    
    private boolean mPaused = false;
    private boolean mCreditsOn = false;
    
	private MediaPlayer mVideoPlayer;
	private SimpleAudioPlayer mAudioPlayer;
		
	private Config mConfig;
	
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
    	mConfig = ApplicationData.getSingleton().getConfig();
    	mAudioPlayer = ApplicationData.getSingleton().getAudioPlayer();
    	IFacialRecognitionAPI frAPI = ApplicationData.getSingleton().getFacialRecognitionAPI();
    	mEmotionDetectorInterface = new FacialRecognitionInterface(frAPI);
        setUpVideoPlayer();
		mVideoPlayer.play();
		//mAudioPlayer.Resume();
    }
    
    /**
     * Sets up a Media Player for the video file which path is provided.
     * @param path
     */
    public void setUpVideoPlayer() {    	
    	mVideoPlayer = ApplicationData.getSingleton().getVideoPlayer();
    	Media video = mVideoPlayer.getMedia();
    	    	
    	for(Level level : mConfig.getLevels()) {
    		video.getMarkers().put(level.getId() + "::start", new Duration(level.getStart()));
    		video.getMarkers().put(level.getId() + "::end", new Duration(level.getEnd()));
    		for(Branch branch : level.getBranches()) {
    			video.getMarkers().put(level.getId() + ":" + branch.getId() + ":start", new Duration(branch.getStart()));
    			video.getMarkers().put(level.getId() + ":" + branch.getId() + ":end", new Duration(branch.getEnd()));
    		}
    	}
        
    	mVideoPlayer.setMute(true);
        mVideoPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
            	goToEndScreen();
            }
        });
        
    	mVideoPlayer.setOnMarker(new EventHandler<MediaMarkerEvent>() {
			
			@Override
			public void handle(MediaMarkerEvent arg0) {
				
				//Cannot use a switch as keys are dynamic
				String key = arg0.getMarker().getKey();
				String[] splitKey = key.split(":");
				String levelId = splitKey[0];
				String branchId = splitKey[1];
				String timeId = splitKey[2];
				
				for(Level level : mConfig.getLevels()) {
					if(level.getId().equals(levelId)) {
						if(branchId.equals("")) {
							//Start, measure or end of level
							if(timeId.equals("start")) {
								System.out.println("Level " + level.getId() + " start");
								mEmotionDetectorInterface.startMeasure();
							}
							else if(timeId.equals("end")) {
								//Get emotion result
								mEmotionDetectorInterface.stopMeasure();
								String emotion = mEmotionDetectorInterface.getEmotion();
								for(Branch branch : level.getBranches()) {
									if(branch.getEmotion().contains(emotion)) {
										seek(branch.getStart());
										System.out.println("Seeking to branch " + branch.getEmotion());
										break;
									}
								}
							}
						}
						else {
							for(Branch branch : level.getBranches()) {
								if(branch.getId().equals(branchId)) {
									if(timeId.equals("end")) {
										String outcome = branch.getOutcome();
										if(outcome.equals("end")) {
											goToEndScreen();
										}
										else {
											for(Level l : mConfig.getLevels()) {
												if(l.getId().equals(outcome)) {
													System.out.println("Seeking to level " + l.getId());
													seek(l.getStart());
												}
											}
										}
									}
								}
							}
						}
					}
				}						
			}
		});
    	
    	mMediaView.setMediaPlayer(mVideoPlayer);
    }
    
    @Override
    public void setScreenParent(MainContainer screenParent){
        myController = screenParent;
    }
	
	private void pause() {
		mPaused = true;
		mAudioPlayer.Pause();
		mVideoPlayer.pause();
        BoxBlur bb = new BoxBlur();
        bb.setWidth(10);
        bb.setHeight(10);
        bb.setIterations(3);
		mMediaView.setEffect(bb);
		mPauseMenu.setVisible(true);
	}
	
	private void resume() {
		mPaused = false;
        BoxBlur bb = new BoxBlur();
        bb.setWidth(0);
        bb.setHeight(0);
        bb.setIterations(1);
		mMediaView.setEffect(bb);
		mPauseMenu.setVisible(false);
		mVideoPlayer.play();
		mAudioPlayer.Resume();
	}
	
	private void seek(long duration) {
		mVideoPlayer.seek(new Duration(duration));
		double d = ((double) duration)/1000;
		mAudioPlayer.Seek(d);
	}
	
	private void goToHomeScreen() {
    	mVideoPlayer.stop();
    	mAudioPlayer.Pause();
    	mAudioPlayer.Seek(0);
		myController.loadScreen(MainScreen.SCREEN_1);
	}
	
	private void goToEndScreen() {
    	mVideoPlayer.stop();
    	mAudioPlayer.Pause();
    	mAudioPlayer.Seek(0);
    	float totalAnger = 0;
    	float totalFear = 0;
    	float totalCalm = 0;
    	for(EmotionsRecord record : ApplicationData.getSingleton().getEmotionsRecords()) {
    		totalAnger += record.getAnger();
    		totalFear += record.getFear();
    		totalCalm += record.getCalm();
    	}
    	
    	if(totalAnger > totalFear && totalAnger > totalCalm) {
    		myController.loadScreen(MainScreen.SCREEN_END_ANGER);
    	}
    	else if(totalFear > totalAnger && totalFear > totalCalm) {
    		myController.loadScreen(MainScreen.SCREEN_END_FEAR);
    	}
    	else {
    		myController.loadScreen(MainScreen.SCREEN_END_CALM);
    	}
	}
    
	@Override
	public void onRemoteMessage(final String message) {
		Platform.runLater(new Runnable() {
            public void run() {
            	
    			double step = 3000;

        		if(message.equals(ClientEndPoint.SQUARE_MESSAGE)) {
        			if(!mPaused)
        				pause();
        			else if(mPaused) {
        				goToHomeScreen();
        			}
        		} 
        		else if(message.equals(ClientEndPoint.TRIANGLE_MESSAGE)) {
        			if(mPaused)
        				resume();
        			else if(mCreditsOn)
        				goToEndScreen();
        		}
        		//For testing
//        		else if(message.equals("fear") || message.equals("anger") || message.equals("focus") || message.equals("calm")) {
//        			mEmotionDetectorInterface.setEmotion(message);
//        			System.out.println("Emotion set to " + message);
//        		}
        		else if(message.equals(">")) {
        			double currentTime = mVideoPlayer.getCurrentTime().toMillis();
        			if(currentTime + step >= mVideoPlayer.getCycleDuration().toMillis()) {
        				seek((long) mVideoPlayer.getMedia().getDuration().toMillis());
        			}
        			else {
        				seek((long) (currentTime + step));
        			}
        		}
        		else if(message.equals("<")) {
        			double currentTime = mVideoPlayer.getCurrentTime().toMillis();
        			if(currentTime - step <= 0) {
        				seek(0);
        			}
        			else {
        				seek((long) (currentTime - step));
        			}
        		}
        	}
       });
	}
}
