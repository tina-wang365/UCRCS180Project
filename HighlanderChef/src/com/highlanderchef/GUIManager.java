package com.highlanderchef;

public class GUIManager {
	private static GUIManager instance;

	protected GUIManager() {
		// Exists only to defeat instantiation.
	}

	public static GUIManager getInstance() {
		if (instance == null) {
			return new GUIManager();
		}
		return instance;
	}


}
