package com.highlanderchef;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;

public class Direction implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7310913146457605035L;

	String text;

	ArrayList<Bitmap> images;

	public Direction(String text) {
		this.text = text;
	}

	public Direction(String text, Bitmap bmp) {
		this.text = text;
		this.images = new ArrayList<Bitmap>();
		images.add(bmp);
	}

	public Direction(String text, ArrayList<Bitmap> bmps) {
		this.text = text;
		this.images = (ArrayList<Bitmap>)bmps.clone();

	}

	public String getDirectionText()
	{
		return this.text;
	}

	public void putIntoIntent(Intent intent, String key)
	{

	}

}
