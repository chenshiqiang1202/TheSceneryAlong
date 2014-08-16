/**
 * @description:
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月14日 上午12:09:47
 * @version 1.0
 */
package com.csq.thesceneryalong.utils;

public class TimeUtil {

	/**
	 * @description: "HH:mm:ss",通过时间计算，例如time=30*1000(30秒)
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param time
	 * @return
	 */
	public static String getFormatedTimeHMS(long time)
	{
        //return formaterHMS.format(time);  小时出错
        int s = (int) (time/1000);
		int hh = s/3600;
		int mm = (s - hh*3600)/60;
		int ss = s - hh*3600 - mm*60;
        return String.format("%02d", hh)+":"+String.format("%02d", mm)+":"+String.format("%02d", ss);
	}
	
	/**
	 * @description: "HH:mm′ss″",通过时间计算，例如time=30*1000(30秒)
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param time
	 * @return
	 */
	public static String getFormatedTimeHMS1(long time)
	{
        //return formaterHMS.format(time);  小时出错
        int s = (int) (time/1000);
		int hh = s/3600;
		int mm = (s - hh*3600)/60;
		int ss = s - hh*3600 - mm*60;
        return hh+":"+String.format("%02d", mm)+"′"+String.format("%02d", ss)+"″";
	}
	
	/**
	 * 返回"ss″"或"mm′ss″"或"HH:mm′ss″"
	 * @param time
	 */
	public static String getFormatedTime(long time)
	{
        int s = (int) (time/1000);
		int hh = s/3600;
		int mm = (s - hh*3600)/60;
		int ss = s - hh*3600 - mm*60;
		StringBuilder sb = new StringBuilder();
		if(hh > 0){
			sb.append(hh+":");
		}
		if(mm > 0){
			sb.append(String.format("%02d", mm)+"′");
		}
		sb.append(String.format("%02d", ss)+"″");
        return sb.toString();
	}
	
	/**
	 * @description: "HH:mm",通过时间计算，例如time=30*1000(30秒)
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param time
	 * @return
	 */
	public static String getFormatedTimeHM(long time)
	{
        //return formaterHMS.format(time);  小时出错
        int s = (int) (time/1000);
		int hh = s/3600;
		int mm = (s - hh*3600)/60;
        return String.format("%02d", hh)+":"+String.format("%02d", mm);
	}
	
	/**
	 * @description: "mm:ss",通过时间计算，例如time=30*1000(30秒)
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param time
	 * @return
	 */
	public static String getFormatedTimeMS(long time)
	{
        //return formaterHMS.format(time);  小时出错
        int s = (int) (time/1000);
		int hh = s/3600;
		int mm = (s - hh*3600)/60;
		int ss = s - hh*3600 - mm*60;
        return String.format("%02d", mm)+":"+String.format("%02d", ss);
	}
	
	/**
	 * @description: "HH小时 mm分 ss秒",通过时间计算，例如time=30*1000(30秒)
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param time
	 * @return
	 */
	public static String getFormatedTimeHMSChinese(long time)
	{
        //return formaterHMSChinese.format(time);
		int s = (int) (time/1000);
		int hh = s/3600;
		int mm = (s - hh*3600)/60;
		int ss = s - hh*3600 - mm*60;
        return hh+"小时 "+String.format("%02d", mm)+"分 "+String.format("%02d", ss)+"秒";
	}
	
	/**
	 * @description: "HHh mmm sss",通过时间计算，例如time=30*1000(30秒)
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param time
	 * @return
	 */
	public static String getFormatedTimeHMSEng(long time)
	{
        //return formaterHMSChinese.format(time);
		int s = (int) (time/1000);
		int hh = s/3600;
		int mm = (s - hh*3600)/60;
		int ss = s - hh*3600 - mm*60;
        return hh+"h "+String.format("%02d", mm)+"m "+String.format("%02d", ss)+"s";
	}
	
	/**
	 * @description: 取2个值，大于1小时只显示小时和分钟，例如：11小时40分钟 或 40分钟30秒
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param time
	 * @return
	 */
	public static String getFormatedTimeTwoValueChinese(long time)
	{
        //return formaterHMS.format(time);  小时出错
        int s = (int) (time/1000);
		int hh = s/3600;
		int mm = (s - hh*3600)/60;
		int ss = s - hh*3600 - mm*60;
		
		if(hh > 0){
			return String.format("%02d", hh)+"小时"+String.format("%02d", mm)+"分钟";
		}else{
			return String.format("%02d", mm)+"分钟"+String.format("%02d", ss)+"秒";
		}
	}
	
	/**
	 * @description: 取2个值，大于1小时只显示小时和分钟，例如：11h40m 或 40m30s
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param time
	 * @return
	 */
	public static String getFormatedTimeTwoValueEng(long time)
	{
        //return formaterHMS.format(time);  小时出错
        int s = (int) (time/1000);
		int hh = s/3600;
		int mm = (s - hh*3600)/60;
		int ss = s - hh*3600 - mm*60;
		
		if(hh > 0){
			return String.format("%02d", hh)+"h"+String.format("%02d", mm)+"m";
		}else{
			return String.format("%02d", mm)+"m"+String.format("%02d", ss)+"s";
		}
	}
	
	
	/**
	 * 不包含秒的字符串，“hh小时mm分钟”或“mm分钟”
	 * @param time
	 * @return
	 */
	public static String getFormatedTimeNotContainSecond(long time)
	{
		int s = (int) (time/1000);
		int hh = s/3600;
		int mm = (s - hh*3600)/60;
		
		if(hh > 0){
			return String.format("%02d", hh)+"小时"+String.format("%02d", mm)+"分钟";
		}else{
			return String.format("%02d", mm)+"分钟";
		}
	}
	
}
