package activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import seo.extra.wifi_analyzor.R;
import util.Util;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import comm.savagelook.android.UrlJsonAsyncTask;

import database.DatabaseHelper;

public class GetTask extends Activity {
	
	private String CREATE_TASKS_URL;
	
	private String selected;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			Spinner numberOfTasks;
			Button create;
			
			super.onCreate( savedInstanceState );
			setContentView( R.layout.get_task );
			
			getActionBar().setIcon(
					new ColorDrawable( getResources().getColor(
							android.R.color.transparent ) ) );
			setLayoutStrings();
			CREATE_TASKS_URL = Util.getServerAddress( this ) + "take_task.json";
			List<String> numberOfTasksList = new ArrayList<String>();
			
			numberOfTasksList.add( "10 " + Util.getTasks() );
			numberOfTasksList.add( "20 " + Util.getTasks() );
			numberOfTasksList.add( "30 " + Util.getTasks() );
			numberOfTasksList.add( "40 " + Util.getTasks() );
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>( this,
					android.R.layout.simple_spinner_item, numberOfTasksList );
			adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
			
			numberOfTasks = (Spinner) findViewById( R.id.number_of_tasks );
			numberOfTasks.setAdapter( adapter );
			numberOfTasks.setOnItemSelectedListener( new GetSelectedItem() );
			EditText hoursEdit = (EditText) findViewById( R.id.hours );
			hoursEdit.requestFocus();
			
