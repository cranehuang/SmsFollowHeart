package followheart.activities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import followheart.activities.MainActivity;
import followheart.adapters.TableListAdapter;
import followheart.factory.Table;
import android.os.Bundle;
import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TableActivity extends Activity {
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();// ��������ע
	private EditText myTableName;
	private ListView nameList;
	Bundle bundle;
	TableListAdapter adapter;
	private String name[];
	private String isAttended[];
	private String more[];
	private String phoneNumber[];


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ��ȥ��������Ӧ�ó�������֣�
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_table);
		//bundle = new Bundle();
		if (getIntent().getExtras() != null) {
			bundle = getIntent().getExtras();
		}else {
			bundle = null;
		}
		// ������
		myTableName = (EditText) this.findViewById(R.id.mytablename);
		nameList = (ListView) findViewById(R.id.namelist);
		Button addpersonbButton = (Button) findViewById(R.id.newperson);
		myTableName.setText(bundle.getString("tableName"));

		// ������
		try {
			if(getData()!=null){
			try {
				adapter = new TableListAdapter(this, getData(), R.layout.table_listitem,
						new String[] { "name", "isAttended", "note" }, new int[] {
								R.id.name, R.id.isattended, R.id.note });
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			nameList.setAdapter(adapter);
			}else{
				adapter = new TableListAdapter(this, new ArrayList<Map<String,Object>>()
				, R.layout.table_listitem,
						new String[] { "name", "isAttended", "note" }, new int[] {
								R.id.name, R.id.isattended, R.id.note });
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// nameList删除
		nameList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				// 弹出对话框询问是否确定
				AlertDialog.Builder builder = new AlertDialog.Builder(
						TableActivity.this);
				builder.setMessage("是否删除")
						.setCancelable(true)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										// TODO Auto-generated method stub
										// 删除某人
										dialog.cancel();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										// TODO Auto-generated method stub
										// 取消操作
										dialog.cancel();
									}
								});
				AlertDialog shareDialog = builder.create();
				shareDialog.show();
				return true;
			}
		});
		// 添加人按钮
		addpersonbButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 添加新的item,
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", "");
				// 1为到，0为没有到
				map.put("phoneNumber", "110");
				map.put("isAttended", "0");
				map.put("note", "");

				list.add(map);
				adapter = new TableListAdapter(TableActivity.this, list,
						R.layout.table_listitem, new String[] { "name",
								"isAttended", "note" }, new int[] { R.id.name,
								R.id.isattended, R.id.note });
				nameList.setAdapter(adapter);
				// adapter.notifyDataSetChanged();

			}
		});

	}
	
	public List<Map<String, Object>> getData() throws IOException {
		// table :�ĸ��ֶ�id,phonenumber,coming,more

		Table table = new Table(bundle.getString("tableName"), null, null);
		Log.d("out!!!!!!", "=====");
		list = table.getList();
		for(int i = 0;i<list.size();i++){
			if(list.get(i).get("note").equals("傻逼")){
				list.get(i).remove("note");
				list.get(i).put("note","");
			}
		}

		return list;
	}

	public void saveDataFromAdapter() {
		adapter.notifyDataSetChanged();
		if (adapter.getList() == null) {
			name = null;
			isAttended = null;
			more = null;
			
		} else {
			// Log.d("GGGGG",adapter.getList().get(0).get("name").toString());
			name = new String[adapter.getList().size()];
			for (int i = 0; i < name.length; i++) {
				name[i] = adapter.getList().get(i).get("name").toString();
			}
			isAttended = new String[adapter.getList().size()];
			for (int i = 0; i < isAttended.length; i++) {
				isAttended[i] = adapter.getList().get(i).get("isAttended")
						.toString();
			}
			more = new String[adapter.getList().size()];
			for (int i = 0; i < more.length; i++) {
				more[i] = adapter.getList().get(i).get("note").toString();
			}
			phoneNumber = new String[adapter.getList().size()];
			for(int i = 0;i<phoneNumber.length;i++){
				phoneNumber[i] = adapter.getList().get(i).get("phoneNumber").toString();
			}

		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 询问是否保存
			new AlertDialog.Builder(this)
					.setTitle("确认")
					.setMessage("是否保存修改")
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									//saveDataFromAdapter();
									// if(bundle.getString("tableName")==null){
									/*if(bundle==null){
									File file = new File(
											"/sdcard/data/data/mseapp/"
													+ bundle.getString(
															"tableName")
															.toString().trim());
									}*/
									/*Log.d("title!!!!!!",
											bundle.getString("tableName")
													.toString().trim());
									if (file.exists()) {
										file.delete();
									}
									if (!file.exists()) {
										Log.d("delete!!!!!!", "已经删除了！！！！！！1");
									}*/
									dialog.dismiss();
									try {
										if (myTableName.getText().toString()
												.equals("")) {
											saveTable("未命名");
										} else {
											saveTable(myTableName.getText()
													.toString());
										}
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									/*
									 * }else { File file = new
									 * File("/sdcard/data/data/mseapp/"
									 * +bundle.getString("tableName"));
									 * //file.delete(); File file2 = new
									 * File("/sdcard/data/data/mseapp/"
									 * +myTableName.getText().toString());
									 * file.renameTo(file2); //Log.d("aaaaaaaa",
									 * file.getName()); }
									 */
									Intent intent = new Intent(
											TableActivity.this,
											MainActivity.class);
									startActivity(intent);
									TableActivity.this.finish();
								}
							})
					.setNegativeButton("否",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									Intent intent = new Intent(
											TableActivity.this,
											MainActivity.class);
									startActivity(intent);
									TableActivity.this.finish();
								}
							}).show();
		} else {

			// 直接保存
		}
		return false;
	}

	public void getkj(int item) {
		String s = (String) this.adapter.getItem(item);
	}


	// ע��id��name
	public void saveTable(String tableName) throws IOException {
		// Log.d("save!!!!!",name[1]);
		/*File file = new File("/sdcard/data/data/mseapp/"
				+ bundle.getString("tableName").toString().trim());
		if (file.exists()) {
			file.delete();
		}*/
		saveDataFromAdapter();
		Table table = new Table(tableName, "table" + bundle.getInt("tableNum")
				+ 1, "2013", name, new String[100], isAttended, more);
		/*
		 * if(tableName.equals(bundle.getString("tableName").toString().trim())){
		 * file.delete(); }
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.table, menu);
		return true;
	}

}
