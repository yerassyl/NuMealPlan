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
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignupActivity extends Activity {

    protected EditText emailView,idNumberView,passwordView,passwordConfirmView;
    protected Button signUpBtnView;
    protected ProgressDialog dialogSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // grab views
        emailView = (EditText)findViewById(R.id.emailSignup);
        idNumberView = (EditText)findViewById(R.id.idNumberSignup);
        passwordView = (EditText)findViewById(R.id.passwordSignup);
        passwordConfirmView = (EditText)findViewById(R.id.passwordConfirmSignup);
        signUpBtnView = (Button)findViewById(R.id.signupBtnSignup);

        // when sign up btn is clicked
        signUpBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get user input and convert it to String
                String emailStr = emailView.getText().toString().trim();
                String idNumberStr = idNumberView.getText().toString().trim();
                String passwordStr = passwordView.getText().toString().trim();
                String passwordConfirmStr = passwordConfirmView.getText().toString().trim();

                // check if user provided all required data
                if (emailStr.equals("") || idNumberStr.equals("") || passwordStr.equals("") || passwordConfirmStr.equals("")) {
                    //Toast.makeText(getApplicationContext(),"empty",Toast.LENGTH_SHORT).show();
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
                    emailView.setError("Please sign in with your nu.edu.kz gmail");
                } else {
                    // show progress dialog
                    dialogSignup = ProgressDialog.show(SignupActivity.this, "", "Loading. Please wait...", true);

                    // register user using Parse sdk
                    ParseUser user = new ParseUser();
                    user.setUsername(idNumberStr);
                    user.setPassword(passwordStr);
                    user.setEmail(emailStr);
                    // other fields can be set just like with ParseObject
                    //user.put("phone", "650-253-0000");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button_default, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
