/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月18日 下午11:53:00   
 * @version 1.0   
 */
package com.csq.thesceneryalong.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import android.test.AndroidTestCase;

import com.csq.thesceneryalong.constant.PathConstants;

public class FileTest extends AndroidTestCase {

	/**
	 * @description: 删除整个目录
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public void deleteDirectory(){
		try {
			FileUtils.deleteDirectory(
					new File(PathConstants.getApppath() + "/test"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
