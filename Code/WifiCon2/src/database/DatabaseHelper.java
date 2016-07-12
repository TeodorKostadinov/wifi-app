package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super( context, name, factory, version );
		// TODO Auto-generated constructor stub
	}
	
	public DatabaseHelper(Context context) {
		super( context, DATABASE_NAME, null, DATABASE_VERSION );
	}
	
	private static final String LOGTAG = "EXPLORECA";
	
	private static final String DATABASE_NAME = "infromation";
	private static final int DATABASE_VERSION = 3;
	
	public static final String TABLE_EXCEPTIONS = "exceptions";
	public static final String TABLE_SPOTS = "spots";
	public static final String TABLE_TASKS = "tasks";
	
	public static final String TABLE_CREATE_NOEXIST = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_EXCEPTIONS
			+ "("
			+ "ID"
			+ " integer primary key autoincrement, "
			+ ExceptionFields.fromClassName.getText()
			+ " TEXT not null, "
			+ ExceptionFields.classErrorName.getText()
			+ " TEXT not null, "
			+ ExceptionFields.exceptionStack.getText()
			+ " TEXT not null, "
			+ ExceptionFields.timeOfError.getText() + " TEXT not null" + ")";
	
	public static final String EXCEPTIONS_TABLE_CREATE = "CREATE TABLE "
			+ TABLE_EXCEPTIONS + " (ID"
			+ " integer primary key autoincrement, "
			+ ExceptionFields.fromClassName.getText() + " TEXT not null, "
			+ ExceptionFields.classErrorName.getText() + " TEXT not null, "
			+ ExceptionFields.exceptionStack.getText() + " TEXT not null, "
			+ ExceptionFields.timeOfError.getText() + " TEXT not null" + ")";
	
	public static final String SPOTS_TABLE_CREATE = "CREATE TABLE "
			+ TABLE_SPOTS + " (" + SpotFields.fakeId.getText()
			+ " TEXT not null, " + SpotFields.id.getText() + " TEXT not null, "
			+ SpotFields.name.getText() + " TEXT not null, "
			+ SpotFields.password.getText() + " TEXT not null, "
			+ SpotFields.ip.getText() + " TEXT not null, "
			+ SpotFields.address.getText() + " TEXT not null, "
			+ SpotFields.latitude.getText() + " TEXT not null, "
			+ SpotFields.longitude.getText() + " TEXT not null, "
			+ SpotFields.isActive.getText() + " TEXT not null, "
			+ SpotFields.rowId.getText() + " INTEGER PRIMARY KEY AUTOINCREMENT"
			+ ")";
	
	public static final String TASKS_TABLE_CREATE = "CREATE TABLE "
			+ TABLE_TASKS + " (" + TaskFields.fakeId.getText()
			+ " TEXT not null, " + TaskFields.id.getText() + " TEXT not null, "
			+ TaskFields.name.getText() + " TEXT not null, "
			+ TaskFields.password.getText() + " TEXT not null, "
			+ TaskFields.address.getText() + " TEXT not null, "
			+ TaskFields.keyword.getText() + " TEXT not null, "
			+ TaskFields.website.getText() + " TEXT not null, "
			+ TaskFields.status.getText() + " TEXT not null, "
			+ TaskFields.lati.getText() + " TEXT not null, "
			+ TaskFields.longi.getText() + " TEXT not null, "
			+ TaskFields.rowId.getText() + " INTEGER PRIMARY KEY AUTOINCREMENT"
			+ ")";
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL( EXCEPTIONS_TABLE_CREATE );
		Log.i( LOGTAG, "Table has been created: " + EXCEPTIONS_TABLE_CREATE );
		db.execSQL( SPOTS_TABLE_CREATE );
		Log.i( LOGTAG, "Table has been created: " + SPOTS_TABLE_CREATE );
		db.execSQL( TASKS_TABLE_CREATE );
		Log.i( LOGTAG, "Table has been created: " + TASKS_TABLE_CREATE );
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL( "DROP TABLE IF EXISTS " + TABLE_EXCEPTIONS );
		db.execSQL( "DROP TABLE IF EXISTS " + TABLE_SPOTS );
		db.execSQL( "DROP TABLE IF EXISTS " + TABLE_TASKS );
		onCreate( db );
	}
}