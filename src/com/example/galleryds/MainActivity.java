package com.example.galleryds;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainActivity extends Activity {

	TabHost mainTabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		LoadTabs();

	    //setupTab(new TextView(this), "Tab 1");
	    //setupTab(new TextView(this), "Tab 2");
	    //setupTab(new TextView(this), "Tab 3");
	}

	public void LoadTabs()
	{
		// Lấy TabHost từ Id cho trước
		mainTabHost = (TabHost) findViewById(R.id.tabhost);
		mainTabHost.setup();

		Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
		
		// Tạo cấu hình cho tab1 : tab chứa toàn bộ ảnh
		TabHost.TabSpec spec = mainTabHost.newTabSpec("tab1");
		spec.setContent(R.id.tab1);
		spec.setIndicator("All");
		mainTabHost.addTab(spec);
	
		// Tạo cấu hình cho tab2 : tab chứa toàn bộ album ảnh
		spec = mainTabHost.newTabSpec("tab2");
		spec.setContent(R.id.tab2);
		spec.setIndicator("Albums");
		mainTabHost.addTab(spec);
		
		// Tạo cấu hình cho tab3 : tab chứa toàn bộ ảnh yêu thích
		spec = mainTabHost.newTabSpec("tab3");
		spec.setContent(R.id.tab3);
		spec.setIndicator("Favourite");
		mainTabHost.addTab(spec);
		
		//Thiết lập tab mặc định được chọn ban đầu là tab 0
		//mainTabHost.setCurrentTab(0);
	}
	 
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		getMenuInflater().inflate(R.menu.asas, menu);
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
