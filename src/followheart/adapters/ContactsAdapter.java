package followheart.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import followheart.activities.R;
import followheart.entities.ContactInfo;
import android.R.integer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactsAdapter extends BaseAdapter {

	// 填充数据的list
    private ArrayList<ContactInfo> list;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer,Boolean> isSelected;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;
    
    public ContactsAdapter(ArrayList<ContactInfo> list, Context context)
    {
    	this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
//        isSelected = new HashMap<Integer, Boolean>();
//        initDate();
    }
	
    // 初始化isSelected的数据
//    private void initDate(){
//        for(int i=0; i<list.size();i++) {
//            getIsSelected().put(i,false);
//        }
//    }
    
    @Override
    public int getCount() {
        return list.size();
    }
    
//    public String getPY(int position)
//    {
//    	return list.get(position).getPY();
//    }

    @Override
    public ContactInfo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
            if (convertView == null) {
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.conlist_item, null);
            holder.tv = (TextView) convertView.findViewById(R.id.item_tv);
            holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
            holder.iv = (ImageView) convertView.findViewById(R.id.item_iv);
            // 为view设置标签
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
            }

//        System.out.println("------------>convsersationAdapter.position = " + position);
        // 设置list中TextView的显示
        holder.tv.setText(list.get(position).getContactName() + "\n" + list.get(position).getPhoneNumber());
        // 根据isSelected来设置checkbox的选中状况
        holder.cb.setChecked(list.get(position).hasBeSelected());
        holder.iv.setImageBitmap((list.get(position).getContactPhoto()));
        return convertView;
    }

//    public static HashMap<Integer,Boolean> getIsSelected() {
//        return isSelected;
//    }

    public static void setIsSelected(HashMap<Integer,Boolean> isSelected) {
        ContactsAdapter.isSelected = isSelected;
    }

//    public void updateListView(ArrayList<ContactInfo> contactInfos)
//    {
////    	this.list.clear();
////    	this.list.addAll(contactInfos);
//    	this.list = contactInfos;
//    	notifyDataSetChanged();
//    }
    
    public List<ContactInfo> getList()
    {
    	return this.list;
    }
    
//    public void addMoreItems(List<ContactInfo> newItems) {
//		this.list.addAll(newItems);
//		notifyDataSetChanged();
//	}
//
//	public void removeAllItems() {
//		this.list.clear();
//		notifyDataSetChanged();
//	}


}
