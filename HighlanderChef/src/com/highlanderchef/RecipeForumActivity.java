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

public class RecipeForumActivity extends ActionBarActivity {
    
    int rating = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_forum);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recipe_forum, menu);
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

    public void addCommentPressed(View view)
    {
        EditText editTextUserComment = (EditText) findViewById(R.id.userCommentText);
        String strUserComment = editTextUserComment.getText().toString();
    }
    public void addRatingPressed(View view)
    {
        RatingBar bar = (RatingBar) view;
        rating = (int) bar.getRating();
    }
    //confirming whether the user tapped "Comment button" to add a comment
    //TODO: public EditText validateCommentPressed(View view)
   // public EditText validateCommentPressed(View view) {
        
    //}
    //TODO: public createCommentText
    //TODO: public createRating
    //TODO: public createWholeComment

}
