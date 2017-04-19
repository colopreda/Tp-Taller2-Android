package com.fiuba.apredazzi.tp_taller2_android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.fiuba.apredazzi.tp_taller2_android.BaseActivity;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.api.TokenGenerator;
import com.fiuba.apredazzi.tp_taller2_android.api.UsersService;
import com.fiuba.apredazzi.tp_taller2_android.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    private TextView userEmail;
    private TextView nameDrawer;
    private TextView emailDrawer;

    String auth_token_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.content_main, frameLayout, false);
        frameLayout.addView(contentView);

        userEmail = (TextView) findViewById(R.id.user_email);
        nameDrawer = (TextView) findViewById(R.id.name_drawer);
        emailDrawer = (TextView) findViewById(R.id.email_drawer);

        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        auth_token_string = settings.getString("auth_token", "null");

        if (auth_token_string != null) {
            userEmail.setText(auth_token_string);
            UsersService usersService = TokenGenerator.createService(UsersService.class, auth_token_string);
            Call<User> myUser = usersService.getUserMe();
            myUser.enqueue(new Callback<User>() {
                @Override
                public void onResponse(final Call<User> call, final Response<User> response) {
                    if (response.isSuccessful()) {
                        SharedPreferences settingsId = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = settingsId.edit();
                        editor.putString("myId", response.body().getId());
                        editor.commit();
                    }
                }

                @Override
                public void onFailure(final Call<User> call, final Throwable t) {

                }
            });
        } else {
            userEmail.setText("No hay token seteado");
        }

    }
}
