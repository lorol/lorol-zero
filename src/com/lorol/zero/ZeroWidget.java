package com.lorol.zero;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

/*
 * This class the is the class that provides the service to the extension
 */
public class ZeroWidget extends DashClockExtension {

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onCreate()
	 */
	public void onCreate() {

		super.onCreate();
		Log.d("ZeroWidget", "Created");

	}

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onUpdateData(int)
	 */
	@Override
	protected void onUpdateData(int arg0) {
		
		boolean allGood = false;
		
		ExtensionData edtInformation = new ExtensionData();
		edtInformation.visible(false);
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		Log.d("ZeroWidget", "Running");
		Log.d("ZeroWidget", "Checking if we have internet connectivity");

		NetworkInfo nifNetwork = connectivityManager.getActiveNetworkInfo();
		if (nifNetwork != null && nifNetwork.isConnected()) {

			Log.d("ZeroWidget", "We are connected to the internet");
			Log.d("ZeroWidget", "Fetching the page");

			try {
			    URL url = new URL("http://minooch.com/franciscofranco/Nexus4/4.2.2/appfiles/nightly-changelog");			
				
				Log.v("ZeroWidget", "Feched page");
				Log.d("ZeroWidget", "Scraping information from page");
				
				try {

					// Read all the text returned by the server
				    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
				    String str, strev = "---*";
				    strev = in.readLine();
				    int i = 0;
					while (((str = in.readLine()) != null) && (i < 5)){
						if (!str.isEmpty()){	
							edtInformation
								.expandedBody((edtInformation.expandedBody() == null ? ""
								: edtInformation.expandedBody() + "\n")
								+ str);
							i++;
						}
				    }
				    in.close();
				    edtInformation.status(String.format(getString(R.string.status), strev));
					Log.d("ZeroWidget", "Publishing update");
					edtInformation.visible(true);
					allGood = true;
					Log.d("ZeroWidget", "Done");
				    
				} catch (MalformedURLException e) {
					Log.e("ZeroWidget", "URL problems", e);
					
				} catch (IOException e) {
					Log.e("ZeroWidget", "Unable to connect to website", e);
				}
				

			} catch (IOException e) {
				Log.e("ZeroWidget", "Unable to connect", e);

			}

		} else {
			Log.v("ZeroWidget", "Not connected to the internet");
		}
		if (!allGood){
			edtInformation.status("Check ---*");
			edtInformation.visible(true);
		}
//		edtInformation.clickIntent(new Intent(Intent.EXTRA_INTENT));
		edtInformation.clickIntent(null);
		edtInformation.icon(R.drawable.ic_dashclock);
		publishUpdate(edtInformation);
		Log.d("ZeroWidget", "Done");

	}

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onDestroy()
	 */
	public void onDestroy() {

		super.onDestroy();
		Log.d("ZeroWidget", "Destroyed");
//

	}

}