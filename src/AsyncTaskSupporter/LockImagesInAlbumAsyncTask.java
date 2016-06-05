package AsyncTaskSupporter;

import java.util.ArrayList;

import com.example.neogalleryds.MainActivity;

import Adapter.ImageAdapter;
import BusinessLayer.AlbumManager;
import BusinessLayer.FolderManager;
import BusinessLayer.ImageSupporter;
import BusinessLayer.LockManager;
import BusinessLayer.MarkManager;
import android.app.Dialog;
import android.os.AsyncTask;
import android.widget.Toast;

public class LockImagesInAlbumAsyncTask extends AsyncTask<Object, Void, Boolean> 
{
	private Dialog _dialog;
	private ImageAdapter _imageAdapter;
	private ArrayList<String> _imagePaths;
	
	public LockImagesInAlbumAsyncTask(Dialog dialog, ImageAdapter imageAdapter)
	{
		_dialog = dialog;
		_imageAdapter = imageAdapter;
	}
	
	protected void onPreExecute()
	{
		if (_dialog != null)
			_dialog.show();
    } 
	
	@Override
	protected Boolean doInBackground(Object... params) 
	{
		try
		{		
			LockManager lockManager = (LockManager) params[0];
			AlbumManager albumManager = (AlbumManager) params[1];
			MarkManager markManager = (MarkManager) params[2];
			_imagePaths = (ArrayList<String>) params[3];

			int n = _imagePaths.size();
			
			// B�? đánh dấu
			markManager.unmarksImages(_imagePaths);
			
			// Khóa	ảnh				
			if (!lockManager.locksImages(_imagePaths))
				return false;
			
			// Xóa ảnh
			return albumManager.deletesImages(_imagePaths);
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	protected void onPostExecute(Boolean result) 
    {
		// Xử lý kết quả trả v�?
		if (_dialog != null)
			_dialog.dismiss();
		
		MainActivity.cancelLoadImage = false;
		_imageAdapter.removeImages(_imagePaths);
		
		if (result == true)
			Toast.makeText(_dialog.getContext(), "Locked Succesully", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(_dialog.getContext(), "Fail ! +_+ !", Toast.LENGTH_SHORT).show();
    }
}
