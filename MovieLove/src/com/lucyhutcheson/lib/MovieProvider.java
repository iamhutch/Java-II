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

import java.util.ArrayList;
import java.util.HashMap;

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
import android.util.Log;

public class MovieProvider extends ContentProvider {

	public static final String AUTHORITY = "com.lucyhutcheson.movielove.movieprovider";

	public static class MovieData implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/movies");

		public static final String CONTENT_TYPE = "vnd.ndroid.cursor.dir/vnd.lucyhutcheson.movielove.item";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.lucyhutcheson.movielove.item";

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
	public static final int ITEMS_YEAR_FILTER = 3;
	public static final int ITEMS_RATING_FILTER = 4;

	private static final UriMatcher uriMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	// Add URI "match" entries
	static {
		uriMatcher.addURI(AUTHORITY, "movies/", ITEMS);
		uriMatcher.addURI(AUTHORITY, "movies/#", ITEMS_ID);
		uriMatcher.addURI(AUTHORITY, "movies/year/*", ITEMS_YEAR_FILTER);
		uriMatcher.addURI(AUTHORITY, "movies/rating/*", ITEMS_RATING_FILTER);
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

		MatrixCursor result = new MatrixCursor(projection);
		Log.i("MOVIE PROVIDER RESULT", result.toString());

		MoviesSingletonClass _Movies = MoviesSingletonClass.getInstance();
		String JSONString = _Movies.get_movies();
		Log.i("MOVIE PROVIDER", JSONString);

		JSONObject movie = null;
		JSONArray movieArray = null;
		JSONObject field = null;
		try {
			Log.i("TRYING MOVIE PROVIDER", "IN TRY");
			movie = new JSONObject(JSONString);
			Log.i("TRYING MOVIE PROVIDER", "AFTER JSONOBJECT");
			movieArray = movie.getJSONArray(DownloadService.JSON_MOVIES);
			Log.i("TRYING MOVIE PROVIDER", "AFTER JSONARRAY");
			Log.i("TRYING MOVIE PROVIDER", movieArray.toString());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// If there is not data to return, just return the cursor
		if (movieArray == null) {
			Log.e("QUERY", "NULL");
			return result;
		}
		Log.i("URIMATCHER SWITCH", uri.toString());
		
		switch (uriMatcher.match(uri)) {
		case ITEMS:

			for (int i = 0; i < movieArray.length(); i++) {
				try {
					Log.i("TRYING URIMATCHER", "IN TRY");
					field = movieArray.getJSONObject(i);
					// Log.i("TRYING URIMATCHER FIELD", field.toString());

					result.addRow(new Object[] { i + 1,
							field.get(DownloadService.JSON_TITLE),
							field.get(DownloadService.JSON_YEAR),
							field.get(DownloadService.JSON_RATING) });
					// Log.i("TRYING URIMATCHER RESULT ADD ROW",
					// result.toString());

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			break;
		case ITEMS_YEAR_FILTER:
			String yearRequested = uri.getLastPathSegment();
			Log.i("ITEMS_YEAR_FILTER", yearRequested);

			Log.i("MOVIE ARRAY LENGTH", String.valueOf(movieArray.length()));
			
			for (int i = 0; i < movieArray.length(); i++) {
				try {
					field = movieArray.getJSONObject(i);
					Log.i("ITEMS_YEAR_FILTER FIELD", field.toString());

					if (field.getString(DownloadService.JSON_YEAR)
							.contentEquals(yearRequested)) {
						result.addRow(new Object[] { i + 1,
								field.get(DownloadService.JSON_TITLE),
								field.get(DownloadService.JSON_YEAR),
								field.get(DownloadService.JSON_RATING) });
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}

			break;

		case ITEMS_RATING_FILTER:
			String ratingRequested = uri.getLastPathSegment();
			Log.i("ITEMS_RATING_FILTER", ratingRequested);

			for (int i = 0; i < movieArray.length(); i++) {
				try {
					field = movieArray.getJSONObject(i);

					if (field.getString(DownloadService.JSON_RATING)
							.contentEquals(ratingRequested)) {
						result.addRow(new Object[] { i + 1,
								field.get(DownloadService.JSON_TITLE),
								field.get(DownloadService.JSON_YEAR),
								field.get(DownloadService.JSON_RATING) });
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			break;

		default:
			Log.e("QUERY", "INVALID URI + " + uri.toString());

		}

		// TODO Auto-generated method stub
		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
