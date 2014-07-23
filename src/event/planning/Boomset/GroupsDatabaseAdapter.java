package event.planning.Boomset;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class GroupsDatabaseAdapter {

	
	private SQLiteDatabase database;
	private MySQLiteGroupHelper dbHelper;
	private String[] allColumns = {
			MySQLiteGroupHelper.PRIMARY_ID,	MySQLiteGroupHelper.GROUP_ID,
			MySQLiteGroupHelper.GROUP_NAME, MySQLiteGroupHelper.USER_ID 
	};
	
	private String[] Columns = {
			MySQLiteGroupHelper.PRIMARY_ID,	MySQLiteGroupHelper.GROUP_ID,
			MySQLiteGroupHelper.GROUP_NAME 
	};
	
	private static String WHERE = "userID = ?";
	
	private static String WHEREEXISTS =  "groupID = ?";
	
	public GroupsDatabaseAdapter(Context context) {
		dbHelper = new MySQLiteGroupHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public String createGroup(String groupID, String groupName, String userID) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteGroupHelper.GROUP_ID, groupID);
		values.put(MySQLiteGroupHelper.GROUP_NAME, groupName);
		values.put(MySQLiteGroupHelper.USER_ID, userID);
		long insertID = database.insert(MySQLiteGroupHelper.TABLE_GROUP, null, values);
		Cursor cursor = database.query(MySQLiteGroupHelper.TABLE_GROUP, allColumns, MySQLiteGroupHelper.PRIMARY_ID + "= " + insertID, null, null, null, null);
		cursor.moveToFirst();
		String newGroup = cursor.getString(2);
		cursor.close();
		return newGroup;
	}
	
	public List<String> getAllGroups() {
		List<String> groups = new ArrayList<String>();
		Cursor cursor = database.query(MySQLiteGroupHelper.TABLE_GROUP, allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
	//		Group group = cursorToGroup(cursor);
			groups.add(cursor.getString(2));
			cursor.moveToNext();
		}
		cursor.close();
		return groups;
	}
	
	public List<String> getUserGroups(String userID) {
		List<String> userGroups = new ArrayList<String>();
		Cursor cursor = database.query(MySQLiteGroupHelper.TABLE_GROUP, Columns, WHERE, new String[] { userID }, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			userGroups.add(cursor.getString(2));
			cursor.moveToNext();
		}
		cursor.close();
		return userGroups;
	}
	
	public boolean exists(String checkGroupID) {
		Cursor cursor = database.query(MySQLiteGroupHelper.TABLE_GROUP, null, WHEREEXISTS, new String[] { checkGroupID }, null, null, null);
		boolean exists = (cursor.getCount() > 0);
		cursor.close();
		return exists;
	}
	
	public String getGroupID(String groupName) {
		Cursor cursor = database.rawQuery("Select groupID from groups where groupName = ?", new String[] { groupName });
		cursor.moveToFirst();
		String groupID = cursor.getString(0);
		cursor.close();
		return groupID;
	}
	
	private Group cursorToGroup(Cursor cursor) {
		Group group = new Group();
		group.setId(cursor.getLong(0));
		group.setGroupID(cursor.getString(1));
		group.setGroupName(cursor.getString(2));
		group.setUserID(cursor.getString(3));
		return group;
	}
}
