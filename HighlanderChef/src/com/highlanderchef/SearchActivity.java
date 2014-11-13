package com.highlanderchef;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		Intent intent = getIntent();
		String query = intent.getStringExtra("search_query");

		new SearchTask()
		.execute(query);
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

	public void SearchSuccess(ArrayList<Recipe> recipies)
	{
		LinearLayout rl = (LinearLayout) findViewById(R.id.linearLayoutResults);
		for(int i = 0; i < recipies.size(); ++i)
		{
			TextView tv = new TextView(this);
			tv.setText(recipies.get(i).getName());
			//tv.setPadding(0, (i * 30), 0, 0);
			rl.addView(tv);
			if(recipies.get(i).isMainImage())
			{
				ImageView iv = new ImageView(this);
				iv.setImageBitmap(recipies.get(i).getMainImage());
				rl.addView(iv);
			}
			TextView tv_descr = new TextView(this);
			tv_descr.setText(recipies.get(i).getDescription());
			rl.addView(tv_descr);

			TextView tv_cooktime = new TextView(this);
			tv_cooktime.setText(recipies.get(i).getDescription());
			rl.addView(tv_cooktime);
		}

	}

	public void SearchFailure(ArrayList<Recipe> recipies)
	{

	}

	private class SearchTask extends AsyncTask<String, Void, Boolean>
	{
		ArrayList<Recipe> ret = new ArrayList<Recipe>();
		@Override
		protected Boolean doInBackground(String... params) {
			Comm c = new Comm();
			ret = c.searchRecipes(params[0]);
			return (ret.size() > 0);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result == true) {
				//Log.v("login_log","Login Success");
				SearchSuccess(ret);
			} else {
				//	Log.v("login_fail","Login failed");
				SearchFailure(ret);
			}
		}

	}
}
