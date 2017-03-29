package com.hawthornlife.qrt;

import javafx.scene.*;
import javafx.stage.*;
import javafx.fxml.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hawthornlife.qrt.service.FileProcessor;

import javafx.event.*;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;

@SuppressWarnings("restriction")
public class QrtController {

	private static Logger log = LoggerFactory.getLogger(QrtController.class);
	
	@FXML
	private VBox main;
	
	private DirectoryChooser directoryChooser = new DirectoryChooser();
	
	public void onClickOpen() {
		
		log.info("Selecting XML directory");
		
		directoryChooser.setTitle("Select Morningstar XML Directory");
		
		final File selectedDirectory = directoryChooser.showDialog(main.getScene().getWindow());
		
		if(selectedDirectory == null) 
			return;
		
		log.debug("{} directory selected", selectedDirectory.getAbsolutePath());
					
		
		for(File file: selectedDirectory.listFiles()) {
			
			FileProcessor fileProcessor = new FileProcessor();
			fileProcessor.process(file);
			
		}
	}
	
	@FXML
	public void onClickExit(final ActionEvent e) {
			
		log.info("Exiting");
		
	    final Stage stage = (Stage) main.getScene().getWindow();
	    stage.close();
	}
}
