package util;

import activities.ManageNetworks;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
	
	public MyScrollView(Context context) {
		super( context );
	}
	
	public MyScrollView(Context context, AttributeSet attrs) {
		
		super( context, attrs );
	}
	
	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		
		super( context, attrs, defStyle );
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		View view = (View) getChildAt( getChildCount() - 1 );
		int d = view.getBottom();
		d -= (getHeight() + getScrollY());
		if (d <= 0) {
			ManageNetworks.getNextPageItems();
		} else
			super.onScrollChanged( l, t, oldl, oldt );
	}
}
