package AsyncTask;

import BusinessLayer.ImageSupporter;
import android.os.AsyncTask;

public class MoveFileAsyncTask extends AsyncTask<String, Void, Boolean> 
{

	@Override
	protected Boolean doInBackground(String... params) 
	{
		String inputPath = params[0];
		String inputName = params[1];
		String outputPath = params[2];
		String outputName = params[3];
		
		return ImageSupporter.moveFile(inputPath, inputName, outputPath, outputName);
	}
	
	protected void onPostExecute(Boolean result) 
    {
		//showDialog("df");
    }

}