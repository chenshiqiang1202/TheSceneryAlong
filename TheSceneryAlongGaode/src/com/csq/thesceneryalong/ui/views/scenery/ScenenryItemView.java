/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月11日 下午4:52:20   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.views.scenery;

import com.csq.thesceneryalong.db.Scenery;

public interface ScenenryItemView {
	/**
	 * @description: 设置数据
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param scenery
	 */
	public void setData(Scenery scenery);
	
	/**
	 * @description: 释放资源
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public void releaseResources();
	
	/**
	 * @description: 当前页开始展示
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public void onShow();
	
	/**
	 * @description: 非当前页，暂停资源加载
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public void onHide();
}
