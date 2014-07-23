package event.planning.Boomset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class ChooseVenueScreen extends SlidingFragmentActivity {

	private ArrayList<Venues> venueList;
	private double longitude, latitude;
	private ProgressDialog pdia;
	private ListView venueListView;
	private SlidingMenu slidingMenu;
	private Button submitButton;
	private TextView selectVenueText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Sherlock___Theme_DarkActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.venuelayout);
		
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.headerlayout);
		ImageView menuButton = (ImageView) findViewById(R.id.menuButton);
		menuButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				slidingMenu.showMenu(true);
			}
		});
		
		setBehindContentView(R.layout.sidemenu);
		slidingMenu = getSlidingMenu();
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = (int) (metrics.widthPixels / 4);
		slidingMenu.setBehindOffset(width);
		slidingMenu.setFadeDegree(0.50f);
		
		Typeface typeface = Typeface.createFromAsset(getAssets(), "roboto_light.ttf");
		
		selectVenueText = (TextView) findViewById(R.id.SelectAVenueText);
		selectVenueText.setTypeface(typeface);
		
		final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		final LocationListener listener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String paramString, int paramInt,
					Bundle paramBundle) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderEnabled(String paramString) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderDisabled(String paramString) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLocationChanged(Location paramLocation) {
				// TODO Auto-generated method stub
				
			}
		};
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		longitude = location.getLongitude();
		latitude = location.getLatitude();
		
		venueListView = (ListView) findViewById(R.id.venueList);
		final EditText createVenue = (EditText) findViewById(R.id.createVenue);
		
		submitButton = (Button) findViewById(R.id.submitVenueButton);
		submitButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent backToAddEvents = new Intent();
				backToAddEvents.putExtra("venueName", createVenue.getText().toString());
				setResult(RESULT_OK, backToAddEvents);
				finish();
			}
		});
		
		FourSquareVenue fourSquareVenue = new FourSquareVenue();
		fourSquareVenue.execute();
		
	}
	
private class FourSquareVenue extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			pdia = new ProgressDialog(ChooseVenueScreen.this);
			pdia.setTitle("Loading nearby venues");
			pdia.setMessage("One Moment Please");
			pdia.setIndeterminate(false);
			pdia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pdia.setCancelable(false);
			pdia.show();
			venueList = new ArrayList<Venues>();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			HttpURLConnection connection;
		    URL url = null;   
		    String client_id = "4GXEOAPTI5UPNGOT2CCYKYHC0P5DXFQZHS2OAXURFN3NFJ5N";
	        String secret = "WXDV4H25AH1JXAXOA0ZIOEM5LKMN2W4SZZZVJ5ROBXH1ALDL";
	        Calendar c = Calendar.getInstance(); 
			int day = c.get(Calendar.DAY_OF_MONTH);
			String dayParameter = "";
			if (day < 10) {
				dayParameter = "0" + Integer.toString(day);
			} else {
				dayParameter = Integer.toString(day);
			}
			
			int month = c.get(Calendar.MONTH);
			String monthParameter = "";
			if (month < 10) {
				monthParameter = "0" + Integer.toString(month);
			} else {
				monthParameter = Integer.toString(month);
			}
			
			int year = c.get(Calendar.YEAR);
			String dateParameter = Integer.toString(year) + monthParameter + dayParameter;
			String locationParameter = Double.toString(latitude) + "," + Double.toString(longitude);
				try {
					url = new URL("https://api.foursquare.com/v2/venues/search?ll=" + locationParameter + "&client_id=" + client_id + "&client_secret=" + secret + "&v=" + dateParameter + "&limit=20");
					connection = (HttpURLConnection) url.openConnection();
		            connection.setDoOutput(true);
		            connection.setRequestMethod("GET");  
		            String line = "";               
		            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
		            BufferedReader reader = new BufferedReader(isr);
		            StringBuilder sb = new StringBuilder();
		            while ((line = reader.readLine()) != null)
		            {
		                sb.append(line + "\n");
		            }             
		            
		            String response = sb.toString();
		            JSONObject jObject = new JSONObject(response).getJSONObject("response");
		            JSONArray jVenuesArray = jObject.getJSONArray("venues");
		            for (int i = 0; i < jVenuesArray.length(); i++) {
		            	JSONObject jVenueObject = jVenuesArray.getJSONObject(i);
		            	JSONArray foo = jVenueObject.names();
		            	String venue = jVenueObject.getString("name");
		            	JSONObject jLocation = jVenueObject.getJSONObject("location");
		            	String address = jLocation.getString("address");
		            	Venues venueObject = new Venues();
		            	venueObject.setVenueName(venue);
		            	venueObject.setVenueAddress(address);
		            	venueList.add(venueObject);
					}
		            
				} catch (MalformedURLException e) {
					
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				 
			return null;	
		}
		
		@Override
		protected void onPostExecute(Void result) {
			VenueAdapter vAdapter = new VenueAdapter(getApplicationContext(), R.layout.venue_list_row, venueList);
			venueListView.setAdapter(vAdapter);
			pdia.cancel();
			venueListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> listView, View view,
						int position, long id) {
					Venues venue = (Venues) listView.getItemAtPosition(position);
					Intent returnIntent = new Intent();
					returnIntent.putExtra("venueName", venue.getVenueName());
					setResult(RESULT_OK, returnIntent);
					finish();
					
				}
				
			});
			super.onPostExecute(result);
		}
	}
	
}
