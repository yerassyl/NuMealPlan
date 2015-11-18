package com.yerchik.mealplan2;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yerchik.mealplan2.adapter.UsersAdapter;

import org.xml.sax.helpers.ParserAdapter;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
    protected ProgressDialog dialogHome;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    EditText searchFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check if user is logged in
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // stay at HomePage
        } else {
            // show the signup or login screen
            finish();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        mViewPager.setOffscreenPageLimit(3);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.action_logout:
                //show dialog
                dialogHome = ProgressDialog.show(MainActivity.this,"","Please wait",true);
                // logout the user
                ParseUser.logOutInBackground();
                dialogHome.dismiss();
                finish();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 3;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position){
                case 0:
                    return FriendsFragment.newInstance(position);
                case 1:
                    return MealPlansFragment.newInstance(position);
                case 2:
                    return SearchFragment.newInstance(position);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return NUM_ITEMS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.friends).toUpperCase(l);
                case 1:
                    return getString(R.string.meal_plans).toUpperCase(l);
                case 2:
                    return getString(R.string.search).toUpperCase(l);
            }
            return null;
        }
    }

    public static class FriendsFragment extends Fragment {
        private static final String FRAGMENT_NAME = "Friends";

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
                        UsersAdapter adapter = new UsersAdapter(results,getContext());
                        ListView friendsList = (ListView)getActivity().findViewById(R.id.friendsList);
                        friendsList.setAdapter(adapter);
                    }else {
                        dialog.dismiss();
                    }
                }
            });
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.d("yerchik", "friends view is created");
            View friendsView = inflater.inflate(R.layout.fragment_friends, container, false);
            return friendsView;
        }
    }

    public static class MealPlansFragment extends Fragment {
        private static final String FRAGMENT_NAME = "MealPlans";

        public static MealPlansFragment newInstance(int sectionNumber){
            MealPlansFragment fragment = new MealPlansFragment();
            Bundle args = new Bundle();
            args.putInt(FRAGMENT_NAME, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }
        public MealPlansFragment(){
        }

        @Override
        public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
            View MealPlansView = inflater.inflate(R.layout.fragment_meal_plans, container,false);
            return MealPlansView;
        }

    }


    public static class SearchFragment extends Fragment {
        private static final String FRAGMENT_NAME = "Search";

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
    }

    // when searchFriendsBtn is clicked
    public void searchFriends(View v){
        searchFriend = (EditText)findViewById(R.id.searchFriendEditText);
        String searchFriendStr = searchFriend.getText().toString().trim();

        List<ParseQuery<ParseUser>> queries = new ArrayList<>();
        Log.d("yerchik/login", searchFriendStr);
        if (searchFriendStr.contains(" ")){
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
            query3.whereStartsWith("surname",part1);
            queries.add(query3);

            ParseQuery<ParseUser> query4 = ParseUser.getQuery();
            query4.whereStartsWith("name", part2);
            queries.add(query4);
        }else {
            ParseQuery<ParseUser> query1 = ParseUser.getQuery();
            query1.whereStartsWith("name",searchFriendStr);
            queries.add(query1);

            ParseQuery<ParseUser> query2 = ParseUser.getQuery();
            query2.whereStartsWith("surname",searchFriendStr);
            queries.add(query2);
        }

        ParseQuery<ParseUser> mainQuery = ParseUser.getQuery().or(queries);
        mainQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, com.parse.ParseException e) {
                if (e == null) {
                    Log.d("yerchik", "users found " + list.toString());
                    UsersAdapter adapter = new UsersAdapter(list, MainActivity.this);
                    ListView searchResults = (ListView) findViewById(R.id.searchResults);
                    searchResults.setAdapter(adapter);
                } else {
                    Log.d("yerchik", "nothing found");
                }
            }
        });

    }

}
