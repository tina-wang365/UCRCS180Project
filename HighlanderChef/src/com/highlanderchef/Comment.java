package com.highlanderchef;

public class Comment {
	public int recipeID;
	public int rating;
	public String comment;

	Comment(int recipeID, int rating, String comment) {
		this.recipeID = recipeID;
		this.rating = rating;
		this.comment = comment;
	}

	Comment(int recipeID, float rating, String comment) {
		this.recipeID = recipeID;
		this.rating = (int)rating;
		this.comment = comment;
	}
}
