package com.highlanderchef;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MakeARecipe1 extends ActionBarActivity {
	Recipe recipe = new Recipe();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_a_recipe1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.make_arecipe1, menu);
		return true;
	}
	public void AddIngrediantPressed(View view)
	{

		EditText edittext_name = (EditText) findViewById(R.id.recipe_title);
		String new_name = edittext_name.getText().toString();

		//make sure the length of recipe name is not 0
		if(new_name.length() == 0)
			return;

		EditText edittext_descr = (EditText) findViewById(R.id.recipe_description);
		String new_descr = edittext_descr.getText().toString();

		EditText edittext_time = (EditText) findViewById(R.id.recipe_est_time);
		String new_time = edittext_time.getText().toString();

		recipe.setName(new_name);
		recipe.setDescription(new_descr);
		recipe.setCookTime(new_time);

		Intent intent = new Intent(this, MakeARecipe2.class);
		intent.putExtra("recipe", recipe);
		startActivity(intent);
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
}
