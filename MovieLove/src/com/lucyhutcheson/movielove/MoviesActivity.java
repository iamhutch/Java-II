package com.lucyhutcheson.movielove;

import com.lucyhutcheson.java1project.R;

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

public class MoviesActivity extends ListActivity {
	
	static final String[] FRUITS = new String[] { "Apple", "Avocado", "Banana",
		"Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
		"Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple" };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("LATEST MOVIE", "LATEST MOVIE ACTIVITY STARTED");
		setContentView(R.layout.latestmovieslist);

		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FRUITS));
	
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
	
	}
	public void addRowBtn(View button){
	    Intent intent = new Intent(this, MainActivity.class);
	    startActivity(intent);
	} 

}
