package com.lucyhutcheson.movielove;

import com.lucyhutcheson.lib.MovieProvider;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LatestFragment extends Fragment {
	
	EditText editURI;
	private LatestListener listener;
	
	public interface LatestListener {
		public void onLoadLatest(String URI);
		public void onBackButton();
	}
	

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			listener = (LatestListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement LatestListener.");
		}
	}

	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		Log.i("LATEST FRAGMENT", "FRAGMENT STARTED");
		View view =  inflater.inflate(R.layout.latestmovieslist, container, false);

		// GET TEXT AND INITALIZE DATA
		editURI = (EditText) view.findViewById(R.id.searchField);
		editURI.setText(MovieProvider.MovieData.CONTENT_URI.toString());

		// VIEW LATEST MOVIES BUTTON AND HANDLER
		Button backButton = (Button) view.findViewById(R.id.backbutton);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onBackButton();
			}
		});

		// VIEW LATEST MOVIES BUTTON AND HANDLER
		Button loadLatest = (Button) view.findViewById(R.id.searchButton);
		loadLatest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onLoadLatest(editURI.getText().toString());
			}
		});


		return view;
	}
	

}
