package com.highlanderchef;

import java.io.Serializable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeForum extends ActionBarActivity implements Serializable{
	ID_Maker MakerInstance = new ID_Maker();
	private final int LENGTH_SHORT = 2000;
	private final int LENGTH_LONG = 7000;
	Recipe currentRecipe = null;
	LinearLayout ll;
	RelativeLayout questionsLayout = null;
	//	Comment currentComment = null;
	int recipeID = 0;
	private Button btnComment;
	private RatingBar ratingBar;
	boolean ratingBarPressed = false;
	User ownerOfRecipe = new User();
	User currentlyLoggedIn = new User();
	public static int ImageMatch = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		questionsLayout = new RelativeLayout(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_forum);
		//TextView failedToDisplayRecipe = (TextView) findViewById(R.id.errorCannotDisplayRecipe);
		//failedToDisplayRecipe.setVisibility(View.INVISIBLE);

		Intent intent = getIntent();
		recipeID = intent.getIntExtra("recipeID", 0);
		downloadRecipe();
	}

	@Override
	public void onWindowFocusChanged (boolean hasFocus)
	{
		if(hasFocus)
		{
			if(ImageMatch == 1)
			{
				//TODO ask Tina if messages are good
				Toast followToast = Toast.makeText(getApplicationContext(), "Comparison Didn't Match ed", LENGTH_LONG);
				followToast.setGravity(Gravity.TOP, 0, this.getResources().getDisplayMetrics().widthPixels); //gravity, x-offset, y-offset
				followToast.show();
				ImageMatch = 0;
			}
			else if(ImageMatch == 2)
			{
				Toast followToast = Toast.makeText(getApplicationContext(), "Comparison Matched: Goo d to move on", LENGTH_LONG);
				followToast.setGravity(Gravity.TOP, 0, this.getResources().getDisplayMetrics().widthPixels); //gravity, x-offset, y-offset
				followToast.show();
				ImageMatch = 0;
			}
		}
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



	@SuppressWarnings("null")
	public void displayRecipeSuccess(Recipe recipe) {
		ll = (LinearLayout) findViewById(R.id.linearLayoutResults);
		if (ll == null) {
			System.out.println("ll is null in RA");
		}
		final LinearLayout.LayoutParams params =
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);

		//Set objects for display on activity
		TextView textViewTitle = new TextView(this);
		textViewTitle.setText("Title: " + recipe.name + "\n");
		textViewTitle.setLayoutParams(params);
		ll.addView(textViewTitle);

		//Set objects for display on activity
		TextView textViewDes = new TextView(this);
		textViewDes.setText("Description: " + recipe.description + "\n");
		textViewDes.setLayoutParams(params);
		ll.addView(textViewDes);

		//Set objects for display on activity
		TextView textViewCookTime = new TextView(this);
		if(recipe.cookTime == null) {
			String estimate = "Estimate: N/A";
			textViewCookTime.setText(estimate);
			System.out.println("Cook Time N/A");
		}
		else {
			System.out.println("cookTime is not empty!");
			textViewCookTime.setText("Cook Time: " + recipe.cookTime);
		}

		textViewCookTime.setLayoutParams(params);
		ll.addView(textViewCookTime);

		if(recipe.mainImage != null)
		{
			//set main image
			ImageView ivmain = new ImageView(this);
			ivmain.setImageBitmap(recipe.mainImage);
			ivmain.setLayoutParams(params);
			ll.addView(ivmain);
		}
		else {

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
				LinearLayout iLinearLayout = new LinearLayout(this);
				iLinearLayout.setOrientation(LinearLayout.HORIZONTAL); //resume here
				for(int j = 0; j < recipe.directions.get(i).images.size(); ++j)
				{
					//TODO set images horizontal    .setLayoutDirection()

					ImageView iv_dir_image = new ImageView(this);
					iv_dir_image.setImageBitmap(recipe.directions.get(i).images.get(j));
					iv_dir_image.setLayoutParams(params);
					ll.addView(iv_dir_image);

					//button to do image comparison
					Button image_comp = new Button(this);
					image_comp.setText("Compare");
					final LinearLayout.LayoutParams params_comp =
							new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
									LinearLayout.LayoutParams.WRAP_CONTENT);
					image_comp.setLayoutParams(params_comp);
					final Bitmap image = recipe.directions.get(i).images.get(j);
					image_comp.setOnClickListener(new View.OnClickListener(){

						@Override
						public void onClick(View v)
						{
							callImageCompIntent(image);
						}
					});
					ll.addView(image_comp);
				}
			}
		}
		else
		{
			formatOfDirection = "Direction List size = 0";
		}


		Button viewForum = new Button(this);
		viewForum.setId(MakerInstance.useCurrID());
		viewForum.setText("View Forum");

		final LinearLayout.LayoutParams paramsPostQuestion =
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
		viewForum.setLayoutParams(paramsPostQuestion);
		viewForum.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v)
			{
				callQuestionBoardIntent(currentRecipe);

			}
		});
		ll.addView(viewForum);





		//comments edit text field
		EditText et_comment = new EditText(this);
		et_comment.setHint("Add a comment");
		et_comment.setId(1111);
		//et_params.addRule(RelativeLayout.BELOW, ll.getChildAt(ll.getChildCount()-1).getId());
		et_comment.setLayoutParams(params);
		ll.addView(et_comment);

		//comments rating bar
		final LinearLayout.LayoutParams params_rb =
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
		final int id = recipe.id;
		ratingBar = new RatingBar(this);
		ratingBar.setStepSize((float) 0.5);
		ratingBar.setMax(5);
		ratingBar.setId(1);
		ratingBar.setRating(2.5f);
		ratingBar.setNumStars(5);
		ll.addView(ratingBar, params_rb);

		//submit comment button
		Button b_comment = new Button(this);
		b_comment.setText("Comment");
		final LinearLayout.LayoutParams params_com =
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
		b_comment.setLayoutParams(params_com);
		final TextView liveComment = new TextView(this);
		b_comment.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v)
			{
				EditText et2 = (EditText) findViewById(1111);
				final String comment_text = et2.getText().toString();
				Comment new_comment = new Comment(id, ratingBar.getRating(), comment_text, Comm.getEmail());
				addComment(new_comment);
				et2.getText().clear();

				if(Comm.getEmail() != null) {
					System.out.println("username = " + Comm.getEmail());
				}
				else {
					System.out.println("username is null!");
				}


				liveComment.setText(new_comment.username + "\t\t\"" +
						new_comment.rating + " stars\"\n" +
						new_comment.comment + "\n\n\n");
				liveComment.setLayoutParams(params);
				liveComment.setBackgroundColor(Color.WHITE);
				ll.addView(liveComment);
			}
		});
		ll.addView(b_comment);

		//display comments
		for(int i = 0; i < recipe.comments.size(); ++i)
		{
			TextView tv_comment = new TextView(this);
			tv_comment.setText(recipe.comments.get(i).username + "\t\t\"" +
					recipe.comments.get(i).rating + " stars\"\n" +
					recipe.comments.get(i).comment + "\n\n\n");
			tv_comment.setLayoutParams(params);
			tv_comment.setBackgroundColor(Color.WHITE);
			ll.addView(tv_comment);
		}

	}
	public void callImageCompIntent(Bitmap bmp)
	{
		Intent intent = new Intent(this, ImageComp.class);
		intent.putExtra("image", bmp);
		startActivity(intent);
	}
	public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
		// TODO Auto-generated method stub

		TextView rate_val = new TextView(this);
		rate_val.setText(Float.toString(ratingBar.getRating()));
		ratingBarPressed = true;

	}
	public void addComment(Comment comment)
	{
		TextView tv_comment = new TextView(this);
		tv_comment.setText(comment.username + "\t\t\"" +
				comment.rating + " stars\"\n" +
				comment.comment + "\n\n\n");


		final LinearLayout.LayoutParams params =
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);

		tv_comment.setLayoutParams(params);
		LinearLayout ll = (LinearLayout) findViewById(R.id.rflayout);
		System.out.println("RF addComment to layout");
		ll.addView(tv_comment);

		new postCommentTask().execute(comment);
	}

	public void displayRecipeFailure(String text) {
		//return to search activity.
		Intent intent = new Intent(this, MainMenu.class);
		intent.putExtra("errorDisplayRecipe", -1);
		startActivity(intent);

	}


	public void postCommentSuccess() {

	}

	public void postCommentFailure() {
		Utility.displayErrorToasts(getApplicationContext(), -2, LENGTH_LONG);

	}
	public void callQuestionBoardIntent(Recipe recipe)
	{
		Intent intent = new Intent(this, QuestionBoardActivity.class);
		System.out.println(recipeID);
		//TODO:  I NEED TO PASS RECIPE OBJECT THOUGH.

		//intent.putExtra("currentRecipe", recipe.id);
		startActivity(intent);
	}
	private class getOwnerOfRecipe extends AsyncTask<Integer, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Integer... params) {
			Comm c = new Comm();
			int userId = params[0];
			//currently doesn't do anything with the user who owns the recipe.
			//TODO: Need to change c.getUser to c.getUser(uid), when Matt finishes that part.
			User ret = c.getUser(); //this gets the user who is currently logged in.
			//Recipe ret = c.getRecipe(params[0]);

			return (ret != null);
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if (result == true) {
				Log.v("getOwnerOfRecipeSuccess","Success: User of recipe Received");


			} else {
				Log.v("getOwnerOfRecipeFailure","Failure: Did not receive User of recipe");

			}
		}

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



