/*
 * project		MovieLove
 * 
 * package		com.lucyhutcheson.movielove
 * 
 * @author		Lucy Hutcheson
 * 
 * date			July 8, 2013
 * 
 */

package com.lucyhutcheson.movielove;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;
import com.lucyhutcheson.lib.FileFunctions;
import com.lucyhutcheson.lib.GetDataService;
import com.lucyhutcheson.movielove.FavoritesFragment.FavoritesListener;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Class MainActivity which provides access to the search form to search for
 * new movies as well as view any favorite movies that have been saved.
 */
@SuppressLint("HandlerLeak")
public class MainActivity extends Activity implements MainFragment.FormListener, FavoritesListener {

	// SETUP VARIABLES FOR CLASS
	static Context _context;
	Boolean _connected = false;
	String _favorites;
	String _temp;
	EditText _searchField;
	Spinner _list;
	ArrayList<String> _movies = new ArrayList<String>();
	public static final String FAV_FILENAME = "favorites";
	public static final String TEMP_FILENAME = "temp";
	private ProgressDialog pDialog;
	private static final int REQUEST_CODE = 10;

	
	/**
	 * GOOGLE ANALYTICS LIBRARY.
	 */
	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		Log.i("GOOGLE ANALYTICS", "START");
		EasyTracker.getInstance().activityStart(this); // Add this method.
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		Log.i("GOOGLE ANALYTICS", "STOP");
		EasyTracker.getInstance().activityStop(this); // Add this method.
	}

	// Handle communication between this activity and
	// GetDataService class
	Handler searchServiceHandler = new Handler() {

		public void handleMessage(Message mymessage) {

			// DISMISS OUR PROGRESS DIALOG
	        pDialog.dismiss();

	        if (mymessage.arg1 == RESULT_OK	&& mymessage.obj != null) {
		        Log.i("RESPONSE", mymessage.obj.toString());
				JSONObject json = null;
				try {
					json = new JSONObject(mymessage.obj.toString());
					updateData(json);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (mymessage.arg1 == RESULT_CANCELED && mymessage.obj != null){
				Toast.makeText(MainActivity.this,mymessage.obj.toString(), Toast.LENGTH_LONG).show();

			} else {
				Toast.makeText(MainActivity.this,"Download failed.", Toast.LENGTH_LONG).show();
			}
		}

	};
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("ACTIVITY STARTED", "MoviesActivity has started.");
		
		// ADD XML LAYOUT
		setContentView(R.layout.formfrag);
		Log.i("MAIN ACTIVITY", "ContentView SET");

		
		// SETUP VARIABLES AND VALUES
		_context = this;
		_temp = getTemp();
		_favorites = FileFunctions.readStringFile(_context, "favoritestring", true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Clear out the edit fields and text views for future use.
	 * 
	 * @param clearTemp
	 *            the clear temp
	 */
	private void clearFields(Boolean clearTemp) {
		((EditText) findViewById(R.id.searchField)).setText("");
		((TextView) findViewById(R.id._name)).setText("");
		((TextView) findViewById(R.id._rating)).setText("");
		((TextView) findViewById(R.id._year)).setText("");
		((TextView) findViewById(R.id._mpaa)).setText("");
		((TextView) findViewById(R.id._synopsis)).setText("");
		if (clearTemp) {
			_temp = "";
		}
	}



	/**
	 * Gets our temp string from the storage and returns it. The temp string
	 * contains JSON data from the most recent searched-for movie.
	 * 
	 * @return string of our movie data
	 */
	private String getTemp() {
		Object tempStored = FileFunctions.readStringFile(_context,
				TEMP_FILENAME, true);
		String temp = null;

		// CHECK IF OBJECT EXISTS
		if (tempStored == null) {
			Log.i("TEMP", "NO TEMP FILE FOUND");
		}
		// IF OBJECT EXISTS, BRING IN DATA AND ADD TO HASHMAP
		else {
			// CAST HASHMAP
			temp = (String) tempStored;
		}
		return temp;
	}

	/**
	 * Updates all textviews with selected movie JSON data.
	 * 
	 * @param data
	 *            the data
	 */
	public void updateData(JSONObject data) {
		Log.i("UPDATE DATA", data.toString());
		try {
			((TextView) findViewById(R.id._name)).setText(data
					.getString("title"));
			((TextView) findViewById(R.id._rating)).setText(data.getJSONObject(
					"ratings").getString("critics_score"));
			((TextView) findViewById(R.id._year)).setText(data
					.getString("year"));
			((TextView) findViewById(R.id._mpaa)).setText(data
					.getString("mpaa_rating"));
			((TextView) findViewById(R.id._synopsis)).setText(data
					.getString("critics_consensus"));
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("JSON ERROR", e.toString());
		}
	}
	
	/**
	 * FORM FRAGMENT METHODS
	 */

	@Override
	public void onMovieSearch(String movie) {
		// SHOW OUR USERS A FRIENDLY DOWNLOADING PROGRESS DIALOG
	    pDialog = ProgressDialog.show(_context, "Downloading", "Please wait...");

		// GET SEARCHED FOR MOVIE INFORMATION
		Messenger messenger = new Messenger(searchServiceHandler);
		Intent startServiceIntent = new Intent(getApplicationContext(),GetDataService.class);
		startServiceIntent.putExtra(GetDataService.MESSENGER_KEY,messenger);
		startServiceIntent.putExtra(GetDataService.SEARCH_KEY,movie);
		startServiceIntent.setData(Uri.parse("http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=bcqq9h5yxut6nm9qz77h3w3h&page_limit=5&q="
						+ Uri.encode(movie)));
		startService(startServiceIntent);
		
	}

	@Override
	public void onLatestList() {
		// INTENT TO START MAIN ACTIVITY
		Intent intent = new Intent(MainActivity.this,LatestActivity.class);
		MainActivity.this.startActivity(intent);
		MainActivity.this.finish();		
		
	}

	@Override
	public void onAddFavorite() {
		// CHECK IF THERE IS A MOVIE TO SAVE BY CHECKING THE NAME
		// TEXTVIEW VALUE
		String currentMovie = ((TextView) findViewById(R.id._name)).getText().toString();
		Log.i("ADD FAVORITES", currentMovie);
		if (currentMovie != null) {
			// EMPTY OUT OUR FIELDS
			clearFields(true);
			
			if (_favorites != null) {
				_favorites = _favorites.concat(";"+currentMovie);
				Log.i("ADD FAVORITES IF",_favorites.toString());
			} else {
				_favorites = currentMovie;
				Log.i("ADD FAVORITES ELSE",_favorites.toString());
			}
			FileFunctions.storeStringFile(_context, "favoritestring", _favorites, true);
			// ALERT USER THAT NO MOVIE WAS FOUND
			Toast toast = Toast.makeText(_context,
					"Movie successfully saved", Toast.LENGTH_SHORT);
			toast.show();
		} else {
			// ALERT USER THAT NO MOVIE WAS FOUND
			Toast toast = Toast.makeText(_context,
					"No movie found to save", Toast.LENGTH_SHORT);
			toast.show();
		}		
	}

	@Override
	public void onClear() {
		// DISMISS THE KEYBOARD SO WE CAN SEE OUR TEXT
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((EditText) findViewById(R.id.searchField)).getWindowToken(), 0);
		
		// CLEAR OUT ALL FIELDS
		clearFields(false);
		
	}

	@Override
	public void onFavoritesList() {

		// START EXPLICIT INTENT 
		Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
		startActivityForResult(intent, REQUEST_CODE);
		
	}
	

	/**
	 * Receives dynamic user message from Explicit Intent, FavoritesActivity.java 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			if (data.hasExtra("selectedmovie")) {
				String movie = data.getExtras().getString("selectedmovie");
				if (movie != null) {
					onMovieSearch(data.getExtras().getString("selectedmovie"));
				}
			}
		}
	}

	/**
	 * FAVORITES FRAGMENT METHODS
	 */

	@Override
	public void onFavoriteSelected(String movie) {
		onMovieSearch(movie);
	}


}
