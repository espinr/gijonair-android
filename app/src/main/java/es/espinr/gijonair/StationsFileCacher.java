package es.espinr.gijonair;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;


/**
 * @author martin
 * This class caches the file with the information about the stations into a local file.
 */

public class StationsFileCacher extends AsyncTask<Object,Object,String> {

	private static final String TAG = "StationsFileCacher";
	public static String LOCAL_FILENAME = "stations.json";
	public static String LOCAL_BACKUP_FILENAME = "stations_back.json";
	private Context mContext;
	private View view;
	private ScrollableSwipeRefreshLayout swipeRefreshLayout;
	private boolean needsUpdate;

	/**
	 * @param context    The context to be stored.
	 */
	public StationsFileCacher(Context context, boolean needsUpdate, ScrollableSwipeRefreshLayout swipeRefreshLayout) {
		this.mContext = context;
		this.swipeRefreshLayout = swipeRefreshLayout;
		this.needsUpdate = needsUpdate;
	}

	/**
	 * Read all the content from the reader.
	 *
	 * @param reader
	 * @return The content read.
	 * @throws IOException
	 */
	private String readAll(Reader reader) throws IOException {
		StringBuilder stringbuilder = new StringBuilder();
		do {
			int i = reader.read();
			if (i == -1) {
				return stringbuilder.toString();
			}
			stringbuilder.append((char) i);
		} while (true);
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		swipeRefreshLayout.setRefreshing(true);
	}




	/*
	 * Gets two parameters, the view to get the context and the URL to grab the
	 * content and cache it.
	 * 
	 */
	@Override
	protected String doInBackground(Object... params) {
		String s = "" ;
		view = (View) params[0];

		if (needsUpdate) {
			try {
				InputStream inputstream;
				URL url = new URL((String)params[1]);

				inputstream = url.openStream();
				s = readAll(new BufferedReader(new InputStreamReader(inputstream, Charset.forName("UTF-8"))));
				Log.d(TAG, (new StringBuilder("Reading file at ")).append(url.toString()).append(": ").append(s).toString());
				inputstream.close();
				if (s.trim().length()==0){
					Log.e(TAG, "I couldn't read the feed properly so the data will remain the same");
				} else {
					backupDBFile();
					// Caches the file as stations.json
					FileOutputStream fileoutputstream = mContext.openFileOutput(LOCAL_FILENAME, 0);
					fileoutputstream.write(s.getBytes());
					fileoutputstream.close();
					Log.d(TAG, "Local file " + LOCAL_FILENAME + " written");
				}
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, (new StringBuilder("ERROR when reading or writing the file ")).append(params[1]).append(":\n").append(e.getMessage()).toString());
			}
		}
		return s;
    }

	protected void backupDBFile() {
		try {
			AirStationsUtil.backupFile(mContext.getFileStreamPath(LOCAL_FILENAME), mContext.getFileStreamPath(LOCAL_BACKUP_FILENAME));
		} catch (IOException e) {
			Log.e(TAG, "Error making a copy of the DB. Perhaps the original " + LOCAL_FILENAME + " does not exist");
		}
	}


	protected void onPostExecute(String s) {

		try {
			if (s==null) return;

			LinearLayout linearlayout = (LinearLayout) view
					.findViewById(R.id.viewStations);

			// Loads the just added content
			AirStationsLoader stationLoader = new AirStationsLoader(mContext, linearlayout);
			int i = stationLoader.execute();
			Log.d(TAG, "Loaded "+ i +" stations ");
			
			Toast.makeText(
					mContext,
					(new StringBuilder(String.valueOf(i))).append(" ")
							.append(mContext.getString(R.string.text_stations_loaded)).toString(),Toast.LENGTH_SHORT).show();
		} catch (Exception exception) {
			exception.printStackTrace();
			Log.e(TAG, (new StringBuilder("ERROR writing file"))
					.append(LOCAL_FILENAME).append("\n").append(exception.getMessage())
					.toString());
		}
		swipeRefreshLayout.setRefreshing(false);
		return;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onCancelled()
	 */
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onProgressUpdate(java.lang.Object[])
	 */
	protected void onProgressUpdate(Object... values) {
		super.onProgressUpdate(values);
	}



}
