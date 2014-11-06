package com.highlanderchef;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MakeARecipe2 extends ActionBarActivity {

	Recipe recipe = new Recipe();

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
		//gets text for newly added ingredient
		EditText edittext_new_ingred = (EditText) findViewById(R.id.addaningredient);
		String new_ingred = edittext_new_ingred.getText().toString();

		//gets text for newly added ingredient
		EditText edittext_new_ingred_amount = (EditText) findViewById(R.id.ingredientmeasurement);
		String new_ingred_amount = edittext_new_ingred_amount.getText().toString();

		//checks if inputed text length is greater than zero
		if(edittext_new_ingred.length() == 0 || new_ingred_amount.length() == 0)
		{ return; }

		TextView textview_ingred_list = (TextView) findViewById(R.id.listofaddedingredients);

		//creates new text for ingredients list, includes newly added ingredient
		String new_ingred_list = "";
		for(int i = 0; i < recipe.ingredientSize(); ++i)
		{
			new_ingred_list += recipe.getAnIngredient(i).amount + '\t' + recipe.getAnIngredient(i).name + '\n';
		}
		new_ingred_list += new_ingred_amount + '\t' + new_ingred;

		recipe.addIngredient(new Ingredient(new_ingred, new_ingred_amount));
		textview_ingred_list.setText(new_ingred_list);
		edittext_new_ingred.getText().clear();
		edittext_new_ingred_amount.getText().clear();
	}

	public void addDorectoionsPressed(View view)
	{
		Intent intent = new Intent(this, MakeARecipe3.class);
		startActivity(intent);
	}
}
