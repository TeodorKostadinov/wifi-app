package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import seo.extra.wifi_analyzor.R;
import activities.LogIn;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import async.tasks.Logout;

import com.google.android.gms.maps.model.LatLng;
import comm.savagelook.android.UrlJsonAsyncTask;

import database.DatabaseHelper;
import database.InfoDataSource;

public abstract class Util {
	
	private static SharedPreferences userInfo;
	
	static String errorMessage;
	static String noInternetToUpdateStatus;
	static String sessionExpired;
	static String pleaseConnectToInternetForTasks;
	static String loadingOfflineTasks;
	static String noInternetToRegister;
	static String pleaseCompleteFields;
	static String noInternet;
	static String conntectToWifi;
	static String noInternetToManage;
	static String pleaseConnectToManage;
	static String noInternetToUpdate;
	static String edit;
	static String invalidCredentials;
	static String noInternetToLogin;
	static String pleaseGiveTime;
	static String noInternettoSendNew;
	static String emptyName;
	static String emptyIp;
	static String enteredIpInvalid;
	static String validIpFormat;
	static String addressEmpty;
	static String noSpecifiedCoordinates;
	static String InvalidSpotCredentials;
	static String passwordNoMatch;
	static String noInternetToChangeInfo;
	static String menu;
	static String getNewTasks;
	static String viewTasks;
	static String logout;
	static String address;
	static String name;
	static String password;
	static String keyword;
	static String website;
	static String status;
	
	static String email;
	static String userName;
	static String oldPassword;
	static String newPassword;
	static String confirmPassword;
	static String confirmChange;
	
	static String ipString;
	static String cancel;
	
	static String minutes;
	static String hours;
	static String availableTime;
	static String numberOfTasks;
	static String createNew;
	static String tasks;
	
	static String appInfo;
	static String register;
	static String login;
	static String rememberMe;
	
	static String addNew;
	
	static String taskList;
	static String map;
	static String viewAllWifi;
	static String manageWifi;
	
	static int menuId;
	
	static String accSetTitle;
	static String createNewSpotTitle;
	static String getTaskTitle;
	static String googleMapsTitle;
	static String logInTitle;
	static String manageNetworksTitle;
	static String registerTitle;
	static String tabsTitle;
	static String webBrowersTitle;
	
	static String tokenЕxpired;
	static String loadingТasks;
	static String minutesNotLessZero;
	static String hoursNotLessZero;
	static String noTasksInvalidTime;
	static String noTasksFound;
	static String errorLoadingMarkerLocation;
	static String problemLoadingMap;
	static String getNearRoadForRoot;
	static String loggingOut;
	static String logginIn;
	static String updatingInformation;
	static String loadingWiFiSpots;
	static String errorLoadingSpots;
	static String gettingWiFiSpots;
	static String registerNewAccount;
	static String verifyStatus;
	static String loadingTasks;
	static String errorReadingStatus;
	static String creatingNewSpot;
	static String noSpotsFound;
	static String errorLoadingTask;
	
	static String editingPopup;
	static String isSpotActive;
	
	static String dismiss;
	static String confirm;
	static String confirmationMessage;
	static String offerToSetToMissSTR;
	static String notAllTasksFinished;
	static String yesSTR;
	static String noSTR;
	static String searchStr;
	
	public static final String SEPARATOR = "|";
	
	private static List<UserException> userExceptions;
	
	public static String getConfirmationMessage() {
		return confirmationMessage;
	}
	
	public static String getDismiss() {
		return dismiss;
	}
	
	public static String getConfirm() {
		return confirm;
	}
	
	public static String getIsSpotActive() {
		return isSpotActive;
	}
	
	public static String getEditingPopup() {
		return editingPopup;
	}
	
	public static String getMinutesNotLessZero() {
		return minutesNotLessZero;
	}
	
	public static String getAccSetTitle() {
		return accSetTitle;
	}
	
	public static String getCreateNewSpotTitle() {
		return createNewSpotTitle;
	}
	
	public static String getGetTaskTitle() {
		return getTaskTitle;
	}
	
	public static String getGoogleMapsTitle() {
		return googleMapsTitle;
	}
	
	public static String getLogInTitle() {
		return logInTitle;
	}
	
	public static String getManageNetworksTitle() {
		return manageNetworksTitle;
	}
	
	public static String getRegisterTitle() {
		return registerTitle;
	}
	
	public static String getTabsTitle() {
		return tabsTitle;
	}
	
	public static String getWebBrowersTitle() {
		return webBrowersTitle;
	}
	
	public static int getMenuId() {
		return menuId;
	}
	
	public static String getTasks() {
		return tasks;
	}
	
	public static String getErrorMessage() {
		return errorMessage;
	}
	
	public static String getNoInternetToUpdateStatus() {
		return noInternetToUpdateStatus;
	}
	
	public static String getSessionExpired() {
		return sessionExpired;
	}
	
	public static String getPleaseConnectToInternet() {
		return pleaseConnectToInternetForTasks;
	}
	
	public static String getLoadingOfflineTasks() {
		return loadingOfflineTasks;
	}
	
	public static String getNoInternetToRegister() {
		return noInternetToRegister;
	}
	
