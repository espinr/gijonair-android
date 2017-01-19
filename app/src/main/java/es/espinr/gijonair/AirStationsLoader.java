package es.espinr.gijonair;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import es.espinr.gijonair.utils.TimeDifference;

public class AirStationsLoader {

	private static final String TAG = "AirStationsLoader";
	private AirStation airStationsArray[];
	private Context mContext;
	private LinearLayout stationsContainer;

	public AirStationsLoader(Context context, LinearLayout linearlayout) {
		mContext = context;
		stationsContainer = linearlayout;
	}

	private void clearView() {
		stationsContainer.removeAllViewsInLayout();
	}

	private AirStation[] loadStations()
    {
        AirStation stations[] = null;
        
        try {
        
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(mContext.openFileInput(StationsFileCacher.LOCAL_FILENAME)));
        StringBuffer stringbuffer = new StringBuffer();
        String s;
        while ((s = bufferedreader.readLine())!= null){
        	stringbuffer.append(s).append("\n");
        }
       	JSONArray jsonArray= null;
       	
		try {
			jsonArray = new JSONArray(stringbuffer.toString());
		} catch (JSONException e) {
        	Log.e(TAG, (new StringBuilder("Error loading JSON: ")).append(s).toString());
		}
       	
       	if (jsonArray==null) {
       		return null;
       	}
       	
       	stations = new AirStation[jsonArray.length()];
       	
       	for (int i = 0; i < stations.length; i++) {

			// Gathers information about each station
			JSONObject jsonStation = (JSONObject) jsonArray.get(i);
       		   		
			String airStationLabel = jsonStation.getString(this.mContext.getString(R.string.json_label_name));
			String airStationDateTime = jsonStation.getString(this.mContext.getString(R.string.json_label_time));
			
			// The indicators, icas, and values
			JSONArray jsonIndicators = jsonStation.getJSONArray(this.mContext.getString(R.string.json_label_indicator));
			JSONArray jsonIcas = jsonStation.getJSONArray(this.mContext.getString(R.string.json_label_ica));
			JSONArray jsonValues = jsonStation.getJSONArray(this.mContext.getString(R.string.json_label_value));
			
			// Limits to the length of the icas
			String[] indicators = new String[jsonIcas.length()];
			String[] values = new String[jsonIcas.length()];
			String[] icas = new String[jsonIcas.length()];
			
			for (int j = 0; j < jsonIndicators.length(); j++) {
				if (j<jsonIcas.length()) {
					indicators[j] = jsonIndicators.getString(j);
					icas[j] = jsonIcas.getString(j);
					values[j] = jsonValues.getString(j);
				}
			}

			//stations[i] = new AirStation(Html.fromHtml(airStationLabel,Html.FROM_HTML_MODE_LEGACY).toString(), airStationDateTime, indicators, values, icas);
			stations[i] = new AirStation(airStationLabel, airStationDateTime, indicators, values, icas);
			
		}
        } catch (IOException ioexception) {
            Log.e(TAG, (new StringBuilder("Error opening JSON: ")).append(StationsFileCacher.LOCAL_FILENAME).toString());
        } catch (JSONException e) {
        	Log.e(TAG, (new StringBuilder("Error loading JSON\n")).append(e.getMessage()).toString());
		}
        return stations;
    }

	private int loadViewStations() {
		clearView();
		int j;
		for (j = 0; (airStationsArray != null) && (j < airStationsArray.length); j++) {
			AirStation airstation = airStationsArray[j];
			TextView titleStation = new TextView(mContext);
			titleStation.setTextColor(Color.rgb(55, 59, 68));
			titleStation.setTextScaleX(0.9F);
			titleStation.setSingleLine();
			titleStation.setGravity(Gravity.START);
			titleStation.setText(airstation.getLabel());
			
			LinearLayout linearlayout = new LinearLayout(mContext);
			linearlayout.setOrientation(LinearLayout.HORIZONTAL);
			linearlayout
					.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
							-1, -2, 1.0F));
			linearlayout.setBackgroundColor(Color.rgb(225, 245, 196));
			
			linearlayout.addView(titleStation);
			LinearLayout linearlayout1 = new LinearLayout(mContext);
			linearlayout1.setOrientation(LinearLayout.VERTICAL);
			linearlayout1
					.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
							-1, -1, 1.0F));
			linearlayout1.addView(linearlayout);
			
			// The dateTime from the JSON is in UCT timezone
			try {
				DateFormat dFormatIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
			    dFormatIn.setTimeZone(TimeZone.getTimeZone("UTC"));
				Date dateUTC = dFormatIn.parse(airstation.getTime());
								
				// Gets the current time
				long timeMilis = System.currentTimeMillis();
				Date now = new Date(timeMilis);				
								
				// Search for the time difference
				TimeDifference timeDiff = new TimeDifference(this.mContext);
				long[] diffs = timeDiff.getTimeDifference(now, dateUTC);

				TextView updatedText = new TextView(mContext);
				updatedText.setText(this.mContext.getString(R.string.updated) + " " + timeDiff.getDifferencesTextual(diffs));
				updatedText.setTextScaleX(0.6F);
				updatedText.setGravity(Gravity.END);
				updatedText.setTextColor(Color.rgb(200, 200, 200));
				updatedText.setPadding(20, 0, 0, 0);
				updatedText.setSingleLine();
				linearlayout.addView(updatedText);
				Log.d(TAG, "Local datetime is [" + now + "]" + " and the station has [" + airstation.getTime() + "], so :" + timeDiff.getDifferencesTextual(diffs));

			} catch (ParseException e) {
				Log.e(TAG, "Error when parsing the date " + airstation.getTime());
			}

			GridView gridview = new GridView(mContext);
			gridview.setAdapter(new StationAdapter(mContext, airstation));
			gridview.setNumColumns(airStationsArray[j].getIndicadores().length);
			linearlayout1.addView(gridview);
			stationsContainer.addView(linearlayout1);
		}
		return j;
	}
	

	public int execute() {
		this.airStationsArray = loadStations();
		return loadViewStations();
	}
}
