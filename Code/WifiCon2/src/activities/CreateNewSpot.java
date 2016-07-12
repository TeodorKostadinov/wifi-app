package activities;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import seo.extra.wifi_analyzor.R;
import util.Util;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import comm.savagelook.android.UrlJsonAsyncTask;

public class CreateNewSpot extends Activity {
	private String CREATE_NEW_WIFI_URL;
	
	private String getName() {
		return this.name;
	}
	
	private String getIp() {
		return this.ip;
	}
	
	private String getPassword() {
		return this.password;
	}
	
	private void setName(String name) {
		this.name = name;
	}
	
	private void setIp(String ip) {
		this.ip = ip;
	}
	
	private void setPassword(String password) {
		this.password = password;
	}
	
	private GoogleMap googleMap;
	private EditText addressView;
	private LatLng latLng;
	private MarkerOptions markerOptions;
	
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate( savedInstanceState );
			setContentView( R.layout.create );
			getActionBar().setIcon(
					new ColorDrawable( getResources().getColor(
							android.R.color.transparent ) ) );
			setLayoutStrings();
			CREATE_NEW_WIFI_URL = Util
					.getServerAddress( getApplicationContext() )
					+ "add_spot.json";
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map_new )).getMap();
			addressView = (EditText) findViewById( R.id.new_address_new );
			if (googleMap != null) {
				
				googleMap.setOnMapClickListener( new OnMapClickListener() {
					
					@Override
					public void onMapClick(LatLng arg0) {
						if (Util.isWifiEnabled( getApplicationContext() )
								|| Util.isInternetAvailable( getApplicationContext() )) {
							
							// Getting the Latitude and Longitude of the touched
							// location
							latLng = arg0;
							
							// Clears the previously touched position
							googleMap.clear();
							
							// Animating to the touched position
							googleMap.animateCamera( CameraUpdateFactory
									.newLatLng( latLng ) );
							
							// Creating a marker
							markerOptions = new MarkerOptions();
							
							// Setting the position for the marker
							markerOptions.position( latLng );
							
							// Placing a marker on the touched position
							googleMap.addMarker( markerOptions );
							CameraUpdate cu = CameraUpdateFactory
									.newLatLngZoom( latLng, 12 );
							googleMap.animateCamera( cu );
							// Adding Marker on the touched location with
							// address
							
							ReverseGeocodingTask task = new ReverseGeocodingTask(
									getApplicationContext() );
							task.execute( latLng );
							
						} else {
							Toast.makeText( getApplicationContext(),
									Util.getNoInternet(), Toast.LENGTH_LONG )
									.show();
						}
						
					}
				} );
			} else {
				Toast.makeText( getApplicationContext(),
						Util.getProblemLoadingMap(), Toast.LENGTH_LONG ).show();
			}
			((EditText) findViewById( R.id.new_ip_new )).setText( Util
					.getIp( getApplicationContext() ) );
			markerOptions = new MarkerOptions();
			ReverseGeocodingTask task = new ReverseGeocodingTask(
					getApplicationContext() );
			latLng = Util.getCurrentLocation( getApplicationContext() );
			if (latLng.latitude == 0 && latLng.longitude == 0) {
				
				latLng = Util.getSofiaLatLog();
			}
			markerOptions.position( latLng );
			
			// Placing a marker on the touched position
			googleMap.addMarker( markerOptions );
			CameraUpdate cu = CameraUpdateFactory.newLatLngZoom( latLng, 12 );
			googleMap.animateCamera( cu );
			task.execute( latLng );
		} catch (Exception e) {
			Util.addException( this.getClass(), e, getBaseContext() );
		}
	}
	
	private void setLayoutStrings() {
		setTitle( Util.getCreateNewSpotTitle() );
		EditText name = (EditText) findViewById( R.id.new_name_new );
		name.setHint( Util.getName() );
		EditText password = (EditText) findViewById( R.id.new_pass_new );
		password.setHint( Util.getPassword() );
		EditText address = (EditText) findViewById( R.id.new_address_new );
		address.setHint( Util.getAddress() );
		EditText ip = (EditText) findViewById( R.id.new_ip_new );
		ip.setHint( Util.getIpString() );
		Button cancel = (Button) findViewById( R.id.dismiss_button_new ); // TODO:
																			// make
																			// bg
																			// version
																			// shorter
		cancel.setText( Util.getCancel() );
		Button confirm = (Button) findViewById( R.id.confirm_button_new );
		confirm.setText( Util.getConfirm() );
	}
	
	private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String> {
		Context context;
		
		public ReverseGeocodingTask(Context context) {
			super();
			this.context = context;
		}
		
		// Finding address using reverse geocoding
		@Override
		protected String doInBackground(LatLng... params) {
			double latitude = params[0].latitude;
			double longitude = params[0].longitude;
			List<Address> addresses = null;
			String addressText = "";
			
			addresses = getFromLocation( latitude, longitude, 1, context );
			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get( 0 );
				
				addressText = address.getAddressLine( 0 );
				
			}
			return addressText;
		}
		
		@Override
		protected void onPostExecute(String addressText) {
			// Setting the title for the marker.
			// This will be displayed on taping the marker
			markerOptions.title( addressText );
			markerOptions.snippet( "Position:" + latLng.latitude + ", "
					+ latLng.longitude );
			try {
				addressView.setText( addressText.substring( 0, 1 ).toUpperCase(
						Locale.getDefault() )
						+ addressText.substring( 1 ) );
			} catch (StringIndexOutOfBoundsException e) {
				addressView.setText( "" );
			}
			
			// Placing a marker on the touched position
			googleMap.addMarker( markerOptions );
			
		}
	}
	
	public static List<Address> getFromLocation(double lat, double lng,
			int maxResult, Context context) {
		
		String address = String
				.format(
						Locale.ENGLISH,
						"http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="
								+ Locale.getDefault().getCountry(), lat, lng );
		HttpGet httpGet = new HttpGet( address );
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();
		
		List<Address> retList = null;
		
		try {
			response = client.execute( httpGet );
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append( (char) b );
			}
			
			JSONObject jsonObject = new JSONObject();
			jsonObject = new JSONObject( stringBuilder.toString() );
			
			retList = new ArrayList<Address>();
			if ("OK".equalsIgnoreCase( jsonObject.getString( "status" ) )) {
				JSONArray results = jsonObject.getJSONArray( "results" );
				
				for (int i = 0; i < results.length(); i++) {
					JSONObject result = results.getJSONObject( i );
					String indiStr = result.getString( "formatted_address" );
					Address addr = new Address( Locale.getDefault() );
					addr.setAddressLine( 0, indiStr );
					retList.add( addr );
				}
			}
			
		} catch (ClientProtocolException e) {
			Log.e( CreateNewSpot.class.getName(),
					"Error calling Google geocode webservice.", e );
			Util.addException( CreateNewSpot.class, e, context );
		} catch (IOException e) {
			Log.e( CreateNewSpot.class.getName(),
					"Error calling Google geocode webservice.", e );
			Util.addException( CreateNewSpot.class, e, context );
		} catch (JSONException e) {
			Log.e( CreateNewSpot.class.getName(),
					"Error parsing Google geocode webservice response.", e );
			Util.addException( CreateNewSpot.class, e, context );
		}
		
		return retList;
	}
	
	private String name;
	private String ip;
	private String password;
	private String address;
	
	public void confirmCreating(View v) {
		
		if (getSpotInfo()) {
			CreateSpot createSpot = new CreateSpot( CreateNewSpot.this,
					Util.getNoInternettoSendNew() );
			createSpot.setMessageLoading( Util.getCreatingNewSpot() );
			createSpot.execute( CREATE_NEW_WIFI_URL + "?auth_token="
					+ Util.getUserInfo().getString( "AuthToken", "" ) );
		}
		
	}
	
	private Boolean getSpotInfo() {
		setName( ((EditText) findViewById( R.id.new_name_new )).getText()
				.toString() );
		setIp( ((EditText) findViewById( R.id.new_ip_new )).getText()
				.toString() );
		
		setAddress( ((EditText) findViewById( R.id.new_address_new )).getText()
				.toString() );
		
		setPassword( ((EditText) findViewById( R.id.new_pass_new )).getText()
				.toString() );
		
		if (getName().length() == 0) {
			Toast.makeText( getApplicationContext(), Util.getEmptyName(),
					Toast.LENGTH_LONG ).show();
			return false;
		} else if (getIp().length() == 0) {
			Toast.makeText( getApplicationContext(), Util.getEmptyIp(),
					Toast.LENGTH_LONG ).show();
			return false;
		} else if (getIp().split( "\\." ).length != 4) {
			Toast.makeText( getApplicationContext(),
					Util.getEnteredIpInvalid(), Toast.LENGTH_LONG ).show();
			Toast.makeText( getApplicationContext(), Util.getValidIpFormat(),
					Toast.LENGTH_LONG ).show();
			return false;
		} else if (getAddress().length() == 0) {
			Toast.makeText( getApplicationContext(), Util.getAddressEmpty(),
					Toast.LENGTH_LONG ).show();
			return false;
		} else if (latLng == null
				|| (latLng.latitude == 0 || latLng.longitude == 0)) {
			Toast.makeText( getApplicationContext(),
					Util.getNoSpecifiedCoordinates(), Toast.LENGTH_LONG )
					.show();
			return false;
		}
		return true;
	}
	
	@Override
	protected void onResume() {
		invalidateOptionsMenu();
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate( Util.getMenuId(), menu );
		
		MenuItem loggedIn = menu.getItem( 0 );
		String title = loggedIn.getTitle().toString();
		title += " "
				+ Util.getUserInfo().getString( "Username",
						"Error in loading user info" ) + " Lvl: "
				+ Util.getUserInfo().getString( "UserLevel", "0" );
		loggedIn.setTitle( title );
		return super.onCreateOptionsMenu( menu );
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.acc_sett:
				Util.changeScreen( "ACCOUNT_SETTINGS", getApplicationContext() );
				break;
			case R.id.take_tsk:
				Util.changeScreen( "GET_TASK", getApplicationContext() );
				break;
			case R.id.targ_list:
				Util.changeScreen( "TARGET_LIST_TABS", getApplicationContext() );
				break;
			case R.id.logout:
				Util.logout( CreateNewSpot.this, true );
				break;
			default:
				break;
		}
		return true;
	}
	
	private String getAddress() {
		return this.address;
	}
	
	private void setAddress(String address) {
		this.address = address;
	}
	
	public void cancelCreating(View v) {
		// /changeScreen("MANAGE_NETWORKS");
		finish();
	}
	
	private class CreateSpot extends UrlJsonAsyncTask {
		
		public CreateSpot(Context context, String errorMessage) {
			super( context, errorMessage );
		}
		
		@Override
		protected JSONObject doInBackground(String... urls) {
			if (willExecuteJson) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost( urls[0] );
				JSONObject holder = new JSONObject();
				String response = null;
				JSONObject json = new JSONObject();
				// final String FAILURE_URL = "http://10.0.2.2:3000/fail.json";
				try {
					try {
						
						// setup the returned values in case
						// something goes wrong
						json.put( "success", false );
						json.put( "info", Util.getErrorMessage() );
						holder.put( "uip", Util.getIp( CreateNewSpot.this ) );
						
						holder.put( "name", getName() );
						
						holder.put( "ip", getIp() );
						holder.put( "language", Util.getCurrentLanguadge() );
						
						holder.put( "password", getPassword() + "" );
						try {
							holder.put(
									"address",
									getAddress().substring( 0, 1 ).toLowerCase(
											Locale.getDefault() )
											+ getAddress().substring( 1 ) );
						} catch (Exception e) {
							holder.put( "address", "Not specified" );
						}
						
						// TO DO
						holder.put( "lati", latLng.latitude );
						holder.put( "longi", latLng.longitude );
						
						StringEntity se = new StringEntity( holder.toString() );
						post.setEntity( se );
						
						// setup the request headers
						post.setHeader( "Accept", "application/json" );
						post.setHeader( "Content-Type", "application/json" );
						
						ResponseHandler<String> responseHandler = new BasicResponseHandler();
						response = client.execute( post, responseHandler );
						json = new JSONObject( response );
						
					} catch (HttpResponseException e) {
						e.printStackTrace();
						Util.addException( CreateNewSpot.class, e, context );
						// FailureResponse failure = new
						// FailureResponse(LogIn.this);
						// failure.execute(FAILURE_URL);
						json.put( "info", Util.getInvalidSpotCredentials() );
					} catch (IOException e) {
						Util.addException( CreateNewSpot.class, e, context );
						Log.e( "IO", "" + e );
					}
				} catch (JSONException e) {
					Util.addException( CreateNewSpot.class, e, context );
					Log.e( "JSON", "" + e );
				}
				
				return json;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			if (willExecuteJson)
				try {
					if (json.getBoolean( "success" )) {
						Toast.makeText( context, json.getString( "info" ),
								Toast.LENGTH_LONG ).show();
						finish();
					} else {
						if (json.getString( "info" ).equals(
								Util.getTokenExpired() )) {
							Util.logout( CreateNewSpot.this, false );
						} else {
							Toast.makeText( context, json.getString( "info" ),
									Toast.LENGTH_LONG ).show();
						}
					}
					
				} catch (Exception e) {
					// something went wrong: show a Toast
					// with the exception message
					Toast.makeText( context, e.getMessage(), Toast.LENGTH_LONG )
							.show();
					Util.addException( CreateNewSpot.class, e, context );
				} finally {
					super.onPostExecute( json );
				}
		}
	}
}
