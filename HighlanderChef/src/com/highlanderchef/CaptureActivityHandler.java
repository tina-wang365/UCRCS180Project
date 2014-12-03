package com.highlanderchef;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class CaptureActivityHandler extends Handler
{
	private final ImageComp activity;

	private State state;
	private final CameraManager cameraManager;
	private final CompareThread comparethread;

	private enum State {
		PREVIEW,
		SUCCESS,
		DONE
	}

	CaptureActivityHandler(ImageComp activity, CameraManager cameraManager)
	{
		this.activity = activity;
		comparethread = new CompareThread(activity);
		comparethread.start();
		state = State.SUCCESS;

		// Start ourselves capturing previews and decoding.
		this.cameraManager = cameraManager;
		cameraManager.startPreview();
		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) {
		//TODO figure out what this does
		Bundle bundle = message.getData();

	}

	public void quitSynchronously() {
		state = State.DONE;
	}

	private void restartPreviewAndDecode() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;
			//TODO get this line working
			System.out.println("CAH restartPreviewAndDecode()");
			cameraManager.requestPreviewFrame(comparethread.getHandler(), 1);
		}
	}
}