	public static String getPleaseCompleteFields() {
		return pleaseCompleteFields;
	}
	
	public static String getNoInternet() {
		return noInternet;
	}
	
	public static String getConntectToWifi() {
		return conntectToWifi;
	}
	
	public static String getNoInternetToManage() {
		return noInternetToManage;
	}
	
	public static String getPleaseConnectToManage() {
		return pleaseConnectToManage;
	}
	
	public static String getNoInternetToUpdate() {
		return noInternetToUpdate;
	}
	
	public static String getEdit() {
		return edit;
	}
	
	public static String getInvalidCredentials() {
		return invalidCredentials;
	}
	
	public static String getNoInternetToLogin() {
		return noInternetToLogin;
	}
	
	public static String getPleaseGiveTime() {
		return pleaseGiveTime;
	}
	
	public static String getNoInternettoSendNew() {
		return noInternettoSendNew;
	}
	
	public static String getEmptyName() {
		return emptyName;
	}
	
	public static String getEmptyIp() {
		return emptyIp;
	}
	
	public static String getEnteredIpInvalid() {
		return enteredIpInvalid;
	}
	
	public static String getValidIpFormat() {
		return validIpFormat;
	}
	
	public static String getAddressEmpty() {
		return addressEmpty;
	}
	
	public static String getNoSpecifiedCoordinates() {
		return noSpecifiedCoordinates;
	}
	
	public static String getInvalidSpotCredentials() {
		return InvalidSpotCredentials;
	}
	
	public static String getPasswordNoMatch() {
		return passwordNoMatch;
	}
	
	public static String getNoInternetToChangeInfo() {
		return noInternetToChangeInfo;
	}
	
	public static String getMenu() {
		return menu;
	}
	
	public static String getGetNewTasks() {
		return getNewTasks;
	}
	
	public static String getViewTasks() {
		return viewTasks;
	}
	
	public static String getLogout() {
		return logout;
	}
	
	public static String getAddress() {
		return address;
	}
	
	public static String getName() {
		return name;
	}
	
	public static String getPassword() {
		return password;
	}
	
	public static String getKeyword() {
		return keyword;
	}
	
	public static String getWebsite() {
		return website;
	}
	
	public static String getStatus() {
		return status;
	}
	
	public static String getEmail() {
		return email;
	}
	
	public static String getUserName() {
		return userName;
	}
	
	public static String getOldPassword() {
		return oldPassword;
	}
	
	public static String getNewPassword() {
		return newPassword;
	}
	
	public static String getConfirmChange() {
		return confirmChange;
	}
	
	public static String getConfirmPassword() {
		return confirmPassword;
	}
	
	public static String getIpString() {
		return ipString;
	}
	
	public static String getCancel() {
		return cancel;
	}
	
	public static String getMinutes() {
		return minutes;
	}
	
	public static String getHours() {
		return hours;
	}
	
	public static String getAvailableTime() {
		return availableTime;
	}
	
	public static String getNumberOfTasks() {
		return numberOfTasks;
	}
	
	public static String getCreateNew() {
		return createNew;
	}
	
	public static String getAppInfo() {
		return appInfo;
	}
	
	public static String getRegister() {
		return register;
	}
	
	public static String getLogin() {
		return login;
	}
	
	public static String getRememberMe() {
		return rememberMe;
	}
	
	public static String getAddNew() {
		return addNew;
	}
	
	public static String getTaskList() {
		return taskList;
	}
	
	public static String getMap() {
		return map;
	}
	
	public static String getViewAllWifi() {
		return viewAllWifi;
	}
	
	public static String getManageWifi() {
		return manageWifi;
	}
	
	public static String getTokenExpired() {
		return tokenЕxpired;
	}
	
	public static String getLoadingТasks() {
		return loadingТasks;
	}
	
	public static String getHoursNotLessZero() {
		return hoursNotLessZero;
	}
	
	public static String getNoTasksInvalidTime() {
		return noTasksInvalidTime;
	}
	
	public static String getNoTasksFound() {
		return noTasksFound;
	}
	
	public static String getErrorLoadingMarkerLocation() {
		return errorLoadingMarkerLocation;
	}
	
	public static String getProblemLoadingMap() {
		return problemLoadingMap;
	}
	
	public static String getGetNearRoadForRoot() {
		return getNearRoadForRoot;
	}
	
	public static String getLoggingOut() {
		return loggingOut;
	}
	
	public static String getUpdatingInformation() {
		return updatingInformation;
	}
	
	public static String getLoadingWiFiSpots() {
		return loadingWiFiSpots;
	}
	
	public static String getErrorLoadingSpot() {
		return errorLoadingSpots;
	}
	
	public static String getGettingWiFiSpots() {
		return gettingWiFiSpots;
	}
	
	public static String getRegisterNewAccount() {
		return registerNewAccount;
	}
	
	public static String getVerifyStatus() {
		return verifyStatus;
	}
	
	public static String getLoadingTasks() {
		return loadingTasks;
	}
	
	public static String getErrorReadingStatus() {
		return errorReadingStatus;
	}
	
	public static String getPublicIp() {
		return publicIp;
	}
	
	public static String getTokenЕxpired() {
		return tokenЕxpired;
	}
	
