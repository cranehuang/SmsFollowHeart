package followheart.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import followheart.activities.R;
import followheart.entities.ContactInfo;
import followheart.entities.GroupInfo;
import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GroupAdapter extends BaseExpandableListAdapter{
	
	private List<GroupInfo> contactGroups;
	private List<List<ContactInfo>> contactsList;
	private Context context;
	private Handler mHandler;
	public static int SELECTION_CHANGED = 0;
	// 用来控制CheckBox的选中状况
    private static List<HashMap<Integer,Boolean>>  isSelected;
    
    private static HashMap<Integer, Boolean> groupIsSelected;//记录某个group是否被选中
	
	public GroupAdapter(Context context ,Handler handler, List<GroupInfo> contactGroups,List<List<ContactInfo>> contactsList)
	{
		this.contactGroups = contactGroups;
		this.mHandler = handler;
		this.contactsList = contactsList;
		this.context = context;
		isSelected = new ArrayList<HashMap<Integer,Boolean>>();
		groupIsSelected = new HashMap<Integer, Boolean>();
		initDate();
	}
	
	 // 初始化isSelected的数据
    private void initDate(){
        for(int i = 0; i < contactsList.size() ; i ++) {
//        	System.out.println("---------> i = " + i);
        	groupIsSelected.put(i, false);
        	HashMap<Integer, Boolean> hashMap = new HashMap<Integer, Boolean>();
        	for (int j = 0; j < contactsList.get(i).size(); j++) {
        		hashMap.put(j, false);
			}
        	getIsSelected().add(hashMap);
        }
    }

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return contactsList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		  Bitmap contactPhoto = (Bitmap) ((ContactInfo)getChild(groupPosition,childPosition)).getContactPhoto();
		  String contactName = (String) ((ContactInfo)getChild(groupPosition,childPosition)).getContactName();
		  String phoneNumber = (String) ((ContactInfo)getChild(groupPosition,childPosition)).getPhoneNumber();
		    
		  LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		  
//		  RelativeLayout layout = (RelativeLayout) layoutInflater.inflate(R.layout.conlist_item, null);
//		  ImageView contactPhoto_iv = (ImageView) layout.findViewById(R.id.item_iv);
//		  TextView phoneNum_tv = (TextView) layout.findViewById(R.id.item_tv);
//		  CheckBox drugDetailTextView = (CheckBox) layout.findViewById(R.id.item_cb);
	  
		  ViewHolder holder = null;
          if (convertView == null) {
              // 获得ViewHolder对象
              holder = new ViewHolder();
               // 导入布局并赋值给convertview
              convertView = layoutInflater.inflate(R.layout.conlist_item, null);
              holder.tv = (TextView) convertView.findViewById(R.id.item_tv);
              holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
              holder.iv = (ImageView) convertView.findViewById(R.id.item_iv);
              // 为view设置标签
              convertView.setTag(holder);
          } else {
             // 取出holder
             holder = (ViewHolder) convertView.getTag();
          }

          // 设置list中TextView的显示
          holder.tv.setText(contactName + "\n" + phoneNumber);
//           根据isSelected来设置checkbox的选中状况
          holder.cb.setChecked(getIsSelected().get(groupPosition).get(childPosition));
          holder.iv.setImageBitmap(contactPhoto);
		  
		  return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return contactsList.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return contactGroups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return contactGroups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		RelativeLayout groupLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.group_item, null);
		TextView textView = (TextView) groupLayout.findViewById(R.id.group_title_tv);
		Button button = (Button) groupLayout.findViewById(R.id.iSelection_btn);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int childSum = getChildrenCount(groupPosition);
				if (groupIsSelected.get(groupPosition)) {
					groupIsSelected.put(groupPosition, false);
					for (int i = 0; i < childSum; i++) {
//						System.out.println("------------->333333333");
						GroupAdapter.getIsSelected().get(groupPosition).put(i, false);
					}
				}
				else {
					groupIsSelected.put(groupPosition, true);
					for (int i = 0; i < childSum; i++) {
//						System.out.println("------------->333333333");
						GroupAdapter.getIsSelected().get(groupPosition).put(i, true);
					}
				}
				
				sendMessage(groupPosition,childSum,groupIsSelected.get(groupPosition));
				notifyDataSetChanged();
				
			}
		});
		String groupID = ((GroupInfo)getGroup(groupPosition)).getId();
		String groupTilte = ((GroupInfo)getGroup(groupPosition)).getTitle();
		textView.setText(groupTilte);
		return groupLayout;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
//		
		return true;
	}
	
	public static List<HashMap<Integer,Boolean>>  getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(List<HashMap<Integer,Boolean>> isSelected) {
        GroupAdapter.isSelected = isSelected;
    }
    
    private void sendMessage(int groupPosition ,int childSum , boolean isSelected)
    {
    	Message msg = new Message();
    	msg.what = SELECTION_CHANGED;
    	msg.arg1 = groupPosition;
    	msg.arg2 = childSum;
    	msg.obj = isSelected;
    	mHandler.sendMessage(msg);
    }
    
    private TextView getTextView()
    {
    	AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , 64);
    	TextView textView = new TextView(context);
    	textView.setLayoutParams(layoutParams);
    	textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
    	textView.setPadding(59, 0, 0, 0);
    	textView.setTextSize(20);
    	return textView;
    }
    
    public static HashMap<Integer, Boolean> getGroupIsSelected()
    {
    	return groupIsSelected;
    }

}
