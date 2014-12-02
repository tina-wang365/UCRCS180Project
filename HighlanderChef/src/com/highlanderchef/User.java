package com.highlanderchef;

import java.util.ArrayList;

public class User {
	private static volatile int id;
	private static volatile String email = "";

	/*
	 * rids of my current recipes,
	 * draft ids of my drafts,
	 * rids of my favorite recipes,
	 * rids of favorite recipes,
	 * uids of followers and following
	 */
	private static ArrayList<Integer> recipes;
	private static ArrayList<Integer> drafts;
	private static ArrayList<Integer> favorites;
	private static ArrayList<Integer> followers;
	private static ArrayList<Integer> following;

	public User() {
		email = "";
		favorites = new ArrayList<Integer>();
		followers = new ArrayList<Integer>();
		following = new ArrayList<Integer>();
		recipes = new ArrayList<Integer>();
		drafts = new ArrayList<Integer>();
	}
	public User(int id, String email, ArrayList<Integer> favorites, ArrayList<Integer> followers, ArrayList<Integer> following, ArrayList<Integer> recipes, ArrayList<Integer> drafts)
	{
		this.id = id;
		this.email = email;
		this.favorites = favorites;
		this.followers = followers;
		this.following = following;
		this.recipes = recipes;
		this.drafts = drafts;
	}
	public int getID() {
		return id;
	}
	public String getEmail() {
		return email;
	}
	public ArrayList<Integer> getFavorites() {
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
	public void setFavorites(ArrayList<Integer> favorites) {
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

}
