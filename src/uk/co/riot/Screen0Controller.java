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


import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import com.facialrecognition.FacialRecognitionAPI;
import com.synthbot.jasiohost.AsioDriver;
import com.synthbot.jasiohost.SimpleAudioPlayer;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;

public class Screen0Controller implements Initializable, ControlledScreen {

	MainContainer myController;

	@FXML
	private Button mNextButton;
	
	@FXML
	private Text mInitText;
	
	@FXML
	private ChoiceBox<String> mChoiceBox;
	
	@FXML
	private Label mErrorLabel;
		
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {    	
    	//List<String> driverNameList = AsioDriver.getDriverNames();
		List<String> driverNameList = Arrays.asList("Blah");

		if(driverNameList == null || driverNameList.isEmpty()) {
			mErrorLabel.setVisible(true);
			mNextButton.setDisable(true);
			return;
		}
    	ObservableList<String> observableList = FXCollections.observableArrayList(driverNameList);
    	mChoiceBox.setItems(observableList);
    	mChoiceBox.getSelectionModel().selectFirst();
    	
    	mNextButton.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				goToNextScreen();
			}
		});   	
    }
    
    public void initEverything() {   	
    	Config config = XmlReader.readXmlConfig("config.xml");
    	ApplicationData.getSingleton().setConfig(config);
    	FacialRecognitionAPI frAPI = new FacialRecognitionAPI();
    	frAPI.Initialise();
    	ApplicationData.getSingleton().setFacialRecognitionAPI(frAPI);
    	SimpleAudioPlayer audioPlayer = new SimpleAudioPlayer();
    	try {
    		audioPlayer.Initialise((String) mChoiceBox.getSelectionModel().getSelectedItem(), config.getAudioFilepath());
    		ApplicationData.getSingleton().setAudioPlayer(audioPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Media video = new Media(Utils.UrlifyPath(config.getVideoFilepath()));
			MediaPlayer videoPlayer = new MediaPlayer(video);
			ApplicationData.getSingleton().setVideoPlayer(videoPlayer);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    }
    
    public void setScreenParent(MainContainer screenParent) {
        myController = screenParent;
    }
    
    private void goToNextScreen() {
		initEverything();
    	myController.loadScreen(MainScreen.SCREEN_1);
    }
	
	@Override
	public void onRemoteMessage(String message) {
		if(message.equals(ClientEndPoint.TRIANGLE_MESSAGE)) {
			goToNextScreen();
		}
	}
}
