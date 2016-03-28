package com.example.galleryds;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainActivity extends Activity {

	TabHost _mainTabHost;
	File _imageDir;
	GridView _gridViewAll;
	ArrayList<Bitmap> _allImages;
	ArrayList<File> _allImageFiles;
	private ImageAdapter _adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		LoadTabs();
		LoadImages();
		
		ImageButton b = (ImageButton) findViewById(R.id.btnNote);
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TurnOnSelectionMode();
			}
		});
		
		ImageButton b1 = (ImageButton) findViewById(R.id.btnStar);
		b1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TurnOffSelectionMode();
			}
		});
		
		ImageButton b2 = (ImageButton) findViewById(R.id.btnTrash);
		b2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DeleteSelectedImages();
			}
		});
	}


	public void LoadImages()
	{		
		_gridViewAll = (GridView) findViewById(R.id.gridView1);
		_imageDir = new File(Environment.getExternalStorageDirectory().toString());
		
		if (_imageDir.exists())
	    {			
			_allImages = new ArrayList<Bitmap>();
			_allImageFiles = new ArrayList<File>();
						
			DirFolder(_imageDir);
			
			_adapter = new ImageAdapter(this, DataHolder.Convert(_allImageFiles, _allImages));
			_gridViewAll.setAdapter(_adapter);
	    }
	}
	
	public void LoadTabs()
	{
		// Lấy TabHost từ Id cho trước
		_mainTabHost = (TabHost) findViewById(R.id.tabhost);
		_mainTabHost.setup();
		
		// Tạo cấu hình cho tab1 : tab chứa toàn bộ ảnh
		TabHost.TabSpec spec = _mainTabHost.newTabSpec("tab1");
		spec.setContent(R.id.tab1);
		spec.setIndicator("All");
		_mainTabHost.addTab(spec);
	
		// Tạo cấu hình cho tab2 : tab chứa toàn bộ album ảnh
		spec = _mainTabHost.newTabSpec("tab2");
		spec.setContent(R.id.tab2);
		spec.setIndicator("Albums");
		_mainTabHost.addTab(spec);
		
		// Tạo cấu hình cho tab3 : tab chứa toàn bộ ảnh yêu thích
		spec = _mainTabHost.newTabSpec("tab3");
		spec.setContent(R.id.tab3);
		spec.setIndicator("Favourite");
		_mainTabHost.addTab(spec);
		
		//Thiết lập tab mặc định được chọn ban đầu là tab 0
		_mainTabHost.setCurrentTab(0);
	}
	 
	public void DirFolder(File file)
	{
		if (file.getName().equals(".thumbnails"))
			return;
		
		File[] files = file.listFiles();
		
		for (File f : files)
		{			
			if (ImageSupporter.IsImage(f))
            {			
				_allImageFiles.add(f);
				
				Bitmap b =  ImageSupporter.DecodeSampledBitmapFromFile(f, 100, 100);
				_allImages.add(b);
            }
            
            if (f.isDirectory())
            	DirFolder(f);
		}
		
	}
	
	public void TurnOnSelectionMode()
	{
		int count = _adapter.getCount();
		
		for (int i = 0; i < count; i++)
		{		
			View view = getViewByPosition(i, _gridViewAll);

			ViewHolder holder = (ViewHolder) view.getTag();
			
			holder.checkbox.setVisibility(View.VISIBLE);
			holder.checkbox.setChecked(false);
		}
	}

	public void DeleteSelectedImages()
	{
		int count = _adapter.getCount();
		
		for (int i = count - 1; i >= 0; i--)
		{		
			View view = getViewByPosition(i, _gridViewAll);

			ViewHolder holder = (ViewHolder) view.getTag();
			
			if (holder.checkbox.isChecked() == true)
			{				
				_adapter.remove(holder.id);			
			}
		}
	}
	
	public void TurnOffSelectionMode()
	{
		int count = _adapter.getCount();
		
		for (int i = 0; i < count; i++)
		{		
			View view = getViewByPosition(i, _gridViewAll);

			ViewHolder holder = (ViewHolder) view.getTag();
			
			holder.checkbox.setVisibility(View.INVISIBLE);
			holder.checkbox.setChecked(false);
		}
	}
	
	public View getViewByPosition(int pos, GridView listView) 
	{
	    final int firstListItemPosition = listView.getFirstVisiblePosition();
	    final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

	    if (pos < firstListItemPosition || pos > lastListItemPosition ) 
	    {
	        return listView.getAdapter().getView(pos, null, listView);
	    } else 
	    {
	        final int childIndex = pos - firstListItemPosition;
	        return listView.getChildAt(childIndex);
	    }
	}
	
	@Override
	protected void onResume() 
	{

		super.onResume();
		
		//LoadImages();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
