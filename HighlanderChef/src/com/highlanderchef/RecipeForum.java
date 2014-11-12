package com.highlanderchef;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class RecipeForum extends ActionBarActivity {

	Recipe currentRecipe = null;
	int recipeID = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_forum);
		TextView failedToDisplayRecipe = (TextView) findViewById(R.id.errorCannotDisplayRecipe);
		failedToDisplayRecipe.setVisibility(View.INVISIBLE);

		Intent intent = getIntent();
		recipeID = intent.getIntExtra("recipeID", 0);
		downloadRecipe();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recipe_forum, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	//TODO: Create a function that receives the recipeID from another activity
	//TODO: Assume that you have the recipeID (pass in 1). create a task to grab
	//		the recipe object and display all of its contents.


	public void downloadRecipe() {
		new getRecipeTask().execute(recipeID);
	}
	public void displayRecipeSuccess(Recipe recipe) {
		//Load information about this particular recipe
		String title = recipe.name;
		ArrayList<Ingredient> ingredientList = recipe.ingredients;
		ArrayList<Direction> directionList = recipe.directions;
		Bitmap mainPhoto = recipe.mainImage;

		//Set objects for display on activity
		TextView textViewTitle = (TextView) findViewById(R.id.titleOfRecipe);
		textViewTitle.setText(title);

		//Display main Image
		ImageView imageViewMainImage;

		//Parse ingredients into neat format
		//NOTE: This only handles one kind of input for proper formatting. This assumes that
		//the string "* 1/2 ingredientName" does not exceed the width of the mobile screen.
		String formatOfIngredient = "";
		for(int i = 0; i < ingredientList.size(); ++i) {
			formatOfIngredient = "* " + ingredientList.get(i).amount + " " + ingredientList.get(i).name;
			if(i + 1 < ingredientList.size()) {
				formatOfIngredient += "\n";
			}
		}
		TextView textViewDirectionList = (TextView) findViewById(R.id.selectedRecipeIngredientList);
		textViewDirectionList.setText(formatOfIngredient);

		//Parse directions into neat format
		//NOTE: This only handles one kind of input for proper formatting. This assumes that
		//that the length of directions is not larger than the width of the screen.\
		String formatOfDirection = "";
		for(int i = 0; i < ingredientList.size(); ++i) {
			formatOfDirection = i + ". " + directionList.get(i).text;
			if(i + 1 < ingredientList.size()) {
				formatOfDirection += "\n";
			}
			//TODO: upload images. If user doesn't submit image, then do not generate an image.
			//if more than 1 image is found, then display photos scrollable from left to right.
			//images less than the size of the width of the screen should be displayed statically.
			//Otherwise, pictures should be scrolled from left to right to get the last image,
			//and right to left to get to the first image.
		}
		TextView textViewDirections = (TextView) findViewById(R.id.selectedRecipeDirectionList);
		textViewDirections.setText(formatOfDirection);


		TextView failedToDisplayRecipe = (TextView) findViewById(R.id.errorCannotDisplayRecipe);
		failedToDisplayRecipe.setVisibility(View.INVISIBLE);

	}

	public void displayRecipeFailure(String text) {
		TextView failedToDisplayRecipe = (TextView) findViewById(R.id.errorCannotDisplayRecipe);
		failedToDisplayRecipe.setVisibility(View.VISIBLE);
	}

	public void addCommentPressed(View view)
	{
		EditText editTextUserComment = (EditText) findViewById(R.id.userCommentText);
		String strUserComment = editTextUserComment.getText().toString();
	}
	//Set the rating of the 5 stars once the user taps on the rating bar.
	public void addRatingPressed(View view)
	{
		RatingBar bar = (RatingBar) view;
		float rating = bar.getRating();
		bar.setRating(rating);
	}

	//TODO: Create getRecipeTask
	private class getRecipeTask extends AsyncTask<Integer, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Integer... params) {
			Comm c = new Comm();
			Recipe ret = c.getRecipe(params[0]);
			currentRecipe = ret;
			return (ret != null);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result == true) {
				Log.v("getRecipeSuccess","Success: Recipe Received");
				displayRecipeSuccess(currentRecipe);
			} else {
				Log.v("getRecipeFailure","Failure: Did not receive recipe");
				displayRecipeFailure("Could not display the recipe for some reason");
			}
		}

	}
}
