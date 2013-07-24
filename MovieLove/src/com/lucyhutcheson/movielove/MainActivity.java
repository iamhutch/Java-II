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
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;
import com.lucyhutcheson.lib.FileFunctions;
import com.lucyhutcheson.lib.GetDataService;
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
@SuppressLint("HandlerLeak")
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
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Log.i("UPDATE WITH JSON", json.toString());
				updateData(json);
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
		setContentView(R.layout.form);

		// SETUP VARIABLES AND VALUES
		_context = this;
		_favorites = getFavorites();
		_temp = getTemp();
		// INFLATE THE SEARCH FORM AND ADD TO THE CURRENT VIEW
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.mainlayout);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

				// CLEAR OUT ALL FIELDS
				((TextView) findViewById(R.id._name)).setText("");
				((TextView) findViewById(R.id._rating)).setText("");
				((TextView) findViewById(R.id._year)).setText("");
				((TextView) findViewById(R.id._mpaa)).setText("");
				((TextView) findViewById(R.id._synopsis)).setText("");

				// SHOW OUR USERS A FRIENDLY DOWNLOADING PROGRESS DIALOG
			    pDialog = ProgressDialog.show(_context, "Downloading", "Please wait...");

				// GET SEARCHED FOR MOVIE INFORMATION
				Messenger messenger = new Messenger(searchServiceHandler);
				Intent startServiceIntent = new Intent(getApplicationContext(),GetDataService.class);
				startServiceIntent.putExtra(GetDataService.MESSENGER_KEY,messenger);
				startServiceIntent.putExtra(GetDataService.SEARCH_KEY,_searchField.getText().toString());
				startServiceIntent.setData(Uri.parse("http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=bcqq9h5yxut6nm9qz77h3w3h&page_limit=5&q="
								+ Uri.encode(_searchField.getText().toString())));
				startService(startServiceIntent);

			}
		});

		// CLEAR BUTTON AND HANDLER
		Button clearButton = (Button) findViewById(R.id.clearButton);
		clearButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// DISMISS THE KEYBOARD SO WE CAN SEE OUR TEXT
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(((EditText) findViewById(R.id.searchField)).getWindowToken(), 0);
				
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
				Intent intent = new Intent(MainActivity.this,MoviesActivity.class);
				MainActivity.this.startActivity(intent);
				MainActivity.this.finish();

			}
		});

		// VIEW MOVIE INFO BUTTON AND HANDLER
		Button movieInfoButton = (Button) findViewById(R.id.trailerButton);
		movieInfoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// GET JSON DATA THAT WAS STORED INTO TEMP FROM OUR SEARCH
				_temp = getTemp();
				String resultsString = null;
				try {
					JSONObject results = new JSONObject(_temp);
					resultsString = results.toString();

				} catch (JSONException e) {
					Log.e("JSON", "JSON OBJECT EXCEPTION");
					e.printStackTrace();
				}

				// START EXPLICIT INTENT AND PASS OUR JSON DATA
				Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
				intent.putExtra("data", resultsString);
				startActivityForResult(intent, REQUEST_CODE);
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
						_favorites.put(results.getString("title"), results.toString());
						FileFunctions.storeObjectFile(_context, FAV_FILENAME, _favorites, false);
						// ALERT USER OF SUCCESSFUL SAVE
						Toast toast = Toast.makeText(_context,"Movie successfully added to favorites.",	Toast.LENGTH_SHORT);
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
						((TextView) findViewById(R.id._name)).setText(json.getString("title"));
						((TextView) findViewById(R.id._rating)).setText(json.getJSONObject("ratings").getString("critics_score"));
						((TextView) findViewById(R.id._year)).setText(json.getString("year"));
						((TextView) findViewById(R.id._mpaa)).setText(json.getString("mpaa_rating"));
						((TextView) findViewById(R.id._synopsis)).setText(json.getString("critics_consensus"));
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
	 * Receives dynamic user message from Explicit Intent, DetailsActivity.java 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			if (data.hasExtra("returnMessage")) {
				Toast.makeText(this,
						data.getExtras().getString("returnMessage"),
						Toast.LENGTH_LONG).show();
			}
		}
	}

}
