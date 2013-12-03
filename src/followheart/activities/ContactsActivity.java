package followheart.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import followheart.activities.R;
import followheart.adapters.ContactsAdapter;
import followheart.adapters.SectionListAdapter;
import followheart.adapters.SectionListView;
import followheart.adapters.ViewHolder;
import followheart.entities.ContactInfo;
import followheart.utils.ComparatorPY;
import followheart.utils.PinyinUtils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/*
 *  @Author: Bhavya
 */

public class ContactsActivity extends Activity 
{
	private ArrayList<ContactInfo> filterContacts = new ArrayList<ContactInfo>();
	private SearchContactTask curSearchContactTask = null;
	private Object searchLock = new Object();
	private boolean inSearchMode = false;
//	private ArrayList<ArrayList<ContactInfo>> groupArrayList = new ArrayList<ArrayList<ContactInfo>>();
	public ArrayList<ContactInfo> contacts = new ArrayList<ContactInfo>();
	public ArrayList<String> contactsPinyin = new ArrayList<String>();
	public static final int PHONES_DISPLAY_NAME_INDEX = 0; 
    public static final int PHONES_NUMBER_INDEX = 1; 
    public static final int PHONES_PHOTO_ID_INDEX = 2; 
    public static final int PHONES_CONTACT_ID_INDEX = 3; 
	private static final String[] PHONES_PROJECTION = new String[] {
	        Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
	//listview section
    private ContactsAdapter contactsAdapter;
    private SectionListAdapter sectionAdapter;
    private SectionListView listView;
	private TextView m_textView_notice;//无匹配的联系人时显示此控件
    EditText search;
	private TextView textView_sum;
	private int contactsBeSelectedSum = 0;
	private ArrayList<ContactInfo> contactsBeSelectedList;
	//sideIndex
	LinearLayout sideIndex;	
	// height of side index
    private int sideIndexHeight,sideIndexSize;
    // list with items for side index
    private ArrayList<Object[]> sideIndexList = new ArrayList<Object[]>();
	
//    private InputMethodManager imm;
    
    private int groupSum = 0;
    
    private OnItemClickListener onItemClickListener;//listView的点击事件监听
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
//    	Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。  
//    	t.setToNow();
//    	int seconda = t.second;
        super.onCreate(savedInstanceState);
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_contacts);
        textView_sum = (TextView) findViewById(R.id.tv_sum);
        m_textView_notice = (TextView) findViewById(R.id.pb_nocontacts_notice);
        search=(EditText)findViewById(R.id.search_query);
		search.addTextChangedListener(filterTextWatcher);
		listView = (SectionListView) findViewById(R.id.section_list_view);
		sideIndex = (LinearLayout) findViewById(R.id.list_index);	
		sideIndex.setOnTouchListener(new Indextouch());
		onItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				ViewHolder holder = (ViewHolder) arg1.getTag();
                // 改变CheckBox的状态
                holder.cb.toggle();
                if (holder.cb.isChecked()) {
					contactsBeSelectedSum ++;
					textView_sum.setText("" + contactsBeSelectedSum);
				}
                else {
					contactsBeSelectedSum --;
					textView_sum.setText("" + contactsBeSelectedSum);
				}
                // 将CheckBox的选中状况记录下来
                contactsAdapter.getItem(arg2).setHasBeSelected(holder.cb.isChecked());
			}
		};
        contactsBeSelectedList = new ArrayList<ContactInfo>();
		Button cancleButton = (Button) findViewById(R.id.cancle_btn);
        Button confirmButton = (Button) findViewById(R.id.confirm_btn);
        
        cancleButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
        confirmButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			    for (int i = 0; i < contacts.size(); i++) {
					if (contacts.get(i).hasBeSelected()) {
						contactsBeSelectedList.add(contacts.get(i));
					}
				}
			    System.out.println("-----------> count = " + contactsBeSelectedList.size());
			    Intent intent = new Intent();
			    Bundle bundle = new Bundle();
			    bundle.putSerializable("contantsBeSelected",contactsBeSelectedList);
			    intent.putExtras(bundle);
			}
		});
		//		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
		
//		t.setToNow(); // 取得系统时间。  
//		int year = t.year;  
//		int month = t.month;  
//		int date = t.monthDay;  
//		int hour = t.hour; // 0-23  
//		int minute = t.minute;  
//		int second = t.second;
//		System.out.println("---------->layoutTime = " + (second - seconda));
		contacts = getContacts();
	   

