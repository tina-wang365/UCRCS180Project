package com.highlanderchef;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class UserHomepage extends ActionBarActivity {

	//User CurrentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_homepage);
		new UsernameTask().execute();
		new UserRecipes().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_homepage, menu);
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

	public void FollowUser(View iView)
	{
		//Add current user to the user's (being viewed) follower list
	}

	public void callRecipeIntent(int index)
	{
		Intent intent = new Intent(this, RecipeForum.class);
		intent.putExtra("recipeID", index);
		startActivity(intent);
	}

	public void setUsername(String iName)
	{
		((TextView) findViewById(R.id.Username)).setText(iName);
	}

	@SuppressWarnings("unused")
	private class UsernameTask extends AsyncTask<String, Void, Boolean>
	{
		String cUsername = new String();
		@Override
		protected Boolean doInBackground(String... params) {
			cUsername = Comm.getEmail();
			return (cUsername.length() > 0);
		}


		@Override
		protected void onPostExecute(Boolean result) {
			if (result == true) {
				setUsername(cUsername);
			}
			else {
				Log.e("get_username_fail","Could not get username from server.");
				setUsername(new String());
			}
		}

	}

	public void PopulateRecipeList(ArrayList<Recipe> RecipeList)
	{
		LinearLayout ILinearLayout = (LinearLayout) findViewById(R.id.RecipeLayout);
		for(int i = 0; i < RecipeList.size(); ++i)
		{
			if(RecipeList.get(i) == null )
				continue;
			//code for dividers
			if(i >= 1)
			{

				ImageView iv_divider = new ImageView(this);

				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
				bmOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeResource(getResources(), R.drawable.divider, bmOptions);

				//Get the dimensions of the bitmap
				int photoW = bmOptions.outWidth;
				int photoH = bmOptions.outHeight;

				int targetW = 200; //TODO find better way to do this.
				int targetH = 3;
				//scale image
				int scalefactor = Math.min(photoW/targetW, photoH/targetH);

				// Decode the image file into a Bitmap sized to fill the View
				bmOptions.inJustDecodeBounds = false;
				bmOptions.inSampleSize = scalefactor;
				bmOptions.inPurgeable = true;

				Bitmap div = BitmapFactory.decodeResource(getResources(), R.drawable.divider, bmOptions);

				iv_divider.setImageBitmap(div);
				ILinearLayout.addView(iv_divider);
			}
			TextView tv = new TextView(this);
			tv.setText(RecipeList.get(i).getName() + "\n");
			//tv.setPadding(0, (i * 30), 0, 0);
			ILinearLayout.addView(tv);
			if(RecipeList.get(i).isMainImage())
			{
				ImageView iv = new ImageView(this);
				iv.setImageBitmap(RecipeList.get(i).getMainImage());
				ILinearLayout.addView(iv);
			}
			TextView tv_descr = new TextView(this);
			tv_descr.setText("Description \n" + RecipeList.get(i).getDescription());
			ILinearLayout.addView(tv_descr);

			TextView tv_cooktime = new TextView(this);
			tv_cooktime.setText(RecipeList.get(i).getCookTime());
			ILinearLayout.addView(tv_cooktime);

			final int j = RecipeList.get(i).id; //so java doesn't complain

			Button b_view = new Button(this);
			b_view.setText("View");

			b_view.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v)
				{

					callRecipeIntent(j);
				}
			});
			ILinearLayout.addView(b_view);
		}
	}


	@SuppressWarnings("unused")
	private class UserRecipes extends AsyncTask<String, Void, Boolean>
	{
		ArrayList<Recipe> UserRecipeList;
		@Override
		protected Boolean doInBackground(String... params)
		{
			Comm IComm = new Comm();
			//Get Recipes of the User
			UserRecipeList = IComm.searchRecipesByUID(0);
			return (UserRecipeList.size() > 0);
		}


		@Override
		protected void onPostExecute(Boolean result)
		{
			if (result != true)
			{
				//log error
				//Make empty list
				//Display "User has no recipes"
			}
			//Goto function that populate page with recipes;
		}

	}
}


