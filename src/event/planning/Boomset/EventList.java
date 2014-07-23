package event.planning.Boomset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.ActionBar;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import event.planning.Boomset.RefreshableListView.OnRefreshListener;

public class EventList extends SlidingFragmentActivity {

	private AlertDialog.Builder logOutDialog;
	private AlertDialog alertDialog;
	private AlertDialog.Builder connectionBuilder;
	private AlertDialog connectionDialog;
	private AlertDialog.Builder autoSyncBuilder;
	private AlertDialog autoSyncDialog;
	private RefreshableListView eventList;
	protected ImageLoader imageLoader;
	private ProgressDialog pdia;
	private SlidingMenu slidingMenu;
	private EditText searchEvent;
	private EventsDatabaseAdapter eventAdapter;
	private EventAdapter eAdapter;
	private GroupsDatabaseAdapter groupAdapter;
	private String cookie;
	private String responseTwo = null;
	private String response = null;
	private String groupName, userID, groupID, userName, lastUpdated, password;
	String parametersLogin;
	private TextView logoutButton, menuUserName;
//	private ImageView autoSyncToggleImage;
	private boolean pauseOnScroll = false;
	private boolean pauseonFling = true;
	private static SharedPreferences.Editor editor;
	private List<Events> values;
	private static String jtimeStamp;
	private static List<String> testEventIDsArray = new ArrayList<String>();
	private SharedPreferences preferences;
	private LoadGuestList loadGuestList;
	private CountDownTimer eventListRefresher;
	private boolean isSynced, isAuto, isFirstName, deactivatedBoolean, isAutoSynced;
	HttpURLConnection connectionTest;
	
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Sherlock___Theme_DarkActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventlist);
		setBehindContentView(R.layout.sidemenu);
		
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.headerlayout);
		ImageView menuButton = (ImageView) findViewById(R.id.menuButton);

		imageLoader = ImageLoader.getInstance();
		
		slidingMenu = getSlidingMenu();
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = (int) (metrics.widthPixels / 4);
		isAuto = false;
		slidingMenu.setBehindOffset(width);
		slidingMenu.setFadeDegree(0.50f);
		
	//	autoSyncToggleImage = (ImageView) findViewById(R.id.syncToggle);
		
		autoSyncBuilder = new AlertDialog.Builder(this);
		autoSyncBuilder.setTitle("Auto Sync Events");
		autoSyncBuilder.setMessage("Auto syncing will allow the app to constantly update your events and check for new events");
		autoSyncBuilder.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
	//			autoSyncToggleImage.setImageResource(android.R.drawable.checkbox_on_background);
				
			}
		});
		
		autoSyncBuilder.setNegativeButton("Turn off", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
	//			autoSyncToggleImage.setImageResource(android.R.drawable.checkbox_off_background);
			}
		});
		
		
		connectionBuilder = new AlertDialog.Builder(this);
		connectionBuilder.setTitle("The guestlist cannot be downloaded");
		connectionBuilder.setMessage("The Internet connection appears to be offline");
		connectionBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		logOutDialog = new AlertDialog.Builder(this);
		logOutDialog.setTitle("Log Out");
		logOutDialog.setMessage("Are you sure you want to log out?")
				.setCancelable(true);
		logOutDialog.setPositiveButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		logOutDialog.setNegativeButton("Log Out",
				new DialogInterface.OnClickListener() {
			
					@Override
					public void onClick(DialogInterface dialog, int which) {
						editor = preferences.edit();
						editor.clear();
						editor.apply();
						
						Intent loginIntent = new Intent(EventList.this,
								LogInActivity.class);
						startActivity(loginIntent);
						finish();

					}
				});
		
		alertDialog = logOutDialog.create();
		connectionDialog = connectionBuilder.create();
		autoSyncDialog = autoSyncBuilder.create();
		
		LinearLayout addEventRow = (LinearLayout) findViewById(R.id.addEventRow);
		addEventRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent addEventIntent = new Intent(getApplicationContext(), AddEventScreen.class);
				startActivity(addEventIntent);
				finish();
			}
		});

		LinearLayout logOutRow = (LinearLayout) findViewById(R.id.LogOutRow);
		logOutRow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				alertDialog.show();
			}
		});

		LinearLayout eventRow = (LinearLayout) findViewById(R.id.eventRow);
		eventRow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				slidingMenu.toggle(true);
				
				
			}
		});

		logoutButton = (TextView) findViewById(R.id.eventsLogoutButton);
		logoutButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				alertDialog.show();

			}
		});
		

		eventList = (RefreshableListView) findViewById(R.id.eventList);
		eventList.setTextFilterEnabled(true);

		PauseOnScrollListener pauseListener = new PauseOnScrollListener(
				imageLoader, pauseOnScroll, pauseonFling);
		eventList.setOnScrollListener(pauseListener);

		searchEvent = (EditText) findViewById(R.id.searchEvents);
		searchEvent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				eAdapter.getFilter().filter(s.toString());
				eAdapter.notifyDataSetChanged();

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		menuButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				slidingMenu.showMenu(true);
			}
		});

		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		userName = preferences.getString("userName", "");
		String user = preferences.getString("userFirstName", "") + " " + preferences.getString("userLastName", "");
		if (user.equals(" ")) {
			user = preferences.getString("userFullName", "");
		}
		password = preferences.getString("password", "");
		lastUpdated = preferences.getString("lastUpdateTime", "");
		
		

		menuUserName = (TextView) findViewById(R.id.userName);
		menuUserName.setText(user);
		
	editor = preferences.edit();
	
	final ToggleButton sortNameButton = (ToggleButton) findViewById(R.id.nameToggleButton);
	boolean checked = preferences.getBoolean("sortNameBoolean", true);
	sortNameButton.setChecked(checked);
	sortNameButton.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View paramView) {
			if (sortNameButton.isChecked()) {
				editor = preferences.edit();
				isFirstName = false;
				editor.putBoolean("sortNameBoolean", isFirstName);
				editor.apply();
				
			} else {
				editor = preferences.edit();
				isFirstName = true;
				 editor.putBoolean("sortNameBoolean", isFirstName);
				 editor.apply();
				 
			} 
			
			editor.commit();
		}
	});

		eventList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(RefreshableListView listView) {

				RefreshEventLists refreshEvents = new RefreshEventLists();
				if (isNetworkAvailable()) {
					refreshEvents.execute();
				} else {
					eventList.completeRefreshing(lastUpdated);
					eventList.clearAnimation();
					Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
				}
				eventList.completeRefreshing(lastUpdated);
			}
		});
		
		boolean isSynced = preferences.getBoolean("autoSyncBoolean", false);
		final ToggleButton syncToggle = (ToggleButton) findViewById(R.id.syncToggleButton);
		syncToggle.setChecked(isSynced);
		
		eventListRefresher = new CountDownTimer(10000, 1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFinish() {
				if (isNetworkAvailable()) {
					RefreshEventLists refreshEvents = new RefreshEventLists();
					
					if (syncToggle.isChecked()) {
						refreshEvents.execute();
						Toast.makeText(getApplicationContext(), "Sync started", Toast.LENGTH_SHORT).show();
					} else {
						eventListRefresher.start();
					}
				} 
			}
		};
		

		if (isNetworkAvailable()) {
			LoadEventLists loadEventLists = new LoadEventLists();
			loadEventLists.execute();
		} else {
			eventAdapter = new EventsDatabaseAdapter(EventList.this);
			eventAdapter.open();

			String savedUserID = preferences.getString("userID", "");
			values = eventAdapter.getUserEvents(savedUserID);
			 
			if (values == null) {
				values = new ArrayList<Events>();
				Events noEventError = new Events();
				noEventError.setEventName("No Events Loaded");
				values.add(noEventError);
			} 

			eAdapter = new EventAdapter(getApplicationContext(),R.layout.event_list_row, values);
			eventList.setAdapter(eAdapter);
		}
		
		
		eventList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
				Events event = (Events) listView.getItemAtPosition(position + 1);
				String eventIDExtra = event.getEventID();
				loadGuestList = new LoadGuestList();
				if (isNetworkAvailable()) {
					eventListRefresher.cancel();
					loadGuestList.execute(eventIDExtra);
				} else {
				    ReservationAdapter reservationAdapter = new ReservationAdapter(getApplicationContext());
				    reservationAdapter.open();
				    List<Guests> guestValues;
				    boolean sortBoolean = preferences.getBoolean("sortNameBoolean", true);
					if (sortBoolean) {
						guestValues = reservationAdapter.getEventGuests(eventIDExtra);
					} else {
						guestValues = reservationAdapter.getEventLastNameGuests(eventIDExtra);
					}
					
				    reservationAdapter.close();
					if (guestValues.isEmpty() || guestValues == null) {
						connectionDialog.show();
					} else {
						Intent toGuestList = new Intent(EventList.this, GuestList.class);
						toGuestList.putExtra("eventID", eventIDExtra);
						startActivity(toGuestList);
						finish();
					}
					
				}
			}
		});
		
		deactivatedBoolean = preferences.getBoolean("deactivatedBoolean", false);
		final ToggleButton deactivatedToggle = (ToggleButton) findViewById(R.id.deletedToggleButton);
		deactivatedToggle.setChecked(deactivatedBoolean);
		deactivatedToggle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				editor = preferences.edit();
				if (deactivatedToggle.isChecked()) {
					deactivatedBoolean = true;
					editor.putBoolean("deactivatedBoolean", deactivatedBoolean);
					editor.apply();
				} else {
					deactivatedBoolean = false;
					editor.putBoolean("deactivatedBoolean", deactivatedBoolean);
					editor.apply();
				}
				editor.commit();
			}
		});
		
		
		syncToggle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editor = preferences.edit();
				if (syncToggle.isChecked()) {
					boolean isAutoSynced = false;
					editor.putBoolean("autoSyncBoolean", isAutoSynced);
					editor.apply();			
				} else {
					boolean isAutoSynced = true;
					editor.putBoolean("autoSyncBoolean", isAutoSynced);
					editor.apply();
				}
				editor.commit();
				
			}
		});
		
	}

	@Override
	protected void onPause() {
	//	eventAdapter.close();
		eventListRefresher.cancel();
		super.onPause();
	}

	private class LoadEventLists extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdia = new ProgressDialog(EventList.this);
			pdia.setTitle("Retrieving Your Events");
			pdia.setMessage("One Moment Please");
			pdia.setIndeterminate(false);
			pdia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pdia.setCancelable(false);
			pdia.show();
			
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			userName = preferences.getString("userName", "");
			userID = preferences.getString("userID", "");
			password = preferences.getString("password", "");
			lastUpdated = preferences.getString("lastUpdateTime", "");
			String getCookie = preferences.getString("cookie", "");
			

			parametersLogin = "username=" + userName + "&password=" + password
					+ "&csum=XYZ";

			if (lastUpdated.equals("") || (lastUpdated == null)) {		
			try {
				userID = preferences.getString("userID", "");
				userName = preferences.getString("userName", "");

				HttpURLConnection connection;
				URL url = null;
				url = new URL("https://www.rsvpme.in/apps/restapi/events/retrieveEventsByUser/xhr?apiVersion=1.5");
			//	url = new URL("https://www.boomset.com/apps/restapi/events/retrieveEventsByUser/xhr?apiVersion=1.5");
				
				trustEveryone();
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestProperty("Cookie", getCookie);
				connection.setRequestMethod("GET");
				connection.connect();

				String line = "";
				InputStreamReader isr = new InputStreamReader(
						connection.getInputStream());
				BufferedReader reader = new BufferedReader(isr);
				StringBuilder sb = new StringBuilder();

				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}

				response = sb.toString();
				isr.close();
				reader.close();

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			} else {
				try {
					
					
					URL url = null;
					
					String encodedArray = preferences.getString("encodedArray", "");
					String retreiveCookie = preferences.getString("cookie", "");
			//		editor.putString("lastUpdateTime", encodedTimeStamp);
					
					eventAdapter = new EventsDatabaseAdapter(EventList.this);
					eventAdapter.open();
					String userAddedEvents = eventAdapter.getUserAddedEvents();
					String encodedUserAddedEvents = URLEncoder.encode(userAddedEvents, "UTF-8");
					
					url = new URL("https://www.rsvpme.in/apps/restapi/events/retrieveEventsByUser/xhr?apiVersion=1.5&timestamp=" + lastUpdated + "&myevents=" + encodedArray + "&newEvents=" + encodedUserAddedEvents);
			//		url = new URL("https://www.boomset.com/apps/restapi/events/retrieveEventsByUser/xhr?apiVersion=1.5&timestamp=" + lastUpdated + "&myevents=" + encodedArray);
					trustEveryone();
					connectionTest = (HttpURLConnection) url.openConnection();
					connectionTest.setRequestProperty("Cookie", retreiveCookie);
					connectionTest.setRequestMethod("GET");
					connectionTest.connect();
					connectionTest.getErrorStream();
					String line = "";
					InputStreamReader isr = new InputStreamReader(connectionTest.getInputStream());
					BufferedReader reader = new BufferedReader(isr);
					StringBuilder sb = new StringBuilder();

					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}

					response = sb.toString();
					isr.close();
					reader.close();
					
					// values = eventAdapter.getUserEvents(userID);

					return null;
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (ProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
					InputStreamReader isr = new InputStreamReader(connectionTest.getErrorStream());
					BufferedReader reader = new BufferedReader(isr);
					StringBuilder sb = new StringBuilder();
					String line = "";
					try {
						while ((line = reader.readLine()) != null) {
							sb.append(line + "\n");
							
						}
						String errorString = sb.toString();
						String test ="";
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					
				} 
			}
			
			
			
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			eventAdapter = new EventsDatabaseAdapter(EventList.this);
			eventAdapter.open();
			
			groupAdapter = new GroupsDatabaseAdapter(EventList.this);
			groupAdapter.open();
			
			userID = preferences.getString("userID", "");
			if (userID != null) {
				values = eventAdapter.getUserEvents(userID);
			}
			if (values == null) {
				values = new ArrayList<Events>();
				Events noEventError = new Events();
				noEventError.setEventName("No Events Loaded");
				values.add(noEventError);
			} 

			eAdapter = new EventAdapter(getApplicationContext(), R.layout.event_list_row, values);
			eventList.setAdapter(eAdapter);
			

			try {
				if (response != null) {
					jtimeStamp = URLEncoder.encode(new JSONObject(response).getString("gmtTime"), "UTF-8");
					String refreshTimeStamp = URLDecoder.decode(jtimeStamp, "UTF-8");
					String encodedTimeStamp = URLEncoder.encode(refreshTimeStamp, "UTF-8");
					editor.putString("lastUpdateTime", encodedTimeStamp);
					editor.commit();
					JSONObject jObject = new JSONObject(response)
							.getJSONObject("g");
					JSONArray jArray = jObject.names();
					for (int i = 0; i < jArray.length(); i++) {
						groupID = jArray.getString(i);
						String group = jObject.getString(jArray.getString(i));
						JSONObject jGroupName = new JSONObject(group);
						groupName = jGroupName.getString("gN");
						
						if (!groupAdapter.exists(groupID)) {
							groupAdapter.createGroup(groupID, groupName, userID);
						}
						
						JSONObject eventsObject = jGroupName.getJSONObject("e");
						JSONArray eventIDArray = eventsObject.names();
						
						if (eventIDArray != null) {
							for (int j = 0; j < eventIDArray.length(); j++) {
								String eventID = eventIDArray.getString(j);
													
								String event = eventsObject.getString(eventIDArray.getString(j));
								JSONObject eventNameObject = new JSONObject(event);
								String eventName = eventNameObject.getString("n");
								String venueName = eventNameObject.getString("vn");
								String upgradeFlag = eventNameObject.getString("upgrade");
								String venueID = eventNameObject.getString("vID");
								String premiumFlag = eventNameObject.getString("p");
								String startDate = eventNameObject.getString("start");
								String timezone = eventNameObject.getString("tz");

								if (!eventAdapter.exists(eventID, userID)) {
									if (!testEventIDsArray.contains(eventID)) {
										testEventIDsArray.add(eventID);
										String testEventIDsString = testEventIDsArray.toString();
										String encodedArray = URLEncoder.encode(testEventIDsString, "UTF-8");
										editor.putString("encodedArray", encodedArray);
										editor.commit();
									}
									eventAdapter.createEvent(eventID, eventName,
													venueID, venueName, timezone,
													premiumFlag, upgradeFlag,
													startDate, userID, groupName, "", "", groupID, "");
									
								} else {
									eventAdapter.updateEvent(eventID, eventName, 
											venueID, venueName, timezone, 
											premiumFlag, upgradeFlag, 
											startDate, userID, groupName);
									
								}

							}
							
						}
						
					}
					// values = eventAdapter.getUserEvents(userID);
				} 
				
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			eAdapter.clear();
			values = eventAdapter.getUserEvents(userID);
			eAdapter = new EventAdapter(getApplicationContext(),R.layout.event_list_row, values);
			eventList.setAdapter(eAdapter);
			eAdapter.notifyDataSetChanged();
			pdia.dismiss();
		}

	}
	
	private class RefreshEventLists extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			HttpURLConnection connection;
			URL url = null;
			lastUpdated = preferences.getString("lastUpdateTime", "");
			String retreiveCookie = preferences.getString("cookie", "");
			String encodedArray = preferences.getString("encodedArray", "");
			
			
			try {
				if(!lastUpdated.equals("")) {
					url = new URL("https://rsvpme.in/apps/restapi/events/retrieveEventsByUser/xhr?apiVersion=1.5&timestamp=" + lastUpdated + "&myevents=" + encodedArray);
				//		url = new URL("https://www.boomset.com/apps/restapi/events/retrieveEventsByUser/xhr?apiVersion=1.5&timestamp=" + lastUpdated + "&myevents=" + encodedArray);
						trustEveryone();
						connection = (HttpURLConnection) url.openConnection();
						connection.setRequestProperty("Cookie", retreiveCookie);
						connection.setRequestMethod("GET");
						connection.connect();

						String line = "";
						InputStreamReader isr = new InputStreamReader(connection.getInputStream());
						BufferedReader reader = new BufferedReader(isr);
						StringBuilder sb = new StringBuilder();

						while ((line = reader.readLine()) != null) {
							sb.append(line + "\n");
						}

						response = sb.toString();
						isr.close();
						reader.close();
				}
				

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			
			groupAdapter = new GroupsDatabaseAdapter(EventList.this);
			groupAdapter.open();
			
			if (response != null &&  !response.equals("")) {
				try {
					jtimeStamp = URLEncoder.encode(new JSONObject(response).getString("gmtTime"), "UTF-8");
					String refreshTimeStamp = URLDecoder.decode(jtimeStamp, "UTF-8");
					String encodedTimeStamp = URLEncoder.encode(refreshTimeStamp, "UTF-8");
					editor.putString("lastUpdateTime", encodedTimeStamp);
					editor.commit();
					//eventAdapter.deleteAll();
					JSONObject jObject = new JSONObject(response).getJSONObject("g");
					JSONArray jArray = jObject.names();
					for (int i = 0; i < jArray.length(); i++) {
						groupID = jArray.getString(i);
						String group = jObject.getString(jArray.getString(i));
						JSONObject jGroupName = new JSONObject(group);
						groupName = jGroupName.getString("gN");
						
						if (!groupAdapter.exists(groupID)) {
							groupAdapter.createGroup(groupID, groupName, userID);
						}
						
						JSONObject eventsObject = jGroupName.getJSONObject("e");
						JSONArray eventIDArray = eventsObject.names();
						if (eventIDArray != null) {
							for (int j = 0; j < eventIDArray.length(); j++) {
								String eventID = eventIDArray.getString(j);	
								String event = eventsObject.getString(eventIDArray.getString(j));
								JSONObject eventNameObject = new JSONObject(event);
								String eventName = eventNameObject.getString("n");
								String venueName = eventNameObject.getString("vn");
								String upgradeFlag = eventNameObject.getString("upgrade");
								String venueID = eventNameObject.getString("vID");
								String premiumFlag = eventNameObject.getString("p");
								String startDate = eventNameObject.getString("start");
								String timezone = eventNameObject.getString("tz");
								eventAdapter.open();
								if (!eventAdapter.exists(eventID, userID)) {
									Events eventRowValue = eventAdapter
											.createEvent(eventID, eventName,
													venueID, venueName, timezone,
													premiumFlag, upgradeFlag,
													startDate, userID, groupName, "", "", groupID, "");
									eAdapter.add(eventRowValue);
									eAdapter.notifyDataSetChanged();
									if (!testEventIDsArray.contains(eventID)) {
										testEventIDsArray.add(eventID);
										String testEventIDsString = testEventIDsArray.toString();
										String encodedArrayString = URLEncoder.encode(testEventIDsString, "UTF-8");
										editor.putString("encodedArray", encodedArrayString);
										editor.commit();
									}	
								} else {
									eventAdapter.updateEvent(eventID, eventName, 
											venueID, venueName, timezone, 
											premiumFlag, upgradeFlag, 
											startDate, userID, groupName);
								}

							}
							
							
							eAdapter.clear();
							values = eventAdapter.getUserEvents(userID);
							for (int k = 0; k < values.size(); k++) {
								eAdapter.add(values.get(k));
							}
							eAdapter.notifyDataSetChanged();
						}

					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			eventListRefresher.start();
		}
	}
	
	private class LoadGuestList extends AsyncTask<String, Void, Void> {
		private ProgressDialog pdia;
		JSONObject jTransctionObject, jCustomer, jTransaction;
		JSONArray jCustomerValues, jTestArray, jReservation;
		JSONObject jReservationObject;
		private ReservationAdapter reservationAdapter;
		private String eventID;
		HttpURLConnection connection;
		@Override
		protected void onPreExecute() {
	//		extras = getIntent().getExtras();
		//	eventID = extras.getString("eventID");
			pdia = new ProgressDialog(EventList.this);
			pdia.setTitle("One Moment Please");
			pdia.setMax(100);
			pdia.setIndeterminate(false);
			pdia.setCancelable(false);
			pdia.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pdia.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					loadGuestList.cancel(true);
					pdia.cancel();
					
				}
			});
			
			pdia.show();
			
		}
		
		@Override
		protected Void doInBackground(String... eventValue) {
			int currentProgress = 0;
			eventID = eventValue[0];
			reservationAdapter = new ReservationAdapter(EventList.this);
			
			OutputStreamWriter request = null;
			URL url = null;
			String transactionID = "";
			String parameters = "dictHierarchy=True&apiVersion=1.4&eventID=" + eventID;
			if (!isCancelled()) {
				
			try {
				eventAdapter.open();
				String guestTimeStamp = eventAdapter.getGuestTimeStamp(eventID);
				
				if (!guestTimeStamp.equals("")) {
					parameters = "dictHierarchy=True&apiVersion=1.4&eventID=" + eventID + "&timestamp=" + guestTimeStamp;
					pdia.setMessage("");
				} else {
					pdia.setMessage("Retrieving the guest list for the first time.");
				}
				url = new URL("https://www.rsvpme.in/apps/restapi/reservations/retrieveReservationsByEventID/xhr");
		//		url = new URL("https://www.boomset.com/apps/restapi/reservations/retrieveReservationsByEventID/xhr");
				trustEveryone();
				connection = (HttpURLConnection) url.openConnection();
				connection.setDoOutput(true);
				String retreiveCookie = preferences.getString("cookie", "");
				connection.setRequestProperty("Cookie", retreiveCookie);
				connection.setRequestMethod("POST");
				request = new OutputStreamWriter(connection.getOutputStream());
	            request.write(parameters);
	            request.flush();
	            request.close();
			
				
				
				/*JsonFactory jFactory = new JsonFactory();
				@SuppressWarnings("deprecation")
				JsonParser jParser = jFactory.createJsonParser(connection.getInputStream());
				while (jParser.nextToken() != com.fasterxml.jackson.core.JsonToken.END_OBJECT) {
					String fieldName = jParser.getCurrentName();
					if ("timestamp".equals(fieldName)) {
						jParser.nextToken();
						String timeyStampy = jParser.getText();
					}
					
				}*/
				/*Reader readerObject = new InputStreamReader(connection.getInputStream());
				JsonReader jReader = new JsonReader(readerObject);
				String igottimeStamp = "";
				String fieldArray = "";
				ArrayList<String> testFirstNames = new ArrayList<String>();
				jReader.beginObject();
				while (jReader.hasNext()) {
					String fieldName = jReader.nextName();
					 if (fieldName.equals("reservationTypes")) {
						jReader.beginObject();
						while (jReader.hasNext()) {
							String token = jReader.peek().toString();
							if (token.equals("NAME")) {
								fieldArray = jReader.nextName();
							} else if (token.equals("BEGIN_OBJECT")) {
								jReader.beginObject();
								while (jReader.hasNext()) {
									token = jReader.peek().toString();
									if (token.equals("NAME")) {
										
										fieldArray = jReader.nextName();
										if (fieldArray.equals("objs")) {
											String foo = jReader.nextString();
										} else if (fieldArray.equals("door")) {
											boolean door = jReader.nextBoolean();
										} else if (fieldArray.equals("color")) {
											String color = jReader.nextString();
										} else if (fieldArray.equals("price")) {
											String price = jReader.nextString();
										} else if (fieldArray.equals("pricing")) {
											String pricing = jReader.nextString();
										} else if (fieldArray.equals("c")) {
											String c = jReader.nextString();
										} else if (fieldArray.equals("n")) {
											String name = jReader.nextString();
										}
									} else {
										jReader.skipValue();
									}
								}
								jReader.endObject();
							} else {
								jReader.skipValue();
							}
						}
						jReader.endObject();
					} else {
						jReader.skipValue();
					}
					
				}*/
				
				String line = "";
				InputStreamReader isr = new InputStreamReader(connection.getInputStream());
				BufferedReader reader = new BufferedReader(isr);
				StringBuilder sb = new StringBuilder();

				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}

				response = sb.toString();
				isr.close();
				reader.close();
				currentProgress = 25;
				pdia.setProgress(currentProgress);
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			}
			try {
				
				//	String jEventID = new JSONObject(response).getJSONObject("eventID").toString();
				if (response != null) {
					reservationAdapter.open();
					 jCustomer = new JSONObject(response);
					 String reservationTimeStamp = jCustomer.getString("timestamp");
					 String jGuestTimeStamp = URLEncoder.encode(reservationTimeStamp, "UTF-8");
					 String refreshTimeStamp = URLDecoder.decode(jGuestTimeStamp, "UTF-8");
					 String encodedGuestTimeStamp = URLEncoder.encode(refreshTimeStamp, "UTF-8");
					 eventAdapter.open();
					 eventAdapter.updateGuestTimeStamp(encodedGuestTimeStamp, eventID);
					 eventAdapter.close();
					 JSONObject jReservationTypes = jCustomer.getJSONObject("reservationTypes"); 
					 Iterator<String> reservationTypeKeys = jReservationTypes.keys();
					 List<String> listReservationTypeKeys = new ArrayList<String>();
					 while (reservationTypeKeys.hasNext()) {
						listReservationTypeKeys.add(reservationTypeKeys.next());
					}
					 jTestArray = jReservationTypes.names();
					 if (listReservationTypeKeys != null && !isCancelled()) {
						 for (int i = 0; i < listReservationTypeKeys.size(); i++) {
							 String reservationTypeString = jReservationTypes.getString(listReservationTypeKeys.get(i));
							 JSONObject jReservationType = new JSONObject(reservationTypeString);
							 String reservationTypeID = listReservationTypeKeys.get(i);
							 if (jReservationType.has("n")) {
								 String guestDescription = jReservationType.getString("n");
								 String door = jReservationType.getString("door");
								 String color = jReservationType.getString("color");
								 String price = jReservationType.getString("price");
								 String pricing = jReservationType.getString("pricing");
								 if (!reservationAdapter.typeExists(reservationTypeID)) {
									 reservationAdapter.createReservationType(reservationTypeID, guestDescription, door, color, price, pricing);
								} else {
									reservationAdapter.updateReservationType(reservationTypeID, guestDescription, door, color, price, pricing);
								}
							} else {
								if(jReservationType.has("s")) {
									// TODO add to table
								}
							}
							 
						 }
						 
						 currentProgress = 50;
						 pdia.setProgress(currentProgress);
						 
					 }
							 jTransaction = jCustomer.getJSONObject("transactionDict");
							 jReservation = jTransaction.names();
							 if (jReservation != null) {
								 for (int j = 0; j < jReservation.length(); j++) {
									 if (!isCancelled()) {
									String transaction = jTransaction.getString(jReservation.getString(j));
									 jTransctionObject = new JSONObject(transaction);
									 if (jTransctionObject.has("v")) {
										 JSONObject jTransactionV = jTransctionObject.getJSONObject("v");
										 transactionID = jReservation.getString(j);
										 String amount = jTransactionV.getString("a");
										 String paidType = jTransactionV.getString("p");
										 String objectiveStatus = jTransactionV.getString("objS");
										 String quantity = jTransactionV.getString("q");
										 String transGroupID = jTransactionV.getString("tgID");
										 String transGroupBuyer = jTransactionV.getString("tgB");
										 String transDate = jTransactionV.getString("trD");
										 String reservationTypeID = jTransactionV.getString("rtID");
										 reservationAdapter.open();
										 if (!reservationAdapter.transactionExists(transactionID)) {
											 reservationAdapter.createTransaction(transactionID, amount, paidType, objectiveStatus, quantity, transGroupID, transGroupBuyer, transDate, reservationTypeID, eventID);
										} else {
											reservationAdapter.updateTransaction(transactionID, amount, paidType, objectiveStatus, quantity, transGroupID, transGroupBuyer, transDate, eventID);
										}
									}
									 
									 if (jTransctionObject.has("r")) {
										 JSONObject jCustomerR = jTransctionObject.getJSONObject("r");
										 JSONArray jReservationNumber = jCustomerR.names();
										 if (jReservationNumber != null) {
											 for (int k = 0; k < jReservationNumber.length(); k++) {
												 
												 JSONObject jOperation = jCustomerR.getJSONObject(jReservationNumber.getString(k)).getJSONObject("ch");
												 String reservationID = jReservationNumber.getString(k);
												 JSONArray jOperationIDs = jOperation.names();
												 if (jOperationIDs != null) {
													for (int l = 0; l < jOperationIDs.length(); l++) {
														 String jOperationIDString = jOperation.getString(jOperationIDs.getString(l));
														 String operationIDStringFormat = jOperationIDString.replace("[", "").replace("]", "").replace("\"", "");
														 String[] operationIDValues = operationIDStringFormat.split(",");
														 String operationID = jOperationIDs.getString(l);
													/*	 String operationUserID = operationIDValues[0];
														 String operationQuantity = operationIDValues[2];
														 String operationCheckInTime = operationIDValues[3];
														 String operationObjectStatus = operationIDValues[4];*/
														 if (!reservationAdapter.operationExists(operationID)) {
															 reservationAdapter.createOperation(operationID, operationIDValues[0], operationIDValues[2], operationIDValues[3], operationIDValues[4], reservationID, "0");
															}							
													}
												}
												 
												 if (jCustomerR.getJSONObject(jReservationNumber.getString(k)).has("v")) {
													 jReservationObject = jCustomerR.getJSONObject(jReservationNumber.getString(k)).getJSONObject("v").getJSONObject("c");
													 String guestFirstName = jReservationObject.getString("fN");
													 String guestLastName = jReservationObject.getString("lN");
													 String guestEmail = jReservationObject.getString("e");
													 String guestPhone = jReservationObject.getString("p");
													 String guestID = jReservationObject.getString("id");
													 String guestOperationStatus = jCustomerR.getJSONObject(jReservationNumber.getString(k)).getJSONObject("v").getString("objS");
													 String guestQuantity = jCustomerR.getJSONObject(jReservationNumber.getString(k)).getJSONObject("v").getString("q");
													 String guestPaidType = jCustomerR.getJSONObject(jReservationNumber.getString(k)).getJSONObject("v").getString("p");
													 
													 if (!reservationAdapter.reservationExists(reservationID)) {
														reservationAdapter.createReservation(eventID, reservationID, guestFirstName, guestLastName, guestID, guestPhone, guestEmail, transactionID, guestQuantity, guestPaidType, guestOperationStatus);
													} else {
														reservationAdapter.updateReservation(eventID, reservationID, guestFirstName, guestLastName, guestID, guestPhone, guestEmail, guestQuantity, guestPaidType, guestOperationStatus);
													}
												}
												 
												 
												 
											}
											 
										}
									}
									 
								 }
									 
									 if (currentProgress < 99) {
										currentProgress++;
										pdia.setProgress(currentProgress);
									}
									 
							}
					}
					
				pdia.setProgress(100);	 
				reservationAdapter.close();	
				}
					
				} catch (JSONException e) {
					e.printStackTrace();
					reservationAdapter.close();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			
			
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Intent toGuestList = new Intent(EventList.this, GuestList.class);
			toGuestList.putExtra("eventID", eventID);
			startActivity(toGuestList);
			finish();
			pdia.dismiss();
		}
	}
	

	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	@Override
	protected void onStop() {
		imageLoader.clearMemoryCache();
		imageLoader.clearDiscCache();
		eventListRefresher.cancel();
		super.onStop();
	}
	
	private void trustEveryone() {
		try {
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
	    			public boolean verify(String hostname, SSLSession session) {
	    				return true;
	    			}});
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new X509TrustManager[]{new X509TrustManager(){
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {}
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {}
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}}}, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(
					context.getSocketFactory());
		} catch (Exception e) { 
			e.printStackTrace();
		}	
}
	@Override
	protected void onResume() {
		
			eventListRefresher.start();
		
		super.onResume();
	}
	
}
