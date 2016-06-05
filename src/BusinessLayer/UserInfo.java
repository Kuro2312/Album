package BusinessLayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class UserInfo 
{
	public static void savesUserInfo(Context context, String email, String pass)
	{
		SharedPreferences sharedPref =  context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
		
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("Email", email);
		editor.putString("Pass", pass);
		
		editor.apply();
		
		Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
	}
	
	public static boolean checksUserInfo(Context context, String email, String pass)
	{
		SharedPreferences sharedPref =  context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
		
		String userEmail = sharedPref.getString("Email", "");
		String userPass = sharedPref.getString("Pass", "");

		if (userEmail.equals(email) && userPass.equals(pass))
			return true;
		
		return false;
	}

	public static boolean isEmpty(Context context)
	{
		SharedPreferences sharedPref =  context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
		
		String userEmail = sharedPref.getString("Email", "");
		String userPass = sharedPref.getString("Pass", "");

		if (userEmail.equals("") || userPass.equals(""))
			return true;
		
		return false;
	}
	
	public static boolean checksPassword(Context context, String pass)
	{
		SharedPreferences sharedPref =  context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
		
		String userPass = sharedPref.getString("Pass", "");

		if (userPass.equals(pass))
			return true;
		
		return false;
	}
}
