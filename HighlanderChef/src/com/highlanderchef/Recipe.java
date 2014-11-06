package com.highlanderchef;

import java.util.ArrayList;

import android.media.Image;


public class Recipe {
	String name;

	Image mainImage;

	ArrayList<Ingredient> ingredients;
	ArrayList<Direction> directions;

	public Recipe()
	{
		ingredients = new ArrayList<Ingredient>();
		directions = new ArrayList<Direction>();
	}

	public void setIngredients(ArrayList<Ingredient> i)
	{
		this.ingredients = i;
	}
	public ArrayList<Ingredient> getIngredients()
	{
		return this.ingredients;
	}
	public void addIngredient(Ingredient i)
	{
		ingredients.add(i);
	}
	public void setAnIngredient(int index, Ingredient i)
	{
		this.ingredients.set(index, i);
	}
	public Ingredient getAnIngredient(int index)
	{
		return this.ingredients.get(index);
	}
	public int ingredientSize()
	{
		return this.ingredients.size();
	}
	public int directionSize()
	{
		return this.directions.size();
	}


}
