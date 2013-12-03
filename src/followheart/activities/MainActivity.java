package followheart.activities;

import java.util.ArrayList;
import java.util.List;

import followheart.activities.R;
import followheart.adapters.MyPageAdapter;
import followheart.fragments.MyTabFactory;
import followheart.fragments.TableListFragment;
import followheart.fragments.MorePageFragment;
import followheart.fragments.SmsConversationFragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

/**
 * ----------------------------------------------------------------------------------------------------------
 * ------------------------------ Progetto �TabHostAndFragments� Project v1.01 19.07.2013 - MainActivity.java
 * -------------------------------------------------------------------------------------------- Andrea Carrer
 * ----------------------------------------------------------------------------------------------------------
 * ---------------------------------------------------------------------------------------Freely inspired by: 
 * --------------- http://thepseudocoder.wordpress.com/2011/10/13/android-tabs-viewpager-swipe-able-tabs-ftw/

*/

/*
 * Andrea, can you check this file, please?
 */

public class MainActivity extends FragmentActivity implements
		OnTabChangeListener, OnPageChangeListener {

	MyPageAdapter pageAdapter;
	private ViewPager mViewPager;
	private TabHost mTabHost;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private int currentPage;//记录程序退出前的页面

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		preferences = getSharedPreferences("rememberPage", MODE_PRIVATE);
		editor = preferences.edit();
		currentPage = preferences.getInt("currentPage", 1);
		
		mViewPager = (ViewPager) findViewById(R.id.viewpager);

		// Tab Initialization
		initialiseTabHost(currentPage);

		// Fragments and ViewPager Initialization
		List<Fragment> fragments = getFragments();
		pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
		mViewPager.setAdapter(pageAdapter);
		mViewPager.setCurrentItem(mTabHost.getCurrentTab());
		mViewPager.setOnPageChangeListener(MainActivity.this);
	}

	// Method to add a TabHost
	private static void AddTab(MainActivity activity, TabHost tabHost,
			TabHost.TabSpec tabSpec) {
		tabSpec.setContent(new MyTabFactory(activity));
		tabHost.addTab(tabSpec);
	}

	// Manages the Tab changes, synchronizing it with Pages
	public void onTabChanged(String tag) {
		int pos = this.mTabHost.getCurrentTab();
		currentPage = pos;
		this.mViewPager.setCurrentItem(pos);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	// Manages the Page changes, synchronizing it with Tabs
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		int pos = this.mViewPager.getCurrentItem();
		currentPage = pos;
		this.mTabHost.setCurrentTab(pos);
	}

	@Override
	public void onPageSelected(int arg0) {
	}

	private List<Fragment> getFragments() {
		List<Fragment> fList = new ArrayList<Fragment>();

		// TODO Put here your Fragments
		SmsConversationFragment smsInbox =  new SmsConversationFragment();
		TableListFragment f1 = TableListFragment.newInstance();
		MorePageFragment f3 = MorePageFragment.newInstance();
		fList.add(f1);
		fList.add(smsInbox);
		fList.add(f3);

		return fList;
	}

	// Tabs Creation
	private void initialiseTabHost(int currentPage) {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		// TODO Put here your Tabs
		MainActivity.AddTab(this, this.mTabHost,
				this.mTabHost.newTabSpec("Tab1").setIndicator(LayoutInflater.from(this).inflate(R.layout.table_tabwidget_view, null)));
		MainActivity.AddTab(this, this.mTabHost,
				this.mTabHost.newTabSpec("Tab2").setIndicator(LayoutInflater.from(this).inflate(R.layout.conversation_tabwidget_view, null)));
		MainActivity.AddTab(this, this.mTabHost,
				this.mTabHost.newTabSpec("Tab3").setIndicator(LayoutInflater.from(this).inflate(R.layout.setting_tabwidget_view, null)));

		mTabHost.setCurrentTab(currentPage);
		
		mTabHost.setOnTabChangedListener(this);
	}
	
	@Override
	public void onDestroy()
	{
		//退出程序前记录当前页
		editor.putInt("currentPage", currentPage);
		editor.commit();
		super.onDestroy();
	}
	
}