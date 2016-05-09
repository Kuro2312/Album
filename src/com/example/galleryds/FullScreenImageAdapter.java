package com.example.galleryds;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FullScreenImageAdapter extends PagerAdapter{

	private Activity _activity;
	private ArrayList<String> _filePaths;
	private ArrayList<View> _pages;
	private LayoutInflater _inflater;

	// constructor
	public FullScreenImageAdapter(Activity activity,
			ArrayList<String> filePaths) {
		_activity = activity;
		_filePaths = filePaths;
		_pages = new ArrayList<View>();
		_inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return _filePaths.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	@Override
    public Object instantiateItem(ViewGroup container, int position) {
		
        View viewLayout = _inflater.inflate(R.layout.fullscreen_image_layout, container, false); 
        TouchImageView image = (TouchImageView) viewLayout.findViewById(R.id.fullScreenImage);
        
        Display display = _activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        
        Bitmap bitmap = ImageSupporter.decodeSampledBitmapFromFile(new File( _filePaths.get(position)), size.x, size.x);        
        image.setImageBitmap(bitmap);
        
        image.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((ViewImageActivity) _activity).toggle();
			}
		});
        
        ((ViewPager) container).addView(viewLayout);
        
        
        return viewLayout;
	}
	
	@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
	
	public boolean removeImage(int position) {
		_filePaths.remove(position);
		notifyDataSetChanged();
		if  (_filePaths.size() == 0)
			return false;
		return true;
	}

	@Override
	public int getItemPosition(Object object) {
		if (_pages.contains(object)) {
			return _pages.indexOf(object);
		}
		return POSITION_NONE;
	}
	
	
	
	
}
