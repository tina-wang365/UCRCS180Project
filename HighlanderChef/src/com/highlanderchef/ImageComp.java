package com.highlanderchef;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageComp extends ActionBarActivity {

	int recipeID = 0;
	Bitmap image1;
	Bitmap image2;

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_comp);

		Intent intent = getIntent();
		image1 = (Bitmap)intent.getParcelableExtra("image");

		ImageView iv_image1 = (ImageView) findViewById(R.id.image1);
		iv_image1.setImageBitmap(image1);

		Intent intent_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);  // create a file to save the video
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name
		startActivityForResult(intent_camera, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
		{
			if (resultCode == RESULT_OK)
			{
				// Image captured and saved to fileUri specified in the Intent
				Toast.makeText(this, "Image saved to:\n" +
						fileUri.toString(), Toast.LENGTH_LONG).show();

				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
				bmOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(fileUri.toString());
				//Get the dimensions of the bitmap
				int photoW = bmOptions.outWidth;
				int photoH = bmOptions.outHeight;

				int targetW = 500; //TODO find better way to do this.
				int targetH = 3;
				//scale image
				int scalefactor = Math.min(photoW/targetW, photoH/targetH);

				// Decode the image file into a Bitmap sized to fill the View
				bmOptions.inJustDecodeBounds = false;
				bmOptions.inSampleSize = scalefactor;
				bmOptions.inPurgeable = true;

				image2 = BitmapFactory.decodeFile(fileUri.toString());
				ImageView iv_image2 = (ImageView) findViewById(R.id.image2);
				iv_image2.setImageBitmap(image2);


			}
			else if (resultCode == RESULT_CANCELED)
			{
				// User cancelled the image capture
			}
			else
			{
				// Image capture failed, advise user
			}
		}

	}
	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
		return Uri.fromFile(getOutputMediaFile(type));
	}

	//public boolean CompareImages()
	//{
	//		Mat image1 = new Mat();
	//	Utils.bitmapToMat(this.image1, image1);
	//}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				//TODO add error message
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"IMG_"+ timeStamp + ".jpg");
		} else if(type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"VID_"+ timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_comp, menu);
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
}
