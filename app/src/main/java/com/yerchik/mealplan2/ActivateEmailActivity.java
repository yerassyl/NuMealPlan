package com.yerchik.mealplan2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.ParseUser;


public class ActivateEmailActivity extends ActionBarActivity {

    protected ProgressDialog dialogHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_email);

        Intent intent = getIntent();
        String emailStr = intent.getStringExtra("email");
        //Log.d("yerchik/email", emailStr);
        TextView emailTxtView = (TextView)findViewById(R.id.activateEmail);
        emailTxtView.setText(emailStr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activate_email, menu);
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
                    dialogHome = ProgressDialog.show(ActivateEmailActivity.this, "", "Please wait", true);
                    // logout the user
                    ParseUser.logOutInBackground();
                    dialogHome.dismiss();
                    finish();
                    Intent intent = new Intent(ActivateEmailActivity.this,LoginActivity.class);
                    startActivity(intent);
                    break;
            }
            return super.onOptionsItemSelected(item);

    }
}
