package com.fiuba.apredazzi.tp_taller2_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.facebook.login.LoginManager;
import com.fiuba.apredazzi.tp_taller2_android.activities.AlbumsActivity;
import com.fiuba.apredazzi.tp_taller2_android.activities.ArtistsActivity;
import com.fiuba.apredazzi.tp_taller2_android.activities.ChatActivity;
import com.fiuba.apredazzi.tp_taller2_android.activities.ContactsActivity;
import com.fiuba.apredazzi.tp_taller2_android.activities.LoginEmailActivity;
import com.fiuba.apredazzi.tp_taller2_android.activities.MainActivity;
import com.fiuba.apredazzi.tp_taller2_android.activities.PlaylistActivity;
import com.fiuba.apredazzi.tp_taller2_android.activities.ProfileActivity;
import com.fiuba.apredazzi.tp_taller2_android.activities.SongActivity;
import com.fiuba.apredazzi.tp_taller2_android.activities.SongsListActivity;
import com.fiuba.apredazzi.tp_taller2_android.model.Song;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout drawer;

    final int ARTISTA = 0;
    final int ALBUM = 1;
    final int CANCION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("music.io");
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(final View drawerView, final float slideOffset) {
                if (slideOffset >= 0.1) {
                    SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());

                    String full_name = settings.getString("full_name", "null");
                    String email = settings.getString("email", "null");

                    setNameAndEmail(full_name, email);
                }
            }

            @Override
            public void onDrawerOpened(final View drawerView) {

            }

            @Override
            public void onDrawerClosed(final View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(final int newState) {

            }
        });
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setNameEmailDrawer();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            filterDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_canciones) {
            Intent i = new Intent(this, SongsListActivity.class);
            i.putExtra("songs", true);
            i.putExtra("drawer", true);
            startActivity(i);
        } else if (id == R.id.nav_albums) {
            Intent i = new Intent(this, AlbumsActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_artists) {
            Intent i = new Intent(this, ArtistsActivity.class);
            i.putExtra("drawer", true);
            startActivity(i);
        } else if (id == R.id.nav_playlists) {
            Intent i = new Intent(this, PlaylistActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_playing) {
            Intent i = new Intent(this, SongActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        } else if (id == R.id.nav_chat) {
            Intent i = new Intent(this, ContactsActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_settings) {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_log_out) {
            SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
            String auth_token_string = settings.getString("auth_token", "null");
            if (auth_token_string != null) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("auth_token", "null");
                editor.commit();
                if (LoginManager.getInstance() != null) {
                    LoginManager.getInstance().logOut();
                }
            }

            FirebaseAuth.getInstance().signOut();

            Intent i = new Intent(this, LoginEmailActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected String getTokenSession() {
        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        String auth_token_string = settings.getString("auth_token", "null");
        return auth_token_string;
    }

    public void filterDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.filter_dialog, null);

        builder.setTitle("Buscar");
//        builder.setView(R.layout.filter_dialog);
        loadPossibilities(dialogView);
        builder.setView(dialogView)
            .setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner_possibilities);
                    EditText query = (EditText) dialogView.findViewById(R.id.edittext_filter);
                    switch (spinner.getSelectedItemPosition()) {
                        case ARTISTA:
                            Intent i = new Intent(BaseActivity.this, ArtistsActivity.class);
                            i.putExtra("filter", query.getText().toString());
                            startActivity(i);
                            break;
                        case ALBUM:
                            Intent intent = new Intent(BaseActivity.this, AlbumsActivity.class);
                            intent.putExtra("filter", query.getText().toString());
                            startActivity(intent);
                            break;
                        case CANCION:
                            Intent songsIntent = new Intent(BaseActivity.this, SongsListActivity.class);
                            songsIntent.putExtra("songs", true);
                            songsIntent.putExtra("filter", query.getText().toString());
                            startActivity(songsIntent);
                            break;
                        default:
                            break;
                    }
                }
            })
            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        ;
        builder.create();
        builder.show();
    }

    private void loadPossibilities(View v) {
        String[] posibilidades = {
            "Artista", "Album", "Cancion"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, posibilidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner possibilities = (Spinner) v.findViewById(R.id.spinner_possibilities);
        possibilities.setAdapter(adapter);
    }

    protected void setNameAndEmail(String strName, String strEmail) {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        TextView name = (TextView) navigationView.findViewById(R.id.name_drawer);
        TextView email = (TextView) navigationView.findViewById(R.id.email_drawer);
        name.setText(strName);
        email.setText(strEmail);

        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("name_user", strName);
        editor.putString("email_user", strEmail);
        editor.commit();
    }

    protected void setNameEmailDrawer() {

        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        String saved_name = settings.getString("name_user", "null");
        String saved_email = settings.getString("email_user", "null");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView nameDrawer = (TextView) headerView.findViewById(R.id.name_drawer);
        TextView emailDrawer = (TextView) headerView.findViewById(R.id.email_drawer);
        nameDrawer.setText(saved_name);
        emailDrawer.setText(saved_email);

        ImageView profilePic = (ImageView) headerView.findViewById(R.id.imageViewProfile);
        String profileUrl = settings.getString("profile_url", null);
        if (profileUrl != null) {
            Picasso.with(BaseActivity.this).load(profileUrl).into(profilePic);
        }
    }

    protected String getSavedName() {
        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        return settings.getString("name_user", "null");
    }

    protected String getSavedEmail() {
        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        return settings.getString("email_user", "null");
    }

    protected String getSavedCountry() {
        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        return settings.getString("country", "null");
    }

    protected String getSavedBirthday() {
        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        return settings.getString("birthday", "null");
    }

    protected void setTitleTooblar(String titleTooblar) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(titleTooblar);
    }
}
