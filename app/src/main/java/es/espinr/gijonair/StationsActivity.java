package es.espinr.gijonair;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import es.espinr.gijonair.ScrollableSwipeRefreshLayout.OnChildScrollUpListener;

import static java.lang.Long.parseLong;

/**
 * @author martin
 *
 */
public class StationsActivity extends AppCompatActivity implements ScrollableSwipeRefreshLayout.OnRefreshListener {

	private FirebaseAnalytics mFirebaseAnalytics;
    private LinearLayout viewStations;
    private ScrollableSwipeRefreshLayout swipeContainer;
	private SharedPreferences mPrefs;
    private Timer autoUpdate;
    private boolean isInForegroundMode;
	private AssetManager assetManager;
	private static final String TAG = "StationsActivity";
	private static final String GENERAL_TOPIC_NAME = "alerts";
	private Date nextUpdate = null;
	private long UPDATE_FREQUENCY_IN_MS = 0; 	// It will be configured at onCreate
    
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
		assetManager = this.getResources().getAssets();
		nextUpdate = new Date(new Date().getTime() - 10000);

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

		preferencesSetup();

		// Just then the Y value of the scroll is 0, refresh swipe can be performed
		final ScrollView sv = (ScrollView) findViewById(R.id.viewScroll);
		sv.getViewTreeObserver().addOnScrollChangedListener(
				new ViewTreeObserver.OnScrollChangedListener() {
					@Override public void onScrollChanged() {
						boolean isFirstStationShown = sv.getScrollY()==0;
						swipeContainer.setEnabled(isFirstStationShown);
						swipeContainer.setActivated(isFirstStationShown);
					}
				});
    }

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

    /**
     * Function to auto-update values
     */
    @Override
    public void onResume() {
    	super.onResume();
    	// In case the app is visible
    	isInForegroundMode = true;
		updateStations();
	}

	@Override
	public void onRefresh() {
		updateStations();
	}

	@Override
    protected void onPause() {
        super.onPause();
        isInForegroundMode = false;
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
		Log.d(TAG, new Integer(id).toString());
		Intent aboutIntent = new Intent(this, AboutActivity.class);
		Intent settingsIntent = new Intent(this, SettingsActivity.class);

		if (id == R.id.action_refresh || id == R.id.action_refresh_icon) {
			super.onOptionsItemSelected(item);
			Log.d(TAG, "Refreshing data");
			updateStations();
			return true;
		} else if (id == R.id.action_settings) {
			super.onOptionsItemSelected(item);
			Log.d(TAG, "Starting settings activity");
			this.startActivity(settingsIntent);
			return true;
		} else if (id == R.id.action_about) {
			super.onOptionsItemSelected(item);
			Log.d(TAG, "Starting aboutactivity");
			this.startActivity(aboutIntent);
			return true;
		}
        return super.onOptionsItemSelected(item);
    }


	private void preferencesSetup() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean notificationsOn = sharedPref.getBoolean(SettingsActivity.KEY_NOTIFICATIONS_ENABLED, false);
		String notificationsOnRingtone = sharedPref.getString(SettingsActivity.KEY_NOTIFICATIONS_RINGTONE, "");
		boolean notificationsOnVibrate = sharedPref.getBoolean(SettingsActivity.KEY_NOTIFICATIONS_VIBRATE, false);
		Long syncFrequency = parseLong(sharedPref.getString(SettingsActivity.KEY_SYNC_FREQUENCY, ""));

		Log.d(TAG, "Frequency?              : " + syncFrequency);
		Log.d(TAG, "Notifications On?       : " + notificationsOn);

		if (notificationsOn) {
			FirebaseMessaging.getInstance().subscribeToTopic(GENERAL_TOPIC_NAME);
			Log.d(TAG, "Subscribed to notifications: " + GENERAL_TOPIC_NAME);
			Log.d(TAG, "Notifications Ringtone? : " + notificationsOnRingtone);
			Log.d(TAG, "Notifications Vibrate? 	: " + notificationsOnVibrate);
		} else {
			FirebaseMessaging.getInstance().unsubscribeFromTopic(GENERAL_TOPIC_NAME);
			Log.d(TAG, "Unsubscribed to notifications: " + GENERAL_TOPIC_NAME);
		}

		// Setup a large number in case it won't be synchronized
		UPDATE_FREQUENCY_IN_MS =  (syncFrequency.longValue() <= 0)? 36000000 : syncFrequency.longValue();
	}


	private void updateStations(){
		boolean needsUpdate = new Date().after(nextUpdate);
		if (needsUpdate)
			nextUpdate = new Date(new Date().getTime() + UPDATE_FREQUENCY_IN_MS);
		RefreshThread refreshThread = new RefreshThread(this, needsUpdate);
		new Handler().post(refreshThread);
	}

	/*
	 * Class to run the refresh after swipe
	 */
	public class RefreshThread implements Runnable {
		private Context mContext;
		private boolean needsUpdate;

		 public RefreshThread(Context context, boolean needsUpdate) {
			 this.mContext = context;
			 this.needsUpdate = needsUpdate;
		 }

		@Override
		public void run() {
      	  	// Update the content if the date now is after nextUpdate
			StationsFileCacher stationsfilecacher = new StationsFileCacher(mContext, needsUpdate);
			stationsfilecacher.execute(viewStations, AirStationsUtil.getConfigProperty(assetManager, "source.json.url"));
			swipeContainer.setRefreshing(false);
		}
	}
    
}

