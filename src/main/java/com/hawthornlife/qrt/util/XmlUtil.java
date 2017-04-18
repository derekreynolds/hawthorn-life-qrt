/**
 * 
 */
package com.hawthornlife.qrt.util;

import javax.xml.xpath.XPathFactory;

/**
 * @author Derek Reynolds
 *
 */
public class XmlUtil {

	public static final ThreadLocal<XPathFactory> getXpathFactory() {
		return new ThreadLocal<XPathFactory>()	{
	        @Override
	        protected XPathFactory initialValue()
	        {
	            return XPathFactory.newInstance();
	        }
		};
    }
	
}
