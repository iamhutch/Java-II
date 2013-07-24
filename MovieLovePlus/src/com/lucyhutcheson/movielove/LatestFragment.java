package com.lucyhutcheson.movielove;

import com.lucyhutcheson.movielove.FormFragment.FormListener;

import android.app.Activity;
import android.app.Fragment;

public class LatestFragment extends Fragment {
	
	// Calling activity
	private LatestListener listener;
	
	public interface LatestListener {

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
	

}
