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

import com.lucyhutcheson.lib.MoviesSingletonClass;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
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
public class MoviesActivity extends ListActivity {
	
	static final String[] FRUITS = new String[] { "Apple", "Avocado", "Banana",
		"Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
		"Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple" };
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("LATEST MOVIE", "LATEST MOVIE ACTIVITY STARTED");
		setContentView(R.layout.latestmovieslist);
		
	}
	
	public void displayData(){
		MoviesSingletonClass _Movies = MoviesSingletonClass.getInstance();

		String JSONString = _Movies.get_movies();
		ArrayList<HashMap<String, String>> myMovieList = new ArrayList<HashMap<String, String>>();
		JSONObject movies = null;
		JSONArray singleMovie = null;
		
		try{
			movies = new JSONObject(JSONString);
			singleMovie = movies.getJSONArray("movies");
			int movieLength = movies.length();
			Log.i("MOVIE LENGTH", getString(movieLength) );
			for (int i= 0; i<movieLength; i++)
			{
				JSONObject movieObject = singleMovie.getJSONObject(i);
				String title = movieObject.getString("title");
				
				HashMap<String, String> displayMap = new HashMap<String,String>();
				displayMap.put("title", title);
				myMovieList.add(displayMap);
			}
			
			final String[] movieTitles = new String[] { "title" };

			setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, movieTitles));
			
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
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Adds the row btn.
	 *
	 * @param button the button
	 */
	public void addRowBtn(View button){
	    Intent intent = new Intent(this, MainActivity.class);
	    startActivity(intent);
	} 

}
