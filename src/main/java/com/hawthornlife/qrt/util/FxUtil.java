/**
 * 
 */
package com.hawthornlife.qrt.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.BigDecimalStringConverter;

/**
 * Util class for Java FX components.
 * 
 * @author Derek Reynolds
 *
 */
@SuppressWarnings("restriction")
public class FxUtil {
	
	private static Pattern validDoubleText = Pattern.compile("((\\d*)|(\\d+\\.\\d*))");
	
	/**
	 * Creates a {@link TextFormatter} that only allows a user to input numbers.
	 * 
	 * @return {@link TextFormatter} 
	 */
	public static TextFormatter<BigDecimal> bigDecimalTextFormatter() {
		return new TextFormatter<BigDecimal>(new BigDecimalStringConverter(), BigDecimal.ZERO, 	
            change -> {
                String newText = change.getControlNewText() ;
                if (validDoubleText.matcher(newText).matches()) {
                    return change;
                } else 
                	return null;
            });
	}
}
