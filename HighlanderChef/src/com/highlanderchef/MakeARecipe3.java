package com.highlanderchef;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MakeARecipe3 extends ActionBarActivity {

	Recipe recipe = new Recipe();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_a_recipe3);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.make_arecipe3, menu);
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

	public void addAnIngredientPressed(View view)
	{
		Intent intent = getIntent();
		recipe = (Recipe)intent.getSerializableExtra("recipe");
		//gets text for newly added direction
		EditText edittext_new_dir = (EditText) findViewById(R.id.addadirection);
		String new_dir = edittext_new_dir.getText().toString();

		//checks if inputed text length is greater than zero
		if(new_dir.length() == 0 )
		{ return; }

		TextView textview_dir_list = (TextView) findViewById(R.id.listofingredientsadded);

		//creates new text for ingredients list, includes newly added ingredient
		String new_dir_list = "";
		int i;
		for(i = 0; i < recipe.directionSize(); ++i)
		{
			int tmp = i + 1;
			new_dir_list += tmp + ") " + recipe.getADirection(i).getDirectionText() + '\n';
		}
		++i;
		new_dir_list += i + ") " + new_dir;

		recipe.AddADirection(new_dir);
		textview_dir_list.setText(new_dir_list);
		edittext_new_dir.getText().clear();
	}
}
