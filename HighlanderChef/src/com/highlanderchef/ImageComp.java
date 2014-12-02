package com.highlanderchef;

import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

public class ImageComp extends ActionBarActivity implements SurfaceHolder.Callback{

	int recipeID = 0;
	Bitmap image1;

	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private TextView statusView;
	private View resultView;
	private boolean hasSurface;
	//private InactivityTimer inactivityTimer;



	public Handler getHandler() {
		return handler;
	}

	CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_comp);

		Intent intent = getIntent();
		image1 = (Bitmap)intent.getParcelableExtra("image");

		//ImageView iv_image1 = (ImageView) findViewById(R.id.image1);
		//iv_image1.setImageBitmap(image1);

		hasSurface = false;
		//inactivityTimer = new InactivityTimer(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
		// want to open the camera driver and measure the screen size if we're going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the wrong size and partially
		// off screen.
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		cameraManager = new CameraManager(getApplication(), surfaceView.getHeight(), surfaceView.getWidth() );


		//resultView = findViewById(R.id.result_view);
		//statusView = (TextView) findViewById(R.id.status_view);

		handler = null;

		//resetStatusView();



		if (hasSurface) {
			// The activity was paused but not stopped, so the surface still exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the camera.
			surfaceHolder.addCallback(this);
		}

		//inactivityTimer.onResume();

		Intent intent = getIntent();
	}

	@Override
	protected void onPause()
	{
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		//inactivityTimer.onPause();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}
	@Override
	protected void onDestroy()
	{
		//inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			/*if (source == IntentSource.NATIVE_APP_INTENT) {
				setResult(RESULT_CANCELED);
				finish();
				return true;
			}*/
			/*if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK) && lastResult != null) {
				restartPreviewAfterDelay(0L);
				return true;
			}*/
			break;
		case KeyEvent.KEYCODE_FOCUS:
		case KeyEvent.KEYCODE_CAMERA:
			// Handle these events so they don't launch the Camera app
			return true;
			// Use volume up/down to turn on light
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			cameraManager.setTorch(false);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			cameraManager.setTorch(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		if (resultCode == RESULT_OK) {
			/*if (requestCode == HISTORY_REQUEST_CODE) {
				int itemNumber = intent.getIntExtra(Intents.History.ITEM_NUMBER, -1);
				if (itemNumber >= 0) {
					HistoryItem historyItem = historyManager.buildHistoryItem(itemNumber);

					//TODO change this to perform image comp
					decodeOrStoreSavedBitmap(null, historyItem.getResult());
				}
			}*/
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		if (holder == null) {

		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}


	private void initCamera(SurfaceHolder surfaceHolder)
	{
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {

			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, cameraManager);
			}
			//decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe) {
		} catch (RuntimeException e) {
		}
	}

	public void restartPreviewAfterDelay(long delayMS)
	{
		if (handler != null)
		{
			//handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
		resetStatusView();
	}

	private void resetStatusView() {
		resultView.setVisibility(View.GONE);
		//statusView.setText(R.string.msg_default_status);
		statusView.setVisibility(View.VISIBLE);
		//lastResult = null;
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
