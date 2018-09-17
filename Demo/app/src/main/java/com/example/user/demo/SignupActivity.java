package com.example.user.demo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SIGNUP_ACTIVITY";
    Button mSignin, mSignUp;

    String mBaseUrl = "127.0.0.1/demo.php";

    EditText mName, mEmail, mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mSignin = (Button) findViewById(R.id.btn_link_login);
        mSignUp = (Button) findViewById(R.id.btn_signup);

        mName = (EditText) findViewById(R.id.signup_input_name);
        mEmail = (EditText) findViewById(R.id.signup_input_email);
        mPhone = (EditText) findViewById(R.id.signup_input_password);


        mSignin.setOnClickListener(this);
        mSignUp.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_link_login:
                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                finish();
                break;


            case R.id.btn_signup:

                signUp();

                break;

        }
    }

    private void signUp() {

        final String mUserName = mName.getText().toString();
        final String mEmailAdd = mEmail.getText().toString();
        final String mPhoneNumber = mPhone.getText().toString();

        Log.d(TAG, "Login");
        if (!userInputValidation()) {

            return;
        }

        mSignUp.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        //for testing purpose
        //startActivity(new Intent(SignupActivity.this, SignupActivity.class));

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                mBaseUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response != null) {

                            //todo get the response and check it

                            startActivity(new Intent(SignupActivity.this, MainActivity.class));

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(SignupActivity.this, "ERROR", Toast.LENGTH_SHORT).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", mUserName);
                params.put("e-mail", mEmailAdd);
                params.put("phone_number", mPhoneNumber);
                return params;
            }
        };


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        //onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                        mSignUp.setEnabled(true);
                    }
                }, 1000);

        stringRequest.setShouldCache(false);
        MySingleton.getmInstance(SignupActivity.this).addToRequestQueue(stringRequest);


    }

    private boolean userInputValidation() {

        boolean valid = true;

        String email = mEmail.getText().toString();
        String password = mPhone.getText().toString();
        String name = mName.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError("Enter a valid email address");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mPhone.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            mPhone.setError(null);
        }

        if (name.isEmpty()) {
            mName.setError("Enter a valid Name!");
            valid = false;
        } else {
            mName.setError(null);
        }

        return valid;

    }
}
