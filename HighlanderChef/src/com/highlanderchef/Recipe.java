package com.highlanderchef;

import java.util.ArrayList;

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

	public Recipe(String name, String description, Image mainImage) {
		this.name = name;
		this.description = description;
		this.mainImage = mainImage;

		categories = new ArrayList<String>();
		ingredients = new ArrayList<Ingredient>();
		directions = new ArrayList<Direction>();
	}

	public void parseIngredientsFromJson(JsonNode node) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			Ingredient[] list = mapper.readValue(node, Ingredient[].class);
			for(int i = 0; i < list.length; i++) {
				ingredients.add(list[i]);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
