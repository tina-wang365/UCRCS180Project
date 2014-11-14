package com.highlanderchef;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainMenu extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new UsernameTask().execute();
		setContentView(R.layout.activity_main_menu);

		Intent intent = getIntent();
		String con_msg = (String)intent.getSerializableExtra("Recipe Confirmation");
		TextView tv_con_msg = (TextView) findViewById(R.id.confirmationMessage);
		if(con_msg == null)
		{

			tv_con_msg.setVisibility(View.INVISIBLE);
		}
		else
		{
			//TODO test this
			tv_con_msg.setText(con_msg);
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

		//This currently opens up the Default view recipe activity, so it can
		//be view for testing.
		Intent intent = new Intent(this, RecipeForum.class);
		intent.putExtra("recipeID", 50);
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

	public void setUsername(String iName)
	{
		String username = iName;
		String strWelcomeFormat = getResources().getString(R.string.Welcome_Chef);
		String strWelcomeMsg = String.format(strWelcomeFormat,username);
		((TextView) findViewById(R.id.textView1)).setText(strWelcomeMsg);
	}

	private class UsernameTask extends AsyncTask<String, Void, Boolean>
	{
		String cUsername = new String();
		@Override
		protected Boolean doInBackground(String... params) {
			Comm iComm = new Comm();
			cUsername = iComm.getEmail();
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
}
