package activities;

import seo.extra.wifi_analyzor.R;
import util.Util;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebBrower extends Activity {
	boolean loadingFinished = true;
	boolean redirect = false;
	
	public boolean hasFinishedLoading() {
		return loadingFinished;
	}
	
	@SuppressWarnings({ "deprecation", "static-access" })
	@SuppressLint({ "NewApi", "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate( savedInstanceState );
			setContentView( R.layout.web_brower );
			setTitle( Util.getWebBrowersTitle() );
			final WebView website = (WebView) findViewById( R.id.web_brower );
			
			website.getSettings().setJavaScriptEnabled( true );
			
			/* Register a new JavaScript interface called HTMLOUT */
			// website.setWebChromeClient( new WebChromeClient() {
			// public boolean onConsoleMessage(ConsoleMessage cmsg) {
			// // check secret prefix
			// if (cmsg.message().startsWith( "MAGIC" )) {
			// String msg = cmsg.message().substring( 5 ); // strip off
			// // prefix
			//
			// Log.i( "msg", msg.toLowerCase().contains( "google" )
			// + "" );
			//
			// return true;
			// }
			//
			// return false;
			// }
			// } );
			getActionBar().setIcon(
					new ColorDrawable( getResources().getColor(
							android.R.color.transparent ) ) );
			
			/*
			 * website.setWebViewClient(new WebViewClient() {
			 * 
			 * @Override public boolean shouldOverrideUrlLoading(WebView view,
			 * String urlNewString) { if (!loadingFinished) { redirect = true; }
			 * 
			 * loadingFinished = false; view.loadUrl(urlNewString); return true;
			 * }
			 * 
			 * @Override public void onPageStarted(WebView view, String url,
			 * Bitmap facIcon) { loadingFinished = false; // SHOW LOADING IF IT
			 * ISNT ALREADY VISIBLE }
			 * 
			 * @Override public void onPageFinished(WebView view, String url) {
			 * if (!redirect) { loadingFinished = true; view.getResources();
			 * view.getUrl(); view.toString(); Log.i("childs",
			 * view.getChildCount() + ""); Log.i("getResources",
			 * view.getResources() + ""); Log.i("getUrl", view.getUrl() + "");
			 * Log.i("toString", view.toString() + ""); //
			 * view.loadUrl("javascript:(function(){"
			 * +"for(int i = 0; i < document." +"})()");
			 * 
			 * view.loadUrl(
			 * "javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');"
			 * );
			 * 
			 * if (loadingFinished && !redirect) { // HIDE LOADING IT HAS
			 * FINISHED } else { redirect = false; } } } });
			 */
			//
			// website.setWebChromeClient(new WebChromeClient() {
			// public boolean onConsoleMessage(ConsoleMessage cmsg)
			// {
			// // check secret prefix
			// Log.e("cmos", cmsg.message());
			// Log.i("test2", "test2");
			// if (cmsg.message().startsWith("MAGIC"))
			// {
			// String msg = cmsg.message().substring(5); // strip off prefix
			// Log.e("message", cmsg.toString());
			//
			// return true;
			// }
			//
			// return false;
			// }
			// });
			//
			// // inject the JavaScript on page load
			// website.setWebViewClient(new WebViewClient() {
			// public void onPageFinished(WebView view, String address)
			// {
			// // have the page spill its guts, with a secret prefix
			// Log.e("test", "fail1");
			// //document.getElementsByClassName("gbqfif")[0].value = keyword
			// //
			// view.loadUrl("javascript:console.log('MAGIC'+'document.getElementById('gbqfbwa');');");
			// Log.e("test", "fail2");
			// //
			// view.loadUrl("javascript:console.log('MAGIC'+'document.getElementsByClassName('gbqfif')[0].value;');");
			// Log.e("test", "fail3");
			// view.loadUrl("javascript:function test(){var a = document.getElementsByClassName('gbqfif')[0]; a.value = 'test'; return a;}; test(); console.log(test());");
			// view.loadUrl("javascript:function test(){var a = document.getElementsByClassName('gbqfif')[0]; a; return a;}; test();");
			// //
			// view.loadUrl("javascript:function a(){document.getElementsByClassName('gbqfif')[0].value = 'something';}");
			// Log.e("test", "fail4");
			// //
			// view.loadUrl("javascript:console.log('MAGIC'+'document.getElementsByClassName('gbqfif')[0].value;');");
			// }
			// });
			// website.getSettings().setJavaScriptEnabled(true);
			// // register class containing methods to be exposed to JavaScript
			// website.addJavascriptInterface(new JSInterface(website),
			// "JSInterface");
			website.setWebViewClient( new WebViewClient() {
				
				@Override
				public void onPageFinished(WebView view, String url) {
					// TODO Auto-generated method stub
					clearHistory( website );
					super.onPageFinished( view, url );
				}
				
			} );
			Intent intent = getIntent();
			// String url = intent.getStringExtra("url");
			String keyword = intent.getStringExtra( "keyword" );
			String google = "http://www.google.com/#q=" + keyword;// + url + " "
																	// +
																	// keyword
			
			// Make sure no autofill for Forms/ user-name password happens for
			// the
			// app
			clearHistory( website );
			website.loadUrl( google );
		} catch (Exception e) {
			Util.addException( this.getClass(), e, getBaseContext() );
		}
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
	
	private static void clearHistory(WebView website) {
		CookieManager.getInstance().setAcceptCookie( false );
		
		// Make sure no caching is done
		website.getSettings()
				.setCacheMode( website.getSettings().LOAD_NO_CACHE );
		website.getSettings().setAppCacheEnabled( false );
		website.clearHistory();
		website.clearCache( true );
		website.clearFormData();
		website.getSettings().setSavePassword( false );
		website.getSettings().setSaveFormData( false );
	}
	
	@Override
	protected void onResume() {
		invalidateOptionsMenu();
		super.onResume();
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
				Util.logout( WebBrower.this, true );
				break;
			default:
				break;
		}
		return true;
	}
}
