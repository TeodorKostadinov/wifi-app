package util;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import database.TaskFields;

public class Task {
	private String address;
	private String fakeId;
	private String id;
	private String name;
	private String password;
	private String keyword;
	private String website;
	private String status;
	private String longi;
	private String lati;
	private int tableId;
	private Context context;
	
	public Task(JSONObject jsonTask, String fakeId, Context context) {
		try {
			this.context = context;
			this.setFakeId( fakeId );
			this.setId( jsonTask.getString( "Id" ) );
			this.setAddress( jsonTask.getString( "Address" ) );
			this.setName( jsonTask.getString( "Name" ) );
			this.setPassword( jsonTask.getString( "Password" ) );
			this.setKeyword( jsonTask.getString( "Keyword" ) );
			this.setWebsite( jsonTask.getString( "Website" ) );
			this.setLongi( jsonTask.getString( "Longi" ) );
			this.setLati( jsonTask.getString( "Lati" ) );
			this.setStatus( jsonTask.getString( "Status" ) );
			
		} catch (JSONException e) {
			Util.addException( this.getClass(), e, context );
		}
	}
	
	public Task(String[] taskInfo, Context context) {
		try {
			this.context = context;
			this.setFakeId( taskInfo[0] );
			this.setId( taskInfo[1] );
			this.setAddress( taskInfo[2] );
			this.setName( taskInfo[3] );
			this.setPassword( taskInfo[4] );
			this.setKeyword( taskInfo[5] );
			this.setWebsite( taskInfo[6] );
			this.setLongi( taskInfo[7] );
			this.setLati( taskInfo[8] );
			this.setStatus( taskInfo[9] );
			this.setTableId( Integer.parseInt( taskInfo[10] ) );
			
		} catch (NullPointerException e) {
			Util.addException( this.getClass(), e, context );
		}
	}
	
	public int getTableId() {
		return this.tableId;
	}
	
	public void setTableId(int id) {
		this.tableId = id;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getWebsite() {
		return website;
	}
	
	public String getId() {
		return id;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getLongi() {
		return longi;
	}
	
	public String getLati() {
		return lati;
	}
	
	private void setLongi(String longi) {
		this.longi = longi;
	}
	
	private void setLati(String lati) {
		this.lati = lati;
	}
	
	private void setId(String id) {
		this.id = id;
	}
	
	public void setStatus(String string) {
		this.status = string;
	}
	
	private void setWebsite(String website) {
		this.website = website;
	}
	
	private void setName(String name) {
		this.name = name;
	}
	
	private void setPassword(String password) {
		this.password = password;
	}
	
	private void setAddress(String address) {
		this.address = address;
	}
	
	private void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public String getFakeId() {
		return fakeId;
	}
	
	public void setFakeId(String fakeId) {
		this.fakeId = fakeId;
	}
	
	// for printing info to console, no use atm
	public String getTaskInfo() {
		String separator = Util.SEPARATOR;
		String output = "";
		output += getFakeId() + separator;
		output += getId() + separator;
		output += getAddress() + separator;
		output += getName() + separator;
		output += getPassword() + separator;
		output += getKeyword() + separator;
		output += getWebsite() + separator;
		output += getLongi() + separator;
		output += getLati() + separator;
		output += getStatus();
		return output;
	}
	
	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		values.put( TaskFields.fakeId.getText(), this.getFakeId() );
		values.put( TaskFields.id.getText(), this.getId() );
		values.put( TaskFields.address.getText(), this.getAddress() );
		values.put( TaskFields.name.getText(), this.getName() );
		values.put( TaskFields.password.getText(), this.getPassword() );
		values.put( TaskFields.website.getText(), this.getWebsite() );
		values.put( TaskFields.keyword.getText(), this.getKeyword() );
		values.put( TaskFields.longi.getText(), this.getLongi() );
		values.put( TaskFields.lati.getText(), this.getLati() );
		values.put( TaskFields.status.getText(), this.getStatus() );
		return values;
	}
}
