package com.yerchik.mealplan2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.List;


public class UserProfileActivity extends ActionBarActivity {

    private ProgressDialog dialogHome;

    TextView profileNameView,emailView,friendsCountView,sharedCountView,takenCountView;
    TextView requestSentView;
    String userID,profileNameStr,emailStr,friendsCountStr,sharedCountStr,takenCountStr;
    Button requestFriendshipBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // grab views
        profileNameView = (TextView)findViewById(R.id.profileName);
        emailView = (TextView)findViewById(R.id.profileEmail);
        friendsCountView = (TextView)findViewById(R.id.friendsCount);
        sharedCountView = (TextView)findViewById(R.id.sharedCount);
        takenCountView = (TextView)findViewById(R.id.takenCount);
        requestFriendshipBtn = (Button)findViewById(R.id.requestFriendshipBtn);
        requestSentView = (TextView)findViewById(R.id.requestSent);

        // take intent and extras
        Intent intent = getIntent();

        // take name and surname and capitalize them
        String name = intent.getStringExtra("name");
        String surname = intent.getStringExtra("surname");
        String Name = name.substring(0,1).toUpperCase()+name.substring(1);
        String Surname = surname.substring(0,1).toUpperCase()+surname.substring(1);

        profileNameStr = Name+" "+Surname;
        userID = intent.getStringExtra("userID");
        emailStr = intent.getStringExtra("email");
        friendsCountStr = intent.getIntExtra("friend_count",0) + " friends";
        sharedCountStr = intent.getIntExtra("shared_count", 0)+ " shared meal plans";
        takenCountStr = intent.getIntExtra("taken_count", 0)+ " taken taken meal plans";

        // set view text
        profileNameView.setText(profileNameStr);
        emailView.setText(emailStr);
        friendsCountView.setText(friendsCountStr);
        sharedCountView.setText(sharedCountStr);
        takenCountView.setText(takenCountStr);

        // check if current user is in friendship with this user
        ParseUser currentUser = ParseUser.getCurrentUser();
        String currentUserId = currentUser.getObjectId();


    }

    public void requestFriendship(View v) {
        Log.d("yerchik", "add to friends btn is clicked: "+userID);
        ParseQuery<ParseUser> userProfileQuery = ParseUser.getQuery();

        userProfileQuery.whereEqualTo("objectId", userID);
            userProfileQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> requestedUser, ParseException e) {
                    if (e==null){
                        ParseObject friendship = new ParseObject("Friendship");
                        friendship.put("from", ParseUser.getCurrentUser().getObjectId());
                        friendship.put("to", userID);
                        friendship.put("status", 0);
                        friendship.saveInBackground();
                        requestFriendshipBtn.setVisibility(View.INVISIBLE);
                        requestSentView.setVisibility(View.VISIBLE);
                    }else {
                        Log.d("yerchik", "user not found");
                    }
                }
            });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            //show dialog
            dialogHome = ProgressDialog.show(UserProfileActivity.this, "", "Please wait", true);
            // logout the user
            ParseUser.logOutInBackground();
            dialogHome.dismiss();
            finish();
            Intent intent = new Intent(UserProfileActivity.this,LoginActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
