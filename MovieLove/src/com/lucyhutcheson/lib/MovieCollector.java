package com.lucyhutcheson.lib;

import java.net.URI;
import java.net.URISyntaxException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class MovieCollector {
	
	private final String TAG = "MovieCollector";
	public static final String FILE_NAME = "movies.txt";
	public static final String JSON_TITLE = "TITLE";
	public static final String JSON_YEAR = "YEAR";
	public static final String JSON_RATING = "RATING";
	public static final String JSON_FIELDS = null;
	private Context _context;
	
	public MovieCollector(Context context) throws URISyntaxException {
		_context = context;
		
		URI[] uri = new URI[1];
		uri[0] = new URI(DownloadService.LATESTMOVIES_URL);
		
		ConnectivityManager connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connMgr == null) {
			Log.e(TAG, "No network connection detected.");
		} else {
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

			// QUERY URL IF NETWORK IS AVAILABLE
			if (networkInfo != null && networkInfo.isConnected()) {
				//new GetJSONAsyncTask(_context).execute(uri);
			}
			else {
				
			}
		}
	}
	
}
