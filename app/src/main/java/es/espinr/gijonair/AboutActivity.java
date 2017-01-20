package es.espinr.gijonair;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Locale;

public class AboutActivity extends AppCompatActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pagerAbout);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		//int id = item.getItemId();
		// if (id == R.id.action_settings) {
		// return true;
		// }
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		public PlaceholderFragment() {
		}

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			Context mContext = container.getContext();

			RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(
					R.layout.fragment_about, container, false);

			Bundle args = this.getArguments();

			switch (args.getInt(ARG_SECTION_NUMBER)) {
			case 0:
				ScrollView scrollView = new ScrollView(mContext);
				scrollView.setLayoutParams(new LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

				TextView leyendText = new TextView(mContext);
				leyendText.setText(mContext.getString(R.string.leyend));
				Linkify.addLinks(leyendText, Linkify.ALL);
				leyendText.setLinksClickable(true);
				scrollView.addView(leyendText);
				relativeLayout.addView(scrollView);
				break;
/*			case 1:
				// Add the image with the stations on a map
				StationImageView stationsImage= new StationImageView(
						mContext);
				stationsImage
						.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
				stationsImage.setPadding(10, 10, 10, 10);
				stationsImage
						.setLayoutParams(new LayoutParams(
								-2, -2));
				stationsImage.setAdjustViewBounds(true);
				stationsImage.setImageResource(R.drawable.mapaestaciones);
				relativeLayout.addView(stationsImage);
				break;
*/
				case 1:
				ScrollView scrollView2 = new ScrollView(mContext);
				scrollView2.setLayoutParams(new LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

				LinearLayout linearlayout = new LinearLayout(mContext);
					linearlayout.setOrientation(LinearLayout.VERTICAL);
				linearlayout
						.setLayoutParams(new android.widget.AbsListView.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				TextView textview = new TextView(mContext);
				textview.setText(mContext.getString(R.string.app_name));
				textview.setTextSize(TypedValue.COMPLEX_UNIT_MM, 4F);
				Linkify.addLinks(textview, Linkify.ALL);
				// textview.setTextAppearance(context, R.string.app_version);
				textview.setLayoutParams(new LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				textview.setGravity(Gravity.CENTER);
				linearlayout.addView(textview);
				TextView textview1 = new TextView(mContext);
					textview1.setText(new StringBuilder().append(mContext.getString(R.string.app_version)).append(" ").append(BuildConfig.VERSION_NAME).toString());
				textview1.setTextSize(TypedValue.COMPLEX_UNIT_MM, 2F);
				Linkify.addLinks(textview1, Linkify.ALL);
				// textview1.setTextAppearance(context, 0x7f0b008b);
				textview1
						.setLayoutParams(new android.widget.AbsListView.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				textview1.setGravity(Gravity.CENTER);
				linearlayout.addView(textview1);


				// Add the image of the app
				StationImageView stationimageview = new StationImageView(
						mContext);
				stationimageview
						.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
				stationimageview.setPadding(30, 30, 30, 30);
				stationimageview
						.setLayoutParams(new android.widget.AbsListView.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
				stationimageview.setAdjustViewBounds(true);
				stationimageview.setImageResource(R.drawable.ic_launcher);
				linearlayout.addView(stationimageview);

					TextView textIssues = new TextView(mContext);
				textIssues.setText(mContext.getString(R.string.issues));
				Linkify.addLinks(textIssues, Linkify.ALL);
				textIssues.setLayoutParams(new android.widget.AbsListView.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				textIssues.setGravity(Gravity.CENTER);
				linearlayout.addView(textIssues);



				TextView textDisclaimer = new TextView(mContext);
				textDisclaimer.setPadding(0, 10, 0, 10);
				textDisclaimer.setText(mContext.getString(R.string.text_disclaimer));
				Linkify.addLinks(textDisclaimer, Linkify.ALL);
				// textview.setTextAppearance(context, R.string.app_version);
				textDisclaimer.setLayoutParams(new android.widget.AbsListView.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				linearlayout.addView(textDisclaimer);
				linearlayout.setScrollContainer(true);


					scrollView2.addView(linearlayout);
				relativeLayout.addView(scrollView2);

					break;
			default:
				break;
			}
			return relativeLayout;
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			return PlaceholderFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
				case 0:
					return getString(R.string.title_section_leyend).toUpperCase(l);
				case 1:
					return getString(R.string.title_section_about).toUpperCase(l);
			}

			return null;
		}
	}

}
