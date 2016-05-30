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
import android.widget.Toast;

public class MainActivity extends Activity {

	private ViewGroup _scrollViewgroup;
	private ResourceManager _resource;
	private HorizontalScrollView _scrollView;
	private ListView _listViewFolder;
	private FolderAdapter _folderAdapter;
	private ArrayList<String> _data;
	
	private View btnDeleteImage;
	private View btnDeleteAlbum;
	
	private View btnMarkImage;
	private View btnUnmarkImage;
	
	private View btnLockImage;
	private View btnUnlockImage;
	
	private View btnAddToAlbum;
	private View btnRemoveFromAlbum;
	
	private View btnShareImage;
	private View btnCompressImage;
	
	private View btnEditImage;
	
	private View btnConvertToVideo;
	
	
		
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
		initializeAllFunctionButtons();
		_scrollView.setVisibility(View.GONE);
	}
	
	public void initializeAllFunctionButtons()
	{
		int id = 0;
		
		// Delete Image
		setUpFunctionButton(_scrollViewgroup, id++, R.drawable.delete_image, "Delete Image", onClickDelete);
		
		// Delete Album
		//setUpFunctionButton(_scrollViewgroup, id++, R.drawable.delete_album, "Delete Album", onClickDelete);
		
		// Mark Image
		setUpFunctionButton(_scrollViewgroup, id++, R.drawable.mark_image, "Mark Image", onClickDelete);
		
		// Unmark Image
		//setUpFunctionButton(_scrollViewgroup, id++, R.drawable.unmark_image, "Unmark Image", onClickDelete);
		
		// Lock Image
		setUpFunctionButton(_scrollViewgroup, id++, R.drawable.lock_image, "Lock Image", onClickDelete);
		
		// Unlock Image
		//setUpFunctionButton(_scrollViewgroup, id++, R.drawable.unlock_image, "Unlock Image", onClickDelete);
		
		// Add To Album
		setUpFunctionButton(_scrollViewgroup, id++, R.drawable.add_image_album, "Add To Album", onClickDelete);
		
		// Remove From Album
		//setUpFunctionButton(_scrollViewgroup, id++, R.drawable.remove_image_album, "Remove", onClickDelete);
		
		// Share
		setUpFunctionButton(_scrollViewgroup, id++, R.drawable.share, "Share", onClickDelete);
		
		// Zip
		setUpFunctionButton(_scrollViewgroup, id++, R.drawable.compress, "Compress", onClickDelete);
		
		// Convert To Video
		setUpFunctionButton(_scrollViewgroup, id++, R.drawable.convert_video, "Convert", onClickDelete);
	}
	
	public void setUpFunctionButton(ViewGroup parent, int id, int resourceID, String name, View.OnClickListener onClickEvent)
	{
		// Khởi tạo với layout cho trước
		View btn = getLayoutInflater().inflate(R.layout.function_item, null);	
		
		// Gán ID
		btn.setId(id);
		
		// Lấy thành phần con trong View
		ImageView icon = (ImageView) btn.findViewById(R.id.imageViewFunction);
		TextView content = (TextView) btn.findViewById(R.id.textViewFunction);
		
		// Gán ảnh icon cho nút bấm
		icon.setImageBitmap(ResourceManager.getImageFunctionIcon(this.getResources(), resourceID));
		
		// Gán tên chức năng cho nút bấm
		content.setText(name);
		
		btn.setTag(name);
		
		// Gán sự kiện click với chức năng
		btn.setOnClickListener(onClickEvent);
		
		parent.addView(btn);
	}
	
	View.OnClickListener onClickDelete = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Toast.makeText(MainActivity.this, String.valueOf(v.getTag()), Toast.LENGTH_SHORT).show();			
		}
	};
	
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
