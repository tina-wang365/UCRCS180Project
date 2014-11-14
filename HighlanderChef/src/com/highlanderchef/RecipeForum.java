package com.highlanderchef;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecipeForum extends ActionBarActivity {

	Recipe currentRecipe = null;
	Comment currentComment = null;
	int recipeID = 0;
	private Button btnComment;
	float userRating = 0;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_forum);
		//TextView failedToDisplayRecipe = (TextView) findViewById(R.id.errorCannotDisplayRecipe);
		//failedToDisplayRecipe.setVisibility(View.INVISIBLE);

		Intent intent = getIntent();
		recipeID = intent.getIntExtra("recipeID", 0);
		downloadRecipe();
		/*
		LinearLayout rl = (LinearLayout) findViewById(R.id.linearLayoutResults);
		TextView tv_cooktime = new TextView(this);
		   tv_cooktime.setText(recipies.get(i).getCookTime());
		   rl.addView(tv_cooktime);
		 */
		currentComment = new Comment(recipeID, 0, "HelloWorld");


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

	public void addCommentPressed(View v) {
		/*EditText userCommentText = (EditText) findViewById(R.id.userCommentText);
		RatingBar ratingBar = (RatingBar) findViewById(R.id.recipeRatingBar);
		currentComment.rating = (int) ratingBar.getRating();

		Toast.makeText(RecipeForum.this,
				String.valueOf(ratingBar.getRating()),
				Toast.LENGTH_SHORT).show();
		currentComment.comment = userCommentText.getText().toString();
		new postCommentTask().execute(currentComment); */

	}
	public void downloadRecipe() {
		new getRecipeTask().execute(recipeID);
	}
	public void displayRecipeSuccess(Recipe recipe) {
		LinearLayout ll = (LinearLayout) findViewById(R.id.rflayout);
		final LinearLayout.LayoutParams params =
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);

		//Set objects for display on activity
		TextView textViewTitle = new TextView(this);
		textViewTitle.setText(recipe.name);
		textViewTitle.setLayoutParams(params);
		ll.addView(textViewTitle);

		if(recipe.mainImage != null)
		{
			//set main image
			ImageView ivmain = new ImageView(this);
			ivmain.setImageBitmap(currentRecipe.mainImage);
			ivmain.setLayoutParams(params);
			ll.addView(ivmain);
		}

		//create string for ingredient text
		String formatOfIngredient = "";
		if(recipe.ingredients.size() > 0) {
			for(int i = 0; i < recipe.ingredients.size(); ++i) {
				formatOfIngredient += "* " + recipe.ingredients.get(i).amount + " " + recipe.ingredients.get(i).name;
				if((i + 1) < recipe.ingredients.size()) {
					formatOfIngredient += "\n";
				}
			}
			formatOfIngredient += "\n";
		}
		else {
			formatOfIngredient = "Ingredient List size = 0\n";
		}

		TextView textViewIngredients = new TextView(this);
		textViewIngredients.setText(formatOfIngredient);
		textViewIngredients.setLayoutParams(params);
		ll.addView(textViewIngredients);

		//Parse directions into neat format
		String formatOfDirection = "";
		if(recipe.directions.size() > 0)
		{
			for(int i = 0; i < recipe.directions.size(); ++i)
			{
				formatOfDirection += (i + 1) + ". " + recipe.directions.get(i).text;


				TextView textViewDirections = new TextView(this);
				textViewDirections.setText(formatOfDirection);
				textViewIngredients.setLayoutParams(params);
				ll.addView(textViewDirections);
				formatOfDirection = "";

				/*LinearLayout ll_d_images = new LinearLayout(this);
				final LinearLayout.LayoutParams d_image_params =
						new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.WRAP_CONTENT
								);*/

				for(int j = 0; j < recipe.directions.get(i).images.size(); ++j)
				{
					ImageView iv_dir_image = new ImageView(this);
					iv_dir_image.setImageBitmap(recipe.directions.get(i).images.get(j));
					iv_dir_image.setLayoutParams(params);
					ll.addView(iv_dir_image);
				}
			}
		}
		else
		{
			formatOfDirection = "Direction List size = 0";
		}



		//TextView failedToDisplayRecipe = (TextView) findViewById(R.id.errorCannotDisplayRecipe);
		//failedToDisplayRecipe.setVisibility(View.INVISIBLE);



		/*
		 * mdb: this is how to do a dynamic add... may need to also add a
		 *      paam for ABOVE
		RelativeLayout rr = (RelativeLayout) findViewById(R.id.rflayout);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, R.id.titleOfRecipe);
		rr.addView(ivmain, params);
		 */
	}

	public void displayRecipeFailure(String text) {
		//TextView failedToDisplayRecipe = (TextView) findViewById(R.id.errorCannotDisplayRecipe);
		//failedToDisplayRecipe.setVisibility(View.VISIBLE);
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

