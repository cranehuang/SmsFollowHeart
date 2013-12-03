package followheart.activities;

import java.util.ArrayList;
import java.util.List;

import com.todddavies.components.progressbar.ProgressWheel;

import followheart.activities.R;
import followheart.adapters.GroupAdapter;
import followheart.entities.ContactInfo;
import followheart.entities.GroupInfo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

@SuppressLint("HandlerLeak")
public class ContactGroupActivity extends Activity {

	private ExpandableListView groupListView;
	private GroupAdapter groupAdapter;
	private List<GroupInfo> groupList;
	private List<List<ContactInfo>> contactLists;
	private TextView textView_sum;
	private Button cancleBtn;
	private Button confirmBtn;
	private int chooseSum = 0;// 选中的联系人中总数
	private int[] selectedSumInGroup;
	private ArrayList<ContactInfo> contactsBeSelected;

	private ProgressWheel progressWheel;
	
	private static final int LOAD_FINISH = 1;

	// private HashMap<Integer,Boolean> groupHasAddMap = new HashMap<Integer,
	// Boolean>();

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == GroupAdapter.SELECTION_CHANGED) {
				boolean groupIsSelected = (Boolean) msg.obj;
				// int groupPosition = msg.arg1;
				int childSum = msg.arg2;
				if (groupIsSelected) {
					chooseSum += childSum;
				} else {
					chooseSum -= childSum;
				}
				textView_sum.setText("" + chooseSum);
			}
			if (msg.what == LOAD_FINISH) {
				progressWheel.setVisibility(View.GONE);
				groupAdapter = new GroupAdapter(getApplicationContext(), mHandler,
						groupList, contactLists);

				groupListView.setAdapter(groupAdapter);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_group);

		textView_sum = (TextView) findViewById(R.id.tv_sum);
		cancleBtn = (Button) findViewById(R.id.group_cancle_btn);
		confirmBtn = (Button) findViewById(R.id.group_confirm_btn);

		progressWheel = (ProgressWheel) findViewById(R.id.pw_spinner);
		progressWheel.spin();

		contactsBeSelected = new ArrayList<ContactInfo>();
		contactLists = new ArrayList<List<ContactInfo>>();
		groupListView = (ExpandableListView) findViewById(R.id.lv_group);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				groupList = getContactGroups();
				selectedSumInGroup = new int[groupList.size()];
				System.out.println("-------------------------------->0");
				for (int i = 0; i < groupList.size(); i++) {
					contactLists.add(getContactsByGroupId(groupList.get(i).getId()));
					selectedSumInGroup[i] = getContactsByGroupId(
							groupList.get(i).getId()).size();
					// groupHasAddMap.put(i, false);
				}
				Message msg = new Message();
				msg.what = LOAD_FINISH;
				mHandler.sendMessage(msg);
			}
		}).start();

		cancleBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		confirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				for (int i = 0; i < contactLists.size(); i++) {
					for (int j = 0; j < contactLists.get(i).size(); j++) {
						if (GroupAdapter.getIsSelected().get(i).get(j)) {
							contactsBeSelected.add(contactLists.get(i).get(j));
						}
					}
				}
				System.out.println("------------>dfhdhdhfdh");
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("contantsBeSelected", contactsBeSelected);
				intent.putExtras(bundle);

			}
		});

		// Cursor cursor =
		// getContentResolver().query(ContactsContract.Groups.CONTENT_URI, new
		// String[]{ContactsContract.Groups._ID,
		// ContactsContract.Groups.TITLE}, null, null, null);
		// if (cursor.moveToFirst()) {
		// do {
		//
		// } while (cursor.moveToNext());
		// }
		// for (int i = 0; i < cursor.getCount(); i++,cursor.moveToNext()) {
		// // System.out.println("cursor.count = " + cursor.getCount());
		// for (int j = 0; j < cursor.getColumnCount(); j++) {
		// System.out.println(" ColumnName = " + cursor.getColumnName(j) +
		// "\n info = " +
		// cursor.getColumnIndex(cursor.getColumnName(j)));
		// }
		// System.out.println("------------------->\n");
		// }
		System.out.println("---------------------------->2");
