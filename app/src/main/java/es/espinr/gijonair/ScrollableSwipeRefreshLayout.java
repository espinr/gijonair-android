/**
 * Class to extend SwipeRefreshLayout in order to be able to scroll and swipe properly.
 */
package es.espinr.gijonair;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

/**
 * @author martin
 *
 */
public class ScrollableSwipeRefreshLayout extends SwipeRefreshLayout {

	private OnChildScrollUpListener mScrollListenerNeeded;


	public ScrollableSwipeRefreshLayout(Context context) {
		super(context);
		//this.scrollView = (ScrollView) findViewById(R.id.viewScroll);
	}

	public ScrollableSwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);   
	}

	public interface OnChildScrollUpListener {
		boolean canChildScrollUp();
	}
	/**
	 * Listener that controls if scrolling up is allowed to child views or not
	 */
	public void setOnChildScrollUpListener(OnChildScrollUpListener onChildScrollUpListener) {
		mScrollListenerNeeded = onChildScrollUpListener;   
	}

	@Override
	public boolean canChildScrollUp() {
		return mScrollListenerNeeded != null && mScrollListenerNeeded.canChildScrollUp();
	}

}
