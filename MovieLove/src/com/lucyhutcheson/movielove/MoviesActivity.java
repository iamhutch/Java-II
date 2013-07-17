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
import org.json.JSONArray;
import org.json.JSONObject;

import com.lucyhutcheson.lib.DownloadService;
import com.lucyhutcheson.lib.MoviesSingletonClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Class MoviesActivity provides the view controller for the page that will
 * contain a list of the latest movies when initiated by the user from the main
 * activity.
 */
@SuppressLint("HandlerLeak")
public class MoviesActivity extends Activity {

	// Setup variables
	Context context = this;
	ListView listView;

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
		View listHeader = this.getLayoutInflater().inflate(R.layout.latestmovies_header, null);
		listView.addHeaderView(listHeader);

		// Handle communication between this activity and DownloadService class
		Handler dataServiceHandler = new Handler() {

			public void handleMessage(Message mymessage) {

				if (mymessage.arg1 == RESULT_OK && mymessage.obj != null) {
					try {
						Log.i("RESPONSE", mymessage.obj.toString());
						displayData();
					} catch (Exception e) {
						Log.e("ERROR", e.toString());
					}
				}
			}
		};

		Messenger messenger = new Messenger(dataServiceHandler);
		Intent startServiceIntent = new Intent(context, DownloadService.class);
		startServiceIntent.putExtra(DownloadService.MESSENGER_KEY, messenger);
		startService(startServiceIntent);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id)

			{
				// When clicked, show a toast with the TextView text
				Toast.makeText(getApplicationContext(),
						((TextView) view).getText(),

						Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * Display data.
	 */
	public void displayData() {

		MoviesSingletonClass _Movies = MoviesSingletonClass.getInstance();
		ArrayList<HashMap<String, String>> movieArrayList = new ArrayList<HashMap<String, String>>();
		String JSONString = _Movies.get_movies();
		JSONObject movies = null;
		JSONArray singleMovies = null;

		try {

			movies = new JSONObject(JSONString);
			singleMovies = movies.getJSONArray("movies");
			String[] from = new String[] { "Title", "Year", "Rating" };
			int[] to = new int[] { R.id.movietitle, R.id.year, R.id.rating };

			// Add just the latest 10 movies to my array list
			for (int i = 0; i < 10; i++) {
				JSONObject movieObject = singleMovies.getJSONObject(i);
				String title = movieObject.getString("title");
				String year = movieObject.getString("year");
				String mpaa_rating = movieObject.getString("mpaa_rating");

				HashMap<String, String> displayMap = new HashMap<String, String>();
				displayMap.put("Title", title);
				displayMap.put("Year", year);
				displayMap.put("Rating", mpaa_rating);

				movieArrayList.add(displayMap);
			}

			SimpleAdapter adapter = new SimpleAdapter(this, movieArrayList,
					R.layout.latestmovies_row, from, to);
			listView.setAdapter(adapter);

		} catch (Exception e) {
			e.printStackTrace();
		}

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
	

}