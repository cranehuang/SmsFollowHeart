package followheart.fragments;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

import followheart.activities.AboutActivity;
import followheart.activities.BanQuanActivity;
import followheart.activities.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class MorePageFragment extends Fragment {

	private static View mView;
	private Button banQuanButton;
	private Button shuoMingButton;
	private Button gengXinButton;
	private Button returnOfUserButton;
	
	private Activity activity;

	public static final MorePageFragment newInstance() {
		MorePageFragment f = new MorePageFragment();
		Bundle b = new Bundle();
		f.setArguments(b);
		return f;
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		activity = getActivity();

	
	}

	// �Զ����õ�onCreateView
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mView = inflater.inflate(R.layout.activity_my_sample_fragment_tab3,
				container, false);
		banQuanButton = (Button) mView.findViewById(R.id.banQuanButton);
		shuoMingButton = (Button) mView.findViewById(R.id.shuoMingButton);
		gengXinButton = (Button) mView.findViewById(R.id.gengXinButton);
		shuoMingButton.setBackgroundResource(R.drawable.about_selector);
		banQuanButton.setBackgroundResource(R.drawable.banquan_selector);
		gengXinButton.setBackgroundResource(R.drawable.update_selector);
		returnOfUserButton = (Button) mView
				.findViewById(R.id.returnOfUesrButton);
		returnOfUserButton.setBackgroundResource(R.drawable.user_selector);
		banQuanButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						BanQuanActivity.class);
				startActivity(intent);
			}
		});
		shuoMingButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						AboutActivity.class);
				startActivity(intent);
			}
		});
		gengXinButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				UmengUpdateAgent.update(activity);
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
					@Override
					public void onUpdateReturned(int updateStatus,
							UpdateResponse updateInfo) {
						switch (updateStatus) {
						case 0: // has update
							UmengUpdateAgent.showUpdateDialog(activity,
									updateInfo);

							break;
						case 1: // has no update
							Toast.makeText(activity, "没有更新", Toast.LENGTH_SHORT)
									.show();
							// System.out.println("meiyou");
							break;
						case 2: // none wifi
							Toast.makeText(activity, "没有wifi连接， 只在wifi下更新",
									Toast.LENGTH_SHORT).show();
							// System.out.println("meiyou wifi");
							break;
						case 3: // time out
							Toast.makeText(activity, "超时", Toast.LENGTH_SHORT)
									.show();
							// System.out.println("time out ");
							break;
						}
					}

				});
			}
		});
			
		returnOfUserButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				FeedbackAgent agent = new FeedbackAgent(activity);
				agent.startFeedbackActivity();
				agent.sync();
			}
		});

		

		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(activity);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(activity);
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
}
