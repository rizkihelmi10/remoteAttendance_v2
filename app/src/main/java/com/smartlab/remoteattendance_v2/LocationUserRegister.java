package com.smartlab.remoteattendance_v2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.provider.Settings;
import android.provider.Settings.System;

//import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

import static com.smartlab.remoteattendance_v2.Config.getIMEIDeviceId;

public class LocationUserRegister extends AppCompatActivity {

    MySQLiteHelper dbConfigHelper;
    dbActivities dba;

    String sfromMain;
    int primary;
    String AndroidName;
    String token;


    private static String imei = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.locationuserregister);

        dba = new dbActivities();
        dbConfigHelper = new MySQLiteHelper(this);


        final String server = dba.getConfigValue(dbConfigHelper, "server",
                "active='1'");
        final String uid = dba.getConfigValue(dbConfigHelper, "username",
                "active='1'");

        final String pwd = dba.getConfigValue(dbConfigHelper, "password",
                "active='1'");

        EditText LocName = (EditText) findViewById(R.id.edtxtPhoneNameUser);
        String androidID = System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        imei = getIMEIDeviceId(getApplicationContext());

        AndroidName = Settings.Secure.getString(getContentResolver(), "bluetooth_name");

        LocName.setText(AndroidName.replaceAll("'",""));

        addListenerOnButton();

    }


    public String verifyLocationCreator(String locationKey, String LocationName) {

        String strCurrentLine = "";
        boolean bRetBol = false;
        String sTOken = "";
        //sever info
        String Svr = "";
        String domain = "";

        String chksum;
        String rawData = "";
        String encData = "";
        String deviceID = "";

        try {

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                final String server = dba.getConfigValue(dbConfigHelper, "server",
                        "active='1'");
                final String uid = dba.getConfigValue(dbConfigHelper, "username",
                        "active='1'");

                final String pwd = dba.getConfigValue(dbConfigHelper, "password",
                        "active='1'");

                TextView AssignTo = (TextView) findViewById(R.id.edtxtLocAssing);
                TextView PhoneID = (TextView) findViewById(R.id.edtxtPhoneID);

                Svr = server + "/websvc/LocReg.wp";

                URL url = new URL(Svr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                deviceID = imei;

                if (deviceID == null || deviceID.length() == 0) {
                    deviceID = "0000-0000";
                }

                rawData = uid + "||" + pwd + "||" + deviceID +"||" + LocationName + "_" + uid + "||" + locationKey + "||Android";
                encData = encryption(rawData);
                chksum = DES.SHA1(rawData);
                Log.i("Raw Data : ", uid + "||" + pwd + "||" + deviceID + "||" + LocationName + "_" + uid + "||" + locationKey + "||Android");

                JSONObject jsonParam = new JSONObject();

                jsonParam.put("JWT", encData);
                jsonParam.put("CS", chksum);

                Log.i("JWT request Loc stat : ", jsonParam.toString());
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(jsonParam.toString());
                os.flush();
                os.close();

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                String temp = "";

                BufferedReader br = null;
                if (conn.getResponseCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((strCurrentLine = br.readLine()) != null) {
                        temp = strCurrentLine;
                    }
                }

                Log.i("JWT Loc RESPONSE", temp);
                conn.disconnect();
                JSONObject jObject = new JSONObject(temp);
                String aJsonString = jObject.getString("JWT");
                String aJsonStatus = jObject.getString("STATUS");
                String decData = Decryption(aJsonString);


                Log.i("JWT Decypt", aJsonString);
                Log.i("JWT RAw", decData);

                if (aJsonStatus.equalsIgnoreCase("SUCCESS")) {
                    bRetBol = true;
                    sTOken = aJsonString;
                } else
                    showToast("Invalid location key");

            }

        } catch (Exception e) {
            String error = "";
            for (StackTraceElement elem : e.getStackTrace()) {
                error += elem.toString();
            }
            Log.e("Probs", error);

        }

        return sTOken;
    }

    public String encryption(String strNormalText) {

        String normalTextEnc = "";
        try {

            CryptLib _crypt = new CryptLib();
            String output = "";
            String key = CryptLib.SHA256("888.8", 32); //32 bytes = 256 bit
            String iv = "lobster997";
            normalTextEnc = _crypt.encrypt(strNormalText, key, iv); //encrypt
            Log.i("Workplace token", output);

            output = _crypt.decrypt(output, key, iv); //decrypt
            Log.i("Workplace decrypted token", output);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("Workplace token", e.getMessage());
        }
        return normalTextEnc;
    }

    public String Decryption(String strNormalText) {

        String normalTextEnc = "";
        try {

            CryptLib _crypt = new CryptLib();
            String output = "";
            String key = CryptLib.SHA256("888.8", 32); //32 bytes = 256 bit
            String iv = "lobster997";
            //String iv = CryptLib.generateRandomIV(16); //16 bytes = 128 bit
            normalTextEnc = _crypt.decrypt(strNormalText, key, iv); //decrypt
            Log.i("Workplace decrypted token", output);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("Workplace token", e.getMessage());
        }
        return normalTextEnc;
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public void goToMain()
    {
        //Intent intent = new Intent(this, MainActivity.class);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }


    public void addListenerOnButton() {

        Button btnRegister = (Button) findViewById(R.id.btnUserRegisterLoc);
        Button btnReset = (Button) findViewById(R.id.btnUserLocReSET);
        Button btnCancel = (Button) findViewById(R.id.btnUserLocCancel);

        EditText LocKey = (EditText) findViewById(R.id.edtxtLocKeyUser);
        EditText LocName = (EditText) findViewById(R.id.edtxtPhoneNameUser);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {


                String strUserName = LocKey.getText().toString();

                if(TextUtils.isEmpty(strUserName)) {
                    LocKey.setError("Please insert your location key");
                    return;
                }

                if(TextUtils.isEmpty(strUserName)) {
                    LocKey.setError("Please insert your location key");
                    return;
                }
                token = verifyLocationCreator(LocKey.getText().toString(),LocName.getText().toString());

                if(token.isEmpty() != true || token.length() != 0){

                    AlertDialog alertDialog = new AlertDialog.Builder(LocationUserRegister.this).create();
                    alertDialog.setTitle("LOCATION KEY");
                    alertDialog.setMessage("You have successfully submitted valid location key. Click OK to proceed to the Main Menu.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if(dba.UpdateLocationManagement(dbConfigHelper,1,true,LocName.getText().toString(),LocKey.getText().toString(),token)== true){
                                        goToMain();
                                    }
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    ;
                }
                else{
                    AlertDialog alertDialog = new AlertDialog.Builder(LocationUserRegister.this).create();
                    alertDialog.setTitle("INVALID LOCATION KEY");
                    alertDialog.setMessage("You have entered invalid location key. Please ensure to enter the correct location key and try again.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                LocKey.setText("");
                LocName.setText(AndroidName);
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {


                AlertDialog alertDialog = new AlertDialog.Builder(LocationUserRegister.this).create();
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

    public void LoginAsGuest()
    {
        //Intent intent = new Intent(this, MainActivity.class);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("guest", "1");
        startActivity(intent);
        this.finish();
    }

    public static String getIMEIDeviceId(Context context) {

        String deviceId;

        if (Build.VERSION.SDK_INT >= 29)
        {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return "";
                }
            }
            assert mTelephony != null;
            if (mTelephony.getDeviceId() != null)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    deviceId = mTelephony.getImei();
                }else {
                    deviceId = mTelephony.getDeviceId();
                }
            } else {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        }
        Log.d("deviceId", deviceId);
        return deviceId;
    }

    public static String getUniquePsuedoID() {
        // If all else fails, if the user does have lower than API 9 (lower
        // than Gingerbread), has reset their device or 'Secure.ANDROID_ID'
        // returns 'null', then simply the ID returned will be solely based
        // off their Android device information. This is where the collisions
        // can happen.
        // Thanks http://www.pocketmagic.net/?p=1662!
        // Try not to use DISPLAY, HOST or ID - these items could change.
        // If there are collisions, there will be overlapping data
        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

        // Thanks to @Roman SL!
        // https://stackoverflow.com/a/4789483/950427
        // Only devices with API >= 9 have android.os.Build.SERIAL
        // http://developer.android.com/reference/android/os/Build.html#SERIAL
        // If a user upgrades software or roots their device, there will be a duplicate entry
        String serial = null;
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();

            // Go ahead and return the serial for api => 9
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            // String needs to be initialized
            serial = "serial"; // some value
        }

        // Thanks @Joe!
        // https://stackoverflow.com/a/2853253/950427
        // Finally, combine the values we have found by using the UUID class to create a unique identifier
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

}
