package activities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
import util.AdapterTaskSpinner;
import util.Task;
import util.Util;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import comm.savagelook.android.UrlJsonAsyncTask;

public class TargetList extends Activity {
	private String FINISHED = "Finished";
	private String MISSED = "Missed";
	private String STATUS_WHAT = "Status?";
	
	private String TASKS_URL;
	private String STATUS_URL;
	private String STATUS_UPDATE;
	private final Integer ITEM_PER_ROW = 7;
	private static String[] statusArray;
	Button home;
	Button exit;
	Button settings;
	Button manageWiFi;
	Button getTask;
	Button create;
	TableLayout tableView;
	
	private static List<Task> tasksArray;
	
	private static void setStatusArray(String[] statusArray) {
		TargetList.statusArray = statusArray;
	}
	
	private static void setTasksArray(List<Task> tasksArray) {
		TargetList.tasksArray = tasksArray;
	}
	
	public static List<Task> getTasksArray() {
		return tasksArray;
	}
	
	public void switchTabInActivity(int indexTabToSwitchTo,
			Boolean isSingleForMap, HashMap<String, String> content) {
		TargetListTabs parentActivity;
		parentActivity = (TargetListTabs) this.getParent();
		if (isSingleForMap) {
			ManageWifiAll.showOneSpot( content );
		}
		parentActivity.switchTab( indexTabToSwitchTo );
		
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			
			super.onCreate( savedInstanceState );
			setContentView( R.layout.target_list );
			// getActionBar().setIcon(
			// new ColorDrawable(getResources().getColor(
			// android.R.color.transparent)));
			TASKS_URL = Util.getServerAddress( getApplicationContext() )
					+ "tasks.json";
			STATUS_URL = Util.getServerAddress( getApplicationContext() )
					+ "statuses.json";
			STATUS_UPDATE = Util.getServerAddress( getApplicationContext() )
					+ "status_update.json";
			
			getTask();
			
			/*
			 * TextView menu = ((TextView) findViewById(R.id.menu));
			 * menu.setOnClickListener(new View.OnClickListener() {
			 * 
			 * @Override public void onClick(View v) { PopupMenu popup = new
			 * PopupMenu(TargetList.this, v);
			 * popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			 * 
			 * @Override public boolean onMenuItemClick(MenuItem item) { switch
			 * (item.getItemId()) { // case R.id.account_settings: //
			 * changeScreen("ACCOUNT_SETTINGS"); // return true; case
			 * R.id.targ_list: changeScreen("TARGET_LIST_TABS"); return true;
			 * case R.id.manage_netw: changeScreen("MANAGE_NETWORKS"); return
			 * true; case R.id.open_map: changeScreen("GOOGLE_MAP"); return
			 * true; case R.id.logout_m: if
			 * (Util.isWifiEnabled(getApplicationContext()) ||
			 * Util.isInternetAvailable(getApplicationContext())) { if
			 * (Util.getUserInfo().contains("AuthToken")) { Logout logout = new
			 * Logout(TargetList.this);
			 * logout.setMessageLoading("Loging out...");
			 * logout.execute(Logout.URL + "?auth_token=" +
			 * Util.getUserInfo().getString("AuthToken", "")); Intent intent =
			 * new Intent(TargetList.this, LogIn.class);
			 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 * startActivity(intent); finish(); } else { Intent intent = new
			 * Intent(TargetList.this, LogIn.class);
			 * startActivityForResult(intent, 0); finish(); Toast.makeText(
			 * getApplicationContext(), getString(R.string.session_expired_s),
			 * Toast.LENGTH_LONG).show(); } } else { Toast.makeText(
			 * getApplicationContext(),
			 * getString(R.string.no_internet_tologout_s),
			 * Toast.LENGTH_LONG).show(); }
			 * 
			 * return true; case R.id.take_tsk: changeScreen("GET_TASK"); return
			 * true; default: return false; }
			 * 
			 * } });
			 * 
			 * MenuInflater inflater = popup.getMenuInflater();
			 * inflater.inflate(R.menu.menu, popup.getMenu()); popup.show(); }
			 * });
			 */
		} catch (Exception e) {
			Util.addException( this.getClass(), e, getBaseContext() );
		}
	}
	
	private void getTask() {
		if (Util.isWifiEnabled( getApplicationContext() )
				|| Util.isInternetAvailable( getApplicationContext() )) {
			if (Util.getUserInfo().contains( "AuthToken" )) {
				loadTasksFromAPI( TASKS_URL );
			} else {
				Intent intent = new Intent( TargetList.this, LogIn.class );
				startActivityForResult( intent, 0 );
				finish();
				Toast.makeText( getApplicationContext(),
						Util.getSessionExpired(), Toast.LENGTH_LONG ).show();
			}
		} else {
			Toast.makeText( getApplicationContext(), Util.getNoInternet(),
					Toast.LENGTH_LONG ).show();
			Toast.makeText( getApplicationContext(),
					Util.getLoadingOfflineTasks(), Toast.LENGTH_LONG ).show();
			
			readTasksAndStatus();
			if (getTasksArray().size() == 0) {
				Toast.makeText( getApplicationContext(),
						Util.getNoTasksFound(), Toast.LENGTH_LONG ).show();
				Toast.makeText( getApplicationContext(),
						Util.getPleaseConnectToInternet(), Toast.LENGTH_LONG )
						.show();
			}
		}
	}
	
	/*
	 * public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.profile, menu); MenuItem loggedIn =
	 * menu.getItem(0); String title = loggedIn.getTitle().toString(); title +=
	 * " " + Util.getUserInfo().getString("Username",
	 * "Error in loading user info") + " Lvl: " +
	 * Util.getUserInfo().getString("UserLevel", "0"); loggedIn.setTitle(title);
	 * return super.onCreateOptionsMenu(menu); }
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case R.id.acc_sett:
	 * changeScreen("ACCOUNT_SETTINGS"); break; case R.id.logout: if
	 * (Util.isWifiEnabled(getApplicationContext()) ||
	 * Util.isInternetAvailable(getApplicationContext())) { if
	 * (Util.getUserInfo().contains("AuthToken")) { Logout logout = new
	 * Logout(TargetList.this); logout.setMessageLoading("Loging out...");
	 * logout.execute(Logout.URL + "?auth_token=" +
	 * Util.getUserInfo().getString("AuthToken", "")); Intent intent = new
	 * Intent(TargetList.this, LogIn.class);
	 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); startActivity(intent);
	 * finish(); } else { Intent intent = new Intent(TargetList.this,
	 * LogIn.class); startActivityForResult(intent, 0); finish();
	 * Toast.makeText(getApplicationContext(),
	 * getString(R.string.session_expired_s), Toast.LENGTH_LONG).show(); } }
	 * else { Toast.makeText(getApplicationContext(),
	 * getString(R.string.no_internet_tologout_s), Toast.LENGTH_LONG).show(); }
	 * 
	 * default: break; } return true; }
	 */
	
	private Integer updateOnCreateStopper;
	private AdapterView<?> selectedParentSpinner;
	private String typeSelected;
	ArrayAdapter<String> statusArrayAdapter;
	View selectedChangeStatusView;
	
	private void generateTable() {
		try {
			int rowsCount = tasksArray.size() + 1;
			
			TextView[] tView = new TextView[rowsCount * ITEM_PER_ROW];
			int counter = 0;
			
			tableView = (TableLayout) findViewById( R.id.target_table );
			tableView.removeAllViews();
			Context context = this;
			
			TableRow[] tableRow = new TableRow[rowsCount + 1];
			for (int j = 0; j <= rowsCount - 1; j++) {
				tableRow[j] = new TableRow( context );
				final Task theTask;
				if (j == 0) {
					for (int i = 0; i <= ITEM_PER_ROW - 1; i++) {
						
						tView[i] = new TextView( this );
						
						tView[i] = new TextView( context );
						String text = "";
						counter += 1;
						switch (counter) {
							case 1:
								text = "No";
								break;
							case 2:
								text = Util.getAddress();
								break;
							case 3:
								text = Util.getName();
								break;
							case 4:
								text = Util.getPassword();
								break;
							case 5:
								text = Util.getKeyword();
								break;
							case 6:
								text = Util.getWebsite();
								
								break;
							case 7:
								text = Util.getStatus();
								break;
							default:
								text = Util.getErrorLoadingTask();
								;
								break;
						}
						
						tView[i].setText( text );
						tView[i].setTextSize( 10 );
						tView[i].setGravity( 0x11 );
						if (i == 0) {
							tView[i].setWidth( 80 );
						} else {
							tView[i].setWidth( 160 );
							tView[i].setHeight( 40 );
						}
						tableRow[j].addView( tView[i] );
					}
					tableView.addView( tableRow[j] );
					
					continue;
				} else {
					theTask = tasksArray.get( j - 1 );
				}
				counter = 0;
				for (int i = 0; i <= ITEM_PER_ROW - 1; i++) {
					if (i == ITEM_PER_ROW - 1) { // j != 0 &&
						Spinner status = new Spinner( this );
						ArrayList<String> statusList = new ArrayList<String>();
						for (String st : statusArray) {
							statusList.add( st );
						}
						statusArrayAdapter = new AdapterTaskSpinner( this,
								R.layout.spinner_layout, statusList );
						status.setAdapter( statusArrayAdapter );
						status.setOnItemSelectedListener( new OnItemSelectedListener() {
							
							@Override
							public void onItemSelected(
									AdapterView<?> parentView,
									View selectedItemView, int position, long id) {
								RelativeLayout relLay = (RelativeLayout) parentView
										.getChildAt( 0 );
								
								View itemView = relLay.getChildAt( 0 );
								
								selectedChangeStatusView = itemView;
								typeSelected = ((TextView) itemView).getText()
										.toString();
								
								selectedParentSpinner = parentView;
								updateOnCreateStopper -= 1;
								if (updateOnCreateStopper < 0) {
									
									TableRow row = (TableRow) parentView
											.getParent();
									TextView item = (TextView) row
											.getChildAt( 0 );
									
									Integer theFakeID = Integer.parseInt( item
											.getText().toString().trim() );
									
									String theID = tasksArray.get(
											theFakeID - 1 ).getId();
									SendStatusChange changeStatus = new SendStatusChange(
											TargetList.this, Util
													.getNoInternetToUpdate(),
											typeSelected, theID, theFakeID - 1 );
									switch (position) { // tasksArray[position -
														// 1].getStatus()
										case 2:
											
											changeStatus
													.setMessageLoading( Util
															.getVerifyStatus() );
											changeStatus
													.execute( STATUS_UPDATE
															+ "?auth_token="
															+ Util.getUserInfo()
																	.getString(
																			"AuthToken",
																			"" ) );
											break;
										case 1:
											
											changeStatus
													.setMessageLoading( Util
															.getVerifyStatus() );
											changeStatus
													.execute( STATUS_UPDATE
															+ "?auth_token="
															+ Util.getUserInfo()
																	.getString(
																			"AuthToken",
																			"" ) );
											break;
									// case 0:
									//
									// selectedItemView
									// .setBackgroundColor( 0xFFFFFFFF ); //
									// white
									// break;
									//
									// default:
									//
									// selectedItemView
									// .setBackgroundColor( 0xFFFFFFFF );
									//
									// break;
									}
								}
								// } else {
								// switch (position) { // tasksArray[position -
								// // 1].getStatus()
								// case 2:
								// // if(getLocalIpAddress().equals(object))
								// selectedItemView
								// .setBackgroundColor( 0xFF00FF00 ); // green
								// break;
								// case 1:
								//
								// selectedItemView
								// .setBackgroundColor( 0xFFFF0000 ); // red
								// break;
								//
								// case 0:
								//
								// selectedItemView
								// .setBackgroundColor( 0xFFFFFFFF ); // white
								// break;
								//
								// default:
								// selectedItemView
								// .setBackgroundColor( 0xFFFFFFFF );
								// // parentView.setBackgroundColor(0xAA00FF00
								// // );
								// break;
								// }
								// }
							}
							
							@Override
							public void onNothingSelected(
									AdapterView<?> parentView) {
								// your code here
							}
							
						} );
						if (theTask.getStatus().equals( FINISHED )) {
							status.setSelection( statusArrayAdapter
									.getPosition( FINISHED ) );
						} else if (theTask.getStatus().equals( MISSED )) {
							status.setSelection( statusArrayAdapter
									.getPosition( MISSED ) );
						} else if (theTask.getStatus().equals( STATUS_WHAT )) {
							status.setSelection( statusArrayAdapter
									.getPosition( STATUS_WHAT ) );
						} else {
							Toast.makeText( getApplicationContext(),
									Util.getErrorReadingStatus(),
									Toast.LENGTH_LONG ).show();
						}
						tableRow[j].addView( status );
					}
					
					else {
						tView[i] = new TextView( this );
						
						tView[i] = new TextView( context );
						String text = "";
						counter += 1;
						switch (counter) {
							case 1:
								text = theTask.getFakeId();
								tView[i].setTextColor( Color.rgb( 50, 100, 100 ) );
								break;
							case 2:
								text = theTask.getAddress();
								tView[i].setTextColor( Color.rgb( 50, 100, 100 ) );
								break;
							case 3:
								text = theTask.getName();
								tView[i].setTextColor( Color.rgb( 50, 100, 200 ) );
								tView[i].setOnClickListener( new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										// TODO: add direct to wifi spot on map?
										HashMap<String, String> content = new HashMap<String, String>();
										content.put( "name", theTask.getName() );
										content.put( "password",
												theTask.getPassword() );
										content.put( "address",
												theTask.getAddress() );
										switchTabInActivity( 2, true, content );
									}
								} );
								
								break;
							case 4:
								text = theTask.getPassword();
								tView[i].setTextColor( Color.rgb( 50, 100, 100 ) );
								break;
							case 5:
								text = theTask.getKeyword();
								tView[i].setTextColor( Color.rgb( 50, 100, 100 ) );
								break;
							case 6:
								text = theTask.getWebsite();
								tView[i].setTextColor( Color.rgb( 50, 100, 200 ) );
								final String website = text;
								tView[i].setOnClickListener( new OnClickListener() {
									@Override
									public void onClick(View v) {
										String newWeb = "";
										if (website.contains( "http://" )) {
											if (!website.contains( "www." )) {
												newWeb = website.replace(
														"http://",
														"http://www." );
											}
										} else {
											newWeb = "http://";
											if (!website.contains( "www." )) {
												newWeb += "www.";
											}
											newWeb += website;
										}
										Intent intent = new Intent(
												getApplicationContext(),
												WebBrower.class );
										intent.putExtra( "url", newWeb );
										intent.putExtra( "keyword",
												theTask.getKeyword() );
										startActivity( intent );
									}
								} );
								break;
							default:
								text = Util.getErrorLoadingTask();
								break;
						}
						
						tView[i].setText( text );
						tView[i].setTextSize( 10 );
						
						tView[i].setGravity( 0x11 );
						if (i == 0) {
							tView[i].setWidth( 80 );
						} else {
							tView[i].setWidth( 160 );
							tView[i].setHeight( 70 );
						}
						tableRow[j].addView( tView[i] );
					}
				}
				tableView.addView( tableRow[j] );
			}
		} catch (Exception e) {
			Util.addException( this.getClass(), e, getBaseContext() );
			Toast.makeText( getApplicationContext(),
					e.getClass().getName() + " " + e.getMessage(),
					Toast.LENGTH_LONG ).show();
		}
	}
	
	private void loadTasksFromAPI(String url) {
		int tasksCount = Util.datasource.getTasksCount();
		
		if (tasksCount == 0) {
			GetTasksTask getTasksTask = new GetTasksTask( TargetList.this,
					Util.getErrorLoadingTask() );
			getTasksTask.setMessageLoading( Util.getLoadingTasks() );
			getTasksTask.execute( url + "?auth_token="
					+ Util.getUserInfo().getString( "AuthToken", "" ) );
			GetStatus getStatus = new GetStatus( TargetList.this,
					Util.getErrorLoadingTask() );
			getStatus.setMessageLoading( Util.getLoadingTasks() );
			getStatus.execute( STATUS_URL + "?auth_token="
					+ Util.getUserInfo().getString( "AuthToken", "" ) );
		} else {
			readTasksAndStatus();
		}
		
	}
	
	private void readTasksAndStatus() {
		// The name of the file to open.
		
		List<Task> readTasks = Util.datasource.findAllTasks();
		updateOnCreateStopper = readTasks.size();
		setTasksArray( readTasks );
		readStatuses();
		generateTable();
	}
	
	private void writeStatuses() {
		String fileName = "status.txt";
		
		try {
			FileOutputStream fostream = openFileOutput( fileName,
					Context.MODE_PRIVATE );
			OutputStreamWriter oswriter = new OutputStreamWriter( fostream );
			BufferedWriter bwriter = new BufferedWriter( oswriter );
			for (String status : statusArray) {
				bwriter.write( status );
				bwriter.newLine();
			}
			bwriter.close();
			oswriter.close();
			fostream.close();
		} catch (FileNotFoundException e) {
			Util.addException( this.getClass(), e, getBaseContext() );
		} catch (IOException e) {
			Util.addException( this.getClass(), e, getBaseContext() );
		}
	}
	
	private void readStatuses() {
		// The name of the file to open.
		String fileName = "status.txt";
		try {
			FileInputStream fis = openFileInput( fileName );
			InputStreamReader input = new InputStreamReader( fis );
			BufferedReader reader = new BufferedReader( input );
			String line = reader.readLine();
			
			ArrayList<String> listOfStatuses = new ArrayList<String>();
			while (line != null) {
				listOfStatuses.add( line );
				line = reader.readLine();
			}
			int statusCount = listOfStatuses.size();
			setStatusArray( new String[statusCount] );
			String[] readStatus = new String[statusCount];
			for (int i = 0; i < statusCount; i++) {
				
				String status = listOfStatuses.get( i );
				readStatus[i] = status;
			}
			setStatusArray( readStatus );
			
		} catch (FileNotFoundException e) {
			Util.addException( this.getClass(), e, getBaseContext() );
		} catch (IOException e) {
			Util.addException( this.getClass(), e, getBaseContext() );
		}
	}
	
	private void writeTasksAndStatus() {
		
		Thread th = new Thread( new Runnable() {
			
			@Override
			public void run() {
				
				for (Task task : tasksArray) {
					Util.datasource.createTask( task );
				}
			}
		} );
		th.start();
	}
	
	private void updateTask(Task task) {
		Util.datasource.updateTask( task );
	}
	
	public static Integer[] getPositionArray() {
		
		return positionArray;
	}
	
	private static Integer[] positionArray;
	
	public class GetTasksTask extends UrlJsonAsyncTask {
		public GetTasksTask(Context context, String errorMessage) {
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
						holder.put( "ip", Util.getIp( TargetList.this ) );
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
						e.printStackTrace();
						Util.addException( this.getClass(), e, context );
						Looper.prepare();
						// FailureResponse failure = new
						// FailureResponse(LogIn.this);
						// failure.execute(FAILURE_URL);
						json.put( "info", Util.getInvalidCredentials() );
					} catch (IOException e) {
						e.printStackTrace();
						Util.addException( this.getClass(), e, context );
					}
				} catch (JSONException e) {
					e.printStackTrace();
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
					String response = json.getString( "info" );
					if (json.getBoolean( "success" )) {
						
						JSONArray jsonTasks = json.getJSONArray( "data" );
						
						int length = jsonTasks.length();
						
						setTasksArray( new ArrayList<Task>() );
						positionArray = new Integer[length];
						updateOnCreateStopper = length;
						List<Task> newTasks = new ArrayList<Task>();
						for (int i = 0; i < length; i++) {
							Task task = new Task( jsonTasks.getJSONObject( i ),
									(String) (((Integer) (i + 1)) + ""),
									context );
							newTasks.add( task );
							try {
								positionArray[i] = Integer.parseInt( task
										.getId() );
							} catch (NumberFormatException e) {
								Util.addException( this.getClass(), e, context );
								Log.i( "Invalid parse",
										"Task id was in invalid format?? --> "
												+ task.getId() );
							}
						}
						
						setTasksArray( newTasks );
						writeTasksAndStatus();
					} else {
						if (response.equals( Util.getTokenExpired() )) {
							Util.logout( TargetList.this, false );
						}
						
					}
					Toast.makeText( context, response, Toast.LENGTH_LONG )
							.show();
				} catch (Exception e) {
					Util.addException( this.getClass(), e, context );
					Toast.makeText( context, e.getMessage(), Toast.LENGTH_LONG )
							.show();
				} finally {
					
					super.onPostExecute( json );
				}
		}
	}
	
	public class GetStatus extends UrlJsonAsyncTask {
		public GetStatus(Context context, String errorMessage) {
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
						holder.put( "ip", Util.getIp( TargetList.this ) );
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
						e.printStackTrace();
						Util.addException( this.getClass(), e, context );
						Looper.prepare();
						// FailureResponse failure = new
						// FailureResponse(LogIn.this);
						// failure.execute(FAILURE_URL);
						json.put( "info", Util.getInvalidCredentials() );
					} catch (IOException e) {
						e.printStackTrace();
						Util.addException( this.getClass(), e, context );
					}
				} catch (JSONException e) {
					e.printStackTrace();
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
					String response = json.getString( "info" );
					if (json.getBoolean( "success" )) {
						
						JSONArray jsonStatuses = json.getJSONArray( "data" );
						
						int length = jsonStatuses.length();
						setStatusArray( new String[length] );
						String[] newStatus = new String[length];
						for (int i = 0; i < length; i++) {
							newStatus[i] = jsonStatuses.getJSONObject( i )
									.getString( "Name" );
						}
						setStatusArray( newStatus );
						writeStatuses();
						readTasksAndStatus();
						generateTable();
					} else {
						if (response.equals( Util.getTokenExpired() )) {
							Util.logout( TargetList.this, false );
						} else {
							Toast.makeText( context, response,
									Toast.LENGTH_LONG ).show();
						}
					}
					Toast.makeText( context, response, Toast.LENGTH_LONG )
							.show();
				} catch (Exception e) {
					Toast.makeText( context, e.getMessage(), Toast.LENGTH_LONG )
							.show();
					Util.addException( this.getClass(), e, context );
				} finally {
					
					super.onPostExecute( json );
				}
		}
	}
	
	private class SendStatusChange extends UrlJsonAsyncTask {
		private String value;
		private String id;
		private int positionInArray;
		
		public SendStatusChange(Context context, String errorMessage,
				String value, String id, int position) {
			super( context, errorMessage );
			this.value = value;
			this.id = id;
			this.positionInArray = position;
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
				
				try {
					try {
						// setup the returned values in case
						// something goes wrong
						String errorMessage = Util.getErrorMessage();
						json.put( "success", false );
						json.put( "info", errorMessage );
						holder.put( "ip", Util.getIp( TargetList.this ) );
						holder.put( "language", Util.getCurrentLanguadge() );
						// add the users's info to the post params
						userObj.put( "ip", Util.getIp( TargetList.this ) );
						userObj.put( "value", value );
						userObj.put( "id", id );
						holder.put( "status", userObj );
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
						Util.addException( this.getClass(), e, context );
					} catch (IOException e) {
						e.printStackTrace();
						Util.addException( this.getClass(), e, context );
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Util.addException( this.getClass(), e, context );
				}
				
				return json;
			} else {
				runOnUiThread( new Runnable() {
					
					@Override
					public void run() {
						String status = tasksArray.get( positionInArray )
								.getStatus();
						
						// if (status.equals( FINISHED )) {
						// selectedChangeStatusView
						// .setBackgroundColor( 0xFF00FF00 );
						// } else {
						// if (status.equals( MISSED )) {
						// selectedChangeStatusView
						// .setBackgroundColor( 0xFFFF0000 );
						// } else {
						// selectedChangeStatusView
						// .setBackgroundColor( 0xFFFFFFFF );
						// }
						// }
						((TextView) selectedChangeStatusView).setText( status );
						int spinnerPosition = statusArrayAdapter
								.getPosition( status );
						updateOnCreateStopper = 1;
						selectedParentSpinner.setSelection( spinnerPosition );
						
					}
				} );
				
			}
			
			return null;
		}
		
		private String FINISHED_NO_CHANGE = getString( R.string.en_finished_task_no_change );
		
		@Override
		protected void onPostExecute(JSONObject json) {
			if (willExecuteJson)
				try {
					String status = "";
					String taskInfo = json.getString( "info" );
					if (json.getBoolean( "success" )) {
						SharedPreferences.Editor editor = Util.getUserInfo()
								.edit();
						
						editor.putString( "UserLevel",
								json.getJSONObject( "data" )
										.getString( "level" ) );
						editor.commit();
						TargetListTabs.updateLevel();
						Task task = tasksArray.get( positionInArray );
						task.setStatus( value );
						updateTask( task );
						// local checks for the data to be set for the user
						// interface
						if (typeSelected.equals( FINISHED )) {
							status = FINISHED;
							// selectedChangeStatusView
							// .setBackgroundColor( 0xFF00FF00 );
						} else {
							if (typeSelected.equals( MISSED )) {
								status = MISSED;
								// selectedChangeStatusView
								// .setBackgroundColor( 0xFFFF0000 );
							} else {
								status = STATUS_WHAT;
								// selectedChangeStatusView
								// .setBackgroundColor( 0xFFFFFFFF );
							}
						}
						final int spinnerPosition = statusArrayAdapter
								.getPosition( status );
						updateOnCreateStopper = 0;
						selectedParentSpinner.setSelection( spinnerPosition );
						
						Toast.makeText( context, taskInfo, Toast.LENGTH_LONG )
								.show();
					} else {
						// Check from server if the result is the
						if (json.getString( "info" )
								.equals( FINISHED_NO_CHANGE )) {
							status = FINISHED;
							// selectedChangeStatusView
							// .setBackgroundColor( 0xFF00FF00 );
							int spinnerPosition = statusArrayAdapter
									.getPosition( status );
							updateOnCreateStopper = 1;
							selectedParentSpinner
									.setSelection( spinnerPosition );
							Toast.makeText( context, taskInfo,
									Toast.LENGTH_LONG ).show();
						} else {
							// set the default according to value
							
							status = tasksArray.get( positionInArray )
									.getStatus();
							if (status.equals( FINISHED )) {
								status = FINISHED;
								// selectedChangeStatusView
								// .setBackgroundColor( 0xFF00FF00 );
							} else if (status.equals( MISSED )) {
								status = MISSED;
								// selectedChangeStatusView
								// .setBackgroundColor( 0xFFFF0000 );
							} else {
								status = STATUS_WHAT;
								// selectedChangeStatusView
								// .setBackgroundColor( 0xFFFFFFFF );
							}
							int spinnerPosition = statusArrayAdapter
									.getPosition( status );
							updateOnCreateStopper = 1;
							selectedParentSpinner
									.setSelection( spinnerPosition );
							if (taskInfo.equals( Util.getTokenExpired() )) {
								Util.logout( TargetList.this, false );
							} else {
								Toast.makeText( context, taskInfo,
										Toast.LENGTH_LONG ).show();
							}
						}
					}
				} catch (Exception e) {
					Toast.makeText( context, e.getMessage(), Toast.LENGTH_LONG )
							.show();
					Util.addException( this.getClass(), e, context );
				} finally {
					super.onPostExecute( json );
				}
		}
	}
}
