package com.fiuba.apredazzi.tp_taller2_android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.fiuba.apredazzi.tp_taller2_android.BaseActivity;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.adapter.ContactsAdapter;
import com.fiuba.apredazzi.tp_taller2_android.adapter.SongAdapter;
import com.fiuba.apredazzi.tp_taller2_android.api.TokenGenerator;
import com.fiuba.apredazzi.tp_taller2_android.api.UsersService;
import com.fiuba.apredazzi.tp_taller2_android.interfaces.RecyclerViewClickListener;
import com.fiuba.apredazzi.tp_taller2_android.model.Song;
import com.fiuba.apredazzi.tp_taller2_android.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsActivity extends BaseActivity implements RecyclerViewClickListener {

    private List<User> contactsList;
    private ContactsAdapter adapter;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_songs_list, null, false);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        frameLayout.addView(contentView);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.VISIBLE);

        loadContacts();

    }

    private void loadContacts() {
        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        String auth_token_string = settings.getString("auth_token", "null");

        if (auth_token_string != null) {
            UsersService usersService = TokenGenerator.createService(UsersService.class, auth_token_string);
            Call<List<User>> listUsuarios = usersService.getAllUsers();
            listUsuarios.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(final Call<List<User>> call, final Response<List<User>> response) {
                    if (response.isSuccessful()) {
                        contactsList = response.body();
                        setAdapter();
                        Toast.makeText(ContactsActivity.this, "Recibi usuarios", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ContactsActivity.this, "Falle onResponse: " + response.errorBody(),
                            Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(final Call<List<User>> call, final Throwable t) {
                    Toast.makeText(ContactsActivity.this, "Falle", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void setAdapter() {
        progressBar.setVisibility(View.GONE);
        adapter = new ContactsAdapter(ContactsActivity.this, contactsList, this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void recyclerViewListClicked(final View v, final int position) {
        User user = contactsList.get(position);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("email_receiver", user.getEmail());
        startActivity(intent);
    }
}
