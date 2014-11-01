package com.highlanderchef;

public class GUIManager {
	private static GUIManager instance;
	private static String authToken;

	protected GUIManager() {
		// Exists only to defeat instantiation.
	}

	public static GUIManager getInstance() {
		if (instance == null) {
			return new GUIManager();
		}
		return instance;
	}

	public void doLogin(String email, String password) {
		Comm c = new Comm();
		c.login(email, password);
		while(!c.responseReady()) {
			CommResponse cr = Comm.getInstance().getResponse();
		}
	}
}