			/*
			 * TextView menu = ((TextView) findViewById(R.id.menu));
			 * menu.setOnClickListener(new View.OnClickListener() {
			 * 
			 * @Override public void onClick(View v) { PopupMenu popup = new
			 * PopupMenu(GetTask.this, v); popup.setOnMenuItemClickListener(new
			 * OnMenuItemClickListener() {
			 * 
			 * @Override public boolean onMenuItemClick(MenuItem item) { switch
			 * (item.getItemId()) { //case R.id.account_settings: //
			 * changeScreen("ACCOUNT_SETTINGS"); // return true; case
			 * R.id.targ_list: changeScreen("TARGET_LIST_TABS"); return true;
			 * case R.id.manage_netw: changeScreen("MANAGE_NETWORKS"); return
			 * true; case R.id.open_map: changeScreen("GOOGLE_MAP"); return
			 * true; case R.id.logout_m: Logout logout = new
			 * Logout(GetTask.this); logout.setMessageLoading("Loging out...");
			 * logout.execute(Logout.URL + "?auth_token=" +
			 * Util.getUserInfo().getString("AuthToken", "")); Intent intent =
			 * new Intent(GetTask.this, LogIn.class);
			 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 * startActivity(intent); finish(); return true; case R.id.take_tsk:
			 * changeScreen("GET_TASK"); return true; default: return false; }
			 * 
			 * } });
			 * 
			 * MenuInflater inflater = popup.getMenuInflater();
			 * inflater.inflate(R.menu.menu, popup.getMenu()); popup.show();
			 * 
			 * } });
			 */
			create = (Button) findViewById( R.id.create_button );
			create.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					EditText hoursEdit = (EditText) findViewById( R.id.hours );
					EditText minutesEdit = (EditText) findViewById( R.id.minutes );
					if (hoursEdit.length() == 0) {
						Toast.makeText( getApplicationContext(),
								Util.getPleaseGiveTime(), Toast.LENGTH_LONG )
								.show();
					} else {
						if (minutesEdit.length() == 0) {
							minutesEdit.setText( "00" );
						}
						SendTaskRequest sendTaskRequest = new SendTaskRequest(
								GetTask.this, Util.getNoInternet() );
						sendTaskRequest.setMessageLoading( Util
								.getLoadingTasks() );
						sendTaskRequest.execute( CREATE_TASKS_URL
								+ "?auth_token="
								+ Util.getUserInfo()
										.getString( "AuthToken", "" ) );
						// check in data base information from the username
						// and password
					}
				}
			} );
		} catch (Exception e) {
			Util.addException( this.getClass(), e, getBaseContext() );
		}
	}
	
	private void setLayoutStrings() {
		setTitle( Util.getGetTaskTitle() );
		EditText minutes = (EditText) findViewById( R.id.minutes );
		minutes.setHint( Util.getMinutes() );
		EditText hours = (EditText) findViewById( R.id.hours );
		hours.setHint( Util.getHours() );
		TextView availableTime = (TextView) findViewById( R.id.available_time );
		availableTime.setText( Util.getAvailableTime() );
		TextView numberOfTasks = (TextView) findViewById( R.id.number_of_tasks_info );
		numberOfTasks.setText( Util.getNumberOfTasks() );
		Button createNew = (Button) findViewById( R.id.create_button );
		createNew.setText( Util.getCreateNew() );
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
				Util.logout( GetTask.this, true );
				break;
			default:
				break;
		}
		return true;
	}
	
	public class GetSelectedItem implements OnItemSelectedListener {
		
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			selected = parent.getItemAtPosition( pos ).toString();
		}
		
		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing.
		}
	}
	
	private class SendTaskRequest extends UrlJsonAsyncTask {
		public SendTaskRequest(Context context, String errorMessage) {
			super( context, errorMessage );
		}
		
		@Override
		protected JSONObject doInBackground(String... urls) {
			if (willExecuteJson) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost( urls[0] );
				JSONObject holder = new JSONObject();
				JSONObject userObj = new JSONObject();
				String response = null;
				JSONObject json = new JSONObject();
				
				EditText hoursEdit = (EditText) findViewById( R.id.hours );
				EditText minutesEdit = (EditText) findViewById( R.id.minutes );
				Integer ints;
				try {
					if (Util.tryParseInt( hoursEdit.getText().toString() )
							&& Util.tryParseInt( minutesEdit.getText()
									.toString() )) {
						ints = Integer.parseInt( (hoursEdit.getText()
								.toString()) )
								* 60
								+ Integer.parseInt( (minutesEdit.getText()
										.toString()) );
						if (ints.compareTo( 0 ) == -1) {
							ints = Integer.parseInt( (hoursEdit.getText()
									.toString()) );
							if (ints.equals( null )
									|| ints.compareTo( 0 ) == -1) {
								Toast.makeText( context,
										Util.getHoursNotLessZero(),
										Toast.LENGTH_LONG ).show();
							}
							ints = Integer.parseInt( (minutesEdit.getText()
									.toString()) );
							if (ints.equals( null )
									|| ints.compareTo( 0 ) == -1) {
								Toast.makeText( context,
										Util.getMinutesNotLessZero(),
										
										Toast.LENGTH_LONG ).show();
							}
						} else {
							try {
								try {
									String minutesAndSeconds = hoursEdit
											.getText().toString()
											+ ":"
											+ minutesEdit.getText().toString();
									// setup the returned values in case
									// something goes wrong
									json.put( "success", false );
									json.put( "info", Util.getErrorMessage() );
									holder.put( "ip", Util.getIp( GetTask.this ) );
									holder.put( "language",
											Util.getCurrentLanguadge() );
									// add the users's info to the post params
									String items = selected.split( " " )[0];
									LatLng userLocation = Util
											.getCurrentLocation( getApplicationContext() );
									
									// TO DO ADD !! userInfo coordinates
									if (userLocation.latitude == 0
											&& userLocation.longitude == 0) {
										userLocation = Util.getSofiaLatLog();
									}
									userObj.put( "count", items );
									userObj.put( "time", minutesAndSeconds );
									userObj.put( "lati", userLocation.latitude );
									userObj.put( "longi",
											userLocation.longitude );
									holder.put( "task_info", userObj );
									StringEntity se = new StringEntity(
											holder.toString() );
									post.setEntity( se );
									
									// setup the request headers
									post.setHeader( "Accept",
											"application/json" );
									post.setHeader( "Content-Type",
											"application/json" );
									
									ResponseHandler<String> responseHandler = new BasicResponseHandler();
									response = client.execute( post,
											responseHandler );
									json = new JSONObject( response );
									
								} catch (HttpResponseException e) {
									e.printStackTrace();
									Util.addException( this.getClass(), e,
											context );
								} catch (IOException e) {
									e.printStackTrace();
									Util.addException( this.getClass(), e,
											context );
								}
							} catch (JSONException e) {
								e.printStackTrace();
								Util.addException( this.getClass(), e, context );
							}
						}
					}
				} catch (Exception e) {
					Util.addException( this.getClass(), e, context );
				}
				
				return json;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			if (willExecuteJson)
				try {
					if (json.getString( "info" ).equals( "No value for info" )) {
						Toast.makeText( context, Util.getNoTasksInvalidTime(),
								Toast.LENGTH_LONG ).show();
					} else {
						if (json.getBoolean( "success" )) {
							
							Toast.makeText( context, json.getString( "info" ),
									Toast.LENGTH_LONG ).show();
							Util.datasource
									.clearTable( DatabaseHelper.TABLE_TASKS );
							Util.changeScreen( "TARGET_LIST_TABS",
									getApplicationContext() );
						} else {
							String response = json.getString( "info" );
							if (response.equals( Util.getTokenExpired() )) {
								Util.logout( GetTask.this, true );
							} else {
								Toast.makeText( context, response,
										Toast.LENGTH_LONG ).show();
							}
						}
					}
				} catch (Exception e) {
					// something went wrong: show a Toast
					// with the exception message
					Toast.makeText( context, e.getMessage(), Toast.LENGTH_LONG )
							.show();
					Util.addException( this.getClass(), e, context );
				} finally {
					super.onPostExecute( json );
				}
		}
	}
}
