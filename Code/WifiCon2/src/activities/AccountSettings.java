package activities;

import java.io.IOException;
import java.util.ArrayList;
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
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import comm.savagelook.android.UrlJsonAsyncTask;

public class AccountSettings extends Activity {
	
	private String REGISTER_API_ENDPOINT_URL;
	private String email;
	private String oldPassword;
	private String newPassword;
	private String newPasswordConfirmation;
	private String username;
	private ArrayList<String> itemList;
	
	@SuppressLint("NewApi")
	private void createActionBar() {
		try {
			final ActionBar actionBar = getActionBar();
			getActionBar().setIcon(
					new ColorDrawable( getResources().getColor(
							android.R.color.transparent ) ) );
			actionBar.setDisplayShowTitleEnabled( true );
			
			actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_LIST );
			this.itemList = new ArrayList<String>();
			
			this.itemList.add( Util.getMenu() );
			this.itemList.add( Util.getGetNewTasks() );
			this.itemList.add( Util.getViewTasks() );
			this.itemList.add( Util.getLogout() );
			
			ArrayAdapter<String> aAdpt = new ArrayAdapter<String>( this,
					android.R.layout.simple_list_item_1, android.R.id.text1,
					this.itemList );
			
			OnNavigationListener mOnNavigationListener = new OnNavigationListener() {
				// Get the same strings provided for the drop-down's
				// ArrayAdapter
				
				@Override
				public boolean onNavigationItemSelected(int position,
						long itemId) {
					
					switch (position) {
					
						case 1:
							final String MISS_ALL_TASKS_URL = Util
									.getServerAddress( AccountSettings.this )
									+ "miss_all.json";
							String HAS_ACTIVE_URL = Util
									.getServerAddress( AccountSettings.this )
									+ "has_active.json";
							Context context = getBaseContext();
							OnClickListener clickListener = new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
										case DialogInterface.BUTTON_POSITIVE:
											SendMissedForAll missAll = new SendMissedForAll(
													AccountSettings.this,
													Util.getNoInternet() );
											missAll.execute( MISS_ALL_TASKS_URL
													+ "?auth_token="
													+ Util.getUserInfo()
															.getString(
																	"AuthToken",
																	"" ) );
											dialog.dismiss();
											break;
										case DialogInterface.BUTTON_NEGATIVE:
											dialog.dismiss();
											break;
									}
								}
							};
							if (Util.isWifiEnabled( context )
									|| Util.isInternetAvailable( context )) {
								if (Util.getUserInfo().contains( "AuthToken" )) {
									CheckIfAnyTasks checkIfAny = new CheckIfAnyTasks(
											AccountSettings.this,
											Util.getNoInternet() );
									checkIfAny.setMessageLoading( Util
											.getPleaseWait() );
									JSONObject a = null;
									try {
										
										checkIfAny.execute( HAS_ACTIVE_URL
												+ "?auth_token="
												+ Util.getUserInfo().getString(
														"AuthToken", "" ) );
										
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
										Util.showPupupForConfirmation(
												clickListener,
												AccountSettings.this,
												Util.getGetNewTasks(),
												message
														+ "\n"
														+ Util.getWouldYouLike() );
									} else {
										Util.changeScreen( "GET_TASK",
												getApplicationContext() );
									}
									
								} else {
									Intent intent = new Intent( context,
											LogIn.class );
									intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
									context.startActivity( intent );
									
									Toast.makeText( context,
											Util.getSessionExpired(),
											Toast.LENGTH_LONG ).show();
								}
							} else {
								Toast.makeText( context, Util.getNoInternet(),
										Toast.LENGTH_LONG ).show();
							}
							break;
						case 2:
							Util.changeScreen( "TARGET_LIST_TABS",
									getBaseContext() );
							break;
						case 3:
							Util.logout( AccountSettings.this, true );
							break;
					}
					return true;
				}
			};
			
			actionBar.setListNavigationCallbacks( aAdpt, mOnNavigationListener );
			actionBar.setHomeButtonEnabled( true );
		} catch (Exception e) {
			Util.addException( this.getClass(), e, getBaseContext() );
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.account_settings );
		
		setLayoutStrings();
		REGISTER_API_ENDPOINT_URL = Util
				.getServerAddress( getApplicationContext() )
				+ "update_user.json";
		createActionBar();
		
		Button confirm = (Button) findViewById( R.id.confirm_change_editinfo );
		confirm.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				changeUserInfo();
			}
		} );
	}
	
	private void setLayoutStrings() {
		setTitle( Util.getAccSetTitle() );
		EditText email = (EditText) findViewById( R.id.email_editinfo );
		email.setHint( Util.getEmail() );
		EditText userName = (EditText) findViewById( R.id.username_editinfo );
		userName.setHint( Util.getUserName() );
		EditText oldPassword = (EditText) findViewById( R.id.old_password_editinfo );
		oldPassword.setHint( Util.getOldPassword() );
		EditText newPassword = (EditText) findViewById( R.id.new_password_editinfo );
		newPassword.setHint( Util.getNewPassword() );
		EditText confirmPassword = (EditText) findViewById( R.id.confirm_new_password_editinfo );
		confirmPassword.setHint( Util.getConfirmPassword() );
		Button confirmChange = (Button) findViewById( R.id.confirm_change_editinfo );
		confirmChange.setText( Util.getConfirmChange() );
	}
	
	public void changeUserInfo() {
		
		if (validNewUserInfo()) {
			ChangeUserInfo changeInfo = new ChangeUserInfo(
					AccountSettings.this, Util.getNoInternetToChangeInfo() );
			changeInfo.setMessageLoading( Util.getUpdatingInformation() );
			changeInfo.execute( REGISTER_API_ENDPOINT_URL + "?auth_token="
					+ Util.getUserInfo().getString( "AuthToken", "" ) );
		}
	}
	
	private Boolean validNewUserInfo() {
		EditText userEmailField = (EditText) findViewById( R.id.email_editinfo );
		email = userEmailField.getText().toString();
		
		EditText userNameField = (EditText) findViewById( R.id.username_editinfo );
		username = userNameField.getText().toString();
		
		EditText userOldPasswordField = (EditText) findViewById( R.id.old_password_editinfo );
		oldPassword = userOldPasswordField.getText().toString();
		
		EditText userPasswordField = (EditText) findViewById( R.id.new_password_editinfo );
		newPassword = userPasswordField.getText().toString();
		
		EditText userPasswordConfirmationField = (EditText) findViewById( R.id.confirm_new_password_editinfo );
		newPasswordConfirmation = userPasswordConfirmationField.getText()
				.toString();
		
		if (email.length() == 0 || oldPassword.length() == 0
				|| newPassword.length() == 0
				|| newPasswordConfirmation.length() == 0) {
			// input fields are empty
			Toast.makeText( this, Util.getPleaseCompleteFields(),
					Toast.LENGTH_LONG ).show();
			return false;
		} else {
			if (!newPassword.equals( newPasswordConfirmation )) {
				// password doesn't match confirmation
				Toast.makeText( this, Util.getPasswordNoMatch(),
						Toast.LENGTH_LONG ).show();
				return false;
			} else {
				return true;
			}
		}
	}
	
	private class ChangeUserInfo extends UrlJsonAsyncTask {
		public ChangeUserInfo(Context context, String errorMessage) {
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
				
				try {
					try {
						// setup the returned values in case
						// something goes wrong
						json.put( "success", false );
						json.put( "info", Util.getErrorMessage() );
						holder.put( "ip", Util.getIp( AccountSettings.this ) );
						holder.put( "language", Util.getCurrentLanguadge() );
						// add the users's info to the post params
						userObj.put( "email", email );
						userObj.put( "username", username );
						userObj.put( "oldPassword", oldPassword );
						userObj.put( "newPassword", newPassword );
						userObj.put( "passwordConfirmation",
								newPasswordConfirmation );
						holder.put( "user", userObj );
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
					if (json.getBoolean( "success" )) {
						// everything is ok
						SharedPreferences.Editor editor = Util.getUserInfo()
								.edit();
						// save the returned auth_token into
						// the SharedPreferences
						editor.putString(
								"AuthToken",
								json.getJSONObject( "data" ).getString(
										"auth_token" ) );
						editor.putString( "Username",
								json.getJSONObject( "data" ).getString( "name" ) );
						editor.commit();
						
						// launch the HomeActivity and close this one
						Intent intent = new Intent( getApplicationContext(),
								GetTask.class );
						startActivity( intent );
						finish();
						Toast.makeText( context, json.getString( "info" ),
								Toast.LENGTH_LONG ).show();
					} else {
						if (json.getString( "info" ).equals(
								Util.getTokenExpired() )) {
							Util.logout( AccountSettings.this, false );
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
					Util.addException( this.getClass(), e, context );
				} finally {
					super.onPostExecute( json );
				}
		}
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
						holder.put( "ip", Util.getIp( AccountSettings.this ) );
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
								Util.logout( AccountSettings.this, true );
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
						holder.put( "ip", Util.getIp( AccountSettings.this ) );
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
								Util.logout( AccountSettings.this, true );
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
