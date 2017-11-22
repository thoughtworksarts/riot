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

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class MainScreen extends Application {
    
	public static String SCREEN_0 = "0.fxml";
	public static String screen0ID = "0";
	public static String SCREEN_1 = "1.fxml";
	public static String screen1ID = "1";
	public static String SCREEN_2 = "2.fxml";
	public static String screen2ID = "2";
	public static String SCREEN_3 = "3.fxml";
	public static String screen3ID = "3";
	public static String FILM_SCREEN = "film.fxml";
	public static String film_screen_ID = "4";
	public static String SCREEN_END_ANGER = "anger-end.fxml";
	public static String screenEndAngerID = "5a";
	public static String SCREEN_END_FEAR = "fear-end.fxml";
	public static String screenEndFearID = "5b";
	public static String SCREEN_END_CALM = "calm-end.fxml";
	public static String screenEndCalmID = "5c";
	public static String SCREEN_CREDITS_1 = "credits-1.fxml";
	public static String screenCredits1ID = "6";
	public static String SCREEN_CREDITS_2 = "credits-2.fxml";
	public static String screenCredits2ID = "7";

        
    @Override
    public void start(Stage primaryStage) {
    	
        //Initializes the singleton
        ApplicationData.getSingleton(); 
    	        
        MainContainer mainContainer = new MainContainer();
        mainContainer.loadScreen(MainScreen.SCREEN_0);        
                
        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root);
        
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER) {
					MainContainer.mCurrentScreen.onRemoteMessage(ClientEndPoint.TRIANGLE_MESSAGE);
				} else if (event.getCode() == KeyCode.BACK_SPACE) {
					MainContainer.mCurrentScreen.onRemoteMessage(ClientEndPoint.SQUARE_MESSAGE);
				} else if (event.getCode() == KeyCode.F) {
					MainContainer.mCurrentScreen.onRemoteMessage("fear");
				} else if (event.getCode() == KeyCode.A) {
					MainContainer.mCurrentScreen.onRemoteMessage("anger");
				} else if (event.getCode() == KeyCode.C) {
					MainContainer.mCurrentScreen.onRemoteMessage("calm");
				} else if (event.getCode() == KeyCode.RIGHT) {
					MainContainer.mCurrentScreen.onRemoteMessage(">");
				} else if (event.getCode() == KeyCode.LEFT) {
					MainContainer.mCurrentScreen.onRemoteMessage("<");
				}
				
			}
		});
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("RIOT");
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void stop() throws Exception {
    	super.stop();
    	if(ApplicationData.getSingleton().getAudioPlayer() != null) {
    		ApplicationData.getSingleton().getAudioPlayer().Shutdown();
    	}
    	if(ApplicationData.getSingleton().getVideoPlayer() != null) {
    		ApplicationData.getSingleton().getVideoPlayer().dispose();
    	}
    	ApplicationData.clearSingleton();
    }
}
