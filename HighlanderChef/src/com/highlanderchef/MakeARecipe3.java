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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MakeARecipe3 extends ActionBarActivity {

	Recipe recipe = new Recipe();
	String picturePath;

	int dir_added_count = 0;
	ArrayList<Bitmap> added_images = new ArrayList<Bitmap>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_a_recipe3);

		Intent intent = getIntent();
		recipe = (Recipe)intent.getSerializableExtra("recipe");


		TextView tv_header = (TextView) findViewById(R.id.makearecipe3header);
		String header = tv_header.getText().toString();
		tv_header.setText(header + "for " + recipe.getName());

		TextView tv_error = (TextView) findViewById(R.id.submit_error);
		tv_error.setVisibility(View.INVISIBLE);
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

	public void addADirectionPressed(View view)
	{
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
		//LinearLayout linear_layout = (LinearLayout) findViewById(R.id.linearLayoutImages);
		for(i = 0; i < recipe.directionSize(); ++i)
		{
			int tmp = i + 1;
			new_dir_list += tmp + ") " + recipe.getADirection(i).getDirectionText() + '\n';
			/*Bitmap tmp_image = recipe.getAnImage(i);
			if(tmp_image != null)
			{
				ImageView image = new ImageView(MakeARecipe3.this);
				image.setImageBitmap(tmp_image);
				linear_layout.addView(image);
			}*/
		}
		++i;
		new_dir_list += i + ") " + new_dir;

		recipe.AddADirection(new_dir, added_images);
		textview_dir_list.setText(new_dir_list);
		edittext_new_dir.getText().clear();
		ImageView imageview = (ImageView) findViewById(R.id.added_image);
		imageview.setImageResource(R.drawable.uploadimage);
		++dir_added_count;
	}

	private static int RESULT_LOAD_IMAGE = 1;
	public void addImageToDirectionPressed(View view)
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
			picturePath = cursor.getString(columnIndex);
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
				added_images.add(bitmap);
			}
			else
			{
				//TODO added error response
			}
		}
	}
	public void submitRecipePressed(View view)
	{
		new UploadRecipeTask().execute(recipe);
		TextView tv_error = (TextView) findViewById(R.id.submit_error);
		tv_error.setVisibility(View.INVISIBLE);//resume_here
	}
	public void onSuccess()
	{
		Intent intent = new Intent(this, MainMenu.class);
		intent.putExtra("Recipe Confirmation", "Recipe added successfully");
		startActivity(intent);
	}
	public void onFailure()
	{
		TextView tv_error = (TextView) findViewById(R.id.submit_error);
		tv_error.setVisibility(View.VISIBLE);
		//TODO implement better case for failure.
	}
	private class UploadRecipeTask extends AsyncTask<Recipe, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Recipe... params) {
			Comm c = new Comm();
			int ret = c.uploadRecipe(params[0]);

			return (ret == Comm.SUCCESS);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result == true) {
				onSuccess();
			} else {
				onFailure();

			}
		}

	}
}
