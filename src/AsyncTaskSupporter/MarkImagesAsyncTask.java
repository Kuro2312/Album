package AsyncTaskSupporter;

import java.util.ArrayList;

import com.example.neogalleryds.MainActivity;

import BusinessLayer.ImageSupporter;
import BusinessLayer.MarkManager;
import android.app.Dialog;
import android.os.AsyncTask;
import android.widget.Toast;

public class MarkImagesAsyncTask extends AsyncTask<Object, Void, Boolean> 
{
	private Dialog _dialog;
	
	public MarkImagesAsyncTask(Dialog dialog)
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
			MarkManager markManager = (MarkManager) params[0];
			ArrayList<String> imagePaths = (ArrayList<String>) params[1];
			
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
		// Xử lý kết quả trả v�?
		if (_dialog != null)
			_dialog.dismiss();
		
		MainActivity.cancelLoadImage = false;
		if (result == true)
			Toast.makeText(_dialog.getContext(), "Marked Succesully", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(_dialog.getContext(), "Fail ! +_+ !", Toast.LENGTH_SHORT).show();
    }
}
