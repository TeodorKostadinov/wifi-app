package async.tasks;

import java.io.IOException;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import util.Util;
import activities.LogIn;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import comm.savagelook.android.UrlJsonAsyncTask;

public class Logout extends UrlJsonAsyncTask {
	
	public Logout(Context context, String errorMessage) {
		
		super( context, errorMessage );
		
	}
	
	@Override
	protected JSONObject doInBackground(String... urls) {
		if (willExecuteJson) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost( urls[0] );
			String response = null;
			JSONObject json = new JSONObject();
			JSONObject holder = new JSONObject();
			
			try {
				try {
					// setup the returned values in case
					// something goes wrong
					json.put( "success", false );
					json.put( "info", Util.getErrorMessage() );
					holder.put( "ip", Util.getIp( context ) );
					holder.put( "language", Util.getCurrentLanguadge() );
					StringEntity se = new StringEntity( holder.toString() );
					post.setEntity( se );
					
					// add the users's info to the post params
					
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
				SharedPreferences.Editor editor = Util.getUserInfo().edit();
				if (json.getBoolean( "success" )) {
					editor.remove( "AuthToken" );
					editor.remove( "RememberMe" );
					editor.commit();
					Intent intent = new Intent( context, LogIn.class );
					intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
					context.startActivity( intent );
				} else {
					if (response.equals( Util.getTokenExpired() )) {
						
						editor.remove( "AuthToken" );
						editor.remove( "RememberMe" );
						editor.commit();
						Intent intent = new Intent( context, LogIn.class );
						intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
						context.startActivity( intent );
					}
				}
				Toast.makeText( context, response, Toast.LENGTH_LONG ).show();
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