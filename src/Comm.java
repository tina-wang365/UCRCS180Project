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
		getInstance().newAccount("test@test.net", "test1234");
	}

	public static Comm getInstance() {
		if (instance == null) {
			return new Comm();
		}
		return instance;
	}
	
	public int newAccount(String email, String password) {
		HashMap<String, String> req = new HashMap<>();
		req.put("email",  email);
		req.put("password", password);
		return apiRequest("login", req);
	}
	
	public int login(String email, String password) {
		return -1;
	}
	
	public ArrayList<Recipe> searchRecipes(String search) {
		return null;
	}
	
	public Recipe getRecipe(int recipeID) {
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
			URL url = new URL("http://127.0.0.1:9222/chef/" + relUrl);

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