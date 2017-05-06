package com.fiuba.apredazzi.tp_taller2_android.activities;

import android.app.DatePickerDialog;
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
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
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
    private TextView birthdayTextView;
    private CircleImageView userProfilePhoto;

    private String auth_token_string;
    Calendar myCalendar = Calendar.getInstance();

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

        setTitleTooblar("Perfil");

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
        if (!"null".equals(getSavedCountry())) {
            editLocationTextView.setText("  Ubicación: " + getSavedCountry());
        }
        editLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                locationDialg();
            }
        });

        birthdayTextView = (TextView) findViewById(R.id.birthday_textview);
        DateFormat originalformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        originalformat.setTimeZone(TimeZone.getTimeZone("GMT"));
        DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dateObj = originalformat.parse(getSavedBirthday());
            String formattedDate = targetFormat.format(dateObj);
            if (!"null".equals(getSavedBirthday())) {
                birthdayTextView.setText("  Fecha de Nacimiento: " + formattedDate);
            }
            myCalendar.setTime(dateObj);
            birthdayTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    new DatePickerDialog(ProfileActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }


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

    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
            int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            saveBirthday(sdf.format(myCalendar.getTime()));
        }

    };

    private void updateBirthday() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        birthdayTextView.setText("  Fecha de Nacimiento: " + sdf.format(myCalendar.getTime()));
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
                            @SuppressWarnings("VisibleForTests")
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Toast.makeText(ProfileActivity.this, downloadUrl.toString(), Toast.LENGTH_LONG).show();
                            Picasso.with(ProfileActivity.this).load(downloadUrl.toString())
                                .into(userProfilePhoto, new com.squareup.picasso.Callback() {

                                    @Override
                                    public void onSuccess() {
                                        SharedPreferences settings = PreferenceManager
                                            .getDefaultSharedPreferences(getApplicationContext());
                                        SharedPreferences.Editor editor = settings.edit();
                                        @SuppressWarnings("VisibleForTests")
                                        String profile_url = taskSnapshot.getDownloadUrl().toString();
                                        editor.putString("profile_url", profile_url);
                                        editor.commit();
                                        User user = new User();
                                        List<String> profile = new ArrayList<String>();
                                        profile.add(profile_url);
                                        user.setImages(profile);
                                        UsersService usersService = TokenGenerator.createService(UsersService.class, auth_token_string);
                                        Call<ResponseBody> updateUser = usersService.modifyUserMe(user);
                                        updateUser.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(final Call<ResponseBody> call,
                                                final Response<ResponseBody> response) {
                                                progressDialog.dismiss();
                                                if (response.isSuccessful()) {
                                                    Toast.makeText(ProfileActivity.this, "Se actualizó con éxito la foto", Toast.LENGTH_LONG)
                                                        .show();
                                                } else {
                                                    Toast.makeText(ProfileActivity.this, "Error en server", Toast.LENGTH_LONG)
                                                        .show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(final Call<ResponseBody> call, final Throwable t) {
                                                progressDialog.dismiss();
                                                Toast.makeText(ProfileActivity.this, "OnFailure - " + t, Toast.LENGTH_LONG)
                                                    .show();
                                            }
                                        });
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
//        builder.setView(inflater.inflate(R.layout.country_dialog, null))
//            // Add action buttons
//        loadCountries();
        final Spinner spinner = loadCountries();
        builder.setView(spinner)
            .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    saveCountry(spinner.getSelectedItem().toString());
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

    private Spinner loadCountries() {
        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for (Locale loc : locale) {
            country = loc.getDisplayCountry();
            if (country.length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        Spinner citizenship = new Spinner(ProfileActivity.this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countries);
        citizenship.setAdapter(adapter);
        if (!"null".equals(getSavedCountry())) {
            citizenship.setSelection(adapter.getPosition(getSavedCountry()));
        }
        return citizenship;
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
                        Toast.makeText(ProfileActivity.this, "Se actualizó con éxito el usuario", Toast.LENGTH_LONG)
                            .show();
                        setNameAndEmail(name, email);
                        nameTextView.setText(name);
                        emailTextView.setText(email);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Hubo un error actualizando el usuario", Toast.LENGTH_LONG)
                            .show();
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

    private void saveCountry(final String country) {
        if (!"null".equals(auth_token_string)) {
            UsersService usersService = TokenGenerator.createService(UsersService.class, auth_token_string);
            User modifiedUser = new User();
            modifiedUser.setCountry(country);
            Call<ResponseBody> updateUser = usersService.modifyUserMe(modifiedUser);
            final ProgressDialog pd = new ProgressDialog(ProfileActivity.this);
            pd.setMessage("Actualizando ubicación...");
            pd.show();
            updateUser.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Se actualizó con éxito la ubicación", Toast.LENGTH_LONG)
                            .show();
                        SharedPreferences settingsId = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = settingsId.edit();
                        editor.putString("country", country);
                        editor.commit();
                        editLocationTextView.setText("  Ubicación: " + country);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Hubo un error actualizando el usuario", Toast.LENGTH_LONG)
                            .show();
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

    private void saveBirthday(final String birthday) {
        if (!"null".equals(auth_token_string)) {
            UsersService usersService = TokenGenerator.createService(UsersService.class, auth_token_string);
            User modifiedUser = new User();
            String myFormat = "yyyy/MM/dd"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            final DateFormat originalformat = new SimpleDateFormat("dd/MM/yyyy");
            final DateFormat targetFormat = new SimpleDateFormat("yyyy/MM/dd");
            try {
                Date dateObj = originalformat.parse(birthday);
                modifiedUser.setBirthdate(targetFormat.format(dateObj));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Call<ResponseBody> updateUser = usersService.modifyUserMe(modifiedUser);
            final ProgressDialog pd = new ProgressDialog(ProfileActivity.this);
            pd.setMessage("Actualizando fecha de nacimiento...");
            pd.show();
            updateUser.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Se actualizó con éxito el nacimiento", Toast.LENGTH_LONG)
                            .show();
                        SharedPreferences settingsId = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = settingsId.edit();
                        Date dateObj = null;
                        try {
                            dateObj = originalformat.parse(birthday);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        editor.putString("birthday", targetFormat.format(dateObj));
                        editor.commit();
                        updateBirthday();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Hubo un error actualizando el usuario", Toast.LENGTH_LONG)
                            .show();
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
