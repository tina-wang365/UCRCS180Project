package com.highlanderchef;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MakeARecipe2 extends ActionBarActivity {

	ArrayList<String> ingreds_list = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_a_recipe2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.make_arecipe2, menu);
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

	public void addAnotherIngredientPressed(View view)
	{
		EditText edittext_new_ingred = (EditText) findViewById(R.id.addaningredient);
		String new_ingred = edittext_new_ingred.getEditableText().toString();

		TextView textview_ingred_list = (TextView) findViewById(R.id.listofaddedingredients);

		String new_ingred_list = "";
		for(int i = 0; i < ingreds_list.size(); ++i)
		{
			new_ingred_list += '\n' + ingreds_list.get(i);
		}
		new_ingred_list += '\n' + new_ingred;

		ingreds_list.add(new_ingred);
		textview_ingred_list.setText(new_ingred_list);
		//edittext_new_ingred.clear
	}

}
