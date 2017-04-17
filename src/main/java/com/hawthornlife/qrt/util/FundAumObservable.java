/**
 * 
 */
package com.hawthornlife.qrt.util;

import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This observable is used to notify observers when a fund-level AUM
 * was changed.
 * 
 * @author Derek Reynolds
 *
 */
public class FundAumObservable extends Observable {

	private static Logger log = LoggerFactory.getLogger(FundAumObservable.class);
	
	/**
	 * Update that fund-level AUM value has changed.
	 * @param value
	 */
	public void update(Double value) {	
		
		log.debug("Entering with {}", value);
		
		this.setChanged();
		this.notifyObservers(value);
	}
}
