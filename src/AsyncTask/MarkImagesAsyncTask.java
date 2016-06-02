package AsyncTask;

import java.util.ArrayList;

import BusinessLayer.ImageSupporter;
import BusinessLayer.MarkManager;
import android.os.AsyncTask;

public class MarkImagesAsyncTask extends AsyncTask<Object, Void, Boolean> 
{

	@Override
	protected Boolean doInBackground(Object... params) 
	{
		try
		{
			ArrayList<String> imagePaths = (ArrayList<String>) params[0];
			MarkManager markManager = (MarkManager) params[1];
			
			markManager.marksImages(imagePaths);
			
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
