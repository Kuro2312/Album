package AsyncTask;

import BusinessLayer.AlbumManager;
import BusinessLayer.ImageSupporter;
import android.os.AsyncTask;

public class DeleteAlbumAsyncTask extends AsyncTask<Object, Void, Boolean> 
{

	@Override
	protected Boolean doInBackground(Object... params) 
	{
		String inputPath = (String) params[0];
		AlbumManager albumManager = (AlbumManager) params[1];
		
		return albumManager.deletesAlbum(inputPath);
	}
	
	protected void onPostExecute(Boolean result) 
    {
		
    }
}
