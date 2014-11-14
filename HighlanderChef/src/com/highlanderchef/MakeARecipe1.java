package com.highlanderchef;

import java.util.ArrayList;

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
	String errorMessage = "";
	Spinner spinner;
	Bundle b;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_a_recipe1);

		new GetCategoriesTask().execute(1);
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
			for(int i = 0; i < categories.size(); ++i)
			{
				if(categories.get(i).level == 0)
				{
					level0_spinner.add(categories.get(i).name);

				}
			}
			spinner = (Spinner) findViewById(R.id.spinner);
			ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, level0_spinner);
			adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter_state);
			spinner.setOnItemSelectedListener(this);

		}
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		spinner.setSelection(position);
		String selState = (String) spinner.getSelectedItem();

	}
	public void catOnFailure()
	{

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
}
