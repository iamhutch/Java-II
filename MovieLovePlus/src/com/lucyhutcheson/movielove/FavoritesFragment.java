package com.lucyhutcheson.movielove;

import java.util.ArrayList;
import java.util.Arrays;

import com.lucyhutcheson.lib.FileFunctions;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class FavoritesFragment extends Fragment {

	private ArrayList<String> _movies;
	private ArrayAdapter<String> _listAdapter;
	private FavoritesListener listener;

	public interface FavoritesListener {
		public void onFavoriteSelected(String movie);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (FavoritesListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement FavoritesListener");
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String favString = FileFunctions.readStringFile(getActivity(), "favoritestring", true);
		String[] favArray = favString.split(";");
		_movies = new ArrayList<String>(Arrays.asList(favArray));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.i("FAVORITES FRAGMENT", "FRAGMENT STARTED");
		LinearLayout view = (LinearLayout) inflater.inflate(
				R.layout.favoriteslist, container, false);

		// ATTACH LIST ADAPTER
		ListView list = (ListView) view.findViewById(R.id.favoriteslistview);
		_listAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, _movies);
		list.setAdapter(_listAdapter);

		// LIST INTERACTION
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				listener.onFavoriteSelected(_movies.get(pos));
			};
		});

		// CANCEL BUTTON
		Button cancel = (Button) view.findViewById(R.id.cancelButton);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.onFavoriteSelected(null);
			}
		});

		return view;
	}

}
