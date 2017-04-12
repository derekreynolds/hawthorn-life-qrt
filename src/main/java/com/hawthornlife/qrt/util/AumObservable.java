/**
 * 
 */
package com.hawthornlife.qrt.util;

import java.util.Observable;

/**
 * @author Derek Reynolds
 *
 */
public class AumObservable extends Observable {

	public void update() {
		
		this.setChanged();
		this.notifyObservers();
	}
}
