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
import org.json.JSONException;
import org.json.JSONObject;

import com.lucyhutcheson.movielove.MoviesActivity;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
/**
 * IntentService to handle downloading the latest movies from the rotten tomatoes api.
 * To be used for the latest movies view.
 */
public class DownloadService extends IntentService {

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
		String baseURL = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/in_theaters.json?apikey=bcqq9h5yxut6nm9qz77h3w3h";
		URL finalURL;
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			try {
				// SETUP MY URL AND REQUEST IT
				finalURL = new URL(baseURL);
				MovieRequest qr = new MovieRequest();
				qr.execute(finalURL);
			} catch (MalformedURLException e) {
				Log.e("BAD URL", "MALFORMED URL");
				finalURL = null;
			}
		} else {
			Toast toast = Toast.makeText(getApplicationContext(), "No network detected.",
					Toast.LENGTH_SHORT);
			toast.show();
		}

	}
	/**
	 * Class to handle and validate the URL for the movie request.
	 */
	private class MovieRequest extends AsyncTask<URL, Void, String> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(URL... urls) {
			String response = "";
			for (URL url : urls) {
				response = WebConnections.getURLStringResponse(url);
			}
			return response;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			Log.i("URL RESPONSE", result);
			try {
				JSONObject json = new JSONObject(result);
				if (json.getString("total").compareTo("0") == 0) {
					Toast toast = Toast.makeText(getApplicationContext(), "Movies Not Found",
							Toast.LENGTH_SHORT);
					toast.show();
				} else {
					// Instantiate my singleton and save the json result
					MoviesSingletonClass mMovies = MoviesSingletonClass.getInstance();
					mMovies.set_movies(json.toString());
					// Launch the movies activity screen
					Intent intent = new Intent(DownloadService.this, MoviesActivity.class);
					DownloadService.this.startActivity(intent);



				}
			} catch (JSONException e) {
				Log.e("JSON", "JSON OBJECT EXCEPTION");
				e.printStackTrace();
			}
		}
	}
}
