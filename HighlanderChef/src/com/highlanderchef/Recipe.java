package com.highlanderchef;

import java.util.ArrayList;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import android.media.Image;


public class Recipe {
	String name;
	String description;

	Image mainImage;

	ArrayList<String> categories;
	ArrayList<Ingredient> ingredients;
	ArrayList<Direction> directions;
	//add categories, description, img_url, name, rid

	public Recipe(String name, String description, Image mainImage) {
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

	public void parseDirectionsFromJson(String json) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			JsonNode node = mapper.readTree(json);
			Iterator<JsonNode> ite = node.getElements();
			while (ite.hasNext()) {
				JsonNode dir = ite.next();
				String text = mapper.readValue(dir.path("text"), String.class);
				//String text = dir.path("text").getTextValue();

				Direction d = new Direction(text);
				directions.add(d);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
