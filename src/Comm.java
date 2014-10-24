import java.util.ArrayList;

public class Comm {
	private static Comm instance;
	
	public int CONN_FAILED = -1;
	public int CONN_TIMEOUT = -2;
	
	protected Comm() {
		// Exists only to defeat instantiation.
	}
	
	public static Comm getInstance() {
		if (instance == null) {
			return new Comm();
		}
		return instance;
	}
	
	public int newAccount(String email, String password) {
		return -1;
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
	
	private int apiRequest() {
		
		
		return -1;
	}
}