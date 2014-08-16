/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月17日 上午12:00:40   
 * @version 1.0   
 */
package com.csq.thesceneryalong.utils.edittextfilter;

import android.text.InputFilter;
import android.text.Spanned;

import com.csq.thesceneryalong.utils.StringUtils;

public class EditTextLengthFilter implements InputFilter {

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	
	int MAX_CHINESE;// 最大中文字符长度 一个汉字算两个字母  
    

	// ----------------------- Constructors ----------------------
    
    public EditTextLengthFilter(int MAX_CHINESE) {  
        super();  
        this.MAX_CHINESE = MAX_CHINESE;  
    }

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) 
	{
		// TODO Auto-generated method stub
		float oldLen = StringUtils.getChineseCharLength(dest.toString());
		float newLen = StringUtils.getChineseCharLength(source.toString());
        
        if (oldLen + newLen > MAX_CHINESE) {
            return StringUtils.limitedChineseCharLength(source.toString(), 
            		MAX_CHINESE - oldLen);  
        } else {  
            return source;
        } 
	}

	// --------------------- Methods public ----------------------

	// --------------------- Methods private ---------------------
	
	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
