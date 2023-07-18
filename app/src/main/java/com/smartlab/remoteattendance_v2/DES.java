package com.smartlab.remoteattendance_v2;

import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DES {

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String temp = "";
        String finalTemp = "";

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        temp = convertToHex(sha1hash);
        finalTemp = temp.substring(temp.length() - 8);
        return finalTemp;
    }

    public static String testToken() throws NoSuchAlgorithmException, UnsupportedEncodingException {

       String token="";
        String uname;
        String pwd;

        int iUname;
        int iPwd;
        int sum;

        uname = "0x" + Integer.toHexString(Integer.parseInt("syukri"));
        pwd = "0x" + Integer.toHexString(Integer.parseInt("syukri@123"));
        iUname = Integer.parseInt(uname, 16);
        iPwd = Integer.parseInt(pwd, 16);
        sum = iUname + iPwd;
        Log.i("sum",""+ sum);



        return token;
    }

    public static String getToken(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        String devID;
        String uname;
        String pwd;
        String chksum;
        String time;
        String token = "";

        String bdevID;

        String[] separated = text.split("\\|");
        devID = separated[0].trim().replace("*","");
        uname = separated[1].trim().replace("*","");
        pwd = separated[2].trim().replace("*","");
        chksum = separated[3].trim().replace("*","");
        time = separated[2].trim().replace("*","");

        bdevID = "0x" + Integer.toHexString(Integer.parseInt(devID));

        return token;
    }


}