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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.lucyhutcheson.lib.FileFunctions;
import com.lucyhutcheson.lib.WebConnections;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * The Class MainActivity which provides access to the search form to search for
 * new movies as well as view any favorite movies that have been saved.
 */
public class MainActivity extends Activity {

	// SETUP VARIABLES FOR CLASS
	static Context _context;
	Boolean _connected = false;
	HashMap<String, String> _favorites;
	String _temp;
	EditText _searchField;
	Spinner _list;
	ArrayList<String> _movies = new ArrayList<String>();
	HashMap<String, String> favList = new HashMap<String, String>();
	public static final String FAV_FILENAME = "favorites";
	public static final String TEMP_FILENAME = "temp";

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ADD XML LAYOUT
		setContentView(R.layout.form);

		// SETUP VARIABLES AND VALUES
		_context = this;
		_favorites = getFavorites();
		_temp = getTemp();

		// INFLATE THE SEARCH FORM AND ADD TO THE CURRENT VIEW
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.mainlayout);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.searchform, layout, false);
		layout.addView(view);

		// SEARCH BUTTON AND HANDLER
		Button searchButton = (Button) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText _searchField = (EditText) findViewById(R.id.searchField);

				// DISMISS THE KEYBOARD SO WE CAN SEE OUR TEXT
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(_searchField.getWindowToken(), 0);

				// GET MOVIE INFORMATION
				getMovie(_searchField.getText().toString());
			}
		});

		// CLEAR BUTTON AND HANDLER
		Button clearButton = (Button) findViewById(R.id.clearButton);
		clearButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// DISMISS THE KEYBOARD SO WE CAN SEE OUR TEXT
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						((EditText) findViewById(R.id.searchField))
								.getWindowToken(), 0);
				// CLEAR OUT ALL FIELDS
				clearFields(false);
			}
		});

		// VIEW LATEST MOVIES BUTTON AND HANDLER
		Button latestButton = (Button) findViewById(R.id.viewLatest);
		latestButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("LATEST BUTTON", "LATEST BUTTON CLICKED");
				// INTENT TO START MAIN ACTIVITY
				Intent intent = new Intent(MainActivity.this, MoviesActivity.class);
				MainActivity.this.startActivity(intent);
				MainActivity.this.finish();

			}
		});

		// ADD TO FAVORITES BUTTON AND HANDLER
		Button addButton = (Button) findViewById(R.id.addFavButton);
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// CHECK IF THERE IS A MOVIE TO SAVE BY CHECKING THE NAME
				// TEXTVIEW VALUE
				if (((TextView) findViewById(R.id._name)).getText().length() > 0) {
					// EMPTY OUT OUR FIELDS
					clearFields(true);

					_temp = getTemp();
					try {
						JSONObject results = new JSONObject(_temp);
						_favorites.put(results.getString("title"),
								results.toString());
						FileFunctions.storeObjectFile(_context, FAV_FILENAME,
								_favorites, false);
						// ALERT USER OF SUCCESSFUL SAVE
						Toast toast = Toast.makeText(_context,
								"Movie successfully added to favorites.",
								Toast.LENGTH_SHORT);
						toast.show();
						_temp = "";
					} catch (JSONException e) {
						Log.e("JSON", "JSON OBJECT EXCEPTION");
						e.printStackTrace();
					}
					// UPDATE OUR FAVORITES
					updateSaved();
				} else {
					// ALERT USER THAT NO MOVIE WAS FOUND
					Toast toast = Toast.makeText(_context,
							"No movie found to save.", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});

		// CREATE SPINNER (DROPDOWN)
		_list = (Spinner) findViewById(R.id.favSpinner);

		// CREATE ADAPTER FOR MY DROPDOWN
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(_context,
				android.R.layout.simple_spinner_item, _movies);
		listAdapter
				.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		_list.setAdapter(listAdapter);
		_list.setOnItemSelectedListener(new OnItemSelectedListener() {
			// IF A MOVIE IS SELECTED
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int pos,
					long id) {
				String str = parent.getItemAtPosition(pos).toString();
				// MAKE SURE THAT WE AREN'T SELECTING OUR PLACEHOLDER
				if (!str.equals("View Favorites")) {
					String selected = favList.get(str);
					JSONObject json;
					try {
						json = new JSONObject(selected);
						// GET DATA AND DISPLAY ON SCREEN
						((TextView) findViewById(R.id._name)).setText(json
								.getString("title"));
						((TextView) findViewById(R.id._rating)).setText(json
								.getJSONObject("ratings").getString(
										"critics_score"));
						((TextView) findViewById(R.id._year)).setText(json
								.getString("year"));
						((TextView) findViewById(R.id._mpaa)).setText(json
								.getString("mpaa_rating"));
						((TextView) findViewById(R.id._synopsis)).setText(json
								.getString("critics_consensus"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			// IF NO MOVIE IS SELECTED
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Log.i("MOVIE SELECTED", "NONE");
			}
		});
		// UPDATE OUR SPINNER WITH MOVIES
		updateSaved();
		listAdapter.notifyDataSetChanged();
	}

	/**
	 * Update the saved arraylist with movies.
	 */
	public void updateSaved() {
		favList = getFavorites();
		_movies.removeAll(_movies);
		_movies.add("View Favorites");
		_movies.addAll(favList.keySet());
		_list.setSelection(0);
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
		_list.setSelection(0);
	}

	/**
	 * Constructs the URL used to search for the movie based on the movie name
	 * string passed in.
	 * 
	 * @param movie
	 *            string passed in from form
	 * @return movie data result
	 */
	private void getMovie(String movie) {
		String baseURL = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=bcqq9h5yxut6nm9qz77h3w3h&page_limit=5&q=";
		String mqs = movie; // MAKE SURE THAT MY MOVIE STRING IS ENCODED
		String qs;
		try {
			// ENCODE MY MOVIE STRING
			qs = URLEncoder.encode(mqs, "UTF-8");
		} catch (Exception e) {
			Log.e("BAD URL", "ENCODING PROBLEM");
			qs = "";
		}
		URL finalURL;
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			try {
				// SETUP MY URL AND REQUEST IT
				finalURL = new URL(baseURL + qs);
				MovieRequest qr = new MovieRequest();
				qr.execute(finalURL);
			} catch (MalformedURLException e) {
				Log.e("BAD URL", "MALFORMED URL");
				finalURL = null;
			}
		} else {
			Toast toast = Toast.makeText(_context, "No network detected.",
					Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	/**
	 * Function to get read the favorites file which contains any movie data
	 * that was saved as a favorite.
	 * 
	 * @return hashmap of our favorites data
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, String> getFavorites() {
		Object stored = FileFunctions.readObjectFile(_context, "favorites",
				false);
		HashMap<String, String> favorites;

		// CHECK IF OBJECT EXISTS
		if (stored == null) {
			Log.i("FAVORITES", "NO FAVORITES FILE FOUND");
			favorites = new HashMap<String, String>();
		}
		// IF OBJECT EXISTS, BRING IN DATA AND ADD TO HASHMAP
		else {
			// CAST HASHMAP
			favorites = (HashMap<String, String>) stored;
		}
		return favorites;
	}

	/**
	 * Gets our temp string from the storage and returns it. The temp string
	 * contains JSON data from the most recent searched-for movie.
	 * 
	 * @return string of our movie data
	 */
	private String getTemp() {
		Object tempStored = FileFunctions
				.readStringFile(_context, TEMP_FILENAME, true);
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
	 * Class to handle and validate the URL for the movie request.
	 */
	public class MovieRequest extends AsyncTask<URL, Void, String> {

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
					Toast toast = Toast.makeText(_context, "Movie Not Found",
							Toast.LENGTH_SHORT);
					toast.show();
				} else {
					JSONObject results = json.getJSONArray("movies")
							.getJSONObject(0);
					FileFunctions.storeStringFile(_context, "temp",
							results.toString(), true);
					updateData(results);
				}
			} catch (JSONException e) {
				Log.e("JSON", "JSON OBJECT EXCEPTION");
				e.printStackTrace();
			}
		}

		/**
		 * Updates all textviews with selected movie JSON data.
		 * 
		 * @param data
		 *            the data
		 */
		public void updateData(JSONObject data) {
			try {
				((TextView) findViewById(R.id._name)).setText(data
						.getString("title"));
				((TextView) findViewById(R.id._rating)).setText(data
						.getJSONObject("ratings").getString("critics_score"));
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

	}

}
