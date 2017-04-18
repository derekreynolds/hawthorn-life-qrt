package com.hawthornlife.qrt;

import javafx.stage.*;
import javafx.fxml.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hawthornlife.qrt.domain.Fund;
import com.hawthornlife.qrt.util.FundAumObservable;
import com.hawthornlife.qrt.util.FxUtil;
import com.hawthornlife.qrt.util.TotalAumObserver;
import com.hawthornlife.qrt.service.FundHoldingCallable;
import com.hawthornlife.qrt.service.FundHoldingService;
import com.hawthornlife.qrt.service.FundHoldingServiceImpl;
import com.hawthornlife.qrt.service.FundService;
import com.hawthornlife.qrt.service.FundServiceImpl;
import com.hawthornlife.qrt.service.InvestmentReportService;
import com.hawthornlife.qrt.service.InvestmentReportServiceImpl;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.event.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.scene.control.TextFormatter;


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
	
	private SortedMap<String, Fund> funds = new TreeMap<>();	
	
	private FundAumObservable aumObservable = new FundAumObservable();
	
	
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
		
		DirectoryChooser directoryChooser = new DirectoryChooser();
		
		directoryChooser.setTitle("Select Morningstar XML Directory");
		
		final File selectedDirectory = directoryChooser.showDialog(main.getScene().getWindow());
		
		if(selectedDirectory == null) 
			return;
		
		log.info("{} directory selected", selectedDirectory.getAbsolutePath());		
							
		FundService fundService = new FundServiceImpl();
		
		int row = 0;
		int column = 0;
		
		try {
		
			StopWatch watch = StopWatch.createStarted();
			
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
			
			layoutTotal(++row);
			
			watch.stop();
			
			log.debug("Time taken to read XML files: {}", watch.getTime());
			
		} catch(Exception ex) {
			
			Alert alert = new Alert(Alert.AlertType.ERROR);
			
			alert.setTitle("Hawthorn Life QRT");
			alert.setHeaderText("Report Error");
			alert.setContentText(ex.getMessage());

			alert.showAndWait();
		}
		
	}
	
	/**
	 * Handles the exit event. Closes the application. 
	 * 
	 * @param event
	 */
	@FXML
	public void onClickExit(final ActionEvent event) {
			
		log.info("Exiting");
		
	    ((Stage) main.getScene().getWindow()).close();
	    
	}
	
	/**
	 * Generates the Investment report based on the funds read from the XML.
	 * The report will only contain funds with an AUM greater than zero.
	 * 
	 * @param event
	 */
	@FXML
	public void onClickGenerateReport(final ActionEvent event) {
		
		log.debug("Entering");
		
		log.info("Generating Investment Report");
		
		mainGrid.requestFocus();
		
		ProgressDialog<String> progressDialog = new ProgressDialog<>(main.getScene().getWindow(), "Generating Investment Report");
				 
		progressDialog.exec("Calculating", inputParam -> {
		       
			StopWatch watch = StopWatch.createStarted();
			
			ExecutorService executor = Executors.newWorkStealingPool();

			List<Callable<Boolean>> callables = new ArrayList<>();
			
			FundHoldingService fundHoldingService = new FundHoldingServiceImpl();
						
			this.funds.values().stream()
				.filter(f -> f.getAssetUnderManagement() > 0.0)
				.forEach(f -> callables.add(new FundHoldingCallable(fundHoldingService, f)));
			
			try {
						
				for(Future<Boolean> result: executor.invokeAll(callables)) {
					if(!result.get())
						return new Integer(0);
				}
				
			} catch (InterruptedException | ExecutionException e) {
				log.error("Error reading the fund holdings.", e);
				return new Integer(0);
			} finally {
				log.debug("Shutting down executor.");
				executor.shutdown();
			}
			
			watch.split();
			log.info("Time taken to read fund holdings: {}", watch.getSplitTime());
		
			InvestmentReportService InvestmentReportService = new InvestmentReportServiceImpl(this.funds);
			
			InvestmentReportService.generate();
			
			watch.stop();
			
			log.info("Time taken to generate report: {}", watch.getTime());
			
		    return new Integer(1);
		});
		
		progressDialog.addTaskEndNotification(value -> {
			
			if(value == 1) {
				
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				
				alert.setTitle("Hawthorn Life QRT");
				alert.setHeaderText("Report Generated Successfully");
				alert.setContentText("Hawthorn Life QRT successfully generated.");
	
				alert.showAndWait();
				
			} else {
				
				Alert alert = new Alert(Alert.AlertType.ERROR);
				
				alert.setTitle("Hawthorn Life QRT");
				alert.setHeaderText("Report Generation Error");
				alert.setContentText("An error occured while generating the QRT report. Please consult log files.");

				alert.showAndWait();
			}
			
		});
		
		
	}
	
	/**
	 * Lays out the Label and Text box pair in a column. Two column pairs per row.
	 * 
	 * @param row
	 * @param column
	 * @param fund
	 */
	private void layoutFunds(int row, int column, Fund fund) {
		
		log.debug("Entering with row {}, column {} - {}", row, column, fund.getLegalName());
		
		TextField aumField = new TextField();
		aumField.setText("0");
		
		TextFormatter<BigDecimal> textFormatter = FxUtil.bigDecimalTextFormatter();

		textFormatter.valueProperty().addListener((obs, oldValue, newValue) -> {
        	fund.setAssetUnderManagement(newValue.doubleValue());
        	aumObservable.update(newValue.doubleValue());        	
        });	
				
		aumField.setTextFormatter(textFormatter);        	
		
		Label aumLabel = new Label(fund.getLegalName() + " (" + fund.getIsin() + ")");
		aumLabel.setLabelFor(aumField);
		
		mainGrid.add(aumLabel, column, row);
		mainGrid.add(aumField, column + 1, row);		

	}
	
	/**
	 * Lays out the total AUM textbox.
	 * 
	 * @param rowIndex
	 */
	private void layoutTotal(int rowIndex) {
		
		log.debug("Entering with {}", rowIndex);
		
		Label aumLabel = new Label("Assets Under Management Total");
		
		TextField aumField = new TextField();
		aumField.setText("0.0");
		aumField.setTextFormatter(FxUtil.bigDecimalTextFormatter());
						
		aumObservable.addObserver(new TotalAumObserver(aumField, this.funds.values()));
		
		mainGrid.add(aumLabel, 0, rowIndex);
		mainGrid.add(aumField, 1, rowIndex);
		
	}

}
