/**
 * 
 */
package com.hawthornlife.qrt.util;

import java.util.regex.Pattern;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.DoubleStringConverter;

/**
 * @author Derek Reynolds
 *
 */
@SuppressWarnings("restriction")
public class FxUtil {
	
	private static Pattern validDoubleText = Pattern.compile("((\\d*)|(\\d+\\.\\d*))");
	
	public static TextFormatter<Double> doubleTextFormatter() {
		return new TextFormatter<Double>(new DoubleStringConverter(), 0.0, 	
            change -> {
                String newText = change.getControlNewText() ;
                if (validDoubleText.matcher(newText).matches()) {
                    return change;
                } else 
                	return null;
            });
	}
}
