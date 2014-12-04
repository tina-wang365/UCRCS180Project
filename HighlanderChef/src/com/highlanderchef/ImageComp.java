package com.highlanderchef;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class ImageComp extends ActionBarActivity
{
	private Bitmap image1;
	private CameraPreview camPreview;
	private FrameLayout mainLayout;

	private final Handler mHandler = new Handler(Looper.getMainLooper());

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//Set this SPK Full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//Set this APK no title
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_comp);

		Intent intent = getIntent();
		image1 = (Bitmap)intent.getParcelableExtra("image");

		Display display = getWindowManager().getDefaultDisplay();
		SurfaceView camView = new SurfaceView(this);
		SurfaceHolder camHolder = camView.getHolder();
		camPreview = new CameraPreview(800, 480, image1);

		camHolder.addCallback(camPreview);
		camHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mainLayout = (FrameLayout) findViewById(R.id.camera_preview);
		mainLayout.addView(camView, new LayoutParams(800, 480));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			int X = (int)event.getX();
			if ( X >= 640 )
				mHandler.postDelayed(TakePicture, 300);
			else
				camPreview.CameraStartAutoFocus();
		}
		return true;
	};

	private final Runnable TakePicture = new Runnable()
	{
		//String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
		//String MyDirectory_path = extStorageDirectory;
		//String PictureFileName;
		@Override
		public void run()
		{
			/*File file = new File(MyDirectory_path);
	   if (!file.exists())
	    file.mkdirs();
	   PictureFileName = MyDirectory_path + "/MyPicture.jpg";
	   camPreview.CameraTakePicture(PictureFileName);*/
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
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
