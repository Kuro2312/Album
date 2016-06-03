package AsyncTask;

import BusinessLayer.AlbumManager;
import BusinessLayer.ImageSupporter;
import android.app.Dialog;
import android.os.AsyncTask;

public class DeleteAlbumAsyncTask extends AsyncTask<Object, Void, Boolean> 
{

	private Dialog _dialog;
	
	public DeleteAlbumAsyncTask(Dialog dialog)
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
		String inputPath = (String) params[0];
		AlbumManager albumManager = (AlbumManager) params[1];
		
		return albumManager.deletesAlbum(inputPath);
	}
	
	protected void onPostExecute(Boolean result) 
    {
		// Xử lý kết quả trả về
		if (_dialog != null)
			_dialog.dismiss();
    }
}
