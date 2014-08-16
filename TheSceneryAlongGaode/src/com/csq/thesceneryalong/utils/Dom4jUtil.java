/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月17日 下午10:41:39   
 * @version 1.0   
 */
package com.csq.thesceneryalong.utils;

import org.dom4j.Element;

public class Dom4jUtil {

	public static String parseString(Element parentElement, String nodeName, String defaultValue){
		String ret = defaultValue;
		Element element = parentElement.element(nodeName);
		if(element != null){
			ret = element.getTextTrim();
		}
		return ret;
	}
	
	public static int parseInterger(Element parentElement, String nodeName, int defaultValue){
		int ret = defaultValue;
		Element element = parentElement.element(nodeName);
		if(element != null){
			try {
				ret = Integer.valueOf(element.getTextTrim());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	public static long parseLong(Element parentElement, String nodeName, long defaultValue){
		long ret = defaultValue;
		Element element = parentElement.element(nodeName);
		if(element != null){
			try {
				ret = Long.valueOf(element.getTextTrim());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	public static double parseDouble(Element parentElement, String nodeName, double defaultValue){
		double ret = defaultValue;
		Element element = parentElement.element(nodeName);
		if(element != null){
			try {
				ret = Double.valueOf(element.getTextTrim());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	public static float parseFloat(Element parentElement, String nodeName, float defaultValue){
		float ret = defaultValue;
		Element element = parentElement.element(nodeName);
		if(element != null){
			try {
				ret = Float.valueOf(element.getTextTrim());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return ret;
	}
	
}
