package event.planning.Boomset;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;

import com.google.gson.Gson;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ReservationAdapter {
	
	private SQLiteDatabase database;
	private MySQLiteGuestHelper dbHelper;
	long insertID;
	Gson gson;
	
	private String[] allReservationColumns = {
			MySQLiteGuestHelper.PRIMARY_ID, MySQLiteGuestHelper.EVENT_ID, MySQLiteGuestHelper.RESERVATION_ID, MySQLiteGuestHelper.FIRST_NAME,
			MySQLiteGuestHelper.LAST_NAME, MySQLiteGuestHelper.GUEST_ID, MySQLiteGuestHelper.PHONE_NUMBER,
			MySQLiteGuestHelper.EMAIL_ADDRESS
	};
	
	private String[] allGuestColumns = {
			MySQLiteGuestHelper.PRIMARY_ID, MySQLiteGuestHelper.RESERVATION_ID, MySQLiteGuestHelper.FIRST_NAME,
			MySQLiteGuestHelper.LAST_NAME, MySQLiteGuestHelper.GUEST_ID, MySQLiteGuestHelper.PHONE_NUMBER,
			MySQLiteGuestHelper.EMAIL_ADDRESS
	};
	
	private String[] allReservationTypeColumns = { MySQLiteGuestHelper.TYPE_PRIMARY_ID,
			MySQLiteGuestHelper.RESERVATION_TYPE_ID, MySQLiteGuestHelper.RESERVATION_TYPE_NAME, MySQLiteGuestHelper.DOOR_TYPE,
			MySQLiteGuestHelper.COLOR_TYPE, MySQLiteGuestHelper.PRICE_TYPE, MySQLiteGuestHelper.PRICING_TYPE
		};
	
	private String[] allReservationIDColumns = { MySQLiteGuestHelper.TYPE_PRIMARY_ID, MySQLiteGuestHelper.RESERVATION_TYPE_NAME, MySQLiteGuestHelper.DOOR_TYPE,
			MySQLiteGuestHelper.COLOR_TYPE, MySQLiteGuestHelper.PRICE_TYPE, MySQLiteGuestHelper.PRICING_TYPE
		}; 
	
	private String[] allTransactionColumns = {
			MySQLiteGuestHelper.RESERVATION_TRANSACTION_ID, MySQLiteGuestHelper.TRANSACTION_AMOUNT, MySQLiteGuestHelper.OBJECTIVE_STATUS, MySQLiteGuestHelper.TRANSACTION_QUANTITY,
			MySQLiteGuestHelper.TRANSACTION_GROUP_ID, MySQLiteGuestHelper.TRANSACTION_GROUP_BUYER, MySQLiteGuestHelper.TRANSACTION_RESERVATION_TYPE_ID
	};
	
	String query = "SELECT reservationTypeName, quantity FROM " + MySQLiteGuestHelper.TABLE_RESERVATIONS + ", " +  
			MySQLiteGuestHelper.TABLE_RESERVATION_TRANSACTION + ", " + MySQLiteGuestHelper.TABLE_RESERVATION_TYPE + " WHERE " + MySQLiteGuestHelper.TABLE_RESERVATIONS + "." + MySQLiteGuestHelper.RESERVATION_TRANS_ID + " = " +
			MySQLiteGuestHelper.TABLE_RESERVATION_TRANSACTION + "." + MySQLiteGuestHelper.RESERVATION_TRANSACTION_ID + " AND " +  MySQLiteGuestHelper.PRIMARY_ID + " = " + insertID + " AND " + 
			MySQLiteGuestHelper.TABLE_RESERVATION_TRANSACTION + "." + MySQLiteGuestHelper.TRANSACTION_RESERVATION_TYPE_ID + " = " + MySQLiteGuestHelper.TABLE_RESERVATION_TYPE + "." + MySQLiteGuestHelper.RESERVATION_TYPE_ID;
	
	private static String WHERE_RESERVATION_TYPE_ID = "reservationTypeID = ?";
	private static String WHERE_EVENTID = "eventID = ?";
	private static String WHERE_RESERVATIONID = "reservationID = ?";
	private static String WHERE_TYPEID = "reservationTypeID = ?";
	private static String WHERE_TRANSACTIONID = "transactionID = ?";
	private static String WHERE_OPERATIONID = "operationID = ?";
	
	public ReservationAdapter(Context context) {
		dbHelper = new MySQLiteGuestHelper(context);
		gson = new Gson();
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public void createOperation(String operationID, String userID, String quantity, String checkIn, String objectStatus, String reservationID, String toBeSent) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteGuestHelper.OPERATION_ID, operationID);
		values.put(MySQLiteGuestHelper.OPERATION_USER_ID, userID);
		values.put(MySQLiteGuestHelper.OPERATION_QUANTITY, quantity);
		values.put(MySQLiteGuestHelper.OPERATION_TIME_CHECK_IN, checkIn);
		values.put(MySQLiteGuestHelper.OPERATION_OBJECT_STATUS, objectStatus);
		values.put(MySQLiteGuestHelper.OPERATION_RESERVATION_ID, reservationID);
		values.put(MySQLiteGuestHelper.OPERATION_TOBESENT, toBeSent);
		
		database.insert(MySQLiteGuestHelper.TABLE_OPERATIONS, null, values);
	}
	
	public void createTransaction(String transactionID, String amount, String paidType, String status, String quantity, 
			String transGroupID, String transGroupBuyer, String transDate, String transReservationID, String transEventID) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteGuestHelper.RESERVATION_TRANSACTION_ID, transactionID);
		values.put(MySQLiteGuestHelper.TRANSACTION_AMOUNT, amount);
		values.put(MySQLiteGuestHelper.TRANSACTION_PAYMENT, paidType);
		values.put(MySQLiteGuestHelper.OBJECTIVE_STATUS, status);
		values.put(MySQLiteGuestHelper.TRANSACTION_QUANTITY, quantity);
		values.put(MySQLiteGuestHelper.TRANSACTION_GROUP_ID, transGroupID);
		values.put(MySQLiteGuestHelper.TRANSACTION_GROUP_BUYER, transGroupBuyer);
		values.put(MySQLiteGuestHelper.TRANSACTION_DATE, transDate);
		values.put(MySQLiteGuestHelper.TRANSACTION_RESERVATION_TYPE_ID, transReservationID);
		values.put(MySQLiteGuestHelper.TRANSACTION_EVENT_ID, transEventID);
		database.insert(MySQLiteGuestHelper.TABLE_RESERVATION_TRANSACTION, null, values);
