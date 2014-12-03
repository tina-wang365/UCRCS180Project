package com.highlanderchef;

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
			/*byte[] ba_image2 = (byte[])message.obj;
			Bitmap image_captured = BitmapFactory.decodeByteArray(ba_image2, 0, ba_image2.length);

			Mat image2 = new Mat (image_captured.getWidth(), image_captured.getHeight(), CvType.CV_8UC1);
			Utils.matToBitmap(image2, image_captured);

			Bitmap b_image1 = ic.image1;
			Mat image1 = new Mat (b_image1.getWidth(), b_image1.getHeight(), CvType.CV_8UC1);
			Utils.matToBitmap(image1, b_image1);

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

			//do image comparison
			break;

			// Looper.myLooper().quit(); */

		}
	}
}
