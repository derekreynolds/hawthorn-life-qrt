/**
 * 
 */
package com.hawthornlife.qrt.util;

import javafx.scene.control.TextField;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hawthornlife.qrt.domain.Fund;

/**
 * This observer updates the total AUM value if a fund-level AUM changes.
 * 
 * @author Derek Reynolds
 *
 */
@SuppressWarnings("restriction")
public class TotalAumObserver implements Observer {

	private static Logger log = LoggerFactory.getLogger(TotalAumObserver.class);
	
	private final TextField textField;
	
	private final Collection<Fund> funds;
	
	NumberFormat formatter = new DecimalFormat("#0.00");     

	
	public TotalAumObserver(final TextField textField, final Collection<Fund> funds) {
		this.textField = textField;
		this.funds = funds;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		
		log.debug("Entering");
		
		Double totalAumValue = funds.stream().mapToDouble(f -> f.getAssetUnderManagement()).sum();		
		
		log.info("New total AUM is {}", formatter.format(totalAumValue));
		
		textField.setText(formatter.format(totalAumValue));

	}

}
