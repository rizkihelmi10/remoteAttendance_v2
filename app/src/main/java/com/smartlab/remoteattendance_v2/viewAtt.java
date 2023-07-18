package com.smartlab.remoteattendance_v2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class viewAtt extends AppCompatActivity {

    MySQLiteHelper dbHelper;
    SQLiteDatabase db;
    dbActivities dba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.viewatt);


        dba = new dbActivities();
        dbHelper = new MySQLiteHelper(this);

        displayChrmetab();
        //notSubscribe();
    }

    class MyJavascriptInterface
    {

        Context mContext;

        /** Instantiate the interface and set the context */
        MyJavascriptInterface(Context c)
        {
            mContext = c;
        }



        @JavascriptInterface
        public void BackToHome()
        {
            finish();
           // startActivity(new Intent(getApplicationContext(),MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }



    }

    public void displayChrmetab(){


        ProgressBar progressBar = findViewById(R.id.progressSpinner);
        TextView progressText = findViewById(R.id.progresstext);
        progressBar.setMax(100);

        final String server = dba.getConfigValue(dbHelper,"server","active='1'");
        final String uid = dba.getConfigValue(dbHelper,"username","active='1'");

        //String domain = server.replace("http://","");
        String domain = server.replace("/workplace", "");

        final String url = domain + "/cloudtms/report_remoteatt_m.asp?memberID=" + uid + "&android=yes";
        final WebView myWebView = (WebView) findViewById(R.id.webViewAtt);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface(new MyJavascriptInterface(this), "Android");

        myWebView.loadUrl(url);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
      /*  myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                if (request !=null && request.getUrl() !=null)
                    if (request.getUrl().toString().equals(url) && errorResponse.getStatusCode()==404) {
                        //Handle the error

                     /*  myWebView.loadUrl("about:blank");
                        new AlertDialog.Builder(viewAtt.this)
                                .setTitle("No subscription")
                                .setMessage("This module is not subscribed. Please contact administrator")
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Whatever...
                                        finish();
                                    }
                                }).show();*/


                 //   }
           // }
       // });

        myWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                    progressText.setVisibility(View.INVISIBLE);
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressText.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });


    }



    private void notSubscribe(){
        WebView myWebView = (WebView) findViewById(R.id.webViewAtt);
        String summary =
                "<html><head>\n" +
                        "    <meta charset=\"utf-8\" />\n" +
                        "    <title>No connection to the internet</title>\n" +
                        "    <style>\n" +
                        "      html,body { margin:0; padding:0; }\n" +
                        "      html {\n" +
                        "        background: #191919 -webkit-linear-gradient(top, #fff 0%, #191919 100%) no-repeat;\n" +
                        "        background: #191919 linear-gradient(to bottom, #fff 0%, #191919 100%) no-repeat;\n" +
                        "      }\n" +
                        "      body {\n" +
                        "        font-family: sans-serif;\n" +
                        "        color: #FFF;\n" +
                        "        text-align: center;\n" +
                        "        font-size: 50%;\n" +
                        "      }\n" +
                        "      h1, h2 { font-weight: normal; }\n" +
                        "      h1 { margin: 0 auto; padding: 0.15em; font-size: 10em; text-shadow: 0 2px 2px #fff; }\n" +
                        "      h2 { margin-bottom: 2em; }\n" +
                        "    </style>\n" +
                        "  </head>\n" +
                        "  <body>\n" +
                        "    <h1>⚠</h1>\n" +
                        "    <h2>No connection to the internet</h2>\n" +
                        "    <p>This Display has a connection to your network but no connection to the internet.</p>\n" +
                        "    <p class=\"desc\">The connection to the outside world is needed for updates and keeping the time.</p>\n" +
                        "  </body></html>";


        myWebView.loadData(summary, "text/html", "UTF-8");
    }
}
