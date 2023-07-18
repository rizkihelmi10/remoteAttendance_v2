package com.smartlab.remoteattendance_v2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
//import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class serverlist  extends Activity {
	
	MySQLiteHelper dbHelper; 
    SQLiteDatabase db;
    dbActivities dba;
	String sect;
	int primary;
	private String m_Text = "";
	private Boolean wantToCloseDialog = false;
	CheckBox chkFP;

	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	                                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
	        setContentView(R.layout.configlist);
		  	chkFP = (CheckBox)findViewById(R.id.chkFPActive);
	        
	        //auto generate the list of inserted properties
	        
	        //1. check either the database already get data or not (If not, create database)
	        dba = new dbActivities();
	        dbHelper = new MySQLiteHelper(this);

	    	primary = dba.checkConfigDB(dbHelper);
	        
	    	//2. get current domain
	    	getCurrentDomain();
	    	
	        //3. generate button	        
	        generateButton();

		 int iCount = dba.countConfigDB(dbHelper);


		 Button btnAddDomain = (Button) findViewById(R.id.btnAddDomain);

		 Button btnChangeDomain = (Button) findViewById(R.id.btnChangeDomain);

		 if (iCount> 1){
			 btnChangeDomain.setVisibility(View.VISIBLE);
		 }
		 else{
			 btnChangeDomain.setVisibility(View.INVISIBLE);
		 }

	        btnAddDomain.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
		    		callclass();
				}
			});


		 btnChangeDomain.setOnClickListener(new OnClickListener() {
			 public void onClick(View arg0) {
				 changedomain();
			 }
		 });

		 if(checkFPAuth()){
			 chkFP.setChecked(true);
		 }
		 else
		 {
			 chkFP.setChecked(false);
		 }
	 }

	 //checkbox FP
	public void onCheckboxClicked(View view) {

		// Is the view now checked?
		boolean checked = ((CheckBox) view).isChecked();
		// Check which checkbox was clicked

		// Logic when user canceled operation

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Workplace Authentication");


		if(chkFP.isChecked()){
			builder.setMessage("This will enable your fingerprint authentication. Please fill-up your password to proceed or cancel to dismiss");
		}
		else{
			builder.setMessage("This will disable your fingerprint authentication. Please fill-up your password to proceed or cancel to dismiss");
		}


		// Set up the input
		final EditText input = new EditText(this);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// m_Text = input.getText().toString();


			}
		});

		builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// m_Text = input.getText().toString();


			}
		});



		final AlertDialog dialog = builder.create();
		dialog.show();

		//Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//Do stuff, possibly set wantToCloseDialog to true then...
                boolean bRetBol = false;
				m_Text = input.getText().toString();
                bRetBol = validatepassword(m_Text);
				//showToast(""+ bRetBol);
				if (bRetBol){
					wantToCloseDialog = true;

					if(chkFP.isChecked()){
						dba.UpdateSetting(dbHelper,1,1);
					}
					else{
						dba.UpdateSetting(dbHelper,1,0);
					}
				}
				else{
					showToast("Wrong password!..Please try again");
				}
				if(wantToCloseDialog)
					dialog.dismiss();


				//else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
			}
		});

		dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Boolean wantToCloseDialog = true;
				//Do stuff, possibly set wantToCloseDialog to true then...
				if(wantToCloseDialog)
					dialog.dismiss();

                if(chkFP.isChecked()){
                    chkFP.setChecked(false);
                }

                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
			}
		});

	}

	public boolean checkFPAuth(){
		boolean bRetBol = false;
		int result= 0 ;

		int iCount = dba.countSettingDB(dbHelper);

		if (iCount > 0){
			result = dba.getSettingValue(dbHelper, "active",
					"_ID='1'");

			if (result == 1){
				bRetBol = true;
			}
		}

		return bRetBol;
	}

	//
	 private void changedomain(){


		 Intent intent = new Intent(this, DomainList.class);
		 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		 startActivity(intent);
		 finish();
	 }

	public void showToast(String toast)
	{
		Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
	}

    public boolean validatepassword(String pwd){

        String usr = "";
        String sPwd = "";
        boolean bRetBol = false;

        String[] config = dba.getResultConfig(dbHelper, "*", "active='1'");

        //String usr = config[1];
        sPwd = config[2];

        if(sPwd.equals(pwd)){
            bRetBol = true;
        }

        return bRetBol;
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
		domain = server.replace("/workplace", "");
    	lblDomain.setText(domain + "   ( " + uid + " )");
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) { //  syukri add the line
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent a = new Intent(this,MainActivity.class);
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    
	 @SuppressLint("ResourceType")
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
			
			int imgId = getResources().getIdentifier("remove", "drawable", getPackageName());
			
			//DisplayMetrics dm = new DisplayMetrics();
	        //getWindowManager().getDefaultDisplay().getMetrics(dm);
			
	        int iLabelW = dm.widthPixels - 60;
	        
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
			    	final String strDom;
					String domain = "";
					/*if (server.contains("https://")){
						domain = server.replace("https://","");
					}

					else{
						domain = server.replace("http://","");
					}*/
			    	//domain = domain.replace("/workplace", "");
					domain = server.replace("/workplace", "");
			    	strDom = domain + "  (" + usr + ")";
			    	//if (active == 1)
			    	//	domain = domain+"  **";
			    	
					
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
					
					RelativeLayout LayButton = new RelativeLayout (this);
					
					RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, LayHigh);
					layoutParams3.setMargins(LayPdg, LayPdg, LayPdg, LayPdg);
					layoutParams3.addRule(RelativeLayout.RIGHT_OF,label.getId());
					
					/*					
					Button upload = new Button(this);
					upload.setText("Edit");					
					upload.setId(2);
					*/
										
					if (active != 1)
					{
						RelativeLayout.LayoutParams layoutParams6 = new RelativeLayout.LayoutParams(iLabelW, RelativeLayout.LayoutParams.FILL_PARENT);
						layoutParams6.addRule(RelativeLayout.ALIGN_PARENT_LEFT);			
						layoutParams6.addRule(RelativeLayout.CENTER_VERTICAL);
															
						LayButton.addView(label,layoutParams6);
						
						
						ImageView remove = new ImageView(this);
						remove.setImageResource(imgId);
						remove.setId(3);
						
						RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(40, 40);
						layoutParams4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						layoutParams4.addRule(RelativeLayout.CENTER_VERTICAL);
						layoutParams4.addRule(RelativeLayout.RIGHT_OF, label.getId());
							
						LayButton.addView(remove,layoutParams4);
						
						remove.setOnClickListener(new OnClickListener() {
							public void onClick(View arg0) {
								//pop up alert confirmation to delete
								AlertDialogR(j, strDom);
							}
						});
					}
					else
					{
						RelativeLayout.LayoutParams layoutParams6 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
						layoutParams6.addRule(RelativeLayout.ALIGN_PARENT_LEFT);			
						layoutParams6.addRule(RelativeLayout.CENTER_VERTICAL);
															
						LayButton.addView(label,layoutParams6);
					}
	
					LayIcon.addView(LayButton,layoutParams3);
					
					rl.addView(LayIcon,layoutParams);
					rl.addView(line,layoutParams2);
										
					//4.									
					//final Intent intent1 = new Intent(this, Config.class);
					label.setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) {				    		
				    		//intent1.putExtra("sfromMain", "");
				    		//intent1.putExtra("primary", j); 
					        //startActivity(intent1);
					        //finish();
							AlertDialogU(j, strDom);
						}
					});	
					LayIcon.setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) {
							//intent1.putExtra("sfromMain", "");
							//intent1.putExtra("primary", j);
							//startActivity(intent1);
							//finish();
							AlertDialogU(j, strDom);
						}
					});
					//------------------------------------
				}
				
			}			
	    }
	  
	 public void AlertDialogU(final int i,final String strDom)
	    {
	    	final CharSequence[] items = {"Yes", "No"};
	    			android.app.AlertDialog.Builder builder = new
	    			android.app.AlertDialog.Builder(this);
	    			builder.setTitle("UPDATE '"+strDom+"'?");
	    			builder.setItems(items, new
	    					
	    			DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog,
		    			int item) {
		    				
		    				if(item==0)
		    				{
		    					callclassU(i);
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
	 
	 public void AlertDialogR(final int i, final String strDom)
	    {
	    	final CharSequence[] items = {"Yes", "No"};
	    			android.app.AlertDialog.Builder builder = new
	    			android.app.AlertDialog.Builder(this);
	    			builder.setTitle("REMOVE '"+strDom+"' from your domain list?");
	    			builder.setItems(items, new
	    					
	    			DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog,
		    			int item) {
		    				
		    				if(item==0)
		    				{
		    					Log.i("[before] deleteConfigById", "_ID = "+i);
		    					 //delete from database
		    					delConfig(i);
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
	 
	 public void callclass()
	    {
	    	Intent intent1 = new Intent(this, Config.class); 
		intent1.putExtra("sfromMain", "");
		intent1.putExtra("primary", ""); 
	        startActivity(intent1);
	        finish();
	    }
	 
	 public void callclassU(int iPrimary)
	    {
	    	Intent intent1 = new Intent(this, Config.class); 
    		intent1.putExtra("sfromMain", "");
    		intent1.putExtra("primary", iPrimary); 
	        startActivity(intent1);
	        finish();
	    }
	 
	 private void delConfig(int iPrimary)
		 {
			dba.deleteConfigById(iPrimary);
			//int iPrimary=dba.checkConfigDB(dbHelper);
			callclassR(iPrimary);
		 }
	 
	 public void callclassR(int iPrimary)
	    {
	    	Intent intent1 = new Intent(this, serverlist.class); 
	    	intent1.putExtra("sfromMain", "");
	    	intent1.putExtra("primary", ""); 
	        startActivity(intent1);
	        finish();
	    }
	 
}
