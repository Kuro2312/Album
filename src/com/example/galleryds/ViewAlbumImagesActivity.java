package com.example.galleryds;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

public class ViewAlbumImagesActivity extends Activity {

	GridView _gridViewAlbumImages;
    private ImageAdapter _albumImagesAdpater;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_album_images);
		
		_gridViewAlbumImages = (GridView) findViewById(R.id.gridView4);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
	    this.setTitle("Album: " + bundle.getString("AlbumName"));
		_albumImagesAdpater = MainActivity._albumImagesAdapter;
		_gridViewAlbumImages.setAdapter(_albumImagesAdpater);
	}
	
    public void removeFromAlbum()
    {
        int count = _albumImagesAdpater.getCount();

        for (int i = count - 1; i >= 0; i--) {
            View view = MainActivity.getViewByPosition(i, _gridViewAlbumImages);

            ViewHolder holder = (ViewHolder) view.getTag();

            if (holder.checkbox.isChecked() == true) {
                DataHolder data = (DataHolder) _albumImagesAdpater.getItem(holder.id);
            	
                // Xóa khỏi adapter + cập nhật giao diện
                _albumImagesAdpater.remove(data);
                
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
