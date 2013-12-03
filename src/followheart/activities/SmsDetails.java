package followheart.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import followheart.activities.R;
import followheart.adapters.DetailsAdapter;
import followheart.entities.SMSConstant;
import followheart.receivers_and_services.SmsObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.Contacts.People;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.telephony.SmsManager;
import android.text.ClipboardManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "NewApi", "HandlerLeak" })
public class SmsDetails extends Activity {
	
	private static final int  PUT_IN_DRAFT = 2;
	
	private ImageButton imgBtn;
	private TextView tv_contactName;
	private ImageButton addContactBtn;
	private PopupWindow popupWindow;
	
	private String thread_id = null;
	private ArrayList<String> address = null;
	private SmsObserver smsObserver;
	private String SmsBody = null;// 记录将要发送的短信内容
	private boolean isContact = false;
	private boolean isGroup = false;
	private ArrayList<String> contactNames = null;
	Button sendBtn;
	EditText reply_editText;
	ListView listView;
	private DetailsAdapter adapter;
	OnClickListener cl;
	// 需要获得的字段列
	public static final String[] PROJECTION = { SMSConstant.ID,
			SMSConstant.TYPE, SMSConstant.ADDRESS, SMSConstant.BODY,
			SMSConstant.DATE, SMSConstant.THREAD_ID, SMSConstant.READ,
			SMSConstant.PROTOCOL };

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == SmsObserver.SMS_CHANGE) {
				System.out.println("------------->短信数据库变化SmsDetail");
				Cursor cursor = getContentResolver().query(
						Uri.parse(SMSConstant.strUriAll),
						PROJECTION,
						SMSConstant.THREAD_ID + "=" + thread_id
								+ " and type != 3", null, "date asc");
				adapter.setCursor(cursor);
				adapter.notifyDataSetChanged();
				listView.setSelection(listView.getAdapter().getCount() - 1);
				// 更新短信数据库，标记为已读
				ContentValues values = new ContentValues();
				values.put("read", 1);
				getContentResolver().update(
						Uri.parse(SMSConstant.strUriInbox),
						values,
						SMSConstant.THREAD_ID + " = " + thread_id
								+ " and read = 0", null);
			}
			if (msg.what == 1) {
				listView.setSelection(listView.getAdapter().getCount() - 1);
			}
			
			if (msg.what == PUT_IN_DRAFT) {
			    Toast.makeText(SmsDetails.this, "信息已存为草稿", Toast.LENGTH_SHORT).show();
			}
		}
	};

	// 广播接受者
	private SendReceiver sendReceiver = new SendReceiver();
	private DeliverReceiver deliverReceiver = new DeliverReceiver();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms_details);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		View actionBarView = LayoutInflater.from(SmsDetails.this).inflate(R.layout.conversation_title,
                new LinearLayout(SmsDetails.this), false);
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setCustomView(actionBarView);
		
//		getActionBar().setDisplayUseLogoEnabled(true);
		getActionBar().setLogo(R.drawable.anctionbar_bg);
		getActionBar().setBackgroundDrawable(this.getBaseContext().getResources().getDrawable(R.drawable.actionbar_bg));
		
		tv_contactName = (TextView) actionBarView.findViewById(R.id.contact_name);
        imgBtn = (ImageButton) actionBarView.findViewById(R.id.callbtn);
        addContactBtn = (ImageButton) actionBarView.findViewById(R.id.add_contact);
        popupWindow = new PopupWindow();
        
		smsObserver = new SmsObserver(mHandler);

		String[] projection = new String[] {
				"groups.group_thread_id AS group_id",
				"groups.msg_count AS msg_count",
				"groups.group_date AS last_date", "sms.body AS last_msg",
				"sms.address AS contact" };

		listView = (ListView) findViewById(R.id.conlists);
		reply_editText = (EditText) findViewById(R.id.eidt_reply);
		sendBtn = (Button) findViewById(R.id.send_reply);

		// 获取Activity的传值
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		thread_id = bundle.getString("thread_id");
		address = bundle.getStringArrayList("phoneNumber");
		contactNames = bundle.getStringArrayList("contactName");
		
		if (address.size() > 1) {
			isGroup = true;
			imgBtn.setBackgroundResource(R.drawable.info);
			String contactName = "";
			for (int i = 0; i < contactNames.size(); i++) {
				if (i == contactNames.size() - 1) {
					contactName += contactNames.get(i);
				} else {
					contactName += contactNames.get(i) + ";";
				}
			}
			tv_contactName.setText(contactName);
//			if (!address.get(0).equals(contactNames.get(0))) {
//				isContact = true;
//			}
		} else {
			if (!address.get(0).equals(contactNames.get(0))) {
//				isContact = true;
				tv_contactName.setText(contactNames.get(0) + "\n" + address.get(0));
			}
			else {
				addContactBtn.setVisibility(View.VISIBLE);
				addContactBtn.setBackgroundResource(R.drawable.add_contacts_selector);
				tv_contactName.setText(address.get(0));
			}
		}
