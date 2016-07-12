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
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import async.tasks.ChangeWifiInfo;

import comm.savagelook.android.UrlJsonAsyncTask;

public class ManageNetworks extends Activity {
	private String TASKS_URL;
	private String UPDATE_WIFI_URL;
	private ImageButton addNewButton;
	private Dialog popupView;
	TableLayout tableView;
	private final Integer ITEM_PER_ROW = 4;
	private EditText name;
	private EditText IP;
	private EditText pass;
	private CheckBox active;
	
	private int position;
	
	private Boolean popupActive;
	private static Boolean isRecivingTasks;
	private List<WifiSpot> wifiSpotsArray;
	
	public ManageNetworks() {
		mn = this;
	}
	
	private int getPosition() {
		return position;
	}
	
	private void setPosition(int position) {
		this.position = position;
	}
	
	private void setWifiSpotsArray(List<WifiSpot> newWifiSpotsArray) {
		for (int j = 0; j < newWifiSpotsArray.size(); j++) {
			WifiSpot spot = newWifiSpotsArray.get( j );
			this.wifiSpotsArray.add( spot );
		}
	}
	
	@Override
	protected void onResume() {
		invalidateOptionsMenu();
		super.onResume();
	}
	
	@SuppressLint({ "InlinedApi", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate( savedInstanceState );
			
			// getActionBar().setIcon(
			// new ColorDrawable(getResources().getColor(
			// android.R.color.transparent)));
			popupActive = false;
			spotsCount = 0;
			currentPage = 0;
			isRecivingTasks = false;
			isSearching = false;
			setContentView( R.layout.manage_networks );
			
			// setTitle(Util.getManageNetworksTitle());
			TASKS_URL = Util.getServerAddress( getApplicationContext() )
					+ "all_spots.json";
			UPDATE_WIFI_URL = Util.getServerAddress( getApplicationContext() )
					+ "update_spot.json";
			wifiSpotsArray = new ArrayList<WifiSpot>();
			
			manageNetworksFill();
			EditText searchText = (EditText) findViewById( R.id.search_edittext_manage );
			searchText.setHint( Util.getName() );
			addNewButton = (ImageButton) findViewById( R.id.addnew_button_manage );
			// addNewButton.setText( Util.getCreateNew() );
			// addNewButton.setText( "" );
			// addNewButton
			// .setBackgroundResource( android.R.drawable.ic_input_add );
			// addNewButton.setX(50);
			addNewButton.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if (Util.isWifiEnabled( getApplicationContext() )) {
						changeScreen( "CREATE" );
					} else {
						Toast.makeText( getApplicationContext(),
								Util.getConntectToWifi(), Toast.LENGTH_LONG )
								.show();
					}
				}
			} );
			ImageButton searchButton = (ImageButton) findViewById( R.id.search_button_manage );
			// searchButton.setText( Util.getSearchStr() );
			// searchButton.setText( "" );
			// searchButton
			// .setBackgroundResource(
			// android.R.drawable.ic_search_category_default );
			searchButton.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					isSearching = true;
					EditText searchInput = (EditText) findViewById( R.id.search_edittext_manage );
					String name = searchInput.getText().toString();
					currentPage = 0;
					searchForSpot( name, true );
				}
			} );
			
			ImageButton cancelButton = (ImageButton) findViewById( R.id.cancel_search_manage );
			cancelButton.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					isSearching = false;
					((EditText) findViewById( R.id.search_edittext_manage ))
							.setText( "" );
					tableView.removeAllViews();
					currentPage = 0;
					generateTable( context, false, wifiSpotsArray );
					
				}
			} );
		} catch (Exception e) {
			Util.addException( this.getClass(), e, getBaseContext() );
		}
	}
	
	private static List<WifiSpot> foundList;
	
	private static void setFoundResults(List<WifiSpot> list) {
		foundList = list;
	}
	
	private void searchForSpot(String name, Boolean forSearch) {
		WifiSpot spot;
		List<WifiSpot> foundResults = new ArrayList<WifiSpot>();
		for (int i = 0; i < wifiSpotsArray.size(); i++) {
			spot = wifiSpotsArray.get( i );
			if (spot.getName().toLowerCase().contains( name.toLowerCase() )) {
				foundResults.add( spot );
			}
		}
		setFoundResults( foundResults );
		tableView.removeAllViews();
		Toast.makeText( getApplicationContext(),
				"Spots found: " + foundResults.size(), Toast.LENGTH_LONG )
				.show();
		generateTable( context, forSearch, foundResults );
	}
	
	public void changePage(View paramView) {
		final Dialog changePageDialog = new Dialog( this );
		changePageDialog.setTitle( Util.getPickNewPage() );
		changePageDialog.setContentView( R.layout.change_page_picker );
		Button changePageButton = (Button) changePageDialog
				.findViewById( R.id.confirm_page );
		changePageButton.setText( Util.getConfirm() );
		Button dismissButton = (Button) changePageDialog
				.findViewById( R.id.cancel_page );
		dismissButton.setText( Util.getDismiss() );
		final NumberPicker localNumberPicker = (NumberPicker) changePageDialog
				.findViewById( R.id.numberPicker1 );
		localNumberPicker.setMinValue( 1 );
		localNumberPicker.setWrapSelectorWheel( false );
		changePageButton.setOnClickListener( new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				
				ManageNetworks.this.tableView.removeAllViews();
				ManageNetworks.this.manageNetworksFill();
				changePageDialog.dismiss();
			}
		} );
		dismissButton.setOnClickListener( new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				changePageDialog.dismiss();
			}
		} );
		changePageDialog.show();
	}
	
	private void manageNetworksFill() {
		
		if (Util.isWifiEnabled( getApplicationContext() )
				|| Util.isInternetAvailable( getApplicationContext() )) {
			if (Util.getUserInfo().contains( "AuthToken" )) {
				isRecivingTasks = true;
				loadSpotsFromAPI( TASKS_URL );
			} else {
				Intent intent = new Intent( ManageNetworks.this, LogIn.class );
				startActivityForResult( intent, 0 );
				finish();
				Toast.makeText( getApplicationContext(),
						Util.getSessionExpired(), Toast.LENGTH_LONG ).show();
			}
		} else {
			Toast.makeText( getApplicationContext(),
					Util.getNoInternetToManage(), Toast.LENGTH_LONG ).show();
			Toast.makeText( getApplicationContext(),
					Util.getLoadingOfflineSpots(), Toast.LENGTH_LONG ).show();
			readSpots();
			if (wifiSpotsArray.size() == 0) {
				Toast.makeText( getApplicationContext(),
						Util.getNoSpotsFound(), Toast.LENGTH_LONG ).show();
				Toast.makeText( getApplicationContext(),
						Util.getPleaseConnectToInternetForSpots(),
						Toast.LENGTH_LONG ).show();
			} else {
				context = getBaseContext();
				generateTable( context, false, wifiSpotsArray );
			}
		}
	}
	
	public Context context;
	
	private void changeScreen(String intentName) {
		Intent changeScreen = new Intent( "com.example.wificon." + intentName );
		if (!popupActive) {
			startActivity( changeScreen );
			
		} else {
			// pw.dismiss();
			popupActive = false;
			startActivity( changeScreen );
		}
	}
	
	public void onButtonInPopupCancel(View target) {
		popupActive = false;
		popupView.dismiss();
	}
	
	public void onButtonInPopupConfirm(View target) {
		
		wifiUpdate();
	}
	
	private void wifiUpdate() {
		name = (EditText) popupView.findViewById( R.id.new_name_pop );
		IP = (EditText) popupView.findViewById( R.id.new_id_pop );
		pass = (EditText) popupView.findViewById( R.id.new_pass_pop );
		active = (CheckBox) popupView.findViewById( R.id.new_spot_active_pop );
		String nameText = name.getText().toString();
		String ipText = IP.getText().toString();
		String passText = pass.getText().toString();
		Boolean activeCheck = active.isChecked();
		
		setPosition( Integer.parseInt( ((TextView) popupView
				.findViewById( R.id.id_pop )).getText().toString()
				.replaceAll( "Id: ", "" ) ) );
		WifiSpot spot = null;
		int index = 0;
		for (int i = 1; i <= tableView.getChildCount(); i++) {
			TableRow row = (TableRow) tableView.getChildAt( i );
			String id = ((TextView) row.getChildAt( 0 )).getText().toString();
			if (Integer.parseInt( id ) == getPosition()) {
				index = i;
				if (isSearching) {
					
					spot = foundList.get( i - 1 );
					break;
				} else {
					spot = wifiSpotsArray.get( i - 1 );
					break;
				}
				
			}
		}
		TableRow row = (TableRow) tableView.getChildAt( index );
		TextView id = (TextView) row.getChildAt( 0 );
		TextView address = (TextView) row.getChildAt( 3 );
		if (!activeCheck) {
			id.setTextColor( Color.parseColor( "#FF0000" ) );
			address.setTextColor( Color.parseColor( "#FF0000" ) );
		} else {
			id.setTextColor( Color.rgb( 50, 100, 200 ) );
			address.setTextColor( Color.rgb( 50, 100, 200 ) );
		}
		setData( row, 1, nameText, activeCheck );
		setData( row, 2, passText, activeCheck );
		// setData(getPosition(), 3, IP);
		setPosition( Integer.parseInt( spot.getFakeId() ) );
		spot.setIsActive( activeCheck );
		spot.setName( nameText );
		spot.setPassword( passText );
		spot.setIp( ipText );
		
		wifiSpotsArray.set( getPosition() - 1, spot );
		ChangeWifiInfo updateInfo = new ChangeWifiInfo( ManageNetworks.this,
				Util.getNoInternetToUpdate(), spot );
		updateInfo.setMessageLoading( Util.getUpdatingInformation() );
		updateInfo.execute( UPDATE_WIFI_URL + "?auth_token="
				+ Util.getUserInfo().getString( "AuthToken", "" ) );
		popupView.dismiss();
		popupActive = false;
	}
	
	private void setData(TableRow row, int pos, String fromText,
			Boolean isChecked) {
		TextView data = (TextView) row.getChildAt( pos );
		data.setText( fromText );
		if (!isChecked) {
			data.setTextColor( Color.parseColor( "#FF0000" ) );
		} else {
			data.setTextColor( Color.rgb( 50, 100, 200 ) );
		}
	}
	
	private void loadSpotsFromAPI(String url) {
		context = ManageNetworks.this;
		GetSpots getSpots = new GetSpots( context,
				Util.getPleaseConnectToInternetForSpots() );
		getSpots.setMessageLoading( Util.getLoadingWiFiSpots() );
		getSpots.execute( url + "?auth_token="
				+ Util.getUserInfo().getString( "AuthToken", "" ) );
	}
	
	private static int currentPage;
	
	public class GetSpots extends UrlJsonAsyncTask {
		public GetSpots(Context context, String errorMessage) {
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
				// final String FAILURE_URL =
				// "http://10.0.2.2:3000/fail.json";
				try {
					try {
						
						// setup the returned values in case
						// something goes wrong
						json.put( "success", false );
						json.put( "info", Util.getErrorMessage() );
						holder.put( "ip", Util.getIp( ManageNetworks.this ) );
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
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			if (willExecuteJson)
				try {
					String response = json.getString( "info" );
					if (json.getBoolean( "success" )) {
						
						JSONArray jsonSpots = json.getJSONArray( "data" );
						int length = jsonSpots.length();
						wifiSpotsArray.clear();
						List<WifiSpot> newSpots = new ArrayList<WifiSpot>();
						for (int i = 0; i < length; i++) {
							WifiSpot spot = new WifiSpot(
									jsonSpots.getJSONObject( i ),
									1 + (spotsCount++), false, context );
							newSpots.add( spot );
						}
						Toast.makeText( context, response, Toast.LENGTH_LONG )
								.show();
						try {
							setWifiSpotsArray( newSpots );
							isRecivingTasks = false;
						} catch (Exception e) {
							Util.addException( this.getClass(), e, context );
						}
						
						try {
							writeSpots();
						} catch (Exception e) {
							Util.addException( this.getClass(), e, context );
						}
						
						try {
							generateTable( context, false, wifiSpotsArray );
						} catch (Exception e) {
							Util.addException( this.getClass(), e, context );
						}
						
					} else {
						if (response.equals( Util.getTokenExpired() )) {
							Util.logout( ManageNetworks.this, false );
						}
					}
				} catch (Exception e) {
					try {
						Util.addException( this.getClass(), e, context );
					} catch (NullPointerException e2) {
						Util.addException( this.getClass(), e2, context );
					}
				} finally {
					super.onPostExecute( json );
				}
		}
	}
	
	int spotsCount;
	static int openedPage;
	
	private void generateTable(final Context context, Boolean forSearch,
			final List<WifiSpot> listOfSpots) {
		try {
			int rowsCount = listOfSpots.size();
			int rowItems = rowsCount * (ITEM_PER_ROW + 1);
			if (rowItems == 0) {
				rowItems = ITEM_PER_ROW + 1;
			}
			TextView[] tView = new TextView[rowItems];
			int colElementCounter = 0;
			
			tableView = (TableLayout) findViewById( R.id.manage_table );
			final TableRow[] tableRow = new TableRow[rowsCount + 1];
			tableRow[0] = new TableRow( context );
			if (currentPage == 0) {
				for (int i = 0; i <= ITEM_PER_ROW; i++) {
					tView[i] = new TextView( context );
					
					String text = "";
					colElementCounter += 1;
					switch (colElementCounter) {
						case 1:
							text = "No";
							break;
						case 2:
							text = Util.getName();
							break;
						case 3:
							text = Util.getPassword();
							break;
						case 4:
							text = Util.getAddress();
							break;
						case 5:
							tView[i].setId( 1 );
							text = "";
							break;
						default:
							text = Util.getErrorLoadingSpot();
							break;
					}
					
					tView[i].setText( text );
					tView[i].setTextSize( 12 );
					
					tView[i].setTextColor( Color.rgb( 50, 100, 100 ) );
					tView[i].setGravity( 0x11 );
					if (i == 0) {
						tView[i].setWidth( 80 );
					} else {
						tView[i].setWidth( 160 );
						tView[i].setHeight( 50 );
					}
					tableRow[0].addView( tView[i] );
				}
				tableView.addView( tableRow[0] );
			}
			int maxElements = 0;
			maxElements = (currentPage + 1) * 20 - 1;
			if (rowsCount - 1 < maxElements) {
				maxElements = rowsCount - 1;
			}
			for (int j = 20 * currentPage; j <= maxElements; j++) {
				tableRow[j] = new TableRow( context );
				colElementCounter = 0;
				
				for (int i = 0; i <= ITEM_PER_ROW; i++) {
					
					WifiSpot wifiSpot = listOfSpots.get( j );
					
					if (i == ITEM_PER_ROW) { // j != 0 &&
						ImageButton edit = new ImageButton( context );
						// edit.setText( Util.getEdit() );
						// edit.setText( "" );
						// edit.setBackgroundResource(
						// android.R.drawable.ic_menu_edit );
						// edit.setTextSize( 12 );
						edit.setImageResource( android.R.drawable.ic_menu_edit );
						edit.setId( Integer.parseInt( wifiSpot.getFakeId() ) );
						edit.setOnClickListener( new Button.OnClickListener() {
							
							@SuppressWarnings("deprecation")
							@SuppressLint("InlinedApi")
							@Override
							public void onClick(View arg0) {
								popupActive = true;
								popupView = new Dialog( ManageNetworks.this,
										R.style.PauseDialog );
								
								popupView
										.setContentView( R.layout.popup_window );
								popupView.setTitle( Util.getEditingPopup() );
								ImageButton confirm = (ImageButton) (popupView
										.findViewById( R.id.confirm_button_popup ));
								// confirm.setBackgroundResource(
								// R.drawable.check_mark );
								// confirm.setText( "" );
								confirm.setOnClickListener( new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										if (Util.hasInternet( context )) {
											
											onButtonInPopupConfirm( v );
										} else {
											Toast.makeText( context,
													Util.getNoInternet(),
													Toast.LENGTH_LONG ).show();
											popupView.dismiss();
											popupActive = false;
											
										}
										
									}
								} );
								ImageButton dismiss = (ImageButton) (popupView
										.findViewById( R.id.dismiss_button_popup ));
								// dismiss.setBackgroundResource(
								// android.R.drawable.ic_delete );
								// dismiss.setText( "" );
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
								int pos = arg0.getId();
								
								Integer theId = 0;
								for (WifiSpot spot : wifiSpotsArray) {
									if (Integer.parseInt( spot.getFakeId() ) == pos) {
										theId = pos;
										break;
										
									}
								}
								
								WifiSpot spot = wifiSpotsArray.get( theId - 1 );
								idNum.setText( "Id: " + (spot.getFakeId()) );
								name.setText( (spot.getName()) );
								pass.setText( (spot.getPassword()) );
								IP.setText( spot.getIp() );
								checkbox.setChecked( spot.getIsActive() );
								popupView.getWindow().setLayout(
										LayoutParams.FILL_PARENT,
										LayoutParams.WRAP_CONTENT );
								popupView.show();
								
							}
						} );
						tableRow[j].addView( edit );
						colElementCounter += 1;
					}
					
					else {
						tView[i] = new TextView( context );
						String text = "";
						colElementCounter += 1;
						switch (colElementCounter) {
							case 1:
								text = wifiSpot.getFakeId();
								break;
							case 2:
								text = wifiSpot.getName();
								break;
							case 3:
								text = wifiSpot.getPassword();
								break;
							case 4:
								text = wifiSpot.getAddress();
								break;
							default:
								text = Util.getErrorLoadingSpot();
								break;
						}
						
						tView[i].setText( text );
						tView[i].setTextSize( 12 );
						if (wifiSpot.getIsActive()) {
							tView[i].setTextColor( Color.rgb( 50, 100, 200 ) );
						} else {
							tView[i].setTextColor( Color.parseColor( "#FF0000" ) );
						}
						tView[i].setGravity( 0x11 );
						if (i == 0) {
							tView[i].setWidth( 80 );
						} else {
							tView[i].setWidth( 160 );
							tView[i].setHeight( 60 );
						}
						tableRow[j].addView( tView[i] );
					}
				}
				tableView.addView( tableRow[j] );
			}
			tableView.setColumnShrinkable( 1, true );
		} catch (Exception e) {
			Util.addException( this.getClass(), e, context );
			Toast.makeText( getApplicationContext(),
					e.getClass().getName() + " " + e.getMessage(),
					Toast.LENGTH_LONG ).show();
		}
	}
	
	private void readSpots() {
		// The name of the file to open.
		this.wifiSpotsArray.clear();
		currentPage = 0;
		List<WifiSpot> readSpots = new ArrayList<WifiSpot>();
		readSpots = Util.datasource.findAllWifiSpots();
		setWifiSpotsArray( readSpots );
	}
	
	private void writeSpots() throws InterruptedException {
		// The name of the file to open.
		
		final int spots = wifiSpotsArray.size();
		Util.datasource.clearTable( "spots" );
		Thread writeTasksThread = new Thread( new Runnable() {
			
			@Override
			public void run() {
				
				for (int i = 0; i < spots; i++) {
					final WifiSpot spot = wifiSpotsArray.get( i );
					
					Util.datasource.createWifiSpot( spot );
				}
			}
		} );
		writeTasksThread.start();
		
	}
	
	private static Boolean isSearching;
	
	public static void getNextPageItems() {
		if (!isRecivingTasks && !isSearching) {
			currentPage++;
			mn.generateTable( mn.context, false, mn.wifiSpotsArray );
		} else if (isSearching) {
			currentPage++;
			mn.generateTable( mn.context, true, mn.getFoundList() );
			
		}
	}
	
	public List<WifiSpot> getFoundList() {
		return foundList;
	}
	
	public static ManageNetworks mn;
	
}
