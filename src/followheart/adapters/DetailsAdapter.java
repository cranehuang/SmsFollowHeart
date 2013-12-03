package followheart.adapters;

import java.text.SimpleDateFormat;


import followheart.activities.R;
import followheart.entities.SMSConstant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class DetailsAdapter extends BaseAdapter{

	private Context context;
	private Cursor cursor;
	private String smsInDraft = null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd,HH:mm");
	
	public DetailsAdapter(Context context , Cursor cursor)
	{
		this.context = context;
		this.cursor = cursor;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cursor.getCount();
	}

	@Override
	public Object getItem(int index) {
		// TODO Auto-generated method stub
		return cursor.getString(index);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return composeItem(arg0);
	}
	
	private View composeItem(int position){  
        cursor.moveToPosition(position);
        RelativeLayout item = null;
        TextView tbody = null;
        TextView tv_date = null;
        ProgressBar sendProgressBar = null;
		String	body = cursor.getString(cursor.getColumnIndex(SMSConstant.BODY));
		String date = dateFormat.format(cursor.getLong(cursor.getColumnIndex(SMSConstant.DATE)));
		int	type =Integer.parseInt( cursor.getString(cursor.getColumnIndex(SMSConstant.TYPE)));
		
		
		
        if (type == SMSConstant.MESSAGE_TYPE_INBOX) {
			item = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.detail_item_left, null);
			tbody = (TextView)item.findViewById(R.id.bodyleft);
			tv_date = (TextView) item.findViewById(R.id.dateleft);
			tv_date.setText(date);
		}
//        else if(type == SMSConstant.MESSAGE_TYPE_DRAFT){
//        	smsInDraft = body;
//			return null;
//		}
//          
        else {
        	item = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.detail_item_right, null);
        	RelativeLayout smsBodyLayout = (RelativeLayout) item.findViewById(R.id.smsbody_prompt);
//        	smsBodyLayout.
//        	RelativeLayout smsBodyLayout = (RelativeLayout) item.getChildAt(1);
//        	tbody = (TextView) smsBodyLayout.getChildAt(0);
        	tbody = (TextView) item.findViewById(R.id.bodyright);
        	tv_date = (TextView) item.findViewById(R.id.dateright);
        	if (type == SMSConstant.MESSAGE_TYPE_FAILED) {
//        		TextView tv_prompt = (TextView) item.findViewById(R.id.send_prompt);
//        		tv_prompt.setVisibility(View.GONE);
        		tv_date.setText(date);
//        		sendProgressBar = (ProgressBar) item.findViewById(R.id.send_sms_progressbar);
//        		sendProgressBar.setVisibility(View.GONE);
				ImageView imageView = (ImageView) item.findViewById(R.id.warning);
				imageView.setBackgroundResource(R.drawable.warning);
			}
        	else if (type == SMSConstant.MESSAGE_TYPE_OUTBOX) {
//        		TextView tv_prompt = (TextView) item.findViewById(R.id.send_prompt);
        		tv_date.setText("正在发送...");
        		sendProgressBar = (ProgressBar) item.findViewById(R.id.send_sms_progressbar);
        		sendProgressBar.setVisibility(View.VISIBLE);
//        		tv_prompt.setVisibility(View.VISIBLE);
//        		tv_prompt.setText("正在发送...");
			}
        	else {
//        		sendProgressBar = (ProgressBar) item.findViewById(R.id.send_sms_progressbar);
//        		sendProgressBar.setVisibility(View.GONE);
        		tv_date.setText(date);
			}
		}  
       
//        ImageView image = (ImageView)item.getChildAt(0);   
        
        if(tbody != null){  
            tbody.setText(body);  
        }
        if (tv_date != null) {
//			tv_date.setText(date);
		}
//        image.setImageResource(R.drawable.ic_launcher);  
          
        return item;  
    }  
	
	public void setCursor(Cursor cursor)
	{
		this.cursor = cursor;
	}
	public Cursor getCursor()
	{
		return this.cursor;
	}
	public String getSMSInDraft()
	{
		if (smsInDraft != null) {
			return this.smsInDraft;
		}
		else {
			return null;
		}
	}
}
