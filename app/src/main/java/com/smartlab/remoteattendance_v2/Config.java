package com.smartlab.remoteattendance_v2;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
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
//import org.apache.http.entity.mime.content.ByteArrayBody;
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

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
//import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
//import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;




//import com.google.firebase.iid.FirebaseInstanceId;

public class Config  extends Activity {

	String sfromMain;
	int primary;

	MySQLiteHelper dbConfigHelper;

	dbActivities dba;
	SQLiteDatabase db;
	String sHttp;
	Spinner spinHttp;
	private String fcmtoken;

	private static String imei = "";
	private static final int READ_PHONE_STATE = 0;
	private String ver= "";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login);


		EditText myTextBox = (EditText) findViewById(R.id.edtxtPwd);
		final TextView errMsg = (TextView) findViewById(R.id.txtError);
		myTextBox.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start,
										  int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start,
									  int before, int count) {
					if (errMsg.getVisibility() == View.VISIBLE ){
						errMsg.setVisibility(View.INVISIBLE);
					}

			}
		});

		Thread thrd = new Thread(){

			@Override
			public void	 run(){
				while (!isInterrupted()){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showtime();
						}
					});
				}
			}
		};
		thrd.start();

		dba = new dbActivities();
		dbConfigHelper = new MySQLiteHelper(this);
		//ver = getString(R.string.app_ver);
		ver = "";

		sfromMain = getIntent().getExtras().getString("sfromMain");
		primary = getIntent().getExtras().getInt("primary");

		//final TextView txtConfigTitle = (TextView) findViewById(R.id.tvConfigTitle);

		sHttp = "";

		spinHttp = (Spinner) findViewById(R.id.spinHttp);

		//ArrayAdapter<CharSequence> adapterHttp = ArrayAdapter.createFromResource(this, R.array.server_http, android.R.layout.simple_dropdown_item_1line);
		ArrayAdapter<CharSequence> adapterHttp = ArrayAdapter.createFromResource(this, R.array.server_http, R.layout.spinner_item);
		adapterHttp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spinHttp.setAdapter(adapterHttp);

		if (sHttp.equals(""))
			spinHttp.setSelection(1);
		else if (sHttp.equals("http://"))
			spinHttp.setSelection(0);
		else
			spinHttp.setSelection(1);


		if (sfromMain.equals("") == false)
		{
			//txtConfigTitle.setText(R.string.label_firstconfig);

			//       	final ImageView imgWP = (ImageView) findViewById(R.id.imvWpLogo);
			//       	imgWP.setVisibility(View.VISIBLE);
		}
		else
		{
			if (primary > 0)
				getinserteddata();

			final CheckBox chkActive = (CheckBox) findViewById(R.id.chkActive);
			int iCount = dba.countConfigDB(dbConfigHelper);
			if(iCount > 0){
				chkActive.setVisibility(View.VISIBLE);
			}
			else{


				chkActive.setChecked(true);
				chkActive.setVisibility(View.INVISIBLE);
			}

		}

		addListenerOnButton();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			String[] permissions = {Manifest.permission.READ_PHONE_STATE};
			if (ActivityCompat.checkSelfPermission(this,
					Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(permissions, READ_PHONE_STATE);
			}
		} else {
			try {
				TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
					return;
				}


			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		imei = getIMEIDeviceId(getApplicationContext());

		FirebaseMessaging.getInstance().getToken()
				.addOnCompleteListener(new OnCompleteListener<String>() {
					@Override
					public void onComplete(@NonNull Task<String> task) {
						if (!task.isSuccessful()) {
							Log.w("SY FCM TAG", "Fetching FCM registration token failed", task.getException());
							return;
						}
						// Get new FCM registration token
						fcmtoken = task.getResult();

						Log.i("this fcm token",fcmtoken);

					}
				});


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

	public  void showtime(){

		TextView time = (TextView)findViewById(R.id.time);

		long ltime = System.currentTimeMillis();

		SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss a");
		String datetime = sdf1.format(ltime);

		time.setText(datetime);

	}


	public void getinserteddata()
	{
		String[] config = dba.getResultConfig(dbConfigHelper, "*", "_ID ='"+primary+"'");

		final EditText txtUid = (EditText) findViewById(R.id.edtxtUID);
		final EditText txtPwd = (EditText) findViewById(R.id.edtxtPwd);
		final EditText txtServer = (EditText) findViewById(R.id.edtxtServer);
		final CheckBox chkActive = (CheckBox) findViewById(R.id.chkActive);

		String usr = config[1];
		String pass = config[2];
		String server = config[3];
		boolean active;

		if (Integer.parseInt(config[4].toString()) == 1)
			active = true;
		else
			active = false;

		String[] arrDomain = server.split("://");

		sHttp = arrDomain[0].trim();
		String url = arrDomain[1].trim();

		if (sHttp.equalsIgnoreCase("https")){
			spinHttp.setSelection(1);
		}
		else{
			spinHttp.setSelection(0);
		}


		txtUid.setText(usr);
		txtPwd.setText(pass);
		txtServer.setText(url);
		chkActive.setChecked(active);

	}

	public void addListenerOnButton() {

		Button btnSave = (Button) findViewById(R.id.btnSave);
		//Button btnCancel = (Button) findViewById(R.id.btnCancel);


		btnSave.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {

				AlertDialogSave();

			}
		});

    	/*btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (sfromMain.equals("") == false)
		        {
					exitApp();
		        }
				else
					callclass();
			}
		});*/
	}

	public void AlertDialogSave()
	{
		final CharSequence[] items = {"Yes", "Cancel"};
		android.app.AlertDialog.Builder builder = new
				android.app.AlertDialog.Builder(this);
		builder.setTitle("Authenticate this account?");
		builder.setItems(items, new

				DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
										int item) {

						if(item==0)
						{
							final EditText txtUid = (EditText) findViewById(R.id.edtxtUID);
							final EditText txtPwd = (EditText) findViewById(R.id.edtxtPwd);
							final EditText txtServer = (EditText) findViewById(R.id.edtxtServer);
							final CheckBox chkActive = (CheckBox) findViewById(R.id.chkActive);
							String dom = spinHttp.getItemAtPosition(spinHttp.getSelectedItemPosition()).toString();

							boolean chkAct = false;
							if (chkActive.isChecked() == true){
								chkAct = true;
							}

							//Save to database
							//if (dba.SaveConfigDB(dbConfigHelper, 1,txtUid.getText().toString() , txtPwd.getText().toString(), txtServer.getText().toString(), chkActive.isChecked()))
							if(sendJSON(primary, txtUid.getText().toString() , txtPwd.getText().toString(), txtServer.getText().toString(), chkAct,ver) == true)
							{
								if (primary > 0) {
									showToast("Configuration updated successfully.");
								}
								else {
									showToast("Configuration added successfully.");
								}

								renew_Session();
								int retCode = checkLocationCreator(txtUid.getText().toString());

								Log.i("retCode",""+retCode);

								if(retCode == 1){
									AdminLocation();
								}
								else if(retCode == 0) {
									UserLocation();
								}
								else if(retCode == 2){
									exitToMain();
								}
								else{
									showToast("Invalid retCode");


                                  dbConfigHelper.deleteAllRows();
								}

							}
							else
							{
								Log.e("Insert User Config", "Insert User Config error");
							}
						}
					}
				});

		android.app.AlertDialog alerts = builder.create();
		alerts.show();
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

	public boolean sendJSON(int iprimary,String sUid,String sPwd,String sUri,boolean act,String sVer){

		String strCurrentLine = "";
		boolean bRetBol = false;
		String Svr = "";
		String token = "";
		String urlAddress="";
		boolean bFlag = false;

		TextView errMsg = (TextView)findViewById(R.id.txtError);

		try {

			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);


				Spinner spinHttp = (Spinner) findViewById(R.id.spinHttp);
				sHttp = spinHttp.getItemAtPosition(spinHttp.getSelectedItemPosition()).toString();

				Svr = sHttp + sUri;

				urlAddress = Svr +"/websvc/userauth_ios.wp";
				//urlAddress = Svr +"/websvc/userauth.wp";
				//token = FirebaseInstanceId.getInstance().getToken();


				Log.i("uri",urlAddress);
				Log.i("token SR",fcmtoken);

				URL url = new URL(urlAddress);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
				conn.setRequestProperty("Accept", "application/json");
				conn.setDoOutput(true);
				conn.setDoInput(true);

				JSONObject jsonParam = new JSONObject();
				//jsonParam.put("timestamp", 1488873360);
				jsonParam.put("username", sUid);
				jsonParam.put("password", sPwd);
				jsonParam.put("token", fcmtoken);
				jsonParam.put("deviceid", imei);

				Log.i("JSON SR", jsonParam.toString());
				DataOutputStream os = new DataOutputStream(conn.getOutputStream());
				//os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
				os.writeBytes(jsonParam.toString());
				os.flush();
				os.close();

				Log.i("token SR STATUS", String.valueOf(conn.getResponseCode()));
				String temp ="";

				BufferedReader br = null;
				if (conn.getResponseCode() == 200) {
					br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

					while ((strCurrentLine = br.readLine()) != null) {
						temp = strCurrentLine;
					}
				}

				Log.i("MSG", temp);
				conn.disconnect();

				if (temp.contains("1")){
					bRetBol = true;
					Log.i("bol", temp);
				}
			}

		} catch (Exception e) {
			String error = "";
			for(StackTraceElement elem: e.getStackTrace()) {
				error += elem.toString();
			}
			Log.e("Probs", error);

		}

		if (bRetBol== true){

			bFlag = true;
			errMsg.setVisibility(View.INVISIBLE);
			if (act == true)
			{
				bFlag = dba.UpdateAllUnActive(dbConfigHelper);
				//if (bFlag == false)
				//	 showToast("Update all un-active status failed.");
				if (primary > 0)
				{
					bFlag = dba.UpdateConfigDB(dbConfigHelper, primary, sUid, sPwd, Svr, act,ver,"");
					if (bFlag == false)
						showToast("Error Response : \nUpdating configuration data failed.");
				}
				else
				{
					primary = dba.checkConfigDB(dbConfigHelper);
					ver = getString(R.string.app_ver);
					bFlag = dba.SaveConfigDB(dbConfigHelper, primary+1 ,sUid , sPwd, Svr, act,ver,"");
					if (bFlag == false)
						showToast("Error Response : \nInserting configuration data failed.");
				}
			}
			else
			{
				if (primary > 0)
				{
					bFlag = dba.UpdateConfigDB(dbConfigHelper, primary, sUid, sPwd, Svr, false,ver,"");
					if (bFlag == false)
						showToast("Error Response : \nUpdating configuration data failed.");
				}
				else
				{
					primary = dba.checkConfigDB(dbConfigHelper);
					ver = getString(R.string.app_ver);
					bFlag = dba.SaveConfigDB(dbConfigHelper, primary+1 ,sUid , sPwd, Svr, false,ver,"");
					if (bFlag == false)
						showToast("Error Response : \nInserting configuration data failed.");
				}
			}
		}

		else

			errMsg.setVisibility(View.VISIBLE);


		return bRetBol;
	}

	public void renew_Session() {

		String strCurrentLine = "";
		int responseCode = 0;
		String Svr = "";
		String sUid = "";
		String token = "";
		String urlAddress = "";
		boolean bFlag = false;

		String chksum;

		//--- get all data from DB
		final String server = dba.getConfigValue(dbConfigHelper, "server",
				"active='1'");
		final String uid = dba.getConfigValue(dbConfigHelper, "username",
				"active='1'");

		final String pwd = dba.getConfigValue(dbConfigHelper, "password",
				"active='1'");


			try {

				if (android.os.Build.VERSION.SDK_INT > 9) {
					StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
					StrictMode.setThreadPolicy(policy);

					urlAddress = server + "/websvc/updateToken.wp";

					token = fcmtoken;

					Log.i("uri renew token", urlAddress);
					Log.i("renew token", token);

					URL url = new URL(urlAddress);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
					conn.setRequestProperty("Accept", "application/json");
					conn.setDoOutput(true);
					conn.setDoInput(true);

					Log.i("renew Raw Data : ", token);

					JSONObject jsonParam = new JSONObject();
					FirebaseMessaging.getInstance().getToken();
					jsonParam.put("USER", uid);
					jsonParam.put("DEVICEID", imei);
					jsonParam.put("TOKEN", fcmtoken);

					Log.i("FIRETOEKEN SR", jsonParam.toString());
					DataOutputStream os = new DataOutputStream(conn.getOutputStream());
					//os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
					os.writeBytes(jsonParam.toString());
					os.flush();
					os.close();

					Log.i("FIRETOEKEN STATUS", String.valueOf(conn.getResponseCode()));
					String temp = "";

					BufferedReader br = null;
					if (conn.getResponseCode() == 200) {
						br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						while ((strCurrentLine = br.readLine()) != null) {
							temp = strCurrentLine;
						}
					}

					Log.i("\"FIRETOEKEN STATUS 2", temp);
					conn.disconnect();
					//JSONObject jObject = new JSONObject(temp);
					// String aJsonString = jObject.getString("JWT");
					// String decData = Decryption(aJsonString);

					//Log.i("JWT Decypt", aJsonString);
					// Log.i("JWT RAw",decData);

					if (temp.contains("1")) {
						responseCode = 1;
						Log.i("bol", temp);
					}

				}

			} catch (Exception e) {
				String error = "";
				for (StackTraceElement elem : e.getStackTrace()) {
					error += elem.toString();
				}
				Log.e("Probs", error);
			}
	}


	@SuppressLint("SuspiciousIndentation")
	public boolean sendJSONV2(int iprimary, String sUid, String sPwd, String sUri, boolean act, String sVer){

		String strCurrentLine = "";
		boolean bRetBol = false;
		String Svr = "";
		String token = "";
		String jwtToken="";
		String urlAddress="";
		String chksum = "";
		boolean bFlag = false;

		TextView errMsg = (TextView)findViewById(R.id.txtError);

		try {

			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);


				Spinner spinHttp = (Spinner) findViewById(R.id.spinHttp);
				sHttp = spinHttp.getItemAtPosition(spinHttp.getSelectedItemPosition()).toString();

				Svr = sHttp + sUri;

				urlAddress = Svr +"/websvc/userauth_new2.wp";
				//token = FirebaseInstanceId.getInstance().getToken();
				token = fcmtoken;

				Log.i("uri",urlAddress);
				Log.i("firebasetoken",token);

				//chksum = DES.SHA1(sUid + "||" + sPwd + "||" + imei + "||" + "UNKNOWN");

				chksum = DES.SHA1(sUid + "||" + sPwd + "||" + imei + "||" + "UNKNOWN" + "||" + "NE" + "||" + token);

				//generate token =
				Log.i("jwtRaw",sUid + "||" + sPwd + "||" + imei + "||" + "UNKNOWN" + "||" + chksum);
				Log.i("jwtRaw",sUid + "||" + sPwd + "||" + imei + "||" + "UNKNOWN" + "||" + "NE");
				//jwtToken =  encryption(sUid + "||" + sPwd + "||" + imei + "||" + "UNKNOWN" + "||" + chksum);
				jwtToken =  encryption(sUid + "||" + sPwd + "||" + imei + "||" + "UNKNOWN" + "||" + "NE" + "||" + token);

				URL url = new URL(urlAddress);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
				conn.setRequestProperty("Accept", "application/json");
				conn.setDoOutput(true);
				conn.setDoInput(true);

				JSONObject jsonParam = new JSONObject();

				jsonParam.put("JWT", jwtToken);
				jsonParam.put("CS", chksum);

				Log.i("JWT SR", jsonParam.toString());
				DataOutputStream os = new DataOutputStream(conn.getOutputStream());
				//os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
				os.writeBytes(jsonParam.toString());
				os.flush();
				os.close();

				Log.i("JWT STATUS SR", String.valueOf(conn.getResponseCode()));
				String temp ="";

				BufferedReader br = null;
				if (conn.getResponseCode() == 200) {
					br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

					while ((strCurrentLine = br.readLine()) != null) {
						temp = strCurrentLine;
					}
				}

				Log.i("JWT Response", temp);
				conn.disconnect();

				if (temp.contains("1")){
					bRetBol = false;
					Log.i("bol", temp);
				}
				else
                    bRetBol = true;
			}

		} catch (Exception e) {
			String error = "";
			for(StackTraceElement elem: e.getStackTrace()) {
				error += elem.toString();
			}
			Log.e("Probs", error);

		}

		if (bRetBol== true){

			bFlag = true;
			errMsg.setVisibility(View.INVISIBLE);
			if (act == true)
			{
				bFlag = dba.UpdateAllUnActive(dbConfigHelper);
				//if (bFlag == false)
				//	 showToast("Update all un-active status failed.");
				if (primary > 0)
				{
					bFlag = dba.UpdateConfigDB(dbConfigHelper, primary, sUid, sPwd, Svr, act,ver,"");
					if (bFlag == false)
						showToast("Error Response : \nUpdating configuration data failed.");
				}
				else
				{
					primary = dba.checkConfigDB(dbConfigHelper);
					ver = getString(R.string.app_ver);
					bFlag = dba.SaveConfigDB(dbConfigHelper, primary+1 ,sUid , sPwd, Svr, act,ver,"");
					if (bFlag == false)
						showToast("Error Response : \nInserting configuration data failed.");
				}
			}
			else
			{
				if (primary > 0)
				{
					bFlag = dba.UpdateConfigDB(dbConfigHelper, primary, sUid, sPwd, Svr, false,ver,"");
					if (bFlag == false)
						showToast("Error Response : \nUpdating configuration data failed.");
				}
				else
				{
					primary = dba.checkConfigDB(dbConfigHelper);
					ver = getString(R.string.app_ver);
					bFlag = dba.SaveConfigDB(dbConfigHelper, primary+1 ,sUid , sPwd, Svr, false,ver,"");
					if (bFlag == false)
						showToast("Error Response : \nInserting configuration data failed.");
				}
			}
		}

		else

			//errMsg.setVisibility(View.VISIBLE);
		exitToMain();


		return bRetBol;
	}

	public void Timer_Delay(int millisec)
	{
		TimerTask tskSaveToDb;
		Timer tmr = new Timer();

		tskSaveToDb = new TimerTask() {
			public void run() {
				delay();
			}};

		tmr.schedule(tskSaveToDb, millisec);
	}

	public void delay()
	{
		for (int i=0; i<100; i++)
		{
			for (int j=0; j<100; j++)
			{
				i = i + 0;
				j = j + 0;
			}
		}
	}

	public boolean AuthUser(int primary, String Uid, String Pwd, String Svr, boolean active) throws JSONException {
		Timer_Delay(5000);

		boolean bFlag = false;

		//String token = FirebaseInstanceId.getInstance().getToken();
		String token = fcmtoken;
		Spinner spinHttp = (Spinner) findViewById(R.id.spinHttp);
		sHttp = spinHttp.getItemAtPosition(spinHttp.getSelectedItemPosition()).toString();

		Svr = sHttp + Svr;

		String usr = Uid;
		String pass = Pwd;
		String url = Svr + getResources().getString(R.string.user_auth) ;


		String deviceName = android.os.Build.MODEL;
		String deviceMan = android.os.Build.MANUFACTURER;
		String devID = "";



		//upload data to server
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add((NameValuePair) new BasicNameValuePair("username", usr));
		nameValuePairs.add((NameValuePair) new BasicNameValuePair("password", pass));
		nameValuePairs.add((NameValuePair) new BasicNameValuePair("token", token));
		nameValuePairs.add((NameValuePair) new BasicNameValuePair("phoneid", imei));

		bFlag = post(url, nameValuePairs, usr);

		String jSONres = "";

		JSONObject jsonobj = new JSONObject();

		jsonobj.accumulate("username", usr);
		jsonobj.accumulate("password", pass);
		jsonobj.accumulate("modelno", deviceName);
		jsonobj.accumulate("token", token);

		if (bFlag == true)
		{
			if (active == true)
			{
				bFlag = dba.UpdateAllUnActive(dbConfigHelper);
				//if (bFlag == false)
				//	 showToast("Update all un-active status failed.");
			}

			if (primary > 0)
			{
				bFlag = dba.UpdateConfigDB(dbConfigHelper, primary, Uid, Pwd, Svr, active,ver,"");
				if (bFlag == false)
					showToast("Error Response : \nUpdating configuration data failed.");
			}
			else
			{
				primary = dba.checkConfigDB(dbConfigHelper);
				ver = getString(R.string.app_ver);
				bFlag = dba.SaveConfigDB(dbConfigHelper, primary+1 ,Uid , Pwd, Svr, active,ver,"");
				if (bFlag == false)
					showToast("Error Response : \nInserting configuration data failed.");
			}
		}

		return bFlag;
	}



	public boolean post(String url, List<NameValuePair> nameValuePairs,String UserID) {

		boolean bFlag = false;

		ThreadPolicy tp = ThreadPolicy.LAX;
		StrictMode.setThreadPolicy(tp);

		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpContext localContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);

		try {

			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			for(int index=0; index < nameValuePairs.size(); index++) {
				if(nameValuePairs.get(index).getValue() != null){
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

				bFlag = xmlParse(res,UserID);

			}

			if (resEntity != null)
				resEntity.consumeContent();

			httpClient.getConnectionManager().shutdown();

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("post() ~ Exception",e.getClass().getName() + " : " + e.getMessage());
	            /*Toast.makeText(
	                    getApplicationContext(),
	                    "Exception ~ post() :\n" + e.getClass().getName() + " \n" + e.getMessage(),
	                    Toast.LENGTH_LONG).show();*/

			Toast.makeText(getApplicationContext(), "Failure connecting to '" + e.getMessage() + "'. \nPlease check your internet connection.", Toast.LENGTH_LONG).show();

			bFlag = false;
		}

		return bFlag;
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
			httpPost.setHeader("Content-type", "application/json");


			StringEntity se = new StringEntity(object.toString());
			//  se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
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

	public boolean xmlParse(String result, String UserID)
	{
		boolean bFlag = false;

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();

			xpp.setInput( new StringReader (result) );

			int eventType = xpp.getEventType();

			String uid = "";
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
						info = xpp.getText();
					}
					else
						uid = xpp.getText();
				}

				eventType = xpp.next();
			}
			Log.i("End document", "end");
			if ( uid != "" && uid != null)
			{
				TextView errMsg = (TextView)findViewById(R.id.txtError);
				if (uid.equals(UserID))
				{
					//showToast("User authentication successfull.");
					bFlag = true;
					errMsg.setVisibility(View.INVISIBLE);
				}
				else
				{
					//showToast("Error Response : \nUser authentication failed.");
					bFlag = false;
					errMsg.setVisibility(View.VISIBLE);
				}
			}
			else
			{
				bFlag = false;
				showToast("Error Response : \nUser authentication failed.");
				Log.e("xmlError authentication", info);
			}
		}
		catch (Exception e1)
		{
			Log.e("xmlParse() ~ Exception", e1.getClass().getName() + " : " + e1.getMessage(), e1);

			Toast.makeText(
					getApplicationContext(),
					"Exception ~ xmlParse() :\n" + e1.getClass().getName() + " \n" + e1.getMessage(),
					Toast.LENGTH_LONG).show();

			bFlag = false;
		}

		return bFlag;

	}

	public void showToast(String strMsg)
	{
		Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
	}

	public void callclass()
	{
		Intent intent = new Intent(this, serverlist.class);
		startActivity(intent);
		this.finish();
	}

	public void exitApp()
	{
		this.finish();
	}

	public void exitToMain()
	{
		Intent intent = new Intent(this, MainActivity.class);

		startActivity(intent);
		this.finish();
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


	@SuppressLint("SuspiciousIndentation")
	public int checkLocationCreator(String sUid) {

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

				Spinner spinHttp = (Spinner) findViewById(R.id.spinHttp);
				sHttp = spinHttp.getItemAtPosition(spinHttp.getSelectedItemPosition()).toString();

				final EditText txtServer = (EditText) findViewById(R.id.edtxtServer);
				Svr = sHttp + txtServer.getText().toString();

				urlAddress = Svr +"/websvc/CheckLocCreator.wp";

                Log.i("uriCheckLoc", urlAddress);

                URL url = new URL(urlAddress);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                chksum = DES.SHA1(sUid.replaceAll("\\n",""));
				encUid = encryption(sUid.replaceAll("\\n",""));

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
            Log.e("JWT Probs", error);

        }

        return response;
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

					imei = getIMEIDeviceId(getApplicationContext());
                    deviceID = imei;

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


}
