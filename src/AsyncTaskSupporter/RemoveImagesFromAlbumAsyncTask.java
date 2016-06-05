package AsyncTaskSupporter;

import java.util.ArrayList;

import Adapter.ImageAdapter;
import BusinessLayer.AlbumManager;
import BusinessLayer.ImageSupporter;
import BusinessLayer.MarkManager;
import android.app.Dialog;
import android.os.AsyncTask;
import android.widget.Toast;

public class RemoveImagesFromAlbumAsyncTask extends AsyncTask<Object, Void, Boolean> 
{
	private Dialog _dialog;
	private ImageAdapter _imageAdapter;
	ArrayList<String> _imagePaths;
	
	public RemoveImagesFromAlbumAsyncTask(Dialog dialog, ImageAdapter imageAdapter)
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
			
			int n = _imagePaths.size();			
			
			markManager.unmarksImages(_imagePaths);
			
			// Thực hiện xóa từng ảnh trong danh sách được ch�?n
			// Nếu có lỗi thì dừng ngay
			for (int i = 0; i < n; i++)
				if (!albumManager.removesImageFromAlbum(_imagePaths.get(i)))
					return false;
								
			return true;
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
		_imageAdapter.removeImages(_imagePaths);
		
		if (result == true)
			Toast.makeText(_dialog.getContext(), "Removed Succesully", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(_dialog.getContext(), "Fail ! +_+ !", Toast.LENGTH_SHORT).show();
    }
}
