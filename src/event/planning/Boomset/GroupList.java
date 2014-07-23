package event.planning.Boomset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import event.planning.Boomset.R;
import event.planning.Boomset.RefreshableListView.OnRefreshListener;

public class GroupList extends SlidingFragmentActivity {

	private RefreshableListView groupList;
	private SlidingMenu slidingMenu;
	private GroupAdapter gAdapter;
	private EditText searchView;
	private StringBuilder testBuilder = new StringBuilder();
	private GroupsDatabaseAdapter groupAdapter;
	private AlertDialog.Builder logOutDialog;
	private AlertDialog alertDialog;
	private String response, responseTwo = null;
	private TextView logoutButton, menuUserName;
	private ProgressDialog pdia;
	private SharedPreferences.Editor editor;
	int responsecode;
	String cookie;
	JSONObject groupObject;
	private String group, groupName, groupID, userID, userName, password, firstName, lastUpdated;
	ArrayList<String> groupNames = new ArrayList<String>();
	ArrayList<String> groupIDs = new ArrayList<String>();
	ArrayList<String> eventsTest = new ArrayList<String>();
	ArrayAdapter<String> arrad;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Sherlock___Theme_DarkActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grouplist);
		setBehindContentView(R.layout.sidemenu);

		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.headerlayout);
		ImageView menuButton = (ImageView) findViewById(R.id.menuButton);

		groupList = (RefreshableListView) findViewById(R.id.groupList);
		groupList.setTextFilterEnabled(true);
		
		slidingMenu = getSlidingMenu();
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setBehindOffset(80);
		slidingMenu.setFadeDegree(0.50f);
		
		logOutDialog = new AlertDialog.Builder(this);
		logOutDialog.setTitle("Log Out");
		logOutDialog.setMessage("Are you sure you want to log out?").setCancelable(true);
		logOutDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		logOutDialog.setNegativeButton("Log Out", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				editor.clear();
				editor.commit();
				Intent loginIntent = new Intent(GroupList.this, LogInActivity.class);
				startActivity(loginIntent);
				finish();
				
			}
		});
		alertDialog = logOutDialog.create();
		
		/*LinearLayout groupRow = (LinearLayout) findViewById(R.id.groupRow);
		groupRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				slidingMenu.toggle(true);
			}
		});*/
		
		LinearLayout eventRow = (LinearLayout) findViewById(R.id.eventRow);
		eventRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent eventListIntent = new Intent(GroupList.this, EventList.class);
				startActivity(eventListIntent);
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
		
		logoutButton = (TextView) findViewById(R.id.eventsLogoutButton);
		logoutButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				alertDialog.show();
				
			}
		});

		menuButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				slidingMenu.showMenu(true);
			}
		});
		
		groupList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(RefreshableListView listView) {

				new updateGroupTask().execute();
			}
		});
		
		searchView = (EditText) findViewById(R.id.searchView);
		searchView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				gAdapter.getFilter().filter(arg0);
				gAdapter.notifyDataSetChanged();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// GroupList.this.arrad.getFilter().filter(arg0);

			}
		});

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		 userName = preferences.getString("userName", "");
		 password = preferences.getString("password", "");
		 lastUpdated = preferences.getString("lastUpdateTime", "");
		 menuUserName = (TextView) findViewById(R.id.userName);
		 menuUserName.setText(userName);
		 editor = preferences.edit();
		 
		 if (isNetworkAvailable()) {
			 LoadGroupList loadGroupList = new LoadGroupList();
			 loadGroupList.execute();
		} else {
			groupAdapter = new GroupsDatabaseAdapter(GroupList.this);
			groupAdapter.open();
			
			String savedUserID = preferences.getString("userID", "");
			List<String> values = groupAdapter.getUserGroups(savedUserID);

			if (values.isEmpty()) {
				values.clear();
			}

			gAdapter = new GroupAdapter(getApplicationContext(), R.layout.group_list_row, values);
			groupList.setAdapter(gAdapter);
		}
		
		 groupList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// int indexPosition = position + 1;
					Toast.makeText(
							getApplicationContext(),
							groupList.getItemAtPosition(position + 1).toString(), Toast.LENGTH_SHORT).show();

				}
			});
		 
	}

	@Override
	protected void onPause() {
		groupAdapter.close();
		super.onPause();
	}
	
	private class LoadGroupList extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdia = new ProgressDialog(GroupList.this);
			pdia.setTitle("Retrieving Your Groups");
			pdia.setMessage("One Moment Please");
			pdia.setIndeterminate(false);
			pdia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pdia.show();

		}
		
		@Override
		protected Void doInBackground(Void... params) {
			HttpURLConnection connectionTwo;
			OutputStreamWriter requestTwo = null;
			URL urlTwo = null;
			String parametersLogin = "username=" + userName + "&password="
					+ password + "&csum=XYZ";
			
			try {

				urlTwo = new URL("https://rsvpme.in/apps/restapi/accounts/login/xhr");
				connectionTwo = (HttpURLConnection) urlTwo.openConnection();
				connectionTwo.setDoOutput(true);
				connectionTwo.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				connectionTwo.setRequestMethod("POST");
				connectionTwo.setChunkedStreamingMode(0);
				connectionTwo.connect();
				String headerName = null;
				cookie = null;

				requestTwo = new OutputStreamWriter(connectionTwo.getOutputStream());
				requestTwo.write(parametersLogin);
				requestTwo.flush();
				requestTwo.close();

				for (int i = 1; (headerName = connectionTwo.getHeaderFieldKey(i)) != null; i++) {
					if (headerName.equals("Set-Cookie")) {
						cookie = connectionTwo.getHeaderField(i);
					}
				}

				String lineTwo = "";
				InputStreamReader isrTwo = new InputStreamReader(
						connectionTwo.getInputStream());
				BufferedReader readerTwo = new BufferedReader(isrTwo);
				StringBuilder sbTwo = new StringBuilder();
				while ((lineTwo = readerTwo.readLine()) != null) {
					sbTwo.append(lineTwo + "\n");
				}

				responseTwo = sbTwo.toString();
				JSONObject userInfo = new JSONObject(responseTwo);
				JSONArray firstArray = userInfo.getJSONArray("userData");
				firstName = firstArray.getString(1);
				userID = firstArray.getString(0);

				isrTwo.close();
				readerTwo.close();

				HttpURLConnection connection;
				URL url = null;
				response = null;
				url = new URL(
						"https://www.rsvpme.in/apps/restapi/events/retrieveEventsByUser/xhr?apiVersion=1.5");
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestProperty("Cookie", cookie);
				connection.setRequestMethod("GET");
				connection.connect();
				// connection.setDoOutput(true);

				responsecode = connection.getResponseCode();

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
			} catch (ProtocolException e) {
				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();
				// Integer response = Integer.valueOf(responsecode);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			groupAdapter = new GroupsDatabaseAdapter(GroupList.this);
			groupAdapter.open();
			List<String> values = groupAdapter.getUserGroups(userID);

			if (values.isEmpty()) {
				values.clear();
			}

			gAdapter = new GroupAdapter(getApplicationContext(),
					R.layout.group_list_row, values);
			groupList.setAdapter(gAdapter);
					
			try {
				JSONObject jObject = new JSONObject(response).getJSONObject("g");
			
			String groupRowString = null;
			JSONArray jArray = jObject.names();
			for (int i = 0; i < jArray.length(); i++) {
				groupID = jArray.getString(i);
				group = jObject.getString(jArray.getString(i));
				JSONObject jGroupName = new JSONObject(group);

				JSONObject eventsObject = jGroupName.getJSONObject("e");
				JSONArray eventIDArray = eventsObject.names();
				if (eventIDArray != null) {
					for (int j = 0; j < eventIDArray.length(); j++) {
						String event = eventsObject.getString(eventIDArray.getString(j));
						JSONObject eventNameObject = new JSONObject(event);
						String eventName = eventNameObject.getString("n");
						String venueName = eventNameObject.getString("vn");
						String upgradeFlag = eventNameObject.getString("upgrade");
						String venueID = eventNameObject.getString("vID");
						String premiumFlag = eventNameObject.getString("p");
						String startDate = eventNameObject.getString("start");
						String timezone = eventNameObject.getString("tz");
						
						testBuilder.append(eventIDArray.get(j) + " " +eventName + " " + venueID + " " + venueName + " " + startDate + " " + upgradeFlag + " " + premiumFlag + " " + timezone + "\n");
						eventName = eventIDArray.toString();
					}
				}
				

				

				groupName = jGroupName.getString("gN");
				groupNames.add(groupName);
				groupIDs.add(groupID);

				if (!groupAdapter.exists(groupID)) {
					groupRowString = groupAdapter.createGroup(groupID, groupName,
							userID);
					gAdapter.add(groupRowString);
				} else {

				}
				// groupRow = groupAdapter.createGroup(groupID, groupName,
				// userID);
				// adapter.add(groupRow);
			}
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gAdapter.notifyDataSetChanged();
			groupAdapter.close();
			pdia.dismiss();
		}
		
	}

	private class updateGroupTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			HttpURLConnection connection;
			URL url = null;

			try {
				url = new URL(
						"https://rsvpme.in/apps/restapi/events/retrieveEventsByUser/xhr?apiVersion=1.5");
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestProperty("Cookie", cookie);
				connection.setRequestMethod("GET");
				connection.connect();
				// connection.setDoOutput(true);

				responsecode = connection.getResponseCode();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return "test";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			groupList.completeRefreshing(lastUpdated);
		}
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

}
