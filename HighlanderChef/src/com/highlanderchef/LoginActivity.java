package com.highlanderchef;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		getIntent();

		setContentView(R.layout.activity_login);
		TextView invalidlogin = (TextView) findViewById(R.id.invalidlogin);
		invalidlogin.setVisibility(View.INVISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
    //changed function name to fit naming conventions. See '<button>' in activity_login.xml
	public void validateSignInPressed(View view)
	{
		EditText editTextUsername = (EditText) findViewById(R.id.input_username);
		String strUsername = editTextUsername.getText().toString();

		EditText editTextPassword = (EditText) findViewById(R.id.input_password);
		String strPassword = editTextPassword.getText().toString();

		// TODO: only process one of these at once
		new LoginTask().execute(strUsername, strPassword);
	}

	public void loginSuccess()
	{
		Intent intent = new Intent(this, MainMenu.class);
		startActivity(intent);
	}

	public void loginFail(String msg)
	{
		TextView invalidlogin = (TextView) findViewById(R.id.invalidlogin);
        invalidlogin.setText(msg); //Added this to make use of msg (the error message).
		invalidlogin.setVisibility(View.VISIBLE);
	}

	private class LoginTask extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			Comm c = new Comm();
			int ret = c.login(params[0],  params[1]);
            // Log: ret
			return (ret == Comm.SUCCESS);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result == true) {
				Log.v("login_log","Login Success");
				loginSuccess();
			} else {
				Log.v("login_fail","Login failed");
				loginFail("Something bad happened");
			}
		}

	}
}
