package com.highlanderchef;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Comm {
	private static String serverRoot = "http://96.126.122.162:9222/chef/";
	private static String serverImgRoot = "http://96.126.122.162:9223/";
	private static boolean runningAndroid = true;
	private static final int commVersion = 1;

	private static ObjectMapper mapper;

	private int lastStatus;
	private String lastJSON;
	private JsonNode rootNode;

	// User account info
	private static volatile int id;
	private static volatile String email = "";
	private static volatile String authToken = "";
	//MILESTONE1
	private static ArrayList<Recipe> favorites;
	private static ArrayList<Integer> followers; //users that are following THIS user
	private static ArrayList<Integer> following; //users that THIS user are following
	private static Boolean update;


	public static final int SUCCESS = 0;
	public static final int JSON_ERROR = -3;
	public static final int API_FAIL = -50;
	public static final int NETWORK_FAIL = -60;
	public static final int AUTH_FAIL = -70;


	private void registerMapperSerializers() {
		SimpleModule module = new SimpleModule("DirectionModule", new Version(1,0,0,null));
		module.addSerializer(Direction.class, new DirectionSerializer());
		mapper.registerModule(module);
	}

	private void initMapper() {
		mapper = new ObjectMapper();
		registerMapperSerializers();
		mapper.getJsonFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
		mapper.getJsonFactory().configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
	}

	public Comm() {
		System.out.println("Creating new Comm");
		lastJSON = "";
		initMapper();
	}

	public int getUserID() {
		return id;
	}

	public static int staticGetUserID() {
		return id;
	}

	public static String getEmail() {
		return email;
	}

	public String getAuthToken() {
		return authToken;
	}

	public String getLastJSON() {
		return lastJSON;
	}
	//MILESTONE1
	public ArrayList<Recipe> getFavorites() {
		return favorites;
	}
	//MILESTONE1
	public ArrayList<Integer> getFollowers() {
		return followers;
	}
	public static void prettyPrint(String s) {
		try {
			Object json = mapper.readValue(s, Object.class);
			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
		} catch (Exception e) {
			System.out.println("EXCEPTION in prettyPrint");
		}
	}
	public static void prettyPrint(JsonNode s) {
		try {
			Object json = mapper.readValue(s, Object.class);
			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
		} catch (Exception e) {
			System.out.println("EXCEPTION in prettyPrint");
		}
	}
	//MILESTONE1
	public static void sendNotificationToFollowers() {
		if(update) {
			for(int i = 0; i < followers.size(); i++) {
				//send notification that you have uploaded a new recipe
				//set update to false
			}
		}
	}
	//MILESTONE1
	public static void checkNotification() {
		for(int i = 0; i < following.size(); i++) {
			//if(following[i].update == true) { //conflict here. list "following" should be object "users" and not just an integer of their id
			//update list and show the recipe they have just uploaded
			//}
		}
	}

	public static void main(String[] args) {
		runningAndroid = false;
		Comm c = new Comm();
		c.login("test@test.net", "test1234");

		c.getCategories();

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
		ArrayList<Integer> cats = simple.categories;
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
					return API_FAIL;
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
			prettyPrint(r);
			ls.add(parseRecipe(r, true));
		}
		return ls;
	}

	public ArrayList<Recipe> searchRecipesByCategory(int categoryID) {
		HashMap<String, Integer> req = new HashMap<>();
		req.put("categoryID", categoryID);
		apiRequest("searchcat", req);

		// 		 process whatever JSON we are handed back and
		//       spin up some Recipe objects, fill them in
		//       and return those
		ArrayList<Recipe> ls = new ArrayList<>();
		Iterator<JsonNode> ite = rootNode.path("recipes").getElements();
		while(ite.hasNext())
		{
			JsonNode r = ite.next();
			ls.add(parseRecipe(r, true));
		}
		return ls;
	}

	public ArrayList<Recipe> searchRecipesByUID(int userID) {
		HashMap<String, Integer> req = new HashMap<>();
		req.put("uid", userID);
		apiRequest("searchuid", req);

		ArrayList<Recipe> ls = new ArrayList<>();
		Iterator<JsonNode> ite = rootNode.path("recipes").getElements();
		while(ite.hasNext())
		{
			JsonNode r = ite.next();
			ls.add(parseRecipe(r, true));
		}
		return ls;
	}

	private Bitmap getImage(String relUrl) {
		System.out.println("getImage(" + serverImgRoot + relUrl);
		if (relUrl == null) {
			System.out.println("tried to get a null-url image");
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
			System.out.println("getImage sees content-length " + len);
			String type = connection.getContentType();
			System.out.println("getImage sees content-type " + type);
			Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
			if (bitmap == null) {
				System.out.println("BitmapFactory failed to decode PNG from stream");
				connection.disconnect();
				return null;
			} else {
				connection.disconnect();
				return bitmap;
			}
		} catch (Exception e) {
			System.out.println("Error in getImage");
			e.printStackTrace();
			System.out.println("getImage failed network");
			return null;
		}
	}

	// returns a new URL for the uploaded image, or "" on failure
	public String imageUpload(Bitmap bmp) {
		System.out.println("imageUpload");
		if (bmp == null) {
			System.out.println("tried to upload a null image -- bailing");
			return "";
		}
		try {
			HashMap<String, Object> o = new HashMap<>();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp = Bitmap.createScaledBitmap(bmp, 1024, 768, false);
			if (bmp.compress(Bitmap.CompressFormat.PNG, 90, stream)) {
				apiRequestBytePayload("imageupload", stream.toByteArray());
				if (lastStatus == 1) {
					String url = mapper.readValue(rootNode.path("image_url"), String.class);
					return url;
				} else {
					return "";
				}
			} else {
				return "";
			}
		} catch (Exception e) {
			System.out.println("Error in imageUpload");
			e.printStackTrace();
		}
		return "";
	}

	private void parseDirections(Recipe r, String json) {
		try {
			System.out.println("parseDirections got json \"" + json + "\"");
			JsonNode node = mapper.readTree(json);
			Iterator<JsonNode> ite = node.getElements();
			while (ite.hasNext()) {
				JsonNode dir = ite.next();
				String text = mapper.readValue(dir.path("text"), String.class);
				Iterator<JsonNode> ite2 = dir.path("img").getElements();
				ArrayList<Bitmap> bmps = new ArrayList<Bitmap>();
				while(ite2.hasNext()) {
					JsonNode img = ite2.next();
					String img_url = img.getTextValue();
					System.out.println("parseDirections found img_url " + img_url);
					Bitmap bmp = getImage(img_url);
					bmps.add(bmp);
				}

				r.addDirection(text, bmps);
			}
		} catch (Exception e) {
			System.out.println("Error in parseDirections - mapper.readTree or mapper.readValue");
			e.printStackTrace();
		}
	}

	private void parseComments(Recipe r, JsonNode node) {
		try {
			System.out.print("comments node: ");
			prettyPrint(node);
			Float rRating = mapper.readValue(node.path("rating"), Float.class);
			r.rating = rRating.floatValue();
			Iterator<JsonNode> ite = node.path("comments").getElements();
			while(ite.hasNext()) {
				JsonNode cnode = ite.next();
				System.out.print("cnode: ");
				prettyPrint(cnode);
				Integer rating = mapper.readValue(cnode.path("rating"), Integer.class);
				String comment = mapper.readValue(cnode.path("comment"), String.class);
				String username = mapper.readValue(cnode.path("username"), String.class);
				r.comments.add(new Comment(id, rating.intValue(), comment, username));
			}
		} catch (Exception e) {
			System.out.println("parseRecipe had an exception parsing comments");
			e.printStackTrace();
		}
	}

	private void parseQuestions(Recipe r, JsonNode node) {
		try {
			System.out.print("question node: ");
			prettyPrint(node);

			Iterator<JsonNode> ite = node.path("questions").getElements();
			while(ite.hasNext()) {
				JsonNode qnode = ite.next();
				System.out.print("qnode: ");
				prettyPrint(qnode);

				Integer quid = mapper.readValue(qnode.path("uid"), Integer.class);
				String qusername = mapper.readValue(qnode.path("username"), String.class);
				String qtext = mapper.readValue(qnode.path("question"), String.class);

				ArrayList<Question> replies = new ArrayList<Question>();
				Iterator<JsonNode> iter = node.path("replies").getElements();
				while (iter.hasNext()) {
					JsonNode rnode = iter.next();
					System.out.print("rnode: ");
					prettyPrint(rnode);
					Integer uid = mapper.readValue(rnode.path("uid"), Integer.class);
					String username = mapper.readValue(rnode.path("username"), String.class);
					String text = mapper.readValue(rnode.path("reply"), String.class);

					replies.add(new Question(uid, username, text));
				}

				if (r.questions == null) {
					r.questions = new ArrayList<Question>();
				}
				r.questions.add(new Question(quid, qusername, qtext, replies));
			}
		} catch (Exception e) {
			System.out.println("parseQuestions had an exception parsing questions");
			e.printStackTrace();
		}
	}

	private Recipe parseRecipe(JsonNode node) {
		return parseRecipe(node, false);
	}

	private Recipe parseRecipe(JsonNode node, boolean brief) {
		Recipe r = null;

		try {
			Integer id = mapper.readValue(node.path("rid"),Integer.class);
			String description = mapper.readValue(node.path("description"), String.class);
			String image_url = mapper.readValue(node.path("img_url"), String.class);
			if (image_url.length() == 0) {
				image_url = "default.png";
			}
			String name = mapper.readValue(node.path("name"), String.class);
			System.out.println("parseRecipe for image_url " + image_url);
			r = new Recipe(id, name, description, getImage(image_url));
			String cooktime = mapper.readValue(node.path("cooktime"), String.class);
			r.setCookTime(cooktime);

			if (!brief) {
				String ingredientsJson = mapper.readValue(node.path("ingredients"), String.class);
				r.parseIngredientsFromJson(ingredientsJson);
				String directionsJson = mapper.readValue(node.path("directions"), String.class);
				parseDirections(r, directionsJson);
				String categoriesJson = mapper.readValue(node.path("categories"), String.class);
				r.parseCategoriesFromJson(categoriesJson);
				JsonNode commentsNode = node.path("comments");
				parseComments(r, commentsNode);
				JsonNode questionsNode = node.path("questions");
				parseQuestions(r, questionsNode);
			}

			return r;
		} catch (Exception e) {
			System.out.println("Error in parseRecipe - mapper.readValue");
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



	public int uploadRecipe(Recipe r) {
		HashMap<String, Object> req = new HashMap<>();
		req.put("uid", Integer.toString(id));
		HashMap<String, Object> recipe = new HashMap<>();
		recipe.put("rid", r.id);
		recipe.put("name", r.name);
		recipe.put("description", r.description);
		recipe.put("cooktime", r.cookTime);
		System.out.println("uploadRecipe uploading main image");
		recipe.put("image_url", imageUpload(r.mainImage));
		recipe.put("categories", r.categories);
		recipe.put("ingredients", r.ingredients);

		recipe.put("directions", r.directions);
		try {
			recipe.put("parseddirs", mapper.writeValueAsString(r.directions));
		} catch (Exception e) {
			System.out.println("parseddirs serialization failed");
			e.printStackTrace();
		}

		req.put("recipe", recipe);

		apiRequest("uploadrecipe", req);

		if (lastStatus == 1) {
			return SUCCESS;
		} else {
			return API_FAIL;
		}
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
				return API_FAIL;
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
				return API_FAIL;
			}
		} else {
			return ret;
		}
	}

	public int postComment(Comment c) {
		HashMap<String, String> req = new HashMap<>();
		req.put("uid", Integer.toString(id));
		req.put("rid", Integer.toString(c.recipeID));
		req.put("rating", Integer.toString(c.rating));
		req.put("comment", c.comment);
		int ret = apiRequest("rate", req);
		if (ret == 0) {
			if (lastStatus == 1) {
				return SUCCESS;
			} else {
				return API_FAIL;
			}
		} else {
			return ret;
		}
	}

	public int saveDraft(Recipe r) {
		HashMap<String, Object> req = new HashMap();
		req.put("uid", Integer.toString(id));
		HashMap<String, Object> recipe = new HashMap<>();
		recipe.put("rid", Integer.toString(r.id));
		recipe.put("name", r.name);
		recipe.put("description", r.description);
		recipe.put("cooktime", r.cookTime);
		System.out.println("savingDraft uploading main image");
		recipe.put("image_url", imageUpload(r.mainImage));

		recipe.put("categories", r.categories);
		recipe.put("ingredients", r.ingredients);

		recipe.put("directions", r.directions);
		try {
			recipe.put("parseddirs", mapper.writeValueAsString(r.directions));
		} catch (Exception e) {
			System.out.println("parseddirs serialization failed");
			e.printStackTrace();
		}

		req.put("recipe", recipe);
		int ret = apiRequest("savedraft", req);
		if(ret == 0) {
			if(lastStatus == 1) {
				return SUCCESS;
			} else {
				return API_FAIL;
			}
		} else {
			return ret;
		}
	}

	public Recipe getDraft(int draftID) {
		HashMap<String, String> req = new HashMap<>();
		req.put("did", Integer.toString(draftID));
		apiRequest("getdraft", req);

		return parseRecipe(rootNode.path("recipe"));
	}


	/*
	 * getCategories returns a pre-sorted list of categories.
	 *   ie Each top level category is followed by its children,
	 *      and they are followed by their children in hierarchical order.
	 */
	public ArrayList<Category> getCategories() {
		ArrayList<Category> cats = new ArrayList<>();
		int ret = apiRequest("categories", null);
		if (ret == 0) {
			prettyPrint(rootNode);
			if (lastStatus == 1) {
				Iterator<JsonNode> ite = rootNode.path("categories").getElements();
				while(ite.hasNext())
				{
					JsonNode r = ite.next();
					try {
						Integer id = mapper.readValue(r.path("id"),Integer.class);
						String name = mapper.readValue(r.path("name"), String.class);
						Integer level = mapper.readValue(r.path("level"), Integer.class);
						cats.add(new Category(id.intValue(), level.intValue(), name));
					} catch (Exception e) {
						System.out.println("failed in getCategories - mapper.readValue");
						e.printStackTrace();
						return null;
					}
				}
				return cats;
			} else {
				return null;
			}
		}
		return null;
	}

	private int apiRequest(String relUrl, Object o) {
		if (o == null) {
			return apiRequestPayload(relUrl, "");
		}
		try {
			return apiRequestPayload(relUrl, mapper.writeValueAsString(o));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("died writing value string " + o);
			return JSON_ERROR;
		}
	}

	private int apiRequestBytePayload(String relUrl, byte[] payload) {
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
			connection.setRequestProperty("uid", Integer.toString(Comm.id));
			connection.setRequestProperty("token", Comm.authToken);
			OutputStream os = connection.getOutputStream();
			os.write(payload);
			os.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while ((line = br.readLine()) != null) {
				jsonString.append(line);
			}
			br.close();
			connection.disconnect();
			System.out.println(jsonString);
			lastJSON = jsonString.toString();
			rootNode = mapper.readTree(lastJSON);
			lastStatus = API_FAIL;
			try {
				Integer status = mapper.readValue(rootNode.path("status"), Integer.class);
				lastStatus = status;
				return SUCCESS;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("failed in apiRequest : fail to readValue from \"status\" ");
				return API_FAIL;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("failed in apiRequest: failed URL");
			return NETWORK_FAIL;
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
			connection.setRequestProperty("uid", Integer.toString(Comm.id));
			connection.setRequestProperty("token", Comm.authToken);
			connection.setRequestProperty("commversion", Integer.toString(Comm.commVersion));
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
			rootNode = mapper.readTree(lastJSON);
			lastStatus = API_FAIL;
			try {
				Integer status = mapper.readValue(rootNode.path("status"), Integer.class);
				lastStatus = status;
				if (lastStatus == -1) {
					return AUTH_FAIL;
				} else {
					return SUCCESS;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("failed in apiRequest : fail to readValue from \"status\" ");
				return API_FAIL;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("failed in apiRequest: failed URL");
			return NETWORK_FAIL;
		}
	}
}
