package com.example.user.demo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    EditText pass, username;
    Button mShowPass, mSubmitBtn;
    ImageView mGifIv, mHabiIv, mChatIv, mPawIv;
    String mBaseUrl = "127.0.0.1/demo.php",
            DB_Name = "DEMO_DB";

    private static final String TAG = "MainActivity";
    private static final int REQUEST_SIGNUP = 0;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    Boolean passType = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mPreferences = getSharedPreferences(DB_Name, Context.MODE_PRIVATE);

        pass = (EditText) findViewById(R.id.password_et);
        username = (EditText) findViewById(R.id.username_et);

        mGifIv = (ImageView) findViewById(R.id.image_view);
        mHabiIv = (ImageView) findViewById(R.id.habi_image_view);
        mChatIv = (ImageView) findViewById(R.id.chat_image_view);
        mPawIv = (ImageView) findViewById(R.id.paw_image_view);

        mShowPass = (Button) findViewById(R.id.show_btn);
        mSubmitBtn = (Button) findViewById(R.id.submit_btn);

        mShowPass.setOnClickListener(this);
        mSubmitBtn.setOnClickListener(this);
        mHabiIv.setOnClickListener(this);
        mChatIv.setOnClickListener(this);
        mPawIv.setOnClickListener(this);

        username.setOnTouchListener(this);
        pass.setOnTouchListener(this);
        mShowPass.setOnTouchListener(this);
        gifImageLoad();

    }

    private void loginStatus(boolean b) {

        mEditor = mPreferences.edit();
        mEditor.putBoolean("log_in_status", b);
        mEditor.apply();


    }

    private void login() {

        final String mUserName = username.getText().toString();
        final String mPassword = pass.getText().toString();

        Log.d(TAG, "Login");
        if (!userInputValidation()) {
            onLoginFailed();
            return;
        }

        mSubmitBtn.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                R.style.Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                mBaseUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response != null) {

                            //todo check login status code and the log in

                            onLoginSuccess();
                            //Toast.makeText(MainActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();

                        } else {
                            //todo do some thing

                            try {
                                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();

                               /* loginStatus(true);
                                onLoginSuccess();
                                progressDialog.dismiss();
                                startActivity(new Intent(MainActivity.this, SomeActivity.class));
                                finish();*/

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                onLoginFailed();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", mUserName);
                params.put("password", mPassword);
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
                    }
                }, 1000);

        stringRequest.setShouldCache(false);
        MySingleton.getmInstance(MainActivity.this).addToRequestQueue(stringRequest);

    }

    private boolean userInputValidation() {

        boolean valid = true;

        String email = username.getText().toString();
        String password = pass.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            username.setError("Enter a valid email address");
            valid = false;
        } else {
            username.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            pass.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            pass.setError(null);
        }

        return valid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful sign up logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();

            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    private void onLoginSuccess() {
        mSubmitBtn.setEnabled(true);
        loginStatus(false);
        Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
    }

    private void onLoginFailed() {

        Toast.makeText(getBaseContext(), "Login Failed", Toast.LENGTH_SHORT).show();
        loginStatus(false);
        mSubmitBtn.setEnabled(true);
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {


            case R.id.show_btn:

                if (passType == true) {
                    passType = false;
                    pass.setTransformationMethod(null);

                    if (pass.getText().length() > 0) {
                        pass.setSelection(pass.getText().length());

                    }
                    Glide.with(MainActivity.this)
                            .load(R.drawable.show_pass)
                            .into(mGifIv);

                } else {

                    passType = true;
                    pass.setTransformationMethod(new PasswordTransformationMethod());
                    if (pass.getText().length() > 0) {
                        pass.setSelection(pass.getText().length());

                    }

                    Glide.with(MainActivity.this)
                            .load(R.drawable.pass_press)
                            .into(mGifIv);

                }

                break;


            case R.id.submit_btn:


                if (isNetworkConnected()) {

                    //todo: before login check isLogin status true or false!!
                    login();
                    gifImageLoad();

                } else {
                    Toast.makeText(MainActivity.this, "No Internet Connection!", Toast.LENGTH_LONG).show();
                    //loginStatus(false);
                }
                break;

            default:
                //
        }
    }

    private void gifImageLoad() {

        Glide.with(MainActivity.this)
                .load(R.drawable.normal)
                .into(mGifIv);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (view.getId()) {

            case R.id.password_et:

                Glide.with(MainActivity.this)
                        .load(R.drawable.pass_press)
                        .into(mGifIv);

                break;

            case R.id.username_et:

                Glide.with(MainActivity.this)
                        .load(R.drawable.rotation)
                        .into(mGifIv);


                break;

            default:
                //
        }


        return false;
    }
}
