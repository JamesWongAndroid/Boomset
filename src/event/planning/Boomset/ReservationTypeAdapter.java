package event.planning.Boomset;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ReservationTypeAdapter {

	private SQLiteDatabase database;
	private MySQLiteGuestHelper dbHelper;
	
	private String[] allReservationTypeColumns = { MySQLiteGuestHelper.TYPE_PRIMARY_ID,
		MySQLiteGuestHelper.RESERVATION_ID, MySQLiteGuestHelper.RESERVATION_TYPE_NAME, MySQLiteGuestHelper.DOOR_TYPE,
		MySQLiteGuestHelper.COLOR_TYPE, MySQLiteGuestHelper.PRICE_TYPE, MySQLiteGuestHelper.PRICING_TYPE
	};
	
	private String[] allReservationIDColumns = { MySQLiteGuestHelper.TYPE_PRIMARY_ID, MySQLiteGuestHelper.RESERVATION_TYPE_NAME, MySQLiteGuestHelper.DOOR_TYPE,
			MySQLiteGuestHelper.COLOR_TYPE, MySQLiteGuestHelper.PRICE_TYPE, MySQLiteGuestHelper.PRICING_TYPE
		}; 
	
	private static String WHERE_RESERVATIONID = "reservationID = ?";
	
	public ReservationTypeAdapter(Context context) {
		dbHelper = new MySQLiteGuestHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public void createReservationType(String reservationID, String reservationName, String door, String color, String price, String pricing) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteGuestHelper.RESERVATION_ID, reservationID);
		values.put(MySQLiteGuestHelper.RESERVATION_TYPE_NAME, reservationName);
		values.put(MySQLiteGuestHelper.DOOR_TYPE, door);
		values.put(MySQLiteGuestHelper.COLOR_TYPE, color);
		values.put(MySQLiteGuestHelper.PRICE_TYPE, price);
		values.put(MySQLiteGuestHelper.PRICING_TYPE, pricing);
		long insertID = database.insert(MySQLiteGuestHelper.TABLE_RESERVATION_TYPE, null, values);
		Cursor cursor = database.query(MySQLiteGuestHelper.TABLE_RESERVATION_TYPE, allReservationTypeColumns, MySQLiteGuestHelper.TYPE_PRIMARY_ID + "= " + insertID, null, null, null, null);
		cursor.moveToFirst();
		cursor.close();
	}
	
	public String getReservationName(String reservationID) {
		Cursor cursor = database.query(MySQLiteGuestHelper.TABLE_RESERVATIONS, allReservationIDColumns, WHERE_RESERVATIONID, new String[] { reservationID }, null, null, null);
		String reservationName = cursor.getString(2);
		return reservationName;
	}
}
