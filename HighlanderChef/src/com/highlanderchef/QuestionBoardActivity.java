package com.highlanderchef;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionBoardActivity extends ActionBarActivity {

	private final int LENGTH_SHORT = 2000;
	int recipeID = 0;

	ID_Maker MakerInstance = ID_Maker.getInstance();
	RelativeLayout rflayout;
	Question newQuestion = null;
	EditText etQuestionToPost = null;
	Button btnAddQuestion = null;
	View lastView = null;
	TextView tv_questions;

	Recipe currentRecipe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_board);
		rflayout = (RelativeLayout) findViewById(R.id.questionBoardLayout);
		etQuestionToPost = new EditText(this);
		btnAddQuestion = new Button(this);
		Intent intent = getIntent();
		//new getRecipeTask().execute(recipeID);
		if(intent != null) {
			System.out.println("Intent is NOT null");
			//recipeID = intent.getIntExtra("recipeID", 0);
		}
		else {
			System.out.println("Intent is null!");
		}

		etQuestionToPost.setId(MakerInstance.useCurrID());
		RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlParams.addRule(RelativeLayout.BELOW, R.id.textView2);
		etQuestionToPost.setLayoutParams(rlParams);
		rflayout.addView(etQuestionToPost);


		btnAddQuestion.setId(MakerInstance.useCurrID());
		btnAddQuestion.setText("Ask a Question");
		rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlParams.addRule(RelativeLayout.BELOW, etQuestionToPost.getId());
		btnAddQuestion.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v)
			{
				tv_questions.setText(tv_questions.getText() + "\n\n" + etQuestionToPost.getText());
				Question q = new Question(Comm.staticGetUserID(), Comm.getEmail(), etQuestionToPost.getText().toString());
				new postQuestionTask().execute(q);

			}
		});
		btnAddQuestion.setLayoutParams(rlParams);
		rflayout.addView(btnAddQuestion);

		lastView = btnAddQuestion;
		tv_questions = new TextView(this);
		tv_questions.setText("");
		rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlParams.addRule(RelativeLayout.BELOW, lastView.getId());
		tv_questions.setLayoutParams(rlParams);
		rflayout.addView(tv_questions);

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

	public void addAbilityToPostQuestion(EditText etQuestionToPost, Button btnAddQuestion, View lastView) {


	}

	public void displayLiveQuestion( Question newlyAddedQuestion, View lastView) {
		TextView tvQuestion = new TextView(this);
		tvQuestion.setId(MakerInstance.useCurrID());
		tvQuestion.setText(newlyAddedQuestion.text);
		RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlParams.addRule(RelativeLayout.BELOW, lastView.getId());
		rflayout.addView(tvQuestion);

	}
	//TODO: Test display of questions first
	//TODO: Create a function that also displays replies

	void displayListOfQuestions(ArrayList<Question> questions, View lastView) {
		for(int i = 0; i < questions.size(); ++i) {
			TextView question = new TextView(this);
			question.setId(MakerInstance.useCurrID());
			lastView = question;
			question.setText(questions.get(i).text);
			RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			rlParams.addRule(RelativeLayout.BELOW, lastView.getId());
			rlParams.addRule(RelativeLayout.ALIGN_LEFT, lastView.getId());
			question.setLayoutParams(rlParams);
			rflayout.addView(question);
		}
	}



	void postQuestionSuccess(Question question) {
		TextView tv_question = new TextView(this);
		tv_question.setId(MakerInstance.useCurrID());
		tv_question.setText(question.text);
		rflayout.addView(tv_question);

		Toast toastSuccessfullyPostQuestion = Toast.makeText(getApplicationContext(), "You have added a Question!", LENGTH_SHORT);
		toastSuccessfullyPostQuestion.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
		toastSuccessfullyPostQuestion.show();
	}

	void postQuestionFailure(Question question) {
		Utility.displayErrorToasts(getApplicationContext(), -3, LENGTH_SHORT);
	}

	private class postQuestionTask extends AsyncTask<Question, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Question... params) {
			Comm c = new Comm();
			newQuestion = params[0];
			int ret = c.postQuestion(recipeID, params[0].text);
			return (ret != Comm.SUCCESS);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(result == true) {
				Log.v("postQuestionSuccess","Successfully posted a question!");
				postQuestionSuccess(newQuestion);
			}
			else {
				Log.v("postQuestionFailure", "Failed to post a question!");
			}
		}
	}
	private void displayRecipeSuccess()
	{
		String s = "";
		for(int i = 0; i < currentRecipe.questions.size();++i)
		{
			s += currentRecipe.questions.get(i) + "\n\n";
		}
		tv_questions.setText(s);
	}

	private class getRecipeTask extends AsyncTask<Integer, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Integer... params) {
			Comm c = new Comm();
			Recipe ret = c.getRecipe(params[0]);
			currentRecipe = ret;
			return (ret != null);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result == true) {
				Log.v("getRecipeSuccess","Success: Recipe Received");
				displayRecipeSuccess();
			} else {
				Log.v("getRecipeFailure","Failure: Did not receive recipe");
			}
		}
	}

}
