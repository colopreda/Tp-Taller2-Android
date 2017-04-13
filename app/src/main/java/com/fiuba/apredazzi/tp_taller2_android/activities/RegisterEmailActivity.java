package com.fiuba.apredazzi.tp_taller2_android.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.api.LoginService;
import com.fiuba.apredazzi.tp_taller2_android.model.Token;
import com.fiuba.apredazzi.tp_taller2_android.model.User;
import com.google.firebase.auth.FirebaseAuth;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterEmailActivity extends AppCompatActivity {

    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignup;

    private TextView textViewSignin;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_test);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //if getCurrentUser does not returns null
        if (firebaseAuth.getCurrentUser() != null) {
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        //initializing views
        editTextFirstName = (EditText) findViewById(R.id.editTextName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewSignin = (TextView) findViewById(R.id.textViewSignin);

        buttonSignup = (Button) findViewById(R.id.buttonSignup);

        progressDialog = new ProgressDialog(this);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                registerUser();
            }
        });

        textViewSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent i = new Intent(RegisterEmailActivity.this, LoginEmailActivity.class);
                startActivity(i);
            }
        });
    }

    private void registerUser() {

        //getting email and password from edit texts
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String first_name = editTextFirstName.getText().toString().trim();
        final String last_name = editTextLastName.getText().toString().trim();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Por favor, ingrese un mail", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, ingrese una contraseña", Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registrando. Por favor espere...");
        progressDialog.show();

        User user = new User(email, first_name, last_name, password);
        registerSS(user);

        //creating a new user
//        firebaseAuth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//                    //checking if success
//                    if (task.isSuccessful()) {
//                        FirebaseUser userRegistered = firebaseAuth.getCurrentUser();
//                        if (userRegistered.getUid() != null) {
//                            User user = new User(email, first_name, last_name,userRegistered.getUid());
//                            registerSS(user);
//                        } else {
//                            Toast.makeText(RegisterEmailActivity.this, "Registration Error GUID", Toast.LENGTH_LONG).show();
//                        }
//                    } else {
//                        //display some message here
//                        Toast.makeText(RegisterEmailActivity.this, "Registration Error Task Unsuccessful", Toast.LENGTH_LONG).show();
//                    }
//                    progressDialog.dismiss();
//                }
//            });
    }

    private void registerSS(final User userRegister) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …
        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        // Linea de client solo para logueo
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(LoginService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build();

        final LoginService loginService = retrofit.create(LoginService.class);

        Call<User> call = loginService.createUser(userRegister);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(final Call<User> callRegister, final Response<User> response) {
                int statusCode = response.code();
                if (statusCode == 201) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), LoginEmailActivity.class));
                    Toast.makeText(RegisterEmailActivity.this, "Registro con exito", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RegisterEmailActivity.this, "Registration Error code != 200", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(final Call<User> call, final Throwable t) {
                Toast.makeText(RegisterEmailActivity.this, "Registration Error on Failure", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });

    }
}
