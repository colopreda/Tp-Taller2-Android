package com.fiuba.apredazzi.tp_taller2_android.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.api.LoginService;
import com.fiuba.apredazzi.tp_taller2_android.api.TokenGenerator;
import com.fiuba.apredazzi.tp_taller2_android.api.UsersService;
import com.fiuba.apredazzi.tp_taller2_android.model.FBUser;
import com.fiuba.apredazzi.tp_taller2_android.model.Token;
import com.fiuba.apredazzi.tp_taller2_android.model.User;
import com.fiuba.apredazzi.tp_taller2_android.utils.ServerResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class LoginEmailActivity extends AppCompatActivity {

    //defining views
    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login_email);
        setContentView(R.layout.activity_login_test);

        callbackManager = CallbackManager.Factory.create();

        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //if the objects getcurrentuser method is not null
        //means user is already logged in

        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        final String auth_token_string = settings.getString("auth_token", "null");
        if (!"null".equals(auth_token_string) && firebaseAuth.getCurrentUser() != null) {
            //checkToken();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignin);
        textViewSignup = (TextView) findViewById(R.id.textViewSignUp);

        progressDialog = new ProgressDialog(this);

        //attaching click listener
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                userLogin();
            }
        });
        textViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                finish();
                startActivity(new Intent(LoginEmailActivity.this, RegisterEmailActivity.class));
            }
        });

        loginButton = (LoginButton) findViewById(R.id.login_button_fb);
        loginButton.setReadPermissions("email", "public_profile");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "facebook:onError", exception);
            }
        });
    }

    private void handleFacebookAccessToken(final AccessToken accessToken) {

        progressDialog.setMessage("Iniciando sesion, por favor espere...");
        progressDialog.show();

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull final Task<AuthResult> task) {
                    facebookLoginSharedServer(accessToken);
                }
            });
    }

    private void facebookLoginSharedServer(AccessToken accessToken) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …
        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(LoginService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build();

        LoginService loginService = retrofit.create(LoginService.class);
        FBUser fbuser = new FBUser(accessToken.getUserId(), accessToken.getToken());
        Call<Token> callFB = loginService.generateToken(fbuser);
        callFB.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(final Call<Token> call, final Response<Token> response) {
                progressDialog.hide();
                if (response.code() != 401) {
                    Toast.makeText(LoginEmailActivity.this, "Logueo de Facebook con exito", Toast.LENGTH_LONG).show();
                    SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("auth_token", response.body().getToken());
                    editor.putString("full_name", firebaseAuth.getCurrentUser().getDisplayName());
                    editor.putString("email", firebaseAuth.getCurrentUser().getEmail());
                    if (firebaseAuth.getCurrentUser().getPhotoUrl() != null) {
                        editor.putString("profile_url", firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                    }
                    editor.commit();
                    goToMainActivity();
                }
            }

            @Override
            public void onFailure(final Call<Token> call, final Throwable t) {
                Toast.makeText(LoginEmailActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.hide();
            }
        });
    }

    //method for user login
    private void userLogin() {
        final String user = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        //checking if user and passwords are empty
        if (TextUtils.isEmpty(user)) {
            Toast.makeText(this, "Por favor, ingrese su usuario", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, ingrese su contraseña", Toast.LENGTH_LONG).show();
            return;
        }

        //if the user and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Iniciando sesion, por favor espere...");
        progressDialog.show();
        loginSharedServer(user, password);
//        loginFirebaseUser(user, password);
    }

    private void loginFirebaseUser(final String user, final String password) {
        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(user, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    //if the task is successfull
                    if (task.isSuccessful()) {
                        //start the profile activity
                        goToMainActivity();
                    } else {
                        goToMainActivity();
                    }
                }
            });
    }

    private void goToMainActivity() {
        Intent i = new Intent(LoginEmailActivity.this, MainActivity.class);
        startActivity(i);
    }

    private void loginSharedServer(final String user, final String password) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …
        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(LoginService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build();

        LoginService loginService = retrofit.create(LoginService.class);

        User userClass = new User(user, password);
        Call<Token> call = loginService.generateToken(userClass);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(final Call<Token> call, final Response<Token> response) {
                Toast.makeText(LoginEmailActivity.this, "Logueo con exito", Toast.LENGTH_LONG).show();
                if (response.code() != 404) {
                    SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("auth_token", response.body().getToken());
                    editor.commit();
                    getMe(response.body().getToken(), password);
//                    loginFirebaseUser(userClass, password);
                }
            }

            @Override
            public void onFailure(final Call<Token> call, final Throwable t) {
                Toast.makeText(LoginEmailActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.hide();
            }
        });
    }

    private void getMe(String auth_token_string, final String password) {
        UsersService usersService = TokenGenerator.createService(UsersService.class, auth_token_string);
        Call<ServerResponse> getMe = usersService.getUserMe();
        getMe.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(final Call<ServerResponse> call, final Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    User me = response.body().getUser();
                    SharedPreferences settingsId = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = settingsId.edit();
                    editor.putString("myId", me.getId());
                    String full_name = me.getFirst_name() + " " + me.getLast_name();
                    editor.putString("full_name", full_name);
                    editor.putString("email", me.getEmail());
                    editor.putString("country", me.getCountry());
                    editor.putString("birthday", me.getBirthdate());
                    if (me.getImages() != null) {
                        editor.putString("profile_url", me.getImages().get(0));
                    } else {
                        editor.putString("profile_url", null);
                    }
                    editor.commit();
                    loginFirebaseUser(me.getEmail(), password);
                    Toast.makeText(LoginEmailActivity.this, "Nombre e email OK", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginEmailActivity.this, "Falle onResponse ME: " + response.errorBody(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(final Call<ServerResponse> call, final Throwable t) {
                Toast.makeText(LoginEmailActivity.this, "Falle ME", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getMe(String auth_token_string) {
        UsersService usersService = TokenGenerator.createService(UsersService.class, auth_token_string);
        Call<ServerResponse> getMe = usersService.getUserMe();
        getMe.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(final Call<ServerResponse> call, final Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    User me = response.body().getUser();
                    SharedPreferences settingsId = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = settingsId.edit();
                    editor.putString("myId", me.getId());
                    String full_name = me.getFirst_name() + " " + me.getLast_name();
                    editor.putString("full_name", full_name);
                    editor.putString("email", me.getEmail());
                    editor.putString("country", me.getCountry());
                    editor.putString("birthday", me.getBirthdate());
                    if (me.getImages() != null) {
                        editor.putString("profile_url", me.getImages().get(0));
                    }
                    editor.commit();
                    Toast.makeText(LoginEmailActivity.this, "Nombre e email OK", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginEmailActivity.this, "Falle onResponse ME: " + response.errorBody(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(final Call<ServerResponse> call, final Throwable t) {
                Toast.makeText(LoginEmailActivity.this, "Falle ME", Toast.LENGTH_LONG).show();
            }
        });
    }

}
