package com.hawthornlife.qrt;

import javafx.scene.*;
import javafx.stage.*;
import javafx.fxml.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hawthornlife.qrt.domain.Fund;
import com.hawthornlife.qrt.service.FundHoldingCallable;
import com.hawthornlife.qrt.service.FundHoldingService;
import com.hawthornlife.qrt.service.FundHoldingServiceImpl;
import com.hawthornlife.qrt.service.FundService;
import com.hawthornlife.qrt.service.FundServiceImpl;
import com.hawthornlife.qrt.service.InvestmentReportService;
import com.hawthornlife.qrt.service.InvestmentReportServiceImpl;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javafx.scene.control.TextFormatter;

import javafx.util.converter.DoubleStringConverter;;

/**
 * Controller for main window in the application.
 * This controller handles the opening of the directory holding the XML files,
 * generating the investment report.
 * 
 * @author Derek Reynolds
 *
 */
@SuppressWarnings("restriction")
public class QrtController {

	private static Logger log = LoggerFactory.getLogger(QrtController.class);
	
	@FXML
	private VBox main;
	
	@FXML
	private GridPane mainGrid;
	
	private DirectoryChooser directoryChooser = new DirectoryChooser();
	
	private SortedMap<String, Fund> funds = new TreeMap<>();
	
	private Pattern validDoubleText = Pattern.compile("((\\d*)|(\\d+\\.\\d*))");
	
	/**
	 * Presents a dialog to navigate to the directory where the XML files are
	 * located. Creates a Fund per file and shows the fund to the user allowing
	 * them to enter the AUM for the fund.
	 * 
	 * @throws IOException
	 */
	@FXML
	public void onClickOpen() throws IOException {
		
		log.info("Selecting XML directory");
		
		directoryChooser.setTitle("Select Morningstar XML Directory");
		
		final File selectedDirectory = directoryChooser.showDialog(main.getScene().getWindow());
		
		if(selectedDirectory == null) 
			return;
		
		log.info("{} directory selected", selectedDirectory.getAbsolutePath());		
							
		FundService fundService = new FundServiceImpl();
		
		int row = 0;
		int column = 0;
		
		for(File file: selectedDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"))) {
			
			Fund fund = fundService.getFundSummary(file);
			funds.put(fund.getIsin(), fund);
			layoutFunds(row, column, fund);
			if(column == 2) {
				row++;
				column = 0;
			} else {
				column = 2;
			}
		}
		
		
	}
	
	/**
	 * Handles the exit event. Closes the application. 
	 * 
	 * @param e
	 */
	@FXML
	public void onClickExit(final ActionEvent e) {
			
		log.info("Exiting");
		
	    ((Stage) main.getScene().getWindow()).close();
	    
	}
	
	/**
	 * Generates the Investment report based on the funds read from the XML.
	 * The report will only contain funds with an AUM greater than zero.
	 * 
	 * @param e
	 */
	@FXML
	public void onClickGenerateReport(final ActionEvent e) {
		
		log.info("Generating Investment Report");
		
		mainGrid.requestFocus();
		
		ProgressDialog<String> progressDialog = new ProgressDialog<>(main.getScene().getWindow(), "Generating Investment Report");
		
		progressDialog.addTaskEndNotification(result -> {
			System.out.println(result);		       
		});
		 
		progressDialog.exec("Calculating", inputParam -> {
		       
			ExecutorService executor = Executors.newWorkStealingPool();

			List<Callable<Boolean>> callables = new ArrayList<>();
			
			FundHoldingService fundHoldingService = new FundHoldingServiceImpl();
			
			for(Fund fund: this.funds.values()) {
				if(fund.getAssetUnderManagement() > 0.0) {
					callables.add(new FundHoldingCallable(fundHoldingService, fund));
				}
			}
			
			try {
				
				List<Future<Boolean>> results = executor.invokeAll(callables);
				
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			executor.shutdown();
			
			InvestmentReportService InvestmentReportService = new InvestmentReportServiceImpl(this.funds);
			
			InvestmentReportService.generate();
			
		    return new Integer(1);
		});
		
		
	}
	
	/**
	 * 
	 * @param row
	 * @param column
	 * @param fund
	 */
	private void layoutFunds(int row, int column, Fund fund) {
		
		log.debug("Layout row {}, column {} - {}", row, column, fund.getLegalName());
		
		TextField aumField = new TextField();
		aumField.setText("0");
		
		TextFormatter<Double> textFormatter = new TextFormatter<Double>(new DoubleStringConverter(), 0.0, 
	            change -> {
	                String newText = change.getControlNewText() ;
	                if (validDoubleText.matcher(newText).matches()) {
	                    return change ;
	                } else 
	                	return null ;
	            });

		aumField.setTextFormatter(textFormatter);

        textFormatter.valueProperty().addListener((obs, oldValue, newValue) -> {
        	fund.setAssetUnderManagement(newValue);
        });		
		
		Label aumLabel = new Label(fund.getLegalName() + " (" + fund.getIsin() + ")"); // F is mnemonic
		aumLabel.setLabelFor(aumField);
		
		mainGrid.add(aumLabel, column, row);
		mainGrid.add(aumField, column + 1, row);
		

	}
	

}
