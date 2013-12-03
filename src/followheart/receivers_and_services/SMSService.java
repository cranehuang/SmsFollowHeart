package followheart.receivers_and_services;

import com.todddavies.components.progressbar.R;

import followheart.activities.MainActivity;
import followheart.entities.SMSConstant;
import followheart.fragments.SmsConversationFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class SMSService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//Service创建时回调该方法
	@Override
	public void onCreate()
	{
		super.onCreate();
//		System.out.println("-------->service is Created!");
	}
	
	//Service被启动时回调该方法
	@Override
	public int onStartCommand(Intent intent,int flags, int startID)
	{
		System.out.println("service id Started!");
		this.getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, new SMSObserver(new Handler(),this));
		return START_STICKY;
	}
	
	//Service被关闭时调用方法
	public void onDestroy()
	{
		super.onDestroy();
//		System.out.println("service is Destroyed!");
	}
	
	public class SMSObserver extends ContentObserver
	{
        private Context context;
        public static final int NITIFICATION_ID = 0;
		public SMSObserver(Handler handler ,Context context) {
			super(handler);
			// TODO Auto-generated constructor stub
			this.context = context;
		}
		
		
		@SuppressWarnings("deprecation")
		public void onChange(boolean selfChange) {   
	        // TODO Auto-generated method stub   
	        super.onChange(selfChange); 
//	        System.out.println("-------------->notify");
	        Cursor cursor = getContentResolver().query(Uri.parse(SMSConstant.strUriInbox), new String[]{SMSConstant.READ}, null, null, "date DESC");
	        if (cursor != null && cursor.moveToFirst()) {
	        	if (cursor.getString(0).equals("0")) {	
	        		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context,MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);  
			        //notify SMSInbox & SMSSent
			        Notification notification = new NotificationCompat.Builder(context)
			        		.setSmallIcon(R.drawable.logo, 5).getNotification();
			        notification.setLatestEventInfo(context, "Message", "点击查看", contentIntent);
			        notification.vibrate = new long[]{0,50,100,150};
			        NotificationManager notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
			        notificationManager.notify(NITIFICATION_ID, notification);
				}
				
			}
	    }
	}

}
