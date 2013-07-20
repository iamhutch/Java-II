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
import android.widget.EditText;
import android.widget.TextView;

/**
 * This class receives a JSON string from Main Activity which will be used
 * to set the web button which will take the user to a web site.  The user
 * can then set a message to be sent back to Main Activity for display.
 */
@SuppressLint("HandlerLeak")
public class DetailsActivity extends Activity {

	JSONObject json = null;
	String imageURL = null;
	String title = null;
	String linkString = null;
	
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


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		// SETUP ACTIVITY
		setContentView(R.layout.detaillayout);
		Log.i("ACTIVITY STARTED", "DetailsActivity has started.");

		// GET BUNDLE EXTRAS PASSED FROM MAIN ACTIVITY
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			Log.i("BUNDLE EXTRAS", "BUNDLE IS NULL");
			return;
		}

		// ADD DATA TO STRING TO BE USED
		String results = extras.getString("data");		
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
		
			// ADD TITLE TO OUR TEXTVIEW FOR DISPLAY
			TextView titleView = (TextView) findViewById(R.id._name);
			titleView.setText(title);
		}


	// VIEW WEBSITE BUTTON AND HANDLER FOR IMPLICIT INTENT
	Button webButton = (Button) findViewById(R.id.webButton);
	webButton.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
		    Uri intentUri = Uri.parse(linkString);
		    
		    // SETUP IMPLICIT INTENT
		    Intent intent = new Intent();
		    intent.setAction(Intent.ACTION_VIEW);
		    intent.setData(intentUri);
		    startActivity(intent);
		}
	});

	// BACK BUTTON AND HANDLER
	Button backButton = (Button) findViewById(R.id.backButton);
	backButton.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	});


	}

	/* (non-Javadoc)
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {
		Intent data = new Intent();
		// RETURN DYNAMIC USER DATA
		EditText editText = (EditText) findViewById(R.id.editText1);
		String message = editText.getText().toString();
		data.putExtra("returnMessage", message);
		setResult(RESULT_OK, data);
		super.finish();
	}

}
