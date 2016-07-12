package util;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import database.SpotFields;

public class WifiSpot {
	private String fakeId;
	private String id;
	private String name;
	private String password;
	private String ip;
	private String address;
	private String latitude;
	private String longitude;
	private Boolean isActive;
	private int tableId;
	
	public void setTableId(int id) {
		this.tableId = id;
	}
	
	public int getTableId() {
		return this.tableId;
	}
	
	public Boolean getIsActive() {
		return this.isActive;
	}
	
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	public String getLatitude() {
		return latitude.trim();
	}
	
	public String getLongitude() {
		return longitude.trim();
	}
	
	private void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	private void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public String getAddress() {
		return address.trim();
	}
	
	private void setAddress(String address) {
		this.address = address;
	}
	
	Context context;
	
	// for data from database
	
	public WifiSpot(JSONObject jsonSpot, Integer fakeId, Boolean isForMap,
			Context context) {
		try {
			this.context = context;
			this.setFakeId( fakeId.toString().trim() );
			this.setId( jsonSpot.getString( "Id" ).trim() );
			this.setName( jsonSpot.getString( "Name" ).trim() );
			if (jsonSpot.getString( "Password" ).equals( "_" ))
				this.setPassword( "" );
			else
				this.setPassword( jsonSpot.getString( "Password" ).trim() );
			this.setIp( jsonSpot.getString( "Ip" ).trim() );
			this.setAddress( jsonSpot.getString( "Address" ).trim() );
			
			if (jsonSpot.getString( "Active" ).equals( "true" )) {
				this.setIsActive( true );
			} else {
				this.setIsActive( false );
			}
			this.setLatitude( jsonSpot.getString( "Lati" ).trim() );
			this.setLongitude( jsonSpot.getString( "Longi" ).trim() );
			
		} catch (JSONException e) {
			Util.addException( this.getClass(), e, context );
		}
	}
	
	// for local data
	
	public WifiSpot(String[] spotInfo, Context context) {
		try {
			this.context = context;
			this.setFakeId( spotInfo[0].trim() );
			this.setId( spotInfo[1].trim() );
			this.setName( spotInfo[2].trim() );
			try {
				if (spotInfo[3].equals( "_" ))
					this.setPassword( "" );
				else
					this.setPassword( spotInfo[3].trim() );
			} catch (Exception ex) {
				for (int i = 0; i < spotInfo.length; i++) {
					Log.w( "spot", spotInfo[i].trim() );
				}
				Util.addException( this.getClass(), ex, context );
			}
			this.setIp( spotInfo[4].trim() );
			this.setAddress( spotInfo[5].trim() );
			this.setLatitude( spotInfo[6].trim() );
			this.setLongitude( spotInfo[7].trim() );
			if (spotInfo[8].trim().toLowerCase().equals( "true" ))
				this.setIsActive( true );
			else {
				this.setIsActive( false );
			}
			this.setTableId( Integer.parseInt( spotInfo[9] ) );
			
		} catch (NullPointerException e) {
			Util.addException( this.getClass(), e, context );
		}
	}
	
	public String getId() {
		return id.trim();
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name.trim();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPassword() {
		password = password.replaceAll( "_", "" );
		return password.trim();
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getIp() {
		return ip.trim();
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getFakeId() {
		return fakeId;
	}
	
	public void setFakeId(String fakeId) {
		this.fakeId = fakeId;
	}
	
	public String getSpotInfo() {
		String separator = Util.SEPARATOR;
		String output = "";
		output += getFakeId() + separator;
		output += getId() + separator;
		output += getName() + separator;
		output += getPassword() + separator;
		output += getIp() + separator;
		output += getAddress();
		return output;
	}
	
	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		values.put( SpotFields.fakeId.getText(), this.getFakeId() );
		values.put( SpotFields.address.getText(), this.getAddress() );
		values.put( SpotFields.id.getText(), this.getId() );
		values.put( SpotFields.ip.getText(), this.getIp() );
		try {
			if (this.getIsActive()) {
				values.put( SpotFields.isActive.getText(), "true" );
			} else {
				values.put( SpotFields.isActive.getText(), "false" );
			}
			values.put( SpotFields.latitude.getText(), this.getLatitude() );
			values.put( SpotFields.longitude.getText(), this.getLongitude() );
		} catch (Exception e) {
			values.put( SpotFields.isActive.getText(), "true" );
			values.put( SpotFields.latitude.getText(), 42.6954322 );
			values.put( SpotFields.longitude.getText(), 23.3239467 );
		}
		values.put( SpotFields.name.getText(), this.getName() );
		values.put( SpotFields.password.getText(), this.getPassword() );
		return values;
	}
}
