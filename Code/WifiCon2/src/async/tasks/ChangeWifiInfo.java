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
import util.WifiSpot;
import android.content.Context;
import android.widget.Toast;

import comm.savagelook.android.UrlJsonAsyncTask;

public class ChangeWifiInfo extends UrlJsonAsyncTask {
	String name;
	String pass;
	String ip;
	String active;
	WifiSpot spot;
	
	public ChangeWifiInfo(Context context, String errorMessage, WifiSpot spot) {
		
		super( context, errorMessage );
		this.name = spot.getName();
		this.pass = spot.getPassword();
		this.ip = spot.getIp();
		this.active = spot.getIsActive() + "";
		this.spot = spot;
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
					holder.put( "ip", Util.getIp( context ) );
					holder.put( "language", Util.getCurrentLanguadge() );
					// add the users's info to the post params
					userObj.put( "id", spot.getId() );
					userObj.put( "name", name );
					userObj.put( "pass", pass );
					userObj.put( "ip", ip );
					userObj.put( "active", active );
					holder.put( "wifi_spot", userObj );
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
				if (json.getBoolean( "success" )) {
					Toast.makeText( context, json.getString( "info" ),
							Toast.LENGTH_LONG ).show();
					Util.datasource.updateSpot( spot );
				} else {
					if (response.equals( Util.getSessionExpired() )) {
						Util.logout( context, true );
					} else {
						Toast.makeText( context, response, Toast.LENGTH_LONG )
								.show();
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
