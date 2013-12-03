package followheart.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.jar.Attributes.Name;


import com.todddavies.components.progressbar.ProgressWheel;

import followheart.activities.ContactsActivity;
import followheart.activities.R;
import followheart.activities.SmsDetails;
import followheart.adapters.ConversationAdapter;
import followheart.entities.SMSConstant;
import followheart.entities.SMSGroupInfo;
import followheart.receivers_and_services.SMSService;
import followheart.receivers_and_services.SmsObserver;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

@SuppressLint({ "SimpleDateFormat", "HandlerLeak", "NewApi" })
public class SmsConversationFragment extends Fragment {

	private Cursor canonical_addressCur;//根据recipient_ids查相应的号码
	private Cursor phoneCursor;//根据号码查联系人的名字
	
//    private HashMap<String, SMSGroupInfo> groupMap = new HashMap<String, SMSGroupInfo>();
//    private ArrayList<String> groupIDs = new ArrayList<String>();
//    private ArrayList<SMSGroupInfo> smsGroupInfos = new ArrayList<SMSGroupInfo>();
	private ListView listView;
	private ImageButton createMsgButton;
//	private Cursor cursor;
//	private Cursor cursorOriginal;
	private ConversationAdapter adapter;
	private NotificationManager notificationManager;
	private ProgressWheel progressWheel;
	private ArrayList<SMSGroupInfo> smsGroupInfos = new ArrayList<SMSGroupInfo>();
	
	private boolean inSearchMode = false;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
	
	String[] projection = new String[]{"groups.group_thread_id AS group_id", "groups.msg_count AS msg_count",  
            "groups.group_date AS last_date", "sms.body AS last_msg", "sms.address AS contact" ,"sms.type AS type" ,"sms.read AS read" };  

	public final String[] conversationProjection = new String[] {
			"groups.group_date AS last_date",
			"sms.type AS type" };
	
	private Object searchLock = new Object();
	
	//查询短信数据库中的threads表和sms表的sql语句
	public final String[] CONVERSATION = new String[]{
			"distinct threads._id,threads.date,threads.message_count," +
			"threads.recipient_ids,threads.snippet,threads.read,sms.type,sms.address " +
                    "from threads inner join sms " +
                    "on threads._id = sms.thread_id and sms.body = threads.snippet and threads.date = sms.date order by sms.date DESC" + " --"
	};
	
	public static final Uri MMSSMS_FULL_CONVERSATION_URI = Uri.parse("content://mms-sms/conversations");
	public static final Uri CONVERSATION_URI = MMSSMS_FULL_CONVERSATION_URI.buildUpon().
		appendQueryParameter("simple", "true").build();
    private static final int ID             = 0;
    private static final int DATE           = 1;
    private static final int MESSAGE_COUNT  = 2;
    private static final int RECIPIENT_IDS  = 3;
    private static final int SNIPPET        = 4;
    private static final int SNIPPET_CS     = 5;
    private static final int READ           = 5;
    private static final int TYPE           = 6;
    private static final int SMS_ADDRESS    = 7;
    private static final int ERROR          = 8;
    private static final int HAS_ATTACHMENT = 9;
    private static final int SMS_TYPE       = 10;
    private static final String[] ALL_THREADS_PROJECTION = {
        "_id", "date", "message_count", "recipient_ids",
        "snippet", "snippet_cs", "read", "error", "has_attachment"
    };

	
	 //需要获得的字段列  
    public static final String[] PROJECTION={  
        SMSConstant.ID,  
        SMSConstant.TYPE,  
        SMSConstant.ADDRESS,  
        SMSConstant.BODY,  
        SMSConstant.DATE,  
        SMSConstant.THREAD_ID,  
        SMSConstant.READ,  
        SMSConstant.PROTOCOL  
    };  
    
    public static final int PHONES_DISPLAY_NAME_INDEX = 0; 
    public static final int PHONES_NUMBER_INDEX = 1; 
    public static final int PHONES_PHOTO_ID_INDEX = 2; 
    public static final int PHONES_CONTACT_ID_INDEX = 3; 
//    private static final String[] PHONES_PROJECTION = new String[] {
//        Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
    
    private ContentResolver resolver;
//    private Context context;
    private Activity activity;
    
  //取值对应的结果就是PROJECTION 里对应的字段  
//    private static final int COLUMN_INDEX_ID    = 0;  
//    private static final int COLUMN_INDEX_TYPE  = 1;  
//    private static final int COLUMN_INDEX_PHONE = 2;  
//    private static final int COLUMN_INDEX_BODY  = 3;  
//    private static final int COLUMN_INDEX_DATE  = 4;  
//    private static final int COLUMN_INDEX_PROTOCOL = 7;  
//    private int count = 0;
    //查询联系人  
    private static final String CONTACTS_LOOKUP = "content://com.android.contacts/phone_lookup/";  
    
