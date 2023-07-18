package com.smartlab.remoteattendance_v2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.StrictMode;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.widget.ImageView;
import android.widget.Toast;

import android.net.Uri;

import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by reale on 25/11/2016.
 */
public class FingerprintHandler1 extends FingerprintManager.AuthenticationCallback {

    private Context context;
    public static final int CAMERA_REQUEST = 9998;
    private static final int IMAGE_CAPTURE_CODE=1001;
    private static final int PERMISSION_CODE = 1000;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;

    Uri targetURI;

    ImageView imageView;
    protected void onCreate(Bundle savedInstanceState) {

    }
    public FingerprintHandler1(Context context) {
        this.context = context;

    }

    public void startAuthentication(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cenCancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED)
            return;

        fingerprintManager.authenticate(cryptoObject, cenCancellationSignal, 0, this, null);

    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        Toast.makeText(context, "Fingerprint Authentication failed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        //context.startActivity(new Intent(context,MainActivity.class));
        Toast.makeText(context, "Fingerprint Authentication successful!", Toast.LENGTH_SHORT).show();
//          Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//context.startActivity(intent);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request the camera permission
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Camera permission is already granted, proceed with camera operation
            openCamera();
        }

    }



    public void openCamera() {


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        //----------------------------------------------------------------------------
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currDT = sdf.format(new Date());

        Intent CamImageIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        File cameraFolder;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            cameraFolder = new File(Environment.getExternalStorageDirectory(),"DCIM/camera");
        else
            cameraFolder = null;


        if (!cameraFolder.exists())
            cameraFolder.mkdirs();


        File photo = new File(Environment.getExternalStorageDirectory(),"DCIM/imgAtt_" + currDT.toString() + ".jpg");
        targetURI = Uri.fromFile(photo);

        //CamImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, targetURI);

       // Toast.makeText(context, "uri : " + targetURI, Toast.LENGTH_SHORT).show();

       ((MainActivity) context).startActivityForResult(CamImageIntent, CAMERA_REQUEST);


    }



}


