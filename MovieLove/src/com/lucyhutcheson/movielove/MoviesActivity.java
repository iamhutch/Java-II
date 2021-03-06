/*
 * project		MovieLove
 * 
 * package		com.lucyhutcheson.movielove
 * 
 * @author		Lucy Hutcheson
 * 
 * date			Jul 11, 2013
 * 
 */
package com.lucyhutcheson.movielove;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.analytics.tracking.android.EasyTracker;
import com.lucyhutcheson.lib.DownloadService;
import com.lucyhutcheson.lib.MovieProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * The Class MoviesActivity provides the view controller for the page that will
 * contain a list of the latest movies when initiated by the user from the main
 * activity.
 */
@SuppressLint("HandlerLeak")
public class MoviesActivity extends Activity implements OnClickListener {

	// Setup variables
	Context context = this;
	ListView listView;
	EditText editURI;
	Button searchButton;
	String[] from = new String[] { "Title", "Year", "Rating" };
	int[] to = new int[] { R.id.movietitle, R.id.year, R.id.rating };
	SimpleAdapter adapter;
	ArrayList<HashMap<String, String>> movieArrayList;
	private ProgressDialog pDialog;
	
	/**
	 * GOOGLE ANALYTICS LIBRARY
	 */
	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		EasyTracker.getInstance().activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance().activityStop(this); // Add this method.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i("ACTIVITY STARTED", "MoviesActivity has started.");

		// Setup our content view
		this.setContentView(R.layout.latestmovieslist);
		listView = (ListView) findViewById(R.id.listview);
		View listHeader = this.getLayoutInflater().inflate(
				R.layout.latestmovies_header, null);
		listView.addHeaderView(listHeader);

		// GET TEXT AND INITALIZE DATA
		editURI = (EditText) this.findViewById(R.id.searchField);
		editURI.setText(MovieProvider.MovieData.CONTENT_URI.toString());

		// SEARCH BUTTON AND HANDLER
		searchButton = (Button) this.findViewById(R.id.searchButton);
		searchButton.setOnClickListener(this);
	}

	/**
	 * Back button intent
	 * 
	 * @param button
	 *            the button
	 */
	public void backButton(View button) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	
	public void onClick(View v) {	
		// DISMISS THE KEYBOARD SO WE CAN SEE OUR TEXT
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editURI.getWindowToken(), 0);

		// SHOW OUR USERS A FRIENDLY DOWNLOADING PROGRESS DIALOG
	    pDialog = ProgressDialog.show(context, "Downloading", "Please wait...");
		
		
		// Handle communication between this activity and DownloadService class
		Handler dataServiceHandler = new Handler() {

			public void handleMessage(Message mymessage) {

				if (mymessage.arg1 == RESULT_OK && mymessage.obj != null) {
					
					// DISMISS OUR PROGRESS DIALOG
			        pDialog.dismiss();

			        try {
						Log.i("RESPONSE", mymessage.obj.toString());
					} catch (Exception e) {
						Log.e("ERROR", e.toString());
					}

					MovieProvider provider = new MovieProvider();
					Cursor myCursor = provider.query(Uri.parse(editURI.getText().toString()),MovieProvider.MovieData.PROJECTION, null, null,"ASC");
					if (myCursor != null) {
						int count = myCursor.getCount();
						Log.i("CURSOR", String.valueOf(count));
						if (count > 0) {
							movieArrayList = new ArrayList<HashMap<String, String>>();

							while (myCursor.moveToNext()) {
								HashMap<String, String> displayMap = new HashMap<String, String>();
								displayMap.put("Title", myCursor.getString(1));
								displayMap.put("Year", myCursor.getString(2));
								displayMap.put("Rating", myCursor.getString(3));

								movieArrayList.add(displayMap);
							}

							adapter = new SimpleAdapter(context,
									movieArrayList, R.layout.latestmovies_row,
									from, to);
							listView.setAdapter(adapter);
						} else {
							listView = null;
							Toast.makeText(context, "No movies found.",
									Toast.LENGTH_LONG).show();
							Log.i("CURSOR", "CURSOR IS 0");
						}
					} else {
						Toast.makeText(context, "No movies found.",
								Toast.LENGTH_LONG).show();
					}
				}
			}
		};

		Messenger messenger = new Messenger(dataServiceHandler);
		Intent startServiceIntent = new Intent(context, DownloadService.class);
		startServiceIntent.putExtra(DownloadService.MESSENGER_KEY, messenger);
		startService(startServiceIntent);
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		Log.i("ONSAVEINSTANCE", "ABOUT TO SAVEINSTANCE");
		savedInstanceState.putString("saved", editURI.getText().toString());
		super.onSaveInstanceState(savedInstanceState);	
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);	
		Log.i("ONRESTORESAVEINSTANCE", "ABOUT TO RESTORE");

		editURI.setText(savedInstanceState.getString("saved"));
		
		MovieProvider provider = new MovieProvider();
		Cursor myCursor = provider.query(Uri.parse(editURI.getText().toString()),MovieProvider.MovieData.PROJECTION, null, null,"ASC");
		if (myCursor != null) {
			int count = myCursor.getCount();
			Log.i("CURSOR", String.valueOf(count));
			if (count > 0) {
				movieArrayList = new ArrayList<HashMap<String, String>>();

				while (myCursor.moveToNext()) {
					HashMap<String, String> displayMap = new HashMap<String, String>();
					displayMap.put("Title", myCursor.getString(1));
					displayMap.put("Year", myCursor.getString(2));
					displayMap.put("Rating", myCursor.getString(3));

					movieArrayList.add(displayMap);
				}

				adapter = new SimpleAdapter(context,
						movieArrayList, R.layout.latestmovies_row,
						from, to);
				listView.setAdapter(adapter);
			} else {
				listView = null;
				Toast.makeText(context, "No movies found.",
						Toast.LENGTH_LONG).show();
				Log.i("CURSOR", "CURSOR IS 0");
			}
		} else {
			Toast.makeText(context, "No movies found.",
					Toast.LENGTH_LONG).show();
		}	
	}

	
}