//		System.out.println("----------->address = " + address);
//		System.out.println("---------->group_id = "
//				+ bundle.getString("thread_id"));
		// Cursor cursor =
		// getContentResolver().query(Uri.parse(SMSConstant.strUriConversations),
		// projection,"groups.group_thread_id = 15", null,
		// "groups.group_date DESC");
		// System.out.println("------------>cursor" + cursor);
//		String thread_id = bundle.getString("thread_id");
		Cursor cursor_smsInDraft = getContentResolver().query(
				Uri.parse(SMSConstant.strUriAll),
				PROJECTION,
				SMSConstant.THREAD_ID + "=" + thread_id + " and "
						+ SMSConstant.TYPE + " = 3", null, "date asc");

		if (cursor_smsInDraft.moveToFirst()) {
			reply_editText.setText(cursor_smsInDraft
					.getString(cursor_smsInDraft
							.getColumnIndex(SMSConstant.BODY)));
			// for (int j = 0; j < cursor_smsInDraft.getColumnCount(); j++) {
			// String info =
			// cursor_smsInDraft.getString(cursor_smsInDraft.getColumnIndex(cursor_smsInDraft.getColumnName(j)))
			// ;
			// // System.out.println("------>  " +
			// cursor_smsInDraft.getColumnName(j) + " = " + info);
			// }
		}
		Cursor cursor = getContentResolver().query(
				Uri.parse(SMSConstant.strUriAll),
				PROJECTION,
				SMSConstant.THREAD_ID + "=" + thread_id + " and "
						+ SMSConstant.TYPE + " != 3", null, "date asc");

		adapter = new DetailsAdapter(this, cursor);

		// 将草稿箱中的内容直接呈现在edtiText中
		if (adapter.getSMSInDraft() != null) {
			reply_editText.setText(adapter.getSMSInDraft());
		}

		listView.setAdapter(adapter);
		listView.setDivider(null);
        listView.setSelector(R.drawable.list_shape);

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Cursor cursor = adapter.getCursor();
				cursor.moveToPosition(arg2);
				String body = cursor.getString(cursor
						.getColumnIndex(SMSConstant.BODY));
				long _id = Long.parseLong(cursor.getString(cursor
						.getColumnIndex(SMSConstant.ID)));
				show_SMS_Dialog(SmsDetails.this, body, _id);
				return true;
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Cursor cursor = adapter.getCursor();
				cursor.moveToPosition(arg2);
				System.out.println("-----------> type = "
						+ cursor.getString(cursor
								.getColumnIndex(SMSConstant.TYPE)));
			}
		});

		// Cursor cursorOutbox =
		// getContentResolver().query(Uri.parse(SMSConstant.strUriOutbox),
		// PROJECTION, null, null, null);
		// if (cursorOutbox.moveToFirst()) {
		// do{
		// for(int j = 0; j < cursorOutbox.getColumnCount(); j++){
		// String info = "name:" + cursorOutbox.getColumnName(j) + "=" +
		// cursorOutbox.getString(j);
		// System.out.println("---------->" + info);
		// }
		// }while(cursorOutbox.moveToNext());
		// }

		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				// 写入到短信数据源
				// String body = reply_editText.getText().toString();
				// reply_editText.setText("");
				// SmsBody = body;
				// ContentValues values = new ContentValues();
				// values.put("address",address); //发送地址
				// values.put("body", body); //消息内容
				// values.put("date", System.currentTimeMillis()); //创建时间
				// values.put("read", 1); //0:未读;1:已读
				// // values.put("type", 2); //1:接收;2:发送
				// getContentResolver().insert(Uri.parse(SMSConstant.strUriOutbox),
				// values); //插入数据
				// Timer timer = new Timer();
				// timer.schedule(new TimerTask() {
				//
				// @Override
				// public void run() {
				// // TODO Auto-generated method stub
				// ContentValues values1 = new ContentValues();
				// values1.put("address",address); //发送地址
				// values1.put("body", SmsBody); //消息内容
				// values1.put("date", System.currentTimeMillis()); //创建时间
				// values1.put("read", 1); //0:未读;1:已读
				// values1.put("type", 2); //1:接收;2:发送
				// getContentResolver().update(Uri.parse(SMSConstant.strUriOutbox),
				// values1, "type = 4", null);
				// }
				// }, 2000);

				// Cursor cursorOutbox =
				// getContentResolver().query(Uri.parse(SMSConstant.strUriOutbox),
				// PROJECTION, null, null, null);
				// if (cursorOutbox.moveToFirst()) {
				// do{
				// for(int j = 0; j < cursorOutbox.getColumnCount(); j++){
				// String info = "name:" + cursorOutbox.getColumnName(j) + "=" +
				// cursorOutbox.getString(j);
				// System.out.println("---------->" + info);
				// }
				// }while(cursorOutbox.moveToNext());
				// }
				SmsBody = reply_editText.getText().toString();
				if (SmsBody.isEmpty()) {
					Toast.makeText(getApplicationContext(), "亲，不要发送空消息！", Toast.LENGTH_SHORT).show();
				}
				else {
					reply_editText.setText("");
					String addressString = "";
					for (int i = 0; i < address.size(); i++) {
						addressString += address.get(i) + ";";
					}
					sendSMS(addressString, SmsBody,Long.parseLong(thread_id));
				}
				
