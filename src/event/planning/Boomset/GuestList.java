package event.planning.Boomset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.google.gson.Gson;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;
import event.planning.Boomset.RefreshableListView.OnRefreshListener;

public class GuestList extends SlidingFragmentActivity {
	
	private SlidingMenu slidingMenu;
	private AlertDialog.Builder logOutDialog;
	private AlertDialog alertDialog;
	private AlertDialog.Builder autoSyncBuilder;
	private AlertDialog autoSyncDialog;
	private SharedPreferences.Editor editor;
	private SharedPreferences preferences;
	private String userName;
	private TextView menuUserName;
//	private ImageView autoSyncToggleImage;
	TextView totalAttendenceText;
	private GuestAdapter gAdapter;
	private ReservationAdapter reservationAdapter;
	private RefreshableListView guestList;
	private String response = null;
	private String eventID;
	private Bundle extras;
	private JSONObject jTransaction;
	private JSONArray jReservation;
	private List<Guests> values;
	private EditText searchGuests;
	private LoadGuestList loadGuestList = new LoadGuestList();
	private CountDownTimer guestListRefresher;
	private HttpURLConnection connection;
	private Gson gsonObject;
	private EventsDatabaseAdapter eventAdapter;
	private SlideExpandableListAdapter slideAdapter;
	private boolean isSynced = true;
	private boolean isFirstName = true;
	private boolean deactivatedBoolean, isAutoSynced;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Sherlock___Theme_DarkActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guestlist);
		setBehindContentView(R.layout.sidemenu);
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.headerlayout);
		totalAttendenceText = (TextView) findViewById(R.id.attendenceText);
		gsonObject = new Gson();
		ImageView menuButton = (ImageView) findViewById(R.id.menuButton);
		menuButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				slidingMenu.showMenu(true);
			}
		});
		
		slidingMenu = getSlidingMenu();
		slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = (int) (metrics.widthPixels / 4);
		slidingMenu.setBehindOffset(width);
		slidingMenu.setFadeDegree(0.50f);
		
		slidingMenu.setSecondaryMenu(R.layout.addguestsidemenu);
		slidingMenu.setSecondaryShadowDrawable(R.drawable.shadowright);
		
		Button testButton = (Button) findViewById(R.id.addGuestButton);
		testButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
	//	autoSyncToggleImage = (ImageView) findViewById(R.id.syncToggle);
		
		autoSyncBuilder = new AlertDialog.Builder(this);
		autoSyncBuilder.setTitle("Auto Sync Guest List");
		autoSyncBuilder.setMessage("Auto syncing will allow the app to constantly update your guest list and check for new guests");
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
		
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String user = preferences.getString("userFirstName", "") + " " + preferences.getString("userLastName", "");
		if (user.equals(" ")) {
			user = preferences.getString("userFullName", "");
		}
		userName = preferences.getString("userName", "");
		isSynced = preferences.getBoolean("syncBoolean", true);
		menuUserName = (TextView) findViewById(R.id.userName);
		menuUserName.setText(user);
		editor = preferences.edit();
		
		/*CheckBox syncBox = (CheckBox) findViewById(R.id.syncToggle);
		syncBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isSynced = isChecked;
				editor.putBoolean("syncBoolean", isSynced);
				if (isSynced) {
					guestListRefresher.start();
				} else {
					guestListRefresher.cancel();
				}
			}
		});*/
		
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
						editor.clear();
						editor.apply();
						Intent loginIntent = new Intent(GuestList.this,
								LogInActivity.class);
						startActivity(loginIntent);
						finish();

					}
				});
		
		alertDialog = logOutDialog.create();
		
		
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
				Intent groupListIntent = new Intent(GuestList.this,
						EventList.class);
				startActivity(groupListIntent);
				finish();
			}
		});
		
		autoSyncDialog = autoSyncBuilder.create();
		
		LinearLayout autoSyncRow = (LinearLayout) findViewById(R.id.groupRow);
		autoSyncRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				autoSyncDialog.show();
				
			}
		});
		
		guestList = (RefreshableListView) findViewById(R.id.guestList);
		
		
			reservationAdapter = new ReservationAdapter(GuestList.this);
			reservationAdapter.open();
			
			extras = getIntent().getExtras();
			eventID = extras.getString("eventID");
			boolean sortBoolean = preferences.getBoolean("sortNameBoolean", true);
			boolean isDeactivated = preferences.getBoolean("deactivatedBoolean", false);
			if (sortBoolean) {
				if (isDeactivated) {
					values = reservationAdapter.getEventDeletedGuests(eventID);
				} else {
					values = reservationAdapter.getEventGuests(eventID);
				}	
			} else {
				if (isDeactivated) {
					values = reservationAdapter.getEventDeletedLastNameGuests(eventID);
				} else {
					values = reservationAdapter.getEventLastNameGuests(eventID);
				}
				
			}
			if (values.isEmpty() || values == null) {
				Guests guestNull = new Guests();
				guestNull.setGuestName("Guest List not loaded for this event");
				 guestNull.setGuestTitle("General Admission");
				 guestNull.setStampType("free");
				 guestNull.setNotificationStatus("star");
				 guestNull.setCurrentGuestValue("0");
				 guestNull.setTotalGuestValue("0");
				values.add(guestNull);
			}
			gAdapter = new GuestAdapter(getApplicationContext(), R.layout.guest_list_row, values);
			slideAdapter = new SlideExpandableListAdapter(gAdapter, R.id.guestRowButton, R.id.guestButtonsLayout);
			
			guestList.setAdapter(slideAdapter);
			
			
			String guestListValue = reservationAdapter.getTotalEventGuests(eventID);
			String currentGuests = reservationAdapter.getCurrentEventGuests(eventID);
			totalAttendenceText.setText("Attendence: " + currentGuests + " of " + guestListValue);
			
		//	reservationAdapter.close();
		
		searchGuests = (EditText) findViewById(R.id.searchGuests);
		searchGuests.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				gAdapter.getFilter().filter(s.toString());
				gAdapter.notifyDataSetChanged();
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		guestList.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh(RefreshableListView listView) {
				RefreshGuestList refreshGuests = new RefreshGuestList();
				String guestTimeStamp = preferences.getString("guestTimeStamp", "");
				if (isNetworkAvailable()) {
					refreshGuests.execute(eventID);
					
					guestList.completeRefreshing(guestTimeStamp);
				} else {
					guestList.clearAnimation();
					guestList.completeRefreshing(guestTimeStamp);
				}
				
			}
		});
		
		isAutoSynced = preferences.getBoolean("autoSyncBoolean", true);
		final ToggleButton syncToggle = (ToggleButton) findViewById(R.id.syncToggleButton);
		syncToggle.setChecked(isAutoSynced);
		
		guestListRefresher = new CountDownTimer(10000, 1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				reservationAdapter.open();
				String guestListValue = reservationAdapter.getTotalEventGuests(eventID);
				String currentGuests = reservationAdapter.getCurrentEventGuests(eventID);
				
				totalAttendenceText.setText("Attendence: " + currentGuests + " of " + guestListValue);
				
			}
			
			@Override
			public void onFinish() {
				RefreshGuestList refreshGuests = new RefreshGuestList();
				if (isNetworkAvailable()) {
					if (syncToggle.isChecked()) {
						refreshGuests.execute(eventID);
						Toast.makeText(getApplicationContext(), "Sync started", Toast.LENGTH_SHORT).show();
					} else {
						guestListRefresher.start();
					}
				}
					
			}
		};
		
		
		final ToggleButton sortNameButton = (ToggleButton) findViewById(R.id.nameToggleButton);
		final ToggleButton deactivatedToggle = (ToggleButton) findViewById(R.id.deletedToggleButton);
		
		boolean checked = preferences.getBoolean("sortNameBoolean", true);
		deactivatedBoolean = preferences.getBoolean("deactivatedBoolean", false);
		
		deactivatedToggle.setChecked(deactivatedBoolean);
		sortNameButton.setChecked(checked);
		sortNameButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				reservationAdapter = new ReservationAdapter(GuestList.this);
				reservationAdapter.open();
				
				if (sortNameButton.isChecked()) {
					isFirstName = true;
					editor.putBoolean("sortNameBoolean", isFirstName);
					editor.apply();
					gAdapter.clear();
					if (deactivatedToggle.isChecked()) {
						values = reservationAdapter.getEventDeletedGuests(eventID);
						for (int i = 0; i < values.size(); i++) {
							gAdapter.add(values.get(i));
						}
						gAdapter.notifyDataSetChanged();
					} else {
						values = reservationAdapter.getEventGuests(eventID);
						for (int i = 0; i < values.size(); i++) {
							gAdapter.add(values.get(i));
						}
						gAdapter.notifyDataSetChanged();
					}
				} else {
					isFirstName = false;
					 editor.putBoolean("sortNameBoolean", isFirstName);
					 editor.apply();
					 gAdapter.clear();
					 if (deactivatedToggle.isChecked()) {
						values = reservationAdapter.getEventDeletedLastNameGuests(eventID);
						for (int i = 0; i < values.size(); i++) {
							gAdapter.add(values.get(i));
						}
						gAdapter.notifyDataSetChanged();
					} else {
						values = reservationAdapter.getEventLastNameGuests(eventID);
						for (int i = 0; i < values.size(); i++) {
							gAdapter.add(values.get(i));
						}
						gAdapter.notifyDataSetChanged();
					}
				} 
				editor.commit();
			}
		});
		
		deactivatedBoolean = preferences.getBoolean("deactivatedBoolean", false);
		deactivatedToggle.setChecked(deactivatedBoolean);
		deactivatedToggle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				reservationAdapter = new ReservationAdapter(GuestList.this);
				reservationAdapter.open();
				if (deactivatedToggle.isChecked()) {
					editor.putBoolean("deactivatedBoolean", true);
					editor.apply();
					gAdapter.clear();
					if (sortNameButton.isChecked()) {
						values = reservationAdapter.getEventDeletedGuests(eventID);
					} else {
						values = reservationAdapter.getEventDeletedLastNameGuests(eventID);
					}
					
					for (int i = 0; i < values.size(); i++) {
						gAdapter.add(values.get(i));
					}
					gAdapter.notifyDataSetChanged();
				} else {
					editor.putBoolean("deactivatedBoolean", false);
					editor.apply();
					gAdapter.clear();
					if (sortNameButton.isChecked()) {
						values = reservationAdapter.getEventGuests(eventID);
						for (int i = 0; i < values.size(); i++) {
							gAdapter.add(values.get(i));
						}
						gAdapter.notifyDataSetChanged();
					} else {
						values = reservationAdapter.getEventLastNameGuests(eventID);
						for (int i = 0; i < values.size(); i++) {
							gAdapter.add(values.get(i));
						}
						gAdapter.notifyDataSetChanged();
					}
					
				}
				editor.commit();
			}
		});
		
		syncToggle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (syncToggle.isChecked()) {
					isAutoSynced = false;
					editor.putBoolean("autoSyncBoolean", isAutoSynced);
					editor.apply();
				} else {
					isAutoSynced = true;
					editor.putBoolean("autoSyncBoolean", isAutoSynced);
					editor.apply();
				}
				editor.commit();
			}
		});
	}
	
	private class LoadGuestList extends AsyncTask<Void, Void, Void> {
		private ProgressDialog pdia;
		JSONObject jTransctionObject, jCustomer;

		JSONObject jReservationObject;

		@Override
		protected void onPreExecute() {
			extras = getIntent().getExtras();
			eventID = extras.getString("eventID");
			pdia = new ProgressDialog(GuestList.this);
			pdia.setTitle("Retrieving Guest List");
			pdia.setMessage("One Moment Please");
			pdia.setIndeterminate(false);
			pdia.setCancelable(false);
			pdia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
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
		protected Void doInBackground(Void... params) {
			reservationAdapter = new ReservationAdapter(GuestList.this);
			HttpURLConnection connection;
			OutputStreamWriter request = null;
			URL url = null;
			String parameters = "dictHierarchy=True&apiVersion=1.4&eventID=" + eventID;
			if (!isCancelled()) {
				
			
			try {
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
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			}
			try {
				reservationAdapter.open();
				//	String jEventID = new JSONObject(response).getJSONObject("eventID").toString();
				
					 jCustomer = new JSONObject(response);
					 JSONObject jReservationTypes = jCustomer.getJSONObject("reservationTypes"); 
					 Iterator<String> reservationTypeKeys = jReservationTypes.keys();
					 List<String> listReservationTypeKeys = new ArrayList<String>();
					 while (reservationTypeKeys.hasNext()) {
						listReservationTypeKeys.add(reservationTypeKeys.next());
					}
					 
					 if (listReservationTypeKeys != null && !isCancelled()) {
						 for (int i = 0; i < listReservationTypeKeys.size(); i++) {
							 String reservationTypeString = jReservationTypes.getString(listReservationTypeKeys.get(i));
							 JSONObject jReservationType = new JSONObject(reservationTypeString);
							 String reservationTypeID = listReservationTypeKeys.get(i);
							 String guestDescription = jReservationType.getString("n");
							 String door = jReservationType.getString("door");
							 String color = jReservationType.getString("color");
							 String price = jReservationType.getString("price");
							 String pricing = jReservationType.getString("pricing");
							 
							 if (!reservationAdapter.typeExists(reservationTypeID)) {
								 reservationAdapter.createReservationType(reservationTypeID, guestDescription, door, color, price, pricing);
							}
							 
							 
						 }
					 }
							 jTransaction = jCustomer.getJSONObject("transactionDict");
							 jReservation = jTransaction.names();
							 if (jReservation != null) {
								 for (int j = 0; j < jReservation.length(); j++) {
									 if (!isCancelled()) {
									String transaction = jTransaction.getString(jReservation.getString(j));
									 jTransctionObject = new JSONObject(transaction);
									 JSONObject jTransactionV = jTransctionObject.getJSONObject("v");
									 String transactionID = jReservation.getString(j);
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
										 reservationAdapter.createTransaction(transactionID, amount, objectiveStatus, paidType, quantity, transGroupID, transGroupBuyer, transDate, reservationTypeID, eventID);
									}
									 
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
												
										//		gAdapter.add(guestRowValue);
											}
											 
											 
										}
										 
									}
								 }
							}
					}
					
					 
			//	reservationAdapter.close();	
				} catch (JSONException e) {
					e.printStackTrace();
					reservationAdapter.close();
				}
			
			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			reservationAdapter = new ReservationAdapter(GuestList.this);
			reservationAdapter.open();
			eventID = extras.getString("eventID");
	//		values = reservationAdapter.getEventGuests(eventID);
	//		gAdapter = new GuestAdapter(getApplicationContext(), R.layout.guest_list_row, values);
	//		guestList.setAdapter(new SlideExpandableListAdapter(gAdapter, R.id.guestRowButton, R.id.guestButtonsLayout));

			
			boolean sortBoolean = preferences.getBoolean("sortNameBoolean", true);
			if (sortBoolean) {
				values = reservationAdapter.getEventGuests(eventID);
			} else {
				values = reservationAdapter.getEventLastNameGuests(eventID);
			}
			
		//	values = reservationAdapter.getEventGuests(eventID);
			
			gAdapter = new GuestAdapter(getApplicationContext(), R.layout.guest_list_row, values);
			guestList.setAdapter(new SlideExpandableListAdapter(gAdapter, R.id.guestRowButton, R.id.guestButtonsLayout));
			String guestListValue = reservationAdapter.getTotalEventGuests(eventID);
			String currentGuests = reservationAdapter.getCurrentEventGuests(eventID);
			TextView totalAttendenceText = (TextView) findViewById(R.id.attendenceText);
			totalAttendenceText.setText("Attendence: " + currentGuests + " of " + guestListValue);
	//		reservationAdapter.close();
			pdia.dismiss();
		}
	}
	
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
		} catch (Exception e) { // should never happen
			e.printStackTrace();
		}	
}
	
	@Override
	protected void onResume() {
		guestListRefresher.start();
		super.onResume();
	}


	@Override
	protected void onPause() {
		reservationAdapter.close();
		guestListRefresher.cancel();
		super.onPause();
	}
	
	@Override
	public void onBackPressed() {
		if (loadGuestList.getStatus() == AsyncTask.Status.RUNNING) {
			loadGuestList.cancel(true);
		}
		
		Intent closeGuestList = new Intent(GuestList.this, EventList.class);
		startActivity(closeGuestList);
		finish();
	}
	
	private class RefreshGuestList extends AsyncTask<String, Void, Void> {

		JSONObject jTransctionObject, jCustomer, jTransaction;
		JSONArray jCustomerValues, jTestArray, jReservation;
		JSONObject jReservationObject, jTestObject;
		List<Guests> testGuestList= new ArrayList<Guests>();
		private EventsDatabaseAdapter eventAdapter;
		private String eventID;
		private Bundle extras;
		
		@Override
		protected void onPreExecute() {
			
			
		}
		
		
		
		@Override
		protected Void doInBackground(String... eventValue) {
			int currentProgress = 0;
			eventID = eventValue[0];
			eventAdapter = new EventsDatabaseAdapter(GuestList.this);
			//HttpURLConnection connection;
			OutputStreamWriter request = null;
			URL url = null;
			String transactionID = "";
			String parameters = "dictHierarchy=True&apiVersion=1.4&eventID=" + eventID;
			reservationAdapter.open();
			String userOperations = reservationAdapter.getUserOperations(eventID);
			if (!isCancelled()) {
				
			try {
				eventAdapter.open();
				String guestTimeStamp = eventAdapter.getGuestTimeStamp(eventID);
				eventAdapter.close();
				if (!guestTimeStamp.equals("")) {
					parameters = "dictHierarchy=True&apiVersion=1.4&eventID=" + eventID + "&timestamp=" + guestTimeStamp;
				}
				
				if (!userOperations.equals("")) {
				 parameters = "dictHierarchy=True&apiVersion=1.4&eventID=" + eventID + "&timestamp=" + guestTimeStamp + "&countedWithID=" + userOperations;
				} 
				
				url = new URL("https://www.rsvpme.in/apps/restapi/reservations/retrieveReservationsByEventID/xhr");
		//		url = new URL("https://www.boomset.com/apps/restapi/reservations/retrieveReservationsByEventID/xhr");
				trustEveryone();
				connection = (HttpURLConnection) url.openConnection();
				connection.setDoOutput(true);
				String retreiveCookie = preferences.getString("cookie", "");
				connection.setRequestProperty("Cookie", retreiveCookie);
				connection.setRequestMethod("POST");
				currentProgress = 10;
				request = new OutputStreamWriter(connection.getOutputStream());
	            request.write(parameters);
	            request.flush();
	            request.close();
			
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
				
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				
			}
			
			}
			
			try {
			if (response != null) {
				
					reservationAdapter.open();
					//	String jEventID = new JSONObject(response).getJSONObject("eventID").toString();
						
						 jCustomer = new JSONObject(response);
						 String reservationTimeStamp = jCustomer.getString("timestamp");
						 
						 String operationAcknowlegment = jCustomer.getString("operationAcknowledgement");
						 JSONArray jsonOperation = new JSONArray(operationAcknowlegment);
						 reservationAdapter.open();
						 for (int i = 0; i < jsonOperation.length(); i++) {
							 JSONArray operation = jsonOperation.getJSONArray(i);
							 
							 String test1 = operation.getString(0);
							 String operationTime = operation.getString(5).replace(" ", "").replace(":", "").replace("-", "");
							 reservationAdapter.updateOperations(operation.getString(0), operationTime, operation.getString(2));
							// String operationIDStringFormat = jOperationIDString.replace("[", "").replace("]", "").replace("\"", "");
							// String[] operationIDValues = operationIDStringFormat.split(",");
							 
						}
			//			 reservationAdapter.close();
						 String jGuestTimeStamp = URLEncoder.encode(reservationTimeStamp, "UTF-8");
						 String refreshTimeStamp = URLDecoder.decode(jGuestTimeStamp, "UTF-8");
						 String encodedGuestTimeStamp = URLEncoder.encode(refreshTimeStamp, "UTF-8");
						 eventAdapter.open();
						 eventAdapter.updateGuestTimeStamp(encodedGuestTimeStamp, eventID);
						 editor.putString("guestTimeStamp", refreshTimeStamp);
						 editor.commit();
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
									 reservationAdapter.open();
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
				//			 reservationAdapter.close();
							 currentProgress = 50;
							 
							 
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
				//						 reservationAdapter.close();
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
															 reservationAdapter.open();
															 if (!reservationAdapter.operationExists(operationID)) {
																 reservationAdapter.createOperation(operationID, operationIDValues[0], operationIDValues[2], operationIDValues[3], operationIDValues[4], reservationID, "0");
															 }							
									//						 reservationAdapter.close();
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
														 reservationAdapter.open();
														 if (!reservationAdapter.reservationExists(reservationID)) {
															reservationAdapter.createReservation(eventID, reservationID, guestFirstName, guestLastName, guestID, guestPhone, guestEmail, transactionID, guestQuantity, guestPaidType, guestOperationStatus);
														} else {
															reservationAdapter.updateReservation(eventID, reservationID, guestFirstName, guestLastName, guestID, guestPhone, guestEmail, guestQuantity, guestPaidType, guestOperationStatus);
														}
									//					 reservationAdapter.close();
													}
												}
												 
											}
										}
										 
									 }
										 
										 if (currentProgress < 99) {
											currentProgress++;
											
										}
										 
								}
						} 
						} 
				} catch (JSONException e) {
					e.printStackTrace();
					reservationAdapter.close();
				}
	//		reservationAdapter.close();	
				catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			reservationAdapter.open();
			boolean sortBoolean = preferences.getBoolean("sortNameBoolean", true);
			boolean deactivatedBoolean = preferences.getBoolean("deactivatedBoolean", false);
			if (sortBoolean) {
				if (deactivatedBoolean) {
					values = reservationAdapter.getEventDeletedGuests(eventID);
				} else {
					values = reservationAdapter.getEventGuests(eventID);
				}
			} else {
				if (deactivatedBoolean) {
					values = reservationAdapter.getEventDeletedLastNameGuests(eventID);
				} else {
					values = reservationAdapter.getEventLastNameGuests(eventID);
				}
				
			}
			gAdapter.clear();
			for (int i = 0; i < values.size(); i++) {
				gAdapter.add(values.get(i));
			}
			gAdapter.notifyDataSetChanged();
			String guestListValue = reservationAdapter.getTotalEventGuests(eventID);
			String currentGuests = reservationAdapter.getCurrentEventGuests(eventID);
			totalAttendenceText.setText("Attendence: " + currentGuests + " of " + guestListValue);
			reservationAdapter.close();
			guestListRefresher.start();
		}
	}	
}
