package es.espinr.gijonair;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import es.espinr.gijonair.ScrollableSwipeRefreshLayout.OnChildScrollUpListener;

/**
 * @author martin
 *
 */
public class StationsActivity extends ActionBarActivity implements ScrollableSwipeRefreshLayout.OnRefreshListener {

	private FirebaseAnalytics mFirebaseAnalytics;
    private LinearLayout viewStations;
    private ScrollableSwipeRefreshLayout swipeContainer;
    private Timer autoUpdate;
    private boolean isInForegroundMode;
	private static final String TAG = "StationsActivity";
	private static final String GENERAL_TOPIC_NAME = "alerts";
    
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
		// Obtain the FirebaseAnalytics instance.
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_stations);
        viewStations = (LinearLayout) findViewById(R.id.viewStations);
        
        // Setup the swipeContainer
        swipeContainer = (ScrollableSwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this);

        // Stops the execution until the Internet connection is available 
        if (!AirStationsUtil.isInternetAvailable(this)) {
        	Log.d(StationsActivity.TAG, "No internet available");
        	AlertDialog alert = AirStationsUtil.createAlertDialogNoDataLoaded(this);
        	alert.show();
        	return;
        }

		// This is to log an event to check where user clicks
		/*bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
		bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
		bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
		mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
		*/
		FirebaseMessaging.getInstance().subscribeToTopic(GENERAL_TOPIC_NAME);

		// Just then the Y value of the scroll is 0, refresh swipe can be performed
		final ScrollView sv = (ScrollView) findViewById(R.id.viewScroll);
		sv.getViewTreeObserver().addOnScrollChangedListener(
				new ViewTreeObserver.OnScrollChangedListener() {
					@Override public void onScrollChanged() {
						boolean firstStationShown = sv.getScrollY()==0;
						swipeContainer.setEnabled(firstStationShown);
						swipeContainer.setActivated(firstStationShown);
					}
				});
    }

    /**
     * Function to auto-update values
     */
    @Override
    public void onResume() {
    	super.onResume();
    	// In case the app is visible
    	isInForegroundMode = true;
    	autoUpdate = new Timer();
    	autoUpdate.schedule(new TimerTask() {
    		@Override
    		public void run() {
    			runOnUiThread(new Runnable() {
    				public void run() {
    					updateStations();
    				}
    			});
    		}
    	}, 0, 600000); // updates each 10 minutes
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        isInForegroundMode = false;
    }

    
    protected void updateStations() {
    	// If the app is in foreground mode
    	if (isInForegroundMode) {
            // Caches the file (asynchronously)
            StationsFileCacher stationsfilecacher = new StationsFileCacher(this);
            stationsfilecacher.execute(viewStations, this.getString(R.string.url_stations_json));	
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stations, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        StationsFileCacher stationsfilecacher = new StationsFileCacher(this);
    	Intent intent = new Intent(this, AboutActivity.class);
        switch (id) {
			case R.id.action_about:
	        	this.startActivity(intent);
	            return true;
			case R.id.action_about_icon:
	        	this.startActivity(intent);
	            return true;
			case R.id.action_refresh:
		        // Caches the file (asynchronously) and loads the stations that gets
		        stationsfilecacher.execute(viewStations, this.getString(R.string.url_stations_json));			
		        return true;
			case R.id.action_refresh_icon:
		        // Caches the file (asynchronously) and loads the stations that gets
		        stationsfilecacher.execute(viewStations, this.getString(R.string.url_stations_json));			
		        return true;
			default:
				break;
		}
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onRefresh() {
		RefreshThread refreshThread = new RefreshThread(this);
		new Handler().postDelayed( refreshThread , 100);	
		
	}

	/*
	 * Class to run the refresh after swipe
	 */
	public class RefreshThread implements Runnable {
		 private Context mContext;

		 public RefreshThread(Context context) {
		       this.mContext = context;
		   }

		@Override
		public void run() {
      	  // Update the content
      	  StationsFileCacher stationsfilecacher = new StationsFileCacher(mContext);
      	  stationsfilecacher.execute(viewStations, mContext.getString(R.string.url_stations_json));
      	  swipeContainer.setRefreshing(false);			
		}
	}
    
}

