package com.yerchik.mealplan2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;



public class LoginActivity extends Activity {

    protected EditText idNumberView, passwordView;
    protected Button loginBtnView, signUpBtnView;
    protected ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // grab layout views
        idNumberView = (EditText)findViewById(R.id.idNumberLogin);
        passwordView = (EditText)findViewById(R.id.passwordLogin);
        loginBtnView = (Button)findViewById(R.id.loginBtnLogin);
        signUpBtnView = (Button)findViewById(R.id.toSignupBtnLogin);

        // when login btn is clicked
        loginBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // take user idNumber and password
                String idNumberStr = idNumberView.getText().toString().trim();
                String passwordStr = passwordView.getText().toString().trim();

                // check if user provided data
                if (idNumberStr.equals("") || passwordStr.equals("")){
                    if (idNumberStr.equals("")){
                        idNumberView.setError("ID is required");
                    }
                    if (passwordStr.equals("")){
                        passwordView.setError("password is required");
                    }
                }
                else {
                    // show progress dialog
                    dialog = ProgressDialog.show(LoginActivity.this, "", "Loading. Please wait...", true);

                    // login user using parse sdk
                    ParseUser.logInInBackground(idNumberStr, passwordStr, new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                // Success! The user is logged in. Navigate to HomePageActivity
                                finish();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                // Signup failed. Look at the ParseException to see what happened.

                                // alert about the problem
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(e.getMessage());
                                builder.setTitle("Error!");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //close dialog
                                        dialogInterface.dismiss();
                                        dialog.dismiss();
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

        // when sign up btn is clicked
        signUpBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start signup activity
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    // when physical back button_default is pressed
    /*
    @Override
    public void onBackPressed() {
        // do nothing
    }
*/

}

