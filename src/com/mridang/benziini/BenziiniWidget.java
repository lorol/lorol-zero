package com.mridang.benziini;

import java.io.IOException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

/*
 * This class the is the class that provides the service to the extension
 */
public class BenziiniWidget extends DashClockExtension {

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onCreate()
	 */
	public void onCreate() {

		super.onCreate();
		Log.d("BenziiniWidget", "Created");
		BugSenseHandler.initAndStartSession(this, "2692cc94");

	}

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onUpdateData(int)
	 */
	@Override
	protected void onUpdateData(int arg0) {
		
		ExtensionData edtInformation = new ExtensionData();
		edtInformation.visible(false);
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		Log.d("BenziiniWidget", "Running");
		Log.d("BenziiniWidget", "Checking if we have internet connectivity");

		NetworkInfo nifNetwork = connectivityManager.getActiveNetworkInfo();
		if (nifNetwork != null && nifNetwork.isConnected()) {

			Log.d("BenziiniWidget", "We are connected to the internet");
			Log.d("BenziiniWidget", "Fetching prices from the page");

			try {

				Document docPage = Jsoup.connect("http://www.polttoaine.net/index.php?cmd=kaikki").get();

				Log.v("BenziiniWidget", "Feched page without any issues");
				Log.d("BenziiniWidget", "Scraping information from page");

				try {
					
					Elements eleAverages = docPage.select("td.Keskihinnat").first().siblingElements();
					Log.v("BenziiniWidget", "Petrol 95E: " + eleAverages.get(1).select("span").first().html());
					Log.v("BenziiniWidget", "Petrol 98E: " + eleAverages.get(2).html());
					Log.v("BenziiniWidget", "Diesel    : " + eleAverages.get(3).html());
					
					edtInformation
							.expandedBody((edtInformation.expandedBody() == null ? ""
									: edtInformation.expandedBody() + "\n")
									+ String.format(
											getString(R.string.petrol98),
											eleAverages.get(2).html()));
					
					edtInformation
							.expandedBody((edtInformation.expandedBody() == null ? ""
									: edtInformation.expandedBody() + "\n")
									+ String.format(
											getString(R.string.petrol95),
											eleAverages.get(1).select("span")
													.first().html()));
					
					edtInformation
					.expandedBody((edtInformation.expandedBody() == null ? ""
							: edtInformation.expandedBody() + "\n")
							+ String.format(
									getString(R.string.diesel),
									eleAverages.get(3).html()));

					Log.d("BenziiniWidget", "Publishing update");
					edtInformation.status(getString(R.string.status));
					edtInformation.visible(true);
					Log.d("BenziiniWidget", "Done");

				} catch (Exception e) {
					BugSenseHandler.sendException(e);
				}

			} catch (IOException e) {
				Log.e("BenziiniWidget", "Unable to connect to website", e);

				if (e instanceof HttpStatusException) {

					if (((HttpStatusException) e).getStatusCode() >= 400 && ((HttpStatusException) e).getStatusCode() <= 599) {
						BugSenseHandler.sendException(e);
					}

				}

			}

		} else {
			Log.v("BenziiniWidget", "Not connected to the internet");
		}

		edtInformation.icon(R.drawable.ic_dashclock);
		publishUpdate(edtInformation);
		Log.d("BenziiniWidget", "Done");

	}

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onDestroy()
	 */
	public void onDestroy() {

		super.onDestroy();
		Log.d("BenziiniWidget", "Destroyed");
		BugSenseHandler.closeSession(this);

	}

}