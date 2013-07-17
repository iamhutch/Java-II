/*
 * project		MovieLove
 * 
 * package		com.lucyhutcheson.lib
 * 
 * @author		Lucy Hutcheson
 * 
 * date			Jul 11, 2013
 * 
 */
package com.lucyhutcheson.lib;

import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

/**
 * IntentService to handle downloading the latest movies from the rotten tomatoes api.
 * To be used for the latest movies view.
 */
public class GetDataService extends IntentService {
	
	public static String JSON_MOVIES = "movies";
	public static String JSON_TITLE = "title";
	public static final String SEARCH_KEY = "search";
	public static final String MESSENGER_KEY = "messenger";
	Messenger messenger;
	Message message;

	/**
	 * Instantiates a new download service.
	 *           
	 */
	public GetDataService() {
		super("GetDataService");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("GETDATA SERVICE", "GETDATA SERVICE STARTED");
		
        String localUrlString = intent.getDataString();
        Log.i("GETDATA SERVICE", localUrlString);
		Bundle extras = intent.getExtras();
		messenger = (Messenger) extras.get(MESSENGER_KEY);
		message = Message.obtain();

		// SETUP OUR URL TO QUERY
		URL localURL = null;
        try {
			localURL = new URL(localUrlString);
		} catch (MalformedURLException e1) {
			Log.e("ERROR", e1.toString());
			e1.printStackTrace();
		}
        
        // CHECK OUR NETWORK CONNECTION
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		// QUERY URL IF NETWORK IS AVAILABLE
		if (networkInfo != null && networkInfo.isConnected()) {
			String response = "";
			try {
				response = WebConnections.getURLStringResponse(localURL);

				try {
					// GET OUR JSON RESPONSE
					JSONObject json = new JSONObject(response);
					
					// NO RESPONSE RECEIVED
					if (json.getString("total").compareTo("0") == 0) {
						Toast toast = Toast.makeText(getApplicationContext(), "Movies Not Found",
								Toast.LENGTH_SHORT);
						toast.show();
					} else {
						// GET OUR DATA FROM THE JSON ARRAY
						JSONObject results = json.getJSONArray("movies").getJSONObject(0);
						
						// SAVE THE DATA TO OUR TEMP FILE FOR INCLUSION IN FAVORITES IF SELECTED BY USER
						FileFunctions.storeStringFile(getApplicationContext(), "temp", results.toString(), true);
						
						// SETUP OUR MESSAGE AND SEND
						message.arg1 = Activity.RESULT_OK;
						message.obj = results;
						try {
							messenger.send(message);
						} catch (RemoteException e) {
							Log.i("MESSENGER", "ERROR SENDING MESSAGE");
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					message.arg1 = Activity.RESULT_CANCELED;
					message.obj = "Service completed";
					Log.e("ERROR", e.toString());
					e.printStackTrace();
				}
			} catch (Exception e) {
				response = null;
				message.arg1 = Activity.RESULT_CANCELED;
				Log.e("ERROR", e.toString());
				e.printStackTrace();
				localURL = null;
			}

		} 
		// No network connection available
		else {
			message.arg1 = Activity.RESULT_CANCELED;
			Toast toast = Toast.makeText(getApplicationContext(), "No network detected.",
					Toast.LENGTH_SHORT);
			toast.show();
		}

	}
}
