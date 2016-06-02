package AsyncTask;

import java.util.ArrayList;

import BusinessLayer.AlbumManager;
import BusinessLayer.ImageSupporter;
import android.os.AsyncTask;

public class AddImagesToAlbumAsyncTask extends AsyncTask<Object, Void, Boolean> 
{

	@Override
	protected Boolean doInBackground(Object... params) 
	{
		try
		{
			ArrayList<String> imagePaths = (ArrayList<String>) params[0];
			AlbumManager albumManager = (AlbumManager) params[1];
			String albumName = (String) params[2]; 
			
			int n = imagePaths.size();
			
			// Thực hiện xóa từng ảnh trong danh sách được chọn
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
		// Xử lý kết quả trả về
    }
}
