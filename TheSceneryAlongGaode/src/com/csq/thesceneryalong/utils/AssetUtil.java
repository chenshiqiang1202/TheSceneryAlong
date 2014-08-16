/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年6月1日 下午2:39:33   
 * @version 1.0   
 */
package com.csq.thesceneryalong.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.csq.thesceneryalong.app.App;

public class AssetUtil {

	public static void copyFile(String assetFile, String destFile){
		InputStream is = null;
		OutputStream out = null;
		try {
			is = App.app.getAssets().open(assetFile);
			out = new FileOutputStream(destFile);
			byte[] buffer = new byte[8192];
			int count = 0;
			while((count = is.read(buffer)) > 0){
				out.write(buffer, 0, count);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}
