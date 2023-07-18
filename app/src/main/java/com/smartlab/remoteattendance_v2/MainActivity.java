package com.smartlab.remoteattendance_v2;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.fingerprint.FingerprintManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.http.util.EntityUtils;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import android.content.BroadcastReceiver;
import android.os.AsyncTask;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        ResultCallback<Status> {

    private LocationRequest locationRequest;
    private MyBroadcastReceiver myBroadcastReceiver;
    private static final int REQUEST_CODE_PERMISSION = 123;
    Button btnTakePhoto;
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private ActivityResultLauncher<Intent> cameraLauncher;
    Camera camera;
    Uri image_uri;
    private Camera mCamera;
    public static final int CAMERA_REQUEST = 9998;
    private static final int pic_id = 123;
    private Marker currentMarker;
    double latitude;
    double longitude;
    ImageView imageView;
    private Marker geofenceMarker;
    public static final int CAMERA_REQUEST_B = 9999;
    public float GEOFENCE_RADIUS = 100.0f; // in meters
    public float float_no;

    ProgressDialog progressBar;

    private int progressBarStatus = 0; // setting progress bar to zero
    private Handler progressBarHandler = new Handler(); // object for handlemyWebClientr class
    private long progress = 0;
    String imei;

    int primary;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 200 * 10 * 1; // 2 seconds

    String imgPath;
    String fPath;

    private boolean cameraFront = false;
    Button btnLocation;
    TextView txtLocation;
    TextView txtLocation2;
    EditText txtRemark;

    TextView textView10;
    CheckBox checkBox7;
    CheckBox checkBox8;
    Button btnSubmit;

    public String Uid;
    public String Loc;
    public String Lat;
    public String Lon;
    public String Time;
    public String Date;
    public String Stat;
    public String Adress;

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    Marker marker;

    TextView Name;
    dbActivities dba;
    MySQLiteHelper dbConfigHelper;
    private KeyStore keyStore;
    private static final String KEY_NAME = "EDMTDev";
    private Cipher cipher;
    private TextView textView;
    private static String userString;
    // protected static CustomSharedPreference mPref;
    private static UserObject mUser;
    private SimpleDateFormat dateFormat;
    private String date;
    Context context;
    SQLiteDatabase sqLiteDatabase;
    TextView txtCheckIn1;
    Button btntime;
    TextView txtCheckOut;
    TextView textView6;
    TextView txtCheckOut2;
    String IOstat = "";
    String IOenable = "";
    String geoenable = "";
    Integer geoEnb = 0;
    AppLocationService appLocationService;

    Uri targetURI;

    byte[] imgFP;

    DBHelper mydb;

    private static final String TAG = MainActivity.class.getSimpleName();

    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private TextView textLat, textLong;

    private SupportMapFragment mapFragment;

    private PendingIntent GeofencePendingIntent;

    String res = "";
    private boolean isConnected = true;
    private static final String NOTIFICATION_MSG = "NOTIFICATION MSG";
    private GeofencingClient geofencingClient;
    private List<Geofence> geofenceList;

    // Create a Intent send by the notification
    public static Intent makeNotificationIntent(Context context, String msg) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(NOTIFICATION_MSG, msg);
        return intent;
    }

    private NetworkTask networkTask;
    private PendingIntent geofencePendingIntent;
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Boolean isConnected = false;
        isConnected = checkInternetConnection(this);
        dba = new dbActivities();
        dbConfigHelper = new MySQLiteHelper(this);
        checkConfigDB();


        if (isConnected == true) {

            // createGeofencePendingIntent();
           // myBroadcastReceiver = new MyBroadcastReceiver();

            //register BroadcastReceiver
            //  IntentFilter intentFilter = new IntentFilter(GeofenceTrasitionServiceJobIntent.ACTION_MyIntentService);
            //   intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            //   registerReceiver(myBroadcastReceiver, intentFilter);


            //initGMaps();
           /* createGoogleApi();
*/

            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        while (!isInterrupted()) {
                            Thread.sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView tdate = (TextView) findViewById(R.id.textView5);
                                    long date = System.currentTimeMillis();

                                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
                                    String dateString = sdf.format(date);

                                    tdate.setText(dateString);

                                    Button ttime = (Button) findViewById(R.id.btntime);
                                    ttime.setEnabled(false);

                                    long time = System.currentTimeMillis();
                                    SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss a");
                                    String dateString1 = sdf1.format(date);

                                    ttime.setText(dateString1);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                    }
                }
            };
            t.start();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


            mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map4);
            if (mapFragment == null) {
                mapFragment = SupportMapFragment.newInstance();
                fragmentTransaction.add(R.id.map4, mapFragment);
                fragmentTransaction.commit();
            }
            mapFragment.getMapAsync(this);


            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                    .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
            registerReceiver(new GeofenceBroadcastReceiver(), new IntentFilter("com.smartlab.remoteAttendance.GEOFENCE_BROADCAST_ACTION"));
            geofencingClient = LocationServices.getGeofencingClient(this);
            geofenceList = new ArrayList<>();
            // checkGPSStatus();


            GpsTracker gt = new GpsTracker(getApplicationContext());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            btnTakePhoto = findViewById(R.id.btnTakePhoto);
            imageView = findViewById(R.id.imageView2);
            Button imageBack = findViewById(R.id.imgBack);
            btnLocation = findViewById(R.id.button3);
            Name = findViewById(R.id.tvName);
            txtLocation2 = findViewById(R.id.txtLocation8);
            final Button btnAuth = findViewById(R.id.btnAuthorized);

            TextView ip = (TextView) findViewById(R.id.txtIP);

            String ipAdd = getLocalIpAddress();

            ip.setText("IP : " + ipAdd);

            mydb = new DBHelper(this);

            textView10 = findViewById(R.id.textView10);
            checkBox7 = findViewById(R.id.checkBox7);
            checkBox8 = findViewById(R.id.checkBox8);
            btnSubmit = findViewById(R.id.btnSubmit);
            Button btnBack2 = (Button) findViewById(R.id.buttonbck);
            btnBack2.setTypeface(Typeface.SANS_SERIF);
            textView10.setText("CHECK IN");

            final Button btntime = (Button) findViewById(R.id.btntime);
            final TextView txtCheckOut = (TextView) findViewById(R.id.txtCheckOut);
            textView6 = (TextView) findViewById(R.id.textView6);
            txtCheckOut2 = (TextView) findViewById(R.id.txtCheckOut2);

            btnAuth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkGPSStatus();
                    if (geoEnb == 1) {

                        //  getFenceLoc();
                    } else {
                        //  showToast("didn't have geoenable");
                    }
                }
            });

            btnBack2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ((imageView.getDrawable().getConstantState() != getResources().getDrawable(R.drawable.circular).getConstantState() == true)) {
                        AlertDialogReset();
                    }

                }
            });
            imageBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TextView name = (TextView) findViewById(R.id.tvName);
                    String name2 = name.getText().toString();

                    txtLocation = findViewById(R.id.textView11); //location address
                    ImageView profileImage = (ImageView) findViewById(R.id.imageView2); //image
                    TextView ip = (TextView) findViewById(R.id.txtIP);// ipadress

                    //  if ((profileImage.getDrawable().getConstantState() != getResources().getDrawable(R.drawable.dfaultprofile).getConstantState()==true) && (txtLocation2.getText().toString() != "None") && (txtLocation.getText().toString() !="None") && (ip.getText().toString() != "None"))
                    if ((profileImage.getDrawable().getConstantState() != getResources().getDrawable(R.drawable.vrtwo).getConstantState() == true)) {
                        if (geoEnb == 1) {
                            if (btnAuth.getText().toString().contains("UNAUTHORISED LOCATION")) {
                                showToast("This location is not authorised");
                            } else {
                                AlertDialogFPsend();
                            }

                        } else {
                            AlertDialogFPsend();
                        }

                        //showToast("No photo detected");
                    } else {
                        showToast("Please take photo before clicking submit");
                    }
                }
            });



            //------------------------ start POPULATE JSON
            dba = new dbActivities();
            dbConfigHelper = new MySQLiteHelper(this);

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                String jSONres = "";

                String[] config = dba.getResultConfig(dbConfigHelper, "*", "active='1'");
                String usr = config[1];
                String server = config[3];
                String domain = server.replace("/workplace", "");

                String sLat = "";
                String sLon = "";
                String sRange = "";

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("UID", usr);
                jsonParam.put("IOENABLE", 0);
                jsonParam.put("GEOENABLE", 0);
                jsonParam.put("RANGE",0);

                //showToast(jsonParam.toString());

                jSONres = postJSON(domain + "/cloudtms/websvc/remote_check.asp", jsonParam);

                Log.i("cek json333 ", jSONres);

                JSONObject jsonObj = new JSONObject(jSONres);
                IOstat = jsonObj.getString("LASTSTATUS");

                IOenable = jsonObj.getString("IOENABLE");
                geoenable=jsonObj.getString("GEOENABLE");

                Log.i("range",sRange);

            } catch (JSONException e) {
                e.printStackTrace();

                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                Log.e("test err", errors.toString());
            }


            //------------------------ end JSON
            if (IOstat.equals("0")) {
                checkBox7.setChecked(false);
                checkBox8.setChecked(true);
                textView10.setText("CHECK OUT");

            } else {
                checkBox7.setChecked(true);
                checkBox8.setChecked(false);
                textView10.setText("CHECK IN");
            }
            if (IOenable.equals("0")) {
                checkBox7.setVisibility(View.GONE);
                checkBox8.setVisibility(View.GONE);
                textView10.setText("");
            } else {
                checkBox7.setVisibility(View.VISIBLE);
                checkBox8.setVisibility(View.VISIBLE);
                if (checkBox7.isChecked() == true) {
                    textView10.setText("CHECK IN");
                } else if (checkBox8.isChecked() == true) {
                    textView10.setText("CHECK OUT");
                }

            }
            if (geoenable.equals("0")) {
                geoEnb = 0;
                Toast.makeText(this, "no geofence", Toast.LENGTH_SHORT).show();
                btnAuth.setText("AUTHORISED LOCATION");
                btnAuth.setBackgroundColor(getResources().getColor(R.color.colorGreen));


            } else if (geoenable.equals("1")) {
                geoEnb = 1;

                btnAuth.setText("UNAUTHORISED LOCATION");
                btnAuth.setBackgroundColor(getResources().getColor(R.color.colorRed));

            }


            checkBox7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox7.isChecked()) {

                        textView10.setText("CHECK IN");
                        FingerPrintListener();
                        checkBox8.setChecked(false);
                        TextView txtCheckIn10 = (TextView) findViewById(R.id.txtCheckIn10);
                        txtCheckIn10.setText(btntime.getText().toString());
                        TextView textView6 = (TextView) findViewById(R.id.textView6);
                        TextView txtCheckOut2 = (TextView) findViewById(R.id.txtCheckOut2);
                        TextView txtCheckOut = (TextView) findViewById(R.id.txtCheckOut);
                        txtCheckOut.setText(txtCheckOut2.getText().toString());

                    } else {
                        checkBox8.setChecked(true);
                        textView10.setText("CHECK OUT");

                    }
                }
            });

            checkBox8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox8.isChecked()) {
                        FingerPrintListener();
                        textView10.setText("CHECK OUT");
                        checkBox7.setChecked(false);
                        txtCheckOut.setText(btntime.getText().toString());
                        TextView textView6 = (TextView) findViewById(R.id.textView6);
                        TextView txtCheckIn10 = (TextView) findViewById(R.id.txtCheckIn10);
                        txtCheckIn10.setText(textView6.getText().toString());

                    } else {
                        checkBox7.setChecked(true);
                        textView10.setText("CHECK IN");
                    }
                }
            });


            if (checkBox7.isChecked()) {
                checkBox8.setChecked(false);
            } else if (checkBox8.isChecked()) {
                checkBox7.setChecked(false);
            }
            Name = findViewById(R.id.tvName);

            dba = new dbActivities();
            dbConfigHelper = new MySQLiteHelper(this);
            final String uid = dba.getConfigValue(dbConfigHelper, "username",
                    "active='1'");
            TextView navUsername = (TextView) findViewById(R.id.tvName);
            navUsername.setText(uid);

           /* locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                                {Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            }

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                                {Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            }

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                                {Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            }*/
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

            /*        latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    LatLng latLng = new LatLng(latitude, longitude);
                    Log.d("longitude and latitude", "onLocationChanged: " + longitude + "   " + latitude);
                    mMap.clear();
                    *//*mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));*//*
                    MarkerOptions currentLocationMarkerOptions = new MarkerOptions()
                            .position(latLng)
                            .title("Current Location");
                    currentMarker = mMap.addMarker(currentLocationMarkerOptions);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                    txtLocation = findViewById(R.id.textView11);
                    txtLocation.setText("(" + String.valueOf(latitude) + ",\n" + String.valueOf(longitude) + ")");
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addresses =
                                geocoder.getFromLocation(latitude, longitude, 1);
                        String result = addresses.get(0).getLocality() + ":";
                        result += addresses.get(0).getCountryName();
                        latLng = new LatLng(latitude, longitude);

                        addresses = geocoder.getFromLocation(latitude, longitude, 1);

                        String fullAddr = "";
                        fullAddr = addresses.get(0).getAddressLine(0);
                        txtLocation2.setText(fullAddr);
                      //  FingerPrintListener();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

          /*  locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);*/

            LongLatitude();
            getTime();

        }else {
            showToast("no Internet Connection");
            txtLocation2.setText("");
            txtLocation.setText("");
        }

     //  FingerPrintListener();
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                handleCameraResult(data);
            } else {
                showToast("Camera NOK");
            }
        });
    }
    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        Log.d("successtho", "getGeofencePendingIntent: pass to that class");
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Log.d("successtho22", "getGeofencePendingIntent: pass to that class");
        return geofencePendingIntent;
    }
    public class NetworkTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String jSONres = "";

            try {

                String[] config = dba.getResultConfig(dbConfigHelper, "*", "active='1'");
                String usr = config[1];
                String server = config[3];
                String domain = server.replace("/workplace", "");

                String sLat = "";
                String sLon = "";
                String sRange = "";

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("UID", usr);
                jsonParam.put("IOENABLE", 0);
                jsonParam.put("GEOENABLE", 0);
                jsonParam.put("RANGE",0);

                //showToast(jsonParam.toString());

                jSONres = postJSON(domain + "/cloudtms/websvc/remote_check.asp", jsonParam);

                Log.i("cek json333 ", jSONres);

                JSONObject jsonObj = new JSONObject(jSONres);
                IOstat = jsonObj.getString("LASTSTATUS");

                IOenable = jsonObj.getString("IOENABLE");
                geoenable=jsonObj.getString("GEOENABLE");

                Log.i("range",sRange);

            } catch (JSONException e) {
                e.printStackTrace();

                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                Log.e("test err", errors.toString());
            }
            // Perform your network operations here
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            // Update the UI on the main thread if needed
        }
    }



    //-- get location
    private void getLoc(Location location){

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();


        if (latitude != 0) {
            //weiwen remark
//                        Button auth = (Button) findViewById(R.id.btnAuthorized);
//
//                        auth.setText("Authorized Location");
//                        auth.setBackgroundColor(getResources().getColor(R.color.colorGreen));
        }

        txtLocation = findViewById(R.id.textView11);
        txtLocation.setText("(" + String.valueOf(latitude) + ",\n" + String.valueOf(longitude) + ")");
        //get the location name from latitude and longitude
        Geocoder geocoder = new Geocoder(getApplicationContext());

        try {
            List<Address> addresses =
                    geocoder.getFromLocation(latitude, longitude, 1);
            String result = addresses.get(0).getLocality() + ":";
            result += addresses.get(0).getCountryName();
            LatLng latLng = new LatLng(latitude, longitude);

                  /*  StringBuilder sb = new StringBuilder();
                   if (addresses.size() > 0) {
                        Address address = addresses.get(0);
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                            sb.append(address.getAddressLine(i)).append(" ");
                        sb.append(address.getLocality()).append(" ");
                        sb.append(address.getPostalCode()).append(" ");
                        sb.append(address.getCountryName());

                    }
                    txtLocation2.setText(sb);*/

            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String fullAddr = "";
            fullAddr = addresses.get(0).getAddressLine(0);
            txtLocation2.setText(fullAddr);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Initialize GoogleMaps
    private void initGMaps() {
//        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map4);
//        mapFragment.getMapAsync(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map4);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }
    @Override
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);
        if (status.isSuccess()) {
            //saveGeofence();
            drawGeofence();
        } else {
            // inform about fail
        }
    }
    // Draw Geofence circle on GoogleMap
    private Circle geoFenceLimits;

    private void drawGeofence() {
        Log.d(TAG, "drawGeofence()");

		/*if (geoFenceLimits != null)
			geoFenceLimits.remove();*/

        //multipoint
        for (int i = 0; i < gfmarkr.size(); i++) {

            CircleOptions circleOptions = new CircleOptions()
                    .center(gfmarkr.get(i).getPosition())
                    //.center(position)
                    .fillColor(Color.rgb(152,251,152))
                    .strokeColor(Color.TRANSPARENT)
                    .strokeWidth(2)
                    .radius(float_no);

            //.radius(GEOFENCE_RADIUS);

            geoFenceLimits = map.addCircle(circleOptions);
        }

		/*CircleOptions circleOptions = new CircleOptions()
				.center(geoFenceMarker.getPosition())
				//.center(position)
				.fillColor(Color.rgb(152,251,152))
				.strokeColor(Color.TRANSPARENT)
				.strokeWidth(2)
				.radius(GEOFENCE_RADIUS);

		geoFenceLimits = map.addCircle(circleOptions);*/
    }

    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if (googleApiClient == null) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        // Call GoogleApiClient connection when starting the Activity
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected()");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request location updates
            locationRequest = new LocationRequest();
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(500);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            // getLatestLocation();
        }
        if(geoEnb==1){
          //  getFenceLoc();
           // drawGeofence();
        }else{
            // showToast("geofencing failed");
        }

    }

    public void getFenceLoc(){

        String[] config = dba.getResultConfig(dbConfigHelper, "*", "active='1'");
        String usr = config[1];

        String jSONres = "";
        String sLat = "";
        String sLon = "";
        String sRange = "";

        try {

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("UID", usr);
            String server = config[3];
            String domain = server.replace("/workplace", "");

            jSONres = postJSON(domain + "/cloudtms/websvc/remote_check.asp",jsonParam);
            Log.i("cek json " ,jSONres);


            JSONObject jsonObj = new JSONObject(jSONres);
            JSONArray geodata = jsonObj.getJSONArray("GEODATA");
            Log.i("geodata.length",""+geodata.length());
            //for(int i=0;i<geodata.length();i++){

            for(int i=0;i<geodata.length();i++){

                JSONObject subgeodata = geodata.getJSONObject(i);
								/*JSONArray characters = movie.getJSONArray("characters");
								for(int j=0;j<characters.length();j++){
									temp.add(characters.getString(j));
								}*/

                sLat = subgeodata.getString("LATITUDE");
                sLon = subgeodata.getString("LONGITUDE");
                sRange = subgeodata.getString("RANGE");
                float_no = Float.parseFloat(sRange);
                //temp.add(geodata.getString(i));


                float fLat = Float.parseFloat(sLat);
                float fLon = Float.parseFloat(sLon);
                Log.i("err loop","err"+i);

                LatLng position = new LatLng(fLat,fLon);

                markerForGeofence(position);

            }

           // startGeofence();

        } catch (JSONException e) {
            e.printStackTrace();

            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            Log.e("test err", errors.toString());
        }
    }
    //private static final float GEOFENCE_RADIUS = 100.0f; // in meters

    private String idd="Workplace";
    public void startGeofence() {
        //showToast("Detecting auth location ..");
        Log.i(TAG, "startGeofence()");
        if (geoFenceMarker != null) {
			/*Geofence geofence = createGeofence(geoFenceMarker.getPosition(), GEOFENCE_RADIUS);
			GeofencingRequest geofenceRequest = createGeofenceRequest(geofence);
			addGeofence(geofenceRequest);*/
            String name = "";

            //multipoint
            for (int i = 0; i < gfmarkr.size(); i++) {
                Geofence geofence = createGeofence(gfmarkr.get(i), GEOFENCE_RADIUS,idd+i);
                GeofencingRequest geofenceRequest = createGeofenceRequest(geofence);
                addGeofence(geofenceRequest);
                name = idd+i;
                Log.i(TAG, "startGeofenceC()" + gfmarkr.get(i) + " " + name);
            }
        } else {
            Log.e(TAG, "Geofence marker is null");
        }
    }
    private void addGeofence(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        if (checkPermission())
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(this);

    }
    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;
    private PendingIntent createGeofencePendingIntent() {
        Log.d(TAG, "createGeofencePendingIntent");

        if (geoFencePendingIntent != null)
            return geoFencePendingIntent;

        Intent intent = new Intent(this, GeofenceTrasitionService.class);
           /* return PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);*/

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            return PendingIntent.getService(
                    this,
                    0, intent,
                    PendingIntent.FLAG_IMMUTABLE);
        }
        else
        {
            return PendingIntent.getService(
                    this,
                    0,intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

    }


    public GeofencingRequest createGeofenceRequest(Geofence geofence) {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
    }
    private Geofence createGeofence(Marker latLng, float radius, String id) {
        Log.d(TAG, "createGeofence");
        return new Geofence.Builder()
                .setRequestId(id)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                //.setRequestId(id)
                //.setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setCircularRegion(latLng.getPosition().latitude, latLng.getPosition().longitude, radius)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT |Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(40000)
                .build();

    }
    private Marker geoFenceMarker;
    private ArrayList<Marker> gfmarkr = new ArrayList<Marker>();

    private void markerForGeofence(LatLng latLng) {
        Log.i(TAG, "markerForGeofence(" + latLng + ")");
        String title = latLng.latitude + ", " + latLng.longitude;
        // Define marker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.wpicon))
                .title(title);

        if (map != null) {
            // Remove last geoFenceMarker
			/*if (geoFenceMarker != null)
				geoFenceMarker.remove();*/

            geoFenceMarker = map.addMarker(markerOptions);

            gfmarkr.add(geoFenceMarker); //multiple point

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        myBroadcastReceiver = new MyBroadcastReceiver();

        //register BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(GeofenceTrasitionService.ACTION_MyIntentService);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String result = intent.getStringExtra(GeofenceBroadcastReceiver.EXTRA_KEY_OUT);

            Log.d("ayam",result);


            if (result.equals("1")){
                Button btnGetFence = findViewById(R.id.btnAuthorized);
                btnGetFence.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                btnGetFence.setText("AUTHORISED LOCATION");
            }
            else{

                Button btnGetFence = findViewById(R.id.btnAuthorized);
                btnGetFence.setBackgroundColor(getResources().getColor(R.color.colorRed));
                btnGetFence.setText("UNAUTHORISED LOCATION");
            }
        }



    }
    private void removeGeofenceDraw() {
        Log.d(TAG, "removeGeofenceDraw()");
        if (geoFenceMarker != null)
            geoFenceMarker.remove();
        if (geoFenceLimits != null)
            geoFenceLimits.remove();
    }
    // Get last known location
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation()");
        if (checkPermission()) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                Log.i(TAG, "LasKnown location. " +
                        "Long: " + lastLocation.getLongitude() +
                        " | Lat: " + lastLocation.getLatitude());

                writeLastLocation();
                startLocationUpdates();
            } else {

                Log.w(TAG, "No location retrieved yet");
                startLocationUpdates();
            }
        } else askPermission();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    getLastKnownLocation();

                } else {
                    // Permission denied
                    permissionsDenied();
                }
                break;
            }
        }
    }
    // App cannot work without the permissions
    private void permissionsDenied() {
        Log.w(TAG, "permissionsDenied()");
        // TODO close app and warn user
    }

    private final int REQ_PERMISSION = 999;
    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQ_PERMISSION
        );
    }
    private final int UPDATE_INTERVAL = 1000;
    private final int FASTEST_INTERVAL = 900;

    // Start location Updates
    private void startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
