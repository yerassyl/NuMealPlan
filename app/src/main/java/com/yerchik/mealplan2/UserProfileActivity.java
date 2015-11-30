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
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yerchik.mealplan2.adapter.ParseProxyObject;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class UserProfileActivity extends ActionBarActivity {

    private ProgressDialog dialogHome;

    TextView profileNameView,emailView,friendsCountView,sharedCountView,takenCountView;
    TextView requestSentView;
    String clickedUserId,profileNameStr,emailStr,friendsCountStr,sharedCountStr,takenCountStr;
    Button requestFriendshipBtn,acceptFriendshipBtn;

    String Name,Surname;

    //ParseProxyObject ppo;
    ParseUser clickedUser, currentUser;
    boolean isInFriendship;

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
        acceptFriendshipBtn = (Button)findViewById(R.id.acceptFriendshipBtn);

        // get current user
        currentUser = ParseUser.getCurrentUser();

        // take intent and extras
        Intent intent = getIntent();
        //ppo = (ParseProxyObject)intent.getSerializableExtra("clickedUser");

        // get user profile
        clickedUserId = intent.getStringExtra("userId");
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", clickedUserId);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    // The query was successful.
                    // take name and surname and capitalize them
                    clickedUser = objects.get(0);

                    String name = clickedUser.getString("name");
                    String surname = clickedUser.getString("surname");
                    Name = name.substring(0, 1).toUpperCase() + name.substring(1);
                    Surname = surname.substring(0, 1).toUpperCase() + surname.substring(1);

                    profileNameStr = Name + " " + Surname;
                    emailStr = clickedUser.getString("email");
                    //friendsCountStr = clickedUser.getInt("friend_count") + " friends";
                    sharedCountStr = clickedUser.getInt("shared_count") + " shared meal plans";
                    takenCountStr = clickedUser.getInt("taken_count") + " taken meal plans";

                    // set view text
                    if (profileNameStr != null) {
                        profileNameView.setText(profileNameStr);
                    }
                    if (emailStr != null) {
                        emailView.setText(emailStr);
                    }
//                    if (friendsCountStr != null) {
//                        friendsCountView.setText(friendsCountStr);
//                    }
                    if (sharedCountStr != null) {
                        sharedCountView.setText(sharedCountStr);
                    }
                    if (takenCountStr != null) {
                        takenCountView.setText(takenCountStr);
                    }
                    checkRelationship();
                } else {
                    // Something went wrong.
                }
            }
        });


    }// end -- onCreate

    public void requestFriendship(View v) {
        if (clickedUser!=null) {
            ParseObject friendship = new ParseObject("Friendship");
            friendship.put("from", ParseUser.getCurrentUser());
            friendship.put("to", clickedUser);
            friendship.put("status", 0);
            friendship.saveInBackground();
            requestFriendshipBtn.setVisibility(View.GONE);
            requestSentView.setVisibility(View.VISIBLE);
        }
    }

    // Checks the relationships of current user with clicked user
    // users can be in friendship
    // or current user has sent request
    // or current user has recieved request
    public void checkRelationship(){
        isInFriendship = false;
        // first check if current user has sent friendship request to clicked user
        // if there is instance (currentUser,clickedUser,0)
        // then there is no instance of (clickedUser,currentUser,0)
        // and vice versa
        ParseQuery<ParseObject> fromTo = ParseQuery.getQuery("Friendship");
        fromTo.whereEqualTo("from", currentUser);
        fromTo.whereEqualTo("to", clickedUser);
        fromTo.whereEqualTo("status", 0);

        // or clicked user has sent the request to current user
        ParseQuery<ParseObject> toFrom = ParseQuery.getQuery("Friendship");
        toFrom.whereEqualTo("from", clickedUser);
        toFrom.whereEqualTo("to", currentUser);
        toFrom.whereEqualTo("status", 0);

        // or they are already are in friendship
        // if two users are in friendship implies that there are two instances of their relation:
        // currentUser,clickedUser, 1
        // clickedUser,currentUser, 1
        // so it doesn't matter what query to do here
        ParseQuery<ParseObject> friendship = ParseQuery.getQuery("Friendship");
        friendship.whereEqualTo("from", currentUser);
        friendship.whereEqualTo("to", clickedUser);
        friendship.whereEqualTo("status", 1);

        // perform all three queries
        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(fromTo);
        queries.add(toFrom);
        queries.add(friendship);
        ParseQuery<ParseObject> friendshipsQuery = ParseQuery.or(queries);
        friendshipsQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject friendshipRelation, ParseException e) {
                if (e == null) {
                    Log.d("yerchik/request", "found something: " + friendshipRelation);

                    if ((int)friendshipRelation.getNumber("status")==1) {
                        // request IS approved and current user is in friendship with clicked user
                        requestSentView.setVisibility(View.VISIBLE);
                        requestSentView.setText(Name+" is your friend");
                        isInFriendship = true;
                    }else if (friendshipRelation.getParseObject("from").getObjectId().equals(currentUser.getObjectId())){
                        // if request is sent by current user
                        // notify that request is sent
                        requestSentView.setVisibility(View.VISIBLE);
                        Log.d("yerchik/request", "i sent request");
                    }else {
                        // else request is sent by clicked user
                        // show "accept request" button
                        acceptFriendshipBtn.setVisibility(View.VISIBLE);
                        // when acceptRequest btn is clicked
                        acceptFriendshipBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // update (clickedUser,currentUser,0) to (clickedUser,currentUser,1)
                                friendshipRelation.put("status",1);
                                friendshipRelation.saveInBackground();
                                // create (currentUser,clickedUser,1) object
                                ParseObject backwardRelation = new ParseObject("Friendship");
                                backwardRelation.put("from",currentUser);
                                backwardRelation.put("to", clickedUser);
                                backwardRelation.put("status",1);
                                backwardRelation.saveInBackground();
                                acceptFriendshipBtn.setVisibility(View.GONE);
                                requestSentView.setVisibility(View.VISIBLE);
                                requestSentView.setText(Name+" is your friend");
                            }
                        });
                        Log.d("yerchik/request", "clicked user sent the request");
                    }
                } else {
                    // request has NOT been sent yet
                    Log.d("yerchik/request", "no request sent");
                    requestFriendshipBtn.setVisibility(View.VISIBLE);
                }

            } // end -- done
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
