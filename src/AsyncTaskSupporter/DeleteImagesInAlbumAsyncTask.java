package AsyncTaskSupporter;

import java.util.ArrayList;

import Adapter.ImageAdapter;
import BusinessLayer.AlbumManager;
import BusinessLayer.ImageSupporter;
import BusinessLayer.MarkManager;
import android.app.Dialog;
import android.os.AsyncTask;
import android.widget.Toast;

public class DeleteImagesInAlbumAsyncTask extends AsyncTask<Object, Void, Boolean> 
{
	private Dialog _dialog;
	private ImageAdapter _imageAdapter;
	private ArrayList<String> _imagePaths;
	
	public DeleteImagesInAlbumAsyncTask(Dialog dialog, ImageAdapter imageAdapter)
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
			AlbumManager albumManager = (AlbumManager) params[0];
			MarkManager markManager = (MarkManager) params[1];
			_imagePaths = (ArrayList<String>) params[2];
			
			// B�? đánh dấu
			markManager.unmarksImages(_imagePaths);
			
			// Thực hiện xóa từng ảnh trong danh sách được ch�?n
			// Nếu có lỗi thì dừng ngay
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
		
		_imageAdapter.removeImages(_imagePaths);
		
		if (result == true)
			Toast.makeText(_dialog.getContext(), "Deleted Succesully", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(_dialog.getContext(), "Fail ! +_+ !", Toast.LENGTH_SHORT).show();
    }
}
