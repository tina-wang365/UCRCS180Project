package com.highlanderchef;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class QuestionBoardActivity extends ActionBarActivity {

	int recipeID = 0;
	ID_Maker MakerInstance = ID_Maker.getInstance();
	RelativeLayout rflayout = (RelativeLayout) findViewById(R.id.questionBoardLayout);
	Question newQuestion = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("AFTER onCreate(savedInstanceState)");
		setContentView(R.layout.activity_question_board);
		System.out.println("AFTER setContentView");

		Intent intent = getIntent();
		if(intent != null) {
			System.out.println("Inten is NOT null");
			recipeID = intent.getIntExtra("recipeID", 0);

		}
		else {
			System.out.println("Intent is null!");
		}


		//addQuestionToView();
		//new postQuestionTask()
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.question_board, menu);
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

	public void addQuestionToView() {
		EditText etQuestionToPost = new EditText(this);
		etQuestionToPost.setId(MakerInstance.useCurrID());
		RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		etQuestionToPost.setLayoutParams(rlParams);
		rflayout.addView(etQuestionToPost);

	}


	void postQuestionSuccess(Question question) {
		TextView tv_question = new TextView(this);
		tv_question.setId(MakerInstance.useCurrID());
		tv_question.setText(question.text);
		rflayout.addView(tv_question);

	}
	/*
	private class postQuestionTask extends AsyncTask<Question, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Question... params) {
			Comm c = new Comm();
			int ret = c.postQuestion(recipeID, params[0].text);
			return (ret != Comm.SUCCESS);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(result == true) {
				Log.v("postQuestionSuccess","Successfully posted a question!");
			}
			else {
				Log.v("postQuestionFailure", "Failed to post a question!");
			}
		}
	}
	 */
}
