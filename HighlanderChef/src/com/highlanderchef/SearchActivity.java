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



public class SearchActivity extends ActionBarActivity {
	private final String errorMessage = "";
	private final String SearchByString = "Search By String";
	private final String SearchByCategory = "Search By Category";
	private final String SearchByMyUID = "Search By UID";
	private final String ViewDrafts = "ViewDrafts";
	private final String ViewFavorites = "ViewFavorites";

	private boolean ViewingDrafts = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		Intent intent = getIntent();
		String query = intent.getStringExtra("search_query");
		String category = intent.getStringExtra("category_query");
		Utility.GetLoggedInUser();

		if (query != null) {
			new SearchTask().execute(SearchByString, query);
		} else if(category != null) {
			new SearchTask().execute(SearchByCategory, category);
		}
		else if (intent.getStringExtra(ViewDrafts) != null){
			new SearchTask().execute(ViewDrafts);
			ViewingDrafts = true;
		}
		else {
			new SearchTask().execute(SearchByMyUID);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
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

	public void SearchSuccess(ArrayList<Recipe> recipies, ArrayList<Integer> dIDs)
	{
		RecipeList = recipies;
		LinearLayout rl = (LinearLayout) findViewById(R.id.linearLayoutResults);
		for(int i = 0; i < recipies.size(); ++i)
		{
			if(recipies.get(i) == null )
				continue;
			//LinearLayout rl = (LinearLayout) findViewById(R.id.linearLayoutResults);
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

				int targetW = 500; //TODO find better way to do this.
				int targetH = 3;
				//scale image
				int scalefactor = Math.min(photoW/targetW, photoH/targetH);

				// Decode the image file into a Bitmap sized to fill the View
				bmOptions.inJustDecodeBounds = false;
				bmOptions.inSampleSize = scalefactor;
				bmOptions.inPurgeable = true;

				Bitmap div = BitmapFactory.decodeResource(getResources(), R.drawable.divider, bmOptions);

				iv_divider.setImageBitmap(div);
				rl.addView(iv_divider);
			}
			TextView tv = new TextView(this);
			tv.setText(recipies.get(i).getName() + "\n");
			//tv.setPadding(0, (i * 30), 0, 0);
			rl.addView(tv);
			if(recipies.get(i).isMainImage())
			{
				ImageView iv = new ImageView(this);
				iv.setImageBitmap(recipies.get(i).getMainImage());
				rl.addView(iv);
			}
			TextView tv_descr = new TextView(this);
			tv_descr.setText("Description \n" + recipies.get(i).getDescription());
			rl.addView(tv_descr);

			TextView tv_cooktime = new TextView(this);
			tv_cooktime.setText(recipies.get(i).getCookTime());
			rl.addView(tv_cooktime);

			final int j;

			Button b_view = new Button(this);
			if (ViewingDrafts == false) {
				b_view.setText("View");
				j = recipies.get(i).id; //so java doesn't complain
			}
			else {
				b_view.setText("Edit");
				j = dIDs.get(i);
			}

			b_view.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v)
				{
					if (ViewingDrafts == false)
						callRecipeIntent(j);
					else
						callEditIntent(j);
				}
			});
			rl.addView(b_view);
		}
	}

	public void SearchFailure(ArrayList<Recipe> recipies)
	{
		LinearLayout rl = (LinearLayout) findViewById(R.id.linearLayoutResults);
		TextView tv_descr = new TextView(this);
		tv_descr.setText("No Recipes Found!");
		rl.addView(tv_descr);
		//TextView searchNoResults = (TextView) findViewById(R.id.linearLayoutResults);//darren
		//searchNoResults.setText("No Recipes Found!");
		rl.setVisibility(View.VISIBLE);
	}


	public void callRecipeIntent(int index)
	{
		Intent intent = new Intent(this, RecipeForum.class);
		intent.putExtra("recipeID", index);
		startActivity(intent);
	}

	public void callEditIntent(int index)
	{
		Intent intent = new Intent(this, MakeARecipe1.class);
		intent.putExtra("DraftID", index);
		startActivity(intent);
	}

	private class SearchTask extends AsyncTask<String, Void, Boolean>
	{
		ArrayList<Recipe> ret = new ArrayList<Recipe>();
		ArrayList<Integer> DraftsID = new ArrayList<Integer>();
		@Override
		protected Boolean doInBackground(String... params) {
			Comm c = new Comm();
			if (params[0] == SearchByString) {
				ret = c.searchRecipes(params[1]);
			} else if (params[0] == SearchByCategory) {
				ret = c.searchRecipesByCategory(Integer.parseInt(params[1]));
			} else if (params[0] == SearchByMyUID) {
				ret = c.searchRecipesByUID(c.getUserID());
			} else if (params[0] == ViewDrafts) {
				ArrayList<Integer> DraftsID = c.getDraftList();
				for (int i = 0; i < DraftsID.size(); i++)
					ret.add(c.getDraft(DraftsID.get(i)));
			} else if (params[0] == ViewFavorites) {
				//				ArrayList<Integer> FavoriteID = c.getFavorites();
				//				for (int i = 0; i < FavoriteID.size(); i++)
				//					ret.add(c.getDraft(FavoriteID.get(i)));
			}
			return (ret.size() > 0);
		}


		@Override
		protected void onPostExecute(Boolean result) {
			if (result == true) {
				Log.v("searchActivty","Search Success");
				SearchSuccess(ret, DraftsID);
			} else {
				Log.v("login_fail","Search Failed");
				SearchFailure(ret);
			}
		}

	}



}
