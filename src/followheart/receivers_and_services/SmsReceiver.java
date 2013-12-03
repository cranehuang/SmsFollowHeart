package followheart.receivers_and_services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class SmsReceiver extends BroadcastReceiver{
	
	final int NOTIFICATION_ID = 0;
	private NotificationManager nm;  
    private Notification n;  
    public static final int ID = 1; 

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
//			abortBroadcast();
//			StringBuilder sBuilder = new StringBuilder();
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				
//				开启service监听短信数据库
				Intent tIntent = new Intent(context ,SMSService.class);
				tIntent.setAction("followheart.receivers_and_services.SMSServices");
				context.startService(tIntent);
//				String service = Context.NOTIFICATION_SERVICE;  
//		        nm = (NotificationManager) context.getSystemService(service);  
//		        n = new Notification();  
//		        //定义通知的一些属性  
//		        n.icon = R.drawable.ic_launcher;
//		        n.tickerText = "Message";  
//		        n.when = System.currentTimeMillis();  
//		        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context,SmsInbox.class), 0);  
//		        n.setLatestEventInfo(context, "Message", "具体的通知内容", contentIntent);      
//		        //利用通知管理器把封装好的通知发出  
//		        nm.notify(ID, n);  
				
//			    Intent	intent1 = new Intent(context,MainActivity.class);
//				PendingIntent pi = PendingIntent.getActivity(context, 0, intent1, 0);
//				Notification notification = new Notification();
//				notification.when = System.currentTimeMillis();
//				notification.tickerText = "NOTIFICATION";
//				notification.defaults = Notification.DEFAULT_ALL;
//				notification.setLatestEventInfo(context, "普通通知", "点击查看", pi);
//				NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//				notificationManager.notify(NOTIFICATION_ID, notification);
//				Object[] pdus = (Object[]) bundle.get("pdus");
//				SmsMessage[] messages = new SmsMessage[pdus.length];
//				for (int i = 0; i < messages.length; i++) {
//					messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
//					
//				}
//				
//				for (int i = 0; i < messages.length; i++) {
//					sBuilder.append("短信来源:");
//					sBuilder.append(messages[i].getDisplayOriginatingAddress());
//					sBuilder.append(messages[i].getDisplayMessageBody());
//					
//					
//				}
//				
//				Toast.makeText(context, sBuilder, Toast.LENGTH_LONG).show();
			}
		}
	}

}
