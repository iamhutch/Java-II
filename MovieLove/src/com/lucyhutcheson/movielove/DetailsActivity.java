/*
 * project		MovieLove
 * 
 * package		com.lucyhutcheson.movielove
 * 
 * @author		Lucy Hutcheson
 * 
 * date			Jul 20, 2013
 * 
 */
package com.lucyhutcheson.movielove;


import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class DetailsActivity extends Activity {

	JSONObject json = null;
	String imageURL = null;
	String title = null;
	String linkString = null;
	
	/**
	 * GOOGLE ANALYTICS LIBRARY
	 */
	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		Log.i("GOOGLE ANALYTICS", "START");
		EasyTracker.getInstance().activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		Log.i("GOOGLE ANALYTICS", "STOP");
		EasyTracker.getInstance().activityStop(this); // Add this method.
	}


	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.detaillayout);
		Log.i("ACTIVITY STARTED", "DetailsActivity has started.");

		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			Log.i("BUNDLE EXTRAS", "BUNDLE IS NULL");
			return;
		}

		String results = extras.getString("data");
		Log.i("RESULTS", results);
		
		if (results != null) {
			JSONObject json = null;
			try {
				json = new JSONObject(results);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				// SETUP LINK URL
				linkString = json.getJSONObject("links").getString("alternate");
				// SETUP TITLE
				title = json.getString("title");
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		
			TextView titleView = (TextView) findViewById(R.id._name);
			titleView.setText(title);
		}


	// VIEW TRAILER BUTTON AND HANDLER
	Button webButton = (Button) findViewById(R.id.webButton);
	webButton.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
		    Uri intentUri = Uri.parse(linkString);
		    
		    Intent intent = new Intent();
		    intent.setAction(Intent.ACTION_VIEW);
		    intent.setData(intentUri);
		    
		    startActivity(intent);
		    
		}
	});

	// VIEW TRAILER BUTTON AND HANDLER
	Button backButton = (Button) findViewById(R.id.backButton);
	backButton.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	});


	}

	@Override
	public void finish() {
		Intent data = new Intent();
		// RETURN USER SELECTION
		data.putExtra("returnSelection", "I have viewed the movie information.");
		setResult(RESULT_OK, data);
		super.finish();
	}

}