    private ConversationInitTask conversationInitTask;
	
	private Handler handler = new Handler(){
    	public void handleMessage(Message msg){
    		if(msg.what == SmsObserver.SMS_CHANGE){
//    			final Cursor cursorOriginal = resolver.query(Uri.parse(SMSConstant.strUriConversations), projection, null, null,  "groups.group_date DESC");
//    			System.out.println("----------->cursor = " + cursor);	
//    	        cursor = phoneNumberToName(cursorOriginal);
                System.out.println("-------------->SMSinboxFragment change");
                
                if (conversationInitTask != null
    					&& conversationInitTask.getStatus() != AsyncTask.Status.FINISHED) {
    				try {
    					conversationInitTask.cancel(true);
    				} catch (Exception e) {
    					// TODO: handle exception
    					System.out.println("task close exception");
    				}
    			}
    			conversationInitTask = new ConversationInitTask();
    			conversationInitTask.execute();
                
//				if (!inSearchMode) {
//					inSearchMode = true;
//					count ++;
//					System.out.println("------------> count = " + count);
//					new Thread(new Runnable() {
//
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							Message msg = new Message();
//							msg.what = 3;
//							msg.obj = getData();
//							handler.sendMessage(msg);
//						}
//					}).start();
//				}
    		}
//    		if (msg.what == 2) {
//    			progressWheel.setVisibility(View.GONE);
//    			adapter = new ConversationAdapter(activity, smsGroupInfos);
//    			listView.setAdapter(adapter);
//			}
//    		if (msg.what == 3) {
//    			inSearchMode = false;
//    			adapter = new ConversationAdapter(activity, (ArrayList<SMSGroupInfo>)msg.obj);		
//     			listView.setAdapter(adapter);
//			}
    	
    	}
    };
    private SmsObserver smsObserver;
        
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	RelativeLayout viewLayout = (RelativeLayout) inflater.inflate(R.layout.activity_sms_inbox, null);
    	listView = (ListView) viewLayout.findViewById(R.id.lv);
		listView.setDivider(null);
		listView.setSelector(R.drawable.list_shape);
		
		createMsgButton = (ImageButton) viewLayout.findViewById(R.id.creat_sms_btn);
		//创建新短信
		createMsgButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(activity,ContactsActivity.class);
				startActivity(intent);
			}
		});
		
		//创建progressWheel的背景
		int[] pixels = new int[] { 0xFF2E9121, 0xFF2E9121, 0xFF2E9121,
				0xFF2E9121, 0xFF2E9121, 0xFF2E9121, 0xFFFFFFFF, 0xFFFFFFFF };
		Bitmap bm = Bitmap.createBitmap(pixels, 8, 1, Bitmap.Config.ARGB_8888);
		Shader shader = new BitmapShader(bm, Shader.TileMode.REPEAT,
				Shader.TileMode.REPEAT);
	        
	    progressWheel = (ProgressWheel) viewLayout.findViewById(R.id.pw_spinner);
		progressWheel.spin();
		progressWheel.setRimShader(shader);
		
		return viewLayout;
	}

	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		resolver = getActivity().getContentResolver();
		activity = getActivity();
		smsObserver = new SmsObserver(handler);
		this.resolver.registerContentObserver(Uri.parse("content://sms"), true, smsObserver);
		
		notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(SMSService.SMSObserver.NITIFICATION_ID);
     
		conversationInitTask = new ConversationInitTask();
		conversationInitTask.execute();
		//同步查询联系人 
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
////				smsGroupInfos = initDate(cursorOriginal);
//				smsGroupInfos = getData();
//				Message msg = new Message();
//				msg.what = 2;
//				handler.sendMessage(msg);
//			}
//		}).start();

		listView.setOnItemClickListener(new OnItemClickListener(){
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				notificationManager.cancel(SMSService.SMSObserver.NITIFICATION_ID);
				SMSGroupInfo smsGroupInfo = (SMSGroupInfo) adapter.getItem(arg2);
//				cursor.moveToPosition(arg2);
//				Cursor cursorSms = querySMSConversation();
//				cursorSms.moveToPosition(arg2);
//				String body = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString(); 
//				String contactName = cursor.getString(cursor.getColumnIndexOrThrow("contact")).toString();
//				String thread_id = cursor.getString(cursor.getColumnIndex("group_id"));
//				String phoneNumber = cursorSms.getString(cursorSms.getColumnIndex("contact"));
//				String lastDate = "lastDate: " + dateFormat.format(cursor.getLong(cursor.getColumnIndex("last_date"))); 
//				System.out.println("-------->data = " + lastDate);
				//更新短信数据库，标记为已读
				ContentValues values = new ContentValues();
				values.put("read", 1);
				resolver.update(Uri.parse(SMSConstant.strUriInbox), values, SMSConstant.THREAD_ID + " = " +
						smsGroupInfo.getGroupId() + " and read = 0", null);

				for (int i = 0; i < smsGroupInfo.getContactNames().size(); i++) {
					System.out.println("---------->name : " + i + " = " + smsGroupInfo.getContactNames().get(i));
				}
				
				Bundle b = new Bundle();
				b.putStringArrayList("contactName", smsGroupInfo.getContactNames());
				b.putStringArrayList("phoneNumber", smsGroupInfo.getContactNumbers());
				b.putString("thread_id", smsGroupInfo.getGroupId());
				Intent intent = new Intent(activity,SmsDetails.class);
				intent.putExtras(b);	
				startActivity(intent);
			}
        	
        });
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

				ArrayList<String> names = adapter.getItem(arg2).getContactNames();
				ArrayList<String> numbers = adapter.getItem(arg2).getContactNumbers();
				String thread_id = adapter.getItem(arg2).getGroupId();
