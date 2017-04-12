/**
 * 
 */
package com.hawthornlife.qrt.util;

import javafx.scene.control.TextField;

import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.hawthornlife.qrt.domain.Fund;

/**
 * @author Derek Reynolds
 *
 */
@SuppressWarnings("restriction")
public class TotalAumObserver implements Observer {

	private final TextField textField;
	
	private final Collection<Fund> funds;
	
	public TotalAumObserver(final TextField textField, final Collection<Fund> funds) {
		this.textField = textField;
		this.funds = funds;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		
		Double totalAumValue = funds.stream().mapToDouble(f -> f.getAssetUnderManagement()).sum();
		
		textField.setText(totalAumValue.toString());

	}

}
