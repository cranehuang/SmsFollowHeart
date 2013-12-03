package followheart.adapters;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * View displaying the list with sectioned header.
 */
public class SectionListView extends ListView implements OnScrollListener {

    private View transparentView;
    private InputMethodManager imm ;

    public SectionListView(final Context context, final AttributeSet attrs,
            final int defStyle) {
        super(context, attrs, defStyle);
        commonInitialisation();
    }

    public SectionListView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        commonInitialisation();
    }

    public SectionListView(final Context context) {
        super(context);
        commonInitialisation();
    }
    
	
    protected final void commonInitialisation() {
        setOnScrollListener(this);
        setVerticalFadingEdgeEnabled(false);
        setFadingEdgeLength(0);
    }

    @Override
    public void setAdapter(final ListAdapter adapter) {
        if (!(adapter instanceof SectionListAdapter)) {
            throw new IllegalArgumentException(
                    "The adapter needds to be of type "
                            + SectionListAdapter.class + " and is "
                            + adapter.getClass());
        }
        super.setAdapter(adapter);
        final ViewParent parent = getParent();
        if (!(parent instanceof FrameLayout)) {
            throw new IllegalStateException(
                    "Section List should have FrameLayout as parent!");
        }
        if (transparentView != null) {
            ((FrameLayout) parent).removeView(transparentView);
        }
        transparentView = ((SectionListAdapter) adapter)
                .getTransparentSectionView();
        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        ((FrameLayout) parent).addView(transparentView, lp);
        if (adapter.isEmpty()) {
            transparentView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem,
            final int visibleItemCount, final int totalItemCount) {
    	
    	final SectionListAdapter adapter = (SectionListAdapter) getAdapter();
        if (adapter != null && adapter instanceof SectionListAdapter) {
            ((SectionListAdapter)adapter).makeSectionInvisibleIfFirstInList(firstVisibleItem);
        }
    }

    @Override
    public void onScrollStateChanged(final AbsListView view,
            final int scrollState) {
        // do nothing
    	switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_FLING:
	    	closeSoftInput();
			break;
			
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
	    	closeSoftInput();
			break;
			
		case OnScrollListener.SCROLL_STATE_IDLE:
			
			break;

		default:
			break;
		}
    	
    	
    }
    
    private void closeSoftInput()
    {
    	imm =  (InputMethodManager)this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    	if (imm.isActive()) {
    		imm.hideSoftInputFromWindow(this.getApplicationWindowToken(), 0);
		}
    }

    
}
