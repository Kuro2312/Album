package com.example.galleryds;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ExitListenerService extends Service {

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		
		ImageSupporter.clearData();
		// TODO Auto-generated method stub
		super.onTaskRemoved(rootIntent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
