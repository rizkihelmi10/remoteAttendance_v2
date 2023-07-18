package com.smartlab.remoteattendance_v2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
//import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class DomainList  extends Activity {
	
	MySQLiteHelper dbHelper; 
    SQLiteDatabase db;
    dbActivities dba;
	int primary;

	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        setContentView(R.layout.domainlist);
	        
	        //auto generate the list of inserted properties
	        
	        //1. check either the database already get data or not (If not, create database)
	        dba = new dbActivities();
	        dbHelper = new MySQLiteHelper(this);

	    	primary = dba.checkConfigDB(dbHelper);
	        
	    	//2. get current domain
	    	getCurrentDomain();
	    	
	        //3. generate button	        
	        generateButton();
	        
	        //4. Cancel change
	        Button btnCancelChange = (Button) findViewById(R.id.btnCancelChange);
	        btnCancelChange.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0)
				{
		    		callclass();
				}
			});	
	    }
	 
	 public boolean onKeyDown(int keyCode, KeyEvent event)
	 { //  syukri add the line
	        if (keyCode == KeyEvent.KEYCODE_BACK)
	        {
	            Intent a = new Intent(this,MainActivity.class);
	            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(a);
	            return true;
	        }
	        return super.onKeyDown(keyCode, event);
	 }
	        
	 
    private void getCurrentDomain()
    {    	    	    	
    	final String server = dba.getConfigValue(dbHelper,"server","active='1'");
    	final String uid = dba.getConfigValue(dbHelper,"username","active='1'");
    	final TextView lblDomain = (TextView) findViewById(R.id.lblDomain);
		String domain = "";
    	/*if (server.contains("https://")){
		domain = server.replace("https://","");
		}

		else{
		domain = server.replace("http://","");
		}*/



    	//domain = domain.replace("/workplace", "");
		domain = server.replace("/workplace", "");
    	lblDomain.setText(domain + "   ( " + uid + " )");
    }
	 
	 @SuppressLint("ResourceType")
	 @SuppressWarnings("deprecation")
	public void generateButton()
	    {

	    	//1.		 			
			//int txtSize = 20;
			//int TxtPdg = 10;
			//int LayHigh = 40;
			//int LayPdg = 10;
		//1-----------------------------------------------------    	
	    	int txtSize = 0;
			int TxtPdg = 0;
			int LayHigh = 0;
			int LayPdg = 0;
			 
			DisplayMetrics dm = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(dm);
			 
			if (dm.heightPixels <= 800)
			{
				txtSize = 20;
				TxtPdg = 5;
				LayHigh = 60;
				LayPdg = 10;
			}
			else if (dm.heightPixels > 800 && dm.heightPixels <= 1024)
			{
				txtSize = 22;
				TxtPdg = 6;
				LayHigh = 70;
				LayPdg = 8;
			}
			else if (dm.heightPixels > 1024 && dm.heightPixels <= 1280)
			{
				txtSize = 25;
				TxtPdg = 7;
				LayHigh = 75;
				LayPdg = 7;
			}
			else if (dm.heightPixels > 1280 && dm.heightPixels <= 1366)
			{
				txtSize = 27;
				TxtPdg = 8;
				LayHigh = 80;
				LayPdg = 5;
			}
			else if (dm.heightPixels > 1366 && dm.heightPixels <= 1600)
			{
				txtSize = 28;
				TxtPdg = 9;
				LayHigh = 85;
				LayPdg = 3;
			}
			else
			{
				/*txtSize = 26;
				TxtPdg = 10;
				LayHigh = 90;
				LayPdg = 5;*/
			
				txtSize = 20;
				TxtPdg = 8;
				LayHigh = 100;
				LayPdg = 5;
			}
			//------------------------------------------------------ 
			
			//2.  					
			LinearLayout rl = (LinearLayout) findViewById(R.id.linearLayout1);

			//3.
			int k=0;
			for (int i=1; i<=primary; i++)
			{
				final int j = i;
				
				String id = dba.getConfigValue(dbHelper, "_ID", "_ID ='"+i+"'");
				
				if (id != null)
				{
					k=k+1;
				
					//---------------------
					
					String[] config = dba.getResultConfig(dbHelper, "*", "_ID ='"+i+"'");	    	
			    	final String usr = config[1];
			    	final String server = config[3];
			    	final int active = Integer.parseInt(config[4].toString());
			    	String domain ="";
			    	//String domain = server.replace("http://","");
			    	
			    	domain = server.replace("/workplace", "");
			    	
			    	final String strDomain = domain + "  (" + usr + ")";
			    	
			    	//if (active == 1)
			    	//	domain = domain+" **";
					
					TextView label = new TextView(this);
					label.setText(k+".  " + domain + "   ( " + usr + " )");
					label.setGravity(Gravity.CENTER_VERTICAL);
					label.setTextSize(txtSize);
					label.setPadding(TxtPdg, 0, 0, 0);
					
					if (active == 1)
						label.setTextColor(Color.BLUE);
					else
						label.setTextColor(Color.BLACK);
					
					label.setLines(5);
					label.setMovementMethod(new ScrollingMovementMethod());
					label.setId(1);
										
					int lineId = getResources().getIdentifier("cback", "drawable", getPackageName());
					ImageView line = new ImageView(this);
					line.setBackgroundResource(lineId);
					
					LinearLayout  LayIcon = new LinearLayout (this);
					LayIcon.setOrientation(LinearLayout.HORIZONTAL);
					
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LayHigh);
					layoutParams.setMargins(LayPdg, LayPdg, LayPdg, LayPdg);
					
					LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					layoutParams2.setMargins(LayPdg, LayPdg, LayPdg, LayPdg);
					
					RelativeLayout  LayButton = new RelativeLayout (this);
					
					RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, LayHigh);
					layoutParams3.setMargins(LayPdg, LayPdg, LayPdg, LayPdg);
					layoutParams3.addRule(RelativeLayout.RIGHT_OF,label.getId());
	
					RelativeLayout.LayoutParams layoutParams6 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
					layoutParams6.addRule(RelativeLayout.ALIGN_PARENT_LEFT);			
					layoutParams6.addRule(RelativeLayout.CENTER_VERTICAL);
					
					LayButton.addView(label,layoutParams6);
					
					LayIcon.addView(LayButton,layoutParams3);
					
					rl.addView(LayIcon,layoutParams);
					rl.addView(line,layoutParams2);
										
					//4.	
					if (active != 1)
					{
						label.setOnClickListener(new OnClickListener() {
							public void onClick(View arg0) {				    		
								AlertDialogU(j, strDomain);
							}
						});	
						LayIcon.setOnClickListener(new OnClickListener() {
							public void onClick(View arg0) {
								AlertDialogU(j, strDomain);
							}
						});
					}
				}
				
			}			
	    }
	 
	 
	 public void AlertDialogU(final int i, String strDomain)
	    {
	    	final CharSequence[] items = {"Yes", "No"};
	    			android.app.AlertDialog.Builder builder = new
	    			android.app.AlertDialog.Builder(this);
	    			builder.setTitle("Set '"+strDomain+"' as current domain?");
	    			builder.setItems(items, new
	    					
	    			DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog,
		    			int item) {
		    				
		    				if(item==0)
		    				{
		    					setActiveDomain(i);
		    				}
		    				else
		    				{
		    					//do nothing
		    				}
		    			}
	    			});
	    			
	    			android.app.AlertDialog alerts = builder.create();
	    			alerts.show();
	    }

	 
	    public void setActiveDomain(final int iprimary)
	    {	    	
	    	dba.UpdateAllUnActive(dbHelper);
	    	dba.UpdateActiveStat(dbHelper, iprimary);
			callclass();
	    }
	 
	    public void callclass()
	    {

	    	Intent intent1 = new Intent(this, MainActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
	        startActivity(intent1);
	        this.finish();

	    }
	 
	    public void showToast(String strMsg)
	    {
	    	Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
	    }

	public void onBackPressed(){

			this.finish();

	}
}
