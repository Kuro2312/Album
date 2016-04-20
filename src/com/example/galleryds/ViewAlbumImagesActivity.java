package com.example.galleryds;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
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

	GridView _gridViewAlbumImages;
    private ImageAdapter _albumImagesAdapter;
    private ImageAdapter _favouriteAdapter;
    private HashMap<String, String> _favouriteMap;
    
    private boolean _inSelectionMode;
    private int _contextPosition;
    
    // Các thành phần trên toolbar
    private LinearLayout llSelect;
    private ImageButton btnSelect;
    private TextView tvSelect;
    
    private View vwFavourite;
    private LinearLayout llFavourite;
    private ImageButton btnFavourite;
    private TextView tvFavourite;
    
    private View vwDelete;
    private LinearLayout llDelete;
    private ImageButton btnDelete;
    private TextView tvDelete;
    
    private View vwRemove;
    private LinearLayout llRemove;
    private ImageButton btnRemove;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_album_images);
		
		_gridViewAlbumImages = (GridView) findViewById(R.id.gridView4);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
	    this.setTitle("Album: " + bundle.getString("AlbumName"));
		_albumImagesAdapter = MainActivity._albumImagesAdapter;
		_gridViewAlbumImages.setAdapter(_albumImagesAdapter);
		
		//_favouriteAdapter = MainActivity._favouriteAdapter;
		//_favouriteMap = MainActivity._favouriteMap;
		
		registerForContextMenu(_gridViewAlbumImages);
		
		/*_inSelectionMode = false;
		
		llSelect = (LinearLayout) findViewById(R.id.llSelect);
        tvSelect = (TextView) findViewById(R.id.tvSelect);
        btnSelect = (ImageButton) findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (_inSelectionMode) {
                    MainActivity.turnOffSelectionMode(_gridViewAlbumImages, _albumImagesAdapter);
                    _inSelectionMode = false;
                } else {
                    MainActivity.turnOnSelectionMode(_gridViewAlbumImages, _albumImagesAdapter);
                    _inSelectionMode = true;
                }

                populateToolbar();
            }
        });

        vwFavourite = (View) findViewById(R.id.vwFavourite);
        llFavourite = (LinearLayout) findViewById(R.id.llFavourite);
        tvFavourite = (TextView) findViewById(R.id.tvFavourite);
        btnFavourite = (ImageButton) findViewById(R.id.btnFavourite);
        btnFavourite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    addToFavourite();
            }
        });

        vwDelete = (View) findViewById(R.id.vwDelete);
        llDelete = (LinearLayout) findViewById(R.id.llDelete);
        tvDelete = (TextView) findViewById(R.id.tvDelete);
        btnDelete = (ImageButton) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    MainActivity.deleteSelectedImages(_gridViewAlbumImages, _albumImagesAdapter);
            }
        });

        vwRemove = (View) findViewById(R.id.vwAdd);
        llRemove = (LinearLayout) findViewById(R.id.llAdd);
        btnRemove = (ImageButton) findViewById(R.id.btnAdd);
        btnRemove.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	removeFromAlbum();
            }
        });*/
		
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
    public boolean onContextItemSelected(MenuItem item) {
        ViewHolder holder = (ViewHolder) ImageSupporter.getViewByPosition(_contextPosition, _gridViewAlbumImages).getTag();

        if (item.getTitle().equals("Delete"))
            _albumImagesAdapter.remove(holder.id);
        
        if (item.getTitle().equals("Add to Favourite")) {
            ArrayList<String> newFavourite = new ArrayList<String>();
            DataHolder data = (DataHolder) _albumImagesAdapter.getItem(holder.id);

            if (_favouriteMap.containsKey(data._file.getAbsolutePath()) == false) {
                newFavourite.add(data._file.getAbsolutePath());
                _favouriteAdapter.add(data);
                _favouriteMap.put(data._file.getAbsolutePath(), data._file.getAbsolutePath());
            }

            ImageSupporter.addNewFavouriteImagePaths(this, newFavourite);
        }
        
        if (item.getTitle().equals("Remove from Album")) {
        	
            DataHolder data = (DataHolder) _albumImagesAdapter.getItem(holder.id);
      	
            // Xóa khỏi adapter + cập nhật giao diện
            _albumImagesAdapter.remove(data);
            
            // Di chuyển file
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            ImageSupporter.moveFile(data._file.getParent(), data._file.getName(), path.getAbsolutePath());
        }
            
        return true;
    } 
	 
	 public void addToFavourite() {
	        int count = _albumImagesAdapter.getCount();
	        ArrayList<String> newFavourite = new ArrayList<String>();

	        for (int i = count - 1; i >= 0; i--) {
	            View view = ImageSupporter.getViewByPosition(i, _gridViewAlbumImages);

	            ViewHolder holder = (ViewHolder) view.getTag();

	            if (holder.checkbox.isChecked() == true) {
	                holder.checkbox.setChecked(false);

	                DataHolder data = (DataHolder) _albumImagesAdapter.getItem(holder.id);

	                if (_favouriteMap.containsKey(data._file.getAbsolutePath()) == false) {
	                    newFavourite.add(data._file.getAbsolutePath());
	                    _favouriteAdapter.add(data);
	                    _favouriteMap.put(data._file.getAbsolutePath(), data._file.getAbsolutePath());
	                }

	                holder.checkbox.setChecked(true);
	            }
	        }

	        ImageSupporter.addNewFavouriteImagePaths(this, newFavourite);
	    }
	
	// Tạo nút cho toolbar. Phải thay đổi giá trị _inSelectionMode trước khi gọi
    private void populateToolbar() {
        if (_inSelectionMode) {
        	
            btnSelect.setImageResource(R.drawable.deselect);
            tvSelect.setText("Deselect");

            vwFavourite.setVisibility(View.VISIBLE);
            llFavourite.setVisibility(View.VISIBLE);
            btnFavourite.setImageResource(R.drawable.favourite);
            tvFavourite.setText("Favourite");

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
	
    public void removeFromAlbum()
    {
        int count = _albumImagesAdapter.getCount();

        for (int i = count - 1; i >= 0; i--) {
            View view = ImageSupporter.getViewByPosition(i, _gridViewAlbumImages);

            ViewHolder holder = (ViewHolder) view.getTag();

            if (holder.checkbox.isChecked() == true) {
                DataHolder data = (DataHolder) _albumImagesAdapter.getItem(holder.id);
            	
                // Xóa khỏi adapter + cập nhật giao diện
                _albumImagesAdapter.remove(data);
                
                // Di chuyển file
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                ImageSupporter.moveFile(data._file.getParent(), data._file.getName(), path.getAbsolutePath());
            }
        }
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
