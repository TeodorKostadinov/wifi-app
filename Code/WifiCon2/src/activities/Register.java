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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import comm.savagelook.android.UrlJsonAsyncTask;

public class Register extends Activity {
	private String REGISTER_API_ENDPOINT_URL;
	private String registerEmail;
	private String registerUserName;
	private String registerPassword;
	private String registerConfirmationP;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			
			super.onCreate( savedInstanceState );
			setContentView( R.layout.register );
			setLayoutStrings();
			getActionBar().setIcon(
					new ColorDrawable( getResources().getColor(
							android.R.color.transparent ) ) );
			REGISTER_API_ENDPOINT_URL = Util
					.getServerAddress( getApplicationContext() )
					+ "registration";
		} catch (Exception e) {
			Util.addException( this.getClass(), e, getBaseContext() );
		}
	}
	
	private void setLayoutStrings() {
		setTitle( Util.getRegisterTitle() );
		EditText username = (EditText) findViewById( R.id.username_register );
		username.setHint( Util.getUserName() );
		EditText email = (EditText) findViewById( R.id.email_register );
		email.setHint( Util.getEmail() );
		EditText password = (EditText) findViewById( R.id.password_register );
		password.setHint( Util.getPassword() );
		TextView appInfo = (TextView) findViewById( R.id.name_popup );
		appInfo.setText( Util.getAppInfo() );
		EditText confirmPassword = (EditText) findViewById( R.id.confirm_pass_reg );
		confirmPassword.setHint( Util.getConfirmPassword() );
		Button register = (Button) findViewById( R.id.register_new );
		register.setText( Util.getRegister() );
	}
	
	public void goToHome(View v) {
		finish();
	}
	
	private Boolean setAndCheckRegisterInfo() {
		EditText userEmailField = (EditText) findViewById( R.id.email_register );
		registerEmail = userEmailField.getText().toString();
		EditText userNameField = (EditText) findViewById( R.id.username_register );
		registerUserName = userNameField.getText().toString();
		EditText userPasswordField = (EditText) findViewById( R.id.password_register );
		registerPassword = userPasswordField.getText().toString();
		EditText userPasswordConfirmationField = (EditText) findViewById( R.id.confirm_pass_reg );
		registerConfirmationP = userPasswordConfirmationField.getText()
				.toString();
		
		if (registerEmail.length() == 0 || registerUserName.length() == 0
				|| registerPassword.length() == 0
				|| registerConfirmationP.length() == 0) {
			// input fields are empty
			Toast.makeText( this, Util.getPleaseCompleteFields(),
					Toast.LENGTH_LONG ).show();
			return false;
		} else {
			if (!registerPassword.equals( registerConfirmationP )) {
				// password doesn't match confirmation
				Toast.makeText( this, Util.getPasswordNoMatch(),
						Toast.LENGTH_LONG ).show();
				return false;
			}
		}
		return true;
	}
	
	public void registerNewAccount(View button) {
		
		if (setAndCheckRegisterInfo()) {
			// everything is ok!
			RegisterUser registerUser = new RegisterUser( Register.this,
					Util.getNoInternetToRegister() );
			registerUser.setMessageLoading( Util.getRegisterNewAccount() );
			registerUser.execute( REGISTER_API_ENDPOINT_URL );
		}
		
	}
	
	private class RegisterUser extends UrlJsonAsyncTask {
		public RegisterUser(Context context, String errorMessage) {
			super( context, errorMessage );
		}
		
		@Override
		protected JSONObject doInBackground(String... urls) {
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
					holder.put( "ip", Util.getIp( Register.this ) );
					holder.put( "language", Util.getCurrentLanguadge() );
					// add the users's info to the post params
					userObj.put( "email", registerEmail );
					userObj.put( "name", registerUserName );
					userObj.put( "password", registerPassword );
					userObj.put( "password_confirmation", registerConfirmationP );
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
					editor.commit();
					
					// launch the HomeActivity and close this one
					Intent intent = new Intent( getApplicationContext(),
							LogIn.class );
					startActivity( intent );
					finish();
				}
				Toast.makeText( context, json.getString( "info" ),
						Toast.LENGTH_LONG ).show();
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