//		groupAdapter = new GroupAdapter(getApplicationContext(), mHandler,
//				groupList, contactLists);
//
//		groupListView.setAdapter(groupAdapter);

		groupListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				CheckBox checkBox = (CheckBox) v.findViewById(R.id.item_cb);
				checkBox.toggle();
				if (checkBox.isChecked()) {
					chooseSum++;
					selectedSumInGroup[groupPosition] += 1;
					textView_sum.setText("" + chooseSum);
				} else {
					selectedSumInGroup[groupPosition] -= 1;
					chooseSum -= 1;
					System.out.println("----------->childSum = "
							+ selectedSumInGroup[groupPosition]);
					if (selectedSumInGroup[groupPosition] == 0) {
						selectedSumInGroup[groupPosition] = groupAdapter
								.getChildrenCount(groupPosition);
						GroupAdapter.getGroupIsSelected().put(groupPosition,
								false);
					}
					textView_sum.setText("" + chooseSum);
				}
				GroupAdapter.getIsSelected().get(groupPosition)
						.put(childPosition, checkBox.isChecked());

				return true;

			}
		});

		groupListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				// TODO Auto-generated method stub
				int childSum = groupAdapter.getChildrenCount(groupPosition);

				if (!GroupAdapter.getGroupIsSelected().get(groupPosition)) {
					GroupAdapter.getGroupIsSelected().put(groupPosition, true);
					chooseSum += childSum;
					for (int i = 0; i < childSum; i++) {
						// ContactInfo contactInfo = (ContactInfo)
						// groupAdapter.getChild(groupPosition, i);
						// contactsMap.put(contactInfo.getContactID(),
						// contactInfo);
						GroupAdapter.getIsSelected().get(groupPosition)
								.put(i, true);
					}
				}
				// else {
				// GroupAdapter.getGroupIsSelected().put(groupPosition, false);
				// chooseSum -= childSum;
				// for (int i = 0; i < childSum; i++) {
				// // ContactInfo contactInfo = (ContactInfo)
				// groupAdapter.getChild(groupPosition, i);
				// // contactsMap.put(contactInfo.getContactID(), contactInfo);
				// GroupAdapter.getIsSelected().get(groupPosition).put(i,
				// false);
				// }
				// }
				textView_sum.setText("" + chooseSum);
			}
		});

	}

	public List<GroupInfo> getContactGroups() {
		// 我们要得到分组的id 分组的名字
		String[] RAW_PROJECTION = new String[] { ContactsContract.Groups._ID,
				ContactsContract.Groups.TITLE };
		// 查询条件是Groups.DELETED=0
		String RAW_CONTACTS_WHERE = ContactsContract.Groups.GROUP_VISIBLE
				+ "=0 and " + ContactsContract.Groups.DELETED + " = 0 ";
		Cursor cursor = this.getContentResolver().query(
				ContactsContract.Groups.CONTENT_URI, RAW_PROJECTION,
				RAW_CONTACTS_WHERE, null, ContactsContract.Groups.TITLE);

		// 存放分组信息
		List<GroupInfo> islist = new ArrayList<GroupInfo>();

		while (cursor.moveToNext()) {
			// 分组的实体类
			GroupInfo groupInfo = new GroupInfo();
			String id = cursor.getString(cursor.getColumnIndex("_id"));
			System.out.println("groupId = " + id);
			String[] projection = new String[] {
					ContactsContract.Groups.SUMMARY_COUNT,
					ContactsContract.Groups.SUMMARY_WITH_PHONES };
			Cursor cursorGroup = getContentResolver().query(
					ContactsContract.Groups.CONTENT_SUMMARY_URI,
					projection,
					ContactsContract.Groups.SUMMARY_WITH_PHONES + " != 0"
							+ " and " + ContactsContract.Groups._ID + " = "
							+ id, null, null);
			if (cursorGroup.moveToFirst()) {
				groupInfo.setId(id);
				String date = cursor.getString(cursor.getColumnIndex("title"));
				// System.out.println("--------------> title = " + date);
				if (date.equalsIgnoreCase("Family") || date.contains("Family")) {
					groupInfo.setTitle("家人");
				} else if (date.equalsIgnoreCase("Friends")
						|| date.contains("Friends")) {
					groupInfo.setTitle("好友");
				} else if (date.equalsIgnoreCase("Coworkers")
						|| date.contains("Coworkers")) {
					groupInfo.setTitle("同事");
				} else {

					groupInfo.setTitle(date);
				}
				// 把分组放到集合里去
				islist.add(groupInfo);
			} else {
				continue;
			}

		}
		cursor.close();
		return islist;

	}

	// 查询分组的联系人方法 outid是分组的id
	public List<ContactInfo> getContactsByGroupId(String outid) {
		ArrayList<ContactInfo> mymaplist = new ArrayList<ContactInfo>();
		// 思路 我们通过组的id 去查询 RAW_ContactInfo_ID, 通过RAW_ContactInfo_ID去查询联系人
		// 要查询得到 data表的Data.RAW_CONTACT_ID字段
		ContactInfo contact = null;
		String[] RAW_PROJECTION = new String[] { ContactsContract.Data.RAW_CONTACT_ID, };
		String RAW_CONTACTS_WHERE = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID
				+ "="
				+ outid
				+ " and "
				+ ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE
				+ "="
				+ "'"
				+ ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE
				+ "'";
		// 通过分组的id 查询得到RAW_CONTACT_ID
		Cursor cursor = this.getContentResolver().query(
				ContactsContract.Data.CONTENT_URI, RAW_PROJECTION,
				RAW_CONTACTS_WHERE, null, null);
		if (cursor.moveToFirst() && cursor != null) {
			do {
				// RAW_CONTACT_ID
				int contactId = cursor.getInt(cursor
						.getColumnIndex("raw_contact_id"));
				String[] RAW_PROJECTION02 = new String[] { ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, };
				String RAW_CONTACTS_WHERE02 = ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID
						+ "=?"
						+ " and "
						+ ContactsContract.Data.MIMETYPE
						+ "="
						+ "'"
						+ ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
						+ "'";

				// 通过raw_contact_id的值获取用户的名字
				Cursor cursor01 = this.getContentResolver().query(
						ContactsContract.Data.CONTENT_URI, RAW_PROJECTION02,
						RAW_CONTACTS_WHERE02, new String[] { "" + contactId },
						"data1 asc");
				String contacts_name = null;
				if (cursor01 != null && cursor01.moveToFirst()) {
					do {
						contacts_name = cursor01.getString(cursor01
								.getColumnIndex("data1"));
					} while (cursor01.moveToNext());
					cursor01.close();
				}

				// map.put("namekey", contacts_name);

				String[] RAW_PROJECTION03 = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER, };

				String RAW_CONTACTS_WHERE03 = ContactsContract.CommonDataKinds.Phone.CONTACT_ID
						+ "=?"
						+ " and "
						+ ContactsContract.Data.MIMETYPE
						+ "="
						+ "'"
						+ ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
						+ "'";

				// 通过raw_contact_id 获取电话号码
				Cursor cursor02 = this.getContentResolver().query(
						ContactsContract.Data.CONTENT_URI, RAW_PROJECTION03,
						RAW_CONTACTS_WHERE03, new String[] { "" + contactId },
						"data1 asc");

				String phonenum = null;
				if (cursor02 != null && cursor02.moveToFirst()) {
					do {
						phonenum = cursor02.getString(cursor02
								.getColumnIndex("data1"));
					} while (cursor02.moveToNext());
					cursor02.close();
				}

				if (!TextUtils.isEmpty(phonenum)) {
					contact = new ContactInfo(getApplicationContext(),
							contacts_name, phonenum, (long) 0, (long) contactId);
					mymaplist.add(contact);
				}

			} while (cursor.moveToNext());
			cursor.close();
		}

		return mymaplist;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_contact_group, menu);
		return true;
	}

	public void iSelectionClick(View v) {

	}

}
