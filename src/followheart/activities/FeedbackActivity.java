package followheart.activities;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;

import android.os.Bundle;
import android.app.Activity;

public class FeedbackActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		FeedbackAgent agent = new FeedbackAgent(this);
		agent.startFeedbackActivity();
		

	}
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPause(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this);
	}
	
}