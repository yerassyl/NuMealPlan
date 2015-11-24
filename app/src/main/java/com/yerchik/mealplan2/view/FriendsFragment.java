package com.yerchik.mealplan2.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yerchik.mealplan2.R;
import com.yerchik.mealplan2.UserProfileActivity;
import com.yerchik.mealplan2.adapter.UsersAdapter;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {
    private static final String FRAGMENT_NAME = "Friends";
    ListView friendsList;
    UsersAdapter adapter;

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

        // grab all user friends
        // status 0 - not confirmed Friendship
        // status 1 - confirmed Friendship
            /*
                // We need to perform this Join query
                SELECT Friendship.status, User.username from Friendship, User
                WHERE
                CASE

                WHEN Friendship.user_id = current_user.objectId
                THEN Friendship.friend_id = User.user_id
                WHEN Friendship.friend_id= current_user.objectId
                THEN Friendship.user_id= User.user_id
                END

                AND
                Friendship.status='1';
            */
        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery<ParseObject> userId = ParseQuery.getQuery("Friendship");
        userId.whereEqualTo("user_id", currentUser);

        ParseQuery<ParseObject> friendId = ParseQuery.getQuery("Friendship");
        friendId.whereEqualTo("friend_id", currentUser);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(userId);
        queries.add(friendId);

        ParseQuery<ParseObject> friendshipsQuery = ParseQuery.or(queries);
        ParseQuery<ParseUser> friendsQuery = ParseUser.getQuery();
        //friendsQuery.whereMatchesKeyInQuery("username","" friendshipsQuery);
        friendsQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> results, com.parse.ParseException e) {
                if (e==null){
                    // success
                    Log.d("yerchik", "friends found");
                    //dialog.dismiss();
                    // populate list view with friends
                    adapter = new UsersAdapter(results,getContext());
                    friendsList = (ListView)getActivity().findViewById(R.id.friendsList);
                    friendsList.setAdapter(adapter);
                    friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            ParseObject clickedUser = (ParseUser)adapter.getItem(position);
                                Log.d("yerchik", "found "+clickedUser.getString("name"));
                                Intent intent = new Intent(getContext(), UserProfileActivity.class);
                                intent.putExtra("userID", clickedUser.getObjectId());
                                intent.putExtra("name", clickedUser.getString("name"));
                                intent.putExtra("surname", clickedUser.getString("surname"));
                                intent.putExtra("email", clickedUser.getString("email"));
                                intent.putExtra("friend_count",clickedUser.getNumber("friend_count"));
                                intent.putExtra("shared_count",clickedUser.getNumber("shared_count"));
                                intent.putExtra("taken_count", clickedUser.getNumber("taken_count"));
                                Log.d("yerchik", "friend_count: "+clickedUser.getNumber("friend_count"));
                                startActivity(intent);
                        }
                    });
                }else {
                    dialog.dismiss();
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

}