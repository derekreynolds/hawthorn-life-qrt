package com.hawthornlife.qrt;


import javafx.scene.image.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;



/**
 * Application entry point for Hawthorn Life QRT
 *
 */
@SuppressWarnings("restriction")
public class QrtApplication extends Application {
	
	private static Logger log = LoggerFactory.getLogger(QrtApplication.class);
	
	
    public static void main( String[] args )  {
		
		log.info("Launching application");
		
    	Application.launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
    	
    	Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fx/QrtApplication.fxml"));
    	
    	stage.setTitle("Hawthorn Life QRT");
    	
    	stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/favicon.ico")));
    	stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/favicon-16x16.png")));
    	stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/favicon-32x32.png")));
    	stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/favicon-96x96.png")));
    	stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/hawthorn-life-logo.png")));
    	
    	stage.setMaximized(true);
    	
    	stage.setScene(new Scene(root));
    	
    	stage.show();
    }
    
   
}
