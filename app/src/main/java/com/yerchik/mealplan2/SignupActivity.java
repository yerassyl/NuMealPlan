package com.yerchik.mealplan2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignupActivity extends Activity {

    protected EditText fnameView,lnameView,emailView,idNumberView,passwordView,passwordConfirmView;
    protected Button signUpBtnView;
    protected ProgressDialog dialogSignup;
    protected CheckBox hasMealPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // grab views
        fnameView = (EditText)findViewById(R.id.firstname);
        lnameView = (EditText)findViewById(R.id.lastname);
        emailView = (EditText)findViewById(R.id.emailSignup);
        idNumberView = (EditText)findViewById(R.id.idNumberSignup);
        passwordView = (EditText)findViewById(R.id.passwordSignup);
        passwordConfirmView = (EditText)findViewById(R.id.passwordConfirmSignup);
        signUpBtnView = (Button)findViewById(R.id.signupBtnSignup);
        hasMealPlan = (CheckBox)findViewById(R.id.hasMealPlan);

        // when sign up btn is clicked
        signUpBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get user input and convert it to String
                String fnameStr = fnameView.getText().toString().trim().toLowerCase(); // also convert to lower case for better search
                String lnameStr = lnameView.getText().toString().trim().toLowerCase();
                String emailStr = emailView.getText().toString().trim();
                String idNumberStr = idNumberView.getText().toString().trim();
                String passwordStr = passwordView.getText().toString().trim();
                String passwordConfirmStr = passwordConfirmView.getText().toString().trim();
                boolean hasMealPlanbool = hasMealPlan.isChecked();

                // check if user provided all required data
                if (fnameStr.equals("") || lnameStr.equals("") || emailStr.equals("") || idNumberStr.equals("") || passwordStr.equals("") || passwordConfirmStr.equals("")) {
                    //Toast.makeText(getApplicationContext(),"empty",Toast.LENGTH_SHORT).show();
                    if (fnameStr.equals("")){
                        fnameView.setError(getString(R.string.name_is_required));
                    }
                    if (lnameStr.equals("")){
                        lnameView.setError(getString(R.string.lname_is_required));
                    }
                    if (emailStr.equals("")) {
                        emailView.setError(getString(R.string.email_is_required));
                    }
                    if (idNumberStr.equals("")) {
                        idNumberView.setError(getString(R.string.id_is_required));
                    }
                    if (passwordStr.equals("")) {
                        passwordView.setError(getString(R.string.password_is_required));
                    }
                    if (passwordConfirmStr.equals("")) {
                        passwordConfirmView.setError(getString(R.string.pconfirm_is_required));
                    }
                } else if (!passwordStr.equals(passwordConfirmStr)) {
                    passwordConfirmView.setError(getString(R.string.pconfirm_is_required));
                    //Toast.makeText(getApplicationContext(),getString(R.string.password_not_confirmed),Toast.LENGTH_SHORT).show();
                } else if (!emailStr.contains("@nu.edu.kz")) {
                    emailView.setError("Please sign in with University gmail");
                } else {
                    // show progress dialog
                    dialogSignup = ProgressDialog.show(SignupActivity.this, "", "Loading. Please wait...", true);

                    // register user using Parse sdk
                    ParseUser user = new ParseUser();
                    user.setUsername(idNumberStr);
                    user.setPassword(passwordStr);
                    user.setEmail(emailStr);
                    // other fields can be set just like with ParseObject
                    user.put("name", fnameStr);
                    user.put("surname", lnameStr);
                    user.put("friend_count", 0);
                    user.put("shared_count", 0);
                    user.put("taken_count", 0);
                    user.put("has_meal_plan", hasMealPlanbool);
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Success! Let them use the app now. navigate to HomePageActivity
                                finish();
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                // Sign up didn't succeed. Look at the ParseException
                                // to figure out what went wrong
                                // Toast.makeText(SignupActivity.this,"Failed to Sign up", Toast.LENGTH_SHORT).show();
                                // alert about the problem
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                                builder.setMessage(e.getMessage());
                                builder.setTitle("Error!");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //close dialog
                                        dialogInterface.dismiss();
                                        dialogSignup.dismiss();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }
            }
        });

    }

}
