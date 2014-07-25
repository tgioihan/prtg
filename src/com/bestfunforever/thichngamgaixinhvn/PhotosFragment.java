package com.bestfunforever.thichngamgaixinhvn;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.internal.fn;
import com.thichngamgaixinh.model.Photo;

public class PhotosFragment extends Fragment {

	private HackyViewPager mViewPager;
	private ArrayList<Photo> photos;
	private PagerAdapter adapter;
	private TextView title;

	// flag add temp photo to load new photo
	private boolean is_add_temp_photo = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.photo_fragment, null);
		initView(view);
		return view;
	}

	private void initView(View view) {
		mViewPager = (HackyViewPager) view.findViewById(R.id.view_pager);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				onPageChange(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		title = (TextView) view.findViewById(R.id.title);
	}

	/**
	 * event happen when view pager change
	 * 
	 * @param pos
	 *            : position of page
	 */
	protected void onPageChange(int pos) {
		Log.e("", "onPageChange " + pos + " " + photos.size());
		if (pos == photos.size() - 1) {
			if (!is_add_temp_photo) {
				Photo photo = new Photo();
				photo.setLike_count(0);
				photo.setLike_count(0);
				photo.setName("");
				photo.setSource("");
				photos.add(photo);
				is_add_temp_photo = true;
				adapter.notifyDataSetChanged();
			} else {
				mViewPager.setPagingEnabled(false);
				((MainActivity) getActivity()).loadData();
			}
		}
		title.setText(photos.get(pos).getName());
		((MainActivity) getActivity()).bindCommentDataToView(photos.get(pos), pos);
	}

	private class PagerAdapter extends FragmentStatePagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			Log.e("", "getItem " + arg0);
			ImageFragment imageFragment = new ImageFragment();
			imageFragment.setSource(photos.get(arg0).getSource());
			imageFragment.setPostion(arg0);
			return imageFragment;
		}

		@Override
		public int getCount() {
			return photos.size();
		}

		// force adapter recreate item when notify data
		@Override
		public int getItemPosition(Object object) {

			return POSITION_NONE;
		}

	}

	public void bindData(ArrayList<Photo> photos2) {
		this.photos = photos2;
		Log.e("", "bindData " + " " + photos.size());
		if (photos.size() > 0) {
			if (mViewPager.getAdapter() == null) {
				adapter = new PagerAdapter(getActivity().getSupportFragmentManager());
				mViewPager.setAdapter(adapter);
			} else {
				Log.e("", "notifidatasetchange ");
				adapter.notifyDataSetChanged();
			}
		}
		is_add_temp_photo = false;
		mViewPager.setPagingEnabled(true);
		mViewPager.setVisibility(View.VISIBLE);
		onPageChange(0);
	}

	/**
	 * @return current photo postion
	 */
	public int getCurrentPage() {
		if (mViewPager != null) {
			return mViewPager.getCurrentItem();
		}
		return 0;
	}
}