//		Cursor cursor = database.query(MySQLiteGuestHelper.TABLE_RESERVATION_TRANSACTION, allTransactionColumns, MySQLiteGuestHelper.TRANSACTION_PRIMARY_ID + "= " + insertID, null, null, null, null);
	}
	
	public void updateTransaction(String transactionID, String amount, String paidType, String status, String quantity, 
			String transGroupID, String transGroupBuyer, String transDate, String transEventID) {
		
		ContentValues values = new ContentValues();
		values.put(MySQLiteGuestHelper.RESERVATION_TRANSACTION_ID, transactionID);
		values.put(MySQLiteGuestHelper.TRANSACTION_AMOUNT, amount);
		values.put(MySQLiteGuestHelper.TRANSACTION_PAYMENT, paidType);
		values.put(MySQLiteGuestHelper.OBJECTIVE_STATUS, status);
		values.put(MySQLiteGuestHelper.TRANSACTION_QUANTITY, quantity);
		values.put(MySQLiteGuestHelper.TRANSACTION_GROUP_ID, transGroupID);
		values.put(MySQLiteGuestHelper.TRANSACTION_GROUP_BUYER, transGroupBuyer);
		values.put(MySQLiteGuestHelper.TRANSACTION_DATE, transDate);
		values.put(MySQLiteGuestHelper.TRANSACTION_EVENT_ID, transEventID);
		database.update(MySQLiteGuestHelper.TABLE_RESERVATION_TRANSACTION, values, MySQLiteGuestHelper.RESERVATION_TRANSACTION_ID + " = " + transactionID, null);
		
	}
	
	public void createReservation(String eventID, String reservationID, String firstName, 
			String lastName, String guestID, String phoneNumber, String emailAddress, String transactionID, String quantity, String payment, String objectStatus) {
		
		ContentValues values = new ContentValues();
		values.put(MySQLiteGuestHelper.EVENT_ID, eventID);
		values.put(MySQLiteGuestHelper.RESERVATION_ID, reservationID);
		values.put(MySQLiteGuestHelper.FIRST_NAME, firstName);
		values.put(MySQLiteGuestHelper.LAST_NAME, lastName);
		values.put(MySQLiteGuestHelper.GUEST_ID, guestID);
		values.put(MySQLiteGuestHelper.PHONE_NUMBER, phoneNumber);
		values.put(MySQLiteGuestHelper.EMAIL_ADDRESS, emailAddress);
		values.put(MySQLiteGuestHelper.RESERVATION_TRANS_ID, transactionID);
		values.put(MySQLiteGuestHelper.GUEST_QUANTITY, quantity);
		values.put(MySQLiteGuestHelper.RESERVATION_PAID, payment);
		values.put(MySQLiteGuestHelper.RESERVATION_OPERATION_STATUS, objectStatus);
		database.insert(MySQLiteGuestHelper.TABLE_RESERVATIONS, null, values);
	}
	
	public void updateReservation(String eventID, String reservationID, String firstName, 
			String lastName, String guestID, String phoneNumber, String emailAddress, String quantity, String payment, String objectStatus) {
		
		ContentValues values = new ContentValues();
		values.put(MySQLiteGuestHelper.EVENT_ID, eventID);
		values.put(MySQLiteGuestHelper.RESERVATION_ID, reservationID);
		values.put(MySQLiteGuestHelper.FIRST_NAME, firstName);
		values.put(MySQLiteGuestHelper.LAST_NAME, lastName);
		values.put(MySQLiteGuestHelper.GUEST_ID, guestID);
		values.put(MySQLiteGuestHelper.PHONE_NUMBER, phoneNumber);
		values.put(MySQLiteGuestHelper.EMAIL_ADDRESS, emailAddress);
		values.put(MySQLiteGuestHelper.GUEST_QUANTITY, quantity);
		values.put(MySQLiteGuestHelper.RESERVATION_PAID, payment);
		values.put(MySQLiteGuestHelper.RESERVATION_OPERATION_STATUS, objectStatus);
		database.update(MySQLiteGuestHelper.TABLE_RESERVATIONS, values, MySQLiteGuestHelper.RESERVATION_ID + " = " + reservationID, null);
	}
	
	public void updateReservationStatus(String reservationID, String objectStatus) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteGuestHelper.RESERVATION_OPERATION_STATUS, objectStatus);
		database.update(MySQLiteGuestHelper.TABLE_RESERVATIONS, values, MySQLiteGuestHelper.RESERVATION_ID + " = " + reservationID, null);
	}
	
	public List<Guests> getEventDeletedGuests(String eventID) {
		List<Guests> eventGuests = new ArrayList<Guests>();
	//	Cursor cursor = database.query(MySQLiteGuestHelper.TABLE_RESERVATIONS, allGuestColumns, WHERE_EVENTID, new String[] { eventID }, null, null, MySQLiteGuestHelper.FIRST_NAME + " ASC");
		Cursor cursor = database.rawQuery("SELECT reservationTypeName, guestQuantity, firstName, LastName, reservationID, reservationPaid, quantity, reservationOperationStatus FROM " + MySQLiteGuestHelper.TABLE_RESERVATIONS + ", " +  
				MySQLiteGuestHelper.TABLE_RESERVATION_TRANSACTION + ", " + MySQLiteGuestHelper.TABLE_RESERVATION_TYPE + " WHERE " + MySQLiteGuestHelper.TABLE_RESERVATIONS + "." + MySQLiteGuestHelper.RESERVATION_TRANS_ID + " = " +
				MySQLiteGuestHelper.TABLE_RESERVATION_TRANSACTION + "." + MySQLiteGuestHelper.RESERVATION_TRANSACTION_ID + " AND " + 
				MySQLiteGuestHelper.TABLE_RESERVATION_TRANSACTION + "." + MySQLiteGuestHelper.TRANSACTION_RESERVATION_TYPE_ID + " = " + MySQLiteGuestHelper.TABLE_RESERVATION_TYPE + "." + MySQLiteGuestHelper.RESERVATION_TYPE_ID
				+ " AND " + MySQLiteGuestHelper.TABLE_RESERVATIONS + "." + MySQLiteGuestHelper.EVENT_ID + " = ? " + "ORDER BY " + "UPPER(" + MySQLiteGuestHelper.TABLE_RESERVATIONS + "." + MySQLiteGuestHelper.FIRST_NAME + ")", new String[] { eventID });
				
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			int guestAmount = 0;
			int totalGuests = 0;
			Guests guestInfo = new Guests();
			guestInfo.setGuestName(cursor.getString(2) + " " + cursor.getString(3));
			String guestDescription = cursor.getString(0);
			guestInfo.setGuestTitle(guestDescription);
			guestInfo.setNotificationStatus("");
			guestInfo.setTotalGuestValue(cursor.getString(1));
			guestInfo.setReservationID(cursor.getString(4));
			String testBoolean = cursor.getString(7);
			if (cursor.getString(7).equals("2")) {
				guestInfo.setDeleted(true);
			} else {
				guestInfo.setDeleted(false);
			}
			
			String stampType = cursor.getString(5);
			if (stampType.equals("3")) {
				guestInfo.setStampType("comp");
			} else if (stampType.equals("1") || stampType.equals("5") || stampType.equals("6") || stampType.equals("7") || stampType.equals("8")) {
				guestInfo.setStampType("paid");
			} else if (stampType.equals("4")) {
				guestInfo.setStampType("unpaid");
			} else if (stampType.equals("10")) {
				guestInfo.setStampType("free");
			} else if (stampType.equals("13")) {
				guestInfo.setStampType("going");
			} else if (stampType.equals("14")) {
				guestInfo.setStampType("maybe");
			} else {
				guestInfo.setStampType("rsvp");
			}
			
			
			Cursor cursorTwo = database.rawQuery("Select operationQuantity from reservation, operation where reservation.reservationID = operation.operationReservationID and reservation.reservationID = " + cursor.getString(4), null);
			if (cursorTwo.getCount() > 0) {
				cursorTwo.moveToFirst();
				while (!cursorTwo.isAfterLast()) {
					String quantity = cursorTwo.getString(0);
					int amount = Integer.parseInt(quantity);
					guestAmount += (amount);
					cursorTwo.moveToNext();
				}
				guestInfo.setCurrentGuestValue(Integer.toString(guestAmount));
			} else {
				guestInfo.setCurrentGuestValue("0");
			}
			
			totalGuests += Integer.parseInt(cursor.getString(6));
			
			
			eventGuests.add(guestInfo);
			cursorTwo.close();
			cursor.moveToNext();
		}
		cursor.close();
		return eventGuests;
	}
	
	public List<Guests> getEventDeletedLastNameGuests(String eventID) {
		List<Guests> eventGuests = new ArrayList<Guests>();
	//	Cursor cursor = database.query(MySQLiteGuestHelper.TABLE_RESERVATIONS, allGuestColumns, WHERE_EVENTID, new String[] { eventID }, null, null, MySQLiteGuestHelper.FIRST_NAME + " ASC");
		Cursor cursor = database.rawQuery("SELECT reservationTypeName, guestQuantity, firstName, LastName, reservationID, reservationPaid, quantity, reservationOperationStatus FROM " + MySQLiteGuestHelper.TABLE_RESERVATIONS + ", " +  
				MySQLiteGuestHelper.TABLE_RESERVATION_TRANSACTION + ", " + MySQLiteGuestHelper.TABLE_RESERVATION_TYPE + " WHERE " + MySQLiteGuestHelper.TABLE_RESERVATIONS + "." + MySQLiteGuestHelper.RESERVATION_TRANS_ID + " = " +
				MySQLiteGuestHelper.TABLE_RESERVATION_TRANSACTION + "." + MySQLiteGuestHelper.RESERVATION_TRANSACTION_ID + " AND " + 
				MySQLiteGuestHelper.TABLE_RESERVATION_TRANSACTION + "." + MySQLiteGuestHelper.TRANSACTION_RESERVATION_TYPE_ID + " = " + MySQLiteGuestHelper.TABLE_RESERVATION_TYPE + "." + MySQLiteGuestHelper.RESERVATION_TYPE_ID
				+ " AND " + MySQLiteGuestHelper.TABLE_RESERVATIONS + "." + MySQLiteGuestHelper.EVENT_ID + " = ? " + "ORDER BY " + "UPPER(" + MySQLiteGuestHelper.TABLE_RESERVATIONS + "." + MySQLiteGuestHelper.LAST_NAME + ")", new String[] { eventID });
				
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			int guestAmount = 0;
			int totalGuests = 0;
			Guests guestInfo = new Guests();
			guestInfo.setGuestName(cursor.getString(2) + " " + cursor.getString(3));
			String guestDescription = cursor.getString(0);
			guestInfo.setGuestTitle(guestDescription);
			guestInfo.setNotificationStatus("");
			guestInfo.setTotalGuestValue(cursor.getString(1));
			guestInfo.setReservationID(cursor.getString(4));
			String testBoolean = cursor.getString(7);
			if (cursor.getString(7).equals("2")) {
				guestInfo.setDeleted(true);
			} else {
				guestInfo.setDeleted(false);
			}
			
			String stampType = cursor.getString(5);
			if (stampType.equals("3")) {
				guestInfo.setStampType("comp");
			} else if (stampType.equals("1") || stampType.equals("5") || stampType.equals("6") || stampType.equals("7") || stampType.equals("8")) {
				guestInfo.setStampType("paid");
			} else if (stampType.equals("4")) {
				guestInfo.setStampType("unpaid");
			} else if (stampType.equals("10")) {
				guestInfo.setStampType("free");
			} else if (stampType.equals("13")) {
				guestInfo.setStampType("going");
			} else if (stampType.equals("14")) {
				guestInfo.setStampType("maybe");
			} else {
				guestInfo.setStampType("rsvp");
			}
			
			
			Cursor cursorTwo = database.rawQuery("Select operationQuantity from reservation, operation where reservation.reservationID = operation.operationReservationID and reservation.reservationID = " + cursor.getString(4), null);
			if (cursorTwo.getCount() > 0) {
				cursorTwo.moveToFirst();
				while (!cursorTwo.isAfterLast()) {
					String quantity = cursorTwo.getString(0);
					int amount = Integer.parseInt(quantity);
					guestAmount += (amount);
					cursorTwo.moveToNext();
				}
				guestInfo.setCurrentGuestValue(Integer.toString(guestAmount));
			} else {
				guestInfo.setCurrentGuestValue("0");
			}
			
			totalGuests += Integer.parseInt(cursor.getString(6));
			
			
			eventGuests.add(guestInfo);
			cursorTwo.close();
			cursor.moveToNext();
		}
		cursor.close();
		return eventGuests;
	}
	
	
	public List<Guests> getEventGuests(String eventID) {
		List<Guests> eventGuests = new ArrayList<Guests>();
	//	Cursor cursor = database.query(MySQLiteGuestHelper.TABLE_RESERVATIONS, allGuestColumns, WHERE_EVENTID, new String[] { eventID }, null, null, MySQLiteGuestHelper.FIRST_NAME + " ASC");
		Cursor cursor = database.rawQuery("SELECT reservationTypeName, guestQuantity, firstName, LastName, reservationID, reservationPaid, quantity FROM " + MySQLiteGuestHelper.TABLE_RESERVATIONS + ", " +  
				MySQLiteGuestHelper.TABLE_RESERVATION_TRANSACTION + ", " + MySQLiteGuestHelper.TABLE_RESERVATION_TYPE + " WHERE " + MySQLiteGuestHelper.TABLE_RESERVATIONS + "." + MySQLiteGuestHelper.RESERVATION_TRANS_ID + " = " +
				MySQLiteGuestHelper.TABLE_RESERVATION_TRANSACTION + "." + MySQLiteGuestHelper.RESERVATION_TRANSACTION_ID + " AND " + 
				MySQLiteGuestHelper.TABLE_RESERVATION_TRANSACTION + "." + MySQLiteGuestHelper.TRANSACTION_RESERVATION_TYPE_ID + " = " + MySQLiteGuestHelper.TABLE_RESERVATION_TYPE + "." + MySQLiteGuestHelper.RESERVATION_TYPE_ID
				+ " AND reservation.reservationOperationStatus = 1 AND " + MySQLiteGuestHelper.TABLE_RESERVATIONS + "." + MySQLiteGuestHelper.EVENT_ID + " = ? " + "ORDER BY " + "UPPER(" + MySQLiteGuestHelper.TABLE_RESERVATIONS + "." + MySQLiteGuestHelper.FIRST_NAME + ")", new String[] { eventID });
				
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			int guestAmount = 0;
			int totalGuests = 0;
			Guests guestInfo = new Guests();
			guestInfo.setGuestName(cursor.getString(2) + " " + cursor.getString(3));
			String guestDescription = cursor.getString(0);
			guestInfo.setGuestTitle(guestDescription);
			guestInfo.setNotificationStatus("");
			guestInfo.setTotalGuestValue(cursor.getString(1));
			guestInfo.setReservationID(cursor.getString(4));
			
			String stampType = cursor.getString(5);
			if (stampType.equals("3")) {
				guestInfo.setStampType("comp");
			} else if (stampType.equals("1") || stampType.equals("5") || stampType.equals("6") || stampType.equals("7") || stampType.equals("8")) {
				guestInfo.setStampType("paid");
			} else if (stampType.equals("4")) {
				guestInfo.setStampType("unpaid");
			} else if (stampType.equals("10")) {
				guestInfo.setStampType("free");
			} else if (stampType.equals("13")) {
				guestInfo.setStampType("going");
			} else if (stampType.equals("14")) {
				guestInfo.setStampType("maybe");
			} else {
				guestInfo.setStampType("rsvp");
			}
			
			
			Cursor cursorTwo = database.rawQuery("Select operationQuantity from reservation, operation where reservation.reservationID = operation.operationReservationID and reservation.reservationID = " + cursor.getString(4), null);
			if (cursorTwo.getCount() > 0) {
				cursorTwo.moveToFirst();
				while (!cursorTwo.isAfterLast()) {
					String quantity = cursorTwo.getString(0);
					int amount = Integer.parseInt(quantity);
					guestAmount += (amount);
					cursorTwo.moveToNext();
				}
				guestInfo.setCurrentGuestValue(Integer.toString(guestAmount));
			} else {
				guestInfo.setCurrentGuestValue("0");
			}
			
			totalGuests += Integer.parseInt(cursor.getString(6));
			
			
			eventGuests.add(guestInfo);
			cursorTwo.close();
			cursor.moveToNext();
		}
		cursor.close();
		return eventGuests;
	}
	
	public List<Guests> getEventLastNameGuests(String eventID) {
		List<Guests> eventGuests = new ArrayList<Guests>();
	//	Cursor cursor = database.query(MySQLiteGuestHelper.TABLE_RESERVATIONS, allGuestColumns, WHERE_EVENTID, new String[] { eventID }, null, null, MySQLiteGuestHelper.FIRST_NAME + " ASC");
		Cursor cursor = database.rawQuery("SELECT reservationTypeName, guestQuantity, firstName, LastName, reservationID, reservationPaid, quantity FROM " + MySQLiteGuestHelper.TABLE_RESERVATIONS + ", " +  
				MySQLiteGuestHelper.TABLE_RESERVATION_TRANSACTION + ", " + MySQLiteGuestHelper.TABLE_RESERVATION_TYPE + " WHERE " + MySQLiteGuestHelper.TABLE_RESERVATIONS + "." + MySQLiteGuestHelper.RESERVATION_TRANS_ID + " = " +
				MySQLiteGuestHelper.TABLE_RESERVATION_TRANSACTION + "." + MySQLiteGuestHelper.RESERVATION_TRANSACTION_ID + " AND " + 
				MySQLiteGuestHelper.TABLE_RESERVATION_TRANSACTION + "." + MySQLiteGuestHelper.TRANSACTION_RESERVATION_TYPE_ID + " = " + MySQLiteGuestHelper.TABLE_RESERVATION_TYPE + "." + MySQLiteGuestHelper.RESERVATION_TYPE_ID
				+ " AND reservation.reservationOperationStatus = 1 AND " + MySQLiteGuestHelper.TABLE_RESERVATIONS + "." + MySQLiteGuestHelper.EVENT_ID + " = ? " + "ORDER BY " + "UPPER(" + MySQLiteGuestHelper.TABLE_RESERVATIONS + "." + MySQLiteGuestHelper.LAST_NAME + ")", new String[] { eventID });
				
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			int guestAmount = 0;
			int totalGuests = 0;
			Guests guestInfo = new Guests();
			guestInfo.setGuestName(cursor.getString(2) + " " + cursor.getString(3));
			String guestDescription = cursor.getString(0);
			guestInfo.setGuestTitle(guestDescription);
			guestInfo.setNotificationStatus("");
			guestInfo.setTotalGuestValue(cursor.getString(1));
			guestInfo.setReservationID(cursor.getString(4));
			
			String stampType = cursor.getString(5);
			if (stampType.equals("3")) {
				guestInfo.setStampType("comp");
			} else if (stampType.equals("1") || stampType.equals("5") || stampType.equals("6") || stampType.equals("7") || stampType.equals("8")) {
				guestInfo.setStampType("paid");
			} else if (stampType.equals("4")) {
				guestInfo.setStampType("unpaid");
			} else if (stampType.equals("10")) {
				guestInfo.setStampType("free");
			} else if (stampType.equals("13")) {
				guestInfo.setStampType("going");
			} else if (stampType.equals("14")) {
				guestInfo.setStampType("maybe");
			} else {
				guestInfo.setStampType("rsvp");
			}
			
			
			Cursor cursorTwo = database.rawQuery("Select operationQuantity from reservation, operation where reservation.reservationID = operation.operationReservationID and reservation.reservationID = " + cursor.getString(4), null);
			if (cursorTwo.getCount() > 0) {
				cursorTwo.moveToFirst();
				while (!cursorTwo.isAfterLast()) {
					String quantity = cursorTwo.getString(0);
					int amount = Integer.parseInt(quantity);
					guestAmount += (amount);
					cursorTwo.moveToNext();
				}
				guestInfo.setCurrentGuestValue(Integer.toString(guestAmount));
			} else {
				guestInfo.setCurrentGuestValue("0");
			}
			
			totalGuests += Integer.parseInt(cursor.getString(6));
			
			
			eventGuests.add(guestInfo);
			cursorTwo.close();
			cursor.moveToNext();
		}
		cursor.close();
		return eventGuests;
	}
	
	public boolean reservationExists(String checkReservationID) {
		Cursor cursor = database.query(MySQLiteGuestHelper.TABLE_RESERVATIONS, null, WHERE_RESERVATIONID, new String[] {checkReservationID}, null, null, null);
		boolean exists = (cursor.getCount() > 0);
		cursor.close();
		return exists;
	}
	
	public boolean typeExists(String checkTypeID) {
		Cursor cursor = database.query(MySQLiteGuestHelper.TABLE_RESERVATION_TYPE, new String[] { MySQLiteGuestHelper.RESERVATION_TYPE_ID } , WHERE_TYPEID, new String[] { checkTypeID }, null, null, null);
		boolean exists = (cursor.getCount() > 0);
		cursor.close();
		return exists;
	}
	
	public boolean transactionExists(String checkTransactionID) {
		Cursor cursor = database.query(MySQLiteGuestHelper.TABLE_RESERVATION_TRANSACTION, new String[] { MySQLiteGuestHelper.RESERVATION_TRANSACTION_ID }, WHERE_TRANSACTIONID, new String[] { checkTransactionID }, null, null, null);
		boolean exists = (cursor.getCount() > 0);
		cursor.close();
		return exists;
	}
	
	public boolean operationExists(String checkOperationID) {
		Cursor cursor = database.query(MySQLiteGuestHelper.TABLE_OPERATIONS, new String[] { MySQLiteGuestHelper.OPERATION_ID }, WHERE_OPERATIONID, new String[] { checkOperationID } , null, null, null);
		boolean exists = (cursor.getCount() > 0);
		cursor.close();
		return exists;
	}
	
	public void createReservationType(String reservationTypeID, String reservationName, String door, String color, String price, String pricing) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteGuestHelper.RESERVATION_TYPE_ID, reservationTypeID);
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
	
	public void updateReservationType(String reservationTypeID, String reservationName, String door, String color, String price, String pricing) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteGuestHelper.RESERVATION_TYPE_ID, reservationTypeID);
		values.put(MySQLiteGuestHelper.RESERVATION_TYPE_NAME, reservationName);
		values.put(MySQLiteGuestHelper.DOOR_TYPE, door);
		values.put(MySQLiteGuestHelper.COLOR_TYPE, color);
		values.put(MySQLiteGuestHelper.PRICE_TYPE, price);
		values.put(MySQLiteGuestHelper.PRICING_TYPE, pricing);
		database.update(MySQLiteGuestHelper.TABLE_RESERVATION_TYPE, values, MySQLiteGuestHelper.RESERVATION_TYPE_ID + "=" + reservationTypeID, null);
	}
	
	public String getReservationName(String reservationID) {
		String reservationName;
		Cursor cursor = database.query(MySQLiteGuestHelper.TABLE_RESERVATION_TYPE, allReservationIDColumns, WHERE_RESERVATION_TYPE_ID, new String[] { reservationID }, null, null, null);
		
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			reservationName = "";
		} else {
			reservationName = cursor.getString(1);
		}
		cursor.close();
		return reservationName;
	}
	
	//public String getQuantity(String)
	
	public void sortGuests() {
		Cursor cursor = database.query(MySQLiteGuestHelper.TABLE_RESERVATIONS, allGuestColumns, null, null, null, null, MySQLiteGuestHelper.FIRST_NAME + " ASC");
		cursor.moveToFirst();
	}
	
	public String getTotalEventGuests(String eventID) {
		
		Cursor cursor = database.rawQuery("select guestQuantity from reservation where reservationOperationStatus = 1 AND reservation.eventID = ?", new String[] { eventID });
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			int totalEventGuest = 0;
			while(!cursor.isAfterLast()) {
					
			  int totalValue = (Integer.parseInt(cursor.getString(0)));
			  totalEventGuest +=  totalValue;
			  cursor.moveToNext();
			}
			cursor.close();
			return Integer.toString(totalEventGuest);
			
		} else {
			cursor.close();
			return "0";
		}
		
	}
	
	public String getCurrentEventGuests(String eventID) {
		Cursor cursor = database.rawQuery("select operationQuantity from reservation, operation where operationReservationID = reservationID AND reservationOperationStatus = 1 AND eventID = ?", new String[] { eventID });
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			int currentEventGuest = 0;
			while(!cursor.isAfterLast()) {
				int totalValue = (Integer.parseInt(cursor.getString(0)));
				currentEventGuest += totalValue;
				cursor.moveToNext();
			}
			cursor.close();
			return Integer.toString(currentEventGuest);
		} else {
			cursor.close();
			return "0";
		}
	}
	
	public String getReservationID(String guestID) {
		Cursor cursor = database.rawQuery("select reservationID from reservation where guestID = ?", new String[] { guestID });
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			cursor.close();
			return cursor.getString(0);
		} else {
			cursor.close();
			return "0";
		}
	}
	
	public String getUserOperations(String eventID) {
		ArrayList<Object[][]> operationArray = new ArrayList<Object[][]>();
		String encodedUserOperations = "";
		Cursor cursor = database.rawQuery("select operationID, operationReservationID, operationQuantity, operationCheckIn, operationToBeSent from operation, reservation where operationToBeSent = 1 and reservation.eventID = ? and operationReservationID = reservationID", new String[] { eventID });
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			int i = 0;
			Object[][] operationMultiArray = new Object[cursor.getCount()][7];
			while(!cursor.isAfterLast()) {
				
				int operationID = Integer.parseInt(cursor.getString(cursor.getColumnIndex("operationID")));
				operationMultiArray[i][0] = operationID;
				operationMultiArray[i][1] = 0;
				operationMultiArray[i][2] =  Integer.parseInt(cursor.getString(cursor.getColumnIndex("operationReservationID")));
				operationMultiArray[i][3] = 0;
				operationMultiArray[i][4] = Integer.parseInt(cursor.getString(cursor.getColumnIndex("operationQuantity")));
				String timeStamp = cursor.getString(cursor.getColumnIndex("operationCheckIn"));
				// fix this bad programming!
				StringBuffer sb = new StringBuffer(timeStamp);
				sb.insert(4, '-');
				sb.insert(7, '-');
				sb.insert(10, " ");
				sb.insert(13, ":");
				sb.insert(16, ":");
				
				operationMultiArray[i][5] = sb.toString();
				operationMultiArray[i][6] = cursor.getString(cursor.getColumnIndex("operationToBeSent"));
			//	String jsonOperation = gson.toJson(operationMultiArray);
			//	encodedUserOperations = URLEncoder.encode(jsonOperation, "UTF-8");
			//	operationArray.add(operationMultiArray);
				i++;
			//	String[] operation =  {cursor.getString(cursor.getColumnIndex("operationID")), "0", cursor.getString(cursor.getColumnIndex("operationReservationID")), "0", cursor.getString(cursor.getColumnIndex("operationQuantity")), cursor.getString(cursor.getColumnIndex("operationCheckIn")), cursor.getString(cursor.getColumnIndex("operationToBeSent")) };
			//	String operationString = Arrays.toString(operation);
				
				cursor.moveToNext();
			}
			String jsonOperation = gson.toJson(operationMultiArray);
			
			try {
				encodedUserOperations = URLEncoder.encode(jsonOperation, "UTF-8");
				cursor.close();
				return encodedUserOperations;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			cursor.close();
			return encodedUserOperations;
		}
	//	JSONArray jsonArray = new JSONArray(operationArray);	
		cursor.close();
		return encodedUserOperations;
	}
	
	public void updateOperations(String operationID, String checkInTime, String operationReservationID) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteGuestHelper.OPERATION_ID, operationID);
		values.put(MySQLiteGuestHelper.OPERATION_TOBESENT, "0");
	//	database.rawQuery("update operation set operationID = " + operationID + ", operationToBeSent = 0 where operationToBeSent = 1 and operationCheckIn = ? and operationReservationID = ?", new String[] { checkInTime, operationReservationID });
		database.update(MySQLiteGuestHelper.TABLE_OPERATIONS, values, MySQLiteGuestHelper.OPERATION_TIME_CHECK_IN + " = " + checkInTime + " AND operationReservationID = " + operationReservationID, null);
	}
	
	public int getReservationQuantity(String reservationID) {
		Cursor cursor = database.rawQuery("select guestquantity from reservation where reservationID = ?", new String[] { reservationID });
		cursor.moveToFirst();
		String guestQuantityString = cursor.getString(cursor.getColumnIndex("guestquantity"));
		int guestQuantityNumber = Integer.parseInt(guestQuantityString);
		return guestQuantityNumber;
	}
	

}
