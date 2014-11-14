package com.highlanderchef;

import java.io.Serializable;

public class Ingredient implements Serializable
{
	public String name;

	public String amount; //ex. 3, 1/2 cup, 5 ounces.

	public Ingredient(String name, String amount) {
		this.name = name;
		this.amount = amount;
	}

	public String getName(){ return this.name; }
	public void setName(String n){this.name = n;}

	public String getAmount(){return this.amount; }
	public void setAmount(String a){this.amount = a; }
}

// Picture?