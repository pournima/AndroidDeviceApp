package com.webo.deviceandroid.webservice;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.content.Context;
import android.util.Log;
import com.webo.deviceandroid.task.DeviceResult;

public class DeviceWebService implements WebServiceIface {

	static List<DeviceResult> result = null;

	public static String setDeviceData(Context context,String[] device_params){
		
		WebService webService = new WebService(BASE_URL);

        List<NameValuePair> params = new ArrayList<NameValuePair>(6);
        params.add(new BasicNameValuePair("qr_code", device_params[0]));
        params.add(new BasicNameValuePair("device_id", device_params[1]));
        params.add(new BasicNameValuePair("device_name",device_params[2]));
        params.add(new BasicNameValuePair("password",device_params[3]));
        params.add(new BasicNameValuePair("date",device_params[4]));
        params.add(new BasicNameValuePair("time",device_params[5]));
        
        String response = webService.webPost("", params);
        if(response != null)
            Log.e("WEBO", "Device Result "+ response.toString());

        return response;
	}

	
}
