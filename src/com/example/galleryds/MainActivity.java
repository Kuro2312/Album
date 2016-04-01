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
import android.provider.MediaStore.Files;
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
	
	// Cho việc thao tác trên mục toàn bô ảnh
	GridView _gridViewAll;
	ArrayList<DataHolder> _allImageData;
	private ImageAdapter _allImageAdapter;
	
	// Cho việc thao tác trên mục album ảnh
	GridView _gridViewAlbum;
	ArrayList<DataHolder> _albumData;
	private AlbumAdapter _albumAdapter;
	
	// Cho việc thao tác trên mục yêu thích
	GridView _gridViewFavourite;
	ArrayList<DataHolder> _favouriteData;
	private ImageAdapter _favouriteAdapter;
		
	HashMap<String, DataHolder> _allMap;
	HashMap<String, String> _favouriteMap;
		
	//Toast.makeText(this, "Successfully", Toast.LENGTH_SHORT).show();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		_gridViewAll = (GridView) findViewById(R.id.gridView1);
		_gridViewFavourite = (GridView) findViewById(R.id.gridView2);
		_gridViewAlbum = (GridView) findViewById(R.id.gridView3);
		//_gridViewAlbumImages = (GridView) findViewById(R.id.gridView4);
		
		loadTabs();
		loadImages();
		
		ImageButton b = (ImageButton) findViewById(R.id.btnNote);
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				turnOnSelectionMode(_gridViewAll, _allImageAdapter);
			}
		});
		
		ImageButton b1 = (ImageButton) findViewById(R.id.btnStar);
		b1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addToFavourite(_gridViewAll, _allImageAdapter);				
			}
		});
		
		ImageButton b2 = (ImageButton) findViewById(R.id.btnTrash);
		b2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//deleteSelectedImages(_gridViewAll, _allImageAdapter);
				removeFromFavourite(_gridViewAll, _allImageAdapter);
			}
		});
		
		ImageButton b3 = (ImageButton) findViewById(R.id.btnDeNote);
		b3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				turnOffSelectionMode(_gridViewAll, _allImageAdapter);				
			}
		});
		
		/* Khu vực để test: hô hô hô ^_^
		
		createAlbum("Kuro");
		deleteAlbum("Kuro");
		
		code test file in private mode
		ArrayList<String> arr = ImageSupporter.foo(this);
		
		for (int i = 0; i < arr.size(); i++)
			Toast.makeText(this, arr.get(i), Toast.LENGTH_SHORT).show();
			
		code to reset favourite
		ImageSupporter.saveFavouriteImagePaths(this, null);
			
		ArrayList<String> arr = ImageSupporter.getFavouriteImagePaths(this);
		
		for (int i = 0; i < arr.size(); i++)
			Toast.makeText(this, arr.get(i), Toast.LENGTH_SHORT).show(); */	
		
		loadFavouriteImages();
		loadAlbums();
	}

	void foo()
	{
		HashMap<String, String> a = new HashMap<String, String>();
		
		String c = "kutorot";
		a.put("Kuro", "1");
		a.put("Shiro", "2");
		a.put("d", c);
		
		ArrayList<String> b = new ArrayList<String>();
		//b.set(1, "3");
		//b.set(0, "4");
		b.add(c);
		
		b.remove(0);
		
		//b.remove(a.get("Kuro"));
		
		Toast.makeText(this, a.get("d"), Toast.LENGTH_SHORT).show();		
	}
	
	// Thêm vào favourite
	// Ghi thêm vào file
	public void addToFavourite(GridView gridview, ImageAdapter adapter)
	{
		int count = adapter.getCount();
		ArrayList<String> newFavourite = new ArrayList<String>();
		
		for (int i = count - 1; i >= 0; i--)
		{		
			View view = getViewByPosition(i, gridview);

			ViewHolder holder = (ViewHolder) view.getTag();
			
			if (holder.checkbox.isChecked() == true)
			{				
				DataHolder data = (DataHolder) adapter.getItem(holder.id);
				
				if (_favouriteMap.containsKey(data._file.getAbsolutePath()) == false)
				{
					newFavourite.add(data._file.getAbsolutePath());
					_favouriteAdapter.add(data);
					_favouriteMap.put(data._file.getAbsolutePath(), data._file.getAbsolutePath());
				}
			}
		}
		
		ImageSupporter.addNewFavouriteImagePaths(this, newFavourite);
	}
	
	// Xóa khỏi favourite
	// Luu lai vào file
	public void removeFromFavourite(GridView gridview, ImageAdapter adapter)
	{
		int count = adapter.getCount();

		for (int i = count - 1; i >= 0; i--)
		{		
			View view = getViewByPosition(i, gridview);

			ViewHolder holder = (ViewHolder) view.getTag();
			
			if (holder.checkbox.isChecked() == true)
			{				
				DataHolder data = (DataHolder) adapter.getItem(holder.id);
				
				if (_favouriteMap.containsKey(data._file.getAbsolutePath()) == true)
				{
					_favouriteAdapter.remove(data);
					_favouriteMap.remove(data._file.getAbsolutePath());
				}
			}
		}
		
		ImageSupporter.saveFavouriteImagePaths(this, new ArrayList<String>(_favouriteMap.values()));		
	}
		
	// Nạp toàn bộ ảnh trong máy lên
	public void loadImages()
	{		
		File imageDir = new File(Environment.getExternalStorageDirectory().toString());
		
		if (imageDir.exists())
	    {			
			_allImageData = new ArrayList<DataHolder>();
			_allMap = new HashMap<String, DataHolder>();
			
			dirFolder(imageDir);
			
			_allImageAdapter = new ImageAdapter(this, _allImageData);
			_gridViewAll.setAdapter(_allImageAdapter);
	    }
	}
	
	// Nạp các ảnh ưa thích lên
	public void loadFavouriteImages()
	{		
		ArrayList<String> data = ImageSupporter.getFavouriteImagePaths(this);	
		
		if (data != null)
		{
			_favouriteData = new ArrayList<DataHolder>();
			_favouriteMap = new HashMap<String, String>();
			
			for (int i = 0; i < data.size(); i++)
			{
				//File f = new File(data.get(i));
				//Bitmap b =  ImageSupporter.decodeSampledBitmapFromFile(f, 100, 100);
				
				//DataHolder tmpData = new DataHolder(f,b);
				
				//_favouriteData.add(tmpData);
				//_favourite.put(f.getAbsolutePath(), tmpData);
				
				DataHolder d = _allMap.get(data.get(i));
				
				if (d != null)
				{
					_favouriteData.add(d);		
					_favouriteMap.put(d._file.getAbsolutePath(), d._file.getAbsolutePath());
				}
			}
			//new ArrayList<DataHolder>(_favourite.values()
			_favouriteAdapter = new ImageAdapter(this, _favouriteData);
			_gridViewFavourite.setAdapter(_favouriteAdapter);
		}
	}
	
	//ArrayList<String> data;
	// Nạp các album lên
	public void loadAlbums()
	{		
		ArrayList<String> data = ImageSupporter.getAlbumPaths(this);	
		
		data = new ArrayList<String>();
		data.add("Kuro");
		data.add("Kuro1");
		data.add("Kuro2");
		
		if (data != null)
		{
			_albumAdapter = new AlbumAdapter(this, data);
			_gridViewAlbum.setAdapter(_albumAdapter);
		}
	}
		
	// Nạp các tab cần thể hiện cho tabhost
	public void loadTabs()
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
	 
	// Tạo mới Album
	public void createAlbum(String name)
	{
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

		// Kiểm tra có tạo folder thành công không
		if (ImageSupporter.createFolder(path.getAbsolutePath(), name)) 
		{
			// Cập nhật giao diện
			Toast.makeText(this, "Create Album Successfully", Toast.LENGTH_SHORT).show();
		} 
		else 
		{
			// Xử lý lỗi
			Toast.makeText(this, "Fail To Create Album", Toast.LENGTH_SHORT).show();
		}
	}
	
	// Đổi tên 1 Album
	public void renameAlbum(File album, String newName)
	{
		// Xử lý giao diện gì đó đó
		ImageSupporter.renameFile(album, newName);
	}
	
	// Xóa toàn bộ ảnh trong album
	public void deleteWholeAlbum(String name)
	{
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		
		ImageSupporter.deleteWholeFolder(path.getAbsolutePath(), name);
		
		// Cập nhật giao diện
	}
		
	// Chỉ xóa album và chuyển ảnh sang chỗ khác
	public void deleteAlbum(String name)
	{
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File folder = new File(path.getAbsolutePath() + File.separator + name);

		// Kiểm tra có tồn tại folder đó không
		if (folder.exists()) 
		{
			File[] files = folder.listFiles();
			
		    if (files != null)	    
		    	for (File f : files) 
		    		ImageSupporter.moveFile(f.getParent(), f.getName(), path.getAbsolutePath());
		    	   
	        folder.delete();
	        
	        Toast.makeText(this, "Successfully", Toast.LENGTH_SHORT).show();
		}
	}
	
	// Duyệt và tìm kiếm tất cả tập tin hình ảnh
	public void dirFolder(File file)
	{
		if (file.getName().equals(".thumbnails"))
			return;
		
		File[] files = file.listFiles();
		
		if (files == null)
			return;
		
		for (File f : files)
		{			
			if (ImageSupporter.isImage(f))
            {			
				Bitmap b =  ImageSupporter.decodeSampledBitmapFromFile(f, 100, 100);

				_allImageData.add(new DataHolder(f,b));
				_allMap.put(f.getAbsolutePath(), _allImageData.get(_allImageData.size() - 1));
            }
            
            if (f.isDirectory())
            	dirFolder(f);
		}
		
	}
	
	// Bật chế độ chọn hình ảnh
	public void turnOnSelectionMode(GridView gridView, ImageAdapter adapter)
	{
		int count = adapter.getCount();
		
		for (int i = 0; i < count; i++)
		{		
			View view = getViewByPosition(i, gridView);

			ViewHolder holder = (ViewHolder) view.getTag();
			
			holder.checkbox.setVisibility(View.VISIBLE);
			holder.checkbox.setChecked(false);
		}
	}

	// Xóa ảnh đã chọn
	public void deleteSelectedImages(GridView gridView, ImageAdapter adapter)
	{
		int count = adapter.getCount();
		
		for (int i = count - 1; i >= 0; i--)
		{		
			View view = getViewByPosition(i, gridView);

			ViewHolder holder = (ViewHolder) view.getTag();
			
			if (holder.checkbox.isChecked() == true)
			{				
				adapter.remove(holder.id);			
			}
		}
	}
	
	// Tắt chế độ chọn hình ảnh
	public void turnOffSelectionMode(GridView gridView, ImageAdapter adapter)
	{
		int count = adapter.getCount();
		
		for (int i = 0; i < count; i++)
		{		
			View view = getViewByPosition(i, gridView);

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
