package com.bestfunforever.prettygirl;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.thichngamgaixinh.model.Photo;

public class MainActivity extends MenuActionActivity {

	MainFragment mainFragment;
	MenuFragment menuFragment;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private FrameLayout frameLayout;
	private InterstitialAd interstitial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		Log.d("", "keyhash "+getKeyHash(getPackageName()));
		setContentView(R.layout.main_activity);
		 getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg_black));
		frameLayout = (FrameLayout) findViewById(R.id.menu_frame);
		mainFragment = new MainFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mainFragment).commit();
		menuFragment = new MenuFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, menuFragment).commit();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.app_name,
				R.string.app_name) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getSupportActionBar().setTitle(getString(R.string.app_name));
				getSupportActionBar().hide();
				mainFragment.changepageIfNeed();
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().show();
				getSupportActionBar().setTitle(getString(R.string.app_name));
				if(mainFragment!=null){
					mainFragment.setBackCount(0);
				}
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		loadInterstitialAd();
	}

	public void loadData() {
		mainFragment.loadData();
		
	}

	public void bindCommentDataToView(Photo photo, int pos) {
		mainFragment.bindCommentDataToView(photo, pos);
	}

	public void setPage(int i) {
		mainFragment.setPage(i);
	}

	public void updateCommentPhotoObject(int postionOfPhoto) {
		mainFragment.updateCommentPhotoObject(postionOfPhoto);
	}

	public void updatePhotoObject(int postionOfPhoto, boolean likeSuccess) {
		mainFragment.updatePhotoObject(postionOfPhoto, likeSuccess);
	}

	public void toogleActionBar() {
		mainFragment.toogleActionBar();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mainFragment != null&&mainFragment.onOptionsItemSelected(item)) {
			return false;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (mainFragment != null&&!mainFragment.onBackPressed()) {
		} else
			super.onBackPressed();
	}

	public void setPositionPage(int arg2) {
		if (mainFragment != null) {
			mainFragment.setPosPage(arg2);
		}
	}

	public void toogleDrawer() {
		// TODO Auto-generated method stub
		if (mDrawerLayout.isDrawerOpen(frameLayout)) {
			mDrawerLayout.closeDrawers();
		} else {
			mDrawerLayout.openDrawer(frameLayout);
		}
	}
	

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		if (mainFragment != null) {
			mainFragment.onOptionsMenuClosed(menu);
		}
		super.onOptionsMenuClosed(menu);
	}

	public void loadInterstitialAd() {
		interstitial = new InterstitialAd(this);
	    interstitial.setAdUnitId("ca-app-pub-2714906120093430/6978388901");
	    
	    interstitial.setAdListener(new AdListener() {
	        @Override
	        public void onAdLoaded() {
	          Log.d("", "loadInterstitialAd onAdLoaded");
	        }

	        @Override
	        public void onAdFailedToLoad(int errorCode) {
	          String message = String.format("onAdFailedToLoad (%s)", getErrorReason(errorCode));
	          Log.d("", "loadInterstitialAd "+message);
	        }
	      });

	    // Create ad request.
	    AdRequest adRequest = new AdRequest.Builder().build();

	    // Begin loading your interstitial.
	    interstitial.loadAd(adRequest);
	}
	
	private String getErrorReason(int errorCode) {
	    String errorReason = "";
	    switch(errorCode) {
	      case AdRequest.ERROR_CODE_INTERNAL_ERROR:
	        errorReason = "Internal error";
	        break;
	      case AdRequest.ERROR_CODE_INVALID_REQUEST:
	        errorReason = "Invalid request";
	        break;
	      case AdRequest.ERROR_CODE_NETWORK_ERROR:
	        errorReason = "Network Error";
	        break;
	      case AdRequest.ERROR_CODE_NO_FILL:
	        errorReason = "No fill";
	        break;
	    }
	    return errorReason;
	  }

	public void showInterstitialAd() {
		Log.d("", "showInterstitialAd "+interstitial.isLoaded());
		 if (interstitial.isLoaded()) {
		      interstitial.show();
		    }
	}

}
