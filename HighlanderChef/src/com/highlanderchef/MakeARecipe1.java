package com.highlanderchef;

import java.util.ArrayList;
import java.util.Stack;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

public class MakeARecipe1 extends ActionBarActivity {
	Recipe recipe = new Recipe();
	ArrayList<Category> categories;
	ArrayList<Integer> categoryIDs;
	int curSelCat = 0;
	String errorMessage = "";
	Spinner spinner;
	Bundle b;
	User currentUser;
	Recipe Draft;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_a_recipe1);
		categoryIDs = new ArrayList<>();
		new GetCategoriesTask().execute(1);
		currentUser = Utility.GetLoggedInUser();
		Intent intent = this.getIntent();
		int DraftID = intent.getIntExtra("DraftID", -1);
		if (DraftID > 0){
			new GetDraft().execute(DraftID);
		}
		Utility.displayErrorToast(this, "Got DID: " + DraftID);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.make_arecipe1, menu);
		return true;
	}

	private Recipe getCurrentRecipeInfo()
	{
		Recipe returnRecipe = null;
		if (Draft == null)
			returnRecipe = new Recipe();
		else
			returnRecipe = Draft;

		EditText edittext_name = (EditText) findViewById(R.id.recipe_title);
		String new_name = edittext_name.getText().toString();

		EditText edittext_descr = (EditText) findViewById(R.id.recipe_description);
		String new_descr = edittext_descr.getText().toString();

		EditText edittext_time = (EditText) findViewById(R.id.recipe_est_time);
		String new_time = edittext_time.getText().toString();

		returnRecipe.setName(new_name);
		returnRecipe.setDescription(new_descr);
		returnRecipe.setCookTime(new_time);

		return returnRecipe;
	}

	public void SaveAsDraftPressed(View iView)
	{
		recipe = getCurrentRecipeInfo();
		Utility.UploadDraft(recipe);
		Intent intent = new Intent(this, MainMenu.class);
		startActivity(intent);
	}

	public void AddIngrediantPressed(View view)
	{
		recipe = getCurrentRecipeInfo();
		if (recipe.getName().length() <= 0)
		{
			Utility.displayErrorToast(this, "Please enter a name for the recipe");
			return;
		}
		Intent intent = new Intent(this, MakeARecipe2.class);
		if (Draft != null)
		{
			Utility.UploadDraft(Draft);
			intent.putExtra("DraftID", Draft.id);
			intent.putExtra("DraftName", Draft.getName());
		}
		else
		{
			intent.putExtra("recipe", recipe);
		}
		startActivity(intent);
	}
	private static int RESULT_LOAD_IMAGE = 1;
	public void AddImagePressed(View view)
	{
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null)
		{
			recipe.categories.clear();
			recipe.categories.add(categoryIDs.get(curSelCat));
			Uri selectedImage = data.getData();
			String[] filePathColumn = {MediaStore.Images.Media.DATA};

			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			ImageView imageView = (ImageView) findViewById(R.id.added_image);

			//Get the dimensions of the Image View
			int targetW = imageView.getWidth();
			int targetH = imageView.getHeight();

			//load bitmap
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(picturePath, bmOptions);

			//Get the dimensions of the bitmap
			int photoW = bmOptions.outWidth;
			int photoH = bmOptions.outHeight;

			//scale image
			int scalefactor = Math.min(photoW/targetW, photoH/targetH);

			// Decode the image file into a Bitmap sized to fill the View
			bmOptions.inJustDecodeBounds = false;
			bmOptions.inSampleSize = scalefactor;
			bmOptions.inPurgeable = true;

			Bitmap bitmap = BitmapFactory.decodeFile(picturePath, bmOptions);

			if(bitmap != null)
			{
				imageView.setImageBitmap(bitmap);
				recipe.mainImagepath = picturePath;
			}
			else
			{
				//TODO added error response
			}
		}
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

	public void catOnSuccess()
	{
		if(categories.size() > 0)
		{
			ArrayList<String> level0_spinner = new ArrayList<String>();
			Stack<String> cat_prefix = new Stack<String>();
			categoryIDs.clear();
			int last_level = -1;
			for(int i = 0; i < categories.size(); ++i)
			{
				if (last_level >= categories.get(i).level) {
					cat_prefix.pop();
				}
				if(categories.get(i).level == 0) {
					cat_prefix.push(categories.get(i).name);
				} else {
					cat_prefix.push(cat_prefix.peek() + " > " + categories.get(i).name);
				}

				if (i == (categories.size() - 1)) {
					level0_spinner.add(cat_prefix.peek());
					categoryIDs.add(new Integer(categories.get(i).id));
				} else if (categories.get(i + 1).level <= categories.get(i).level) {
					level0_spinner.add(cat_prefix.peek());
					categoryIDs.add(new Integer(categories.get(i).id));
				}

				last_level = categories.get(i).level;
			}
			spinner = (Spinner) findViewById(R.id.spinner);
			ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, level0_spinner);
			adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter_state);
			//spinner.setOnItemSelectedListener(this);

		}
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		spinner.setSelection(position);
		String selState = (String) spinner.getSelectedItem();
		curSelCat = spinner.getSelectedItemPosition();

	}
	public void catOnFailure()
	{

	}

	public void LoadDraft(Recipe iRecipe)
	{
		Draft = iRecipe;
		((EditText) findViewById(R.id.recipe_title)).setText(Draft.getName());

		((EditText) findViewById(R.id.recipe_description)).setText(Draft.getDescription());

		((EditText) findViewById(R.id.recipe_est_time)).setText(Draft.getCookTime());
	}

	private class GetCategoriesTask extends AsyncTask<Object, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Object... params) {
			Comm c = new Comm();
			categories = c.getCategories();
			if(categories != null) {
				errorMessage = "Error! Network Failed to connect. Check your network";
			} else if(categories == null) {
				errorMessage = "Sorry! There was an error making your recipe";
			}
			return (categories != null);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result == true) {
				catOnSuccess();
			} else {
				catOnFailure();
			}
		}
	}

	private class GetDraft extends AsyncTask<Integer, Void, Recipe> {

		@Override
		protected Recipe doInBackground(Integer... params) {
			Comm iComm = new Comm();
			return iComm.getDraft(params[0]);
		}

		@Override
		protected void onPostExecute(Recipe result) {
			if (result == null)
				return;
			LoadDraft(result);
		}
	}
}