	public static String getCreatingNewSpot() {
		return creatingNewSpot;
	}
	
	public static String getNoSpotsFound() {
		return noSpotsFound;
	}
	
	public static String getErrorLoadingTask() {
		return errorLoadingTask;
	}
	
	public static String getLogginIn() {
		return logginIn;
	}
	
	public static String getPleaseConnectToInternetForTasks() {
		return pleaseConnectToInternetForTasks;
	}
	
	public static String getErrorLoadingSpots() {
		return errorLoadingSpots;
	}
	
	public static String getPleaseConnectToInternetForSpots() {
		return pleaseConnectToInternetForSpots;
	}
	
	public static String getLoadingOfflineSpots() {
		return loadingOfflineSpots;
	}
	
	public static String getCurrentLanguadge() {
		return currentLanguadge;
	}
	
	public static String getOfferToSetToMissSTR() {
		return offerToSetToMissSTR;
	}
	
	public static String getNotAllTasksFinished() {
		return notAllTasksFinished;
	}
	
	public static String getYesSTR() {
		return yesSTR;
	}
	
	public static String getNoSTR() {
		return noSTR;
	}
	
	static String currentLanguadge;
	
	private static String pleaseConnectToInternetForSpots;
	
	private static String loadingOfflineSpots;
	
	private static String currentPage;
	
	private static String pickNewPage;
	public static InfoDataSource datasource;
	
