package es.espinr.gijonair;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Date;
import java.util.Timer;

import static java.lang.Long.parseLong;

/**
 * @author martin
 *
 */
public class StationsActivity extends AppCompatActivity implements ScrollableSwipeRefreshLayout.OnRefreshListener {

	private static final String TAG = "StationsActivity";
	private static final String GENERAL_TOPIC_NAME = "alerts";
	private FirebaseAnalytics mFirebaseAnalytics;
    private LinearLayout viewStations;
    private ScrollableSwipeRefreshLayout swipeContainer;
	private AirStationsLoader dataLoader;
	private SharedPreferences mPrefs;
    private Timer autoUpdate;
    private boolean isInForegroundMode;
	private AssetManager assetManager;
	private Date nextUpdate = null;
	private long UPDATE_FREQUENCY_IN_MS = 0; 	// It will be configured at onCreate
    
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
		// Policy of threads
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

		assetManager = this.getResources().getAssets();

		nextUpdate = new Date(new Date().getTime() - 10000);   // Should update now

		// Obtain the FirebaseAnalytics instance.
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_stations);
        viewStations = (LinearLayout) findViewById(R.id.viewStations);
		dataLoader = new AirStationsLoader(this, viewStations);
		dataLoader.execute();

		// Setup the swipeContainer
        swipeContainer = (ScrollableSwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this);
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
		updateStations();
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
		dataLoader.execute();
	}

	@Override
	public void onRefresh() {
		updateStations();
		// Loads the latest cached stations cached
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
			Log.d(TAG, "Starting about activity");
			this.startActivity(aboutIntent);
			return true;
		}
        return super.onOptionsItemSelected(item);
    }


	private void preferencesSetup() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean notificationsOn = sharedPref.getBoolean(SettingsActivity.KEY_NOTIFICATIONS_ENABLED, false);
		Long syncFrequency = parseLong(sharedPref.getString(SettingsActivity.KEY_SYNC_FREQUENCY, ""));

		if (notificationsOn) {
			FirebaseMessaging.getInstance().subscribeToTopic(GENERAL_TOPIC_NAME);
			Log.d(TAG, "Subscribed to notifications: " + GENERAL_TOPIC_NAME);
		} else {
			FirebaseMessaging.getInstance().unsubscribeFromTopic(GENERAL_TOPIC_NAME);
			Log.d(TAG, "Unsubscribed to notifications: " + GENERAL_TOPIC_NAME);
		}

		// Setup a large number in case it won't be synchronized
		UPDATE_FREQUENCY_IN_MS = (syncFrequency.longValue() <= 0) ? 360000000 : syncFrequency.longValue();
	}


	private void updateStations(){
		boolean needsUpdate = new Date().after(nextUpdate);
		if (needsUpdate)
			this.nextUpdate = new Date(new Date().getTime() + UPDATE_FREQUENCY_IN_MS);
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
			if (!AirStationsUtil.isInternetAvailable()) {
				Log.d(StationsActivity.TAG, "No internet available");
				AlertDialog alert = AirStationsUtil.createAlertDialogNoDataLoaded(mContext);
				alert.show();
				swipeContainer.setRefreshing(false);
			} else {
				StationsFileCacher stationsfilecacher = new StationsFileCacher(mContext, needsUpdate, swipeContainer);
				stationsfilecacher.execute(viewStations, AirStationsUtil.getConfigProperty(assetManager, "source.json.url"));
			}
		}
	}
    
}

