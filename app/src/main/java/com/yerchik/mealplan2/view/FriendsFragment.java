package com.yerchik.mealplan2.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yerchik.mealplan2.MainActivity;
import com.yerchik.mealplan2.R;
import com.yerchik.mealplan2.UserProfileActivity;
import com.yerchik.mealplan2.adapter.ParseProxyObject;
import com.yerchik.mealplan2.adapter.UsersAdapter;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {
    private static final String FRAGMENT_NAME = "Friends";

    public static List<ParseObject> requestsPOList;
    public static List<ParseObject> friendshipsPOList;

    ListView requestsList,friendsList;
    UsersAdapter usersAdapter;
    UsersAdapter requestsAdapter;
    ParseUser currentUser;

    CircularProgressView requestsProgress,friendsProgress;

    SwipeRefreshLayout swipeRefreshFriends;

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
    public void onViewCreated(View view, Bundle savedInstanceState) {

        requestsPOList = new ArrayList<ParseObject>();
        friendshipsPOList = new ArrayList<ParseObject>();
        currentUser = ParseUser.getCurrentUser();

        requestsProgress = (CircularProgressView) getActivity().findViewById(R.id.progressRequests);
        friendsProgress = (CircularProgressView) getActivity().findViewById(R.id.progressFriends);

        requestsList = (ListView) getActivity().findViewById(R.id.requestsToFriendship);
        friendsList = (ListView) getActivity().findViewById(R.id.friendsList);



        getAllFriendships();
        getAllIncomingRequests();

        swipeRefreshFriends = (SwipeRefreshLayout)getActivity().findViewById(R.id.swipeRefreshFriends);
        // set swipe to refresh listener
        swipeRefreshFriends.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllFriendships();
                getAllIncomingRequests();
                swipeRefreshFriends.setRefreshing(false);
            }

        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        }else {
            intent.putExtra("userId", clickedUser.getObjectId());
        }
        startActivity(intent);
    }

    public void getAllIncomingRequests(){
        // GRAB ALL INCOMING FRIENDSHIP REQUESTS /////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        ParseQuery<ParseObject> incomingFriendshipRequests = ParseQuery.getQuery("Friendship");
        incomingFriendshipRequests.whereEqualTo("to", currentUser );
        incomingFriendshipRequests.whereEqualTo("status", 0);
        incomingFriendshipRequests.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> requests, ParseException e) {
                if (e == null) {
                    // hide requestsProgress view
                    requestsProgress.setVisibility(View.GONE);
                    requestsPOList.clear();
                    requestsPOList.addAll(requests);
                    requestsAdapter = new UsersAdapter(requestsPOList, getContext());
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

    public void getAllFriendships(){
        // grab all friendships
        ParseQuery<ParseUser> friendsQuery = ParseUser.getQuery();
        ParseQuery<ParseObject> friendshipsQuery = ParseQuery.getQuery("Friendship");
        friendshipsQuery.whereEqualTo("to",currentUser);
        friendshipsQuery.whereEqualTo("status",1);
        friendshipsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> results, com.parse.ParseException e) {
                if (e == null) {
                    friendshipsPOList.clear();
                    friendshipsPOList.addAll(results);
                    MainActivity.userFriendships = results;
                    MealPlansFragment.getAllOpenMealPlans(getContext(), getActivity()); // launch code for getting all open access mealplans

                    // hide friendsProgress view
                    friendsProgress.setVisibility(View.GONE);

                    // populate list view with friends
                    usersAdapter = new UsersAdapter(friendshipsPOList, getContext());
                    friendsList.setAdapter(usersAdapter);
                    friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            showUserProfile(position, usersAdapter);
                        }
                    });

                } else {
                }
            }
        });
    }


}