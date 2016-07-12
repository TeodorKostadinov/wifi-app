package activities;

import java.io.IOException;

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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import comm.savagelook.android.UrlJsonAsyncTask;

public class LogIn extends Activity {
	private String LOGIN_API_ENDPOINT_URL;
	private String mUserEmail;
	private String mUserPassword;
	
	EditText username;
	EditText password;
	Button login;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate( savedInstanceState );
			Util.setUserInfo( getSharedPreferences( "CurrentUser", MODE_PRIVATE ) );
			
			SharedPreferences user = Util.getUserInfo();
			try {
				Util.setLanguadgeStrings( this );
			} catch (Exception e) {
				Toast.makeText( getBaseContext(),
						"Problem with app... contact support",
						Toast.LENGTH_LONG ).show();
				// StackTraceElement[] elms = e.getStackTrace();
				// String content = "";
				// for (StackTraceElement stackTraceElement : elms) {
				// content += stackTraceElement.getClassName().toString()
				// + ".";
				// content += stackTraceElement.getFileName().toString();
				// content += "("
				// + stackTraceElement.getMethodName().toString()
				// + ":";
				// content += stackTraceElement.getLineNumber() + ") ";
				// }
				// Toast.makeText( getBaseContext(), content, 30 ).show();
			}
			
			if (!user.getBoolean( "RememberMe", false )) {
				SharedPreferences.Editor editor = user.edit();
				editor.remove( "AuthToken" );
				editor.commit();
			}
			if (user.getString( "AuthToken", "nope" ).equals( "nope" )) {
				setContentView( R.layout.log_in );
				setLayoutStrings();
				getActionBar().setIcon(
						new ColorDrawable( getResources().getColor(
								android.R.color.transparent ) ) );
				LOGIN_API_ENDPOINT_URL = Util
						.getServerAddress( getApplicationContext() )
						+ "session.json";
				username = (EditText) findViewById( R.id.username_login );
				String lastLoggedIn = user.getString( "LastLoggedIn", "" );
				if (lastLoggedIn != null) {
					username.setText( lastLoggedIn );
				}
				password = (EditText) findViewById( R.id.password_login );
				login = (Button) findViewById( R.id.login_button );
				login.setOnClickListener( new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						SharedPreferences.Editor editor = Util.getUserInfo()
								.edit();
						// save the returned auth_token into
						// the SharedPreferences
						CheckBox checkbox = (CheckBox) findViewById( R.id.remember_me );
						Boolean checked = checkbox.isChecked();
						editor.putBoolean( "RememberMe", checked );
						editor.commit();
						
						EditText userEmailField = username;
						mUserEmail = userEmailField.getText().toString();
						EditText userPasswordField = password;
						mUserPassword = userPasswordField.getText().toString();
						
						if (mUserEmail.length() == 0
								|| mUserPassword.length() == 0) {
							// input fields are empty
							Toast.makeText( LogIn.this,
									Util.getPleaseCompleteFields(),
									Toast.LENGTH_LONG ).show();
							return;
						} else {
							if (Util.isWifiEnabled( getBaseContext() )
									|| Util.isInternetAvailable( getBaseContext() )) {
								LoginTask loginTask = new LoginTask(
										LogIn.this, Util.getNoInternetToLogin() );
								loginTask.execute( LOGIN_API_ENDPOINT_URL );
							} else {
								Toast.makeText( LogIn.this,
										Util.getNoInternetToLogin(),
										Toast.LENGTH_LONG ).show();
							}
						}
					}
				} );
				Button register = (Button) findViewById( R.id.register );
				register.setOnClickListener( new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// No account, load new account view
						Intent intent = new Intent( LogIn.this, Register.class );
						startActivityForResult( intent, 0 );
					}
				} );
				
			} else {
				Util.changeScreen( "TARGET_LIST_TABS", this );
				finish();
			}
		} catch (Exception e) {
			Util.addException( this.getClass(), e, getBaseContext() );
			e.printStackTrace();
		}
		
	}
	
	private void setLayoutStrings() {
		setTitle( Util.getLogInTitle() );
		EditText email = (EditText) findViewById( R.id.username_login );
		email.setHint( Util.getEmail() );
		CheckBox rememberMe = (CheckBox) findViewById( R.id.remember_me );
		rememberMe.setText( Util.getRememberMe() );
		EditText password = (EditText) findViewById( R.id.password_login );
		password.setHint( Util.getPassword() );
		TextView appInfo = (TextView) findViewById( R.id.app_info );
		appInfo.setText( Util.getAppInfo() );
		Button register = (Button) findViewById( R.id.register );
		register.setText( Util.getRegister() );
		Button login = (Button) findViewById( R.id.login_button );
		login.setText( Util.getLogin() );
	}
	
	@Override
	public void onBackPressed() {
		Intent startMain = new Intent( Intent.ACTION_MAIN );
		startMain.addCategory( Intent.CATEGORY_HOME );
		startMain.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		startActivity( startMain );
		finish();
	}
	
	// public String jsonResponse;
	private class LoginTask extends UrlJsonAsyncTask {
		
		public LoginTask(Context context, String errorMessage) {
			super( context, errorMessage );
			progressDialog = new ProgressDialog( context );
		}
		
		final ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			
			progressDialog.setTitle( Util.getLogInTitle() );
			progressDialog.setMessage( Util.getLogginIn() );
			progressDialog.setCancelable( false );
			progressDialog.setIndeterminate( true );
			progressDialog.setCanceledOnTouchOutside( true );
			progressDialog
					.setOnCancelListener( new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface arg0) {
							progressDialog.cancel();
						}
					} );
			progressDialog.show();
		}
		
		@Override
		protected JSONObject doInBackground(String... urls) {
			
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost( urls[0] );
			JSONObject holder = new JSONObject();
			JSONObject userObj = new JSONObject();
			String response = null;
			JSONObject json = new JSONObject();
			// final String FAILURE_URL = "http://10.0.2.2:3000/fail.json";
			try {
				try {
					// setup the returned values in case
					// something goes wrong
					json.put( "success", false );
					json.put( "info", Util.getErrorMessage() );
					holder.put( "ip", Util.getIp( LogIn.this ) );
					holder.put( "language", Util.getCurrentLanguadge() );
					// add the user email and password to
					// the params
					userObj.put( "email", mUserEmail );
					userObj.put( "password", mUserPassword );
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
		
		@Override
		protected void onPostExecute(JSONObject json) {
			
			try {
				if (json.getBoolean( "success" )) {
					// everything is ok
					SharedPreferences.Editor editor = Util.getUserInfo().edit();
					// save the returned auth_token into
					// the SharedPreferences
					editor.putString( "AuthToken", json.getJSONObject( "data" )
							.getString( "auth_token" ) );
					editor.putString( "LastLoggedIn", mUserEmail );
					editor.putString( "Username", json.getJSONObject( "data" )
							.getString( "name" ) );
					editor.putString( "UserLevel", json.getJSONObject( "data" )
							.getString( "level" ) );
					editor.commit();
					
					// launch the HomeActivity and close this one
					Intent intent = new Intent( getApplicationContext(),
							TargetListTabs.class );
					progressDialog.dismiss();
					startActivity( intent );
					finish();
					Toast.makeText( context, json.getString( "info" ),
							Toast.LENGTH_LONG ).show();
				} else {
					if (json.getString( "info" )
							.equals( Util.getTokenExpired() )) {
						Util.logout( LogIn.this, false );
					} else {
						Toast.makeText( context, json.getString( "info" ),
								Toast.LENGTH_LONG ).show();
						progressDialog.dismiss();
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