//				for (int i = 0; i < smsGroupInfo.getContactNames().size(); i++) {
//					System.out.println("---------->name : " + i + " = " + smsGroupInfo.getContactNames().get(i));
//				}
				show_SMS_Dialog(activity, names,
						thread_id, numbers);
				return true;
			}
		});
		
	}
	

	
	private void show_SMS_Dialog(final Context context,
			final ArrayList<String> contacts, final String thread_id,
			final ArrayList<String> phoneNums) {

		// 【Ⅰ】 获取自定义popupWindow布局文件

		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.operator_conversation_dialog);
		
		String contact = "";
        
		final Button deleteBtn = (Button) dialog
				.findViewById(R.id.delete_conversations);
		final Button callBtn = (Button) dialog.findViewById(R.id.call_contact);
		final Button putInRubbishBtn = (Button) dialog
				.findViewById(R.id.to_be_rubbish);
		final TextView textView = (TextView) dialog.findViewById(R.id.title);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		if (contacts.size() > 1) {
			callBtn.setVisibility(View.GONE);
			for (int j = 0; j < contacts.size(); j++) {
				if (j == contacts.size() - 1) {
					contact += contacts.get(j);
				} else {
					contact += contacts.get(j) + ";";
				}
			}
		} else {
			contact = contacts.get(0);
		}
