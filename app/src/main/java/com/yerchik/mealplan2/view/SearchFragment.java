package com.yerchik.mealplan2.view;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yerchik.mealplan2.R;
import com.yerchik.mealplan2.UserProfileActivity;
import com.yerchik.mealplan2.adapter.UsersAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private static final String FRAGMENT_NAME = "Search";

    ListView searchResults;
    UsersAdapter adapter;

    public static SearchFragment newInstance(int sectionNumber){
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(FRAGMENT_NAME, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public SearchFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View SearchView = inflater.inflate(R.layout.fragment_search, container,false);
        return SearchView;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final SearchView searchFriends = (SearchView)getActivity().findViewById(R.id.friendSearchView);
        searchFriends.setSubmitButtonEnabled(true);// enable submit button

        searchFriends.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchFriendStr) {
                List<ParseQuery<ParseUser>> queries = new ArrayList<>();
                Log.d("yerchik/login", searchFriendStr);
                if (searchFriendStr.contains(" ")) {
                    String[] parts = searchFriendStr.split(" ");
                    String part1 = parts[0];
                    String part2 = parts[1];
                    Log.d("yerchik/login", "name: " + part1 + ", surname: " + part2);

                    ParseQuery<ParseUser> query1 = ParseUser.getQuery();
                    query1.whereStartsWith("name", part1);
                    queries.add(query1);

                    ParseQuery<ParseUser> query2 = ParseUser.getQuery();
                    query2.whereStartsWith("surname", part2);
                    queries.add(query2);

                    ParseQuery<ParseUser> query3 = ParseUser.getQuery();
                    query3.whereStartsWith("surname", part1);
                    queries.add(query3);

                    ParseQuery<ParseUser> query4 = ParseUser.getQuery();
                    query4.whereStartsWith("name", part2);
                    queries.add(query4);
                } else {
                    ParseQuery<ParseUser> query1 = ParseUser.getQuery();
                    query1.whereStartsWith("name", searchFriendStr);
                    queries.add(query1);

                    ParseQuery<ParseUser> query2 = ParseUser.getQuery();
                    query2.whereStartsWith("surname", searchFriendStr);
                    queries.add(query2);
                }

                ParseQuery<ParseUser> mainQuery = ParseUser.getQuery().or(queries);
                mainQuery.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, com.parse.ParseException e) {
                        if (e == null) {
                            Log.d("yerchik", "users found " + list.toString());
                            adapter = new UsersAdapter(list, getContext());
                            searchResults = (ListView) getActivity().findViewById(R.id.searchResults);
                            searchResults.setAdapter(adapter);

                            searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                    ParseObject clickedUser = (ParseUser) adapter.getItem(position);
                                    Log.d("yerchik", "found " + clickedUser.getString("name"));
                                    Intent intent = new Intent(getContext(), UserProfileActivity.class);
                                    intent.putExtra("userId", clickedUser.getObjectId());
                                    startActivity(intent);
                                }
                            });

                        } else {
                            Log.d("yerchik", "nothing found");
                        }
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("yerchik/search", "changed");
                return false;
            }
        });


    }
}