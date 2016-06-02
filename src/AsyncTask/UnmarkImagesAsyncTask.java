package AsyncTask;

import java.util.ArrayList;

import BusinessLayer.ImageSupporter;
import BusinessLayer.MarkManager;
import android.os.AsyncTask;

public class UnmarkImagesAsyncTask extends AsyncTask<Object, Void, Boolean> 
{

	@Override
	protected Boolean doInBackground(Object... params) 
	{
		try
		{
			ArrayList<String> imagePaths = (ArrayList<String>) params[0];
			MarkManager markManager = (MarkManager) params[1];
			
			/*int n = imagePaths.size();
			
			// Thực hiện đánh dấu từng ảnh trong danh sách được chọn
			for (int i = 0; i < n; i++)
				if (markManager.isMarkImage(imagePaths.get(i)))
					markManager.marksImage(imagePaths.get(i));*/
			
			markManager.unmarksImages(imagePaths);
					
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