//
        if (checkPermission())
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

        @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        Log.d("longitude and latitude", "onLocationChanged: " + longitude + "   " + latitude);
        mMap.clear();
        /*mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));*/
        MarkerOptions currentLocationMarkerOptions = new MarkerOptions()
                .position(latLng)
                .title("Current Location");
        currentMarker = mMap.addMarker(currentLocationMarkerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        txtLocation = findViewById(R.id.textView11);
        txtLocation.setText("(" + String.valueOf(latitude) + ",\n" + String.valueOf(longitude) + ")");
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            List<Address> addresses =
                    geocoder.getFromLocation(latitude, longitude, 1);
            String result = addresses.get(0).getLocality() + ":";
            result += addresses.get(0).getCountryName();
            latLng = new LatLng(latitude, longitude);

            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String fullAddr = "";
            fullAddr = addresses.get(0).getAddressLine(0);
            txtLocation2.setText(fullAddr);
           // FingerPrintListener();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void writeActualLocation(Location location) {
//               textLat.setText("Lat: " + location.getLatitude());
////               textLong.setText("Long: " + location.getLongitude());
        //set marker coordinate
        markerLocation(new LatLng(location.getLatitude(), location.getLongitude()));


    }
    private Marker locationMarker;
    private void markerLocation(LatLng latLng) {
        Log.i(TAG, "markerLocation(" + latLng + ")");
        String title = latLng.latitude + ", " + latLng.longitude;
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);
        if (map != null) {

            if (locationMarker != null)
                locationMarker.remove();
            locationMarker = map.addMarker(markerOptions);
            //	float zoom = 16f; //zoom
            float zoom = 13f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            map.animateCamera(cameraUpdate);

        }
    }

    public String postJSON(String url,JSONObject object) {

        String Response = "";

        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
            httpPost.setHeader("Accept","application/json");

            StringEntity se = new StringEntity(object.toString());
            //se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(se);
            httpResponse = httpClient.execute(httpPost);


            httpEntity = httpResponse.getEntity();
            Response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Response;


    }

    public boolean checkInternetConnection(Context context) {

        ConnectivityManager con_manager = (ConnectivityManager)

                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (con_manager.getActiveNetworkInfo() != null
                && con_manager.getActiveNetworkInfo().isAvailable()
                && con_manager.getActiveNetworkInfo().isConnected()) {
            return true;
        }

        else

        {
            return false;
        }

    }

    public void getNetworkGPS(){
        Location nwLocation = appLocationService
                .getLocation(LocationManager.NETWORK_PROVIDER);

        if (nwLocation != null) {
            double latitude = nwLocation.getLatitude();
            double longitude = nwLocation.getLongitude();
            Toast.makeText(
                    getApplicationContext(),
                    "Mobile Location (NW): \nLatitude: " + latitude
                            + "\nLongitude: " + longitude,
                    Toast.LENGTH_LONG).show();
        } else {
            showToast("NETWORK");
        }
    }

    private void checkGPSStatus() {

        LocationManager locationManager = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if ( locationManager == null ) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex){}
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex){}
        if ( !gps_enabled && !network_enabled ){
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setMessage("You need to enable your GPS location");
            dialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //this will navigate user to the device location settings screen
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);

                }
            });
            AlertDialog alert = dialog.create();
            alert.show();
        }
    }

    @SuppressWarnings("deprecation")
    public String getRealPathFromURI(Uri contentUri) {
        String [] proj      = {MediaStore.Images.Media.DATA};
        Cursor cursor       = managedQuery( contentUri, proj, null, null,null);

        if (cursor == null) return null;

        int column_index    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);
    }


    public MainActivity() {
        this.context = context;

    }

    public void checking1(){
        TextView name=(TextView)findViewById(R.id.tvName);
        String name2=name.getText().toString();

        boolean checking=mydb.checkUser(name2);

        if (checking==true){
            Toast.makeText(MainActivity.this, "Name is inserted!!!!!!", Toast.LENGTH_SHORT).show();
        }else if (checking==false){
            Toast.makeText(MainActivity.this, "Name is insert......", Toast.LENGTH_SHORT).show();
        }

    }


    public void getTime() {
        TextView name = (TextView) findViewById(R.id.tvName);
        String name2 = name.getText().toString();
        TextView textView6 = (TextView) findViewById(R.id.textView6);
        String txtCheckIn4 = textView6.getText().toString();
        TextView txtCheckOut3 = (TextView) findViewById(R.id.txtCheckOut);
        String txtCheckOut4 = txtCheckOut3.getText().toString();

        //SELECT * FROM Table ORDER BY date(dateColumn) DESC Limit 1

       /* String[] config = mydb.getResultAtt("*", "date(name)","DESC Limit 1");

        showToast(config[0] + config[1] + config[2] + config[3]);




                        textView6.setText(config[1] + " "  + config[2]);*/
        //txtCheckOut2.setText(config[2] );






    }

    public void addTime(){
        TextView txtName=(TextView)findViewById(R.id.tvName);
        TextView txtCheckIn10=(TextView)findViewById(R.id.txtCheckIn10);
        TextView txtCheckOut=(TextView)findViewById(R.id.txtCheckOut);



        boolean isInserted = mydb.insertData(txtName.getText().toString(),
                txtCheckIn10.getText().toString(),
                txtCheckOut.getText().toString() );


        if(isInserted == true)
            Toast.makeText(getApplicationContext(),"Data Inserted",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(),"Data not Inserted",Toast.LENGTH_LONG).show();
    }

    public void setTime(String att){
        int x = 0;

        //name publi
        //Date    public static final String COLUMN_CheckIn = "CheckIn";
        //status    public static final String COLUMN_CheckOut = "CheckOut"

        x = mydb.numberOfRows();

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy ");
        String dateString = sdf.format(date);

        SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss a");
        String timeString = sdf1.format(date);

        boolean isInserted = mydb.insertData(dateString,
                timeString, att);

        if (isInserted){
            showToast("insert data success");
        }


    }

    public void getData(){
        ArrayList<String> theList = new ArrayList<>();
        Cursor data = mydb.getListContents();
        if(data.getCount() == 0){
            Toast.makeText(this, "There are no contents in this list!",Toast.LENGTH_LONG).show();
        }else{
            while(data.moveToNext()){
                //theList.add(data.getString(1));
                textView6.setText(data.getString(2));
                //theList.add(data.getString(2));
                //theList.add(data.getString(3));
                txtCheckOut2.setText(data.getString(3));
//            ListAdapter listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,theList);
//            listView.setAdapter(listAdapter);
            }
        }

    }
    public void updateData(){
        TextView name=(TextView)findViewById(R.id.tvName);
        String name2=name.getText().toString();
        TextView textView6=(TextView)findViewById(R.id.txtCheckIn10);
        String txtCheckIn4=textView6.getText().toString();
        TextView txtCheckOut3=(TextView)findViewById(R.id.txtCheckOut);
        String txtCheckOut4=txtCheckOut3.getText().toString();
        boolean updateData=mydb.updateContact(name2,txtCheckIn4,txtCheckOut4);

        if (updateData==true){
            Toast.makeText(MainActivity.this, "Data updated", Toast.LENGTH_SHORT).show();
        }else if (updateData==false){
            Toast.makeText(MainActivity.this, "Data not updated", Toast.LENGTH_SHORT).show();
        }
    }

    private void fingerprint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) !=
                    PackageManager.PERMISSION_GRANTED) {

                return;


            } else {

            }
            if (!fingerprintManager.isHardwareDetected()) {
//                Toast.makeText(this, "Fingerprint authentication permission not enable", Toast.LENGTH_SHORT).show();
//                ImageView imageView5 = (ImageView) findViewById(R.id.imageView5);
//
//                imageView5.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
////showToast("On");
//                        openCamera();
//                        //frontCamera();
//                    }
//                });

            } else {
                if (!fingerprintManager.hasEnrolledFingerprints())
                    Toast.makeText(this, "Register at least one fingerprint in Settings", Toast.LENGTH_SHORT).show();
                else {
                    if (!keyguardManager.isKeyguardSecure())
                        Toast.makeText(this, "Lock screen security not enabled in Settings", Toast.LENGTH_SHORT).show();
                    else
                        genKey();
                    //openCamera();

                    if (cipherInit()) {
                        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                        FingerprintHandler1 helper = new FingerprintHandler1(this);

                        helper.startAuthentication(fingerprintManager, cryptoObject);

                    }

                }

            }

        }else{
            ImageView imageView5 = (ImageView) findViewById(R.id.imageView5);

            imageView5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//showToast("On");
                    openCamera();

                    //frontCamera();
                }
            });
        }

    }




    private void fingerprint_syuk() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {

            return;


        } else {

        }
        if (!fingerprintManager.isHardwareDetected()) {
            Toast.makeText(this, "Fingerprint authentication permission not enable", Toast.LENGTH_SHORT).show();
            ImageView imageView5 = (ImageView) findViewById(R.id.imageView5);

            imageView5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//showToast("On");
                    openCamera();
                    //frontCamera();
                }
            });

        }else {
            if (!fingerprintManager.hasEnrolledFingerprints())
                Toast.makeText(this, "Register at least one fingerprint in Settings", Toast.LENGTH_SHORT).show();
            else {
                if (!keyguardManager.isKeyguardSecure())
                    Toast.makeText(this, "Lock screen security not enabled in Settings", Toast.LENGTH_SHORT).show();
                else
                    genKey();
                //openCamera();

                if (cipherInit()) {
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerprintHandler1 helper = new FingerprintHandler1(this);

                    helper.startAuthentication(fingerprintManager, cryptoObject);

                }

            }

        }

    }

    public void showToast(String strMsg)
    {
        Toast.makeText(this, strMsg, Toast.LENGTH_LONG).show();
    }

    private void LongLatitude(){
        GpsTracker gt = new GpsTracker(getApplicationContext());
        Location l = gt.getLocation();
        double lat;
        double lon;

        if( l == null){

            //getNetworkGPS();
            Toast.makeText(getApplicationContext(),"Please wait while we retrieve location ..",Toast.LENGTH_SHORT).show();

        }else {
            lat = l.getLatitude();
            lon = l.getLongitude();

            if (lat != 0){

                ///get using network provide
                //weiwen remark
//                Button auth = (Button)findViewById(R.id.btnAuthorized);
//                auth.setText("Authorized Location");
//                auth.setBackgroundColor(getResources().getColor(R.color.colorGreen));

            }

            txtLocation=findViewById(R.id.textView11);
            txtLocation.setText("(" +String.valueOf( lat )+ ",\n"+String.valueOf( lon )+")" );

            Geocoder gc = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = gc.getFromLocation(lat, lon, 1);

                String fullAddr = "";
                fullAddr = addresses.get(0).getAddressLine(0);
                txtLocation2.setText(fullAddr);
                /*StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                        sb.append(address.getAddressLine(i)).append(" ");
                    sb.append(address.getLocality()).append(" ");
                    sb.append(address.getPostalCode()).append(" ");
                    sb.append(address.getCountryName());
                    txtLocation2.append(sb);
                }*/
            }catch (IOException e) {
//                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Cannot get Address!",Toast.LENGTH_SHORT).show();
            }
        }
    }



    protected void onActivityResult(int requestcode,int result,Intent data){
        super.onActivityResult(requestcode, result, data);
        showToast("here");
        ImageView profileImage = (ImageView) findViewById(R.id.imageView2);
        //if (requestcode == pic_id && result == RESULT_OK) {
        if (result == RESULT_OK) {
            /*if (data == null) {
                // No data was returned from the camera activity
                // Handle this case accordingly

                Toast.makeText(this, "No data returned from camera", Toast.LENGTH_SHORT).show();
                return;}
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            byte[] pic1 = null;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            photo.compress(Bitmap.CompressFormat.PNG,100,stream);

            pic1 = stream.toByteArray();

            try {
                stream.flush();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Bitmap roundedPhoto = getRoundedBitmap(photo);
            profileImage.setImageBitmap(roundedPhoto);

            // Picture was taken successfully
            // Slide up the menu
            imgFP = pic1;

            // Show the sliding menu
            //  TextView slidingMenu = findViewById(R.id.slidingmenu);
            // slidingMenu.setVisibility(View.VISIBLE);*/
            try {
                showToast("Camera OK");
            }catch (Exception e) {
                Log.e("err Rizki", "onActivityResult: " + e);
            }
        }
        else
            showToast("Camera NOK");

        }


    private Bitmap getRoundedBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int diameter = Math.min(width, height);

        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);

        RectF rectF = new RectF(0, 0, diameter, diameter);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, (diameter - width) / 2, (diameter - height) / 2, paint);

        return output;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
    public void AlertDialogFPsend() {
        Boolean isConnected = false;
        isConnected = checkInternetConnection(this);

        if (isConnected) {
            final CharSequence[] items = {"Yes", "No"};
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                    this);
            builder.setTitle("Your attendance will be submitted. Do you wish to continue?");
            builder.setItems(items, new

                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {

                            if (item == 0) {

                                Name = findViewById(R.id.tvName);
                                txtLocation2 = findViewById(R.id.txtLocation8);
                                txtRemark =  findViewById(R.id.txtRemark);
                                TextView ipaddress = (TextView) findViewById(R.id.txtIP);

                                textView10 = findViewById(R.id.textView10);

                                if (textView10.getText().toString().equals("CHECK IN")) {
                                    Stat = "0";
                                } else if (textView10.getText().toString().equals("CHECK OUT")) {
                                    Stat = "1";
                                }

                                //  showToast("" + textView10 + " " + Stat );

                                long date = System.currentTimeMillis();

                                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
                                String dateString = sdf.format(date);

                                long time = System.currentTimeMillis();
                                SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss a");
                                String dateTime = sdf1.format(date);

                                txtLocation = findViewById(R.id.textView11);

                                String coor[] = null;
                                coor = txtLocation.getText().toString().split(",");

                                if (coor != null) {

                                    String[] splitCoor = null;

                                    Lat = coor[0];
                                    splitCoor = coor[1].split("\n");
                                    Lon = splitCoor[1];

                                }

                                Uid = Name.getText().toString();
                                Loc = txtLocation2.getText().toString();
                                Time = dateTime;
                                Date = dateString;
                                String ip = getLocalIpAddress();
                                String sRemark = txtRemark.getText().toString();;

//                            setTime(Stat);
                                // showToast("Loc" + )
                                // showToast(Uid + " " + Loc + " " + Lat + " " + Lon + " " + " " + Time + " " + Date + " " + imgPath + " Stat : " + Stat);

                                load_progressbarFP(Uid, Loc, Lat, Lon, Time, Date, Stat, imgFP, ip,sRemark);


                            } else {
                                // do nothing
                            }
                        }

                    });

            android.app.AlertDialog alerts = builder.create();
            alerts.show();
        }else{
            showToast("no Internet Connection");
        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public void AlertDialogR() {
        final CharSequence[] items = { "Yes", "No" };
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                this);
        builder.setTitle("Your attendance will be send. Do you wish to continue?");
        builder.setItems(items, new

                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (item == 0) {

                            Name=findViewById(R.id.tvName);
                            txtLocation2=findViewById(R.id.txtLocation8);

                            textView10 = findViewById(R.id.textView10);

                            if (textView10.getText().toString().equals("CHECK IN")){
                                Stat = "0";
                            }
                            else{
                                Stat = "1";
                            }

                            long date = System.currentTimeMillis();

                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
                            String dateString = sdf.format(date);


                            long time = System.currentTimeMillis();
                            SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss a");
                            String dateTime = sdf1.format(date);

                            txtLocation=findViewById(R.id.textView11);

                            String coor[] = null;
                            //  coor = txtLocation.getText().toString().split(",");

                            if (coor != null){

                                Lat = coor[0];
                                Lon = coor[1];
//weiwen remark
//                                Button auth = (Button)findViewById(R.id.btnAuthorized);
//
//                                auth.setText("Authorized Location");
//                                auth.setBackgroundColor(getResources().getColor(R.color.colorGreen));

                            }

                            Uid = Name.getText().toString();
                            Loc = txtLocation2.getText().toString();
                            Time = dateTime;
                            Date = dateString;

                            //showToast(Uid + " " + Loc + " " + Lat + " " + Lon + " " + " " + Time + " " + Date + " " + imgPath);
                            load_progressbar(Uid,Loc,Lat,Lon,Time,Date,Stat,imgPath);



                        } else {
                            // do nothing
                        }
                    }
                });

        android.app.AlertDialog alerts = builder.create();
        alerts.show();
    }

    public boolean load_progressbar(final String sUid,final String sLoc,final String sLat,final String sLon,final String sTime,final String sdate,final String sStat,final String fPath){


        // prepare for a progress bar dialog
        progressBar = new ProgressDialog(MainActivity.this);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please wait..it may take sometimes."); // set message in progressbar dialog
        //horizontal progress bar type of progress bar
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0); //set min value of progress bar
        progressBar.setMax(100); // set max value of progress bar
        progressBar.show(); // display progress bar
        //reset progress bar status
        progressBarStatus = 0;
        //reset progress
        progress = 0;
        new Thread(new Runnable() {
            public void run() {
                while (progressBarStatus < 100) {
                    // process some tasks
                    progressBarStatus = doSomeTasksFP();

                    // your computer is too fast, sleep 1 second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Update the progress bar
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                            if (progressBarStatus== 30){


                                uploadtoserver(primary,sUid,sLoc,sLat,sLon,sTime,sdate,sStat,fPath);

                            }
                        }
                    });
                }

                // Progress completed ?!?!,
                if (progressBarStatus >= 100) {
                    // sleep 2000 milliseconds, so that you can see the 100%
                    try {

                        Thread.sleep(2000);

                        //showToast("Data Berjaya Di MuatNaik");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // close the progress bar dialog
                    progressBar.dismiss();

                }
            }
        }).start();

        return true;
    }

    public int doSomeTasksFP() {

        int prog = 0;
        while (progress <= 1000000) {
            progress++;
            if (progress == 100000) {

                return 10;
            } else if (progress == 200000) {
                return 20;
            } else if (progress == 300000) {
                return 30;
            }
        }

        return 100;
    }

    /*public void uploadtoserverFP(int primary,String uid,String loc,String lat,String lon,String time,String date,String stat,byte[] k,String IP,String remark)  {

        String dothis;

        String[] config = dba.getResultConfig(dbConfigHelper, "*", "active='1'");


        String usr = "";
        String url3 = config[3];
        String domain = url3.replace("workplace","");

        String url = domain + "/Cloudtms/websvc/remote_att3.asp";
        //String url = domain + "/workplace/websvc/remote_att2.asp";
        // String url = domain + "/utility/assets/workplace/uploadnews.wp";

        //pic start
        //byte[] pic1 = k;


        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("username", uid));
        //  nameValuePairs.add((NameValuePair) new BasicNameValuePair("password", ""));
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("location", loc));
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("long", lon));
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("lat", lat));
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("time", time));
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("date", date));
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("status", stat));
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("image1", ""));
        //add ip
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("IPAddress", IP));
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("remark", remark));
        //nameValuePairs.add((NameValuePair) new BasicNameValuePair("NewsID", "99999"));
        // nameValuePairs.add((NameValuePair) new BasicNameValuePair("dothis", dothis));
        Log.i("uploadtoserver","before post");


        postNoImage(url, nameValuePairs,k,uid);

    }


    public void exitToMain()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }*/
    public void uploadtoserverFP(int primary, String uid, String loc, String lat, String lon, String time, String date, String stat, byte[] k, String IP, String remark) {

        String dothis;

        String[] config = dba.getResultConfig(dbConfigHelper, "*", "active='1'");
        String usr = "";
        String url3 = config[3];
        String domain = url3.replace("workplace", "");

        String url = domain + "/Cloudtms/websvc/remote_att3.asp";

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("username", uid));
        nameValuePairs.add(new BasicNameValuePair("location", loc));
        nameValuePairs.add(new BasicNameValuePair("long", lon));
        nameValuePairs.add(new BasicNameValuePair("lat", lat));
        nameValuePairs.add(new BasicNameValuePair("time", time));
        nameValuePairs.add(new BasicNameValuePair("date", date));
        nameValuePairs.add(new BasicNameValuePair("status", stat));
        nameValuePairs.add(new BasicNameValuePair("image1", ""));
        nameValuePairs.add(new BasicNameValuePair("IPAddress", IP));
        nameValuePairs.add(new BasicNameValuePair("remark", remark));

        Log.i("uploadtoserver", "before post");
        postNoImage(url, nameValuePairs, k, uid);
    }

    public void exitToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }



    public boolean load_progressbarFP(final String sUid,final String sLoc,final String sLat,final String sLon,final String sTime,final String sdate,final String sStat,final byte[] img1,final String IP,final String remark){


        // prepare for a progress bar dialog
        progressBar = new ProgressDialog(MainActivity.this);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please wait..it may take a while."); // set message in progressbar dialog
        //horizontal progress bar type of progress bar
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0); //set min value of progress bar
        progressBar.setMax(100); // set max value of progress bar
        progressBar.show(); // display progress bar
        //reset progress bar status
        progressBarStatus = 0;
        //reset progress
        progress = 0;
        new Thread(new Runnable() {
            public void run() {
                while (progressBarStatus < 100) {
                    // process some tasks
                    progressBarStatus = doSomeTasksFP();

                    // your computer is too fast, sleep 1 second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Update the progress bar
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                            if (progressBarStatus== 30){

                                uploadtoserverFP(primary,sUid,sLoc,sLat,sLon,sTime,sdate,sStat,img1,IP,remark);

                            }
                        }
                    });
                }

                // Progress completed ?!?!,
                if (progressBarStatus >= 100) {
                    // sleep 2000 milliseconds, so that you can see the 100%
                    try {

                        Thread.sleep(2000);

                        //showToast("Data Berjaya Di MuatNaik");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // close the progress bar dialog
                    progressBar.dismiss();

                }
            }
        }).start();

        return true;
    }

    public int doSomeTasks() {

        int prog = 0;
        while (progress <= 1000000) {
            progress++;
            if (progress == 100000) {

                return 10;
            } else if (progress == 200000) {
                return 20;
            } else if (progress == 300000) {
                return 30;
            }
        }

        return 100;
    }

    public void uploadtoserver(int primary,String uid,String loc,String lat,String lon,String time,String date,String stat,String fpath)  {

        String dothis;

        String[] config = dba.getResultConfig(dbConfigHelper, "*", "active='1'");


        String usr = "";
        String url3 = config[3];
        String domain = url3.replace("workplace","");

        String url = domain + "/workplace/websvc/remote_att3.asp";
        // String url = domain + "/utility/assets/workplace/uploadnews.wp";

        //pic start
        byte[] pic1 = null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imgPath = fpath;

        if (imgPath != null)
        {
            Bitmap bitmap = null;

            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inSampleSize = 2;
            bitmap  = BitmapFactory.decodeFile(imgPath,opt);


            bitmap.compress(Bitmap.CompressFormat.PNG,60,stream);

            pic1 = stream.toByteArray();

            try {
                stream.flush();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("username", uid));
        //  nameValuePairs.add((NameValuePair) new BasicNameValuePair("password", ""));
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("location", loc));
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("long", lon));
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("lat", lat));
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("time", time));
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("date", date));
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("status", stat));
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("image1", ""));
        //nameValuePairs.add((NameValuePair) new BasicNameValuePair("NewsID", "99999"));
        // nameValuePairs.add((NameValuePair) new BasicNameValuePair("dothis", dothis));
        Log.i("uploadtoserver","before post");
        // post(url, nameValuePairs,pic1,primary);
        postNoImage(url, nameValuePairs,pic1,uid);

    }

    public void frontCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//    Uri photoUri = Uri.fromFile(getOutputPhotoFile());
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        // startActivityForResult(intent, CAMERA_REQUEST);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    public void openCamera() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Camera permission granted, proceed with opening the camera
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Start the camera activity using the camera launcher
            cameraLauncher.launch(cameraIntent);
        } else {
            // Camera permission not granted, request it from the user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_PERMISSION);
        }


    }
    private void handleCameraResult(Intent data) {
        if (data == null || data.getExtras() == null) {
            // No data or extras were returned from the camera activity
            // Handle this case accordingly

            Toast.makeText(this, "No data returned from camera", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap photo = (Bitmap) data.getExtras().get("data");
        byte[] pic1 = null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);

        pic1 = stream.toByteArray();

        try {
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageView profileImage = findViewById(R.id.imageView2);
        Bitmap roundedPhoto = getRoundedBitmap(photo);
        profileImage.setImageBitmap(roundedPhoto);

        imgFP = pic1;

     //   showToast("Camera OK");
    }

    public void onMapReady(GoogleMap googleMap) {
        //mMap = googleMap;
        mMap = googleMap;
        LatLng location = new LatLng(5.3530475, 100.3073234);

        /*googleMap.addMarker(new MarkerOptions().position(location).title("AUTHORIZED LOCATON")).setIcon(markerIcon);*/
        MarkerOptions geofenceMarkerOptions = new MarkerOptions()
                .position(location)
                .title("Authoeized Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        geofenceMarker = mMap.addMarker(geofenceMarkerOptions);
        // Move the camera to the marker location
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "onMapClick(" + latLng + ")");

    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClickListener: " + marker.getPosition());
        return false;
    }
    @Override
    protected void onStop() {
        super.onStop();
//        locationManager.removeUpdates(locationListener);
        googleApiClient.disconnect();
    }

    private boolean cipherInit() {
//openCamera();textView5
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES+"/"+KeyProperties.BLOCK_MODE_CBC+"/"+KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey)keyStore.getKey(KEY_NAME,null);
            cipher.init(Cipher.ENCRYPT_MODE,key);
            //openCamera();
            return true;
        } catch (IOException e1) {

            e1.printStackTrace();
            return false;
        } catch (NoSuchAlgorithmException e1) {

            e1.printStackTrace();
            return false;
        } catch (CertificateException e1) {

            e1.printStackTrace();
            return false;
        } catch (UnrecoverableKeyException e1) {

            e1.printStackTrace();
            return false;
        } catch (KeyStoreException e1) {

            e1.printStackTrace();
            return false;
        } catch (InvalidKeyException e1) {

            e1.printStackTrace();
            return false;
        }

    }

    private void genKey() {
        try {
            //openCamera();
            keyStore = KeyStore.getInstance("AndroidKeyStore");

        } catch (KeyStoreException e) {
//            openCamera();
            e.printStackTrace();
//            openCamera();
        }

        KeyGenerator keyGenerator = null;
        //openCamera();
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
            // openCamera();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        try {
            keyStore.load(null);
            //openCamera();
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setUserAuthenticationRequired(true)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build()
                    // openCamera()
            );
            keyGenerator.generateKey();
            //openCamera();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        catch (InvalidAlgorithmParameterException e)
        {
            e.printStackTrace();
        }


    }

    public void post(String url, List<NameValuePair> nameValuePairs,byte[] pic1,int primary) {

        StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);

        // showToast("Upload to server");

        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);
        ByteArrayBody image1 = null;

        if (pic1 != null)
        {
            image1 = new ByteArrayBody(pic1, "imgNews");
        }

        try {

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            for(int index=0; index < nameValuePairs.size(); index++) {
                if(image1 != null && nameValuePairs.get(index).getName().equalsIgnoreCase("image1")) {
                    entity.addPart(nameValuePairs.get(index).getName(), image1);
                    Log.i(nameValuePairs.get(index).getName(), "image1");
                }
                else if(nameValuePairs.get(index).getValue() != null){
                    entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
                    Log.i(nameValuePairs.get(index).getName(), nameValuePairs.get(index).getValue());
                }
            }

            httpPost.setEntity(entity);
            Log.i("getRequestLine()" , httpPost.getRequestLine().toString());

            HttpResponse response = httpClient.execute(httpPost, localContext);

            HttpEntity resEntity = response.getEntity();

            Log.i("getStatusLine()",response.getStatusLine().toString());

            if (resEntity != null) {
                String res =  EntityUtils.toString(resEntity);

                Log.i("post : res",res);

                xmlParse(res,primary);

            }
            if (resEntity != null) {
                resEntity.consumeContent();
            }

            httpClient.getConnectionManager().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("post() ~ Exception",e.getClass().getName() + " : " + e.getMessage());
            Toast.makeText(
                    getApplicationContext(),
                    "Exception ~ post() :\n" + e.getClass().getName() + " \n" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void xmlParse(String result,int primary)
    {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput( new StringReader(result) );

            int eventType = xpp.getEventType();

            String dat = "";
            String info = "";
            String id = "";

            while (eventType != XmlPullParser.END_DOCUMENT)
            {

                if(eventType == XmlPullParser.START_DOCUMENT)
                {
                    Log.i("Start document", "start");
                }
                else if(eventType == XmlPullParser.START_TAG)
                {
                    Log.i("Start tag ",xpp.getName());
                    if (xpp.getName().equals("MSG"))
                    {
                        id = xpp.getName();
                    }
                }
                else if(eventType == XmlPullParser.END_TAG)
                {
                    Log.i("End tag ",xpp.getName());
                }
                else if(eventType == XmlPullParser.TEXT)
                {
                    if (id.equals("MSG"))
                    {
                        info = "Error updating data, "+xpp.getText();
                    }
                    else
                        dat = xpp.getText();
                }

                eventType = xpp.next();
            }
            Log.i("End document", "end");
            if ( dat != "" && dat != null)
            {
                //update db insert property id
                //dba.updateDB(dat, primary);
                //Toast.makeText(this, "Your attendance had been send successfully.", Toast.LENGTH_SHORT).show();
                //exitToMain();

            }
            else
                Toast.makeText(this, "Error updating data for attendance '" + dat + "' : \n" + info, Toast.LENGTH_SHORT).show();
            Log.e("data for NewsID '" + dat + "'.", info);
        }
        catch (Exception e1)
        {
            Log.e("xmlParse() ~ Exception", e1.getClass().getName() + " : " + e1.getMessage(), e1);

            Toast.makeText(
                    getApplicationContext(),
                    "Exception ~ xmlParse() : \n" + e1.getClass().getName() + " \n" + e1.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

    }

   /* public boolean postNoImage(String url, List<NameValuePair> nameValuePairs,byte[] pic1,String UserID) {

        boolean bFlag = false;

        StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);

        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);


        try {

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            ByteArrayBody image1 = null;

            if (pic1 != null)
            {
                image1 = new ByteArrayBody(pic1, "imgsyuk");


            }

            for(int index=0; index < nameValuePairs.size(); index++) {
                if(image1 != null && nameValuePairs.get(index).getName().equalsIgnoreCase("image1"))
                {
                    entity.addPart(nameValuePairs.get(index).getName(), image1);

                    // showToast("success load img : " + nameValuePairs.get(index).getName());
                }
                else if(nameValuePairs.get(index).getValue() != null){
                    entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
                    Log.i(nameValuePairs.get(index).getName(), nameValuePairs.get(index).getValue());
                }
            }

            httpPost.setEntity(entity);
            Log.i("getRequestLine()" , httpPost.getRequestLine().toString());

            HttpResponse response = httpClient.execute(httpPost, localContext);

            HttpEntity resEntity = response.getEntity();

            Log.i("getStatusLine()",response.getStatusLine().toString());

            if (resEntity != null) {

                String res = "";
                res = EntityUtils.toString(resEntity);

                // showToast(res);

                if (res.contains(String.valueOf(1))){

                    showToast("Loading.. Please wait..");
                    callViewAttLog();

                }
                else{
                    showToast("Process failed, please try again!");
                }

                Log.d("post : res",res);
                // showToast(res);

                //xmlParse(res,1);

            }

            if (resEntity != null)
                resEntity.consumeContent();

            httpClient.getConnectionManager().shutdown();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("post() ~ Exception",e.getClass().getName() + " : " + e.getMessage());
	            *//*Toast.makeText(
	                    getApplicationContext(),
	                    "Exception ~ post() :\n" + e.getClass().getName() + " \n" + e.getMessage(),
	                    Toast.LENGTH_LONG).show();*//*

            Toast.makeText(getApplicationContext(), "Failure connecting to '" + e.getMessage() + "'. \nPlease check your internet connection.", Toast.LENGTH_LONG).show();

            bFlag = false;
        }

        return bFlag;
    }*/
   public boolean postNoImage(String url, List<NameValuePair> nameValuePairs, byte[] pic1, String UserID) {
       boolean bFlag = false;

       try {
           StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
           StrictMode.setThreadPolicy(tp);

           // Create the connection
           URL connectionUrl = new URL(url);
           HttpURLConnection connection = (HttpURLConnection) connectionUrl.openConnection();
           connection.setRequestMethod("POST");
           connection.setDoOutput(true);

           // Create the multipart entity
           String boundary = "===" + System.currentTimeMillis() + "===";
           connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
           DataOutputStream request = new DataOutputStream(connection.getOutputStream());

           // Add form fields
           for (NameValuePair nameValuePair : nameValuePairs) {
               String name = nameValuePair.getName();
               String value = nameValuePair.getValue();
               request.writeBytes("--" + boundary + "\r\n");
               request.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
               request.writeBytes(URLEncoder.encode(value, "UTF-8") + "\r\n");
           }

           // Add image if available
           if (pic1 != null) {
               request.writeBytes("--" + boundary + "\r\n");
               request.writeBytes("Content-Disposition: form-data; name=\"image1\"; filename=\"imgsyuk\"\r\n");
               request.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
               request.write(pic1);
               request.writeBytes("\r\n");
           }

           request.writeBytes("--" + boundary + "--\r\n");
           request.flush();
           request.close();

           // Read the response
           int responseCode = connection.getResponseCode();
           if (responseCode == HttpURLConnection.HTTP_OK) {
               BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
               StringBuilder response = new StringBuilder();
               String line;
               while ((line = reader.readLine()) != null) {
                   response.append(line);
               }
               reader.close();

               String res = response.toString();
               Log.d("post : res", res);

               if (res.contains(String.valueOf(1))) {
                   showToast("Loading.. Please wait..");
                   callViewAttLog();
               } else {
                   showToast("Process failed, please try again!");
               }

               bFlag = true;
           } else {
               // Read the error stream and log the error message
               BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
               StringBuilder errorResponse = new StringBuilder();
               String errorLine;
               while ((errorLine = errorReader.readLine()) != null) {
                   errorResponse.append(errorLine);
               }
               errorReader.close();

               Log.e("post() ~ HTTP Error", "Error code: " + responseCode);
               Log.e("post() ~ Error Response", errorResponse.toString());

               showToast("Failure connecting to the server. Please check your internet connection.");
           }

           connection.disconnect();
       } catch (Exception e) {
           e.printStackTrace();
           Log.e("post() ~ Exception", e.getClass().getName() + " : " + e.getMessage());
           showToast("Failure connecting to the server. Please check your internet connection.");
       }

       return bFlag;
   }

    public void AlertDialogReset() {
        final CharSequence[] items = { "Yes", "No" };
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                this);
        builder.setTitle("Are you sure to recaptured image?");
        builder.setItems(items, new

                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (item == 0) {
                            finish();
                            startActivities(new Intent[]{getIntent()});
                        } else {
                            // do nothing
                        }
                    }
                });

        android.app.AlertDialog alerts = builder.create();
        alerts.show();
    }


    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void callViewAttLog()
    {
        //finish();
        Intent intent = new Intent(this, viewAtt.class);
        startActivity(intent);

    }
    public void checkConfigDB() {
        int iCount = dba.countConfigDB(dbConfigHelper);
        if (iCount == 0) {
            callclassExecute("Config", "yes");
        } else {


            String[] config = dba.getResultConfig(dbConfigHelper,"*","active='1'");

/*            final String locName = dba.getConfigValue(dbConfigHelper, "loca",
                    "active='1'");

            final String sUid = dba.getConfigValue(dbConfigHelper, "username",
                    "active='1'");

            final String server = dba.getConfigValue(dbConfigHelper, "server",
                    "active='1'");*/

            String locName = config[8];
            String sUid = config[1];
            String server = config[3];

            if(locName.isEmpty()) {
                String guest = "";

                try {
                    guest = getIntent().getExtras().getString("guest");
                } catch (Exception e) {
                    Log.i("GUESTMODE", "GUEST");
                }

                if (guest == null) {
                    if (checkLocationCreator(sUid, server) == 1) {
                        AdminLocation();
                    } else {
                        UserLocation();
                    }
                } else {


                }
            }}}
    public void callclassExecute(String clss, String sfromMain) {
        Log.i("callclassExecute", "execute");


        if (clss.equals("Config")) {
            Intent intent = new Intent(this, Config.class);
            intent.putExtra("sfromMain", sfromMain);
            startActivity(intent);

            if (sfromMain.contains("yes"))
                finish();
        } else if (clss.equals("ConfigList")) {
            Intent intent = new Intent(this, serverlist.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }


    }
    public int checkLocationCreator(String sUid, String Svr) {

        int response = 0;

        String strCurrentLine = "";
        String token = "";
        String urlAddress = "";
        String chksum;

        String encUid;

        try {

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);


                urlAddress = Svr +"/websvc/CheckLocCreator.wp";

                Log.i("uriCheckLoc", urlAddress);

                URL url = new URL(urlAddress);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                chksum = DES.SHA1(sUid);
                encUid = encryption(sUid);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("JWT", encUid);
                jsonParam.put("CS", chksum);

                Log.i("JWT request Loc stat : ", jsonParam.toString());
                //showToast(jsonParam.toString());
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

                    JSONObject jObject = new JSONObject(temp);

                    String aJsonStatus = jObject.getString("STATUS");

                    if (aJsonStatus.contains("1")) {
                        response = 1;
                        Log.i("bol", temp);
                    }
                    else if(aJsonStatus.contains("0")){
                        response = 0;
                    }
                    else{
                        response = 2;
                        reqToken();
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
            Log.e("Probs", error);

        }

        return response;
    }
    public void AdminLocation()
    {
        Intent intent = new Intent(this, LocationManagement.class);
        startActivity(intent);
        this.finish();
    }

    public void UserLocation()
    {
        Intent intent = new Intent(this, InitLocationManagement.class);
        startActivity(intent);
        this.finish();
    }
    public String encryption(String strNormalText) {

        String normalTextEnc = "";
        try {

            CryptLib _crypt = new CryptLib();
            String output = "";
            String key = CryptLib.SHA256("888.8", 32); //32 bytes = 256 bit
            String iv = "lobster997";
            //String iv = CryptLib.generateRandomIV(16); //16 bytes = 128 bit
            output = _crypt.encrypt(strNormalText, key, iv); //encrypt
            Log.i("Workplace token", output);
            normalTextEnc = output;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("Workplace token", e.getMessage());
        }
        return normalTextEnc;
    }
    public void reqToken(){

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

                Svr = server + "/websvc/LocReg.wp";

                URL url = new URL(Svr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                // imei = getIMEIDeviceId(getApplicationContext());

                imei = UUID.randomUUID().toString();
                String substrDeviceID;
                if (imei.length() > 7){
                    substrDeviceID =imei.substring(0,7);
                }
                else
                    substrDeviceID =imei;
                deviceID = substrDeviceID;

                if (deviceID == null || deviceID.length() == 0) {
                    deviceID = "0000-0000";
                }

                rawData = uid + "||" + pwd + "||"+ deviceID + "||LOC_DISABLED_" + uid + "||LOCKEY_DISABLED||Android";
                encData = encryption(rawData);
                chksum = DES.SHA1(rawData);

                JSONObject jsonParam = new JSONObject();

                jsonParam.put("JWT", encData);
                jsonParam.put("CS", chksum);

                Log.i("getTOK raw : ", rawData);
                Log.i("getTOK : ", jsonParam.toString());
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(jsonParam.toString());
                os.flush();
                os.close();

                Log.i("getTOK STATUS", String.valueOf(conn.getResponseCode()));
                String temp = "";

                BufferedReader br = null;
                if (conn.getResponseCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((strCurrentLine = br.readLine()) != null) {
                        temp = strCurrentLine;
                    }
                }

                sTOken = "";
                Log.i("getTOK RESPONSE", temp);
                conn.disconnect();
                JSONObject jObject = new JSONObject(temp);
                String aJsonString = jObject.getString("JWT");
                String aJsonStatus = jObject.getString("STATUS");
                String decData = Decryption(aJsonString);

                Log.i("getTOK Decypt", aJsonString);
                Log.i("getTOK RAw", decData);

                if (aJsonStatus.equalsIgnoreCase("SUCCESS")) {
                    bRetBol = true;
                    sTOken = aJsonString;
                    dba.UpdateLocationManagement(dbConfigHelper,1,true,"LOCDISABLE_"+ uid,"LOCKEY_DISABLED",sTOken);
                } else
                    showToast("Invalid location key");
            }

        } catch (Exception e) {
            String error = "";
            for (StackTraceElement elem : e.getStackTrace()) {
                error += elem.toString();
            }
            Log.e("getTOK Probs", error);

        }
    }
    public String Decryption(String strNormalText){

        String normalTextEnc="";
        try {

            CryptLib _crypt = new CryptLib();
            String output= "";
            String key = CryptLib.SHA256("888.8", 32); //32 bytes = 256 bit
            String iv = "lobster997";
            //String iv = CryptLib.generateRandomIV(16); //16 bytes = 128 bit
            normalTextEnc = _crypt.decrypt(strNormalText, key,iv); //decrypt
            Log.i("Workplace decrypted token",output);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("Workplace token", e.getMessage());
        }
        return normalTextEnc;
    }
    public void FingerPrintListener() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(MainActivity.this, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this, "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                if (allPermissionsGranted()) {
                    openCamera();

                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION);
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Place your finger")
                .setSubtitle("here")
                .setDescription("Description")
                .setDeviceCredentialAllowed(true)
                .build();

        biometricPrompt.authenticate(promptInfo);

    }
    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }



}
