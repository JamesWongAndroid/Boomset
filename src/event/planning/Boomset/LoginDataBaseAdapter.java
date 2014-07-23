package event.planning.Boomset;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LoginDataBaseAdapter {
	
	static final String DATABASE_NAME = "login.db";
	static final int DATABASE_VERSION = 1;
	public static final int NAME_COLUMN = 1;
	
	
	// SQL Statement to create a new database.
	static final String DATABASE_CREATE = "create table "+"LOGIN"+
	                             "( " +"ID"+" integer primary key autoincrement,"+ "NAME text,EMAIL text,PASSWORD text); ";
	
	public  SQLiteDatabase db;
	private final Context context;
	private DataBaseHelper dbHelper;
	
	public LoginDataBaseAdapter(Context cxt) {
		context = cxt;
		dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public  LoginDataBaseAdapter open() throws SQLException {
		db = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		db.close();
	}

	public  SQLiteDatabase getDatabaseInstance() {
		return db;
	}
	
	public void insertEntry(String name, String email, String password) {
		
		ContentValues newValues = new ContentValues();
		newValues.put("NAME", name);
		newValues.put("EMAIL", email);
		newValues.put("PASSWORD", password);
		
		db.insert("LOGIN", null, newValues);
	}
	
	public String getSinglePassword(String name) {
		Cursor cursor = db.query("LOGIN", null, "EMAIL=?", new String[]{name}, null, null, null);
		if(cursor.getCount() < 1) {
			cursor.close();
			return "NOT EXIST";
		}
		
		cursor.moveToFirst();
		String password = cursor.getString(cursor.getColumnIndex("PASSWORD"));
		cursor.close();
		return password;
	}
	
	public String getSingleName(String email) {
		Cursor cursor = db.query("LOGIN", null, "EMAIL=?", new String[]{email}, null, null, null);
		if(cursor.getCount() < 1) {
			cursor.close();
			return "NOT EXIST";
		}
		
		cursor.moveToFirst();
		String name = cursor.getString(cursor.getColumnIndex("NAME"));
		cursor.close();
		return name;
	}
	
	
}