//				sendSMS(view);
			}
		});

		reply_editText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Message message = new Message();
						message.what = 1;
						mHandler.sendMessage(message);
					}
				}, 500);

				return false;
			}
		});

		
		imgBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isGroup) {
//					System.out.println("---------->isgroup");
					showPopupWindow(v);
				}else {
					Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
							+ address));
					SmsDetails.this.startActivity(intent);
				}
				
			}
		});
		
		//如果不是联系人则添加到联系人
		addContactBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
				intent.setType("vnd.android.cursor.item/person");
				intent.setType("vnd.android.cursor.item/contact");
				intent.setType("vnd.android.cursor.item/raw_contact");
//				intent.putExtra(android.provider.ContactsContract.Intents.Insert.NAME, name);
//				intent.putExtra(android.provider.ContactsContract.Intents.Insert.COMPANY,company);
				intent.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE, address.get(0));
				intent.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE_TYPE, 3);
				startActivity(intent);

			}
		});

	}

	private void show_SMS_Dialog(final Context context, final String smsBody,
			final long _id) {

		// 【Ⅰ】 获取自定义Dialog布局文件

		final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.operator_sms_dialog);
//		dialog.setTitle("信息选项" + " : ");

		final Button deleteBtn = (Button) dialog.findViewById(R.id.delete_sms);
		final Button transmitBtn = (Button) dialog
				.findViewById(R.id.transmit_sms);
		final Button copyBtn = (Button) dialog.findViewById(R.id.copy_sms);
//		final TextView textView = (TextView) dialog.findViewById(R.id.sms_title);
		dialog.setCanceledOnTouchOutside(true);

		dialog.show();

		deleteBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Uri uri = ContentUris.withAppendedId(SMSConstant.CONTENT_URI,
						_id);

				/*
				 * 可以根据短信内容进行判断，执行您想要的操作，如发送 Filter字符+dialog你就弹出个对话框，
				 * 操作省略，自行完善所需控制操作 。。。。。。。。。。。。。。
				 */

				// 删除指定的短信,操作不留痕迹。。。^_^
				context.getContentResolver().delete(uri, null, null);

			}
		});
		transmitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		copyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				copyText(SmsDetails.this, smsBody);
				Toast.makeText(SmsDetails.this, "亲，短信已复制哦！", Toast.LENGTH_SHORT)
						.show();
			}
		});

	}

	// 复制到剪切板
	@SuppressWarnings("deprecation")
	public void copyText(Context context, String text) {
		ClipboardManager cm = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cm.setText(text);
	}
	
	  /*关于弹出悬浮框的方法
	   * 
	   * */	
	private void showPopupWindow(View anchor) {

		// 【Ⅰ】 获取自定义popupWindow布局文件

		LayoutInflater inflater = (LayoutInflater) SmsDetails.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View vPopupWindow = inflater.inflate(R.layout.popupwindow, null,
				false);
		vPopupWindow.setBackgroundColor(Color.rgb(0, 0, 0));

		// 【Ⅳ】自定义布局中的事件响应
		// OK按钮及其处理事件
		final ListView listView = (ListView) vPopupWindow
				.findViewById(R.id.contacts_info_lv);
		
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1);
        String contactInfo = "";
        for (int i = 0; i < address.size(); i++) {
        	if (address.get(i).equals(contactNames.get(i))) {
				contactInfo = contactNames.get(i);
				adapter.add(contactInfo);
			}
        	else {
        		contactInfo = contactNames.get(i) + "\n" + address.get(i);
        		adapter.add(contactInfo);
			}
			
		}
        listView.setAdapter(adapter);
		// 【Ⅲ】 显示popupWindow对话框
		// 获取屏幕和对话框各自高宽
