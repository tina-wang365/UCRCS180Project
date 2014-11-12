package com.highlanderchef;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Bitmap;

public class Direction implements Serializable
{
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
		this.images = bmps;
	}

	public String getDirectionText()
	{
		return this.text;
	}



}