//		t.setToNow();
//		int second1 = t.second;
//		System.out.println("time1 = " + (second1 - second));
		
        for (int i = 0; i < contacts.size(); i++) {
        	contactsPinyin.add(contacts.get(i).getPY());
		}
        if (contactsPinyin.size() > 0) {
        	// not forget to sort array	
        	Collections.sort(contactsPinyin, new ComparatorPY());
        	PoplulateSideview();
		}
        
//        t.setToNow();
//        int second2 = t.second;
//        long start = System.nanoTime();
//        System.out.println("----------->pinyinpaixu Time = " + (second2 - second1));
//      按中文拼音顺序排序
//        Comparator<ContactInfo> comp = new MyComparator(); 
//        Collections.sort(contacts,comp);
//        long end = System.nanoTime();
//        int second3 = t.second;
//        System.out.println("--------->sortTime = " + (int)(end - start));
        
//        groupSum = (int)contacts.size()/20 + 1;
//        ArrayList<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
//        contactInfos.addAll(contacts);
        
//		for (int i = 0; i < contacts.size(); i++) {
//			contactInfos.add(contacts.get(i));
//			if (contactInfos.size() == 20) {
//				groupArrayList.add(contactInfos);
//				contactInfos.clear();
//			}
//			else if (i == contacts.size() -1) {
//				
//				groupArrayList.add(contactInfos);
//				contactInfos = null;
//			}
//		}
        contactsAdapter = new ContactsAdapter(contacts, ContactsActivity.this);
//        t.setToNow();
//        int second4 = t.second;
        
        //adaptor for section
	    sectionAdapter = new SectionListAdapter(this.getLayoutInflater(),contactsAdapter);
