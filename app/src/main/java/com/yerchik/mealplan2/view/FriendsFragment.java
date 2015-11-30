package com.yerchik.mealplan2.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yerchik.mealplan2.R;
import com.yerchik.mealplan2.UserProfileActivity;
import com.yerchik.mealplan2.adapter.UsersAdapter;

import java.util.List;

public class FriendsFragment extends Fragment {
    private static final String FRAGMENT_NAME = "Friends";

    ListView requestsList,friendsList;
    UsersAdapter usersAdapter;
    UsersAdapter requestsAdapter;
    ParseUser currentUser;

    public static FriendsFragment newInstance(int sectionNumber){
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putInt(FRAGMENT_NAME, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public FriendsFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("yerchik", "friends fragment is created");
        // start spinner to show that search is going on
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        ProgressBar pbar = new ProgressBar(getContext());
        builder.setView(pbar);
        final AlertDialog dialog = builder.create();
        //dialog.show();
        currentUser = ParseUser.getCurrentUser();


        ParseQuery<ParseUser> friendsQuery = ParseUser.getQuery();
        ParseQuery<ParseObject> friendshipsQuery = ParseQuery.getQuery("Friendship");
        friendshipsQuery.whereEqualTo("to",currentUser);
        friendshipsQuery.whereEqualTo("status",1);
        friendshipsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> results, com.parse.ParseException e) {
                if (e==null) {
                    // success
                    // dialog.dismiss();
                    // populate list view with friends
                    usersAdapter = new UsersAdapter(results,getContext());
                    Log.d("yerchik/list", results.size()+"");
                    friendsList = (ListView)getActivity().findViewById(R.id.friendsList);
                    friendsList.setAdapter(usersAdapter);
                    friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Log.d("yerchik", "pos: "+position);
                            showUserProfile(position, usersAdapter);
                        }
                    });

                } else {
                    dialog.dismiss();
                }
            }
        });


        // GRAB ALL INCOMING FRIENDSHIP REQUESTS /////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        ParseQuery<ParseObject> incomingFriendshipRequests = ParseQuery.getQuery("Friendship");
        incomingFriendshipRequests.whereEqualTo("to", currentUser );
        incomingFriendshipRequests.whereEqualTo("status", 0);
        incomingFriendshipRequests.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> requests, ParseException e) {
                if (e==null) {
                    requestsAdapter = new UsersAdapter(requests, getContext());
                    requestsList = (ListView)getActivity().findViewById(R.id.requestsToFriendship);
                    requestsList.setAdapter(requestsAdapter);
                    requestsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            showUserProfile(position, requestsAdapter);
                        }
                    });
                } else {

                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("yerchik", "on friends view create");
        View friendsView = inflater.inflate(R.layout.fragment_friends, container, false);
        return friendsView;
    }

    // starts UserPofileActivity and passes user information to it
    // position: position of list item in list view (passed in onItemClickListenre event
    public void showUserProfile(int position, Adapter adapter){
        ParseObject clickedUser = (ParseObject)adapter.getItem(position);
        //ParseProxyObject ppo = new ParseProxyObject(clickedUser);

        Intent intent = new Intent(getContext(), UserProfileActivity.class);
        //intent.putExtra("clickedUser", ppo);

        // if our object is Friendship object
        // we need to get User object from it
        if (clickedUser.has("from")){
            intent.putExtra("userId", clickedUser.getParseObject("from").getObjectId());
            Log.d("yerchik", "has:" + clickedUser.getParseObject("from").getObjectId());
        }else {
            intent.putExtra("userId", clickedUser.getObjectId());
        }
        startActivity(intent);
    }

}