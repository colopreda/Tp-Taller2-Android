package com.fiuba.apredazzi.tp_taller2_android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.fiuba.apredazzi.tp_taller2_android.BaseActivity;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.adapter.ContactsAdapter;
import com.fiuba.apredazzi.tp_taller2_android.api.TokenGenerator;
import com.fiuba.apredazzi.tp_taller2_android.api.UsersService;
import com.fiuba.apredazzi.tp_taller2_android.interfaces.RecyclerViewClickListener;
import com.fiuba.apredazzi.tp_taller2_android.model.FriendId;
import com.fiuba.apredazzi.tp_taller2_android.model.User;
import com.fiuba.apredazzi.tp_taller2_android.utils.ServerResponse;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsActivity extends BaseActivity implements RecyclerViewClickListener {

    private List<User> contactsList;
    private ContactsAdapter adapter;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton floating;

    private boolean isUsers;

    String auth_token_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_songs_list, null, false);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        frameLayout.addView(contentView);

        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        auth_token_string = settings.getString("auth_token", "null");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        floating = (FloatingActionButton) findViewById(R.id.floating_add);

        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(ContactsActivity.this, ContactsActivity.class);
                intent.putExtra("users", true);
                startActivity(intent);
            }
        });

        progressBar.setVisibility(View.VISIBLE);

        isUsers = false;

        if (getIntent().getExtras() != null) {
            isUsers = getIntent().getExtras().getBoolean("users", false);
        }
        if (isUsers) {
            loadUsers();
        } else {
            loadContacts();
        }


    }

    private void loadUsers() {

        if (auth_token_string != null) {
            UsersService usersService = TokenGenerator.createService(UsersService.class, auth_token_string);
            Call<ServerResponse> listUsuarios = usersService.getAllUsers();
            listUsuarios.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(final Call<ServerResponse> call, final Response<ServerResponse> response) {
                    if (response.isSuccessful()) {
                        contactsList = response.body().getUsers();
                        setAdapter();
                        Toast.makeText(ContactsActivity.this, "Recibi usuarios", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ContactsActivity.this, "Falle onResponse: " + response.errorBody(),
                            Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(final Call<ServerResponse> call, final Throwable t) {
                    Toast.makeText(ContactsActivity.this, "Falle", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void loadContacts() {

        if (auth_token_string != null) {
            UsersService usersService = TokenGenerator.createService(UsersService.class, auth_token_string);
            Call<ServerResponse> listUsuarios = usersService.getAllContacts();
            listUsuarios.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(final Call<ServerResponse> call, final Response<ServerResponse> response) {
                    if (response.isSuccessful()) {
                        contactsList = response.body().getContacts();
                        setAdapter();
                        floating.setVisibility(View.VISIBLE);
                        Toast.makeText(ContactsActivity.this, "Recibi usuarios", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ContactsActivity.this, "Falle onResponse: " + response.errorBody(),
                            Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(final Call<ServerResponse> call, final Throwable t) {
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
        if (isUsers) {
            progressBar.setVisibility(View.VISIBLE);
            addContact(user.getId());
        } else {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("id_receiver", user.getFriendId());
            startActivity(intent);
        }
    }

    private void addContact(String id) {
        UsersService usersService = TokenGenerator.createService(UsersService.class, auth_token_string);
        FriendId friendId = new FriendId(Integer.valueOf(id));
        Call<ResponseBody> addContact = usersService.addContact(friendId);
        addContact.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ContactsActivity.this, "Contacto agregado con éxito", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ContactsActivity.this, "Error al agregar contacto", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(ContactsActivity.this, ContactsActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(final Call<ResponseBody> call, final Throwable t) {
                Toast.makeText(ContactsActivity.this, "OnFailure al agregar contacto", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(ContactsActivity.this, ContactsActivity.class);
                startActivity(intent);
            }
        });
    }
}
