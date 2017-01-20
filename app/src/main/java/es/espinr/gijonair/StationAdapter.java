package es.espinr.gijonair;

import android.content.Context;
import android.content.res.AssetManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

// Referenced classes of package es.espinr.gijonair:
//            AirStation, StationImageView

public class StationAdapter extends BaseAdapter {

	private AirStation airStation;
	private Context mContext;
	private AssetManager assetManager;

	public StationAdapter(Context context, AirStation airstation) {
		mContext = context;
		airStation = airstation;
		assetManager = context.getResources().getAssets();
	}

	public int getCount() {
		return airStation.getIndicadores().length;
	}

	public Object getItem(int i) {
		return null;
	}

	public long getItemId(int i) {
		return 0L;
	}

	public int getResourceFromIca(String s) {
		if (s.equals(AirStationsUtil.getConfigProperty(assetManager, "source.json.value.ica.verygood"))) {
			return R.drawable.muybuena;
		} else if (s.equals(AirStationsUtil.getConfigProperty(assetManager, "source.json.value.ica.good"))) {
			return R.drawable.buena;
		} else if (s.equals(AirStationsUtil.getConfigProperty(assetManager, "source.json.value.ica.poor"))) {
			return R.drawable.mala;
		} else if (s.equals(AirStationsUtil.getConfigProperty(assetManager, "source.json.value.ica.verypoor"))) {
			return R.drawable.muymala;
		}
		return R.drawable.nd;
	}

	/* 
	 * Returns the label corresponding to the indicator provided as argument.
	 */
	private String getLabelIndicator(String indicatorSymbol) {
		if (indicatorSymbol.equals(AirStationsUtil.getConfigProperty(assetManager, "source.json.indicator.pm10"))) {
			return this.mContext.getString(R.string.indicator_symbol_pm10);
		}
		if (indicatorSymbol.equals(AirStationsUtil.getConfigProperty(assetManager, "source.json.indicator.ben"))) {
			return this.mContext.getString(R.string.indicator_symbol_ben);
		}
		if (indicatorSymbol.equals(AirStationsUtil.getConfigProperty(assetManager, "source.json.indicator.pm25"))) {
			return this.mContext.getString(R.string.indicator_symbol_pm25);
		}
		if (indicatorSymbol.equals(AirStationsUtil.getConfigProperty(assetManager, "source.json.indicator.so2"))) {
			return this.mContext.getString(R.string.indicator_symbol_so2);
		}
		if (indicatorSymbol.equals(AirStationsUtil.getConfigProperty(assetManager, "source.json.indicator.co"))) {
			return this.mContext.getString(R.string.indicator_symbol_co);
		}
		if (indicatorSymbol.equals(AirStationsUtil.getConfigProperty(assetManager, "source.json.indicator.no"))) {
			return this.mContext.getString(R.string.indicator_symbol_no);
		}
		if (indicatorSymbol.equals(AirStationsUtil.getConfigProperty(assetManager, "source.json.indicator.o3"))) {
			return this.mContext.getString(R.string.indicator_symbol_o3);
		}
		return indicatorSymbol;
	}

	private String getDescriptionIndicator(String indicatorSymbol) {
		if (indicatorSymbol.equals(AirStationsUtil.getConfigProperty(assetManager, "source.json.indicator.pm10"))) {
			return this.mContext.getString(R.string.indicator_text_pm10);
		}
		if (indicatorSymbol.equals(AirStationsUtil.getConfigProperty(assetManager, "source.json.indicator.ben"))) {
			return this.mContext.getString(R.string.indicator_text_ben);
		}
		if (indicatorSymbol.equals(AirStationsUtil.getConfigProperty(assetManager, "source.json.indicator.pm25"))) {
			return this.mContext.getString(R.string.indicator_text_pm25);
		}
		if (indicatorSymbol.equals(AirStationsUtil.getConfigProperty(assetManager, "source.json.indicator.so2"))) {
			return this.mContext.getString(R.string.indicator_text_so2);
		}
		if (indicatorSymbol.equals(AirStationsUtil.getConfigProperty(assetManager, "source.json.indicator.co"))) {
			return this.mContext.getString(R.string.indicator_text_co);
		}
		if (indicatorSymbol.equals(AirStationsUtil.getConfigProperty(assetManager, "source.json.indicator.no"))) {
			return this.mContext.getString(R.string.indicator_text_no);
		}
		if (indicatorSymbol.equals(AirStationsUtil.getConfigProperty(assetManager, "source.json.indicator.o3"))) {
			return this.mContext.getString(R.string.indicator_text_o3);
		}
		return indicatorSymbol;
	}
	
	public View getView(int i, View view, ViewGroup viewgroup) {
		String values[] = airStation.getValores();
		String indicators[] = airStation.getIndicadores();
		String icas[] = airStation.getIca();
		RelativeLayout relativelayout = new RelativeLayout(mContext);
		relativelayout
				.setLayoutParams(new android.widget.AbsListView.LayoutParams(
						-1, -1));
		StationImageView stationimageview = new StationImageView(mContext);
		stationimageview
				.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
		stationimageview.setPadding(10, 10, 10, 10);
		stationimageview
				.setLayoutParams(new android.widget.AbsListView.LayoutParams(
						-2, -2));
		stationimageview.setAdjustViewBounds(true);
		stationimageview.setImageResource(getResourceFromIca(icas[i]));
		relativelayout.addView(stationimageview);
		TextView textview = new TextView(mContext);
		textview.setText(getLabelIndicator(indicators[i]));
		textview.setTextScaleX(1F);
		textview.setLayoutParams(new android.widget.AbsListView.LayoutParams(
				-1, -1));
		textview.setGravity(Gravity.CENTER);
		textview.setTextColor(0xff444444);
		
		relativelayout.addView(textview);
		if (icas[i].equals("")) {
			relativelayout.setTag(airStation.getLabel() + "\n" + 
					this.getDescriptionIndicator(indicators[i]) +  
					": " + this.mContext.getString(R.string.no_available));
		} else {
			relativelayout.setTag(airStation.getLabel() + "\n" + 
					this.getDescriptionIndicator(indicators[i]) +  
					": " + values[i] + this.mContext.getString(R.string.unit_measure) + "\n" + icas[i]);
		}		
		
		relativelayout.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Toast.makeText(v.getContext(),
						v.getTag().toString(), Toast.LENGTH_SHORT).show();			}
		});

		return relativelayout;
	}
}
