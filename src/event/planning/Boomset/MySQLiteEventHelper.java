package event.planning.Boomset;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteEventHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_EVENTS = "events";
	public static final String PRIMARY_ID = "_id";
	public static final String EVENT_ID = "eventID";
	public static final String EVENT_NAME = "eventName";
	public static final String VENUE_ID = "venueID";
	public static final String VENUE_NAME = "venueName";
	public static final String TIMEZONE = "timezone";
	public static final String PREMIUM_FLAG = "premiumFlag";
	public static final String UPGRADE_FLAG = "upgradeFlag";
	public static final String START_DATE = "startDate";
	public static final String GROUP_ID = "groupID";
	public static final String USER_ID = "userID";
	public static final String GUEST_TIMESTAMP = "guestTimeStamp";
	public static final String EVENT_HASHCODE = "eventHashCode";
	public static final String CREATED_START_DATE_FORMAT = "newEventStartDate";
	public static final String GROUP_NAME = "groupName";
	public static final String CREATED_END_DATE_FORMAT = "newEventEndDate";
	
	private static final String DATABASE_NAME = "events.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = "create table " + TABLE_EVENTS + "(" + PRIMARY_ID + 
			 " integer primary key autoincrement, " + EVENT_ID + " text," + EVENT_NAME + " text," + VENUE_ID +
			 " text, " + VENUE_NAME + " text, " + TIMEZONE + " text, " + PREMIUM_FLAG + " text, " + UPGRADE_FLAG +
			 " text, " + START_DATE + " text, " + USER_ID + " text, " + GROUP_ID + " text, " + GUEST_TIMESTAMP + " text, " +
			 		EVENT_HASHCODE + " text," + CREATED_START_DATE_FORMAT + " text, " + GROUP_NAME + " text, " + CREATED_END_DATE_FORMAT + " text"  +");";
	
	

	public MySQLiteEventHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
		onCreate(db);
		
	}
	
	public SQLiteDatabase getWritable() {
		return getWritableDatabase();
	}

}
