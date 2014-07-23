package event.planning.Boomset;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteGuestHelper extends SQLiteOpenHelper {

	public static final String TABLE_RESERVATIONS = "reservation";
	public static final String TABLE_RESERVATION_TRANSACTION = "reservationTransaction";
	public static final String TABLE_RESERVATION_TYPE = "reservationType";
	public static final String TABLE_OPERATIONS = "operation";
	
	public static final String PRIMARY_ID = "reservation_id";
	public static final String TYPE_PRIMARY_ID = "type_id";
	public static final String TRANSACTION_PRIMARY_ID = "transaction_id";
	public static final String OPERATION_PRIMARY_ID = "operation_id";
	
	public static final String EVENT_ID = "eventID";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String PHONE_NUMBER = "phone";
	public static final String EMAIL_ADDRESS = "email";
	public static final String GUEST_ID = "guestID";
	public static final String RESERVATION_ID = "reservationID";
	public static final String RESERVATION_TRANS_ID = "reservationTransactionID";
	public static final String GUEST_QUANTITY = "guestQuantity";
	public static final String RESERVATION_PAID = "reservationPaid";
	public static final String RESERVATION_OPERATION_STATUS = "reservationOperationStatus";
//	public static final String RESERVATION_OPERATION_ID = "reservationOperationID";
	
	public static final String RESERVATION_TYPE_ID = "reservationTypeID";
	public static final String RESERVATION_TYPE_NAME = "reservationTypeName";
	public static final String DOOR_TYPE = "door";
	public static final String COLOR_TYPE = "color";
	public static final String PRICE_TYPE = "price";
	public static final String PRICING_TYPE = "pricing";
	
	public static final String RESERVATION_TRANSACTION_ID = "transactionID";
	public static final String TRANSACTION_AMOUNT = "amount";
	public static final String TRANSACTION_PAYMENT = "paidType";
	public static final String OBJECTIVE_STATUS = "objectiveStatus";
	public static final String TRANSACTION_QUANTITY = "quantity";
	public static final String TRANSACTION_GROUP_ID = "transactionGroupID";
	public static final String TRANSACTION_GROUP_BUYER = "transactionGroupBuyer";
	public static final String TRANSACTION_DATE = "transactionDate";
	public static final String TRANSACTION_RESERVATION_TYPE_ID = "transactionReservationTypeID";
	public static final String TRANSACTION_EVENT_ID = "transactionEventID";
	
	public static final String OPERATION_ID = "operationID";
	public static final String OPERATION_USER_ID = "operationUserID";
	public static final String OPERATION_QUANTITY = "operationQuantity";
	public static final String OPERATION_TIME_CHECK_IN = "operationCheckIn";
	public static final String OPERATION_OBJECT_STATUS = "operationObjectStatus";
	public static final String OPERATION_RESERVATION_ID = "operationReservationID";
	public static final String OPERATION_TOBESENT = "operationToBeSent";
	
	private static final String DATABASE_CREATE_RESERVATIONS = "create table " + TABLE_RESERVATIONS + "(" + PRIMARY_ID + 
			 " integer primary key autoincrement, " + EVENT_ID + " text," + RESERVATION_ID + " text UNIQUE," + FIRST_NAME + " text," + LAST_NAME +
			 " text, " + GUEST_ID + " text, " + PHONE_NUMBER + " text, " + EMAIL_ADDRESS + " text, " +
			 RESERVATION_TRANS_ID + " text, " + GUEST_QUANTITY + " text, " + RESERVATION_PAID + " text, " + RESERVATION_OPERATION_STATUS + " text, " + "FOREIGN KEY(" + RESERVATION_TRANS_ID + ") REFERENCES " 
			 + TABLE_RESERVATION_TRANSACTION + " (" + RESERVATION_TRANSACTION_ID + "));";
	
	private static final String DATABASE_CREATE_RESERVATION_TYPE = "create table " + TABLE_RESERVATION_TYPE + "(" + TYPE_PRIMARY_ID + 
			 " integer primary key autoincrement, " + RESERVATION_TYPE_ID + " text UNIQUE," + RESERVATION_TYPE_NAME + " text," + DOOR_TYPE +
			 " text, " + COLOR_TYPE + " text, " + PRICE_TYPE + " text, " + PRICING_TYPE + " text);";
	
	private static final String DATABASE_CREATE_RESERVATION_TRANSACTION = "create table " + TABLE_RESERVATION_TRANSACTION + "(" + TRANSACTION_PRIMARY_ID +
			" integer primary key autoincrement, " + RESERVATION_TRANSACTION_ID + " text UNIQUE, " + TRANSACTION_AMOUNT + " text, " + TRANSACTION_PAYMENT + " text, " + OBJECTIVE_STATUS + " text, " + 
			TRANSACTION_QUANTITY + " text, " + TRANSACTION_GROUP_ID + " text, " + TRANSACTION_GROUP_BUYER + " text, " + TRANSACTION_DATE + " text, " + TRANSACTION_RESERVATION_TYPE_ID + " text," 
			+ TRANSACTION_EVENT_ID + " text, " + "FOREIGN KEY(" + TRANSACTION_RESERVATION_TYPE_ID + ") REFERENCES " + TABLE_RESERVATION_TYPE + " (" + RESERVATION_TYPE_ID + "));"; 
	
	private static final String DATABASE_CREATE_OPERATION = "create table " + TABLE_OPERATIONS + "(" + OPERATION_PRIMARY_ID + " integer primary key autoincrement, " + 
			OPERATION_ID + " text, " + OPERATION_USER_ID + " text, " + OPERATION_QUANTITY + " text, " + OPERATION_TIME_CHECK_IN + " text, " + OPERATION_OBJECT_STATUS + " text, "
			+ OPERATION_RESERVATION_ID + " text, " + OPERATION_TOBESENT + " text, " + "FOREIGN KEY(" + OPERATION_RESERVATION_ID + ") REFERENCES " + TABLE_RESERVATIONS + " (" + RESERVATION_ID + "));"; 
			
	
	private static final String DATABASE_NAME = "guests.db";
	private static final int DATABASE_VERSION = 1;
	
	public MySQLiteGuestHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("PRAGMA foreign_keys=ON;");
		db.execSQL(DATABASE_CREATE_RESERVATION_TYPE);
		db.execSQL(DATABASE_CREATE_RESERVATION_TRANSACTION);
		db.execSQL(DATABASE_CREATE_RESERVATIONS);
		db.execSQL(DATABASE_CREATE_OPERATION);
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATION_TYPE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATION_TRANSACTION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPERATIONS);
		onCreate(db);
	}

}
