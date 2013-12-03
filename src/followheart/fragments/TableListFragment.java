package followheart.fragments;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import followheart.activities.R;
import followheart.activities.TableActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class TableListFragment extends Fragment {

	private static View mView;
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	List<Map<String, Boolean>> checkboxList = new ArrayList<Map<String, Boolean>>();
	private int tableNumber;
	ListView tablelist;
	Button newtable_btn;
	SimpleAdapter adapter;

	public static final TableListFragment newInstance() {
		TableListFragment f = new TableListFragment();

		Bundle b = new Bundle();
		f.setArguments(b);

		return f;
	}

	// �Զ����õ�onCreateView
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mView = inflater.inflate(R.layout.activity_tablelist, container, false);
		tablelist = (ListView) mView.findViewById(R.id.tablelist);
		newtable_btn = (Button) mView.findViewById(R.id.newtable_btn);

		// �����½����ť����ת�����ҳ��
		newtable_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putInt("tableNumber", tableNumber);
				// String s = "";
				// bundle.putString("tableName",);
				String s = "未命名";
				bundle.putString("tableName",s);

				Intent intent = new Intent();
				intent.setClass(getActivity(), TableActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 0);
				getActivity().finish();
			}
		});
		// �������б�
		adapter = new SimpleAdapter(getActivity(), getTableListData(),
				R.layout.tablelist_listitem,
				new String[] { "tablename", "date" }, new int[] {
						R.id.tablename, R.id.date });
		tablelist.setAdapter(adapter);
		// ���tablelist��ת������ı��
		tablelist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putInt("tableNumber", tableNumber);
				bundle.putString("tableName", getFile().get(arg2).getName());
				Intent intent = new Intent();
				intent.setClass(getActivity(), TableActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 0);
				getActivity().finish();
			}
		});

		// 长按分享弹出对话框
		tablelist.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				// TODO Auto-generated method stub

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setMessage("分享至")
						.setCancelable(true)
						.setPositiveButton("删除",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										// TODO Auto-generated method stub
										File file = new File(
												"/sdcard/data/data/mesapp/"
														+ getFile().get(arg2)
																.getName());
										file.delete();
										adapter = new SimpleAdapter(
												getActivity(),
												getTableListData(),
												R.layout.tablelist_listitem,
												new String[] { "tablename",
														"date" }, new int[] {
														R.id.tablename,
														R.id.date });
										tablelist.setAdapter(adapter);
										dialog.cancel();

									}
								})
						.setNegativeButton("重命名",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										// TODO Auto-generated method stub

										dialog.cancel();
										final EditText renameEditText = new EditText(
												getActivity());
										new AlertDialog.Builder(getActivity())
												.setTitle("请输入")
												.setIcon(
														android.R.drawable.ic_dialog_info)
												.setView(renameEditText)
												.setPositiveButton(
														"确定",
														new DialogInterface.OnClickListener() {

															@Override
															public void onClick(
																	DialogInterface dialog,
																	int id) {
																File file = new File(
																		"/sdcard/data/data/mesapp/"
																				+ getFile()
																						.get(arg2)
																						.getName());
																File file2 = new File(
																		"/sdcard/data/data/mesapp/"
																				+ renameEditText
																						.getText());
																Log.d("ccccccccc",
																		file2.getName());
																file.renameTo(file2);
																Log.d("ddddddddd",
																		file.getName());
																adapter = new SimpleAdapter(
																		getActivity(),
																		getTableListData(),
																		R.layout.tablelist_listitem,
																		new String[] {
																				"tablename",
																				"date" },
																		new int[] {
																				R.id.tablename,
																				R.id.date });
																tablelist
																		.setAdapter(adapter);
															}
														})
												.setNegativeButton("取消", null)
												.show();
									}
								})
						.setNeutralButton("分享",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										// TODO Auto-generated method stub
										dialog.cancel();
									}
								});
				AlertDialog shareDialog = builder.create();
				shareDialog.show();
				return true;
			}

		});

		return mView;
	}

	// ��ȡ���ڱ��б���ƣ���ţ��������ڲ�����
	private List<Map<String, Object>> getTableListData() {
		List<Map<String, Object>> tablelist = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		// Log.d("������������������������",".................");
		List<File> list = getFile();
		tableNumber = getFile().size();
		for (int i = 0; i < list.size(); i++) {
			map = new HashMap<String, Object>();
			map.put("tablename", list.get(i).getName());
			// ��ȡ�ļ�����޸�ʱ��
			Calendar cal = Calendar.getInstance();
			long time = list.get(i).lastModified();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal.setTimeInMillis(time);
			map.put("date", formatter.format(cal.getTime()));
			tablelist.add(map);
		}

		return tablelist;
	}

	// ����Ŀ¼���������еı�
	public List<File> getFile() {
		File f1 = new File("/sdcard/data");

		if(!f1.exists()){
			f1.mkdir();
		}
		File f2 = new File("/sdcard/data/data");
		if(!f2.exists()){
			f2.mkdir();
		}
		File f3 = new File("/sdcard/data/data/measapp");
		if(!f3.exists()){
			try {
				f3.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		List<File> listFile = new ArrayList<File>();
		File allFiles[] = new File("/sdcard/data/data/mesapp").listFiles();
		// Log.d("������������������������",".................");
		if(allFiles != null){
			for (int i = 0; i < allFiles.length; i++) {
				File file = allFiles[i];
				// Log.d("������������������������","!!!!!!!!!");
				if (file.isFile()) {
					listFile.add(file);
				}
			}
		}

		
		return listFile;
	}

	public int getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(int tableNumber) {
		this.tableNumber = tableNumber;
	}

}