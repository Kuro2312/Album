package AsyncTask;

import java.util.ArrayList;

import Adapter.ImageAdapter;
import BusinessLayer.ImageSupporter;
import BusinessLayer.MarkManager;
import android.app.Dialog;
import android.os.AsyncTask;
import android.widget.Toast;

public class UnmarkImagesAsyncTask extends AsyncTask<Object, Void, Boolean> 
{
	private Dialog _dialog;
	private ImageAdapter _imageAdapter;
	private MarkManager _markManager;
	
	public UnmarkImagesAsyncTask(Dialog dialog, ImageAdapter imageAdapter)
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
			_markManager = (MarkManager) params[0];
			ArrayList<String> imagePaths = (ArrayList<String>) params[1];
					
			_markManager.unmarksImages(imagePaths);			
			
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
		if (_dialog != null)
			_dialog.dismiss();
		
		_imageAdapter.updateData(_markManager.getsMarkedImages());
		
		if (result == true)
			Toast.makeText(_dialog.getContext(), "Unmarked Succesully", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(_dialog.getContext(), "Fail ! +_+ !", Toast.LENGTH_SHORT).show();
    }
}

/*int n = imagePaths.size();

// Thực hiện đánh dấu từng ảnh trong danh sách được chọn
for (int i = 0; i < n; i++)
	if (markManager.isMarkImage(imagePaths.get(i)))
		markManager.marksImage(imagePaths.get(i));*/

