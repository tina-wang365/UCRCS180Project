package com.highlanderchef;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainMenu extends ActionBarActivity {

	private static final int LENGTH_LONG = 3500;
	private User currentUser = new User();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new UsernameTask().execute();
		setContentView(R.layout.activity_main_menu);

		Intent intent = getIntent();
		String con_msg = (String)intent.getSerializableExtra("Recipe Confirmation");
		if(con_msg == null)
		{
			;
		}
		else
		{
			int pos[] = {0 , 0};
			pos[0] = this.getResources().getDisplayMetrics().widthPixels;
			pos[1] = this.getResources().getDisplayMetrics().heightPixels / 4;
			Toast followToast = Toast.makeText(getApplicationContext(), "Recipe added successfully", LENGTH_LONG);
			followToast.setGravity(Gravity.TOP, 0, pos[1]); //gravity, x-offset, y-offset
			followToast.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	public void BrowsePressed(View view)
	{
		Intent intent = new Intent(this, BrowseActivity.class);
		startActivity(intent);
	}

	public void ComposePressed(View view)
	{
		Intent intent = new Intent(this, MakeARecipe1.class);
		startActivity(intent);
	}

	public void ViewMyRecipesPressed(View view)
	{
		//Intent intent = new Intent(this, ViewMyRecipes.class);
		//startActivity(intent);

		/*
		//This currently opens up the Default view recipe activity, so it can
		//be view for testing.
		Intent intent = new Intent(this, RecipeForum.class);
		intent.putExtra("recipeID", 50);
		startActivity(intent);
		 */

		Intent intent = new Intent(this, SearchActivity.class);
		startActivity(intent);
	}

	public void SearchPressed(View view)
	{
		EditText et_search_query = (EditText) findViewById(R.id.editText1);
		String search_query = et_search_query.getText().toString();
		Intent intent = new Intent(this, SearchActivity.class);
		intent.putExtra("search_query", search_query);
		startActivity(intent);

	}

	public void ViewHomepage(View view)
	{
		Intent intent = new Intent(this, UserHomepage.class);
		intent.putExtra("ViewUser", currentUser.getID());
		startActivity(intent);
	}

	public void ViewDrafts(View view)
	{
		Intent intent = new Intent(this, SearchActivity.class);
		intent.putExtra("ViewDrafts", "View Drafts");
		startActivity(intent);
	}

	public void setUser(User iUser)
	{
		currentUser = iUser;
		String username = currentUser.getUsername();
		String strWelcomeFormat = getResources().getString(R.string.Welcome_Chef);
		String strWelcomeMsg = String.format(strWelcomeFormat,username);
		((TextView) findViewById(R.id.textView1)).setText(strWelcomeMsg);
	}

	private class UsernameTask extends AsyncTask<String, Void, Boolean>
	{
		User cUser = new User();
		@Override
		protected Boolean doInBackground(String... params) {
			cUser = Comm.getUser();
			if (cUser == null)
				return false;
			return (cUser.getUsername().length() > 0);
		}


		@Override
		protected void onPostExecute(Boolean result) {
			if (result == true) {
				setUser(cUser);
			}
			else {
				Log.e("getUser Failed","Could not get user from server.");
				setUser(new User());
			}
		}

	}
}