//		int screenWidth;
		// screenWidth = metrics.widthPixels;
		
        popupWindow = new PopupWindow(vPopupWindow, 400,
				LayoutParams.WRAP_CONTENT, true);// 声明一个弹出框											// ，最后一个参数和setFocusable对应
		popupWindow.setContentView(vPopupWindow); // 为弹出框设定自定义的布局
		popupWindow.setAnimationStyle(R.style.PopupAnimation);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.showAsDropDown(anchor, 0, 0);

	}
	@Override
	protected void onResume() {
		// 注册发送成功的广播
		registerReceiver(sendReceiver, new IntentFilter("SENT_SMS_ACTION"));
		// 注册接收成功的广播
		registerReceiver(deliverReceiver, new IntentFilter(
				"DELIVERED_SMS_ACTION"));
		this.getContentResolver().registerContentObserver(
				Uri.parse("content://sms"), true, smsObserver);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// unregisterReceiver(sendReceiver);
		// unregisterReceiver(deliverReceiver);
		// getContentResolver().unregisterContentObserver(sObserver);
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
//		putIntoSmsDraft(address);
		getContentResolver().unregisterContentObserver(smsObserver);// 注销监听
		unregisterReceiver(sendReceiver);
		unregisterReceiver(deliverReceiver);
		super.onDestroy();
	}

	// 存为草稿
	private void putIntoSmsDraft(ArrayList<String> addresses) {
//		Uri uri = Uri.parse(SMSConstant.strUriDraft);
//		Uri uriDraft = ContentUris.withAppendedId(
//				Uri.parse(SMSConstant.strUriDraft), Long.parseLong(thread_id));
		// System.out.println("------------->uriDraft  = " + uriDraft);
		// 存为草稿
		String smsDraft = reply_editText.getText().toString();
		Cursor cursor = getContentResolver().query(
				Uri.parse(SMSConstant.strUriDraft), PROJECTION,
				"type = 3" + " and thread_id = " + thread_id, null, null);
		ContentValues values = new ContentValues();
		values.put(SMSConstant.BODY, smsDraft);
		values.put(SMSConstant.THREAD_ID, thread_id);
		String address = "";
		for (int i = 0; i < addresses.size(); i++) {
				address += addresses.get(i) + ";";
		}
//		System.out.println("----------->address = " + address);
		values.put(SMSConstant.ADDRESS, address);
		// values.put(SMSConstant.TYPE, 3);
		if (cursor.moveToFirst()) {
			if (!smsDraft.equals("")) {
//				System.out.println("--------------->update");
				// getContentResolver().update(uri , values,
				// SMSConstant.THREAD_ID + "=" + thread_id + " and type = 3",
				// null);
				// 先删除后插入，保证回话中可以查到草稿，直接更新的话草稿不会是最后一条（回话查到的是该回话的最后一条）
				getContentResolver().delete(Uri.parse(SMSConstant.strUriAll),
						"thread_id = " + thread_id + " and type = 3", null);
				getContentResolver().insert(Uri.parse(SMSConstant.strUriDraft),
						values);
				Message msg = new Message();
				msg.what = PUT_IN_DRAFT;
				mHandler.sendMessage(msg);
//				Toast.makeText(SmsDetails.this, "信息已存为草稿", Toast.LENGTH_SHORT)
//						.show();
			} else {
				getContentResolver().delete(Uri.parse(SMSConstant.strUriAll),
						"thread_id = " + thread_id + " and type = 3", null);
			}
            cursor.close();
		} else {
			if (!smsDraft.equals("")) {
				getContentResolver().insert(Uri.parse(SMSConstant.strUriDraft),
						values);
//				System.out.println("--------------->insert");
				Message msg = new Message();
				msg.what = PUT_IN_DRAFT;
				mHandler.sendMessage(msg);
//				Toast.makeText(SmsDetails.this, "信息已存为草稿", Toast.LENGTH_SHORT)
//						.show();
			}

		}

	}

	private class SendReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				for (String ads : address) {
					ContentValues values1 = new ContentValues();
					values1.put("address", ads); // 发送地址
					values1.put("body", SmsBody); // 消息内容
					values1.put("date", System.currentTimeMillis()); // 创建时间
					values1.put("read", 1); // 0:未读;1:已读
					values1.put("type", 2); // 1:接收;2:发送
					getContentResolver().update(
							Uri.parse(SMSConstant.strUriOutbox), values1,
							"type = 4", null);
				}
				Toast.makeText(context, "短信已发送.", Toast.LENGTH_SHORT)
				.show();

				break;
			default:
				for (String ads : address) {
					ContentValues values2 = new ContentValues();
					values2.put("address", ads); // 发送地址
					values2.put("body", SmsBody); // 消息内容
					values2.put("date", System.currentTimeMillis()); // 创建时间
					values2.put("read", 1); // 0:未读;1:已读
					values2.put("type", 5); // 1:接收;2:发送
					getContentResolver().update(
							Uri.parse(SMSConstant.strUriOutbox), values2,
							"type = 4", null);
				}
				Toast.makeText(context, "Failed to Send.", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	/**
	 * 发送方的短信发送到对方手机上之后,对方手机会返回给运营商一个信号, 运营商再把这个信号发给发送方,发送方此时可确认对方接收成功
	 * 模拟器不支持,真机上需等待片刻
	 * 
	 * @author user
	 * 
	 */
	private class DeliverReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// Toast.makeText(context, "Delivered Successfully.",
			// Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			this.finish();
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK /* && event.getRepeatCount() == 0 */) {
			if (popupWindow.isShowing()) {
				popupWindow.dismiss();
			}
			else {
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						putIntoSmsDraft(address);
					}
				}).start();

				finish();
			}
			
		}
		return false;
	}
	

	// ////////////////////////////////////////////////////////////////////////////////
		// send sms.
	public void sendSMS(String messageAddress, String messageContent,
			long threadID) {

		if (messageAddress.trim().length() != 0
				&& messageContent.trim().length() != 0) {
			try {
				final String SENT_SMS_ACTION = "SENT_SMS_ACTION";
				final String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
				// final Uri SMS_URI = Uri.parse("content://sms/");
				// 初始化静态变量
				// index = 1;
				// smsCount = 0;
				// smsSuccess = 0;
				// 获取联系人列表
				String[] addressList = messageAddress.trim().split(";");
				SmsManager smsManager = SmsManager.getDefault();
				// smsCount = addressList.length;
				// 短信发送广播
				Intent send = new Intent(SENT_SMS_ACTION);
				PendingIntent sendPI = PendingIntent.getBroadcast(this, 0,
						send, 0);
				// registerSMSBroadcastReceiver();
				// 发送结果广播
				Intent delive = new Intent(DELIVERED_SMS_ACTION);
				PendingIntent deliverPI = PendingIntent.getBroadcast(this, 0,
						delive, 0);
				Set<String> phoneSet = new HashSet<String>(
						Arrays.asList(addressList));

				// // 创建或获取会话编号
				// long threadId = Threads.getOrCreateThreadId(
				// getApplicationContext(), phoneSet);
				// System.out.println("------------>thread ID = " + threadId);
				// 迭代发送短信
				ContentValues cv = new ContentValues();
				for (String phoneNum : phoneSet) {
					// 写入到短信数据源
					cv.put("thread_id", threadID);
					cv.put("date", System.currentTimeMillis());
					cv.put("body", messageContent);
					cv.put("read", 1);
					// cv.put("type", 2);
					cv.put("address", phoneNum);
					getContentResolver().insert(
							Uri.parse(SMSConstant.strUriOutbox), cv);
				}

				if (messageContent.trim().length() > 70) { // 如果字数超过70,需拆分成多条短信发送
					List<String> msgs = smsManager
							.divideMessage(messageContent);
					for (String phoneNum : phoneSet) {
						for (String msg : msgs) {
							smsManager.sendTextMessage(phoneNum, null, msg,
									sendPI, deliverPI);
						}

					}

				} else {
					for (String phoneNum : phoneSet) {
						smsManager.sendTextMessage(phoneNum, null,
								messageContent, sendPI, deliverPI);
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("-------------->Exception has occur");
			}
		}
	}

}
