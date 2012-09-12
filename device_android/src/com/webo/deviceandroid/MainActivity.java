package com.webo.deviceandroid;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.webo.deviceandroid.task.DeviceTask;

public class MainActivity extends Activity {

	TextView txtDeviceId;
	Button btnQRSnap;
	Button btnSubmit;
	AppStatus appStatus;
	String qr_code;
	String device_id;
	String device_name = "android";
	String scanResult;
	LinearLayout mLinearLayout;
	EditText editPassword;
	String date;
	String time;
	String strPassword;
	String ampm;
	public ProgressDialog mProgressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appStatus = AppStatus.getInstance(this);
    	
        setButtons();
        getCurrentDateTime();
        
    }
    
    public void getCurrentDateTime(){
    	Calendar c = Calendar.getInstance();
    	int month=c.get(Calendar.MONTH)+1;
    	int AMPM=c.get(Calendar.AM_PM);
    	
    	if (AMPM == 1){
    		ampm="AM";
    	}
    	else{
    		ampm="PM";
    	}
    	date = c.get(Calendar.YEAR) + "-" + month+ "-" + c.get(Calendar.DAY_OF_MONTH);
    	time=c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND) +" "+ampm;
    
    	Time currentTime = new Time();
    	currentTime.setToNow();
    	
    	Log.v("Date / Time", "Date:- "+date +"Time:- "+time);
    	
    	
    }
    
	public void setButtons(){
		mLinearLayout = (LinearLayout)findViewById(R.id.linearlayout);
		
        txtDeviceId=(TextView) findViewById(R.id.deviceId);
    	btnQRSnap = (Button) findViewById(R.id.btnSnap);
    	btnSubmit = (Button) findViewById(R.id.btnSubmit);
    	editPassword=(EditText) findViewById(R.id.editPassword);
        
    	device_id = Secure.getString(this.getBaseContext().getContentResolver(), Secure.ANDROID_ID);
        txtDeviceId.setText("Device ID : " + device_id);  //Device - 67cd7dcba7e0e0dc      Small- c493c979ef4d4c8e
        editPassword.setText("");
        mLinearLayout.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);
    	btnQRSnap.setVisibility(View.VISIBLE);
    	
		btnQRSnap.setOnClickListener(new View.OnClickListener() {
	
			public void onClick(View v) {
				snapBtnClicked();
	
			}
		});
		
		
		btnSubmit.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				submitBtnClicked();
	
			}
		});
	}

    public void snapBtnClicked() {
    	
		Intent intent = new Intent(MainActivity.this,
				com.google.zxing.client.android.CaptureActivity.class);
		intent.setPackage("com.google.zxing.client.android");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		startActivityForResult(intent, appStatus.REQUEST_CODE);
		
	}
    public void submitBtnClicked() {
    	strPassword = editPassword.getText().toString().trim();
    	if (strPassword.equals("")) {
			Log.i("validation: ", "Please Enter password ");
			warningDialogBox("Please Enter password ..!");
			return;
		}else{
			this.showDialog(0);	
			setDeviceData(scanResult);
		}
    }
    
	@Override
	public void onActivityResult(final int requestCode, final int resultCode,
			final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == appStatus.REQUEST_CODE) {
			if (data != null) {
				scanResult = data.getStringExtra("SCAN_RESULT");
				if (scanResult != null) {
					//appStatus.mFromQrCode = true;
					//appStatus.mQrCodeResult = scanResult;
					mLinearLayout.setVisibility(View.VISIBLE);
					btnSubmit.setVisibility(View.VISIBLE);
					btnQRSnap.setVisibility(View.GONE);
				} else {
					//appStatus.mFromQrCode = false;
					mLinearLayout.setVisibility(View.GONE);
					btnSubmit.setVisibility(View.GONE);
					btnQRSnap.setVisibility(View.VISIBLE);
					Log.v("MainActivity", "User donot exits!");
				}
			}
		} else {
			appStatus.mFromQrCode = false;
			
		}

	}

	public void setDeviceData(String scanResult) {
		
		if(appStatus.isOnline()) {
			
			Log.v("result", "scan -- "+scanResult);
			new DeviceTask(this).execute(scanResult,device_id,device_name,strPassword,date,time);
			
		} else {
			Log.v("MainActivity", "App is not online!");
			warningDialogBox("App is not online");
			this.dismissDialog(0);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog exitAlert = new AlertDialog.Builder(this).create();
			exitAlert.setTitle("Exit Application");
			exitAlert.setMessage("Are you sure you want to leave application?");

			exitAlert.setButton("Yes", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			exitAlert.show();
		}
		return true;
		}
	
	public void warningDialogBox(String warningText) {
		// TODO Auto-generated method stub

		// prepare the alert box
		AlertDialog.Builder alertbox = new AlertDialog.Builder(
				MainActivity.this);

		// set the message to display
		alertbox.setMessage(warningText);

		// add a neutral button to the alert box and assign a click listener
		alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {

			// click listener on the alert box
			public void onClick(DialogInterface arg0, int arg1) {
				// the button was clicked
			}
		});

		// show it
		alertbox.show();
	}
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setTitle("Please Wait...");
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			public void onCancel(DialogInterface arg0) {
				// TODO Auto-generated method stub
				Log.i("Webo", "user cancelling");
			}
			
		});
		mProgressDialog = dialog;
		return dialog;
	}
	public void setResponse(Boolean success){
		if(success ==true){
			//**hide keyboard
			InputMethodManager imm = (InputMethodManager)getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editPassword.getWindowToken(), 0);
			//******	
			mLinearLayout.setVisibility(View.GONE);
			btnSubmit.setVisibility(View.GONE);
			btnQRSnap.setVisibility(View.VISIBLE);
			editPassword.setText("");
			Toast toast = Toast.makeText(this,"Device assigned to user", 100000);
			toast.show();
		}else{
			
			warningDialogBox("User not valid..Please enter valid data");
		}
		this.dismissDialog(0);
	}

}
