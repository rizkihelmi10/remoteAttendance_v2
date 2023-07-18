package com.smartlab.remoteattendance_v2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LocationRequestkey extends AppCompatActivity {

    MySQLiteHelper dbConfigHelper;
    dbActivities dba;
    String imei;
    String cardStatusString;
    String adminID ="";
    Object stag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.locationrequestkey);


        dba = new dbActivities();
        dbConfigHelper = new MySQLiteHelper(this);

        EditText LocName = (EditText) findViewById(R.id.edtxtLocReqKey);
        String AndroidName = Settings.Secure.getString(getContentResolver(), "bluetooth_name");
        imei = getIMEIDeviceId(getApplicationContext());

        LocName.setText(AndroidName);

        checkLocationCreator();

        addListenerOnButton();

    }

    public void populate_dropdown_userlist(){

    }
    public void addListenerOnButton() {

        Button btnUserRequestLoc = (Button) findViewById(R.id.btnRequestKey);


        btnUserRequestLoc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                sendrequest();

                AlertDialog.Builder builder1 = new AlertDialog.Builder(LocationRequestkey.this);
                builder1.setTitle("LOCATION KEY REQUEST");
                builder1.setMessage("Your request was sent successfully. Please check your email for the activation key. Click OK to proceed.");
                builder1.setCancelable(false);

                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                goToReq();
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });

    }

    public int checkLocationCreator() {

        int response = 3;

        String strCurrentLine = "";
        String Svr = "";
        String token = "";
        String urlAddress = "";
        String chksum;

        String encUid;

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

                urlAddress = server +"/websvc/CheckLocCreator.wp";

                Log.i("uriCheckLoc", urlAddress);

                URL url = new URL(urlAddress);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                chksum = DES.SHA1(uid.replaceAll("\\n",""));
                encUid = encryption(uid.replaceAll("\\n",""));

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("JWT", encUid);
                jsonParam.put("CS", chksum);

                Log.i("JWT Loc status JSON", jsonParam.toString());
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(jsonParam.toString());
                os.flush();
                os.close();

                Log.i("JWT STATUS Loc status", String.valueOf(conn.getResponseCode()));
                String temp = "";

                BufferedReader br = null;
                if (conn.getResponseCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((strCurrentLine = br.readLine()) != null) {
                        temp = strCurrentLine;
                    }

                    JSONObject jObject = new JSONObject(temp);

                    String aJsonStatus = jObject.getString("STATUS");

                    if (aJsonStatus.contains("1")) {
                        response = 1;
                        Log.i("bol", temp);
                    }
                    else if(aJsonStatus.contains("0")){

                        List<String> allNames = new ArrayList<String>();
                        List<StringWithTag> list = new ArrayList<StringWithTag>();
                        JSONArray cast = jObject.getJSONArray("DATA");
                        for (int i=0; i<cast.length(); i++) {
                            JSONObject adminlist = cast.getJSONObject(i);
                            String userid = adminlist.getString("USERID");
                            String nick = adminlist.getString("NICK");
                            Log.i("JWT admin i =" + i,userid + " NICK :" + nick);
                            allNames.add(userid);
                            list.add(new StringWithTag(nick,userid));

                        }


                        Spinner spinHttp = (Spinner) findViewById(R.id.ddlSelectAdmin);

                     /*   ArrayAdapter<String> adapterHttp = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item, allNames);*/
                        ArrayAdapter<StringWithTag> adapterHttp = new ArrayAdapter<StringWithTag>(this,
                                android.R.layout.simple_spinner_item, list);

                        adapterHttp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                        spinHttp.setAdapter(adapterHttp);
                        spinHttp.setSelection(0);


                        spinHttp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                //adminID = spinAdmin.getSelectedItem().toString();
                                //adminID=adapterView.getItemAtPosition(i).toString();
                                StringWithTag s = (StringWithTag) spinHttp.getItemAtPosition(i);
                                stag = s.tag;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }


                        });


                        response = 0;
                    }
                    else{
                        response = 2;

                    }
                }

                Log.i("JWT Loc status response", temp);
                conn.disconnect();


            }

        } catch (Exception e) {
            String error = "";
            for (StackTraceElement elem : e.getStackTrace()) {
                error += elem.toString();
            }
            Log.e("JWT Probs", error);

        }

        return response;
    }

    public String encryption(String strNormalText){

        String normalTextEnc="";
        try {

            CryptLib _crypt = new CryptLib();
            String output= "";
            String key = CryptLib.SHA256("888.8", 32); //32 bytes = 256 bit
            String iv = "lobster997";
            //String iv = CryptLib.generateRandomIV(16); //16 bytes = 128 bit
            normalTextEnc = _crypt.encrypt(strNormalText, key, iv); //encrypt
            Log.i("Workplace token",output);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("Workplace token", e.getMessage());
        }
        return normalTextEnc;
    }

    public void goToReq()
    {
        //user page
        Intent intent = new Intent(this, InitLocationManagement.class);
        startActivity(intent);
        this.finish();
    }

    public int sendrequest(){

        int response = 3;

        String strCurrentLine = "";
        String urlAddress = "";
        String sLoc = "";

        EditText LocName = (EditText) findViewById(R.id.edtxtLocReqKey);

        Spinner spinAdmin = (Spinner) findViewById(R.id.ddlSelectAdmin);

        sLoc = LocName.getText().toString();

       // String Text = String.valueOf(spinAdmin.getSelectedItem());
        String convertedToString = String.valueOf(stag);
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

                urlAddress = server +"/websvc/RequestLocKey.wp";

                Log.i("uriRequestLocKey", urlAddress);

                URL url = new URL(urlAddress);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("USERID", uid);
                jsonParam.put("LOCNAME", sLoc);
                jsonParam.put("ADMINUSERID",convertedToString);

                Log.i("JWT Loc status JSON", jsonParam.toString());
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(jsonParam.toString());
                os.flush();
                os.close();

                Log.i("JWT RequestLocKey STAT", String.valueOf(conn.getResponseCode()));
                String temp = "";

                BufferedReader br = null;
                if (conn.getResponseCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((strCurrentLine = br.readLine()) != null) {
                        temp = strCurrentLine;
                    }


                    if (temp.contains("1")) {
                        response = 1;
                        Log.i("bol", temp);
                    }


                }

                Log.i("JWT RequestLocKey response", temp);
                conn.disconnect();


            }

        } catch (Exception e) {
            String error = "";
            for (StackTraceElement elem : e.getStackTrace()) {
                error += elem.toString();
            }
            Log.e("JWT RequestLocKey Probs", error);

        }

        return response;
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

    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    public class StringWithTag {
        public String string;
        public Object tag;

        public StringWithTag(String stringPart, Object tagPart) {
            string = stringPart;
            tag = tagPart;
        }

        @Override
        public String toString() {
            return string;
        }
    }
}
