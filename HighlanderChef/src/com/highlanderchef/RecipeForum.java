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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeForum extends ActionBarActivity {

	Recipe currentRecipe = null;
	int recipeID = 0;
	private RatingBar ratingBar;
	//private TextView txtRatingValue;
	private Button btnComment;
	float userRating = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_forum);
		TextView failedToDisplayRecipe = (TextView) findViewById(R.id.errorCannotDisplayRecipe);
		failedToDisplayRecipe.setVisibility(View.INVISIBLE);

		Intent intent = getIntent();
		recipeID = intent.getIntExtra("recipeID", 0);
		downloadRecipe();


		addListenerOnRatingBar();
		addListenerOnButton();
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
		//ImageView imageViewMainImage;

		//Parse ingredients into neat format
		String formatOfIngredient = "";

		if(ingredientList.size() > 0) {
			for(int i = 0; i < ingredientList.size(); ++i) {
				formatOfIngredient += "* " + ingredientList.get(i).amount + " " + ingredientList.get(i).name;
				if((i + 1) < ingredientList.size()) {
					formatOfIngredient += "\n";
				}
			}
		}
		else {
			formatOfIngredient = "Ingredient List size = 0\n";

		}

		TextView textViewIngredients = (TextView) findViewById(R.id.selectedRecipeIngredientList);
		textViewIngredients.setText(formatOfIngredient);


		//Parse directions into neat format
		String formatOfDirection = "";
		if(directionList.size() > 0) {
			for(int i = 0; i < directionList.size(); ++i) {
				formatOfDirection += i + ". " + directionList.get(i).text;
				if((i + 1) < directionList.size()) {
					formatOfDirection += "\n";
				}
				//TODO: upload images. If user doesn't submit image, then do not generate an image.
				//if more than 1 image is found, then display photos scrollable from left to right.
				//images less than the size of the width of the screen should be displayed statically.
				//Otherwise, pictures should be scrolled from left to right to get the last image,
				//and right to left to get to the first image.
			}
		}
		else {
			formatOfDirection = "Direction List size = 0";
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
	//Event listener are Input events.
	//change the rating to display

	public void addListenerOnRatingBar() {

		ratingBar = (RatingBar) findViewById(R.id.recipeRatingBar);

		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {

				//txtRatingValue.setText(String.valueOf(rating));
				userRating = rating;


			}
		});
	}

	//display rating

	public void addListenerOnButton() {
		ratingBar = (RatingBar) findViewById(R.id.recipeRatingBar);
		btnComment = (Button) findViewById(R.id.submitComment);

		//if click on me, then display the current rating value
		btnComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Toast.makeText(RecipeForum.this,
						String.valueOf(ratingBar.getRating()),
						Toast.LENGTH_SHORT).show();
			}

		});
	}

	public void postCommentSuccess() {

	}

	public void postCommentFailure() {

	}

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

	private class postCommentTask extends AsyncTask<Comment, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Comment... params) {
			Comm c = new Comm();
			int ret = c.postComment(params[0]);
			return (ret != Comm.SUCCESS);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result == true) {
				Log.v("postComment", "postCommentSuccess");
				postCommentSuccess();
			} else {
				Log.v("postComment", "postComentFailure");
				postCommentFailure();
			}
		}
	}
}
