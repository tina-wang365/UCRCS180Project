import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.*;
import org.codehaus.jackson.map.*;

public class Comm {
	private static Comm instance;

	public int CONN_FAILED = -1;
	public int CONN_TIMEOUT = -2;

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
		req.put("email",  email);
		req.put("password", password);
		return apiRequest("signup", req);
	}
	
	public int login(String email, String password) {
		HashMap<String, String> req = new HashMap<>();
		req.put("email",  email);
		req.put("password", password);
		return apiRequest("login", req);
	}
	
	public ArrayList<Recipe> searchRecipes(String search) {
		HashMap<String, String> req = new HashMap<>();
		req.put("keyword", search);
		apiRequest("search", req);

		// TODO: process whatever JSON we are handed back and
		//       spin up some Recipe objects, fill them in
		//       and return those
		return null;
	}
	
	public Recipe getRecipe(int recipeID) {
		HashMap<String, String> req = new HashMap<>();
		req.put("rid", Integer.toString(recipeID));
		apiRequest("get", req);
		return null;
	}
	
	public int UploadRecipe(int stub) {
		return -1;
	}
	
	private int apiRequest(String relUrl, Object o) {
		if (o == null) {
			return apiRequestPayload(relUrl, "");
		}
		ObjectWriter ow = new ObjectMapper().writer();
		try {
			return apiRequestPayload(relUrl, ow.writeValueAsString(o));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
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
			return -1;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return -1;
		}
	}
}