//		dialog.setTitle(contact);
        textView.setText(contact);
		callBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ phoneNums.get(0)));
				startActivity(intent);
			}
		});
		deleteBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Uri uri = Uri.parse("content://sms/conversations/" + thread_id);

				/*
				 * 可以根据短信内容进行判断，执行您想要的操作，如发送 Filter字符+dialog你就弹出个对话框，
				 * 操作省略，自行完善所需控制操作 。。。。。。。。。。。。。。
				 */

				// 删除指定的短信,操作不留痕迹。。。^_^
				resolver.delete(uri, null, null);

			}
		});
		putInRubbishBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}

	 
	//获取会话信息
	private ArrayList<SMSGroupInfo> getData() {
		ArrayList<SMSGroupInfo> groupInfos = new ArrayList<SMSGroupInfo>();
		String thread_id = "";
		String message_count = "";
		String snippet = "";
		String recipient_ids = "";
		String read = "";
		String type = "";
		String date = "";
		// Cursor cursor = resolver.query(
		// Uri.parse(SMSConstant.strUriAll),
		// new String[] { "* from threads--" }, "_id = 1119", null,
		// "date DESC");
		Cursor cursor = resolver.query(Uri.parse(SMSConstant.strUriAll),
				CONVERSATION, null, null, null);
		// Cursor cursor = resolver.query(CONVERSATION_URI,
		// ALL_THREADS_PROJECTION, null, null, "date DESC");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				do {
					// thread_id = cursor.getString(cursor
					// .getColumnIndex("_id"));
					// // String date = dateFormat.format(cursor.getLong(cursor
					// // .getColumnIndex("date")));
					// message_count = cursor.getString(cursor
					// .getColumnIndex("message_count"));
					// snippet = cursor.getString(cursor
					// .getColumnIndex("snippet"));
					// recipient_ids = cursor.getString(cursor
					// .getColumnIndex("recipient_ids"));
					// read = cursor.getString(cursor.getColumnIndex("read"));

					thread_id = cursor.getString(ID);
					message_count = cursor.getString(MESSAGE_COUNT);
					snippet = cursor.getString(SNIPPET);
					recipient_ids = cursor.getString(RECIPIENT_IDS);
					read = cursor.getString(READ);
					date = dateFormat.format(cursor.getLong(DATE));
					type = cursor.getString(TYPE);

					// ArrayList<String> phoneNums = new ArrayList<String>();
					// ArrayList<String> contactsNames = new
					// ArrayList<String>();
					// Cursor cursorSms = resolver.query(
					// Uri.parse(SMSConstant.strUriConversations),
					// conversationProjection, "groups.group_thread_id = " +
					// thread_id,
					// null, null);
					// if (cursorSms != null && cursorSms.moveToFirst()) {
					// type =
					// cursorSms.getString(cursorSms.getColumnIndex("type"));
					// date =
					// dateFormat.format(cursorSms.getLong(cursorSms.getColumnIndex("last_date")));
					// }
					SMSGroupInfo groupInfo = new SMSGroupInfo(activity,
							thread_id, date, snippet, message_count, read, type);
					// System.out.println("-------------->recipient = "
					// + recipient_ids.split(" "));

					// 根据recipient_ids获取群发短信的收件人的号码及名字
					for (String recipientId : recipient_ids.split(" ")) {
						canonical_addressCur = resolver
								.query(Uri
										.parse("content://mms-sms/canonical-addresses"),
										null, "_id = " + recipientId, null,
										null);
						try {
							if (canonical_addressCur != null
									&& canonical_addressCur.moveToFirst()) {
								do {
									String phoneNum = canonical_addressCur
											.getString(canonical_addressCur
													.getColumnIndex("address"));
									groupInfo.getContactNumbers().add(phoneNum);
									Uri uri = Uri.parse(CONTACTS_LOOKUP
											+ phoneNum);
									phoneCursor = resolver.query(uri, null,
											null, null, null);
									try {
										if (phoneCursor != null
												&& phoneCursor.moveToFirst()) {
											// do {
											groupInfo
													.getContactNames()
													.add(phoneCursor
															.getString(phoneCursor
																	.getColumnIndex("display_name")));

										} else {
											groupInfo.getContactNames().add(
													phoneNum);
										}
									} finally {
										phoneCursor.close();
									}

								} while (canonical_addressCur.moveToNext());

							}
						} finally {
							canonical_addressCur.close();
						}

					}

					groupInfos.add(groupInfo);
					// for (String name : groupInfo.getContactNames()) {
					// System.out.println("------------>name = " + name);
					// }
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
		}

		return groupInfos;

	}
	
	private class ConversationInitTask extends AsyncTask<Void, Void, Integer> {

		private static final int STATE_FINISH = 1;
		private static final int STATE_ERROR = -1;

		/* 该方法将在执行实际的后台操作前被UI thread调用。可以在该方法中做一些准备工作，如在界面上显示一个进度条。 */
		@Override
		protected void onPreExecute() {
			// 先显示ProgressDialog

		}

		/* 执行那些很耗时的后台计算工作。可以调用publishProgress方法来更新实时的任务进度。 */
		@Override
		protected Integer doInBackground(Void...argVoids) {
			System.out.println("------------>doInBackground");
			smsGroupInfos = getData();
			return STATE_FINISH;
		}

		/*
		 * 在doInBackground 执行完成后，onPostExecute 方法将被UI thread调用，
		 * 后台的计算结果将通过该方法传递到UI thread.
		 */
		@Override
		protected void onPostExecute(Integer result) {
			int state = result.intValue();
			switch (state) {
			case STATE_FINISH:
				synchronized (searchLock) {
					System.out.println("--------------->onPostExecute");
					progressWheel.setVisibility(View.GONE);
	    			adapter = new ConversationAdapter(activity, smsGroupInfos);
	    			listView.setAdapter(adapter);	
				}
				break;
			case STATE_ERROR:
                System.out.println("----------->ConversationInitTask ERROR");
				break;

			default:

			}
		}

	}
	
	
    @Override
	public void onResume()
	 {
		 notificationManager.cancel(SMSService.SMSObserver.NITIFICATION_ID);
		 //数据刷新后重新查一遍会话列表（短信内容已经改变）
//		 cursorOriginal = resolver.query(Uri.parse(SMSConstant.strUriConversations), projection, null, null,  "groups.group_date DESC"); 
		 super.onResume();
	 }
	 
	 
	 
	 @Override
	public void onPause()
	 {
		 super.onPause();
	 }
	 
	 @Override
	public void onStop()
	 {
		 super.onStop();
	 }
	 
	 @Override
	public void onDestroy()
	 {
		 resolver.unregisterContentObserver(smsObserver);
		 super.onDestroy();
	 }
	 
	 
	
}
