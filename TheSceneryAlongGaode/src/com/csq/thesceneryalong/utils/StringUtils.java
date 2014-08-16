/**
 * @description:
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月14日 上午12:09:47
 * @version 1.0
 */
package com.csq.thesceneryalong.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.text.TextUtils;

public class StringUtils {
    
    /**
     * 格式化小数数据,会四舍五入
     * @param data 要格式化的小数
     * @param decimalNum 小数点的个数
     * @return
     */
    public static String getFormatDecimal(double data,int decimalNum){
    	String format = "";
    	try {
    		StringBuilder fs = new StringBuilder();
            fs.append("#.");
            if(decimalNum < 1){
                decimalNum = 3;
            }
            for(int i = 0 ; i < decimalNum ; i++){
                fs.append("#");
            }
            DecimalFormat df = new DecimalFormat(fs.toString());
            format = df.format(data);
		} catch (Exception e) {
			// TODO: handle exception
			e.toString();
		}
        return format;
    }
    
    /**
     * 将距离格式化，返回带单位的字符串
     * decimalNum 小数点个数，默认3个小数点
     */
    public static String getFormatDistance(int mile,int decimalNum, String unitM, String unitKm){
        String format = "";
        if(mile <= 1000){
            format = mile + unitM;
        }else{
            float f = mile/1000f;
            format = getFormatDecimal(f, decimalNum) + unitKm;
        }
        return format;
    }
    
    /**
     * 小数四舍五入取整
     * @param data
     * @return
     */
    public static int decimalRoundToInt(double data){
    	return new BigDecimal(data).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
    }
    
    
    /**
     * 过滤特殊字符
     */
    public static String filterIllegalWords(String str) throws PatternSyntaxException {
        // 只允许字母和数字
        // String regEx = "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
    
    private static String chineseEx = "[\\u4e00-\\u9fa5]"; // unicode编码，判断是否为汉字
    /**
     * 获取字符串的长度，中文占一个字符,英文数字占半个字符
     */
    public static float getChineseCharLength(String value){
    	float count = 0;  
        Pattern p = Pattern.compile(chineseEx);  
        Matcher m = p.matcher(value);  
        while (m.find()) {  
            for (int i = 0; i <= m.groupCount(); i++) {  
                count++;  
            }  
        }  
        count += 0.5f * (value.length() - count);
        return count;
    }
    
    /**
     * 截取指定中文字符长度的字符串
     * @param value
     * @param chineseCharLength
     */
    public static String limitedChineseCharLength(String value, float chineseCharLength){
    	int length = value.length();
     	int index = (int) chineseCharLength;
    	while(getChineseCharLength(value.substring(0, index)) < chineseCharLength 
    			&& index < length){
    		index++;
    	}
    	return value.substring(0, index);
    }
    
    /**
     * @description: 避免返回null，如果是null则返回""
     * @author: chenshiqiang E-mail:csqwyyx@163.com
     * @param str
     * @return
     */
    public static String avoidNull(Object str, String emptyStr){
    	return (str == null || (str instanceof String && TextUtils.isEmpty((String)str)))
    			? emptyStr : str.toString();
    }
    
    /**
	 * @author chenshiqiang
	 * Description: 通过文件长度获取文件大小字符串（如xxMB）
	 * @param fileLength
	 * @return
	 */
    public static String getSizeStr(long fileLength) {
    	String strSize = "";
		try {
			if(fileLength >= 1024*1024*1024){
				strSize = (float)Math.round(10*fileLength/(1024*1024*1024))/10 + " GB";
			}else if(fileLength >= 1024*1024){
				strSize = (float)Math.round(10*fileLength/(1024*1024*1.0))/10 + " MB";
			}else if(fileLength >= 1024){
				strSize = (float)Math.round(10*fileLength/(1024))/10 + " KB";
			}else if(fileLength >= 0){
				strSize = fileLength + " B";
			}else {
				strSize = "0 B";
			}
		} catch (Exception e) {
			e.printStackTrace();
			strSize = "0 B";
		}
		return strSize;
	}

}
