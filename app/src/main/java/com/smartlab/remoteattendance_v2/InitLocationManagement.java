package com.smartlab.remoteattendance_v2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import static com.smartlab.remoteattendance_v2.Config.getIMEIDeviceId;

public class InitLocationManagement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.initlocationmanagement);


        addListenerOnButton();

    }

    public void addListenerOnButton() {

        Button btnUserRequestLoc = (Button) findViewById(R.id.btnUserRequestLoc);
        Button btnUserHaveLocKey = (Button) findViewById(R.id.btnUserHaveLocKey);
        Button btnGoToGuest = (Button) findViewById(R.id.btnLocGuest);

        btnUserRequestLoc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                goToReguest();

            }
        });

        btnUserHaveLocKey.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                goToRegister();

            }
        });

        btnGoToGuest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                AlertDialog alertDialog = new AlertDialog.Builder(InitLocationManagement.this).create();
                alertDialog.setTitle("GUEST MODE");
                alertDialog.setMessage("Please select OK to log in as a GUEST.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                LoginAsGuest();
                                dialog.dismiss();
                            }
                        });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            }
        });

    }

    public void goToReguest()
    {
        //Intent intent = new Intent(this, MainActivity.class);

        //admin page
        //Intent intent = new Intent(this, LocationManagement.class);

        //user page
        Intent intent = new Intent(this, LocationRequestkey.class);
        startActivity(intent);
        this.finish();
    }

    public void goToRegister()
    {
        //Intent intent = new Intent(this, MainActivity.class);

        //admin page
        //Intent intent = new Intent(this, LocationManagement.class);

        //user page
        Intent intent = new Intent(this, LocationUserRegister.class);
        startActivity(intent);
       this.finish();
    }

    public void LoginAsGuest()
    {
        //Intent intent = new Intent(this, MainActivity.class);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("guest", "1");
        startActivity(intent);
        this.finish();
    }


}
