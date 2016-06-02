package FullscreenImage;

import Adapter.FullscreenImageAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyViewPager extends android.support.v4.view.ViewPager {
	
	boolean disablePaging = false;

	public MyViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void disablePaging() {
		disablePaging = true;
	}
	
    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	if (disablePaging || isZoomed())
    		return false;
    	
    	return super.onTouchEvent(event);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (disablePaging || isZoomed())
    		return false;
		
	    return super.onInterceptTouchEvent(event);
	}
	
	// nếu ảnh đang zoom thì không chuyển ảnh
	public boolean isZoomed() {
		FullscreenImageAdapter adapter = (FullscreenImageAdapter) this.getAdapter();
    	TouchImageView img = (TouchImageView) findViewWithTag(adapter.getFilePaths().get(getCurrentItem()));
    	if (img._savedScale > 1)    	
    		return true;
    	return false;
	}
}

