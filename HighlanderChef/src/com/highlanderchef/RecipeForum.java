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
import android.widget.EditText;
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
	LinearLayout ll;

	public void displayRecipeSuccess(Recipe recipe) {
		ll = (LinearLayout) findViewById(R.id.rflayout);
		final LinearLayout.LayoutParams params =
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);

		//Set objects for display on activity
		TextView textViewTitle = new TextView(this);
		textViewTitle.setText(recipe.name + "\n");
		textViewTitle.setLayoutParams(params);
		ll.addView(textViewTitle);

		//Set objects for display on activity
		TextView textViewDes = new TextView(this);
		textViewDes.setText(recipe.description);
		textViewDes.setLayoutParams(params);
		ll.addView(textViewDes);

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

				for(int j = 0; j < recipe.directions.get(i).images.size(); ++j)
				{
					//TODO set images horizontal    .setLayoutDirection()

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

		EditText et_comment = new EditText(this);
		et_comment.setHint("Add a comment");
		et_comment.setId(1111);
		//et_params.addRule(RelativeLayout.BELOW, ll.getChildAt(ll.getChildCount()-1).getId());
		et_comment.setLayoutParams(params);
		ll.addView(et_comment);

		Button b_comment = new Button(this);
		b_comment.setText("Comment");
		final int id = recipe.id;

		b_comment.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v)
			{
				EditText et2 = (EditText) findViewById(1111);
				final String comment_text = et2.getText().toString();
				Comment new_comment = new Comment(id, 3, comment_text);
				addComment(new_comment);
				et2.getText().clear();
			}
		});
		ll.addView(b_comment);

		//comments
		for(int i = 0; i < recipe.comments.size(); ++i)
		{
			TextView tv_comment = new TextView(this);
			tv_comment.setText(recipe.comments.get(i).username + "\t\t" +
					recipe.comments.get(i).rating + "\n" +
					recipe.comments.get(i).comment + "\n\n\n");
			tv_comment.setLayoutParams(params);
			ll.addView(tv_comment);
		}
	}

	public void addComment(Comment comment)
	{
		TextView tv_comment = new TextView(this);
		tv_comment.setText(comment.username + "\t\t" +
				comment.rating + "\n" +
				comment.comment + "\n\n\n");
		final LinearLayout.LayoutParams params =
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
		tv_comment.setLayoutParams(params);
		LinearLayout ll = (LinearLayout) findViewById(R.id.rflayout);
		ll.addView(tv_comment);
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