	public static void setLanguadgeStrings(Context context) {
		datasource = new InfoDataSource( context );
		datasource.open();
		final Context cont = context;
		// recreatedDBTables();
		Thread exceptionThread = new Thread( new Runnable() {
			
			@Override
			public void run() {
				userExceptions = datasource.findAllExceptions();
				if (userExceptions != null) {
					if (userExceptions.size() != 0) {
						try {
							Looper.prepare();
							sendErrorReport( cont );
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					userExceptions = new ArrayList<UserException>();
				}
			}
		} );
		if (!userInfo.getString( "AuthToken", "nope" ).equals( "nope" )) {
			exceptionThread.start();
		} else {
			userExceptions = new ArrayList<UserException>();
		}
		
		Util.currentLanguadge = Locale.getDefault().getDisplayLanguage();
		if (Util.currentLanguadge.equals( "English" )) {
			Util.currentLanguadge = "en";
			setEnglishLanguadges( context );
			setMenuId( R.menu.en_profile );
		} else if (Util.currentLanguadge.equals( "български" )) {
			setBulgarianLanguadges( context );
			Util.currentLanguadge = "bg";
			setMenuId( R.menu.bg_profile );
		} else {
			setEnglishLanguadges( context );
			Util.currentLanguadge = "en";
			setMenuId( R.menu.en_profile );
		}
	}
	
	private static void recreatedDBTables() {
		deleteTables();
		createTables();
		
	}
	
	private static void deleteTables() {
		datasource.deleteTable( DatabaseHelper.TABLE_SPOTS );
		datasource.deleteTable( DatabaseHelper.TABLE_EXCEPTIONS );
		datasource.deleteTable( DatabaseHelper.TABLE_TASKS );
	}
	
	private static void createTables() {
		
		datasource.createTableFromSQL( DatabaseHelper.SPOTS_TABLE_CREATE );
		datasource.createTableFromSQL( DatabaseHelper.TASKS_TABLE_CREATE );
		datasource.createTableFromSQL( DatabaseHelper.EXCEPTIONS_TABLE_CREATE );
	}
	
	private static void saveUserException(UserException exception,
			Context context) {
		Log.i( "idCreated", datasource.createUserException( exception ) + "" );
		
	}
	
	public static void setMenuId(int menuId) {
		Util.menuId = menuId;
	}
	
	private static void setBulgarianLanguadges(Context context) {
		Util.errorMessage = context.getString( R.string.bg_something_wrong_s );
		Util.noInternetToUpdateStatus = context
				.getString( R.string.bg_no_internet_toupdate_status_s );
		Util.loadingOfflineSpots = context
				.getString( R.string.bg_loading_offline_spots_s );
		Util.sessionExpired = context.getString( R.string.bg_session_expired_s );
		Util.pleaseConnectToInternetForTasks = context
				.getString( R.string.bg_please_connect_togettask_s );
		Util.pleaseConnectToInternetForSpots = context
				.getString( R.string.bg_please_connect_togetspots_s );
		Util.loadingOfflineTasks = context
				.getString( R.string.bg_loading_offline_task_s );
		Util.noInternetToRegister = context
				.getString( R.string.bg_no_internet_toregister_s );
		Util.pleaseCompleteFields = context
				.getString( R.string.bg_please_comple_s );
		Util.noInternet = context.getString( R.string.bg_no_internet_s );
		Util.conntectToWifi = context.getString( R.string.bg_connect_to_wifi );
		Util.noInternetToManage = context
				.getString( R.string.bg_no_internet_manage_s );
		Util.pleaseConnectToManage = context
				.getString( R.string.bg_please_connect_togetspots_s );
		Util.noInternetToUpdate = context
				.getString( R.string.bg_no_internet_toupdate_s );
		Util.edit = context.getString( R.string.bg_edit_s );
		Util.invalidCredentials = context
				.getString( R.string.bg_invalid_credentials_s );
		Util.noInternetToLogin = context
				.getString( R.string.bg_no_internet_login_s );
		Util.pleaseGiveTime = context
				.getString( R.string.bg_please_give_time_s );
		Util.noInternettoSendNew = context
				.getString( R.string.bg_no_internet_tosendnew_s );
		Util.emptyName = context.getString( R.string.bg_enter_name_empty_s );
		Util.emptyIp = context.getString( R.string.bg_enter_ip_empty_s );
		Util.enteredIpInvalid = context
				.getString( R.string.bg_enter_ip_invalid_s );
		Util.validIpFormat = context.getString( R.string.bg_ip_valid_s );
		Util.addressEmpty = context
				.getString( R.string.bg_enter_address_empty_s );
		Util.noSpecifiedCoordinates = context
				.getString( R.string.bg_no_specified_coordinates_s );
		Util.InvalidSpotCredentials = context
				.getString( R.string.bg_invalid_spot_credentials_s );
		Util.passwordNoMatch = context
				.getString( R.string.bg_password_nomatch_s );
		Util.noInternetToChangeInfo = context
				.getString( R.string.bg_no_internet_tochangeinfo_s );
		Util.menu = context.getString( R.string.bg_menu_s );
		Util.getNewTasks = context.getString( R.string.bg_get_new_task_s );
		Util.viewTasks = context.getString( R.string.bg_view_task_s );
		Util.logout = context.getString( R.string.bg_logout );
		Util.address = context.getString( R.string.bg_address_s );
		Util.name = context.getString( R.string.bg_name_s );
		Util.password = context.getString( R.string.bg_password_s );
		Util.keyword = context.getString( R.string.bg_keyword_s );
		Util.website = context.getString( R.string.bg_website_s );
		Util.status = context.getString( R.string.bg_status_s );
		
		Util.email = context.getString( R.string.bg_email );
		Util.userName = context.getString( R.string.bg_username );
		Util.oldPassword = context.getString( R.string.bg_old_password );
		Util.newPassword = context.getString( R.string.bg_new_password );
		Util.confirmPassword = context.getString( R.string.bg_confirm_password );
		Util.confirmChange = context.getString( R.string.bg_confirm_change );
		
		Util.ipString = context.getString( R.string.bg_ip );
		Util.cancel = context.getString( R.string.bg_cancel );
		
		Util.minutes = context.getString( R.string.bg_minutes );
		Util.hours = context.getString( R.string.bg_hours );
		Util.availableTime = context.getString( R.string.bg_available_time );
		Util.numberOfTasks = context.getString( R.string.bg_number_of_tasks );
		Util.createNew = context.getString( R.string.bg_create );
		
		Util.tasks = context.getString( R.string.bg_tasks_s );
		
		Util.appInfo = context.getString( R.string.bg_description );
		Util.register = context.getString( R.string.bg_register );
		Util.login = context.getString( R.string.bg_login );
		Util.rememberMe = context.getString( R.string.bg_remember_me );
		
		Util.addNew = context.getString( R.string.bg_create );
		
		Util.taskList = context.getString( R.string.bg_task_list );
		Util.map = context.getString( R.string.bg_map );
		Util.viewAllWifi = context.getString( R.string.bg_view_wifi_all_s );
		Util.manageWifi = context.getString( R.string.bg_manage_wifi_s );
		
		Util.accSetTitle = context.getString( R.string.bg_action_settings );
		Util.createNewSpotTitle = context
				.getString( R.string.bg_create_new_spot );
		Util.getTaskTitle = context.getString( R.string.bg_get_new_task_s );
		Util.googleMapsTitle = context.getString( R.string.bg_map );
		Util.logInTitle = context.getString( R.string.bg_login_title );
		Util.manageNetworksTitle = context.getString( R.string.bg_manage_wifi );
		Util.registerTitle = context.getString( R.string.bg_register );
		Util.tabsTitle = context.getString( R.string.bg_tabs_title );
		Util.webBrowersTitle = context.getString( R.string.bg_web_browser );
		
		Util.tokenЕxpired = context.getString( R.string.bg_token_expired );
		Util.loadingТasks = context.getString( R.string.bg_loading_tasks );
		Util.minutesNotLessZero = context
				.getString( R.string.bg_minutes_not_less_zero );
		Util.hoursNotLessZero = context
				.getString( R.string.bg_hours_not_less_zero );
		Util.noTasksInvalidTime = context
				.getString( R.string.bg_no_tasks_invalid_time );
		Util.noTasksFound = context.getString( R.string.bg_no_tasks_found );
		Util.errorLoadingMarkerLocation = context
				.getString( R.string.bg_error_loading_marker_location );
		Util.problemLoadingMap = context
				.getString( R.string.bg_problem_loading_map );
		Util.getNearRoadForRoot = context
				.getString( R.string.bg_get_near_road_for_root );
		Util.loggingOut = context.getString( R.string.bg_logging_out );
		Util.updatingInformation = context
				.getString( R.string.bg_updating_information );
		Util.loadingWiFiSpots = context
				.getString( R.string.bg_loading_wifi_spots );
		Util.errorLoadingSpots = context
				.getString( R.string.bg_error_loading_spots );
		Util.gettingWiFiSpots = context
				.getString( R.string.bg_getting_wifi_spots );
		Util.registerNewAccount = context
				.getString( R.string.bg_register_new_account );
		Util.verifyStatus = context.getString( R.string.bg_verify_status );
		Util.errorReadingStatus = context
				.getString( R.string.bg_error_reading_status );
		Util.creatingNewSpot = context
				.getString( R.string.bg_creating_new_spot );
		Util.noSpotsFound = context.getString( R.string.bg_no_spots_s );
		Util.errorLoadingTask = context
				.getString( R.string.bg_error_loading_task );
		Util.logginIn = context.getString( R.string.bg_logging_in );
		Util.loadingTasks = context.getString( R.string.bg_loading_tasks );
		
		Util.editingPopup = context.getString( (R.string.bg_editing_popup) );
		Util.isSpotActive = context.getString( R.string.bg_active_spot );
		Util.currentPage = context.getString( R.string.bg_current_page );
		Util.pickNewPage = context.getString( R.string.bg_pick_new_page );
		
		Util.dismiss = context.getString( R.string.bg_dismiss );
		Util.confirm = context.getString( R.string.bg_confirm );
		Util.confirmationMessage = context
				.getString( R.string.bg_confrimation_message );
		Util.offerToSetToMissSTR = context
				.getString( R.string.bg_offer_to_miss_tasks );
		Util.notAllTasksFinished = context
				.getString( R.string.bg_not_all_tasks_finished );
		Util.yesSTR = context.getString( R.string.bg_yes );
		Util.noSTR = context.getString( R.string.bg_no );
		Util.pleaseWait = context.getString( R.string.bg_please_wait );
		Util.wouldYouLike = context.getString( R.string.bg_would_you_like );
		Util.searchStr = context.getString( R.string.bg_search );
	}
	
	public static String getCurrentPage() {
		return currentPage;
	}
	
	public static String getPickNewPage() {
		return pickNewPage;
	}
	
	private static void setEnglishLanguadges(Context context) {
		Util.errorMessage = context.getString( R.string.en_something_wrong_s );
		Util.pleaseConnectToInternetForSpots = context
				.getString( R.string.en_please_connect_togetspots_s );
		Util.loadingOfflineSpots = context
				.getString( R.string.en_loading_offline_spots_s );
		Util.noInternetToUpdateStatus = context
				.getString( R.string.en_no_internet_toupdate_status_s );
		Util.sessionExpired = context.getString( R.string.en_session_expired_s );
		Util.pleaseConnectToInternetForTasks = context
				.getString( R.string.en_please_connect_togettask_s );
		Util.loadingOfflineTasks = context
				.getString( R.string.en_loading_offline_task_s );
		Util.noInternetToRegister = context
				.getString( R.string.en_no_internet_toregister_s );
		Util.pleaseCompleteFields = context
				.getString( R.string.en_please_comple_s );
		Util.noInternet = context.getString( R.string.en_no_internet_s );
		Util.conntectToWifi = context.getString( R.string.en_connect_to_wifi );
		Util.noInternetToManage = context
				.getString( R.string.en_no_internet_manage_s );
		Util.pleaseConnectToManage = context
				.getString( R.string.en_please_connect_togetspots_s );
		Util.noInternetToUpdate = context
				.getString( R.string.en_no_internet_toupdate_s );
		Util.edit = context.getString( R.string.en_edit_s );
		Util.invalidCredentials = context
				.getString( R.string.en_invalid_credentials_s );
		Util.noInternetToLogin = context
				.getString( R.string.en_no_internet_login_s );
		Util.pleaseGiveTime = context
				.getString( R.string.en_please_give_time_s );
		Util.noInternettoSendNew = context
				.getString( R.string.en_no_internet_tosendnew_s );
		Util.emptyName = context.getString( R.string.en_enter_name_empty_s );
		Util.emptyIp = context.getString( R.string.en_enter_ip_empty_s );
		Util.enteredIpInvalid = context
				.getString( R.string.en_enter_ip_invalid_s );
		Util.validIpFormat = context.getString( R.string.en_ip_valid_s );
		Util.addressEmpty = context
				.getString( R.string.en_enter_address_empty_s );
		Util.noSpecifiedCoordinates = context
				.getString( R.string.en_no_specified_coordinates_s );
		Util.InvalidSpotCredentials = context
				.getString( R.string.en_invalid_spot_credentials_s );
		Util.passwordNoMatch = context
				.getString( R.string.en_password_nomatch_s );
		Util.noInternetToChangeInfo = context
				.getString( R.string.en_no_internet_tochangeinfo_s );
		Util.menu = context.getString( R.string.en_menu_s );
		Util.getNewTasks = context.getString( R.string.en_get_new_task_s );
		Util.viewTasks = context.getString( R.string.en_view_task_s );
		Util.logout = context.getString( R.string.en_logout );
		Util.address = context.getString( R.string.en_address_s );
		Util.name = context.getString( R.string.en_name_s );
		Util.password = context.getString( R.string.en_password_s );
		Util.keyword = context.getString( R.string.en_keyword_s );
		Util.website = context.getString( R.string.en_website_s );
		Util.status = context.getString( R.string.en_status_s );
		
		Util.email = context.getString( R.string.en_email );
		Util.userName = context.getString( R.string.en_username );
		Util.oldPassword = context.getString( R.string.en_old_password );
		Util.newPassword = context.getString( R.string.en_new_password );
		Util.confirmPassword = context.getString( R.string.en_confirm_password );
		Util.confirmChange = context.getString( R.string.en_confirm_change );
		
		Util.ipString = context.getString( R.string.en_ip );
		Util.cancel = context.getString( R.string.en_cancel );
		
		Util.minutes = context.getString( R.string.en_minutes );
		Util.hours = context.getString( R.string.en_hours );
		Util.availableTime = context.getString( R.string.en_available_time );
		Util.numberOfTasks = context.getString( R.string.en_number_of_tasks );
		Util.createNew = context.getString( R.string.en_create );
		
		Util.tasks = context.getString( R.string.en_tasks_s );
		
		Util.appInfo = context.getString( R.string.en_description );
		Util.register = context.getString( R.string.en_register );
		Util.login = context.getString( R.string.en_login );
		Util.rememberMe = context.getString( R.string.en_remember_me );
		
		Util.addNew = context.getString( R.string.en_create );
		
		Util.taskList = context.getString( R.string.en_task_list );
		Util.map = context.getString( R.string.en_map );
		Util.viewAllWifi = context.getString( R.string.en_view_wifi_all_s );
		Util.manageWifi = context.getString( R.string.en_manage_wifi_s );
		
		Util.accSetTitle = context.getString( R.string.en_action_settings );
		Util.createNewSpotTitle = context
				.getString( R.string.en_create_new_spot );
		Util.getTaskTitle = context.getString( R.string.en_get_new_task_s );
		Util.googleMapsTitle = context.getString( R.string.en_map );
		Util.logInTitle = context.getString( R.string.en_login );
		Util.manageNetworksTitle = context.getString( R.string.en_manage_wifi );
		Util.registerTitle = context.getString( R.string.en_register );
		Util.tabsTitle = context.getString( R.string.en_tabs_title );
		Util.webBrowersTitle = context.getString( R.string.en_web_browser );
		
		Util.tokenЕxpired = context.getString( R.string.en_token_expired );
		Util.loadingТasks = context.getString( R.string.en_loading_tasks );
		Util.minutesNotLessZero = context
				.getString( R.string.en_minutes_not_less_zero );
		Util.hoursNotLessZero = context
				.getString( R.string.en_hours_not_less_zero );
		Util.noTasksInvalidTime = context
				.getString( R.string.en_no_tasks_invalid_time );
		Util.noTasksFound = context.getString( R.string.en_no_tasks_found );
		Util.errorLoadingMarkerLocation = context
				.getString( R.string.en_error_loading_marker_location );
		Util.problemLoadingMap = context
				.getString( R.string.en_problem_loading_map );
		Util.getNearRoadForRoot = context
				.getString( R.string.en_get_near_road_for_root );
		Util.loggingOut = context.getString( R.string.en_logging_out );
		Util.updatingInformation = context
				.getString( R.string.en_updating_information );
		Util.loadingWiFiSpots = context
				.getString( R.string.en_loading_wifi_spots );
		Util.errorLoadingSpots = context
				.getString( R.string.en_error_loading_spots );
		Util.gettingWiFiSpots = context
				.getString( R.string.en_getting_wifi_spots );
		Util.registerNewAccount = context
				.getString( R.string.en_register_new_account );
		Util.verifyStatus = context.getString( R.string.en_verify_status );
		Util.errorReadingStatus = context
				.getString( R.string.en_error_reading_status );
		Util.creatingNewSpot = context
				.getString( R.string.en_creating_new_spot );
		Util.noSpotsFound = context.getString( R.string.en_no_spots_s );
		Util.errorLoadingTask = context
				.getString( R.string.en_error_loading_task );
		Util.logginIn = context.getString( R.string.en_logging_in );
		Util.loadingTasks = context.getString( R.string.en_loading_tasks );
		
		Util.editingPopup = context.getString( (R.string.en_editing_popup) );
		Util.isSpotActive = context.getString( R.string.en_active_spot );
		Util.currentPage = context.getString( R.string.en_current_page );
		Util.pickNewPage = context.getString( R.string.en_pick_new_page );
		
		Util.dismiss = context.getString( R.string.en_dismiss );
		Util.confirm = context.getString( R.string.en_confirm );
		Util.confirmationMessage = context
				.getString( R.string.en_confrimation_message );
		Util.offerToSetToMissSTR = context
				.getString( R.string.en_offer_to_miss_tasks );
		Util.notAllTasksFinished = context
				.getString( R.string.en_not_all_tasks_finished );
		Util.yesSTR = context.getString( R.string.en_yes );
		Util.noSTR = context.getString( R.string.en_no );
		Util.pleaseWait = context.getString( R.string.en_please_wait );
		Util.wouldYouLike = context.getString( R.string.en_would_you_like );
		Util.searchStr = context.getString( R.string.en_search );
	}
	
	public static SharedPreferences getUserInfo() {
		return userInfo;
	}
	
	public static void setUserInfo(SharedPreferences newUserInfo) {
		userInfo = newUserInfo;
	}
	
	public static String getPublicIP() throws IOException {
		org.jsoup.nodes.Document doc = Jsoup.connect( "http://www.checkip.org" )
				.get();
		
		return (doc.getElementById( "yourip" )).select( "h1" ).first()
				.select( "span" ).text();
	}
	
	private static String publicIp;
	
	public static String getIp(Context activityCaller) {
		Thread thread = new Thread( new Runnable() {
			@Override
			public void run() {
				try {
					publicIp = getPublicIP();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} );
		thread.start();
		if (publicIp == null || publicIp.isEmpty() || publicIp.equals( "" )) {
			publicIp = getCurrentIP();
		}
		if (publicIp.contains( "404" )) {
			publicIp = getIpAddress( activityCaller );
			
		}
		if (publicIp == null) {
			return userInfo.getString( "Username", "nope" ) + " ip ";
		}
		if (publicIp.equals( null )) {
			return userInfo.getString( "Username", "nope" ) + " ip ";
		}
		return publicIp;
	}
	
	private static String getCurrentIP() {
		String ip = "";
		
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet( "http://whatismyip.everdot.org/ip" );
			// HttpGet httpget = new HttpGet("http://whatismyip.com.au/");
			// HttpGet httpget = new HttpGet("http://www.whatismyip.org/");
			HttpResponse response;
			
			response = httpclient.execute( httpget );
			
			// Log.i("externalip",response.getStatusLine().toString());
			
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				long len = entity.getContentLength();
				if (len != -1 && len < 1024) {
					String str = EntityUtils.toString( entity );
					// Log.i("externalip",str);
					ip = str;
					return ip;
				} else {
					ip = "0.0.0.0-IP-ERROR";
					// debug
					// ip.setText("Response too long or error: "+EntityUtils.toString(entity));
					// Log.i("externalip",EntityUtils.toString(entity));
				}
			} else {
				// setText( "Null:" + response.getStatusLine().toString() );
			}
			
		} catch (Exception e) {
			// ip.setText( "Error" );
		}
		return ip;
	}
	
	public static String getServiceState(ConnectivityManager conMgr) {
		String state = "no connection";
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			state = activeNetwork.getState().toString();
		} else {
			state = activeNetwork.getState().toString();
		}
		String[] stateInfo = state.split( " " );
		state = stateInfo[1];
		return state;
	}
	
	public static boolean isWifiEnabled(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo wifiNetwork = cm
				.getNetworkInfo( ConnectivityManager.TYPE_WIFI );
		
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			return true;
		}
		return false;
	}
	