//	    t.setToNow();
//        int second5 = t.second;
//        System.out.println("------------> adapterTime = " + (second5 - second4));
//        sectionAdapter.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				// TODO Auto-generated method stub
//				ViewHolder holder = (ViewHolder) arg1.getTag();
//                // 改变CheckBox的状态
//                holder.cb.toggle();
//                if (holder.cb.isChecked()) {
//					contactsBeSelectedSum ++;
//					textView_sum.setText("" + contactsBeSelectedSum);
//				}
//                else {
//					contactsBeSelectedSum --;
//					textView_sum.setText("" + contactsBeSelectedSum);
//				}
//                // 将CheckBox的选中状况记录下来
//                conversationListAdapter.getItem(arg2).setHasBeSelected(holder.cb.isChecked());
////                ConversationListAdapter.getIsSelected().put(arg2, holder.cb.isChecked()); 
//			}
//		});
	    sectionAdapter.setOnItemClickListener(onItemClickListener);
        listView.setAdapter(sectionAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
//				if (arg1.getTag() instanceof ViewHolder) {
//					ViewHolder holder = (ViewHolder) arg1.getTag();
//	                 // 改变CheckBox的状态
//	                holder.cb.toggle();
//	                 // 将CheckBox的选中状况记录下来
//	                ConversationListAdapter.getIsSelected().put(arg2, holder.cb.isChecked()); 
////	                if (imm.isActive()) {
////	              	    System.out.println("------------->imm.isActive");
////	              	    imm.hideSoftInputFromWindow(ContactsActivity.this.getCurrentFocus().getWindowToken(), 0);
////					}
//				}
				sectionAdapter.onItemClick(arg0, arg1, arg2, arg3);
			}
		});

    }
   
    
    
  
    
    private class Indextouch implements OnTouchListener 
    {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

         	
         	if(event.getAction() ==MotionEvent.ACTION_MOVE  || event.getAction() ==MotionEvent.ACTION_DOWN)
         	{
         		 
         		 sideIndex.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_rectangle_shape));
         		
         		 // now you know coordinates of touch
                 float  sideIndexX = event.getX();
                 float  sideIndexY = event.getY();

                  if(sideIndexX>0 && sideIndexY>0)
                  {
                  	 // and can display a proper item it country list
                      displayListItem(sideIndexY);
                  	
                  }
         	}
         	else
         	{
         		sideIndex.setBackgroundColor(Color.TRANSPARENT);
         	}
            return true;
         
		}
    	
    };
   
    public void onWindowFocusChanged(boolean hasFocus)
	{
    	 // get height when component is poplulated in window
		 sideIndexHeight = sideIndex.getHeight();
//		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 super.onWindowFocusChanged(hasFocus);
	}  
	  
	   
	private void displayListItem(float sideIndexY) {
		// compute number of pixels for every side index item
		double pixelPerIndexItem = (double) sideIndexHeight / sideIndexSize;

		// compute the item index for given event position belongs to
		int itemPosition = (int) (sideIndexY / pixelPerIndexItem);

		if (itemPosition < sideIndexList.size()) {
			// get the item (we can do it since we know item index)
			Object[] indexItem = sideIndexList.get(itemPosition);
			listView.setSelectionFromTop((Integer) indexItem[1], 0);
		}
	}

	@SuppressLint("DefaultLocale")
	private void PoplulateSideview() {

		String latter_temp, latter = "";
		int index = 0;
		sideIndex.removeAllViews();
		sideIndexList.clear();
		for (int i = 0; i < contactsPinyin.size(); i++) {
			Object[] temp = new Object[2];
			latter_temp = (contactsPinyin.get(i)).substring(0, 1).toUpperCase();
			if (!latter_temp.equals(latter)) {
				// latter with its array index
				latter = latter_temp;
				temp[0] = latter;
				temp[1] = i + index;
				index++;
				sideIndexList.add(temp);

				TextView latter_txt = new TextView(this);
				latter_txt.setText(latter);
				latter_txt.setTextColor(getResources().getColor(R.color.text_color_deep));

				latter_txt.setSingleLine(true);
				latter_txt.setHorizontallyScrolling(false);
				latter_txt.setTypeface(null, Typeface.BOLD);
				latter_txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
						getResources().getDimension(R.dimen.index_list_font));
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
				params.gravity = Gravity.CENTER_HORIZONTAL;

				latter_txt.setLayoutParams(params);
				latter_txt.setPadding(10, 0, 10, 0);

				sideIndex.addView(latter_txt);
			}
		}

		sideIndexSize = sideIndexList.size();

	}

	private TextWatcher filterTextWatcher = new TextWatcher() {

		public void afterTextChanged(Editable s) {
			if (curSearchContactTask != null
					&& curSearchContactTask.getStatus() != AsyncTask.Status.FINISHED) {
				try {
					curSearchContactTask.cancel(true);
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("task close exception");
				}
			}
			curSearchContactTask = new SearchContactTask();
			curSearchContactTask.execute(s.toString());

			// if(!"".equals(s.toString().trim()))
			// {
			// inSearchMode = true;
			// 根据编辑框值过滤联系人并更新联系列表
			// filterContacts(s.toString().trim());
			// if (curSearchContactTask != null
			// && curSearchContactTask.getStatus() != AsyncTask.Status.FINISHED)
			// {
			// try {
			// curSearchContactTask.cancel(true);
			// } catch (Exception e) {
			// // TODO: handle exception
			// System.out.println("task close exception");
			// }
			// }
			// curSearchContactTask = new SearchContactTask();
			// curSearchContactTask.execute(s.toString());

			// }
			// else
			// {
			// inSearchMode = false;
			// // System.out.println("------------->contacts.size() = " +
			// contacts.size());
			// ArrayList<ContactInfo> contactInfos = new
			// ArrayList<ContactInfo>();
			// contactInfos.addAll(contacts);
			// conversationListAdapter = new
			// ConversationListAdapter(contactInfos, ContactsActivity.this);
			// sectionAdapter = new SectionListAdapter(getLayoutInflater(),
			// conversationListAdapter);
			// listView.setAdapter(sectionAdapter);
			// }
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// your search logic here

		}

	};
    
	//读取联系人信息
	public ArrayList<ContactInfo> getContacts() {
		ContentResolver resolver = getContentResolver();
		ArrayList<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
		// 获取手机联系人
		Cursor phoneCursor = resolver.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				PHONES_PROJECTION, ContactsContract.CommonDataKinds.Phone.TYPE+"="+ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE, null, "sort_key asc");

		ArrayList<String> contactNumbers = new ArrayList<String>();

//		Time time = new Time();
//		time.setToNow();
//		int second1 = time.second;
		
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX).trim();
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;

				phoneNumber = phoneNumber.replaceAll(" ", "");
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				// 得到联系人ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
//				System.out.println("---------->cotact_id = " + contactid);

				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

				if (!contactNumbers.contains(phoneNumber)) {
					contactNumbers.add(phoneNumber);
					contactInfos.add(new ContactInfo(getApplicationContext(),
							contactName, phoneNumber, photoid, contactid));
				}

				else {
					continue;
				}
