package com.lucyhutcheson.lib;

public class MoviesSingletonClass {

	/**********************
	 * Private members
	 **********************/
	// Will hold single instance of this class
	private static MoviesSingletonClass _instance = null;
	// Will hold JSON string of movies
	private String _movies;
	
	
	protected MoviesSingletonClass() {
		
	}
	
	/**********************
	 * Private Methods
	 **********************/
	
	/**********************
	 * Public method
	 **********************/
	public static MoviesSingletonClass getInstance() {
		if(_instance == null){
			_instance = new MoviesSingletonClass();
		}
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
