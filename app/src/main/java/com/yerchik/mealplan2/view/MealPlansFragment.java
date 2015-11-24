package com.yerchik.mealplan2.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yerchik.mealplan2.R;

public class MealPlansFragment extends Fragment {
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
