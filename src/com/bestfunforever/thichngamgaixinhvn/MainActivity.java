package com.bestfunforever.thichngamgaixinhvn;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;

import com.actionbarsherlock.view.MenuItem;
import com.bestfunforever.activity.facebook.BaseFacebookActivity;
import com.thichngamgaixinh.model.Photo;

public class MainActivity extends BaseFacebookActivity {

	MainFragment mainFragment;
	MenuFragment menuFragment;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private FrameLayout frameLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
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
				mainFragment.changepageIfNeed();
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle(getString(R.string.app_name));
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
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
		if (mainFragment != null) {
			return mainFragment.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (mainFragment != null) {
			mainFragment.onBackPressed();
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

}
