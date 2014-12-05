package com.highlanderchef;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.SurfaceView;
import android.view.WindowManager;

public class ImageComp extends ActionBarActivity implements CvCameraViewListener2
{
	private CameraBridgeViewBase mOpenCvCameraView;
	private Bitmap bm_image1;
	boolean firstframe = true;
	boolean halfassmutex =false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_image_comp);
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);

		Intent intent = getIntent();
		bm_image1 = (Bitmap)intent.getParcelableExtra("image");
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
	}

	@Override
	public void onCameraViewStopped() {
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		if(firstframe || halfassmutex)
		{
			firstframe = false;
			return inputFrame.rgba();
		}

		Mat image2 = inputFrame.rgba();

		halfassmutex = true;
		new CompareImageTask().execute(image2);


		return inputFrame.rgba();

	}

	private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
			{
				mOpenCvCameraView.enableView();
			} break;
			default:
			{
				super.onManagerConnected(status);
			} break;
			}
		}
	};

	@Override
	public void onResume()
	{
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
	}

	public void CompareSuccess()
	{
		halfassmutex = false;
	}

	public void CompareFailure()
	{
		halfassmutex = false;
	}

	private class CompareImageTask extends AsyncTask<Mat, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Mat... params)
		{
			Mat image1 = new Mat (bm_image1.getWidth(), bm_image1.getHeight(), CvType.CV_8UC1);
			Utils.bitmapToMat(bm_image1, image1);
			Mat image2 = params[0];

			MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
			MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
			Mat descr1 = new Mat();
			Mat descr2 = new Mat();

			FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
			DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);

			detector.detect(image1, keypoints1);
			detector.detect(image2, keypoints2);

			int image1_matches_count = 0;
			for(int i = 0; i < keypoints1.rows(); ++i)
			{
				for(int j = 0; j < keypoints1.cols(); ++j )
				{
					++image1_matches_count;
				}
			}
			System.out.println("Number of keypoint for image1 : " + image1_matches_count);

			int image2_matches_count = 0;
			for(int i = 0; i < keypoints2.rows(); ++i)
			{
				for(int j = 0; j < keypoints2.cols(); ++j )
				{
					++image2_matches_count;
				}
			}

			System.out.println("Number of keypoint for image2 : " + image2_matches_count);

			if(image2_matches_count == 0 || image1_matches_count == 0)
			{
				return false;
			}
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
			System.out.println("Total Number : " + total_number_matches_count);
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result == true) {
				CompareSuccess();
			} else {
				CompareFailure();
			}
		}
	}


}
