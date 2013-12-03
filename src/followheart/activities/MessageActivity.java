package followheart.activities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import followheart.factory.Table;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
public class MessageActivity extends Activity {
	private Button testButton;
	private Button sendButton;
	private EditText numEditText;
	private EditText messageEditText;
	private Context context;
	final String SMS_URI_ALL = "content://sms/";
	//Uri uri ;
	//final String Contacts.CONTENT_URI = "";
	
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// ��ȥ��������Ӧ�ó�������֣�
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN); 
		setContentView(R.layout.activity_message_activity);
		
		context = this;
		testButton = (Button) this.findViewById(R.id.testButton);
		sendButton = (Button) this.findViewById(R.id.sendButton);
		numEditText = (EditText) this.findViewById(R.id.numEditText);
		messageEditText = (EditText) this.findViewById(R.id.messageEditText);
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String number = numEditText.getText().toString();
				//uri = Uri.parse("content://com.android.contacts/contacts"); 
				String num[];
				if (number.contains(" ")) {
					// ��ֹ����ո� ̰����
					num = number.split("( )+");
					Log.d(".................", String.valueOf(num.length));
					for (int i = 0; i < num.length; i++) {
						if (initNum(num[i]).isEmpty()) {
							// �������������people��ѯ
							/*if (context
									.getContentResolver()
									.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
											null,
											ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",
											new String[] {num[i]}, null)
									.getCount() == 0) {
								createDialog("��������");
								numEditText.setText(" ");
								Log.d("!!!!!!!", "lllllllllllllll");
							} else {*/
								Log.d("�Ѿ����룡", "==========");
								Cursor cursor = context
										.getContentResolver()
										.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
												null,
												ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" = ?",
												new String[] {num[i]}, null);
								Log.d("��������", ".=.=.=.=.==.");
								for (int j = 0; j < cursor.getCount(); j++) {
									Log.d("����", "=.=.=.=.=");
									cursor.moveToPosition(j);
									Log.d("����", ".=.=.=.=.=.");
									if (j >= 0) {
										Log.d("number!!!", "��Ԫ������ȣ�");
										num[i] = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
									}
								}//for
								cursor.close();
							//}//else
						}else {
							//���봿���ֻ����ֺ����ֻ��
							List list = initNum(num[i]);
							num[i] = list.get(0).toString();
						}
					}//for
				} else {
					//�޿ո������
					//num = new String[] { number };
					if (initNum(number).isEmpty()) {
						num = getNumByName(number);
					}else{
						num = new String[] { number };
					}
					//sendMessage(num);
				}//else
				sendMessage(num);
			}
			// ���������֮�������Ⱥ������ֱ����Ⱥ�����Ⱥ��������ʾ��Ŀ��
			// ����Ǹ�һ���˷��͵��������Ǹ��˵ĶԻ�����ȥ
			// ����ʵ��activity����ת

		});
		testButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new AlertDialog.Builder(MessageActivity.this)
						.setTitle("ѡ����ϵ��")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setSingleChoiceItems(
								new String[] { "��ϵ��", "���еı�", "Ⱥ��" }, 0,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										if (which == 0) {
											//�����Ŀ������ת
											
										} else if (which == 1) {
											//���еı�
											new AlertDialog.Builder(MessageActivity.this)
											.setTitle("���еı�")
											.setIcon(android.R.drawable.ic_dialog_info)
											.setSingleChoiceItems(
													getTable(), 0,
													new DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog,
																int which) {
															String table[] = getTable();
															for(int i = 0;i<table.length;i++){
																if(which==i){
																	try {
																		String peopleString[] = getPeopleFromTable(table[i]);
																		String textString = "";
																		for(int j = 0;j<peopleString.length;j++){
																			textString += peopleString[j]+" ";
																		}
																		numEditText.setText(textString);
																	} catch (IOException e) {
																		// TODO Auto-generated catch block
																		e.printStackTrace();
																	}
																}
															}
															
															
															/*if (which == 0) {
																//�����Ŀ������ת
																
															} else if (which == 1) {
																//���еı�
																
															} else if (which == 2) {
																//Ⱥ��
																
															}*/

															dialog.dismiss();
														}
													}).show();
										} else if (which == 2) {
											//Ⱥ��
											
										}

										dialog.dismiss();
									}
								}).show();
			}

		});

	}
	public String[] getPeopleFromTable(String s) throws IOException{
		
		Table table = new Table(s.trim(),null,null);
		List<Map<String, Object>> list = table.getList();
		String peopleString[] = new String[list.size()];
		Log.d("numnumnum!!!",String.valueOf(list.size()));
		for(int i = 0;i<list.size();i++){
			peopleString[i] = list.get(i).get("name").toString();
		}
		return peopleString;
	}
	
	// ����Ŀ¼���������еı�
		public List<File> getFile() {
			List<File> listFile = new ArrayList<File>();
			File allFiles[] = new File("/sdcard/data/data/mesapp").listFiles();
			// Log.d("������������������������",".................");
			for (int i = 0; i < allFiles.length; i++) {
				File file = allFiles[i];
				// Log.d("������������������������","!!!!!!!!!");
				if (file.isFile()) {
					listFile.add(file);
				}
			}
			return listFile;
		}
	public String[] getTable(){
		
		List<File> listFile = getFile();
		String table[] = new String[listFile.size()];
		for (int i = 0;i<table.length;i++){
			table[i] = listFile.get(i).getName().toString().trim();
		}
		return table;
	}
	public String[] getNumByName(String number){
		String num[] ;
		Cursor cursor = context
				.getContentResolver()
				.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" = ?",
						new String[] {number}, null);
		Log.d("��������", ".=.=.=.=.==.");
		num = new String[cursor.getCount()];
		for (int j = 0; j < cursor.getCount(); j++) {
			Log.d("����", "=.=.=.=.=");
			cursor.moveToPosition(j);
			
			Log.d("����", ".=.=.=.=.=.");
				Log.d("number!!!", "��Ԫ������ȣ�");
				num[j] = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			//sendMessage(num);
		}//for
		cursor.close();
        return num; 
	}
	public void sendMessage(String num[]){
		String smsContent = messageEditText.getText().toString();
		SmsManager smsManager = SmsManager.getDefault();
		Log.d("����", "=========");
		if (smsContent.length() > 70) {
			List<String> contents = smsManager.divideMessage(smsContent);
			for (String c : contents) {
				for (int i = 0; i < num.length; i++) {
					Log.d("sms4",smsContent );
					smsManager.sendTextMessage(num[i], null, c, null,
							null);
					
				}
			}
		} else {
			for (int i = 0; i < num.length; i++) {
				Log.d("sms2",smsContent );
				smsManager.sendTextMessage(num[i], null, smsContent,
						null, null);
			}
		}
		Log.d("sms3",smsContent );
		createDialog("�����ѷ���");
	
	}
	public void createDialog(String text){
		Log.d("�Ի��򣡣���", "=.=.=.==.=");
		final Dialog dialog = new Dialog(MessageActivity.this);
		dialog.setTitle(text);
		dialog.show();
		android.os.Handler hander = new android.os.Handler();
		// �趨��ʱ�������趨ʱ���ʹ�Ի���ر�
		hander.postDelayed(new Runnable() {
			@Override
			public void run() {
				dialog.dismiss();
			}
		}, 500);
	
	}
	public List<String> initNum(String str) {
		List list = new ArrayList<String>();
		// �����ֺ����ֺ����ֻ���л�ȡ����
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			String srcStr = matcher.group(); // ����õ���ip
			list.add(srcStr); // ��ƥ��Ľ�����List

		}
		return list;
	}
	
	 /*public boolean onKeyDown(int keyCode, KeyEvent event) { 
		 if (keyCode == KeyEvent.ACTION_DOWN) { 
			// Intent intent = new Intent(MessageActivity.this,MainActivity.class); startActivity(intent); }
		 }
		 return false;
	  }
	*/ 
}
