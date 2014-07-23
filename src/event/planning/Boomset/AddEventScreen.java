package event.planning.Boomset;


import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.actionbarsherlock.app.ActionBar;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class AddEventScreen extends SlidingFragmentActivity  {

	private SlidingMenu slidingMenu;
	public static final int DATE_PICKER_ACTION = 9000;
	private EditText venuePicker;
	private Button submitEventButton;
	private String timezone, userID, startDateString, endDateString;
	private SharedPreferences preferences;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Sherlock___Theme_DarkActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addeventlayout);
		setBehindContentView(R.layout.sidemenu);
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.addeventheader);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String savedUserID = preferences.getString("userID", "");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose a nearby venue or create venue");
		
		ImageView menuButton = (ImageView) findViewById(R.id.addMenuButton);
		menuButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				slidingMenu.showMenu(true);
			}
		});
		
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
		
		final DatePickerDialog datePickerDialog = new DatePickerDialog();
		final DatePickerDialog datePickerDialogTwo = new DatePickerDialog();
		
		final EditText eventNameInput = (EditText) findViewById(R.id.inputEventName);
		
		venuePicker = (EditText) findViewById(R.id.inputVenueName);
		
		venuePicker.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isNetworkAvailable()) {
					venuePicker.setFocusable(false);
					venuePicker.setFocusableInTouchMode(false);
					Intent testIntent = new Intent(getApplicationContext(), ChooseVenueScreen.class);
					startActivityForResult(testIntent, 1);
				} else {
					venuePicker.setFocusable(true);
					venuePicker.setFocusableInTouchMode(true);
					venuePicker.requestFocus();
				}
				
			}
		});
		
		final EditText startDateBox = (EditText) findViewById(R.id.inputStartDate);
		startDateBox.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				datePickerDialog.setYearRange(2013, 2020);
				datePickerDialog.show(getSupportFragmentManager(), "datepicker");
			}
		});
		
		final EditText endDateBox = (EditText) findViewById(R.id.inputEndDate);
		endDateBox.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				datePickerDialogTwo.setYearRange(2013, 2020);
				datePickerDialogTwo.show(getSupportFragmentManager(), "datepicker");
				
			}
		});
		
		Calendar c = Calendar.getInstance(); 
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		timezone = c.getTimeZone().toString();
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		
		
		
		
		datePickerDialog.initialize(new OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePickerDialog datePickerDialog, int year,
					int month, int day) {
				
				Calendar cal = Calendar.getInstance();
				cal.set(year, month, day);
				int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
				String dayofWeekString = "";
				String shortenDayOfWeek = "";
				String monthString;
				String dayString;
				int monthInt = month + 1;
				switch (dayOfWeek) {
				case 1:
					dayofWeekString = "Sunday";
					shortenDayOfWeek = "Sun";
					break;
				case 2:
					dayofWeekString = "Monday";
					shortenDayOfWeek = "Mon";
					break;
				case 3:
					dayofWeekString = "Tuesday";
					shortenDayOfWeek = "Tue";
					break;
				case 4:
					dayofWeekString = "Wednesday";
					shortenDayOfWeek = "Wed";
					break;
				case 5:
					dayofWeekString = "Thursday";
					shortenDayOfWeek = "Thu";
					break;
				case 6: 
					dayofWeekString = "Friday";
					shortenDayOfWeek = "Fri";
					break;
				case 7: 
					dayofWeekString = "Saturday";
					shortenDayOfWeek = "Sat";
					break;
			}
				if (monthInt < 10) {
					monthString = "0" + Integer.toString(monthInt);
				} else {
					monthString = Integer.toString(monthInt);
				}
				
				if (day < 10) {
					dayString = "0" + Integer.toString(day);
				} else {
					dayString = Integer.toString(day);
				}
				
	//			startDateString = Integer.toString(year) + "-" + monthString + "-" + dayString + " " + "00:00:00";
				startDateString = shortenDayOfWeek + ", " + getMonthForInt(month) + " " + dayString + ", " + Integer.toString(year) + " " + "00:00"; 
				startDateBox.setText(dayofWeekString + ", " + getMonthForInt(month) + " " + dayString + " " + Integer.toString(year));
				
			}
		}, year, month, day);
		
		datePickerDialogTwo.initialize(new OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePickerDialog datePickerDialog, int year,
					int month, int day) {
				
				Calendar cal = Calendar.getInstance();
				cal.set(year, month, day);
				int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
				String dayofWeekString = "";
				String shortenDayOfWeek = "";
				String monthString = "";
				String dayString = "";
				int monthInt = month + 1;
				switch (dayOfWeek) {
				case 1:
					dayofWeekString = "Sunday";
					shortenDayOfWeek = "Sun";
					break;
				case 2:
					dayofWeekString = "Monday";
					shortenDayOfWeek = "Mon";
					break;
				case 3:
					dayofWeekString = "Tuesday";
					shortenDayOfWeek = "Tue";
					break;
				case 4:
					dayofWeekString = "Wednesday";
					shortenDayOfWeek = "Wed";
					break;
				case 5:
					dayofWeekString = "Thursday";
					shortenDayOfWeek = "Thu";
					break;
				case 6: 
					dayofWeekString = "Friday";
					shortenDayOfWeek = "Fri";
					break;
				case 7: 
					dayofWeekString = "Saturday";
					shortenDayOfWeek = "Sat";
					break;
			}
				
				if (monthInt < 10) {
					monthString = "0" + Integer.toString(monthInt);
				} else {
					monthString = Integer.toString(monthInt);
				}
				
				if (day < 10) {
					dayString = "0" + Integer.toString(day);
				} else {
					dayString = Integer.toString(day);
				}
				
				endDateString = shortenDayOfWeek + ", " + getMonthForInt(month) + " " + dayString + ", " + Integer.toString(year) + " " + "00:00";
				endDateBox.setText(dayofWeekString + ", " + getMonthForInt(month) + " " + Integer.toString(day) + " " + Integer.toString(year));
				
			}
		}, year, month, day);
		
		final Spinner groupSpinner = (Spinner) findViewById(R.id.inputGroup);
		GroupsDatabaseAdapter groupAdapter = new GroupsDatabaseAdapter(getApplicationContext());
		groupAdapter.open();
		List<String> groups = groupAdapter.getUserGroups(savedUserID);
		groupAdapter.close();
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groups);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		groupSpinner.setAdapter(spinnerAdapter);
		
		submitEventButton = (Button) findViewById(R.id.addEventSubmitButton);
		submitEventButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!isValidated(eventNameInput.getText().toString().trim(), venuePicker.getText().toString().trim(), startDateBox.getText().toString().trim(), endDateBox.toString().trim())) {
					if (eventNameInput.getText().toString().trim().equals("")) {
						eventNameInput.setError("Please enter an event name");
					} 
					if (venuePicker.getText().toString().trim().equals("")) {
						venuePicker.setError("Please enter a venue");
					} 
					
					if (startDateBox.getText().toString().trim().equals("")) {
						startDateBox.setError("Please enter a start date");
					}
					
					if (endDateBox.getText().toString().trim().equals("")) {
						endDateBox.setError("Please enter an end date");
					}
				} else {
					EventsDatabaseAdapter eAdapter = new EventsDatabaseAdapter(getApplicationContext());
					eAdapter.open();
					String eventName = eventNameInput.getText().toString();
					String venueName = venuePicker.getText().toString();
					String gmtDateNoSpace = startDateString.replace(" ", "").replace(":", "").replace(",", "");
					String startDate = startDateBox.getText().toString();
					
					String gmtDateNoSpaceEnd = endDateString.replace(" ", "").replace(":", "").replace(",", "");
					
					
					GroupsDatabaseAdapter groupAdapter = new GroupsDatabaseAdapter(getApplicationContext());
					groupAdapter.open();
					String groupID = groupSpinner.getSelectedItem().toString();
					String groupName = groupAdapter.getGroupID(groupID);
					groupAdapter.close();
					
					userID = preferences.getString("userID", "");
					String premiumFlag = "false";
					String upgradeFlag = "true";
					String eventID = "9001";
					String venueID = "9001";
					
					Hashtable<String, String> eventHash = new Hashtable<String, String>();
					eventHash.put("eventID", eventID);
					eventHash.put("eventName", eventName);
					eventHash.put("venueName", venueName);
					eventHash.put("venueID", venueID);
					eventHash.put("startDate", startDate);
					eventHash.put("userID", userID);
					eventHash.put("premiumFlag", premiumFlag);
					eventHash.put("upgradeFlag", upgradeFlag);
					eventHash.put("groupID", groupID);
					
					int eventHashCode = eventHash.hashCode();
					
					eAdapter.createEvent(eventID, eventName, venueID, venueName, timezone, premiumFlag, upgradeFlag, startDate, userID, groupID, Integer.toString(eventHashCode), startDateString, groupName, endDateString);
					
					Intent backToEventsList = new Intent(getApplicationContext(), EventList.class);
					startActivity(backToEventsList);
					finish();
				}
			}
		});
	}
		
	
	@Override
	public void onBackPressed() {
		Intent closeAddEvent = new Intent(AddEventScreen.this, EventList.class);
		startActivity(closeAddEvent);
		finish();
	}
	
	private String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK) {
			String venueResult = data.getStringExtra("venueName");
			venuePicker.setText(venueResult);
		} 
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	private boolean isValidated(String eventName, String eventVenue, String eventStartDate, String eventEndDate) {
		boolean isValid;
		if (eventName.equals("") || eventVenue.equals("") || eventStartDate.equals("") || eventEndDate.equals("")) {
			isValid = false;
		} else {
			isValid = true;
		}
		return isValid;
	}
	
}
