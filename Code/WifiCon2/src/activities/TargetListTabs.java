package activities;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import seo.extra.wifi_analyzor.R;
import util.Task;
import util.Util;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import comm.savagelook.android.UrlJsonAsyncTask;

@SuppressWarnings("deprecation")
public class TargetListTabs extends TabActivity {
	/** Called when the activity is first created. */
	public static Context context = null;
	private String MISS_ALL_TASKS_URL;
	private String HAS_ACTIVE_URL;
	private TabHost tabHost;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate( savedInstanceState );
			setContentView( R.layout.tab_select );
			addLegacyOverflowButton( getWindow() );
			tabHost = (TabHost) findViewById( android.R.id.tabhost );
			MISS_ALL_TASKS_URL = Util.getServerAddress( this )
					+ "miss_all.json";
			HAS_ACTIVE_URL = Util.getServerAddress( this ) + "has_active.json";
			// getActionBar().hide();
			// setTitle(Util.getTabsTitle());
			context = TargetListTabs.this;
			
			/* TabSpec setIndicator() is used to set name for the tab. */
			/* TabSpec setContent() is used to set content for a particular tab. */
			createTabAndAdd( Util.getTaskList(), new Intent( this,
					TargetList.class ) );
			createTabAndAdd( Util.getMap(), new Intent( this, GoogleMaps.class ) );
			createTabAndAdd( Util.getViewAllWifi(), new Intent( this,
					ManageWifiAll.class ) );
			createTabAndAdd( Util.getManageWifi(), new Intent( this,
					ManageNetworks.class ) );
			int tabCount = tabHost.getTabWidget().getTabCount();
			for (int i = 0; i < tabCount; i++) {
				final View view = tabHost.getTabWidget().getChildTabViewAt( i );
				view.setTag( "tid" + (i + 1) );
				if (view != null) {
					// reduce height of the tab
					view.getLayoutParams().height *= 0.66;
					
					// get title text view
					final View textView = view
							.findViewById( android.R.id.title );
					if (textView instanceof TextView) {
						// just in case check the type
						
						// center text
						((TextView) textView).setGravity( Gravity.CENTER );
						// wrap text
						((TextView) textView).setSingleLine( false );
						
						// explicitly set layout parameters
						textView.getLayoutParams().height = ViewGroup.LayoutParams.FILL_PARENT;
						textView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
					}
				}
			}
			
