package com.highlanderchef;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview implements SurfaceHolder.Callback, Camera.PreviewCallback
{
	static {
		if (!OpenCVLoader.initDebug()) {
			// Handle initialization error
		}
	}

	private SurfaceHolder mSurfHolder;
	private Camera mCamera;
	SurfaceView mSurfaceView;
	private PreviewCallback previewcallback;

	private final int PreviewSizeWidth;
	private final int PreviewSizeHeight;
	private boolean TakePicture;

	private final Bitmap bm_image1;


	public CameraPreview(int PreviewlayoutWidth, int PreviewlayoutHeight, Bitmap image1)
	{
		PreviewSizeWidth = PreviewlayoutWidth;
		PreviewSizeHeight = PreviewlayoutHeight;
		bm_image1 = image1;
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
	{
		Parameters parameters;
		mSurfHolder = arg0;

		parameters = mCamera.getParameters();
		// Set the camera preview size
		parameters.setPreviewSize(PreviewSizeWidth, PreviewSizeHeight);
		// Set the take picture size, you can set the large size of the camera supported.
		parameters.setPictureSize(PreviewSizeWidth, PreviewSizeHeight);

		// Turn on the camera flash.
		String NowFlashMode = parameters.getFlashMode();
		if ( NowFlashMode != null )
			parameters.setFlashMode(Parameters.FLASH_MODE_ON);
		// Set the auto-focus.
		String NowFocusMode = parameters.getFocusMode ();
		if ( NowFocusMode != null )
			parameters.setFocusMode("auto");

		mCamera.setParameters(parameters);

		mCamera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0)
	{
		mCamera = Camera.open();
		try
		{
			// If did not set the SurfaceHolder, the preview area will be black.
			mCamera.setPreviewDisplay(arg0);
			mCamera.setPreviewCallback(this);
		}
		catch (IOException e)
		{
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0)
	{
		mCamera.setPreviewCallback(null);
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}


	// Set auto-focus interface
	public void CameraStartAutoFocus()
	{
		TakePicture = false;
		mCamera.autoFocus(myAutoFocusCallback);
	}

	AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback()
	{
		@Override
		public void onAutoFocus(boolean arg0, Camera NowCamera)
		{
			if ( TakePicture )
			{
				NowCamera.stopPreview();
				NowCamera.takePicture(shutterCallback, rawPictureCallback, jpegPictureCallback);
				TakePicture = false;
			}
		}
	};

	ShutterCallback shutterCallback = new ShutterCallback()
	{
		@Override
		public void onShutter()
		{
			// Just do nothing.
		}
	};

	PictureCallback rawPictureCallback = new PictureCallback()
	{
		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1)
		{
			// Just do nothing.
		}
	};

	PictureCallback jpegPictureCallback = new PictureCallback()
	{
		@Override
		public void onPictureTaken(byte[] data, Camera arg1)
		{
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,data.length);
			//FileOutputStream out = new FileOutputStream(NowPictureFileName);
			//bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
		}
	};


	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		Camera.Parameters parameters = camera.getParameters();
		int width = parameters.getPreviewSize().width;
		int height = parameters.getPreviewSize().height;

		YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

		byte[] bytes = out.toByteArray();
		Bitmap image_captured = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

		Mat image1 = new Mat (bm_image1.getWidth(), bm_image1.getHeight(), CvType.CV_8UC1);
		Utils.matToBitmap(image1, bm_image1);

		Mat image2 = new Mat (image_captured.getWidth(), image_captured.getHeight(), CvType.CV_8UC1);
		Utils.matToBitmap(image2, image_captured);

		MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
		MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
		Mat descr1 = new Mat();
		Mat descr2 = new Mat();

		FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
		DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);

		detector.detect(image1, keypoints1);
		detector.detect(image2, keypoints2);

		extractor.compute(image1, keypoints1, descr1);
		extractor.compute(image2, keypoints2, descr2);

		//definition of descriptor matcher
		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

		//match points of two images
		MatOfDMatch matches = new MatOfDMatch();
		matcher.match(descr1, descr2, matches);

		int total_number_matches_count = 0;
		for(int i = 0; i < matches.rows(); ++i)
		{
			for(int j = 0; j < matches.cols(); ++j )
			{
				++total_number_matches_count;
			}
		}
	}

}
