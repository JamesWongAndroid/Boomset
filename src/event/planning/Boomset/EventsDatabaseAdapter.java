package event.planning.Boomset;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.TimeZone;

import com.google.gson.Gson;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class EventsDatabaseAdapter {

	private SQLiteDatabase database;
	private MySQLiteEventHelper dbHelper;
	private static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
	Gson gson;

	private String[] allColumns = { MySQLiteEventHelper.PRIMARY_ID, MySQLiteEventHelper.EVENT_ID,
			MySQLiteEventHelper.EVENT_NAME, MySQLiteEventHelper.VENUE_ID,
			MySQLiteEventHelper.VENUE_NAME, MySQLiteEventHelper.TIMEZONE,
			MySQLiteEventHelper.PREMIUM_FLAG, MySQLiteEventHelper.UPGRADE_FLAG,
			MySQLiteEventHelper.START_DATE, MySQLiteEventHelper.USER_ID,
			MySQLiteEventHelper.GROUP_ID, };
	
	private String[] allUsers = { MySQLiteEventHelper.PRIMARY_ID, MySQLiteEventHelper.EVENT_ID,
			MySQLiteEventHelper.EVENT_NAME, MySQLiteEventHelper.VENUE_ID,
			MySQLiteEventHelper.VENUE_NAME, MySQLiteEventHelper.TIMEZONE,
			MySQLiteEventHelper.PREMIUM_FLAG, MySQLiteEventHelper.UPGRADE_FLAG,
			MySQLiteEventHelper.START_DATE,
			MySQLiteEventHelper.GROUP_ID, };

	private static String WHERE_USERID = "userID = ?";
	private static String WHERE_EVENT_EXISTS = "eventID = ?";

	public EventsDatabaseAdapter(Context context) {
		dbHelper = new MySQLiteEventHelper(context);
		gson = new Gson();
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Events createEvent(String eventID, String eventName, String venueID,
			String venueName, String timezone, String premiumFlag,
			String upgradeFlag, String startDate, String userID, String groupID, 
			String hashCode, String newEventStartDate, String groupName, String newEventEndDate) {
		Events event = new Events();
		ContentValues values = new ContentValues();
		values.put(MySQLiteEventHelper.EVENT_ID, eventID);
		values.put(MySQLiteEventHelper.EVENT_NAME, eventName);
		values.put(MySQLiteEventHelper.VENUE_ID, venueID);
		values.put(MySQLiteEventHelper.VENUE_NAME, venueName);
		values.put(MySQLiteEventHelper.TIMEZONE, timezone);
		values.put(MySQLiteEventHelper.PREMIUM_FLAG, premiumFlag);
		values.put(MySQLiteEventHelper.UPGRADE_FLAG, upgradeFlag);
		values.put(MySQLiteEventHelper.START_DATE, startDate);
		values.put(MySQLiteEventHelper.USER_ID, userID);
		values.put(MySQLiteEventHelper.GROUP_ID, groupID);
		values.put(MySQLiteEventHelper.GUEST_TIMESTAMP, "");
		values.put(MySQLiteEventHelper.EVENT_HASHCODE, hashCode);
		values.put(MySQLiteEventHelper.CREATED_START_DATE_FORMAT, newEventStartDate);
		values.put(MySQLiteEventHelper.GROUP_NAME, groupName);
		values.put(MySQLiteEventHelper.CREATED_END_DATE_FORMAT, newEventEndDate);
		long insertID = database.insert(MySQLiteEventHelper.TABLE_EVENTS, null,
				values);
		Cursor cursor = database.query(MySQLiteEventHelper.TABLE_EVENTS,
				allColumns, MySQLiteEventHelper.PRIMARY_ID + "= " + insertID,
				null, null, null, null);
		cursor.moveToFirst();
		cursor.close();
		
		event.setEventName(eventName);
		event.setGroupName(groupID);
		event.setStartDate(startDate);
		event.setVenueName(venueName);
		event.setEventID(eventID);
		
		return event;
	}
	
	public void updateGuestTimeStamp(String timestamp, String eventID) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteEventHelper.GUEST_TIMESTAMP, timestamp);
		database.update(MySQLiteEventHelper.TABLE_EVENTS, values, MySQLiteEventHelper.EVENT_ID + " = " + eventID, null);
	}
	
	public String getGuestTimeStamp(String eventID) {
		Cursor cursor = database.rawQuery("SELECT guestTimeStamp from events where events.eventID = ?", new String[] { eventID });
		cursor.moveToFirst();
		
		if (cursor.getCount() == 0) {
			return "";
		} else {
			return cursor.getString(0); 
		}
	}
	
	public void updateEvent(String eventID, String eventName, String venueID,
			String venueName, String timezone, String premiumFlag,
			String upgradeFlag, String startDate, String userID, String groupID) {
		
		ContentValues values = new ContentValues();
		values.put(MySQLiteEventHelper.EVENT_NAME, eventName);
		values.put(MySQLiteEventHelper.VENUE_ID, venueID);
		values.put(MySQLiteEventHelper.VENUE_NAME, venueName);
		values.put(MySQLiteEventHelper.TIMEZONE, timezone);
		values.put(MySQLiteEventHelper.PREMIUM_FLAG, premiumFlag);
		values.put(MySQLiteEventHelper.UPGRADE_FLAG, upgradeFlag);
		values.put(MySQLiteEventHelper.START_DATE, startDate);
		values.put(MySQLiteEventHelper.USER_ID, userID);
		values.put(MySQLiteEventHelper.GROUP_ID, groupID);
		database.update(MySQLiteEventHelper.TABLE_EVENTS, values, MySQLiteEventHelper.EVENT_ID + "=" + eventID, null);
		
	}

	public List<Events> getUserEvents(String userID) {
		List<Events> userEvents = new ArrayList<Events>();
		
		Cursor cursor = database.query(MySQLiteEventHelper.TABLE_EVENTS, allUsers, WHERE_USERID, new String[] { userID }, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Events eventInfo = new Events();
			eventInfo.setEventName(cursor.getString(2));
			eventInfo.setStartDate(cursor.getString(8));
			eventInfo.setGroupName(cursor.getString(9));
			eventInfo.setVenueName(cursor.getString(4));
			eventInfo.setEventID(cursor.getString(1));
			userEvents.add(eventInfo);
			cursor.moveToNext();
		}
		cursor.close();
		return userEvents;
	}

	public boolean exists(String checkEventID, String userID) {
		Cursor cursor = database.query(MySQLiteEventHelper.TABLE_EVENTS, null,
				WHERE_EVENT_EXISTS + "AND " + WHERE_USERID, new String[] { checkEventID, userID }, null, null,
				null);
		boolean exists = (cursor.getCount() > 0);
		cursor.close();
		return exists;
	}
	
	public String getUserAddedEvents() {
		ArrayList<String> userAddedEvents = new ArrayList<String>();
		
		Cursor cursor = database.rawQuery("Select eventName, groupID, venueName, eventHashCode, newEventStartDate, groupName, newEventEndDate From events where eventHashCode != ''", null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Dictionary<String, String> eventDictionary = new Hashtable<String, String>();
			eventDictionary.put("eventname", cursor.getString(cursor.getColumnIndex("eventName")));
			eventDictionary.put("groupname", cursor.getString(cursor.getColumnIndex("groupID")));
			eventDictionary.put("gid", cursor.getString(cursor.getColumnIndex("groupName")));
			eventDictionary.put("venuename", cursor.getString(cursor.getColumnIndex("eventName")));
			String timeStamp = cursor.getString(cursor.getColumnIndex("newEventStartDate"));
			StringBuffer sb = new StringBuffer(timeStamp);
			sb.insert(4, '-');
			sb.insert(7, '-');
			sb.insert(10, " ");
			sb.insert(13, ":");
			sb.insert(16, ":");
			String timeStampEnd = cursor.getString(cursor.getColumnIndex("newEventEndDate"));
			StringBuffer stringBuffer = new StringBuffer(timeStampEnd);
			stringBuffer.insert(4, '-');
			stringBuffer.insert(7, '-');
			stringBuffer.insert(10, " ");
			stringBuffer.insert(13, ":");
			stringBuffer.insert(16, ":");
			eventDictionary.put("enddate", timeStampEnd);
			eventDictionary.put("startdate", timeStamp);
			eventDictionary.put("hashcode",  cursor.getString(cursor.getColumnIndex("eventHashCode")));
			eventDictionary.put("hash", "eventDictionary");
			String jsonString = gson.toJson(eventDictionary);
			userAddedEvents.add(jsonString);
			cursor.moveToNext();
		}
		//String jsonString = gson.toJson(userAddedEvents);
		cursor.close();
		return userAddedEvents.toString();
	}
	
	public void deleteAll(String userID) {
		database.delete(MySQLiteEventHelper.TABLE_EVENTS, WHERE_USERID, new String[] { userID });
	}

}
