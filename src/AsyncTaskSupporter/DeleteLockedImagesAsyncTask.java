package AsyncTaskSupporter;

import java.util.ArrayList;

import Adapter.ImageAdapter;
import BusinessLayer.ImageSupporter;
import BusinessLayer.LockManager;
import android.app.Dialog;
import android.os.AsyncTask;
import android.widget.Toast;

public class DeleteLockedImagesAsyncTask extends AsyncTask<Object, Void, Boolean> 
{
	private Dialog _dialog;
	private ImageAdapter _imageAdapter;
	private LockManager _lockManager;
	
	public DeleteLockedImagesAsyncTask(Dialog dialog, ImageAdapter imageAdapter)
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
			_lockManager = (LockManager) params[0];
			ArrayList<String> imagePaths = (ArrayList<String>) params[1];
			
			return _lockManager.deletesImages(imagePaths);
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
		
		// Cập nhật dữ liệu trên image Adapter
		_imageAdapter.updateData(_lockManager.getsLockedImages());
		
		if (result == true)
			Toast.makeText(_dialog.getContext(), "Deleted Succesully", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(_dialog.getContext(), "Fail ! +_+ !", Toast.LENGTH_SHORT).show();
    }
}
