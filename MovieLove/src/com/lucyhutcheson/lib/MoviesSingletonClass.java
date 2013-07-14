/*
 * project		MovieLove
 * 
 * package		com.lucyhutcheson.lib
 * 
 * @author		Lucy Hutcheson
 * 
 * date			Jul 11, 2013
 * 
 */
package com.lucyhutcheson.lib;

/**
 * Singleton to hold the searched-for movie JSON data as well as the latest movies
 * JSON data when initiated by the user in the main activity.
 */
public class MoviesSingletonClass {

	/**********************
	 * Private members
	 **********************/
	// Will hold single instance of this class
	private static MoviesSingletonClass _instance = null;
	// Will hold JSON string of movies
	private String _movies;
	
	
	/**
	 * ********************
	 * Private Methods
	 * ********************.
	 */
	// Used to hide constructor and prevent other classes from instantiating
	protected MoviesSingletonClass() {
	}
	
	/**
	 * ********************
	 * Public method
	 * ********************.
	 *
	 * @return single instance of MoviesSingletonClass
	 */
	public static MoviesSingletonClass getInstance() {
		// Create a new instantiation if there isn't one
		if(_instance == null){
			_instance = new MoviesSingletonClass();
		}
		// Otherwise, just return the one already created
		return _instance;
	}
	
	/**
	 * *************************
	 * Public setters / getters
	 * *************************.
	 *
	 * @return the _movies
	 */
	public String get_movies(){
		return _movies;
	}
	
	/**
	 * Set_movies.
	 *
	 * @param string the movie
	 */

	public void set_movies(String string) {
		this._movies = string;
	}
	
}
