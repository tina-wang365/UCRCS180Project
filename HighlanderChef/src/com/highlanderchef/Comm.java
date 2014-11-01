package com.highlanderchef;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class Comm {
	private static Comm instance;
	private static String lastJSON;
	private static String authToken;

	public int SUCCESS = 0;
	public int CONN_FAILED = -1;
	public int CONN_TIMEOUT = -2;
	public int JSON_ERROR = -3;
	public int NOT_IMPL = -42;

	protected Comm() {
		// Exists only to defeat instantiation.
	}

	public static void main(String[] args) {
		getInstance().apiRequest("", null);
		getInstance().login("test@test.net", "test1234");
		getInstance().newAccount("bob@test.net", "bobhasbadpasswords");
		getInstance().login("bob@test.net", "bobhasbadpasswords");
		getInstance().login("bob@test.net", "bobhasGOODpasswords");

		getInstance().searchRecipes("cheese");
		getInstance().getRecipe(42);
	}

	public static Comm getInstance() {
		if (instance == null) {
			return new Comm();
		}
		return instance;
	}

	public int newAccount(String email, String password) {
		HashMap<String, String> req = new HashMap<>();
		req.put("email", email);
		req.put("password", password);
		return apiRequest("signup", req);
	}

	public int login(String email, String password) {
		HashMap<String, String> req = new HashMap<>();
		req.put("email", email);
		req.put("password", password);

		int ret = apiRequest("login", req);

		if (ret == 0) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(lastJSON);
				String token = mapper.readValue(rootNode.path("authtoken"), String.class);
				authToken = token;
			} catch (Exception e) {
				e.printStackTrace();
				return JSON_ERROR;
			}
			return SUCCESS;
		} else {
			return ret;
		}
	}

	public ArrayList<Recipe> searchRecipes(String search) {
		HashMap<String, String> req = new HashMap<>();
		req.put("keyword", search);
		apiRequest("search", req);

		// TODO: process whatever JSON we are handed back and
		//       spin up some Recipe objects, fill them in
		//       and return those
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(lastJSON);

			//Recipe[] recipes = mapper.readValue(rootNode.path("recipes"),
			//									  Recipe[]);
			// TODO: walk the list and add elems to ls
			ArrayList<Recipe> ls = new ArrayList<>();

			return ls;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Recipe getRecipe(int recipeID) {
		HashMap<String, String> req = new HashMap<>();
		req.put("rid", Integer.toString(recipeID));
		apiRequest("get", req);

		Recipe r = null;
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode;
		try {
			rootNode = mapper.readTree(lastJSON);
			r = mapper.readValue(rootNode.path("recipe"), Recipe.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// parse the request, pull any image URLs as a byte array, then:
		//  (where bitmapdata is a byte array)
		//Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata .length);

		return r;
	}

	public int uploadRecipe(int stub) {
		return NOT_IMPL;
	}

	private int apiRequest(String relUrl, Object o) {
		if (o == null) {
			return apiRequestPayload(relUrl, "");
		}
		ObjectWriter ow = new ObjectMapper().writer();
		try {
			return apiRequestPayload(relUrl, ow.writeValueAsString(o));
		} catch (Exception e) {
			e.printStackTrace();
			return JSON_ERROR;
		}
	}

	private int apiRequestPayload(String relUrl, String payload) {
		String line;
		StringBuffer jsonString = new StringBuffer();
		try {
			URL url = new URL("http://96.126.122.162:9222/chef/" + relUrl);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
			writer.write(payload);
			writer.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while ((line = br.readLine()) != null) {
				jsonString.append(line);
			}
			br.close();
			connection.disconnect();
			System.out.println(jsonString);
			lastJSON = jsonString.toString();
			return SUCCESS;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return CONN_FAILED;
		}
	}
}
