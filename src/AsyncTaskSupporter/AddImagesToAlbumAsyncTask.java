package AsyncTaskSupporter;

import java.util.ArrayList;

import BusinessLayer.AlbumManager;
import BusinessLayer.ImageSupporter;
import android.app.Dialog;
import android.os.AsyncTask;
import android.widget.Toast;

public class AddImagesToAlbumAsyncTask extends AsyncTask<Object, Void, Boolean> 
{
	private Dialog _dialog;
	
	public AddImagesToAlbumAsyncTask(Dialog dialog)
	{
		_dialog = dialog;
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
			String albumName = (String) params[1]; 
			ArrayList<String> imagePaths = (ArrayList<String>) params[2];
			
			int n = imagePaths.size();
			
			// Thực hiện xóa từng ảnh trong danh sách được ch�?n
			// Nếu có lỗi thì dừng ngay
			for (int i = 0; i < n; i++)
				albumManager.addsImageToAlbum(albumName, imagePaths.get(i));
								
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
		
		if (result == true)
			Toast.makeText(_dialog.getContext(), "Added Succesully", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(_dialog.getContext(), "Fail ! +_+ !", Toast.LENGTH_SHORT).show();
    }
}
