package com.highlanderchef;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;


public class CompareHandler extends Handler{
	private final ImageComp ic;
	private final boolean running = true;

	CompareHandler(ImageComp ic)
	{
		this.ic = ic;
	}

	@Override
	public void handleMessage(Message message)
	{
		if (!running) {
			return;
		}
		switch (message.what) {
		case 1:
			//Mat image1 = org.opencv.android.Utils.matToBitmap(ic.image1);
			byte[] ba_image2 = (byte[])message.obj;
			Bitmap image_captured = BitmapFactory.decodeByteArray(ba_image2, 0, ba_image2.length);


			//do image comparison
			break;

			// Looper.myLooper().quit();

		}
	}
}
