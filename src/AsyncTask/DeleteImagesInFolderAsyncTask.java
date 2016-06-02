package AsyncTask;

import java.util.ArrayList;

import BusinessLayer.FolderManager;
import BusinessLayer.ImageSupporter;
import android.os.AsyncTask;

public class DeleteImagesInFolderAsyncTask extends AsyncTask<Object, Void, Boolean> 
{

	@Override
	protected Boolean doInBackground(Object... params) 
	{
		try
		{
			ArrayList<String> imagePaths = (ArrayList<String>) params[0];
			FolderManager folderManager = (FolderManager) params[1];
			
			int n = imagePaths.size();
			
			// Thực hiện xóa từng ảnh trong danh sách được chọn
			// Nếu có lỗi thì dừng ngay
			for (int i = 0; i < n; i++)
				if (!folderManager.deletesImage(imagePaths.get(i)))
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
		// Xử lý kết quả trả về
    }
}
