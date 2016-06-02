package AsyncTask;

import BusinessLayer.ImageSupporter;
import android.os.AsyncTask;

public class DeleteAlbumAsyncTask extends AsyncTask<String, Void, Boolean> 
{

	@Override
	protected Boolean doInBackground(String... params) 
	{
		String inputPath = params[0];
		
		return ImageSupporter.deleteWholeFolder(inputPath);
	}
	
	protected void onPostExecute(Boolean result) 
    {
		
    }
}
