package followheart.factory;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TabHost.TabContentFactory;
//tabπ§≥ß¿‡
public class MyTabFactory implements TabContentFactory {

    private final Context mContext;

    public MyTabFactory(Context context) {
        mContext = context;
    }

    public View createTabContent(String tag) {
    	Log.d("aaaabbbbb", "....");
        View v = new View(mContext);
        v.setMinimumWidth(0);
        v.setMinimumHeight(0);
        Log.d("bbbssss", "rrrr");
        return v;
    }
}