package com.example.neogalleryds;

import java.io.File;
import java.util.ArrayList;


import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ViewGroup _scrollViewgroup;
	private ResourceManager _resource;
	private HorizontalScrollView _scrollView;
	private ListView _listViewFolder;
	private FolderAdapter _folderAdapter;
	private ArrayList<String> _data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		_resource = new ResourceManager(this);
		_scrollViewgroup = (ViewGroup) findViewById(R.id.groupView1);
		_scrollView = (HorizontalScrollView) this.findViewById(R.id.horizontalScrollView1);
		_listViewFolder = (ListView) this.findViewById(R.id.listView1);
		
		File imageDir = new File(Environment.getExternalStorageDirectory().toString());
		_data = new ArrayList<String>();
		
        // Duyệt đệ quy theo chiến lược
        if (imageDir.exists()) 
            dirFolder(imageDir);
        
		_folderAdapter = new FolderAdapter(this, _data);
		_listViewFolder.setAdapter(_folderAdapter);
		
		populateFunctionBar();
	}
	
	 public void dirFolder(File file) {
	    	
    	// Kiểm tra tên có hợp lệ không
        if (file.getName().startsWith(".") || file.getName().startsWith("com."))
            return;

        File[] files = file.listFiles();

        // Kiễm tra các tập tin con có rỗng không
        if (files == null)
            return;

        for (File f : files) {
        	
        	// Kiểm tra xem có phải ảnh không
            if (ImageSupporter.isImage(f)) 
            {
            	String path = f.getParentFile().getAbsolutePath();
            	
            	if (!_data.contains(path))
            		_data.add(path);
            }

            // Kiểm tra phải thư mục không
            if (f.isDirectory())
                dirFolder(f);
        }

	}
	 
	public void populateFunctionBar()
	{
		ArrayList<IconData> iconList = _resource.getImageFunctionIcon(getResources());
		
		for (int i = 0; i < iconList.size(); i++) 
		{
			final View singleFrame = getLayoutInflater().inflate(R.layout.function_item, null);				
			singleFrame.setId(i);
			
			ImageView icon = (ImageView) singleFrame.findViewById(R.id.imageView1);
			
			icon.setImageBitmap(iconList.get(i)._bitmap);
			
			_scrollViewgroup.addView(singleFrame);
		}
		
		_scrollView.setVisibility(View.GONE);
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
		else if (id == R.id.addNewAlbum)
		{
			return true;
		}
		else if (id == R.id.selectMode)
		{
			if (_scrollView.getVisibility() == View.GONE)
				_scrollView.setVisibility(View.VISIBLE);
			else if (_scrollView.getVisibility() == View.VISIBLE)
				_scrollView.setVisibility(View.GONE);
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