	public static boolean isInternetAvailable(Context context) {
		NetworkInfo info = (NetworkInfo) ((ConnectivityManager) context
				.getSystemService( Context.CONNECTIVITY_SERVICE ))
				.getActiveNetworkInfo();
		
		if (info == null) {
			Log.d( "TAG", "no internet connection" );
			return false;
		} else {
			if (info.isConnected()) {
				Log.d( "TAG+", " internet connection available..." );
				return true;
			} else {
				Log.d( "TAG+", " internet connection" );
				return true;
			}
		}
	}
	
	public static Boolean tryParseInt(String value) {
		try {
			Integer.parseInt( value );
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
	
	public static Boolean tryParseDouble(String value) {
		try {
			Double.parseDouble( value );
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
	
	public static String getServerAddress(Context context) {
		String URL = context.getString( R.string.en_server_name );
		return URL;
	}
	
	public static LatLng getCurrentLocation(Context currentContext) {
		
		LocationManager lm = (LocationManager) currentContext
				.getSystemService( Context.LOCATION_SERVICE );
		List<String> providers = lm.getProviders( true );
		
		Location l = null;
		for (int i = 0; i < providers.size(); i++) {
			l = lm.getLastKnownLocation( providers.get( i ) );
			if (l != null)
				break;
		}
		double latitude = 0;
		double longitude = 0;
		
		if (l != null) {
			latitude = l.getLatitude();
			longitude = l.getLongitude();
		}
		LatLng gps = new LatLng( latitude, longitude );
		return gps;
	}
	
	public static void showPupupForConfirmation(
			DialogInterface.OnClickListener clickListener,
			Context activityContext, String title, String message) {
		DialogInterface.OnClickListener dialogClickListener = clickListener;
		AlertDialog.Builder builder = new AlertDialog.Builder( activityContext );
		builder.setTitle( title );
		builder.setMessage( message )
				.setPositiveButton( getYesSTR(), dialogClickListener )
				.setNegativeButton( getNoSTR(), dialogClickListener ).show();
	}
	
	public static void logout(final Context context,
			final boolean currentTokenIsValid) {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						Logout logoutCommand = new Logout( context,
								Util.getNoInternet() );
						String URL = Util.getServerAddress( context )
								+ "logout.json";
						if (currentTokenIsValid) { // true when logging out from
													// menu, false
													// when logging out because
													// of server
													// response
													// "Token has expired"
							logoutCommand.setMessageLoading( getLoggingOut() );
						}
						logoutCommand.execute( URL
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
		showPupupForConfirmation( clickListener, context, getLogout(),
				Util.getConfirmationMessage() );
	}
	
	public static void changeScreen(String activityName, Context activity) {
		Intent changeScreen;
		changeScreen = new Intent( "com.example.wificon." + activityName );
		changeScreen.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		activity.startActivity( changeScreen );
	}
	
	public static void addException(Class theClass, Exception exception,
			Context context) {
		UserException ue = new UserException( theClass.getName(), exception );
		saveUserException( ue, context );
		userExceptions.add( ue );
	}
	
	public static String NO_MESSAGE = "NoMessage";
	
	private static void sendErrorReport(Context context) {
		SendErrorReport sendErrorToServer = new SendErrorReport( context,
				NO_MESSAGE );
		String errorURL = getServerAddress( context ) + "exception.json";
		sendErrorToServer.execute( errorURL + "?auth_token="
				+ Util.getUserInfo().getString( "AuthToken", "" ) );
	}
	
	private static class SendErrorReport extends UrlJsonAsyncTask {
		
		public SendErrorReport(Context context, String errorMessage) {
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
						String errorMessage = Util.getErrorMessage();
						
						json.put( "success", false );
						json.put( "info", errorMessage );
						String ip = getIp( context );
						try {
							if (ip == null) {
								org.jsoup.nodes.Document doc = Jsoup.connect(
										"http://www.checkip.org" ).get();
								
								ip = (doc.getElementById( "yourip" ))
										.select( "h1" ).first().select( "span" )
										.text();
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						holder.put( "ip", ip );
						
						JSONObject contentHolder = new JSONObject();
						
						for (int i = 0; i < userExceptions.size(); i++) {
							
							UserException ue = userExceptions.get( i );
							JSONObject data = new JSONObject();
							Calendar timeExc = ue.getTimeOfError();
							HashMap<String, String> map = new HashMap<String, String>();
							
							map.put( "year", timeExc.get( Calendar.YEAR ) + "" );
							map.put( "month", timeExc.get( Calendar.MONTH )
									+ "" );
							map.put( "day", timeExc.get( Calendar.DAY_OF_MONTH )
									+ "" );
							map.put( "hour", timeExc.get( Calendar.HOUR ) + "" );
							map.put( "minute", timeExc.get( Calendar.MINUTE )
									+ "" );
							map.put( "second", timeExc.get( Calendar.SECOND )
									+ "" );
							
							JSONObject theTime = new JSONObject( map );
							data.put( "class", ue.getFromClassName() );
							data.put( "type", ue.getClassErrorName() );
							data.put( "stack", ue.getExceptionStack() );
							data.put( "time", theTime );
							
							contentHolder.put( i + "", data );
							
						}
						holder.put( "errors", contentHolder );
						holder.put( "language", Util.getCurrentLanguadge() );
						if (ip == null) {
							ip = getIp( context );
							holder.put( "ip", ip );
						}
						Log.i( "holder", holder.toString() );
						// add the users's info to the post params
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
						
						Log.w( "before update", userExceptions.size() + "" );
						userExceptions.clear();
						datasource.clearTable( "exceptions" );
						Log.w( "after update", userExceptions.size() + "" );
					} else {
						if (json.getString( "info" ).equals(
								Util.getTokenExpired() )) {
							Util.logout( context, false );
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
	
	public static LatLng getSofiaLatLog() {
		
		return new LatLng( 42.6954322, 23.3239467 );
	}
	
	private static String pleaseWait;
	
	public static String getPleaseWait() {
		return pleaseWait;
	}
	
	private static String wouldYouLike;
	
	public static String getWouldYouLike() {
		return wouldYouLike;
	}
	
	public static String getSearchStr() {
		return searchStr;
	}
	
	public static Boolean hasValidToken() {
		if (Util.getUserInfo().contains( "AuthToken" )) {
			return true;
		}
		return false;
	}
	
	public static Boolean hasInternet(Context context) {
		if (Util.isWifiEnabled( context ) || Util.isInternetAvailable( context )) {
			return true;
		} else {
			return false;
		}
	}
	
	public static Boolean hasInternetAndValidToken(Context context) {
		if (hasInternet( context )) {
			if (hasValidToken()) {
				return true;
			} else {
				Intent intent = new Intent( context, LogIn.class );
				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
				context.startActivity( intent );
				
				Toast.makeText( context, Util.getSessionExpired(),
						Toast.LENGTH_LONG ).show();
			}
		}
		return false;
	}
	
	private static String getIpAddress(Context context) {
		String ip = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(
					"http://ip2country.sourceforge.net/ip2c.php?format=JSON" );
			// HttpGet httpget = new HttpGet("http://whatismyip.com.au/");
			// HttpGet httpget = new HttpGet("http://www.whatismyip.org/");
			HttpResponse response;
			
			response = httpclient.execute( httpget );
			// Log.i("externalip",response.getStatusLine().toString());
			
			HttpEntity entity = response.getEntity();
			entity.getContentLength();
			String str = EntityUtils.toString( entity );
			JSONObject json_data = new JSONObject( str );
			ip = json_data.getString( "ip" );
		} catch (Exception e) {
		}
		
		return ip;
	}
}
