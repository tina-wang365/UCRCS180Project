package com.highlanderchef;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import android.graphics.Bitmap;


public class Recipe implements Serializable{
	int id;
	String name;
	String description;
	String cookTime;
	Bitmap mainImage;

	ArrayList<String> categories;
	ArrayList<Ingredient> ingredients;
	ArrayList<Direction> directions;
	//add categories, description, img_url, name, rid

	public Recipe(int id, String name, String description, Bitmap mainImage) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.mainImage = mainImage;

		categories = new ArrayList<String>();
		ingredients = new ArrayList<Ingredient>();
		directions = new ArrayList<Direction>();
	}

	public void parseIngredientsFromJson(String json) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			JsonNode node = mapper.readTree(json);
			Iterator<JsonNode> ite = node.getElements();
			while (ite.hasNext()) {
				JsonNode ing = ite.next();
				String name = mapper.readValue(ing.path("name"), String.class);
				String amount = mapper.readValue(ing.path("amount"), String.class);
				Ingredient ingr = new Ingredient(name, amount);
				ingredients.add(ingr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addDirection(String text, Bitmap bmp) {
		directions.add(new Direction(text, bmp));
	}

	public void addDirection(String text, ArrayList<Bitmap> bmps) {
		directions.add(new Direction(text, bmps));
	}

	public void parseCategoriesFromJson(String json){
		ObjectMapper mapper = new ObjectMapper();

		try	{
			JsonNode node = mapper.readTree(json);
			Iterator<JsonNode> ite = node.getElements();
			while(ite.hasNext()) {
				JsonNode cat = ite.next();
				String text = cat.getTextValue();
				//for now do them as strings
				//Category c = new Category(text);
				categories.add(text);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

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
	public Direction getADirection(int index)
	{
		return this.directions.get(index);
	}
	public void AddADirection(String dir, Bitmap bmp)
	{
		this.directions.add(new Direction(dir, bmp));
	}
	public void AddADirection(String dir)
	{
		this.directions.add(new Direction(dir));
	}
	public int ingredientSize()
	{
		return this.ingredients.size();
	}
	public int directionSize()
	{
		return this.directions.size();
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return this.name;
	}
	public void setDescription(String d)
	{
		this.description = d;
	}
	public String getDescription()
	{
		return this.description;
	}
	public void setCookTime(String ct)
	{
		this.cookTime = ct;
	}
	public String getCookTime()
	{
		return this.cookTime;
	}

}
