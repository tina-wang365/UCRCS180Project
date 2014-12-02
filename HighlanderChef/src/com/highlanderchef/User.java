package com.highlanderchef;

import java.util.ArrayList;

public class User {
	private static volatile int id;
	private static volatile String email = "";
	private static ArrayList<Recipe> favorites;
	private static ArrayList<Integer> followers; //holds id of followers
	private static ArrayList<Integer> following; //holds id of following
	private static ArrayList<Integer> recipes;   //holds id of recipes this user had made
	private static Recipe draft;

	public User() {
		email = "";
		favorites = new ArrayList<Recipe>();
		followers = new ArrayList<Integer>();
		following = new ArrayList<Integer>();
		recipes = new ArrayList<Integer>();
		draft = new Recipe();
	}
	public User(int id, String email, ArrayList<Recipe> favorites, ArrayList<Integer> followers, ArrayList<Integer> following, ArrayList<Integer> recipes, Recipe draft)
	{
		this.id = id;
		this.email = email;
		this.favorites = favorites;
		this.followers = followers;
		this.following = following;
		this.recipes = recipes;
		this.draft = draft;
	}
	public int getID() {
		return id;
	}
	public String getEmail() {
		return email;
	}
	public ArrayList<Recipe> getFavorites() {
		return favorites;
	}
	public ArrayList<Integer> getFollowers() {
		return followers;
	}
	public ArrayList<Integer> getFollowing() {
		return following;
	}
	public ArrayList<Integer> getRecipes() { //already have a similar function searchRecipesByUID() in Comm.java
		return recipes;
	}
	public void setID(int id) {
		this.id = id;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setFavorites(ArrayList<Recipe> favorites) {
		this.favorites = favorites;
	}
	public void setFollowers(ArrayList<Integer> followers) {
		this.followers = followers;
	}
	public void setFollowing(ArrayList<Integer> following) {
		this.following = following;
	}

	public void follow(Integer uid) {
		this.following.add(uid);
	}
	public void unfollow(Integer uid) {
		this.following.remove(uid);
	}

	public void addRecipe(int rid) {
		recipes.add(id);
	}
	public void deleteDraft() { //based on specs there can only be one draft. either finish that draft or delete it
		draft = new Recipe();
	}


}
