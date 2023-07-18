package com.smartlab.remoteattendance_v2;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.places.Places;


public class geoFenceActivity extends AppCompatActivity
		implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback,
		GoogleMap.OnMapClickListener,
		GoogleMap.OnMarkerClickListener,
        ResultCallback<Status> {

	private MyBroadcastReceiver myBroadcastReceiver;

	private static final String TAG = MainActivity.class.getSimpleName();

	private GoogleMap map;
	private GoogleApiClient googleApiClient;
	private Location lastLocation;
	private TextView textLat, textLong;


	MySQLiteHelper dbConfigHelper;
	dbActivities dba;

	private MapFragment mapFragment;

	String res = "";

	private static final String NOTIFICATION_MSG = "NOTIFICATION MSG";

	// Create a Intent send by the notification
	public static Intent makeNotificationIntent(Context context, String msg) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.putExtra(NOTIFICATION_MSG, msg);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_map);
		textLat = (TextView) findViewById(R.id.lat);
		textLong = (TextView) findViewById(R.id.lon);

		dba = new dbActivities();
		dbConfigHelper = new MySQLiteHelper(this);

		myBroadcastReceiver = new MyBroadcastReceiver();

		//register BroadcastReceiver
		IntentFilter intentFilter = new IntentFilter(GeofenceTrasitionService.ACTION_MyIntentService);
		intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
		registerReceiver(myBroadcastReceiver, intentFilter);

		// initialize GoogleMaps
		initGMaps();

		// create GoogleApiClient
		createGoogleApi();

		Button btnClearFEnce = findViewById(R.id.btnClearFencing);
		Button btnAuth = findViewById(R.id.btnAtt);


		btnClearFEnce.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				//clearGeofence();
				getFenceLoc();
			}
		});


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

			jSONres = postJSON("http://ge.smartlab.com.my/cloudtms/websvc/remote_check.asp",jsonParam);
			JSONObject jsonObj = new JSONObject(jSONres);
			JSONArray geodata = jsonObj.getJSONArray("GEODATA");

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
				//temp.add(geodata.getString(i));

				float fLat = Float.parseFloat(sLat);
				float fLon = Float.parseFloat(sLon);

				LatLng position = new LatLng(fLat,fLon);
				markerForGeofence(position);
			}

			startGeofence();


			BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					String s1 = intent.getStringExtra("DATAPASSED");
					//showToast(s1);
				}
			};


		} catch (JSONException e) {
			e.printStackTrace();

			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			Log.e("test err", errors.toString());
		}
	}



	// Create GoogleApiClient instance
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
	protected void onStop() {
		super.onStop();

		// Disconnect GoogleApiClient when stopping Activity
		googleApiClient.disconnect();
	}

	private final int REQ_PERMISSION = 999;

	// Check for permission to access Location
	private boolean checkPermission() {
		Log.d(TAG, "checkPermission()");
		// Ask for permission if it wasn't granted yet
		return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED);
	}

	// Asks for permission
	private void askPermission() {
		Log.d(TAG, "askPermission()");
		ActivityCompat.requestPermissions(
				this,
				new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
				REQ_PERMISSION
		);
	}

	// Verify user's response of the permission requested
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

	// Initialize GoogleMaps
	private void initGMaps() {
		mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	// Callback called when Map is ready
	@Override
	public void onMapReady(GoogleMap googleMap) {
		Log.d(TAG, "onMapReady()");
		map = googleMap;
		map.setOnMapClickListener(this);
		map.setOnMarkerClickListener(this);
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

	private LocationRequest locationRequest;
	// Defined in mili seconds.
	// This number in extremely low, and should be used only for debug
	private final int UPDATE_INTERVAL = 1000;
	private final int FASTEST_INTERVAL = 900;

	// Start location Updates
	private void startLocationUpdates() {
		Log.i(TAG, "startLocationUpdates()");
		locationRequest = LocationRequest.create()
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setInterval(UPDATE_INTERVAL)
				.setFastestInterval(FASTEST_INTERVAL);

		if (checkPermission())
			LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "onLocationChanged [" + location + "]");
		lastLocation = location;
		writeActualLocation(location);
	}

	// GoogleApiClient.ConnectionCallbacks connected
	@Override
	public void onConnected(@Nullable Bundle bundle) {
		Log.i(TAG, "onConnected()");
		getLastKnownLocation();
		getFenceLoc();


	}

	// GoogleApiClient.ConnectionCallbacks suspended
	@Override
	public void onConnectionSuspended(int i) {
		Log.w(TAG, "onConnectionSuspended()");
	}

	// GoogleApiClient.OnConnectionFailedListener fail
	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		Log.w(TAG, "onConnectionFailed()");
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

	private void writeActualLocation(Location location) {
		textLat.setText("Lat: " + location.getLatitude());
		textLong.setText("Long: " + location.getLongitude());
        //set marker coordinate
		markerLocation(new LatLng(location.getLatitude(), location.getLongitude()));


	}

	private void writeLastLocation() {
		writeActualLocation(lastLocation);
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


	private Marker geoFenceMarker;
	private ArrayList<Marker> gfmarkr = new ArrayList<Marker>();

	//single point
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

	// Start Geofence creation process
	private String idd="Workplace";
	private void startGeofence() {
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

	private static final long GEO_DURATION = 12 * 60 * 60 * 1000;
	private static final String GEOFENCE_REQ_ID = "Workplace Geofence";
	private static final float GEOFENCE_RADIUS = 400.0f; // in meters

	// Create a Geofence
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

	// Create a Geofence Request
	private GeofencingRequest createGeofenceRequest(Geofence geofence) {
		Log.d(TAG, "createGeofenceRequest");
		return new GeofencingRequest.Builder()
				.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
				.addGeofence(geofence)
				.build();
	}

	private PendingIntent geoFencePendingIntent;
	private final int GEOFENCE_REQ_CODE = 0;

	private PendingIntent createGeofencePendingIntent() {
		Log.d(TAG, "createGeofencePendingIntent");
		if (geoFencePendingIntent != null)
			return geoFencePendingIntent;

		Intent intent = new Intent(this, GeofenceTrasitionService.class);
		return PendingIntent.getService(
				this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	// Add the created GeofenceRequest to the device's monitoring list
	private void addGeofence(GeofencingRequest request) {
		Log.d(TAG, "addGeofence");
		if (checkPermission())
			LocationServices.GeofencingApi.addGeofences(
					googleApiClient,
					request,
					createGeofencePendingIntent()
					).setResultCallback(this);

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
				.radius(GEOFENCE_RADIUS);

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

	private final String KEY_GEOFENCE_LAT = "GEOFENCE LATITUDE";
	private final String KEY_GEOFENCE_LON = "GEOFENCE LONGITUDE";

	// Saving GeoFence marker with prefs mng
	private void saveGeofence() {
		Log.d(TAG, "saveGeofence()");
		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();

		editor.putLong(KEY_GEOFENCE_LAT, Double.doubleToRawLongBits(geoFenceMarker.getPosition().latitude));
		editor.putLong(KEY_GEOFENCE_LON, Double.doubleToRawLongBits(geoFenceMarker.getPosition().longitude));
		editor.apply();
	}

	// Recovering last Geofence marker
	private void recoverGeofenceMarker() {
		Log.d(TAG, "recoverGeofenceMarker");
		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

		if (sharedPref.contains(KEY_GEOFENCE_LAT) && sharedPref.contains(KEY_GEOFENCE_LON)) {
			double lat = Double.longBitsToDouble(sharedPref.getLong(KEY_GEOFENCE_LAT, -1));
			double lon = Double.longBitsToDouble(sharedPref.getLong(KEY_GEOFENCE_LON, -1));
			LatLng latLng = new LatLng(lat, lon);
			markerForGeofence(latLng);
			drawGeofence();
		}
	}


	public class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String result = intent.getStringExtra(GeofenceTrasitionService.EXTRA_KEY_OUT);
			 if (result.equals("1")){
				 Button btnGetFence = findViewById(R.id.btnGetFencing);
				 btnGetFence.setBackgroundColor(getResources().getColor(R.color.colorGreen));
				 btnGetFence.setText("AUTHORISED");
			 }
		}
	}

	// Clear Geofence
	private void clearGeofence() {
		Log.d(TAG, "clearGeofence()");
		LocationServices.GeofencingApi.removeGeofences(
				googleApiClient,
				createGeofencePendingIntent()
		).setResultCallback(new ResultCallback<Status>() {
			@Override
			public void onResult(@NonNull Status status) {
				if (status.isSuccess()) {
					// remove drawing
					removeGeofenceDraw();
				}
			}
		});
	}

	private void removeGeofenceDraw() {
		Log.d(TAG, "removeGeofenceDraw()");
		if (geoFenceMarker != null)
			geoFenceMarker.remove();
		if (geoFenceLimits != null)
			geoFenceLimits.remove();
	}

	private interface DataListener{
		void onDataReady(String data);
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


	public void showToast(String strMsg)
	{
		Toast.makeText(this, strMsg, Toast.LENGTH_LONG).show();
	}
}
