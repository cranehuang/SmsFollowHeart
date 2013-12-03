package followheart.receivers_and_services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LauncherReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context,Intent intent)
	{
		Intent tIntent = new Intent(context ,SMSService.class);
		tIntent.setAction("followheart.receivers_and_services.SMSService");
		context.startService(tIntent);
	}

}
