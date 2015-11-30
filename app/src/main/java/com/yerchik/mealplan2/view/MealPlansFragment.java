package com.yerchik.mealplan2.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yerchik.mealplan2.R;

import java.util.List;

public class MealPlansFragment extends Fragment {
    private static final String FRAGMENT_NAME = "MealPlans";

    Button shareLunch,shareDinner,takeBackLunch,takeBackDinner;
    ParseUser currentUser;

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shareLunch = (Button)getActivity().findViewById(R.id.shareLunchBtn);
        shareDinner = (Button)getActivity().findViewById(R.id.shareDinerBtn);
        takeBackLunch = (Button)getActivity().findViewById(R.id.takeBackLunchBtn);
        takeBackDinner = (Button)getActivity().findViewById(R.id.takeBackDinnerBtn);

        currentUser = ParseUser.getCurrentUser();

        // check if user shared any of his meal plans for the day (lucnh/dinner)
        ParseQuery<ParseObject> checkIfShared = ParseQuery.getQuery("MealPlans");
        checkIfShared.whereEqualTo("owner", currentUser);
        checkIfShared.setLimit(2);
        checkIfShared.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> mealPlans, ParseException e) {
                if (e == null) {
                    ParseObject mp;
                    shareLunch.setVisibility(View.VISIBLE);
                    shareDinner.setVisibility(View.VISIBLE);
                    for (int i = 0; i < mealPlans.size(); i++) {
                        mp = mealPlans.get(i);
                        if (mp != null) {
                            if (mp.getString("type").equals("lunch")) {
                                takeBackLunch.setVisibility(View.VISIBLE);
                                shareLunch.setVisibility(View.GONE);
                            } else {
                                // dinner
                                takeBackDinner.setVisibility(View.VISIBLE);
                                shareDinner.setVisibility(View.GONE);
                            }
                        }
                    }
                } else {

                }
            }
        });
        // load all open access meal plans
        ParseQuery<ParseUser> friends = ParseUser.getQuery();
        friends.whereEqualTo("from", currentUser);
        friends.whereEqualTo("status",1);
        Log.d("yerchik/mf", friends+"");

        ParseQuery<ParseObject> openMealPlans = ParseQuery.getQuery("MealPlans");
        openMealPlans.whereMatchesQuery("owner",friends);
        openMealPlans.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e==null){
                    Log.d("yerchik/mp", list+"");
                }else {

                }

            }
        });

        // When user wants to share either lunch or dinner
        // When shareLunchBtn is clicked
        shareLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    ParseObject mealPlan = new ParseObject("MealPlans");
                    mealPlan.put("owner", currentUser);
                    mealPlan.put("type", "lunch");
                    mealPlan.put("isTaken",0);
                    mealPlan.saveEventually();
                    shareLunch.setVisibility(View.GONE);
                    takeBackLunch.setVisibility(View.VISIBLE);
                }
            }
        });

        // When shareDinnerBtn is clicked
        shareDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    ParseObject mealPlan = new ParseObject("MealPlans");
                    mealPlan.put("owner", currentUser);
                    mealPlan.put("type", "dinner");
                    mealPlan.put("isTaken",0);
                    mealPlan.saveEventually();
                    shareDinner.setVisibility(View.GONE);
                    takeBackDinner.setVisibility(View.VISIBLE);
                }
            }
        });

        // When user wants to take back any of his meal plans in case if no one has already taken it
        takeBackLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // find that meal plan in MealPlnas table
                if (currentUser != null) {
                    ParseQuery<ParseObject> mealPlan = ParseQuery.getQuery("MealPlans");
                    mealPlan.whereEqualTo("owner", currentUser);
                    mealPlan.whereEqualTo("type", "lunch");
                    mealPlan.setLimit(1);
                    mealPlan.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> mp, ParseException e) {
                            // delete it
                            mp.get(0).deleteEventually();
                            takeBackLunch.setVisibility(View.GONE);
                            shareLunch.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

        takeBackDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    ParseQuery<ParseObject> mealPlan = ParseQuery.getQuery("MealPlans");
                    mealPlan.whereEqualTo("owner", currentUser);
                    mealPlan.whereEqualTo("type", "dinner");
                    mealPlan.setLimit(1);
                    mealPlan.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> mp, ParseException e) {
                            mp.get(0).deleteEventually();
                            takeBackDinner.setVisibility(View.GONE);
                            shareDinner.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

    }
}
