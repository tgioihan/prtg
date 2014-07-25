package com.bestfunforever.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

public class CommonUtils {
	private static final String TAG = CommonUtils.class.getName();
	
	public static final String EMAIL_REGEX = "^[_A-Za-z0-9]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public static String convertUnixTime(int time) {
		if (time == 0) {
			return "";
		}
		Date date = new Date(Long.valueOf(time) * 1000);
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String returnDate = format.format(date);
		return returnDate;
	}

	public static String inputStreamToString(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();
		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		// Read response until the end
		try {
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			Log.e(TAG, "Error read data");
			return null;
		}
		// Return full string
		Log.e(TAG, total.toString());
		return total.toString();
	}

	public static String getDeviceId(Context context) {
		TelephonyManager tManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String device_id = tManager.getDeviceId();
		Log.d("Device Id", device_id);
		return device_id;
	}

	public static String getDeviceVersion() {
		Log.d(TAG, android.os.Build.VERSION.RELEASE);
		return android.os.Build.VERSION.RELEASE;
	}

	public static String getDeviceOs() {
		return "android";
	}

	public static String getVendor() {
		Log.d(TAG, android.os.Build.MANUFACTURER);
		return android.os.Build.MANUFACTURER;
	}

	public static String getPhoneNumber(Context context) {
		TelephonyManager tMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tMgr.getLine1Number();
	}

	public static float convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	/**
	 * This method converts device specific pixels to device independent pixels.
	 * 
	 * @param px
	 *            A value in px (pixels) unit. Which we need to convert into db
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent db equivalent to px value
	 */
	public static float convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;

	}

	// Check whether the device's network is available.
	public static boolean checkNetworkAvaliable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null) {
			if (networkInfo.isConnected()) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkValidEmail(String email) {
		if (email == null) {
			return false;
		}

		String inputStr = email.trim();
		Pattern pattern = Pattern.compile(EMAIL_REGEX,
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);

		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	public static String getClientVersion(Context context) {
		try {
			Log.d(TAG,
					context.getPackageManager().getPackageInfo(
							context.getPackageName(), 0).versionName
							+ "");
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return "1.0";
	}

	public static int getClientVersionCode(Context context) {
		try {
			Log.d(TAG,
					context.getPackageManager().getPackageInfo(
							context.getPackageName(), 0).versionCode
							+ "");
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return 1;
	}

	public static void writetoFile(String str, Context context) {
		String path = "/mnt/sdcard/" + "test.txt";
		try {

			BufferedWriter out = new BufferedWriter(new FileWriter(path));
			out.write(str);
			out.close();
		} catch (IOException e) {
			System.out.println("Exception " + e.toString());
		}

	}

	public static int getWidthScreen(Context context) {
		DisplayMetrics metric = context.getResources().getDisplayMetrics();
		int width = metric.widthPixels;
		return width;
	}

	public static int getHeightScreen(Context context) {
		DisplayMetrics metric = context.getResources().getDisplayMetrics();
		int height = metric.heightPixels;
		return height;
	}
}
