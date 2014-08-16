/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月18日 下午9:55:22   
 * @version 1.0   
 */
package com.csq.thesceneryalong.test;

import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.test.AndroidTestCase;

import com.csq.thesceneryalong.constant.PathConstants;

public class ZipTest extends AndroidTestCase {

	public void unPack(){
		String zipFile = PathConstants.getExportpath() 
				+ File.separator + "2014-05-14 1356.tsa";
		
		try {
			ZipInputStream inZip = new ZipInputStream(new java.io.FileInputStream(
					zipFile));

			ZipEntry zipEntry;
			String entryName;
			while ((zipEntry = inZip.getNextEntry()) != null) {
				entryName = zipEntry.getName();
				if (zipEntry.isDirectory() && entryName.contains(File.separator + "xml")) {
					System.out.println("zipList --- " + entryName);
					
					String um = entryName.substring(0, entryName.indexOf(File.separator + "xml"));
					System.out.println("zipList --- um = " + um);
				}
			}
			
			inZip.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
