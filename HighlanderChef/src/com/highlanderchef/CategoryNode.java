package com.highlanderchef;

import java.util.ArrayList;

public class CategoryNode {
	public final int id;
	public final String name;
	public ArrayList<CategoryNode> children;

	public CategoryNode(int id, String name) {
		this.id = id;
		this.name = name;
		this.children = null;
	}

	public void addChild(int id, String name) {
		if (children == null) {
			children = new ArrayList<CategoryNode>();
		}

		children.add(new CategoryNode(id, name));
	}
}