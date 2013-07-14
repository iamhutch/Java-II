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
import org.json.JSONException;
import org.json.JSONObject;

import com.lucyhutcheson.lib.DownloadService;
import com.lucyhutcheson.lib.MoviesSingletonClass;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * The Class MoviesActivity provides the view controller for the page that will contain
 * a list of the latest movies when initiated by the user from the main activity.
 */
@SuppressLint("HandlerLeak")
public class MoviesActivity extends ListActivity {
		
	// Setup variables
	Context context = this;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("LATEST MOVIE", "LATEST MOVIE ACTIVITY STARTED");
		setContentView(R.layout.latestmovieslist);
		
		Handler dataServiceHandler = new Handler() {
			
			public void handleMessage(Message mymessage){
				
				if (mymessage.arg1 == RESULT_OK && mymessage.obj != null){
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
	}
	
	/**
	 * Display data.
	 */
	public void displayData(){
		Log.i("MOVIESACTIVITY", "In DISPLAY DATA" );
		
		MoviesSingletonClass _Movies = MoviesSingletonClass.getInstance();

		String JSONString = _Movies.get_movies();
		Log.i("JSONSTRING", JSONString );
		ArrayList<HashMap<String, String>> myMovieList = new ArrayList<HashMap<String, String>>();
		JSONObject movies = null;
		JSONArray singleMovie = null;
		final String[] movieTitles;
		ArrayList<String> movieArrayList = new ArrayList<String>();
		
		try{
			Log.i("DISPLAY DATA", "TRYING" );
			movies = new JSONObject(JSONString);
			Log.i("MOVIES DATA", movies.toString() );
			singleMovie = movies.getJSONArray("movies");
			Log.i("SINGLE MOVIES DATA", singleMovie.toString() );
			for (int i= 0; i<10; i++)
			{
				JSONObject movieObject = singleMovie.getJSONObject(i);
				Log.i("JSON OBJECT MOVIES DATA", movieObject.toString() );
				String title = movieObject.getString("title");
				Log.i("JSON OBJECT MOVIES DATA TITLE", title );
				movieArrayList.add(title);
				//HashMap<String, String> displayMap = new HashMap<String,String>();
				//displayMap.put("title", title);
				//myMovieList.add(displayMap);
			}
			

			setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, movieArrayList));
			
			ListView listView = getListView();
			listView.setTextFilterEnabled(true);
			
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
				    // When clicked, show a toast with the TextView text
				    Toast.makeText(getApplicationContext(),
					((TextView) view).getText(), Toast.LENGTH_SHORT).show();
				}
			});

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Adds the row btn.
	 *
	 * @param button the button
	 */
	public void backButton(View button){
	    Intent intent = new Intent(this, MainActivity.class);
	    startActivity(intent);
	} 

}
