package followheart.adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import followheart.activities.R;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;

public class TableListAdapter extends BaseAdapter {
	private List<Map<String, Object>> list;// ��������ע
	public List<Map<String, Object>> getList() {
		
		for(int i = 0;i<list.size();i++){
			if(list.get(i).get("note").equals("")){
				list.get(i).remove("note");
				list.get(i).put("note","傻逼");
			}

			if(list.get(i).get("name").equals("")){
				list.remove(i);
			}
			
		}

		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	private LayoutInflater mInflater;
	private int layoutID;
	private String flag[];
	private int ItemIDs[];

	public TableListAdapter(Context context, List<Map<String, Object>> list,
			int layoutID, String flag[], int ItemIDs[]) {
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
		this.layoutID = layoutID;
		this.flag = flag;
		this.ItemIDs = ItemIDs;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Log.d("have a try!",String.valueOf(list.size()));
		
		convertView = mInflater.inflate(layoutID, null);
		if(convertView == null){
			Log.d("convert!!!!","=========");
		}
		for (int i = 0; i < flag.length; i++) {
			if (convertView.findViewById(ItemIDs[i]) instanceof EditText) {
				// ��ȡlist�ص���ݣ�������ص�listview
				EditText iv = (EditText) convertView.findViewById(ItemIDs[i]);
				iv.setText((String) list.get(position).get(flag[i]));
				
			} else if (convertView.findViewById(ItemIDs[i]) instanceof Button) {
				// ��ȡisAttendedList�е���ݣ�������ص�listview
				Button tv = (Button) convertView.findViewById(ItemIDs[i]);
				if(list.get(position).get("isAttended").toString().equals("1")){
					tv.setBackgroundResource(R.drawable.yes);
				}else{
					tv.setBackgroundResource(R.drawable.no);
				}
				/*if ((Integer) list.get(position).get(flag[i]) == 1) {
					// tv.setBackgroundColor("#00ffff");

				}
			} else {

			}*/
			}
		}
		addlistener(convertView, position);
		notifyDataSetChanged();
		return convertView;
	}

	// ����ť
	public void addlistener(final View convertView, final int position) {
		//name
		((EditText) convertView.findViewById(R.id.name))
				.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// TODO Auto-generated method stub

					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
						// TODO Auto-generated method stub

					}

					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub

						Map<String, Object> map = new HashMap<String, Object>();
						map.put("name", s.toString().trim());
						map.put("note",list.get(position).get("note"));
						map.put("isAttended",list.get(position).get("isAttended"));
						map.put("phoneNumber",list.get(position).get("phoneNumber"));

						Log.d("before",s.toString());
						list.set(position, map);
						//Log.d("after","222======");
						notifyDataSetChanged();

					}
				});
		
		((EditText) convertView.findViewById(R.id.note))
				.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// TODO Auto-generated method stub

					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
						// TODO Auto-generated method stub

					}

					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("name", list.get(position).get("name"));
						map.put("note", s.toString().trim());
						map.put("isAttended",
								list.get(position).get("isAttended"));
						map.put("phoneNumber",list.get(position).get("phoneNumber"));

						Log.d("before",s.toString());
						list.set(position, map);
						notifyDataSetChanged();
						//Log.d("after","444=======");
						

					}
				});
		((Button) convertView.findViewById(R.id.isattended))
				.setOnClickListener(new OnClickListener() {
					// ����ť���ı�isAttendedList������
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						//�ٶ����е�isAttended����Ϊ0��û�е���
						if(list.get(position).get("isAttended").equals("0")){
							list.get(position).remove("isAttended");
							list.get(position).put("isAttended","1");
							((Button) convertView.findViewById(R.id.isattended)).setBackgroundResource(R.drawable.yes);
						}else {
							list.get(position).remove("isAttended");
							list.get(position).put("isAttended","0");
							((Button) convertView.findViewById(R.id.isattended)).setBackgroundResource(R.drawable.no);
						}
					}
				});
	}
}
