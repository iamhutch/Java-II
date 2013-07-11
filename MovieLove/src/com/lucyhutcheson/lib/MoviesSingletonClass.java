package com.lucyhutcheson.lib;

public class MoviesSingletonClass {

	/**********************
	 * Private members
	 **********************/
	// Will hold single instance of this class
	private static MoviesSingletonClass _instance = null;
	// Will hold JSON string of movies
	private String _movies;
	
	
	/**********************
	 * Private Methods
	 **********************/
	// Used to hide constructor and prevent other classes from instantiating
	protected MoviesSingletonClass() {
	}
	
	/**********************
	 * Public method
	 **********************/
	public static MoviesSingletonClass getInstance() {
		// Create a new instantiation if there isn't one
		if(_instance == null){
			_instance = new MoviesSingletonClass();
		}
		// Otherwise, just return the one already created
		return _instance;
	}
	
	/***************************
	 * Public setters / getters
	 ***************************/
	public String get_movies(){
		return _movies;
	}
	public Boolean set_movies(String movie) {
		this._movies = movie;
		return true;
	}
	
}
