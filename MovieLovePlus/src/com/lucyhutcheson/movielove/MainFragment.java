package com.lucyhutcheson.movielove;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainFragment extends Fragment {

	// Calling activity
	private FormListener listener;
	
	public interface FormListener {
		public void onMovieSearch(String movie);
		public void onLatestList();
		public void onAddFavorite();
		public void onClear();
		public void onFavoritesList();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		Log.i("MAIN FRAGMENT", "FRAGMENT STARTED");
		View view = inflater.inflate(R.layout.form, container, false);
		
		// SEARCH BUTTON AND HANDLER
		Button searchButton = (Button) view.findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText _searchField = (EditText) getActivity().findViewById(R.id.searchField);

				// DISMISS THE KEYBOARD SO WE CAN SEE OUR TEXT
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(_searchField.getWindowToken(), 0);

				// CLEAR OUT ALL FIELDS
				((TextView) getActivity().findViewById(R.id._name)).setText("");
				((TextView) getActivity().findViewById(R.id._rating)).setText("");
				((TextView) getActivity().findViewById(R.id._year)).setText("");
				((TextView) getActivity().findViewById(R.id._mpaa)).setText("");
				((TextView) getActivity().findViewById(R.id._synopsis)).setText("");

				listener.onMovieSearch(_searchField.getText().toString());
			}
		});
		
		
		// ADD TO FAVORITES BUTTON AND HANDLER
		Button addButton = (Button) view.findViewById(R.id.addFavButton);
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onAddFavorite();
			}
		});
		
		// VIEW LATEST MOVIES BUTTON AND HANDLER
		Button latestButton = (Button) view.findViewById(R.id.viewLatest);
		latestButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onLatestList();
			}
		});

		// CLEAR BUTTON AND HANDLER
		Button clearButton = (Button) view.findViewById(R.id.clearButton);
		clearButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onClear();
			}
		});

		// VIEW MOVIE INFO BUTTON AND HANDLER
		Button favButton = (Button) view.findViewById(R.id.viewFavButton);
		favButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onFavoritesList();
			}
		});

		return view;
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		
		try {
			// CAST ACTIVITY TO FORMLISTENER
			listener = (FormListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement FormListener");
		}
	}
	
}
