package com.fiuba.apredazzi.tp_taller2_android.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.fiuba.apredazzi.tp_taller2_android.BaseActivity;
import com.fiuba.apredazzi.tp_taller2_android.R;

public class ProfileActivity extends BaseActivity {

    private ImageView editNameButton;
    private TextView editLocationTextView;
    private TextView nameTextView;
    private TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_profile, null, false);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        frameLayout.addView(contentView);

        nameTextView = (TextView) findViewById(R.id.name_profile);
        emailTextView = (TextView) findViewById(R.id.email_profile);

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
    }

    public void nameEmailDialg() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        builder.setTitle("Editar");
        View view = inflater.inflate(R.layout.name_profile_dialog, null);
        builder.setView(view)
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

        EditText old_name = (EditText) view.findViewById(R.id.name_dialog);
        old_name.setText(nameTextView.getText());

        EditText old_email = (EditText) view.findViewById(R.id.email_dialog);
        old_email.setText(emailTextView.getText());

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
}
