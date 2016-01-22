package com.yerchik.mealplan2;

import java.util.List;
import java.util.Locale;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yerchik.mealplan2.view.FriendsFragment;
import com.yerchik.mealplan2.view.MealPlansFragment;
import com.yerchik.mealplan2.view.SearchFragment;


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
    String currentUserName;
    ParseUser currentUser;
    public static List<ParseObject> userFriendships;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("yerchik", "on create");


        currentUser = ParseUser.getCurrentUser();

        // check if user is logged in and activated his email
        // currentUser.getBoolean("emailVerified") is not reliable, since it takes data from local session
        if (currentUser!=null) {
            ParseQuery<ParseUser> currUser = ParseUser.getQuery();
            currUser.whereEqualTo("objectId", currentUser.getObjectId());
            currUser.setLimit(1);
            currUser.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> list, ParseException e) {
                    if (e == null) {
                        if (!list.isEmpty()) {
                            if (!list.get(0).getBoolean("emailVerified")) {
                                Log.d("yerchik/email", "not verified");
                                // redirect to activity that tells that user has to activate his email
                                Intent intent = new Intent(getApplicationContext(), ActivateEmailActivity.class);
                                intent.putExtra("email", currentUser.getString("email"));
                                startActivity(intent);
                                finish();
                            }
                        }
                    } else {

                    }
                }
            });
        } else {
            // show the signup or login screen
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }


        try {
            currentUserName = currentUser.getString("name");
        }catch(NullPointerException e){

        }

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        if (currentUserName!=null){
            setTitle(currentUserName.substring(0,1).toUpperCase()+currentUserName.substring(1));
        }



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

    public void takeMealPlan(View v) {
        MealPlansFragment.takeMealPlan(v);
    }

    public void usedMealPlan(View v){
        MealPlansFragment.usedMealPlan(v);
    }


}
