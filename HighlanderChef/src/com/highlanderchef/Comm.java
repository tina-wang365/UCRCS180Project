package com.highlanderchef;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Comm {
	private static String serverRoot = "http://96.126.122.162:9222/chef/";
	private static String serverImgRoot = "http://96.126.122.162:9223/";
	private static boolean runningAndroid = true;

	private static ObjectMapper mapper;

	private int lastStatus;
	private String lastJSON;
	private JsonNode rootNode;

	// User account info
	private int id;
	private String email;
	private String authToken;

	public static final int SUCCESS = 0;
	public static final int CONN_FAILED = -1;
	public static final int CONN_TIMEOUT = -2;
	public static final int JSON_ERROR = -3;
	public static final int GENL_FAIL = -4;
	public static final int NOT_IMPL = -42;

	public Comm() {
		lastJSON = "";
		email = "";
		authToken = "";

		mapper = new ObjectMapper();
	}

	public Comm(String email, String authToken) {
		this.authToken = authToken;
		this.email = email;
		lastJSON = "";

		mapper = new ObjectMapper();
	}

	public String getEmail() {
		return email;
	}

	public String getAuthToken() {
		return authToken;
	}

	public String getLastJSON() {
		return lastJSON;
	}
	public static void prettyPrint(String s) throws JsonGenerationException, JsonMappingException, IOException {
		Object json = mapper.readValue(s, Object.class);
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
	}
	public static void prettyPrint(JsonNode s) throws JsonGenerationException, JsonMappingException, IOException {
		Object json = mapper.readValue(s, Object.class);
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
	}

	public static void main(String[] args) {
		runningAndroid = false;
		Comm c = new Comm();
		c.login("test@test.net", "test1234");
		c.newAccount("bob@test.net", "bobhasbadpasswords");
		System.out.println("c.login returns " + c.login("bob@test.net", "bobhasbadpasswords"));
		System.out.println("c.login returns " + c.login("bob@test.net", "bobhasGOODpasswords"));

		c.searchRecipes("soup");
		Recipe simple = c.getRecipe(1);
		ArrayList<Ingredient> list = simple.ingredients;
		for (int i = 0; i < list.size(); i++) {
			System.out.println(i + " " + list.get(i).amount + " " + list.get(i).name);
		}
		ArrayList<Direction> dirs = simple.directions;
		for (int i = 0; i < dirs.size(); i++) {
			System.out.println(i + " " + dirs.get(i).text);
		}
		System.out.println("now display the categories");
		ArrayList<String> cats = simple.categories;
		for(int i = 0; i < cats.size(); i++) {
			System.out.println(i + " " + cats.get(i));
		}
		if(simple.categories.size() == 0)
			System.out.println("there are no categories");

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
			try {
				if (lastStatus == 1) {
					String token = mapper.readValue(rootNode.path("token"), String.class);
					authToken = token;
					this.email = email;
					Integer userId = mapper.readValue(rootNode.path("id"), Integer.class);
					this.id = userId.intValue();
					return SUCCESS;
				} else {
					return GENL_FAIL;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return JSON_ERROR;
			}
		} else {
			return ret;
		}
	}

	public ArrayList<Recipe> searchRecipes(String search) {
		HashMap<String, String> req = new HashMap<>();
		req.put("keyword", search);
		apiRequest("search", req);

		// 		 process whatever JSON we are handed back and
		//       spin up some Recipe objects, fill them in
		//       and return those
		ArrayList<Recipe> ls = new ArrayList<>();
		Iterator<JsonNode> ite = rootNode.path("recipes").getElements();
		while(ite.hasNext())
		{
			JsonNode r = ite.next();
			try {
				prettyPrint(r);
			} catch (JsonGenerationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ls.add(parseRecipe(r));
		}
		return ls;
	}

	public ArrayList<Recipe> searchRecipesByCategory(String search) {
		HashMap<String, String> req = new HashMap<>();
		req.put("categoryID", search);
		apiRequest("searchCat", req);

		// 		 process whatever JSON we are handed back and
		//       spin up some Recipe objects, fill them in
		//       and return those
		ArrayList<Recipe> ls = new ArrayList<>();
		Iterator<JsonNode> ite = rootNode.path("recipes").getElements();
		while(ite.hasNext())
		{
			JsonNode r = ite.next();
			ls.add(parseRecipe(r));
		}
		return ls;
	}

	private Bitmap getImage(String relUrl) {
		System.out.println("getImage(" + serverImgRoot + relUrl);
		if (relUrl == null) {
			return null;
		}

		try {
			URL url = new URL(serverImgRoot + relUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setRequestMethod("GET");

			System.out.println("getImage sees code " + connection.getResponseCode());
			if (connection.getResponseCode() != 200) {
				connection.disconnect();
				return null;
			}

			int len = connection.getContentLength();
			System.out.println("getImage sees content-length " + connection.getContentLength());

			if (!runningAndroid) {
				return null;
			}
			byte[] imgData = new byte[len];
			BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
			bis.read(imgData);
			Bitmap bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
			connection.disconnect();
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void parseDirections(Recipe r, String json) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			JsonNode node = mapper.readTree(json);
			Iterator<JsonNode> ite = node.getElements();
			while (ite.hasNext()) {
				JsonNode dir = ite.next();
				String text = mapper.readValue(dir.path("text"), String.class);
				Iterator<JsonNode> ite2 = node.getElements();
				ArrayList<Bitmap> bmps = new ArrayList<Bitmap>();
				while(ite2.hasNext()) {
					JsonNode img = ite2.next();
					String img_url = img.getTextValue();
					Bitmap bmp = getImage(img_url);
					bmps.add(bmp);
				}

				r.addDirection(text, bmps);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Recipe parseRecipe(JsonNode node) {
		ObjectMapper mapper = new ObjectMapper();
		Recipe r = null;

		try {
			Integer id = mapper.readValue(node.path("rid"),Integer.class);
			String description = mapper.readValue(node.path("description"), String.class);
			String image_url = mapper.readValue(node.path("img_url"), String.class);
			String name = mapper.readValue(node.path("name"), String.class);
			r = new Recipe(id, name, description, getImage(image_url));

			String ingredientsJson = mapper.readValue(node.path("ingredients"), String.class);
			r.parseIngredientsFromJson(ingredientsJson);
			String directionsJson = mapper.readValue(node.path("directions"), String.class);
			parseDirections(r, directionsJson);
			String categoriesJson = mapper.readValue(node.path("categories"), String.class);
			r.parseCategoriesFromJson(categoriesJson);

			return r;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Recipe getRecipe(int recipeID) {
		HashMap<String, String> req = new HashMap<>();
		req.put("rid", Integer.toString(recipeID));
		apiRequest("get", req);

		return parseRecipe(rootNode.path("recipe"));
	}

	public int uploadRecipe(int stub) {
		return NOT_IMPL;
	}

	public int postQuestion(int recipeId, String question) {
		HashMap<String, String> req = new HashMap<>();
		req.put("uid", Integer.toString(id));
		req.put("rid", Integer.toString(recipeId));
		req.put("question", question);
		int ret = apiRequest("postquestion", req);
		if (ret == 0) {
			if (lastStatus == 1) {
				return SUCCESS;
			} else {
				return GENL_FAIL;
			}
		} else {
			return ret;
		}
	}

	public int postReply(int questionId, String reply) {
		HashMap<String, String> req = new HashMap<>();
		req.put("uid", Integer.toString(id));
		req.put("qid", Integer.toString(questionId));
		req.put("reply", reply);
		int ret = apiRequest("postquestion", req);
		if (ret == 0) {
			if (lastStatus == 1) {
				return SUCCESS;
			} else {
				return GENL_FAIL;
			}
		} else{
			return ret;
		}
	}

	public CategoryNode getCategories() {
		CategoryNode root = new CategoryNode(-1, "");
		int ret = apiRequest("getcategories", null);
		if (ret == 0) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(lastJSON);
				Integer status = mapper.readValue(rootNode.path("status"), Integer.class);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		return root;
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
			URL url = new URL(serverRoot + relUrl);

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
			ObjectMapper mapper = new ObjectMapper();
			rootNode = mapper.readTree(lastJSON);
			lastStatus = GENL_FAIL;
			try {
				Integer status = mapper.readValue(rootNode.path("status"), Integer.class);
				lastStatus = status;
				return SUCCESS;
			} catch (Exception e) {
				e.printStackTrace();
				return GENL_FAIL;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return CONN_FAILED;
		}
	}
}
