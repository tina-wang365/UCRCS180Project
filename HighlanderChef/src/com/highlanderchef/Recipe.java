package com.highlanderchef;

import java.util.ArrayList;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import android.graphics.Bitmap;


public class Recipe {
	String name;
	String description;

	Bitmap mainImage;

	ArrayList<String> categories;
	ArrayList<Ingredient> ingredients;
	ArrayList<Direction> directions;
	//add categories, description, img_url, name, rid

	public Recipe(String name, String description, Bitmap mainImage) {
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
				Double amount = mapper.readValue(ing.path("amount"), Double.class);
				Ingredient ingr = new Ingredient(name, amount);
				ingredients.add(ingr);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
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

}
