package es.espinr.gijonair;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;


/**
 * @author martin
 * This class caches the file with the information about the stations into a local file.
 */

public class StationsFileCacher extends AsyncTask<Object,Object,String> {

	public static String LOCAL_FILENAME = "stations.json";
	private Context mContext;
	private View view;
	private ProgressDialog dialog;

	/**
	 * Read all the content from the reader. 
	 * 
	 * @param reader
	 * @return	The content read. 
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
	
	
	/**
	 * @param context	The context to be stored.
	 */
	public StationsFileCacher(Context context) {
		this.mContext = context;
		this.dialog = new ProgressDialog(context);
	}
	
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = AirStationsUtil.createProgressDialogLoading(mContext);
		dialog.show();		
		AirStationsUtil.clearFileCache(LOCAL_FILENAME);
	}




	/*
	 * Gets two parameters, the view to get the context and the URL to grab the
	 * content and cache it.
	 * 
	 */
	@Override
	protected String doInBackground(Object... params) {
		String s = "" ;
    	try {
    		view = (View) params[0];
	        InputStream inputstream;
	        URL url = new URL((String)params[1]);
	        
	        inputstream = url.openStream();
	        s = readAll(new BufferedReader(new InputStreamReader(inputstream, Charset.forName("UTF-8"))));
	        Log.d("GIJON", (new StringBuilder("Leyendo el fichero ")).append(url.toString()).append(": ").append(s).toString());
	        inputstream.close();

    	} catch (IOException e) {
            Log.e("GIJON", (new StringBuilder("ERROR al leer el fichero ")).append(params[1]).append(":\n").append(e.getMessage()).toString());
            return null;
    	}
    	try {
	        // Caches the file as stations.json
			FileOutputStream fileoutputstream = mContext.openFileOutput(LOCAL_FILENAME, 0);
			fileoutputstream.write(s.getBytes());
			fileoutputstream.close();
			
	        Log.d("GIJON", "Fichero local " + LOCAL_FILENAME + " escrito");

    	} catch (IOException e) {
            Log.e("GIJON", (new StringBuilder("ERROR al escribir el fichero ")).append(LOCAL_FILENAME).append(":\n").append(e.getMessage()).toString());
            return null;
        }
	        
		return s;
    }
	


	protected void onPostExecute(String s) {

		try {

			if (s==null) {
				this.dialog.dismiss();
	            AirStationsUtil.createAlertDialogNoDataLoaded(this.mContext).show();

//				AlertDialog alertDialog = AirStationsUtil.createAlertDialogNoDataLoaded(this.mContext);
//				alertDialog.show();				
//				Toast.makeText(
//						mContext,
//						mContext.getString(R.string.text_stations_loaded_error),Toast.LENGTH_SHORT).show();			
				return;
			}
			
			LinearLayout linearlayout = (LinearLayout) view
					.findViewById(R.id.viewStations);

			// Loads the just added content
			AirStationsLoader stationLoader = new AirStationsLoader(mContext, linearlayout);
			int i = stationLoader.execute();
			
			Log.d("GIJON", "Cargadas "+ i +" estaciones ");
			
			// close the dialog and shows the number of stations loaded
			if (dialog.isShowing()) {
				//dialog.setMessage(String.valueOf(i) + " " + mContext.getString(R.string.text_stations_loaded));
				dialog.dismiss();
			}
			Toast.makeText(
					mContext,
					(new StringBuilder(String.valueOf(i))).append(" ")
							.append(mContext.getString(R.string.text_stations_loaded)).toString(),Toast.LENGTH_SHORT).show();
		} catch (Exception exception) {
			Log.e("GIJON", (new StringBuilder("ERROR al escribir el fichero "))
					.append(LOCAL_FILENAME).append("\n").append(exception.getMessage())
					.toString());
		}
		return;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onCancelled()
	 */
	@Override
	protected void onCancelled() {
		super.onCancelled();
//    	AlertDialog alert = AirStationsUtil.createAlertDialogNoDataLoaded(this.mContext);
//    	alert.show();
		
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onProgressUpdate(java.lang.Object[])
	 */
	protected void onProgressUpdate(Object... values) {
		super.onProgressUpdate(values);
	}



}
