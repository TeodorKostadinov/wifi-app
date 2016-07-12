package database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import util.Task;
import util.UserException;
import util.WifiSpot;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class InfoDataSource {
	
	public static final String LOGTAG = "EXPLORECA";
	
	SQLiteOpenHelper dbhelper;
	SQLiteDatabase database;
	Context context;
	
	public static final String[] allExceptionColumns = { "ID",
			ExceptionFields.fromClassName.getText(),
			ExceptionFields.classErrorName.getText(),
			ExceptionFields.exceptionStack.getText(),
			ExceptionFields.timeOfError.getText() };
	
	public static final String[] allTaskColumns = {
			TaskFields.address.getText(), TaskFields.fakeId.getText(),
			TaskFields.id.getText(), TaskFields.keyword.getText(),
			TaskFields.lati.getText(), TaskFields.longi.getText(),
			TaskFields.name.getText(), TaskFields.password.getText(),
			TaskFields.status.getText(), TaskFields.website.getText(),
			TaskFields.rowId.getText()
	
	};
	
	public static final String[] allSpotColumns = {
			SpotFields.address.getText(), SpotFields.fakeId.getText(),
			SpotFields.id.getText(), SpotFields.ip.getText(),
			SpotFields.isActive.getText(), SpotFields.latitude.getText(),
			SpotFields.longitude.getText(), SpotFields.name.getText(),
			SpotFields.password.getText(), SpotFields.rowId.getText(), };
	
	public InfoDataSource(Context context) {
		dbhelper = new DatabaseHelper( context );
		this.context = context;
	}
	
	public void open() {
		Log.i( LOGTAG, "Database opened" );
		database = dbhelper.getWritableDatabase();
	}
	
	public void close() {
		Log.i( LOGTAG, "Database closed" );
		dbhelper.close();
	}
	
	public long createUserException(UserException userException) {
		database = dbhelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put( ExceptionFields.classErrorName.getText(),
				userException.getClassErrorName() );
		values.put( ExceptionFields.fromClassName.getText(),
				userException.getFromClassName() );
		values.put( ExceptionFields.exceptionStack.getText(),
				userException.getExceptionStack() );
		// TODO://values.put( DatabaseHelper.TimeOfError,
		// userException.getTimeOfError()
		// .format( "%Y %m %d %H %M %S" ) );
		SimpleDateFormat format1 = new SimpleDateFormat(
				"yyyy, MM, dd, HH, mm, ss" );
		values.put( ExceptionFields.timeOfError.getText(),
				format1.format( userException.getTimeOfError().getTime() ) );
		return database.insert( DatabaseHelper.TABLE_EXCEPTIONS, null, values );
	}
	
	public List<UserException> findAllExceptions() {
		List<UserException> list = new ArrayList<UserException>();
		database = dbhelper.getReadableDatabase();
		Cursor c = database.query( DatabaseHelper.TABLE_EXCEPTIONS,
				allExceptionColumns, null, null, null, null, null );
		
		c.moveToFirst();
		if (c.getCount() > 0) {
			while (!c.isAfterLast()) {
				try {
					String fromClass = c.getString( c
							.getColumnIndex( ExceptionFields.fromClassName
									.getText() ) );
					String trace = c.getString( c
							.getColumnIndex( ExceptionFields.exceptionStack
									.getText() ) );
					String timeDB = c.getString( c
							.getColumnIndex( ExceptionFields.timeOfError
									.getText() ) );
					String errorClass = c.getString( c
							.getColumnIndex( ExceptionFields.classErrorName
									.getText() ) );
					
					if (fromClass.isEmpty() || fromClass == null) {
						c.moveToNext();
					} else {
						String[] parse = timeDB.split( " " );
						Calendar time = new GregorianCalendar();
						time.set(
								Integer.parseInt( parse[0].replace( ",", "" ) ),
								Integer.parseInt( parse[1].replace( ",", "" ) ),
								Integer.parseInt( parse[2].replace( ",", "" ) ),
								Integer.parseInt( parse[3].replace( ",", "" ) ),
								Integer.parseInt( parse[4].replace( ",", "" ) ),
								Integer.parseInt( parse[5].replace( ",", "" ) ) );
						Exception exception = new Exception( trace );
						UserException uex = new UserException();
						uex.setFromClassName( fromClass );
						uex.setException( exception );
						uex.setTimeOfError( time );
						uex.setClassErrorName( errorClass );
						list.add( uex );
						c.moveToNext();
					}
				} catch (NullPointerException e) {
					c.moveToNext();
				} catch (CursorIndexOutOfBoundsException ex) {
					c.moveToNext();
				}
			}
		}
		return list;
	}
	
	public long createTask(Task task) {
		database = dbhelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put( TaskFields.fakeId.getText(), task.getFakeId() );
		values.put( TaskFields.id.getText(), task.getId() );
		values.put( TaskFields.address.getText(), task.getAddress() );
		values.put( TaskFields.name.getText(), task.getName() );
		values.put( TaskFields.password.getText(), task.getPassword() );
		values.put( TaskFields.website.getText(), task.getWebsite() );
		values.put( TaskFields.keyword.getText(), task.getKeyword() );
		values.put( TaskFields.longi.getText(), task.getLongi() );
		values.put( TaskFields.lati.getText(), task.getLati() );
		values.put( TaskFields.status.getText(), task.getStatus() );
		return database.insert( DatabaseHelper.TABLE_TASKS, null, values );
	}
	
	public List<Task> findAllTasks() {
		List<Task> list = new ArrayList<Task>();
		database = dbhelper.getReadableDatabase();
		Cursor c = database.query( DatabaseHelper.TABLE_TASKS, allTaskColumns,
				null, null, null, null, null );
		
		c.moveToFirst();
		
		if (c.getCount() > 0) {
			while (!c.isAfterLast()) {
				try {
					String fakeId = c.getString( c
							.getColumnIndex( TaskFields.fakeId.getText() ) );
					String id = c.getString( c.getColumnIndex( TaskFields.id
							.getText() ) );
					String address = c.getString( c
							.getColumnIndex( TaskFields.address.getText() ) );
					String name = c.getString( c
							.getColumnIndex( TaskFields.name.getText() ) );
					
					String password = c.getString( c
							.getColumnIndex( TaskFields.password.getText() ) );
					
					String keyword = c.getString( c
							.getColumnIndex( TaskFields.keyword.getText() ) );
					String website = c.getString( c
							.getColumnIndex( TaskFields.website.getText() ) );
					
					String longi = c.getString( c
							.getColumnIndex( TaskFields.longi.getText() ) );
					String lati = c.getString( c
							.getColumnIndex( TaskFields.lati.getText() ) );
					String status = c.getString( c
							.getColumnIndex( TaskFields.status.getText() ) );
					String rowId = c.getString( c
							.getColumnIndex( TaskFields.rowId.getText() ) );
					String[] taskInfo = { fakeId, id, address, name, password,
							keyword, website, longi, lati, status, rowId };
					Task task = new Task( taskInfo, context );
					list.add( task );
					c.moveToNext();
				} catch (NullPointerException e) {
					c.moveToNext();
				} catch (CursorIndexOutOfBoundsException ex) {
					c.moveToNext();
				}
			}
		}
		return list;
	}
	
	public long createWifiSpot(WifiSpot wifiSpot) {
		database = dbhelper.getWritableDatabase();
		ContentValues values = wifiSpot.getContentValues();
		
		return database.insert( DatabaseHelper.TABLE_SPOTS, null, values );
	}
	
	public List<WifiSpot> findAllWifiSpots() {
		List<WifiSpot> list = new ArrayList<WifiSpot>();
		database = dbhelper.getReadableDatabase();
		Cursor c = database.query( DatabaseHelper.TABLE_SPOTS, allSpotColumns,
				null, null, null, null, null );
		c.moveToFirst();
		if (c.getCount() > 0) {
			while (!c.isAfterLast()) {
				try {
					String fakeId = c.getString( c
							.getColumnIndex( SpotFields.fakeId.getText() ) );
					String id = c.getString( c.getColumnIndex( SpotFields.id
							.getText() ) );
					String name = c.getString( c
							.getColumnIndex( SpotFields.name.getText() ) );
					String password = c.getString( c
							.getColumnIndex( SpotFields.password.getText() ) );
					String ip = c.getString( c.getColumnIndex( SpotFields.ip
							.getText() ) );
					String address = c.getString( c
							.getColumnIndex( SpotFields.address.getText() ) );
					String latitude = c.getString( c
							.getColumnIndex( SpotFields.latitude.getText() ) );
					String longitude = c.getString( c
							.getColumnIndex( SpotFields.longitude.getText() ) );
					String isActive = c.getString( c
							.getColumnIndex( SpotFields.isActive.getText() ) );
					String rowId = c.getString( c
							.getColumnIndex( SpotFields.rowId.getText() ) );
					String[] data = { fakeId, id, name, password, ip, address,
							latitude, longitude, isActive, rowId };
					WifiSpot uex = new WifiSpot( data, context );
					
					list.add( uex );
					c.moveToNext();
				} catch (NullPointerException e) {
					c.moveToNext();
				} catch (CursorIndexOutOfBoundsException ex) {
					c.moveToNext();
				}
			}
		}
		return list;
	}
	
	public int getTasksCount() {
		int count = 0;
		String countQuery = "SELECT  * FROM " + "tasks";
		Cursor cursor = database.rawQuery( countQuery, null );
		count = cursor.getCount();
		cursor.close();
		return count;
	}
	
	public int getSpotsCount() {
		int count = 0;
		String countQuery = "SELECT  * FROM " + "spots";
		Cursor cursor = database.rawQuery( countQuery, null );
		count = cursor.getCount();
		cursor.close();
		return count;
	}
	
	public void deleteTable(String table) {
		database.execSQL( "DROP TABLE IF EXISTS " + table );
		Log.i( "deleted", "table " + table );
	}
	
	public void createTableFromSQL(String tableSQL) {
		database.execSQL( tableSQL );
		Log.i( "created", "table " + tableSQL );
	}
	
	public void clearTable(String table) {
		database.execSQL( "delete from " + table );
		Log.i( "cleared", "table " + table );
	}
	
	public void updateTask(Task task) {
		ContentValues data = task.getContentValues();
		
		database.update( DatabaseHelper.TABLE_TASKS, data,
				TaskFields.rowId.getText() + "=" + task.getTableId() + "", null );
		Cursor cursor = database.rawQuery(
				"SELECT changes() AS affected_row_count", null );
		int count = cursor.getColumnCount();
		if (count <= 0) {
			createTask( task );
		}
		
	}
	
	public long updateSpot(WifiSpot spot) {
		ContentValues data = spot.getContentValues();
		// long a = database
		// .update( DatabaseHelper.TABLE_SPOTS, data,
		// SpotFields.rowId.getText() + "=" + spot.getTableId()
		// + "", null );
		
		long a = createWifiSpot( spot );
		Cursor cursor = database.rawQuery(
				"SELECT changes() AS affected_row_count", null );
		int count = cursor.getCount();
		if (count <= 0) {
			a = createWifiSpot( spot );
		}
		return a;
	}
}
