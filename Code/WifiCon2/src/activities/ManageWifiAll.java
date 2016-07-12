package activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import seo.extra.wifi_analyzor.R;
import util.Util;
import util.WifiSpot;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import async.tasks.ChangeWifiInfo;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import comm.savagelook.android.UrlJsonAsyncTask;

public class ManageWifiAll extends Activity {
	private String GET_ALL_WIFI_URL;
	private GoogleMap map;
	private List<WifiSpot> spots;
	
	private Marker userPosition;
	private HashMap<Marker, WifiSpot> markerList;
	private byte clickedTimes;
	private Marker lastClickedMarker;
	private Dialog popupView;
	ArrayList<LatLng> markerPoints;
	private static Boolean isFocusOnSpot;
	private static String spotName;
	private static String spotPassword;
	private static String spotAddress;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			markerList = new HashMap<Marker, WifiSpot>();
			activeMarkers = new ArrayList<Marker>();
			try {
				if (!isFocusOnSpot) {// checks if there is a request for one
										// stop if
										// there is there will be no exception
										// and
										// the data will be set
				}
			} catch (Exception e) {
				// Shows all spots on map, since there was no request, this just
				// sets the default values;
				isFocusOnSpot = false;
			}
			super.onCreate( savedInstanceState );
			
			setContentView( R.layout.google_maps );
			EditText searchText = (EditText) findViewById( R.id.map_search_edittext );
			searchText.setHint( Util.getName() );
			// ImageButton searchInput = (ImageButton) findViewById(
			// R.id.map_search_button );
			// // searchInput.setText( Util.getSearchStr() );
			// searchInput.setText( "" );
			// searchInput
			// .setBackgroundResource(
			// android.R.drawable.ic_search_category_default );
			GET_ALL_WIFI_URL = Util.getServerAddress( getApplicationContext() )
					+ "all_spots.json";
			Build.FINGERPRINT.startsWith( "generic" );
			map = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map )).getMap();
			if (map != null) {
				map.setMyLocationEnabled( true );
				
				map.setOnInfoWindowClickListener( new OnInfoWindowClickListener() {
					
					@SuppressWarnings("deprecation")
					@Override
					public void onInfoWindowClick(Marker currentMarker) {
						if (lastClickedMarker == null) {
							clickedTimes++;
							lastClickedMarker = currentMarker;
						} else {
							if (lastClickedMarker.getId().equals(
									currentMarker.getId() )) {
								clickedTimes++;
								if (clickedTimes >= 2) {
									
									// pw.setAnimationStyle(android.R.style.Animation_Dialog);
									popupView = new Dialog( ManageWifiAll.this,
											R.style.PauseDialog );
									
									popupView
											.setContentView( R.layout.popup_window );
									popupView.setTitle( Util.getEditingPopup() );
									// Util.getEditingPopup()
									ImageButton confirm = (ImageButton) (popupView
											.findViewById( R.id.confirm_button_popup ));
									// confirm.setText( "" );
									// confirm.setBackgroundResource(
									// R.drawable.check_mark );
									confirm.setOnClickListener( new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											if (Util.hasInternet( getBaseContext() )) {
												onButtonInPopupConfirm( v,
														lastClickedMarker );
											} else {
												Toast.makeText(
														getBaseContext(),
														Util.getNoInternet(),
														Toast.LENGTH_LONG )
														.show();
												popupView.dismiss();
											}
											
										}
									} );
									ImageButton dismiss = (ImageButton) (popupView
											.findViewById( R.id.dismiss_button_popup ));
									// dismiss.setText( "" );
									// dismiss.setBackgroundResource(
									// android.R.drawable.ic_delete );
									dismiss.setOnClickListener( new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											onButtonInPopupCancel( v );
											
										}
									} );
									
									TextView nameText = (TextView) (popupView
											.findViewById( R.id.name_popup ));
									nameText.setText( Util.getName() );
									TextView passText = (TextView) (popupView
											.findViewById( R.id.password_popup ));
									passText.setText( Util.getPassword() );
									EditText name = (EditText) (popupView
											.findViewById( R.id.new_name_pop ));
									name.setHint( Util.getName() );
									EditText IP = (EditText) (popupView
											.findViewById( R.id.new_id_pop ));
									IP.setHint( Util.getIpString() );
									EditText pass = (EditText) (popupView
											.findViewById( R.id.new_pass_pop ));
									pass.setHint( Util.getPassword() );
									TextView idNum = (TextView) popupView
											.findViewById( R.id.id_pop );
									CheckBox checkbox = ((CheckBox) popupView
											.findViewById( R.id.new_spot_active_pop ));
									checkbox.setText( Util.getIsSpotActive() );
									WifiSpot spot = markerList
											.get( currentMarker );
									// WifiSpot spot = spots.get(
									// positionInArray );
									checkbox.setChecked( spot.getIsActive() );
									idNum.setText( "Id: " + spot.getFakeId() );
									name.setText( spot.getName() );
									pass.setText( spot.getPassword().replace(
											"_", "_" ) );
									IP.setText( spot.getIp() );
									popupView.getWindow().setLayout(
											LayoutParams.FILL_PARENT,
											LayoutParams.WRAP_CONTENT );
									popupView.show();
								}
							} else {
								clickedTimes = 1;
								lastClickedMarker = currentMarker;
							}
						}
					}
				} );
				
				placeUserMarker();
				
				getAllWifiSpots();
			} else {
				Toast.makeText( getApplicationContext(),
						Util.getProblemLoadingMap(), Toast.LENGTH_LONG ).show();
			}
			ImageButton searchButton = (ImageButton) findViewById( R.id.map_search_button );
			searchButton.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					EditText searchInput = (EditText) findViewById( R.id.map_search_edittext );
					String name = searchInput.getText().toString();
					searchForSpot( name );
				}
			} );
			
			ImageButton cancelButton = (ImageButton) findViewById( R.id.map_cancel_search );
			cancelButton.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					((EditText) findViewById( R.id.map_search_edittext ))
							.setText( "" );
					placeUserMarker();
					getAllWifiSpots();
					
				}
			} );
			
			ImageButton addNewButton = (ImageButton) findViewById( R.id.map_create_new );
			// addNewButton.setText( Util.getCreateNew() );
			// addNewButton.setText( "" );
			// addNewButton
			// .setBackgroundResource( android.R.drawable.ic_input_add );
			// addNewButton.setX(50);
			addNewButton.setOnClickListener( new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (Util.isWifiEnabled( getApplicationContext() )) {
						Util.changeScreen( "CREATE", getApplicationContext() );
					} else {
						Toast.makeText( getApplicationContext(),
								Util.getConntectToWifi(), Toast.LENGTH_LONG )
								.show();
					}
				}
			} );
		} catch (Exception e) {
			Util.addException( this.getClass(), e, getBaseContext() );
		}
	}
	
	private void placeUserMarker() {
		LatLng userLocation = getMyLocationAndFollowIt();
		String userName = Util.getUserInfo().getString( "Username", "You" );
		userPosition = map.addMarker( new MarkerOptions()
				.position( userLocation ) );
		userPosition.setTitle( userName );
		userPosition.setIcon( BitmapDescriptorFactory
				.fromResource( R.drawable.ic_launcher ) );
	}
	
	private List<Marker> activeMarkers;
	
	public void onButtonInPopupCancel(View target) {
		popupView.dismiss();
	}
	
	public void onButtonInPopupConfirm(View target, Marker marker) {
		
		wifiUpdate( marker );
		
	}
	
	private void wifiUpdate(Marker marker) {
		EditText nameEdit = (EditText) popupView
				.findViewById( R.id.new_name_pop );
		EditText IPEdit = (EditText) popupView.findViewById( R.id.new_id_pop );
		EditText passEdit = (EditText) popupView
				.findViewById( R.id.new_pass_pop );
		CheckBox activeCheck = (CheckBox) popupView
				.findViewById( R.id.new_spot_active_pop );
		String name = nameEdit.getText().toString();
		String IP = IPEdit.getText().toString();
		String pass = passEdit.getText().toString();
		Boolean active = activeCheck.isChecked();
		WifiSpot spot = markerList.get( marker );
		spot.setName( name );
		spot.setIp( IP );
		spot.setPassword( pass );
		spot.setIsActive( active );
		
		ChangeWifiInfo updateInfo = new ChangeWifiInfo( ManageWifiAll.this,
				Util.getNoInternetToUpdate(), spot );
		updateInfo.setMessageLoading( Util.getUpdatingInformation() );
		String UPDATE_WIFI_URL = Util
				.getServerAddress( getApplicationContext() )
				+ "update_spot.json";
		updateInfo.execute( UPDATE_WIFI_URL + "?auth_token="
				+ Util.getUserInfo().getString( "AuthToken", "" ) );
		
		LatLng position = marker.getPosition();
		marker.remove();
		addMarkers( position, spot, false );
		
		popupView.dismiss();
		
	}
	
	private void getAllWifiSpots() {
		
		GetAllSpots allSpots = new GetAllSpots( ManageWifiAll.this,
				Util.getPleaseConnectToInternetForSpots() );
		allSpots.setMessageLoading( Util.getGettingWiFiSpots() );
		allSpots.execute( GET_ALL_WIFI_URL + "?auth_token="
				+ Util.getUserInfo().getString( "AuthToken", "" ) );
	}
	
	private void centerMapOnMarkers() {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		int markersCount = activeMarkers.size();
		if (markersCount > 0) {
			for (Marker marker : activeMarkers) {
				builder.include( marker.getPosition() );
			}
			if (userPosition.getPosition().latitude == 0
					&& userPosition.getPosition().longitude == 0) {
				builder.include( Util.getSofiaLatLog() );
			}
			LatLngBounds bounds = builder.build();
			int padding = 100; // offset from edges of the map in pixels
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds( bounds,
					padding );
			if (map != null) {
				map.animateCamera( cu );
			}
		}
	}
	
	private void generateMarkers() {
		map.clear();
		activeMarkers.clear();
		markerPoints = new ArrayList<LatLng>();
		int spotsLenght = spots.size();
		WifiSpot currentSpot;
		if (spots != null && spotsLenght != 0) {
			for (int i = 0; i < spotsLenght; i++) {
				currentSpot = spots.get( i );
				if (isFocusOnSpot) {
					
					if (currentSpot.getName().equals( spotName )) {
						if (currentSpot.getPassword().replaceAll( "_", "_" )
								.equals( spotPassword )) {
							if (currentSpot.getAddress().equals( spotAddress )) {
								addMarkerInfo( currentSpot, false );
								isFocusOnSpot = false;
								break;
							}
						}
					}
				} else {
					addMarkerInfo( currentSpot, false );
				}
			}
			centerMapOnMarkers();
		} else {
			Toast.makeText( getApplicationContext(), Util.getNoSpotsFound(),
					Toast.LENGTH_LONG ).show();
			Toast.makeText( getApplicationContext(), Util.getNoInternet(),
					Toast.LENGTH_LONG ).show();
			LatLng user = userPosition.getPosition();
			if (user.latitude == 0 && user.longitude == 0) {
				user = Util.getSofiaLatLog();
			}
			CameraUpdate cu = CameraUpdateFactory.newLatLngZoom( user, 12 );
			map.animateCamera( cu );
		}
	}
	
	private void addMarkerInfo(WifiSpot currentSpot, Boolean forSearch) {
		if (Util.tryParseDouble( currentSpot.getLatitude() )
				&& Util.tryParseDouble( currentSpot.getLongitude() )) {
			double lat = Double.parseDouble( currentSpot.getLatitude() );
			double longi = Double.parseDouble( currentSpot.getLongitude() );
			LatLng coordinates = new LatLng( lat, longi );
			addMarkers( coordinates, currentSpot, forSearch );
			
		}
	}
	
	private void addMarkers(LatLng position, WifiSpot spot, Boolean forSearch) {
		String name = spot.getName();
		String password = spot.getPassword();
		Boolean isActive = spot.getIsActive();
		Marker marker = null;
		
		if (isActive) {
			marker = map
					.addMarker( new MarkerOptions()
							.position( position )
							.title( Util.getName() + ": " + name )
							.snippet( Util.getPassword() + ": " + password )
							.icon( BitmapDescriptorFactory
									.defaultMarker( BitmapDescriptorFactory.HUE_GREEN ) ) );
			activeMarkers.add( marker );
		} else {
			marker = map.addMarker( new MarkerOptions().position( position )
					.title( Util.getName() + ": " + name )
					.snippet( Util.getPassword() + ": " + password ) );
			if (forSearch) {
				activeMarkers.add( marker );
			}
		}
		
		markerList.put( marker, spot );
	}
	
	LatLng getMyLocationAndFollowIt() {
		LocationManager lm = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		lm.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 50,
				new LocationListener() {
					
					@Override
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
					}
					
					@Override
					public void onProviderEnabled(String provider) {
					}
					
					@Override
					public void onProviderDisabled(String provider) {
					}
					
					@Override
					public void onLocationChanged(Location location) {
						getMyLocationAndFollowIt();
						// userPosition.remove();
						// if (userLocation.latitude == 0
						// && userLocation.longitude == 0) {
						// userLocation = Util.getSofiaLatLog();
						// }
						// userPosition = map.addMarker( new MarkerOptions()
						// .position( userLocation ) );
						// String userName = Util.getUserInfo().getString(
						// "Username", "You" );
						// userPosition.setTitle( userName );
						// userPosition.setIcon( BitmapDescriptorFactory
						// .fromResource( R.drawable.ic_launcher ) );
						
					}
				} );
		LatLng gps = new LatLng( 0, 0 );
		try {
			gps = Util.getCurrentLocation( getApplicationContext() );
		} catch (Exception ex) {
		}
		return gps;
	}
	
	private class GetAllSpots extends UrlJsonAsyncTask {
		
		public GetAllSpots(Context context, String errorMessage) {
			
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
						holder.put( "ip", Util.getIp( ManageWifiAll.this ) );
						holder.put( "language", Util.getCurrentLanguadge() );
						// add the user email and password to
						// the params
						StringEntity se = new StringEntity( holder.toString() );
						post.setEntity( se );
						
						// setup the request headers
						post.setHeader( "Accept", "application/json" );
						post.setHeader( "Content-Type", "application/json" );
						
						ResponseHandler<String> responseHandler = new BasicResponseHandler();
						response = client.execute( post, responseHandler );
						json = new JSONObject( response );
						
					} catch (HttpResponseException e) {
						Util.addException( this.getClass(), e, context );
						Looper.prepare();
						// FailureResponse failure = new
						// FailureResponse(LogIn.this);
						// failure.execute(FAILURE_URL);
						json.put( "info", Util.getInvalidCredentials() );
					} catch (IOException e) {
						Util.addException( this.getClass(), e, context );
					}
				} catch (JSONException e) {
					Util.addException( this.getClass(), e, context );
				}
				
				return json;
			} else {
				spots = Util.datasource.findAllWifiSpots();
				if (spots.size() == 0) {
					Toast.makeText( context, Util.getNoInternet(),
							Toast.LENGTH_LONG ).show();
				} else {
					Handler handler = new Handler( Looper.getMainLooper() );
					handler.post( new Runnable() {
						
						@Override
						public void run() {
							generateMarkers();
							
						}
					} );
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			if (willExecuteJson) {
				try {
					String response = json.getString( "info" );
					if (json.getBoolean( "success" )) {
						
						JSONArray jsonTasks = json.getJSONArray( "data" );
						int length = jsonTasks.length();
						spots = new ArrayList<WifiSpot>();
						for (int i = 0; i < length; i++) {
							WifiSpot spot = new WifiSpot(
									jsonTasks.getJSONObject( i ),
									((Integer) (i + 1)), true, context );
							spots.add( spot );
						}
						try {
							writeSpots();
						} catch (Exception e) {
							Util.addException( this.getClass(), e, context );
						}
					} else {
						if (response.equals( Util.getTokenExpired() )) {
							Util.logout( ManageWifiAll.this, false );
						}
					}
					Toast.makeText( context, response, Toast.LENGTH_LONG )
							.show();
				} catch (Exception e) {
					Toast.makeText( context, e.getMessage(), Toast.LENGTH_LONG )
							.show();
					Util.addException( this.getClass(), e, context );
				} finally {
					generateMarkers();
					super.onPostExecute( json );
				}
			}
		}
	}
	
	private void writeSpots() throws InterruptedException {
		final int spotsSize = spots.size();
		Util.datasource.clearTable( "spots" );
		Thread writeTasksThread = new Thread( new Runnable() {
			
			@Override
			public void run() {
				
				for (int i = 0; i < spotsSize; i++) {
					final WifiSpot spot = spots.get( i );
					
					Util.datasource.createWifiSpot( spot );
				}
			}
		} );
		writeTasksThread.start();
	}
	
	private void searchForSpot(String name) {
		WifiSpot spot;
		List<WifiSpot> foundResults = new ArrayList<WifiSpot>();
		for (int i = 0; i < spots.size(); i++) {
			spot = spots.get( i );
			if (spot.getName().toLowerCase().contains( name.toLowerCase() )) {
				foundResults.add( spot );
			}
		}
		displaySearchResults( foundResults );
	}
	
	private void displaySearchResults(List<WifiSpot> spotList) {
		map.clear();
		activeMarkers.clear();
		int spotListCount = spotList.size();
		for (int i = 0; i < spotListCount; i++) {
			WifiSpot currentSpot = spotList.get( i );
			addMarkerInfo( currentSpot, true );
			
		}
		centerMapOnMarkers();
		Toast.makeText( getApplicationContext(),
				"Spots found: " + spotListCount, Toast.LENGTH_LONG ).show();
		placeUserMarker();
	}
	
	public static void showOneSpot(HashMap<String, String> content) {
		isFocusOnSpot = true;
		spotName = content.get( "name" );
		spotPassword = content.get( "password" );
		spotAddress = content.get( "address" );
	}
}
