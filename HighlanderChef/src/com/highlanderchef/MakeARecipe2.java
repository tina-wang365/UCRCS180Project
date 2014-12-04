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
	User currentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_a_recipe2);

		TextView tv_header = (TextView) findViewById(R.id.makearecipe2header);
		String header = tv_header.getText().toString();

		Intent intent = getIntent();
		recipe = (Recipe)intent.getSerializableExtra("recipe");
		tv_header.setText(header + " for " + recipe.getName());
		Utility.GetLoggedInUser();
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
			new_ingred_list += recipe.getAnIngredient(i).getAmount()+ '\t' + recipe.getAnIngredient(i).getName() + '\n';
		}
		new_ingred_list += new_ingred_amount + '\t' + new_ingred;

		recipe.addIngredient(new Ingredient(new_ingred, new_ingred_amount));
		textview_ingred_list.setText(new_ingred_list);
		edittext_new_ingred.getText().clear();
		edittext_new_ingred_amount.getText().clear();
	}

	public void SaveAsDraftPressed(View iView)
	{
		recipe.setUID(currentUser.getID());
		recipe.setUsername(currentUser.getUsername());
		Utility.UploadDraft(recipe);
		Intent intent = new Intent(this, MainMenu.class);
		startActivity(intent);
	}

	public void addDirectionsPressed(View view)
	{
		Intent intent = new Intent(this, MakeARecipe3.class);
		intent.putExtra("recipe", recipe);
		startActivity(intent);
	}
}
