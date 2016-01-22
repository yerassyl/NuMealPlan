package com.yerchik.mealplan2.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yerchik.mealplan2.MainActivity;
import com.yerchik.mealplan2.R;
import com.yerchik.mealplan2.adapter.MealPlanAdapter;
import com.yerchik.mealplan2.adapter.TakenMealPlanAdapter;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MealPlansFragment extends Fragment {
    private static final String FRAGMENT_NAME = "MealPlans";

    private static Context context;
    Activity activity;

    private static ParseObject clickedMealPlan;

    public static CircularProgressView takenMealPlanProgress,openMealPlanProgress;
    Button shareLunch,shareDinner,takeBackLunch,takeBackDinner;
    ParseUser currentUser;

    public static ListView mealPlansList;
    public static ListView takenMealPlansList;

    public static List<ParseObject> takenMealPlansPOList;
    public static List<ParseObject> openMealPlansPOList;

    public static MealPlanAdapter openMealPlanAdapter;
    public static TakenMealPlanAdapter takenMealPlanAdapter;

    SwipeRefreshLayout swipeRefreshMealPlans;

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

        context = getContext();
        activity = getActivity();

        shareLunch = (Button)getActivity().findViewById(R.id.shareLunchBtn);
        shareDinner = (Button)getActivity().findViewById(R.id.shareDinerBtn);
        takeBackLunch = (Button)getActivity().findViewById(R.id.takeBackLunchBtn);
        takeBackDinner = (Button)getActivity().findViewById(R.id.takeBackDinnerBtn);
        mealPlansList = (ListView)getActivity().findViewById(R.id.mealPlans);
        takenMealPlansList = (ListView)getActivity().findViewById(R.id.takenMealPlans);
        swipeRefreshMealPlans = (SwipeRefreshLayout)getActivity().findViewById(R.id.swipeRefreshMealPlans);
        openMealPlanProgress = (CircularProgressView)getActivity().findViewById(R.id.progressOpenMealPlans);
        takenMealPlanProgress = (CircularProgressView)getActivity().findViewById(R.id.progressTakeMealPlans);

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

        getAllTakenMealPlans();

        // set swipe to refresh listener
        swipeRefreshMealPlans.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                getAllTakenMealPlans();
                getAllOpenMealPlans(getContext(),getActivity());
                swipeRefreshMealPlans.setRefreshing(false);
            }

        });

        // When user wants to share either lunch or dinner
        // When shareLunchBtn is clicked
        shareLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    // make button not clickable
                    shareLunch.setEnabled(false);
                    ParseObject mealPlan = new ParseObject("MealPlans");
                    mealPlan.put("owner", currentUser);
                    mealPlan.put("taker",currentUser);
                    mealPlan.put("type", "lunch");
                    mealPlan.put("isTaken", 0);
                    mealPlan.put("isUsed", 0);
                    mealPlan.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            shareLunch.setVisibility(View.GONE);
                            takeBackLunch.setVisibility(View.VISIBLE);
                            shareLunch.setEnabled(true);
                            incrementSharedMealPlans();
                        }
                    });

                }
            }
        });

        // When shareDinnerBtn is clicked
        shareDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    shareDinner.setEnabled(false);
                    ParseObject mealPlan = new ParseObject("MealPlans");
                    mealPlan.put("owner", currentUser);
                    mealPlan.put("taker",currentUser);
                    mealPlan.put("type", "dinner");
                    mealPlan.put("isTaken", 0);
                    mealPlan.put("isUsed", 0);
                    mealPlan.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            shareDinner.setVisibility(View.GONE);
                            takeBackDinner.setVisibility(View.VISIBLE);
                            shareDinner.setEnabled(true);
                            incrementSharedMealPlans();
                        }
                    });
                }
            }
        });

        // When user wants to take back any of his meal plans in case if no one has already taken it
        takeBackLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // find that meal plan in MealPlnas table
                if (currentUser != null) {
                    takeBackLunch.setEnabled(false);
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
                                    mp.get(0).deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            takeBackLunch.setVisibility(View.GONE);
                                            shareLunch.setVisibility(View.VISIBLE);
                                            decrementSharedMealPlans();
                                            takeBackLunch.setEnabled(true);
                                        }
                                    });
                                }
                                else {
                                    takeBackLunch.setEnabled(true);
                                    showAlertMessage(context,"Sorry!","Your meal plan has already been taken by someone","OK");
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
                    takeBackDinner.setEnabled(false);
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
                                    mp.get(0).deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            takeBackDinner.setEnabled(true);
                                            takeBackDinner.setVisibility(View.GONE);
                                            shareDinner.setVisibility(View.VISIBLE);
                                            decrementSharedMealPlans();
                                        }
                                    });

                                } else {
                                    takeBackDinner.setEnabled(true);
                                    showAlertMessage(getContext(),"Sorry!","Your meal plan has already been taken by someone","OK");
                                }

                            }else {

                            }

                        }
                    });
                }
            }
        });


    }// END onViewCreated


    // load all taken meal plans that belong to current_user
    public void getAllTakenMealPlans(){
        takenMealPlanProgress.setVisibility(View.VISIBLE);
        // get all taken meal plans
        ParseQuery<ParseObject> takenMealPlansQuery = ParseQuery.getQuery("MealPlans");
        takenMealPlansQuery.whereEqualTo("taker",currentUser);
        takenMealPlansQuery.whereEqualTo("isTaken",1);
        takenMealPlansQuery.whereEqualTo("isUsed", 0);
        takenMealPlansQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    takenMealPlansPOList.clear();
                    takenMealPlansPOList.addAll(list);
                    takenMealPlanAdapter = new TakenMealPlanAdapter(takenMealPlansPOList, getContext());
                    takenMealPlansList.setAdapter(takenMealPlanAdapter);

                    // hide circular progress view
                    takenMealPlanProgress.setVisibility(View.GONE);
                } else {

                }
            }
        });
    }

    // load all open access meal plans that belong to current_user's friends
    public static void getAllOpenMealPlans(final Context context, final Activity activity){
        MealPlansFragment.showOpenMealPlanCircularProgress(activity);
        if (MainActivity.userFriendships!=null) {
            if (MainActivity.userFriendships.size() == 0) {
                MealPlansFragment.hideOpenMealPlanCircularProgress(activity);
            }
            for (int i = 0; i < MainActivity.userFriendships.size(); i++) {
                final ParseQuery<ParseObject> mealPlan = ParseQuery.getQuery("MealPlans");

                // create temp adapter
                // fix
                ParseObject tempPO = new ParseObject("MealPlans");
                openMealPlansPOList.add(tempPO);

                openMealPlanAdapter = new MealPlanAdapter(openMealPlansPOList, context);
                mealPlansList.setAdapter(openMealPlanAdapter);
                openMealPlansPOList.remove(tempPO);
                // end fix

                Calendar cal = new GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                final Date today = cal.getTime();

                cal.add(Calendar.DAY_OF_MONTH, 1); // add one day to get start of tomorrow

                // start of tomorrow
                Date tomorrow = cal.getTime();

                mealPlan.whereEqualTo("owner", MainActivity.userFriendships.get(i).getParseObject("from"));
                mealPlan.whereGreaterThan("createdAt", today);
                mealPlan.whereLessThan("createdAt", tomorrow);
                mealPlan.whereEqualTo("isTaken", 0);

                mealPlan.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> po, ParseException e) {
                        if (e == null) {
                            openMealPlansPOList.clear();
                            openMealPlansPOList.addAll(po);
                            MealPlansFragment.hideOpenMealPlanCircularProgress(activity);
                        } else {
                            MealPlansFragment.hideOpenMealPlanCircularProgress(activity);
                        }
                        openMealPlanAdapter.notifyDataSetChanged();

                    }
                });
            }
        }

    }

    public static void takeMealPlan(final View v) {
        v.setEnabled(false);
        // check if user reached meal plan limit (limi=2)
        if (MealPlansFragment.takenMealPlansPOList.size()>=2){
            showAlertMessage(context,"Limit", "You can't take more than two meal plans","OK");
            v.setEnabled(true);
        }else {
            final ProgressDialog pdialog = ProgressDialog.show(context,"","Taking...",true);
            //get the row the clicked button is in
            View parentRow = (View) v.getParent();
            ListView listView = (ListView) parentRow.getParent();
            int position = listView.getPositionForView(parentRow);

            clickedMealPlan = (ParseObject) openMealPlanAdapter.getItem(position);
            Log.d("yerchik/take", "clickedMP at " + position + " has id " + clickedMealPlan.getObjectId());
            // check if that meal plan is not taken by someone
            ParseQuery<ParseObject> isTakenQuery = ParseQuery.getQuery("MealPlans");
            isTakenQuery.whereEqualTo("objectId", clickedMealPlan.getObjectId());
            isTakenQuery.whereEqualTo("isTaken", 0);
            isTakenQuery.setLimit(1);
            isTakenQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        if (!list.isEmpty()) {
                            // take that meal plan
                            clickedMealPlan.put("isTaken", 1);
                            clickedMealPlan.put("taker", ParseUser.getCurrentUser());
                            clickedMealPlan.saveEventually();
                            // add it to taken meal plans list
                            takenMealPlansPOList.add(clickedMealPlan);
                            takenMealPlanAdapter.notifyDataSetChanged();
                            // remove it from open access meal plans list
                            openMealPlansPOList.remove(clickedMealPlan);
                            openMealPlanAdapter.notifyDataSetChanged();
                            pdialog.dismiss();
                        } else {
                            // tell the user that meal plan has already been taken
                            showAlertMessage(context, "Sorry!", "That meal plan has already been taken by someone", "OK");
                            // remove that meal plan from open access meal plans list
                            openMealPlansPOList.remove(clickedMealPlan);
                            openMealPlanAdapter.notifyDataSetChanged();
                        }
                    } else {

                    }
                }
            });
        }
    }

    public static void usedMealPlan(View v){
        v.setEnabled(false);
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
    public static void showAlertMessage(Context context,String title,String message, String positiveBtn){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    public static void hideOpenMealPlanCircularProgress(Activity activity){
        // hide circular progress view
        openMealPlanProgress.setVisibility(View.GONE);
    }
    public static void showOpenMealPlanCircularProgress(Activity activity){
        // hide circular progress view
        openMealPlanProgress.setVisibility(View.VISIBLE);
    }


}
