package com.highlanderchef;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

final public class PreviewCallback implements Camera.PreviewCallback
{

	private final CameraConfigurationManager configManager;
	private Handler previewHandler;
	private int previewMessage;

	int height;
	int width;

	boolean halfassmutex;
	boolean firstframe;


	PreviewCallback(CameraConfigurationManager configManager, int height, int width) {
		this.configManager = configManager;
		this.height = height;
		this.width = width;
		halfassmutex = false;
		firstframe = true;
	}

	void setHandler(Handler previewHandler, int previewMessage) {
		this.previewHandler = previewHandler;
		this.previewMessage = previewMessage;
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera)
	{
		System.out.println("PreviewCallback.onPreviewFrame(...)");
		if(firstframe)
		{
			System.out.println("PreviewCallback.onPreviewFrame got firstframe (bailing out)");
			firstframe = false;
			return;
		}
		Point cameraResolution = configManager.getCameraResolution();
		Handler thePreviewHandler = previewHandler;
		if (cameraResolution != null && thePreviewHandler != null)
		{
			Message message = thePreviewHandler.obtainMessage(previewMessage, cameraResolution.x, cameraResolution.y, data);
			message.sendToTarget();
			previewHandler = null;
		}

		if(!halfassmutex )
		{
			halfassmutex = true;
			System.out.println("PreviewCallback.onPreviewFrame set mutex");
			/*int nrOfPixels = data.length / 3; // Three bytes per pixel.
			int pixels[] = new int[nrOfPixels];
			for(int i = 0; i < nrOfPixels; i++) {
				int r = data[3*i];
				int g = data[3*i + 1];
				int b = data[3*i + 2];
				pixels[i] = Color.rgb(r,g,b);
			}*/

			/*int intByteCount = data.length;
			int[] intColors = new int[intByteCount / 3];
			final int intAlpha = 255;
			if ((intByteCount / 3) != (width * height)) {
				throw new ArrayStoreException();
			}
			for (int intIndex = 0; intIndex < intByteCount - 2; intIndex = intIndex + 3) {
				intColors[intIndex / 3] = (intAlpha << 24) | (data[intIndex] << 16) | (data[intIndex + 1] << 8) | data[intIndex + 2];
			}*/
			//Bitmap image_captured = Bitmap.createBitmap(intColors, width, height, Bitmap.Config.ARGB_8888);

			//Bitmap image_captured = BitmapFactory.decodeByteArray(data, 0, data.length);

			//Bitmap image_captured = Bitmap.createBitmap(pixels,  width, height, Bitmap.Config.ARGB_8888);
			//new CompareAnImage().execute(image_captured);



			Camera.Parameters parameters = camera.getParameters();
			int width = parameters.getPreviewSize().width;
			int height = parameters.getPreviewSize().height;

			YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

			byte[] bytes = out.toByteArray();
			Bitmap image_captured = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			new CompareAnImage().execute(image_captured);

		} else {
			System.out.println("PreviewCallback.onPreviewFrame skipping because mutex");
		}
	}

	class CompareAnImage extends AsyncTask<Bitmap, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Bitmap... params) {

			return true;
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

	public void onSuccess()
	{
		halfassmutex = false;
		System.out.println("PreviewCallback.onSuccess unset mutex");
	}

	public void onFailure()
	{
		halfassmutex = false;
		System.out.println("PreviewCallback.onFailure unset mutex");
	}
}



