package com.example.galleryds;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ViewAlbumImagesActivity extends Activity {

	protected AlbumManager _albumManager;
    protected ImageManager _imageManager;
    protected String _albumName;
    
    protected GridView _gridViewAlbumImages;
    protected ImageAdapter _albumImagesAdapter;
    
    
    private boolean _inSelectionMode;
    private int _contextPosition;
    
    // Các thành phần trên toolbar
    private LinearLayout llSelect;
    private ImageButton btnSelect;
    private TextView tvSelect;
    
    private View vwFavourite;
    private LinearLayout llFavourite;
    private ImageButton btnFavourite;
    
    private View vwDelete;
    private LinearLayout llDelete;
    private ImageButton btnDelete;
    
    private View vwRemove;
    private LinearLayout llRemove;
    private ImageButton btnRemove;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_album_images);
		
		_gridViewAlbumImages = (GridView) findViewById(R.id.gridView4);
		
		// Lấy dữ liệu đóng góp
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
		// Khởi tạo tên album
		_albumName = bundle.getString("AlbumName");
	    this.setTitle("Album: " + _albumName);
	    
	    // Lấy đối tượng quảnl ý
	    _albumManager = AlbumManager.getInstance();
	    _imageManager = ImageManager.getInstance();
	    _albumImagesAdapter = _albumManager.getSelectedAlbumAdapter(_albumName);
	    
		_gridViewAlbumImages.setAdapter(_albumImagesAdapter);
		
		registerForContextMenu(_gridViewAlbumImages);
	}
	
	 @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_image_album, menu);
        menu.setHeaderTitle("Choose an action");

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        _contextPosition = info.position;
    }
	 
	 @Override
    public boolean onContextItemSelected(MenuItem item) 
	 {
        ViewHolder holder = (ViewHolder) ImageSupporter.getViewByPosition(_contextPosition, _gridViewAlbumImages).getTag();
        DataHolder data = (DataHolder) _albumImagesAdapter.getItem(holder.id);
        
        if (item.getTitle().equals("Delete"))
        {
        	// Xóa khỏi adapter + cập nhật giao diện
            _albumImagesAdapter.remove(holder.id);
            _imageManager.deleteSelectedImage(data);
        }      
        else if (item.getTitle().equals("Add to Favourite"))      
            _imageManager.addImageToFavourite(data);      
        else if (item.getTitle().equals("Remove from Album")) 
        {   	
            // Xóa khỏi adapter + cập nhật giao diện
        	_albumImagesAdapter.remove(holder.id);
            _albumManager.removeImageFromAlbum(data, _albumName);
        }
            
        return true;
    } 
	 
	 public void addToFavourite() 
	 {
	        int count = _albumImagesAdapter.getCount();

	        for (int i = count - 1; i >= 0; i--) {
	        	
	            View view = ImageSupporter.getViewByPosition(i, _gridViewAlbumImages);
	            ViewHolder holder = (ViewHolder) view.getTag();

	            if (holder.checkbox.isChecked() == true) {
	                holder.checkbox.setChecked(false);

	                _imageManager.addImageToFavourite((DataHolder) _albumImagesAdapter.getItem(holder.id));

	                holder.checkbox.setChecked(true);
	            }
	        }
	    }

	 public void removeSelectedImagesFromAlbum()
     {
         int count = _albumImagesAdapter.getCount();

         for (int i = count - 1; i >= 0; i--) {
             View view = ImageSupporter.getViewByPosition(i, _gridViewAlbumImages);
 
             ViewHolder holder = (ViewHolder) view.getTag();

             if (holder.checkbox.isChecked() == true) 
             {
                 // Xóa khỏi adapter + cập nhật giao diện
                 _albumImagesAdapter.remove(holder.id);
                 _albumManager.removeImageFromAlbum((DataHolder) _albumImagesAdapter.getItem(holder.id), _albumName);
             }
         }
     }
	   
	 public void deleteSelectedImages()
	 {		 
		 int count = _albumImagesAdapter.getCount();

		 for (int i = count - 1; i >= 0; i--) 
		 {
			 View view = ImageSupporter.getViewByPosition(i, _gridViewAlbumImages);

			 ViewHolder holder = (ViewHolder) view.getTag();

			 if (holder.checkbox.isChecked() == true)
			 {
				 DataHolder data = (DataHolder) _albumImagesAdapter.getItem(holder.id);
				 _albumImagesAdapter.remove(holder.id);
            	
				 _imageManager.deleteSelectedImage(data);
			 }
		 }
	 }
	 
	 // Tạo nút cho toolbar. Phải thay đổi giá trị _inSelectionMode trước khi gọi
	 public void populateToolbar() {
        if (_inSelectionMode) {
        	
            btnSelect.setImageResource(R.drawable.deselect);
            tvSelect.setText("Deselect");

            vwFavourite.setVisibility(View.VISIBLE);
            llFavourite.setVisibility(View.VISIBLE);

            vwDelete.setVisibility(View.VISIBLE);
            llDelete.setVisibility(View.VISIBLE);

            vwRemove.setVisibility(View.VISIBLE);
            llRemove.setVisibility(View.VISIBLE);   
            
        } else { // not in slection mode

            btnSelect.setImageResource(R.drawable.select);
            tvSelect.setText("Select");

            vwFavourite.setVisibility(View.GONE);
            llFavourite.setVisibility(View.GONE);

            vwDelete.setVisibility(View.GONE);
            llDelete.setVisibility(View.GONE);

            vwRemove.setVisibility(View.GONE);
            llRemove.setVisibility(View.GONE);

        }
    }
	   	   
	 public void initializeSystem()
	 {
		 _inSelectionMode = false;
			
		llSelect = (LinearLayout) findViewById(R.id.llSelect2);
        tvSelect = (TextView) findViewById(R.id.tvSelect2);
        btnSelect = (ImageButton) findViewById(R.id.btnSelect2);
        
        vwFavourite = (View) findViewById(R.id.vwFavourite2);
        llFavourite = (LinearLayout) findViewById(R.id.llFavourite2);
        btnFavourite = (ImageButton) findViewById(R.id.btnFavourite2);
        
        vwDelete = (View) findViewById(R.id.vwDelete2);
        llDelete = (LinearLayout) findViewById(R.id.llDelete2);
        btnDelete = (ImageButton) findViewById(R.id.btnDelete2);
        
        vwRemove = (View) findViewById(R.id.vwRemove2);
        llRemove = (LinearLayout) findViewById(R.id.llRemove2);
        btnRemove = (ImageButton) findViewById(R.id.btnRemove2);
        

        btnSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (_inSelectionMode)
                    ImageSupporter.turnOffSelectionMode(_gridViewAlbumImages, _albumImagesAdapter);
                else
                	ImageSupporter.turnOnSelectionMode(_gridViewAlbumImages, _albumImagesAdapter);
                
                _inSelectionMode = !_inSelectionMode;
                populateToolbar();
            }
        });

        
        btnFavourite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addToFavourite();
            }
        });

        
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	deleteSelectedImages();
            }
        });


        btnRemove.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	removeSelectedImagesFromAlbum();
            }
        });	
	 }
	 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
		 // Inflate the menu; this adds items to the action bar if it is present.
		 getMenuInflater().inflate(R.menu.view_album_images, menu);
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