//				break;
			}

			phoneCursor.close();
		}
//		time.setToNow();
//		int second2 = time.second;
//		System.out.println("------------->needTime = " + (second2 - second1));
//		for (int i = 0; i < contactIDs.size(); i++) {
//			System.out.println("contactID: " + i + " = " + contactIDs.get(i));
//		}
//		System.out
//				.println("--------> contacts.size() = " + contactInfos.size());
//		for (int i = 0; i < contactNumbers.size(); i++) {
//			System.out.println("---------->number = " + contactNumbers.get(i));
//		}
		return contactInfos;
	}
		
		
	private void filterContacts(String filterStr) {
//		ArrayList<ContactInfo> filterContacts = new ArrayList<ContactInfo>();
		// 遍历所有联系人数组,筛选出包含关键字的联系人
		filterContacts.clear();
		System.out.println("------------------->contacts.size() = " + contacts.size());
		for (int i = 0; i < contacts.size(); i++) {
			// 过滤的条件
			if (isStrInString(contacts.get(i).getPhoneNumber(), filterStr)
					|| isStrInString(PinyinUtils.getPingYin(contacts.get(i)
							.getContactName()), filterStr)
					|| contacts.get(i).getContactName().contains(filterStr)
					|| isStrInString(PinyinUtils.getFirstSpell(contacts.get(i)
							.getContactName()), filterStr)) {
				// 将筛选出来的联系人重新添加到contacts数组中

				filterContacts.add(contacts.get(i));
			}
		}

	}

	public boolean isStrInString(String bigStr, String smallStr) {
		if (bigStr.toUpperCase().indexOf(smallStr.toUpperCase()) > -1) {
			return true;
		} else {
			return false;
		}
	}
	
	//匹配联系人后台执行
	private class SearchContactTask extends AsyncTask<String, Void, Integer> {

		private static final int STATE_FINISH = 1;
		private static final int STATE_ERROR = -1;

		/* 该方法将在执行实际的后台操作前被UI thread调用。可以在该方法中做一些准备工作，如在界面上显示一个进度条。 */
		@Override
		protected void onPreExecute() {
			// 先显示ProgressDialog

		}

		/* 执行那些很耗时的后台计算工作。可以调用publishProgress方法来更新实时的任务进度。 */
		@Override
		protected Integer doInBackground(String... searchKey) {
			System.out.println("------------->");
			String keyword = searchKey[0];
			inSearchMode = (keyword.length() > 0);
			if (inSearchMode) {
				filterContacts(searchKey[0]);
			}

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
					// 如果没有匹配的联系人
					if (inSearchMode) {
						if (filterContacts.isEmpty()) {
//							conversationListAdapter = new ConversationListAdapter(
//									filterContacts, ContactsActivity.this);
//							sectionAdapter = new SectionListAdapter(
//									getLayoutInflater(),
//									conversationListAdapter);
//							sectionAdapter.setOnItemClickListener(onItemClickListener);
//							listView.setAdapter(sectionAdapter);
							resetAdapters(filterContacts);
							listView.setEmptyView(m_textView_notice);
						} else {
							// 将列表更新为过滤的联系人
//							conversationListAdapter = new ConversationListAdapter(
//									filterContacts, ContactsActivity.this);
//							sectionAdapter = new SectionListAdapter(
//									getLayoutInflater(),
//									conversationListAdapter);
//							sectionAdapter.setOnItemClickListener(onItemClickListener);
//							listView.setAdapter(sectionAdapter);
							resetAdapters(filterContacts);
						}
					} else {
//						ArrayList<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
//						contactInfos.addAll(contacts);
						resetAdapters(contacts);
					}

					break;
				}

			case STATE_ERROR:

				break;

			default:

			}
		}
	}
	
	//搜索联系人结束后重置Adapter
	private void resetAdapters(ArrayList<ContactInfo> contactInfos)
	{
		contactsAdapter = new ContactsAdapter(
				contactInfos, ContactsActivity.this);
		sectionAdapter = new SectionListAdapter(
				getLayoutInflater(), contactsAdapter);
		sectionAdapter.setOnItemClickListener(onItemClickListener);
		listView.setAdapter(sectionAdapter);
	}

}


