package event.planning.Boomset;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteGroupHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_GROUP = "groups";
	public static final String PRIMARY_ID = "_id";
	public static final String GROUP_ID = "groupID";
	public static final String GROUP_NAME = "groupName";
	public static final String USER_ID = "userID";
	
	private static final String DATABASE_NAME = "groups.db";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "create table "
		      + TABLE_GROUP + "(" + PRIMARY_ID
		      + " integer primary key autoincrement, " + GROUP_ID
		      + " text," + GROUP_NAME + " text, " + USER_ID + " text);";
	
	public MySQLiteGroupHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteGroupHelper.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		    db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP);
		    onCreate(db);
		
	}

}
