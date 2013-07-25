package com.lucyhutcheson.movielove;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.lucyhutcheson.movielove.FavoritesFragment.FavoritesListener;

public class FavoritesActivity extends Activity implements FavoritesListener {

	Context _context;
	ArrayList<String> _favorites = new ArrayList<String>();
	String _selected;
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("ACTIVITY STARTED", "Favorites Activity has started.");

		// Setup our content view
		setContentView(R.layout.favoritesfrag);		
		
		_context = this;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void finish(){
		Intent data = new Intent();
		data.putExtra("selectedmovie", _selected);
		setResult(RESULT_OK, data);
		super.finish();
	}
		
	@Override
	public void onFavoriteSelected(String movie) {
		_selected = movie;
		finish();
	}

}
