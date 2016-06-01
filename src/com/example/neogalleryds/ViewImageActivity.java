package com.example.neogalleryds;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import FullscreenImage.CustomScroller;
import FullscreenImage.FullscreenImageAdapter;
import FullscreenImage.MyViewPager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ViewImageActivity extends Activity {
	/**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private MyViewPager _viewPager;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            _viewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };   

	private ArrayList<String> _filePaths;
	private FullscreenImageAdapter _adapter;
	
	private ImageButton btnMark;
	private ImageButton btnAdd;
	private ImageButton btnDelete;
	private Context _this;
	
	// dành cho slideshow
	Timer timer;
	int page;
	int maxPage;

    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        _viewPager = (MyViewPager) findViewById(R.id.pager);
        
        /////////////////////////////////////////////////////////////
        
        _this = this;
        
		_viewPager.setPageMargin(10);
		
		Intent i = getIntent();
		int position = i.getIntExtra("position", 0);
		_filePaths = (ArrayList<String>) i.getSerializableExtra("filePaths");
		boolean slideshow = i.getBooleanExtra("slideshow", false);
		int wait = i.getIntExtra("wait", 500);
		int slide = i.getIntExtra("slide", 2000);
		
		_adapter = new FullscreenImageAdapter(this, _filePaths);
		_viewPager.setAdapter(_adapter);		
		_viewPager.setCurrentItem(position);

		btnMark = (ImageButton) findViewById(R.id.btnMark);
		btnMark.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String path = _filePaths.get(_viewPager.getCurrentItem());
				MainActivity._markManager.marksImage(path);
			}
		});
		
		btnAdd = (ImageButton) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String path = _filePaths.get(_viewPager.getCurrentItem());
				ArrayList<String> paths = new ArrayList<String>();
				paths.add(path);
				chooseAlbum(paths);
			}
		});
		
		btnDelete = (ImageButton) findViewById(R.id.btnDelete);
		btnDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(_this);
				builder.setMessage("Are you sure you want to delete?")
					   .setTitle("Delete");
				
				builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int pos = _viewPager.getCurrentItem();
						String path = _filePaths.get(pos);
						ArrayList<String> paths = new ArrayList<String>();
						paths.add(path);
						
						MainActivity._imageAdapter.removeImages(paths);
						
						switch (MainActivity.currentTab) {
						case 0: // tab All
							if (MainActivity._folderManager.deletesImages(paths))
		                 		Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
		                 	else
		                 		Toast.makeText(getApplicationContext(), "Fail To Delete", Toast.LENGTH_SHORT).show();
							break;
						case 1: // tab Albums
							if (MainActivity._albumManager.deletesImages(paths))
		                 		Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
		                 	else
		                 		Toast.makeText(getApplicationContext(), "Fail To Delete", Toast.LENGTH_SHORT).show();
							break;
						}

						if (!_adapter.removeImage(pos)) // nếu đã xoá hết ảnh
							finish();		
					}
				});
				
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();				
					}
				});
				
				builder.show();
			}
		});

		if (slideshow == true) {
			_viewPager.disablePaging();
			page = 0;
			maxPage = _filePaths.size();
			try {
			    Field mScroller;
			    mScroller = ViewPager.class.getDeclaredField("mScroller");
			    mScroller.setAccessible(true);
			    Interpolator sInterpolator = new DecelerateInterpolator();
			    CustomScroller scroller = new CustomScroller(_this, sInterpolator, slide);
			    mScroller.set(_viewPager, scroller);
			} catch (NoSuchFieldException e) {
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			}
			pageSwitcher(slide + wait);
		}
    }
    
 // Hiện dialog dể chọn album 
    private void chooseAlbum(final ArrayList<String> images)
    {
    	AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
    	builderSingle.setIcon(R.drawable.icon);
    	builderSingle.setTitle("Select an Album: ");

    	final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
    	
    	// Nạp dữ liệu cho thông báo qua adapter 
    	arrayAdapter.addAll(MainActivity._albumManager.getsAlbumList());

    	// Tạo sự kiện cho nút hủy thông báo
    	builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
    	{
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
    	});

    	// Tạo sự kiện cho nút chọn album
    	builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() 
    	{
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                
                for (String path: images) {
                	MainActivity._albumManager.addsImageToAlbum(strName, path);
                }
                
            }
        });
    	
    	builderSingle.show();
    }
    
    public void pageSwitcher(int miliseconds) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new RemindTask(), 0, miliseconds);
    }

        // this is an inner class...
    class RemindTask extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable() {
                public void run() {

                    if (page >= maxPage) { 
                        timer.cancel();
                    } else {
                        _viewPager.setCurrentItem(page++);
                    }
                }
            });

        }
    }
    
    
    
    /////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    public void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        _viewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

}
