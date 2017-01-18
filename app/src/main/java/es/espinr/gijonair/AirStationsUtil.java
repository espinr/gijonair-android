/**
 * 
 */
package es.espinr.gijonair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @author martin
 * 
 *         Class with static methods to create dialogs, check Internet
 *         connection, etc.
 */
public class AirStationsUtil {

	private static final String TAG = "AirStationsUtil";

	/**
	 * @return true if Internet connection is available, false if not.
	 */
	public static boolean isInternetAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo wifiNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			return true;
		}

		NetworkInfo mobileNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null && mobileNetwork.isConnected()) {
			return true;
		}

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		}

		return false;
	}

	/**
	 * Clears the already stored file.
	 */
	public static void clearFileCache(String filename) {
		File file = new File(filename);

		if (file.exists() &&  file.delete()) {
			Log.d(TAG, "Fichero local " + filename + " borrado");
		}
		return;
	}

	/**
	 * Create a backup of the existing cached file.
	 */
	public static boolean backupFile(File source, File dest) throws IOException {
		Log.d(TAG, "Backup of " + source + " as " + dest);
		if (source.exists()) {
			source.renameTo(dest);
			Log.d(TAG, "Local file " + source + " now is " + dest);
			return true;
		}
		return false;
	}

	/**
	 * @param context
	 *            The context of the app
	 * @return The ProgressDialog loaded, ready to be shown
	 */
	public static ProgressDialog createProgressDialogLoading(Context context) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setTitle(context
				.getString(R.string.spinner_loading_stations_title));
		dialog.setCancelable(false);

		dialog.setMessage(context.getString(R.string.spinner_loading_stations));
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
			}
		});
		return dialog;
	}

	/**
	 * @return An instantiated alertDialog to be launched when no Internet
	 *         connection or problems loading the file.
	 */
	public static AlertDialog createAlertDialogNoDataLoaded(Context context) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(context
				.getString(R.string.dialog_no_internet_title));
		alertDialog.setMessage(context
				.getString(R.string.dialog_no_internet_description));

		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
				context.getString(R.string.dialog_no_internet_button_retry),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
				        // Caches the file (asynchronously)
						AlertDialog myDialog = (AlertDialog) dialog;
						Context c = myDialog.getContext();
						
						// Firstly gets the layout where the stations are loaded
						final LayoutInflater factory = myDialog.getLayoutInflater();
						ViewGroup root = null;
						final View layout = factory.inflate(R.layout.activity_stations, root);
						LinearLayout viewStations = (LinearLayout) layout.findViewById(R.id.viewStations);

						// Update the content if the date now is after nextUpdate
						AssetManager assetManager = c.getResources().getAssets();
						StationsFileCacher stationsfilecacher = new StationsFileCacher(c, true);
						stationsfilecacher.execute(viewStations, AirStationsUtil.getConfigProperty(assetManager, "source.json.url"));
						// When the task returns it will load the latest values
					}
				});
		return alertDialog;
		// alertDialog.setIcon(R.drawable.icon);
	}


	public static String getConfigProperty(AssetManager assetManager, String key) {
		String value = null;
		try {
			InputStream inputStream = assetManager.open("config.properties");
			Properties properties = new Properties();
			properties.load(inputStream);
			Log.d(TAG, "Configuration Properties loaded");
			value = properties.getProperty(key);
		} catch (IOException e) {
			Log.e(TAG, "Failed to load configuration properties");
		}
		return value;
	}

}