			// }
			/*
			 * else{
			 * firstTabSpec.setIndicator(getString(R.string.Task)).setContent(
			 * new Intent(this, TargetList.class));
			 * secondTabSpec.setIndicator(getString(R.string.map)).setContent(
			 * new Intent(this, GoogleMaps.class));
			 * thirdTabSpec.setIndicator(getString(R.string.manage_wifi_all_s))
			 * .setContent(new Intent(this, ManageWifiAll.class));
			 * fourthTabSpec.setIndicator(getString(R.string.manage_wifi_s))
			 * .setContent(new Intent(this, ManageNetworks.class)); }
			 */
		} catch (Exception e) {
			Util.addException( this.getClass(), e, getBaseContext() );
		}
	}
	
	public static void addLegacyOverflowButton(Window window) {
		if (window.peekDecorView() == null) {
			throw new RuntimeException(
					"Must call addLegacyOverflowButton() after setContentView()" );
		}
		
		try {
			window.addFlags( WindowManager.LayoutParams.class.getField(
					"FLAG_NEEDS_MENU_KEY" ).getInt( null ) );
		} catch (NoSuchFieldException e) {
			// Ignore since this field won't exist in most versions of Android
		} catch (IllegalAccessException e) {
			Log.w( "tag",
					"Could not access FLAG_NEEDS_MENU_KEY in addLegacyOverflowButton()",
					e );
		}
	}
	
	private void createTabAndAdd(String name, Intent intent) {
		TabSpec tabSpec = tabHost.newTabSpec( "tid1" );
		tabSpec.setIndicator( name ).setContent( intent );
		tabHost.addTab( tabSpec );
	}
	
	@Override
	protected void onResume() {
		invalidateOptionsMenu();
		super.onResume();
	}
	
	public static void updateLevel() {
		((Activity) context).invalidateOptionsMenu();
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
				checkForSpotsAndPermissionForNew();
				
				break;
			case R.id.targ_list:
				Util.changeScreen( "TARGET_LIST_TABS", getApplicationContext() );
				break;
			case R.id.logout:
				Util.logout( TargetListTabs.this, true );
			default:
				break;
		}
		return true;
	}
	
	public void checkForSpotsAndPermissionForNew() {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						SendMissedForAll missAll = new SendMissedForAll(
								TargetListTabs.this, Util.getNoInternet() );
						missAll.execute( MISS_ALL_TASKS_URL
								+ "?auth_token="
								+ Util.getUserInfo()
										.getString( "AuthToken", "" ) );
						dialog.dismiss();
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						dialog.dismiss();
						break;
				}
			}
		};
		if (Util.isWifiEnabled( context ) || Util.isInternetAvailable( context )) {
			if (Util.getUserInfo().contains( "AuthToken" )) {
				CheckIfAnyTasks checkIfAny = new CheckIfAnyTasks(
						TargetListTabs.this, Util.getNoInternet() );
				checkIfAny.setMessageLoading( Util.getPleaseWait() );
				JSONObject a = null;
				try {
					
					checkIfAny.execute( HAS_ACTIVE_URL + "?auth_token="
							+ Util.getUserInfo().getString( "AuthToken", "" ) );
					
					a = checkIfAny.get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String message = "";
				Boolean completed = false;
				try {
					message = a.getString( "info" );
					completed = a.getBoolean( "success" );
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (!completed) {
					Util.showPupupForConfirmation( clickListener,
							TargetListTabs.this, Util.getGetNewTasks(), message
									+ "\n" + Util.getWouldYouLike() );
				} else {
					Util.changeScreen( "GET_TASK", getApplicationContext() );
				}
				
			} else {
				Intent intent = new Intent( context, LogIn.class );
				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
				context.startActivity( intent );
				
				Toast.makeText( context, Util.getSessionExpired(),
						Toast.LENGTH_LONG ).show();
			}
		} else {
			Toast.makeText( context, Util.getNoInternet(), Toast.LENGTH_LONG )
					.show();
		}
	}
	
	public void switchTab(int tab) {
		tabHost.setCurrentTab( tab );
	}
	
	private class SendMissedForAll extends UrlJsonAsyncTask {
		public SendMissedForAll(Context context, String errorMessage) {
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
				
				try {
					try {
						
						// setup the returned values in case
						// something goes wrong
						json.put( "success", false );
						json.put( "info", Util.getErrorMessage() );
						// add the users's info to the post params
						holder.put( "ip", Util.getIp( TargetListTabs.this ) );
						holder.put( "language", Util.getCurrentLanguadge() );
						// TO DO ADD !! userInfo coordinates
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
							String jsn = json.getString( "info" ).toString();
							String[] message = new String[3];
							
							message = jsn.split( "\\." );
							Toast.makeText( context,
									message[0] + "." + "\n" + message[1] + ".",
									Toast.LENGTH_LONG ).show();
							List<Task> list = Util.datasource.findAllTasks();
							for (Task task : list) {
								task.setStatus( "Missed" );
								Util.datasource.updateTask( task );
							}
							Util.changeScreen( "GET_TASK",
									getApplicationContext() );
						} else {
							if (json.getString( "info" ).equals(
									Util.getTokenExpired() )) {
								Util.logout( TargetListTabs.this, true );
							} else {
								Toast.makeText( context,
										json.getString( "info" ),
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
	
	public class CheckIfAnyTasks extends UrlJsonAsyncTask {
		public CheckIfAnyTasks(Context context, String errorMessage) {
			super( context, errorMessage );
			this.canGetTasks = false;
		}
		
		private String message;
		private Boolean canGetTasks;
		
		public Boolean getCanGetTasks() {
			return this.canGetTasks;
		}
		
		public String getMessage() {
			return this.message;
		}
		
		@Override
		protected JSONObject doInBackground(String... urls) {
			if (willExecuteJson) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost( urls[0] );
				JSONObject holder = new JSONObject();
				String response = null;
				JSONObject json = new JSONObject();
				
				try {
					try {
						
						// setup the returned values in case
						// something goes wrong
						json.put( "success", false );
						json.put( "info", Util.getErrorMessage() );
						// add the users's info to the post params
						holder.put( "ip", Util.getIp( TargetListTabs.this ) );
						holder.put( "language", Util.getCurrentLanguadge() );
						// TO DO ADD !! userInfo coordinates
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
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			if (willExecuteJson)
				try {
					String response = json.getString( "info" );
					if (response.equals( "No value for info" )) {
						Toast.makeText( context, Util.getNoTasksInvalidTime(),
								Toast.LENGTH_LONG ).show();
					} else {
						if (json.getBoolean( "success" )) {
							this.canGetTasks = true;
						} else {
							
							if (response.equals( Util.getTokenExpired() )) {
								Util.logout( TargetListTabs.this, true );
							}
						}
						this.message = response;
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
