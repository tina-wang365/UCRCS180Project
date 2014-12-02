package com.highlanderchef;

import java.util.concurrent.CountDownLatch;

import android.os.Handler;
import android.os.Looper;

public class CompareThread extends Thread{
	private final ImageComp activity;
	private Handler handler;
	private final CountDownLatch handlerInitLatch;
	CompareThread(ImageComp ic)
	{
		activity = ic;
		handlerInitLatch = new CountDownLatch(1);

	}

	Handler getHandler() {
		try {
			handlerInitLatch.await();
		} catch (InterruptedException ie) {
			// continue?
		}
		return handler;
	}

	@Override
	public void run() {
		Looper.prepare();
		handler = new CompareHandler(activity);
		handlerInitLatch.countDown();
		Looper.loop();
	}
}
