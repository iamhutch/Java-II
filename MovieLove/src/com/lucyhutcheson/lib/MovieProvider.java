/*
 * project		MovieLove
 * 
 * package		com.lucyhutcheson.lib
 * 
 * @author		Lucy Hutcheson
 * 
 * date			Jul 14, 2013
 * 
 */

package com.lucyhutcheson.lib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieProvider extends ContentProvider {

	public static final String AUTHORITY = "com.lucyhutcheson.movielove.movieprovider";

	public static class MovieData implements BaseColumns {

		public static final Uri CONTENT_RUI = Uri.parse("content://"
				+ AUTHORITY + "/items");

		public static final String CONTENT_TYPE = "vnd.ndroid.cursor.dir/vnd.dgardinier.museum.item";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.dgardinier.museum.item";

		// Define Columns
		public static final String MOVIE_COLUMN = "TITLE";
		public static final String YEAR_COLUMN = "YEAR";
		public static final String RATING_COLUMN = "RATING";

		public static final String[] PROJECTION = { "_Id", MOVIE_COLUMN,
				YEAR_COLUMN, RATING_COLUMN };

		private MovieData() {
		};

	}

	public static final int ITEMS = 1;
	public static final int ITEMS_ID = 2;

	private static final UriMatcher uriMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		uriMatcher.addURI(AUTHORITY, "items/", ITEMS);
		uriMatcher.addURI(AUTHORITY, "items/*", ITEMS_ID);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch (uriMatcher.match(uri)) {
		case ITEMS:
			return MovieData.CONTENT_TYPE;
		case ITEMS_ID:
			return MovieData.CONTENT_ITEM_TYPE;
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		MatrixCursor result = new MatrixCursor(MovieData.PROJECTION);
		
		String JSONString = FileFunctions.readStringFile(getContext(), "providers", false);
		JSONObject movie = null;
		JSONArray movieArray = null;
		JSONObject field = null;
		
		try {
			movie = new JSONObject(JSONString);
			movieArray = movie.getJSONArray(DownloadService.JSON_MOVIES);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// If there is not data to return, just return the cursor
		if (movieArray == null)
		{
			return result;
		}
				
		switch (uriMatcher.match(uri)) {
		case ITEMS:
			
			for (int i= 0; i<movieArray.length(); i++)
			{
				try {
					field = movieArray.getJSONObject(i).getJSONObject(DownloadService.JSON_TITLE);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		case ITEMS_ID:

		}

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
