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
import android.util.Log;

/**
 * MovieProvider class to provide content to user based on filters entered into the MoviesActivity view.
 */
public class MovieProvider extends ContentProvider {

	public static final String AUTHORITY = "com.lucyhutcheson.movielove.movieprovider";

	/**
	 * Class that contains data variables and column setup.
	 */
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

		/**
		 * Instantiates a new movie data.
		 */
		private MovieData() {
		};

	}
	// More Variable setup
	public static final int ITEMS = 1;
	public static final int ITEMS_ID = 2;
	public static final int ITEMS_TITLE_FILTER = 3;
	public static final int ITEMS_YEAR_FILTER = 4;
	public static final int ITEMS_RATING_FILTER = 5;

	// Setup our URI MATCHER
	private static final UriMatcher uriMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	// Add URI "match" entries
	static {
		uriMatcher.addURI(AUTHORITY, "movies/", ITEMS);
		uriMatcher.addURI(AUTHORITY, "movies/title/*", ITEMS_TITLE_FILTER);
		uriMatcher.addURI(AUTHORITY, "movies/year/*", ITEMS_YEAR_FILTER);
		uriMatcher.addURI(AUTHORITY, "movies/rating/*", ITEMS_RATING_FILTER);
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// NOT BEING USED RIGHT NOW
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// NOT BEING USED RIGHT NOW
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		return false;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// Setup our variables
		MatrixCursor result = new MatrixCursor(projection);
		MoviesSingletonClass _Movies = MoviesSingletonClass.getInstance();
		String JSONString = _Movies.get_movies();
		JSONObject movie = null;
		JSONArray movieArray = null;
		JSONObject field = null;
		
		// Convert data for future use
		try {
			movie = new JSONObject(JSONString);
			movieArray = movie.getJSONArray(DownloadService.JSON_MOVIES);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// If there is not data to return, just return the cursor
		if (movieArray == null) {
			Log.e("QUERY", "NULL");
			return result;
		}

		// DETERMINE IF WE ARE FILTERING OUT THE DATA OR RETURNING ALL
		switch (uriMatcher.match(uri)) {
		case ITEMS:
			// AS LONG AS WE HAVE AN ARRAY TO SEARCH THROUGH
			for (int i = 0; i < movieArray.length(); i++) {
				// RETURN ALL DATA
				try {
					field = movieArray.getJSONObject(i);
					result.addRow(new Object[] { i + 1,
							field.get(DownloadService.JSON_TITLE),
							field.get(DownloadService.JSON_YEAR),
							field.get(DownloadService.JSON_RATING) });
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
			
		case ITEMS_TITLE_FILTER:
			
			String titleRequested = uri.getLastPathSegment();

			for (int i = 0; i < movieArray.length(); i++) {
				try {
					field = movieArray.getJSONObject(i);
					if (field.getString(DownloadService.JSON_TITLE)
							.contentEquals(titleRequested)) {
						result.addRow(new Object[] { i + 1,
								field.get(DownloadService.JSON_TITLE),
								field.get(DownloadService.JSON_YEAR),
								field.get(DownloadService.JSON_RATING) });
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
			
		case ITEMS_YEAR_FILTER:
			String yearRequested = uri.getLastPathSegment();

			for (int i = 0; i < movieArray.length(); i++) {
				try {
					field = movieArray.getJSONObject(i);
					if (field.getString(DownloadService.JSON_YEAR)
							.contentEquals(yearRequested)) {
						result.addRow(new Object[] { i + 1,
								field.get(DownloadService.JSON_TITLE),
								field.get(DownloadService.JSON_YEAR),
								field.get(DownloadService.JSON_RATING) });
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		case ITEMS_RATING_FILTER:
			String ratingRequested = uri.getLastPathSegment();

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
					e.printStackTrace();
				}
			}
			break;
		// IF THERE WAS AN INVALID URI ENTERED INTO OUR EDITTEXT
		default:
			Log.e("QUERY", "INVALID URI + " + uri.toString());

		}

		return result;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// NOT BEING USED RIGHT NOW
		throw new UnsupportedOperationException();
	}

}
