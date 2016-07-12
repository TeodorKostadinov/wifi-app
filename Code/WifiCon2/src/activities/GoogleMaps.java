package activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import seo.extra.wifi_analyzor.R;
import util.DirectionsJSONParser;
import util.Task;
import util.Util;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class GoogleMaps extends Activity {
	private GoogleMap map;
	private List<Task> tasks;
	private Marker markerForPosition;
	private List<Marker> markerList;
	ArrayList<LatLng> markerPoints;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			markerList = new ArrayList<Marker>();
			
			super.onCreate( savedInstanceState );
			// getActionBar().setTitle("");
			// getActionBar().setIcosn(
			// new ColorDrawable(getResources().getColor(
			// android.R.color.transparent)));
			setContentView( R.layout.google_maps );
			findViewById( R.id.map_search_button ).setVisibility( View.GONE );
			findViewById( R.id.map_search_edittext ).setVisibility( View.GONE );
			findViewById( R.id.map_create_new ).setVisibility( View.GONE );
			findViewById( R.id.map_cancel_search ).setVisibility( View.GONE );
			// Build.FINGERPRINT.startsWith("generic");
			setTitle( Util.getGoogleMapsTitle() );
			map = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map )).getMap();
			if (map != null) {
				map.setMyLocationEnabled( true );
				
				LatLng userLocation = getMyLocationAndFollowIt();
				markerForPosition = map.addMarker( new MarkerOptions()
						.position( userLocation ) );
				String userName = Util.getUserInfo().getString( "Username",
						"You" );
				markerForPosition.setTitle( userName );
				markerForPosition.setIcon( BitmapDescriptorFactory
						.fromResource( R.drawable.ic_launcher ) );
				tasks = Util.datasource.findAllTasks();
				View activityLayout = getWindow().getDecorView().findViewById(
						android.R.id.content );
				activityLayout.getViewTreeObserver().addOnGlobalLayoutListener(
						new OnGlobalLayoutListener() {
							
							@Override
							public void onGlobalLayout() {
								centerMapOnMarkers();
							}
						} );
				List<Task> activeTasks = new ArrayList<Task>();
				for (Task task2 : tasks) {
					if (!task2.getStatus().equals( "Missed" )) {
						activeTasks.add( task2 );
					}
				}
				if (Util.isWifiEnabled( getApplicationContext() )
						|| Util.isInternetAvailable( getApplicationContext() )) {
					
					markerPoints = new ArrayList<LatLng>();
					if (tasks != null && tasks.size() != 0) {
						
						Task task;
						
						Task taskTo;
						Boolean successfulParseCoordinates = generateMarkers( activeTasks );
						if (activeTasks.size() == 1) {
							task = activeTasks.get( 0 );
							generateNextWaypoint( task, userLocation );
						} else {
							
							if (successfulParseCoordinates) {
								Boolean nextWaypointSet = false;
								int markerNum = 0;
								for (int i = 0; i < activeTasks.size(); i++) {
									if (i + 1 >= activeTasks.size()) {
										break;
									}
									task = activeTasks.get( i );
									taskTo = activeTasks.get( i + 1 );
									
									Marker marker = markerList.get( markerNum );
									Boolean finishedTo;
									Boolean finishedFrom;
									
									if (task.getStatus().equals( "Finished" )) {
										finishedTo = true;
									} else {
										finishedTo = false;
									}
									if (taskTo.getStatus().equals( "Finished" )) {
										finishedFrom = true;
									} else {
										finishedFrom = false;
									}
									generateRoadDirections( marker,
											markerList.get( markerNum++ + 1 ),
											finishedTo, finishedFrom );
									
									if (!task.getStatus().equals( "Finished" )
											&& !nextWaypointSet) {
										nextWaypointSet = true;// user +
																// next
																// spot
										generateNextWaypoint( task,
												userLocation );
									}
								}
								
							} else {
								Toast.makeText( getApplicationContext(),
										"Error loading marker location!",
										Toast.LENGTH_LONG ).show();
								CameraUpdate cu = CameraUpdateFactory
										.newLatLngZoom( userLocation, 12 );
								map.animateCamera( cu );
							}
						}
						
					} else {
						Toast.makeText( getApplicationContext(),
								Util.getNoTasksFound(), Toast.LENGTH_LONG )
								.show();
					}
				} else {
					Toast.makeText( getApplicationContext(),
							Util.getNoInternet(), Toast.LENGTH_LONG ).show();
					Boolean successfulParseCoordinates = generateMarkers( activeTasks );
					if (!successfulParseCoordinates) {
						Toast.makeText( getApplicationContext(),
								Util.getErrorLoadingMarkerLocation(),
								Toast.LENGTH_LONG ).show();
					}
					CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(
							userLocation, 12 );
					map.animateCamera( cu );
				}
			} else {
				Toast.makeText( getApplicationContext(),
						Util.getProblemLoadingMap(), Toast.LENGTH_LONG ).show();
			}
		} catch (Exception e) {
			Util.addException( this.getClass(), e, getBaseContext() );
		}
	}
	
	private void generateRoadDirections(Marker taskFrom, Marker taskTo,
			Boolean isFinishedTo, Boolean isFinishedFrom) {
		
		// Checks if the coordinates are valid
		
		LatLng fromPosition = taskFrom.getPosition();
		LatLng toPosition = taskTo.getPosition();
		
		// Getting URL to the Google Directions API
		String url = getDirectionsUrl( fromPosition, toPosition );
		int color = Color.BLACK; // for directions line
		// Checks and sets the current status color
		// acording
		// to it's state
		// Check for the line between two points, if
		// both
		// are finished the line is green
		if (isFinishedFrom && isFinishedTo) {
			color = Color.GREEN;
			
		} else {
			color = Color.RED;
			
		}
		DownloadTask downloadTask = new DownloadTask( color, getBaseContext() );
		
		// Start downloading json data from Google
		// Directions
		// API
		downloadTask.execute( url );
		
	}
	
	private void generateNextWaypoint(Task task, LatLng userLocation) {
		String longi1 = task.getLongi();
		String lati1 = task.getLati();
		LatLng toPosition = new LatLng( Double.parseDouble( lati1 ),
				Double.parseDouble( longi1 ) );
		String url = getDirectionsUrl( userLocation, toPosition );
		int color = Color.BLUE;
		
		DownloadTask downloadTask = new DownloadTask( color, getBaseContext() );
		
		downloadTask.execute( url );
	}
	
	private void centerMapOnMarkers() {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		if (markerList.size() > 0) {
			for (Marker marker : markerList) {
				builder.include( marker.getPosition() );
			}
			if (markerForPosition.getPosition().latitude == 0
					&& markerForPosition.getPosition().longitude == 0) {
				builder.include( Util.getSofiaLatLog() );
			} else {
				builder.include( markerForPosition.getPosition() );
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
	
	private Boolean generateMarkers(List<Task> activeTasks) {
		try {
			// for (int i = 0; i < tasks.length; i++) {
			for (int i = 0; i < activeTasks.size(); i++) {
				Task task = null;
				task = activeTasks.get( i );
				if ((task == null)) {
					Log.i( "Error in tasks ", "Error loading task with id " + i );
					break;
				}
				String longi1 = task.getLongi();
				String lati1 = task.getLati();
				
				if (Util.tryParseDouble( lati1 )
						&& Util.tryParseDouble( longi1 )) {
					LatLng markerLocation = new LatLng(
							Double.parseDouble( lati1 ),
							Double.parseDouble( longi1 ) );
					if (task.getStatus().equals( "Finished" )) {
						addMarkers( markerLocation, task.getName(),
								task.getName(), true );
					} else {
						addMarkers( markerLocation, task.getName(),
								task.getName(), false );
					}
				} else {
					return false;
				}
				
			}
			return true;
		} catch (NullPointerException ex) {
			return false;
		}
	}
	
	private void addMarkers(LatLng position, String password, String name,
			Boolean done) {
		Marker marker = null;
		if (done) {
			marker = map
					.addMarker( new MarkerOptions()
							.position( position )
							.title( Util.getName() + ": " + name )
							.snippet( Util.getPassword() + ": " + password )
							.icon( BitmapDescriptorFactory
									.defaultMarker( BitmapDescriptorFactory.HUE_GREEN ) ) );
		} else {
			marker = map.addMarker( new MarkerOptions().position( position )
					.title( Util.getName() + ": " + name )
					.snippet( Util.getPassword() + ": " + password ) );
		}
		markerList.add( marker );
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
						// markerForPosition.remove();
						// if (userLocation.latitude == 0
						// && userLocation.longitude == 0) {
						// userLocation = Util.getSofiaLatLog();
						// }
						// markerForPosition = map.addMarker( new
						// MarkerOptions()
						// .position( userLocation ) );
						// markerForPosition.setTitle( "You are here!" );
						// markerForPosition.setIcon( BitmapDescriptorFactory
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
	
	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.profile, menu); MenuItem loggedIn =
	 * menu.getItem(0); String title = loggedIn.getTitle().toString(); title +=
	 * " " + Util.getUserInfo().getString("Username",
	 * "Error in loading user info") + " Lvl: " +
	 * Util.getUserInfo().getString("UserLevel", "0"); loggedIn.setTitle(title);
	 * return super.onCreateOptionsMenu(menu); }
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case R.id.acc_sett:
	 * changeScreen("ACCOUNT_SETTINGS"); break;
	 * 
	 * case R.id.logout: Logout logout = new Logout(GoogleMaps.this);
	 * logout.setMessageLoading("Loging out..."); logout.execute(Logout.URL +
	 * "?auth_token=" + Util.getUserInfo().getString("AuthToken", "")); Intent
	 * intent = new Intent(GoogleMaps.this, LogIn.class);
	 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); startActivity(intent);
	 * finish(); default: break; } return true; }
	 */
	
	/*
	 * private void changeScreen(String intentName) { Intent changeScreen;
	 * changeScreen = new Intent("com.example.wificon." + intentName);
	 * startActivity(changeScreen); }
	 */
	
	private String getDirectionsUrl(LatLng origin, LatLng dest) {
		
		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;
		
		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
		
		// Sensor enabled
		String sensor = "sensor=false";
		
		// Waypoints
		String waypoints = "";
		for (int i = 2; i < markerPoints.size(); i++) {
			LatLng point = (LatLng) markerPoints.get( i );
			if (i == 2)
				waypoints = "waypoints=";
			waypoints += point.latitude + "," + point.longitude + "|";
		}
		
		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor + "&"
				+ waypoints;
		
		// Output format
		String output = "json";
		
		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;
		
		return url;
	}
	
	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL( strUrl );
			
			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();
			
			// Connecting to url
			urlConnection.connect();
			
			// Reading data from url
			iStream = urlConnection.getInputStream();
			
			BufferedReader br = new BufferedReader( new InputStreamReader(
					iStream ) );
			
			StringBuffer sb = new StringBuffer();
			
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append( line );
			}
			
			data = sb.toString();
			
			br.close();
			
		} catch (Exception e) {
			Util.addException( this.getClass(), e, getBaseContext() );
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}
	
	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String> {
		private int color;
		private Context context;
		
		public DownloadTask(int color, Context context) {
			this.setColor( color );
			this.context = context;
		}
		
		public int getColor() {
			return color;
		}
		
		public void setColor(int color) {
			this.color = color;
		}
		
		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {
			
			// For storing data from web service
			
			String data = "";
			
			try {
				// Fetching the data from web service
				data = downloadUrl( url[0] );
				
			} catch (Exception e) {
				Util.addException( this.getClass(), e, getBaseContext() );
			}
			return data;
		}
		
		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute( result );
			
			ParserTask parserTask = new ParserTask( getColor(), context );
			
			// Invokes the thread for parsing the JSON data
			parserTask.execute( result );
		}
	}
	
	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
		private int color;
		private Context context;
		
		public ParserTask(int color, Context context) {
			setColor( color );
			this.context = context;
		}
		
		public int getColor() {
			return color;
		}
		
		public void setColor(int color) {
			this.color = color;
		}
		
		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {
			
			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;
			
			try {
				jObject = new JSONObject( jsonData[0] );
				DirectionsJSONParser parser = new DirectionsJSONParser();
				// Starts parsing data
				routes = parser.parse( jObject, context );
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}
		
		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			
			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();
				
				// Fetching i-th route
				List<HashMap<String, String>> path = result.get( i );
				
				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get( j );
					
					double lat = Double.parseDouble( point.get( "lat" ) );
					double lng = Double.parseDouble( point.get( "lng" ) );
					LatLng position = new LatLng( lat, lng );
					
					points.add( position );
				}
				
				// Adding all the points in the route to LineOptions
				lineOptions.addAll( points );
				lineOptions.width( 2 );
				lineOptions.color( getColor() );
			}
			
			// Drawing polyline in the Google Map for the i-th route
			try {
				map.addPolyline( lineOptions );
			} catch (NullPointerException e) {
				Toast.makeText( getApplicationContext(),
						Util.getGetNearRoadForRoot(), Toast.LENGTH_LONG )
						.show();
			}
			
		}
	}
}
