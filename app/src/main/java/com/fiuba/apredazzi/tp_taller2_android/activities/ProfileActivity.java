package com.fiuba.apredazzi.tp_taller2_android.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.fiuba.apredazzi.tp_taller2_android.BaseActivity;
import com.fiuba.apredazzi.tp_taller2_android.R;
import com.fiuba.apredazzi.tp_taller2_android.api.TokenGenerator;
import com.fiuba.apredazzi.tp_taller2_android.api.UsersService;
import com.fiuba.apredazzi.tp_taller2_android.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {

    private static final int REQUEST_IMAGE = 1;
    private static final String PROFILE_PICTURES = "profile_pictures";

    private ImageView editNameButton;
    private TextView editLocationTextView;
    private TextView nameTextView;
    private TextView emailTextView;
    private CircleImageView userProfilePhoto;

    private String auth_token_string;

    private ProgressDialog progressDialog;

    // Create a storage reference from our app
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://musicio-75fa2.appspot.com");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_profile, null, false);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        frameLayout.addView(contentView);

        nameTextView = (TextView) findViewById(R.id.name_profile);
        nameTextView.setText(getSavedName());
        emailTextView = (TextView) findViewById(R.id.email_profile);
        emailTextView.setText(getSavedEmail());

        editNameButton = (ImageView) findViewById(R.id.edit_name_profile);
        editNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                nameEmailDialg();
            }
        });

        editLocationTextView = (TextView) findViewById(R.id.location_textview);
        editLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                locationDialg();
            }
        });

        userProfilePhoto = (CircleImageView) findViewById(R.id.user_profile_photo);
        userProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        progressDialog = new ProgressDialog(this);

        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        auth_token_string = settings.getString("auth_token", "null");

        String profileUrl = settings.getString("profile_url", null);
        if (profileUrl != null) {
            Picasso.with(ProfileActivity.this).load(profileUrl).into(userProfilePhoto);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {

                SharedPreferences settings = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
                final String myId = settings.getString("myId", "null");

                if (data != null && !"null".equals(myId)) {
                    final Uri uri = data.getData();

                    StorageReference riversRef = storageRef.child(PROFILE_PICTURES).child(myId);
                    UploadTask uploadTask = riversRef.putFile(uri);

                    progressDialog.setMessage("Actualizando foto de perfil");
                    progressDialog.show();

                // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Toast.makeText(ProfileActivity.this, downloadUrl.toString(), Toast.LENGTH_LONG).show();
                            Picasso.with(ProfileActivity.this).load(downloadUrl.toString()).into(userProfilePhoto, new com.squareup.picasso.Callback() {

                                @Override
                                public void onSuccess() {
                                    progressDialog.dismiss();
                                    SharedPreferences settings = PreferenceManager
                                        .getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor editor = settings.edit();
                                    @SuppressWarnings("VisibleForTests") String profile_url = taskSnapshot.getDownloadUrl().toString();
                                    editor.putString("profile_url", profile_url);
                                    editor.commit();
                                }

                                @Override
                                public void onError() {
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    public void nameEmailDialg() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        builder.setTitle("Editar");
        View view = inflater.inflate(R.layout.name_profile_dialog, null);

        final EditText old_name = (EditText) view.findViewById(R.id.name_dialog);
        old_name.setText(nameTextView.getText());

        final EditText old_email = (EditText) view.findViewById(R.id.email_dialog);
        old_email.setText(emailTextView.getText());

        builder.setView(view)
            // Add action buttons
            .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    saveNameAndEmail(old_name.getText().toString(), old_email.getText().toString());
                }
            })
            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        builder.create();
        builder.show();
    }

    public void locationDialg() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        builder.setTitle("Editar");
        builder.setView(inflater.inflate(R.layout.location_dialog, null))
            // Add action buttons
            .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                }
            })
            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        builder.create();
        builder.show();
    }

    private void saveNameAndEmail(final String name, final String email) {
        if (!auth_token_string.equals("null")) {
            UsersService usersService = TokenGenerator.createService(UsersService.class, auth_token_string);
            String[] splited = name.split("\\s+");
            User modifiedUser = new User(email, splited[0], splited[1]);
            Call<ResponseBody> updateUser = usersService.modifyUserMe(modifiedUser);
            final ProgressDialog pd = new ProgressDialog(ProfileActivity.this);
            pd.setMessage("Actualizando usuario...");
            pd.show();
            updateUser.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Se actualizó con éxito el usuario", Toast.LENGTH_LONG).show();
                        setNameAndEmail(name, email);
                        nameTextView.setText(name);
                        emailTextView.setText(email);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Hubo un error actualizando el usuario", Toast.LENGTH_LONG).show();
                    }
                    pd.dismiss();
                }

                @Override
                public void onFailure(final Call<ResponseBody> call, final Throwable t) {
                    Toast.makeText(ProfileActivity.this, "OnFailure: " + t, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
