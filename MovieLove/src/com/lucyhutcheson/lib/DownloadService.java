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
 * IntentService to handle downloading the latest movies from the rotten
 * tomatoes api. To be used for the latest movies view.
 */
public class DownloadService extends IntentService {

	Context context = this;
	public static String JSON_MOVIES = "movies";
	public static String JSON_TITLE = "title";
	public static String JSON_YEAR = "year";
	public static String JSON_RATING = "mpaa_rating";
	public static final String MESSENGER_KEY = "messenger";
	Messenger messenger;
	Message message;
	public static final String LATESTMOVIES_URL = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/in_theaters.json?apikey=bcqq9h5yxut6nm9qz77h3w3h";
	
	/**
	 * Instantiates a new download service.
	 * 
	 */
	public DownloadService() {
		super("DownloadService");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("DOWNLOAD SERVICE", "DOWNLOAD SERVICE STARTED");

		Bundle extras = intent.getExtras();
		messenger = (Messenger) extras.get(MESSENGER_KEY);
		message = Message.obtain();

		// SETUP OUR URL TO QUERY
		URL localURL = null;
		try {
			localURL = new URL(LATESTMOVIES_URL);
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
				// QUERY MY URL
				response = WebConnections.getURLStringResponse(localURL);
				try {
					// GET MY RESPONSE
					JSONObject json = new JSONObject(response);
					// NO RESPONSE RECEIVED
					if (json.getString("total").compareTo("0") == 0) {
						Log.i("JSON TOTAL", "NO MOVIES FOUND");
						Toast toast = Toast.makeText(getApplicationContext(),
								"Movies Not Found", Toast.LENGTH_SHORT);
						toast.show();
					} else {
						// INSTANTIATE MY SINGLETON AND SAVE MY RESULT
						MoviesSingletonClass mMovies = MoviesSingletonClass
								.getInstance();
						mMovies.set_movies(json.toString());
						

						// SETUP OUR MESSAGE AND SEND
						message.arg1 = Activity.RESULT_OK;
						message.obj = "Service completed";
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
			Toast toast = Toast.makeText(getApplicationContext(),
					"No network detected.", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

}
