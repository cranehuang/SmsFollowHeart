package followheart.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class BanQuanActivity extends Activity{
	private TextView banQuan;
	
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// ��ȥ��������Ӧ�ó�������֣�
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.banquan);
		banQuan = (TextView)this.findViewById(R.id.pictureTextView);
		
	}
}
