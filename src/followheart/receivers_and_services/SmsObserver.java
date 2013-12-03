package followheart.receivers_and_services;

import android.database.ContentObserver;
import android.os.Handler;

public class SmsObserver extends ContentObserver {
	public final static int SMS_CHANGE = 0;
	
	Handler handle;
	public SmsObserver(Handler h) {
		super(h);
		// TODO Auto-generated constructor stub
		handle = h;
	}
	
	public void onChange(boolean selfChange) {   
        // TODO Auto-generated method stub   
        super.onChange(selfChange);   
        
        //notify SMSInbox & SMSSent
        handle.sendEmptyMessage(SMS_CHANGE);
    }

}
