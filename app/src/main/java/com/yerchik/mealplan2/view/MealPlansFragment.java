package com.yerchik.mealplan2.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yerchik.mealplan2.MainActivity;
import com.yerchik.mealplan2.R;
import com.yerchik.mealplan2.adapter.MealPlanAdapter;
import com.yerchik.mealplan2.adapter.TakenMealPlanAdapter;

import java.util.ArrayList;
import java.util.List;

public class MealPlansFragment extends Fragment {
    private static final String FRAGMENT_NAME = "MealPlans";

    Button shareLunch,shareDinner,takeBackLunch,takeBackDinner;
    ParseUser currentUser;
    public static ListView mealPlansList;
    public static ListView takenMealPlansList;

    public static List<ParseObject> takenMealPlansPOList;
    public static List<ParseObject> openMealPlansPOList;

    public static MealPlanAdapter openMealPlanAdapter;
    public static TakenMealPlanAdapter takenMealPlanAdapter;

    public static int mp_count = 0;

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
        mealPlansList = (ListView)getActivity().findViewById(R.id.mealPlans);
        takenMealPlansList = (ListView)getActivity().findViewById(R.id.takenMealPlans);
        currentUser = ParseUser.getCurrentUser();

        openMealPlansPOList = new ArrayList<ParseObject>();
        takenMealPlansPOList = new ArrayList<ParseObject>();
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

        // get all taken meal plans
        ParseQuery<ParseObject> takenMealPlansQuery = ParseQuery.getQuery("MealPlans");
        takenMealPlansQuery.whereEqualTo("taker",currentUser);
        takenMealPlansQuery.whereEqualTo("isTaken",1);
        takenMealPlansQuery.whereEqualTo("isUsed",0);
        takenMealPlansQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e==null){
                    takenMealPlansPOList = list;
                    takenMealPlanAdapter = new TakenMealPlanAdapter(takenMealPlansPOList,getContext());
                    takenMealPlansList.setAdapter(takenMealPlanAdapter);
                }else {

                }
            }
        });
        // load all open access meal plans
//        ParseQuery<ParseObject> friends = ParseQuery.getQuery("Friendship");
//        friends.whereEqualTo("from", currentUser);
//        friends.whereEqualTo("status",1);
//        Log.d("yerchik/mf", friends+"");
//
//
//        ParseQuery<ParseObject> openMealPlans = ParseQuery.getQuery("MealPlans");
//        openMealPlans.whereMatchesQuery("owner",friends);
//        openMealPlans.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> list, ParseException e) {
//                if (e==null){
//                    Log.d("yerchik/mp", list+"");
//                }else {
//
//                }
//
//            }
//        });


        // When user wants to share either lunch or dinner
        // When shareLunchBtn is clicked
        shareLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    ParseObject mealPlan = new ParseObject("MealPlans");
                    mealPlan.put("owner", currentUser);
                    mealPlan.put("taker",currentUser);
                    mealPlan.put("type", "lunch");
                    mealPlan.put("isTaken", 0);
                    mealPlan.put("isUsed", 0);
                    mealPlan.saveEventually();
                    shareLunch.setVisibility(View.GONE);
                    takeBackLunch.setVisibility(View.VISIBLE);
                    incrementSharedMealPlans();
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
                    mealPlan.put("taker",currentUser);
                    mealPlan.put("type", "dinner");
                    mealPlan.put("isTaken", 0);
                    mealPlan.put("isUsed", 0);
                    mealPlan.saveEventually();
                    shareDinner.setVisibility(View.GONE);
                    takeBackDinner.setVisibility(View.VISIBLE);
                    incrementSharedMealPlans();
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
                    mealPlan.whereEqualTo("isTaken", 0);
                    mealPlan.setLimit(1);
                    mealPlan.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> mp, ParseException e) {
                            // delete it
                            if (e==null){
                                if (mp!=null && mp.size()!=0){
                                    mp.get(0).deleteEventually();
                                    takeBackLunch.setVisibility(View.GONE);
                                    shareLunch.setVisibility(View.VISIBLE);
                                    decrementSharedMealPlans();
                                }
                                else {
                                    showAlertMessage("Sorry!","Your meal plan has already been taken by someone","OK");
                                }
                            }else {

                            }

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
                    mealPlan.whereEqualTo("isTaken",0);
                    mealPlan.setLimit(1);
                    mealPlan.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> mp, ParseException e) {
                            if (e==null){
                                if (mp!=null && mp.size()!=0){
                                    mp.get(0).deleteEventually();
                                    takeBackDinner.setVisibility(View.GONE);
                                    shareDinner.setVisibility(View.VISIBLE);
                                    decrementSharedMealPlans();
                                } else {
                                    showAlertMessage("Sorry!","Your meal plan has already been taken by someone","OK");
                                }

                            }else {

                            }

                        }
                    });
                }
            }
        });


    }// END onViewCreated

    // load all open access meal plans that belong to current_user's friends
    public static void getAllOpenMealPlans(final Context context){

        for(int i=0;i<MainActivity.userFriendships.size();i++){
            final ParseQuery<ParseObject> mealPlan = ParseQuery.getQuery("MealPlans");

            ParseObject tempPO = new ParseObject("MealPlans");
            openMealPlansPOList.add(tempPO);

            openMealPlanAdapter = new MealPlanAdapter(openMealPlansPOList, context);
            mealPlansList.setAdapter(openMealPlanAdapter);
            openMealPlansPOList.remove(tempPO);

            mealPlan.whereEqualTo("owner", MainActivity.userFriendships.get(i).getParseObject("from"));
            mealPlan.whereEqualTo("isTaken",0);
            //Log.d("yerchik/omp",MainActivity.userFriendships.get(i).getParseObject("from").getObjectId()+"");
            mealPlan.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> po, ParseException e) {
                    if (e == null) {
                        Log.d("yerchik/d",po+"");
                        openMealPlansPOList.addAll(po);
                    } else {

                    }
                    openMealPlanAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public static void takeMealPlan(View v) {
        //get the row the clicked button is in
        View parentRow = (View) v.getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);
        ParseObject clickedMealPlan = (ParseObject)openMealPlanAdapter.getItem(position);
        Log.d("yerchik/fr", "take it: " + clickedMealPlan.getObjectId());
        clickedMealPlan.put("isTaken", 1);
        clickedMealPlan.put("taker", ParseUser.getCurrentUser());
        clickedMealPlan.saveEventually();

        takenMealPlansPOList.add(clickedMealPlan);
        //openMealPlansPO.remove(position);
        takenMealPlanAdapter.notifyDataSetChanged();

        openMealPlansPOList.remove(clickedMealPlan);
        openMealPlanAdapter.notifyDataSetChanged();
    }

    public static void usedMealPlan(View v){
        View parentRow = (View) v.getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);
        ParseObject clickedTakenMealPlan = (ParseObject)takenMealPlanAdapter.getItem(position);
        clickedTakenMealPlan.put("isUsed", 1);
        clickedTakenMealPlan.saveEventually();
        takenMealPlansPOList.remove(clickedTakenMealPlan);
        takenMealPlanAdapter.notifyDataSetChanged();
    }

    public void incrementSharedMealPlans(){
        currentUser.increment("shared_count");
        currentUser.saveEventually();
    }
    public void decrementSharedMealPlans(){
        currentUser.increment("shared_count",-1);
        currentUser.saveEventually();
    }

    // alert message to be used in findInBackground callback
    public void showAlertMessage(String title,String message, String positiveBtn){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //close dialog
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
