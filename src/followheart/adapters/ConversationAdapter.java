package followheart.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import followheart.activities.R;
import followheart.entities.SMSGroupInfo;

import android.R.string;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class ConversationAdapter extends BaseAdapter {
	private HashMap<String, SMSGroupInfo> groupMap;
	private Activity activity;
	private Cursor cursor;
	private ArrayList<SMSGroupInfo> groupInfos;

	private ViewHolder viewHolder;
	// private List<HashMap<K, V>>
	// private int flag;
	private ArrayList<String> groupIDs = new ArrayList<String>();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");

	public ConversationAdapter(Activity activity,
			ArrayList<SMSGroupInfo> groupInfos) {
		// this.groupMap = new HashMap<String, SMSGroupInfo>();
		this.activity = activity;
		// this.cursor = cursor;
		// initDate();
		this.groupInfos = groupInfos;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		// return groupMap.size();
		return groupInfos.size();
	}

	@Override
	public SMSGroupInfo getItem(int arg0) {
		// TODO Auto-generated method stub
		// return groupMap.get(groupIDs.get(arg0));
		return groupInfos.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		
		// convertView.setTag(viewHolder);

		SMSGroupInfo smsGroupInfo = groupInfos.get(position);

		RelativeLayout item = (RelativeLayout) activity.getLayoutInflater()
				.inflate(R.layout.conversation_listview_item, null);
		
		TextView contactNameTv = (TextView) item
				.findViewById(R.id.tv_number_or_name);
		TextView msgCountTv = (TextView) item
				.findViewById(R.id.tv_msg_count);
		
		ImageView contactImg = (ImageView) item
				.findViewById(R.id.contact_photo);
		TextView msgBodyTv = (TextView) item
				.findViewById(R.id.tv_msg_body);
		TextView msgDateTv = (TextView) item.findViewById(R.id.tv_date);

		// RelativeLayout number_count_mark =
		// (RelativeLayout)item.getChildAt(1);
		//
		// RelativeLayout msg_body_date = (RelativeLayout)item.getChildAt(2);

		String number = "";
		int type = smsGroupInfo.getType();
		int read = smsGroupInfo.getRead();
		if (smsGroupInfo.getContactNames().size() > 1) {
					contactImg.setImageResource(R.drawable.default_contact_photo);
			for (int i = 0; i < smsGroupInfo.getContactNames().size(); i++) {
				if (i == smsGroupInfo.getContactNames().size() - 1) {
					number += smsGroupInfo.getContactNames().get(i);
				} else {
					number += smsGroupInfo.getContactNames().get(i) + ";";
				}

			}
		} else {
			number = smsGroupInfo.getContactNames().get(0);
			// image.setImageBitmap(smsGroupInfo.getContactPhoto());
			contactImg.setBackgroundResource(R.drawable.default_contact_photo);
		}

		contactNameTv.setText(number);
		msgBodyTv.setText(smsGroupInfo.getBody());
		msgCountTv.setText("(" + smsGroupInfo.getMsg_count() + ")");

		// if (tbody != null) {
		// tbody.setText(smsGroupInfo.getBody());
		// }
		// if (tnum != null) {
		// tnum.setText(number);
		// }
		// tv_msg_count.setText("(" + smsGroupInfo.getMsg_count() + ")");
		msgDateTv.setText(smsGroupInfo.getDate());

		if (type == 3) {
			ImageView markImg = (ImageView) item.findViewById(R.id.iv_mark);
			markImg.setBackgroundResource(R.drawable.draft_bg);
		} else if (type != 3 && read == 0) {
			ImageView markImg = (ImageView) item.findViewById(R.id.iv_mark);
            markImg.setBackgroundResource(R.drawable.new_msg_bg);
		}
		else if (type == 5) {
			ImageView markImg = (ImageView) item.findViewById(R.id.iv_mark);
			markImg.setBackgroundResource(R.drawable.warning);
		}

		return item;
		// return composeItem(arg0);
	}

    private View composeItem(int position){  
//      cursor.moveToPosition(position);
//        SMSGroupInfo smsGroupInfo = (SMSGroupInfo) groupMap.get(groupIDs.get(position));
  
//      String date =  dateFormat.format(cursor.getLong(cursor.getColumnIndex("last_date")));
//		 String body = cursor.getString(cursor.getColumnIndex("last_msg"));
//		 String number = cursor.getString(cursor.getColumnIndex("contact"));
//      String msg_count = "("  + cursor.getString(cursor.getColumnIndex("msg_count")) + ")";
//      int type = Integer.parseInt(cursor.getString(cursor.getColumnIndex("type")));
//      int read = Integer.parseInt(cursor.getString(cursor.getColumnIndex("read")));
      
 	  SMSGroupInfo smsGroupInfo = (SMSGroupInfo) groupInfos.get(position);
 	 
      RelativeLayout item = (RelativeLayout)activity.getLayoutInflater().inflate(R.layout.conversation_listview_item, null);  
        
      RelativeLayout number_count_mark = (RelativeLayout)item.getChildAt(1);
      
      RelativeLayout msg_body_date = (RelativeLayout)item.getChildAt(2);
      
      TextView tnum = (TextView)number_count_mark.getChildAt(0);
      TextView tv_msg_count = (TextView)number_count_mark.getChildAt(1);
      ImageView iv_mark = (ImageView) number_count_mark.getChildAt(2);
      ImageView image = (ImageView)item.getChildAt(0);    
      TextView tbody = (TextView)msg_body_date.getChildAt(1);
      TextView tv_date = (TextView) msg_body_date.getChildAt(0);
      String number = "";
      int type = smsGroupInfo.getType();
      int read = smsGroupInfo.getRead();
      if (smsGroupInfo.getContactNames().size() > 1) {
     	 image.setImageResource(R.drawable.ic_launcher);  
     	 for (int i = 0; i < smsGroupInfo.getContactNames().size(); i++) {
     		 if (i == smsGroupInfo.getContactNames().size() - 1) {
					number += smsGroupInfo.getContactNames().get(i);
				}
     		 else {
     			 number += smsGroupInfo.getContactNames().get(i) + ";";
				}
  			
  		}
		}
      else
      {
     	 number = smsGroupInfo.getContactNames().get(0);
//     	 image.setImageBitmap(smsGroupInfo.getContactPhoto());
     	 image.setBackgroundResource(R.drawable.ic_launcher);
      }
     
      if(tbody != null){  
          tbody.setText(smsGroupInfo.getBody());  
      }   
      if(tnum != null){  
          tnum.setText(number);  
      }
      tv_msg_count.setText("(" + smsGroupInfo.getMsg_count() + ")");
      tv_date.setText(smsGroupInfo.getDate());
      
      if (type == 3) {
			iv_mark.setBackgroundResource(R.drawable.warning);
		 }
      else if(type != 3 && read == 0){
			iv_mark.setBackgroundResource(R.drawable.favorite);
		}
      
      
        
      return item;  
  }  
	// private View composeItem(int position){
	// // cursor.moveToPosition(position);
	// // SMSGroupInfo smsGroupInfo = (SMSGroupInfo)
	// groupMap.get(groupIDs.get(position));
	//
	// // String date =
	// dateFormat.format(cursor.getLong(cursor.getColumnIndex("last_date")));
	// // String body = cursor.getString(cursor.getColumnIndex("last_msg"));
	// // String number = cursor.getString(cursor.getColumnIndex("contact"));
	// // String msg_count = "(" +
	// cursor.getString(cursor.getColumnIndex("msg_count")) + ")";
	// // int type =
	// Integer.parseInt(cursor.getString(cursor.getColumnIndex("type")));
	// // int read =
	// Integer.parseInt(cursor.getString(cursor.getColumnIndex("read")));
	//
	// }

	// public void setCursor(Cursor cursor)
	// {
	// this.cursor = cursor;
	// }
	//
	// public Cursor getCursor()
	// {
	// return this.cursor;
	// }

	public void setGroupInfos(ArrayList<SMSGroupInfo> smsGroupInfos) {
		this.groupInfos = smsGroupInfos;
	}

	public ArrayList<SMSGroupInfo> getGroupInfos() {
		return this.groupInfos;
	}

	// public void setFlag(int flag)
	// {
	// this.flag = flag;
	// }


}
