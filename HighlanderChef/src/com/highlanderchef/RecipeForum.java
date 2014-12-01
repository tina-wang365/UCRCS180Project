package com.highlanderchef;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RecipeForum extends ActionBarActivity {

	int TEXT_ALIGNMENT_CENTER = 4;
	Recipe currentRecipe = null;
	//	Comment currentComment = null;
	int recipeID = 0;
	private Button btnComment;
	private RatingBar ratingBar;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_forum);
		//TextView failedToDisplayRecipe = (TextView) findViewById(R.id.errorCannotDisplayRecipe);
		//failedToDisplayRecipe.setVisibility(View.INVISIBLE);

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

	public void downloadRecipe() {
		new getRecipeTask().execute(recipeID);
	}


	public void displayRecipeSuccess(Recipe recipe) {
		RelativeLayout recipeForumLayout = (RelativeLayout) findViewById(R.id.relativeLayoutRecipeForum);
		ID_Maker MakerInstance = ID_Maker.getInstance();
		View lastView = null;

		ImageView ivmain = new ImageView(this);
		MakerInstance.useCurrID();
		ivmain.setId(MakerInstance.useCurrID());
		if((Integer)ivmain.getId() == null) {
			System.out.println("IVMAIN ID = NULL");
		}
		else {
			System.out.println("ÏV MAIN IS NOT NULL! ID is: " + Integer.toString(ivmain.getId()));
		}

		lastView = ivmain;
		RelativeLayout.LayoutParams ivParams =
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		if(recipe.mainImage != null)
		{
			//set main image
			ivmain.setImageBitmap(Bitmap.createScaledBitmap(currentRecipe.mainImage, 220, 220, false)); //false means not filter
			ivmain.setBackgroundColor(Color.parseColor("#8C001A"));
			ivmain.setLayoutParams(ivParams);
		}
		else {
			System.out.println("Posting No Image Available");
		}
		recipeForumLayout.addView(ivmain);

		//Set objects for display on activity -- Title
		TextView tvTitle = new TextView(this);
		tvTitle.setId(MakerInstance.useCurrID());
		if(tvTitle.getId() == lastView.getId()) {
			System.out.println("THE IDs ARE THE SAME");
		}
		else {
			System.out.println("THE IDS ARE NOT THE SAME! ID's are: " + Integer.toString(tvTitle.getId()) + ", " + Integer.toString(lastView.getId()));
		}
		if(recipe.name != null) {
			tvTitle.setText("\t" + recipe.name + "\n");
		}
		else {
			tvTitle.setText("Title: N/A\n");
		}
		RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		titleParams.addRule(RelativeLayout.RIGHT_OF, lastView.getId());
		titleParams.setMargins(0, 0, 0, 0); //left, top, down, right
		tvTitle.setTextSize(1, 15);
		tvTitle.setLayoutParams(titleParams);
		tvTitle.setBackgroundColor(Color.parseColor("#8C001A"));
		recipeForumLayout.addView(tvTitle);
		lastView = tvTitle;

		//Set objects for display on activity -- CookTime
		TextView tvCookTime = new TextView(this);
		tvCookTime.setId(MakerInstance.useCurrID());

		if(recipe.cookTime == null) {
			tvCookTime.setText("Estimate: N/A");
			System.out.println("Cook Time N/A");
		}
		else {
			tvCookTime.setText("Estimate: " + recipe.cookTime);
			System.out.println("cookTime is not empty!");
		}
		RelativeLayout.LayoutParams estimateParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		estimateParams.addRule(RelativeLayout.RIGHT_OF, ivmain.getId());
		estimateParams.addRule(RelativeLayout.BELOW, tvTitle.getId());
		estimateParams.addRule(RelativeLayout.ALIGN_LEFT, tvTitle.getId());

		tvCookTime.setLayoutParams(estimateParams);
		recipeForumLayout.addView(tvCookTime);

		//
		//		//Set objects for display on activity -- Description
		//		TextView textViewDes = new TextView(this);
		//		if(recipe.name != null) {
		//			textViewDes.setText(recipe.description + "\n");
		//		}
		//		else {
		//			textViewDes.setText("Description: N/A\n");
		//		}
		//		textViewDes.setLayoutParams(params);
		//		ll.addView(textViewDes);
		//

		//

		//
		//		//create string for ingredient text
		//		String formatOfIngredient = "";
		//		if(recipe.ingredients.size() > 0) {
		//			for(int i = 0; i < recipe.ingredients.size(); ++i) {
		//				formatOfIngredient += "* " + recipe.ingredients.get(i).amount + " " + recipe.ingredients.get(i).name;
		//				if((i + 1) < recipe.ingredients.size()) {
		//					formatOfIngredient += "\n";
		//				}
		//			}
		//			formatOfIngredient += "\n";
		//		}
		//		else {
		//			formatOfIngredient = "Ingredient List size = 0\n";
		//		}
		//
		//		TextView textViewIngredients = new TextView(this);
		//		textViewIngredients.setText(formatOfIngredient);
		//		textViewIngredients.setLayoutParams(params);
		//		ll.addView(textViewIngredients);
		//
		//		//Parse directions into neat format
		//		String formatOfDirection = "";
		//		if(recipe.directions.size() > 0)
		//		{
		//			for(int i = 0; i < recipe.directions.size(); ++i)
		//			{
		//				formatOfDirection += (i + 1) + ". " + recipe.directions.get(i).text;
		//
		//				TextView textViewDirections = new TextView(this);
		//				textViewDirections.setText(formatOfDirection);
		//				textViewIngredients.setLayoutParams(params);
		//				ll.addView(textViewDirections);
		//				formatOfDirection = "";
		//
		//				for(int j = 0; j < recipe.directions.get(i).images.size(); ++j)
		//				{
		//					//TODO set images horizontal    .setLayoutDirection()
		//					ImageView iv_dir_image = new ImageView(this);
		//					iv_dir_image.setImageBitmap(recipe.directions.get(i).images.get(j));
		//					iv_dir_image.setLayoutParams(params);
		//					ll.addView(iv_dir_image);
		//
		//					//button to do image comparison
		//					Button image_comp = new Button(this);
		//					image_comp.setText("Compare");
		//					final LinearLayout.LayoutParams params_comp =
		//							new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
		//									LinearLayout.LayoutParams.WRAP_CONTENT);
		//					image_comp.setLayoutParams(params_comp);
		//					final Bitmap image = recipe.directions.get(i).images.get(j);
		//					image_comp.setOnClickListener(new View.OnClickListener(){
		//
		//						@Override
		//						public void onClick(View v)
		//						{
		//							callImageCompIntent(image);
		//						}
		//					});
		//					ll.addView(image_comp);
		//				}
		//			}
		//		}
		//		else
		//		{
		//			formatOfDirection = "Direction List size = 0";
		//		}
		//
		//		//commetns edit text field
		//		EditText et_comment = new EditText(this);
		//		et_comment.setHint("Add a comment");
		//		et_comment.setId(1111);
		//		//et_params.addRule(RelativeLayout.BELOW, ll.getChildAt(ll.getChildCount()-1).getId());
		//		et_comment.setLayoutParams(params);
		//		ll.addView(et_comment);
		//
		//		//comments rating bar
		//		final LinearLayout.LayoutParams params_rb =
		//				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
		//						LinearLayout.LayoutParams.WRAP_CONTENT);
		//		final int id = recipe.id;
		//		ratingBar = new RatingBar(this);
		//		ratingBar.setStepSize((float) 0.5);
		//		ratingBar.setMax(5);
		//		ratingBar.setId(1);
		//		ratingBar.setRating(2.0f);
		//		ratingBar.setNumStars(5);
		//		ll.addView(ratingBar, params_rb);
		//
		//		//submit comment button
		//		Button b_comment = new Button(this);
		//		b_comment.setText("Comment");
		//		final LinearLayout.LayoutParams params_com =
		//				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
		//						LinearLayout.LayoutParams.WRAP_CONTENT);
		//		b_comment.setLayoutParams(params_com);
		//		b_comment.setOnClickListener(new View.OnClickListener(){
		//
		//			@Override
		//			public void onClick(View v)
		//			{
		//				EditText et2 = (EditText) findViewById(1111);
		//				final String comment_text = et2.getText().toString();
		//				Comment new_comment = new Comment(id, ratingBar.getRating(), comment_text, Comm.getEmail());
		//				addComment(new_comment);
		//				et2.getText().clear();
		//			}
		//		});
		//		ll.addView(b_comment);
		//
		//		//display comments
		//		for(int i = 0; i < recipe.comments.size(); ++i)
		//		{
		//			TextView tv_comment = new TextView(this);
		//			tv_comment.setText(recipe.comments.get(i).username + "\t\t\"" +
		//					recipe.comments.get(i).rating + " stars\"\n" +
		//					recipe.comments.get(i).comment + "\n\n\n");
		//			tv_comment.setLayoutParams(params);
		//			ll.addView(tv_comment);
		//		}
	}
	//
	//	public void callImageCompIntent(Bitmap bmp)
	//	{
	//		Intent intent = new Intent(this, ImageComp.class);
	//		intent.putExtra("image", bmp);
	//		startActivity(intent);
	//	}
	//
	//	public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
	//		// TODO Auto-generated method stub
	//		TextView rate_val = new TextView(this);
	//		rate_val.setText(Float.toString(ratingBar.getRating()));
	//		//tv_comment.rating = ratingBar.getRating();
	//	}
	//	public void addComment(Comment comment)
	//	{
	//		TextView tv_comment = new TextView(this);
	//		tv_comment.setText(comment.username + "\t\t\"" +
	//				comment.rating + " stars\"\n" +
	//				comment.comment + "\n\n\n");
	//		final LinearLayout.LayoutParams params =
	//				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
	//						LinearLayout.LayoutParams.WRAP_CONTENT);
	//		tv_comment.setLayoutParams(params);
	//		LinearLayout ll = (LinearLayout) findViewById(R.id.rflayout);
	//		ll.addView(tv_comment);
	//
	//		new postCommentTask().execute(comment);
	//	}
	//
	public void displayRecipeFailure(String text) {
		//TextView failedToDisplayRecipe = (TextView) findViewById(R.id.errorCannotDisplayRecipe);
		//failedToDisplayRecipe.setVisibility(View.VISIBLE);
	}
	//
	//
	//

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
				//postCommentSuccess();
			} else {
				Log.v("postComment", "postComentFailure");
				//postCommentFailure();
			}
		}
	}
}

