package com.yerchik.mealplan2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class SettingsActivity extends ActionBarActivity {

    CheckBox hasMealPlanCheckBox;
    Button saveSettings;
    ParseUser currentUser;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        currentUser = ParseUser.getCurrentUser();

        hasMealPlanCheckBox = (CheckBox)findViewById(R.id.hasMealPlanCheckBox);
        saveSettings = (Button)findViewById(R.id.saveSettingsBtn);

        // check if current user is assigned to meal plan

        hasMealPlanCheckBox.setChecked(currentUser.getBoolean("has_meal_plan"));

        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = ProgressDialog.show(SettingsActivity.this,"Saving","Please wait...", false);
                currentUser.put("has_meal_plan", hasMealPlanCheckBox.isChecked());
                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        dialog.dismiss();
                        Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(i);

                    }
                });
            }
        });


    }


}
