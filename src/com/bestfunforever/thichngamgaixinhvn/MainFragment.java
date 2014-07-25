package com.bestfunforever.thichngamgaixinhvn;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.bestfunforever.activity.facebook.ILoginFacebook;
import com.bestfunforever.util.CommonUtils;
import com.bestfunforever.view.dialog.DialogUtil;
import com.bestfunforever.view.slidingview.SlidingView;
import com.bestfunforever.view.slidingview.SlidingView.PageChangeListenner;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdView;
import com.thichngamgaixinh.model.Photo;

/**
 * @author Nguyen Xuan Tuan
 * 
 */
public class MainFragment extends Fragment {

	private int posPage;

	private LinearLayout mLoadingBg;
	private SlidingView slidingView;

	private AdView mAdView;

	private int[] mPageOffsets = new int[Config.arr.length];
	private ArrayList<Photo> photos = new ArrayList<Photo>();

	// gia tri size cac object trong mang du lieu tra ve
	private int[] mObjectCounts = new int[Config.arr.length];
	private int oldPhotoSize;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.main_fragment, null);
		initView(view);
		initPageOffsets();
		if (CommonUtils.checkNetworkAvaliable(getActivity())) {
			if (Session.getActiveSession() != null && Session.getActiveSession().isOpened()) {
				loadData();
			} else {
				((MainActivity) getActivity()).loginFacebook(null, null, new ILoginFacebook() {

					@Override
					public void onLoginFacebookSuccess() {
						loadData();
					}

					@Override
					public void onLoginFacebookFail(Session session, SessionState state, Exception exception) {
						showMessageDialog(R.string.error_connection, R.string.error_connection_msg);
					}
				});
			}

		} else {
			showMessageDialog(R.string.error_connection, R.string.error_connection_msg);
		}

		// initMobileCore(getString(R.string.mobilecorekey));
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(getActivity()).activityStart(getActivity()); // Add
																				// this
																				// method.
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(getActivity()).activityStop(getActivity()); // Add
																			// this
																			// method.
	}

	/**
	 * back count to quit app
	 */
	private int backCount;

	public void onBackPressed() {
		if (slidingView.getCurrentItem() != 0) {
			slidingView.setCurrentPage(0);
		} else if (backCount < 1) {
			backCount++;
			Toast.makeText(getActivity(), getString(R.string.backclick), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * init page offset for facebook load data
	 */
	private void initPageOffsets() {
		for (int i = 0; i < Config.arr.length; i++) {
			mPageOffsets[i] = 0;
		}
	}

	/**
	 * @param title
	 *            : title of dialog
	 * @param msg
	 *            : message of dialog
	 */
	public void showMessageDialog(int title, int msg) {
		DialogUtil.showMessageDialog(getActivity(), title, msg, getString(R.string.Ok), new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
	}

	/**
	 * load data from facebook
	 */
	public void loadData() {
		if (CommonUtils.checkNetworkAvaliable(getActivity())) {
			String fqlQuery = "{";
			fqlQuery = fqlQuery.concat("'photos" + Config.arr[posPage] + "':");
			fqlQuery = fqlQuery
					.concat("'select caption,images,created,object_id,link,created,album_object_id from photo where album_object_id="
							+ Config.arr_id[posPage] + " limit 25 offset " + mPageOffsets[posPage] + "',");
			fqlQuery = fqlQuery.concat("'commentlikeinfo" + Config.arr[posPage] + "':");
			fqlQuery = fqlQuery.concat("'select like_info, comment_info from photo where album_object_id="
					+ Config.arr_id[posPage] + " limit 25 offset " + mPageOffsets[posPage] + "',");
			fqlQuery = fqlQuery.concat("}");
			Log.e("", "fqlquery " + fqlQuery);
			Bundle params = new Bundle();
			params.putString("q", fqlQuery);
			Request request = new Request(Session.getActiveSession(), "/fql", params, HttpMethod.GET,
					new Request.Callback() {

						@Override
						public void onCompleted(Response response) {
							initListPhoto(response);
						}
					});
			RequestAsyncTask asyncTask = new RequestAsyncTask(request);
			asyncTask.execute();
		} else
			showMessageDialog(R.string.error_connection, R.string.error_connection_msg);
	}

	/**
	 * init photos data from facebook api response
	 * 
	 * @param response
	 *            : response from facebook api
	 */
	protected void initListPhoto(Response response) {
		GraphObject graphObject = response.getGraphObject();
		Log.d("", "initListPhoto rres " + response.toString());
		if (graphObject != null) {
			JSONObject grap = graphObject.getInnerJSONObject();
			if (grap != null) {
				try {
					JSONArray array = grap.getJSONArray("data");
					if (array.length() > 0) {
						initCachePhotos(array);

						for (int i = 0; i < array.length(); i++) {
							JSONObject object = array.getJSONObject(i);
							String nameobject = object.getString("name");
							JSONArray fqlresult = object.getJSONArray("fql_result_set");

							// kiem tra name object tuong ung voi mang cac page
							for (int j = 0; j < Config.arr.length; j++) {
								if (nameobject.equals("photos" + Config.arr[j])) {
									bindImageDataForPhoto(oldPhotoSize, mObjectCounts[j], fqlresult);
								} else if (nameobject.equals("commentlikeinfo" + Config.arr[j])) {
									bindCommentDataForPhoto(oldPhotoSize, mObjectCounts[j], fqlresult);
								}
							}
						}

						indexOffset(array);
						if (photos.size() == 0) {
							DialogUtil.showMessageDialog(getActivity(), getString(R.string.error_loaddata_title),
									getString(R.string.error_loaddata_msg), getString(R.string.Ok),
									new OnClickListener() {

										@Override
										public void onClick(View arg0) {
										}
									});
						} else {
							Collections.sort(photos, new TimePhotoComparator());
							Log.e("", "photo size " + photos.size());
							PhotosFragment mainFragment = (PhotosFragment) getActivity().getSupportFragmentManager()
									.findFragmentById(R.id.frame1);
							mainFragment.bindData(photos);
							if (mLoadingBg.getVisibility() == View.VISIBLE) {
								Animation animation_left_right = AnimationUtils.loadAnimation(getActivity(),
										R.anim.slide_right_to_left);
								mLoadingBg.startAnimation(animation_left_right);
								mLoadingBg.setVisibility(View.GONE);
							}
						}
						handler.removeCallbacks(hideActionBarRunnaable);
						handler.postDelayed(hideActionBarRunnaable, 2000);
						loadAds();
						needChange = false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else {

		}
	}

	/**
	 * load admod
	 */
	private void loadAds() {
		com.google.android.gms.ads.AdRequest adRequest = new com.google.android.gms.ads.AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
	}

	/**
	 * reindes page offset list
	 * 
	 * @param array
	 *            : json array of data
	 */
	private void indexOffset(JSONArray array) {
		try {
			for (int i = 0; i < Config.arr.length; i++) {
				int tmp = array.getJSONObject(i).getJSONArray("fql_result_set").length();
				mPageOffsets[i] += tmp;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * bind comment data for photos
	 * 
	 * @param oldPhotoSize
	 *            : old value of photos's size
	 * @param pos
	 *            : postion of photo need to set
	 * @param fqlresult
	 * @throws JSONException
	 */
	private void bindCommentDataForPhoto(int oldPhotoSize, int pos, JSONArray fqlresult) throws JSONException {
		for (int i = 0; i < fqlresult.length(); i++) {
			JSONObject object1 = fqlresult.getJSONObject(i);
			JSONObject like_info = object1.getJSONObject("like_info");
			boolean user_like = like_info.getBoolean("user_likes");
			int tmp = i + pos + oldPhotoSize;
			photos.get(tmp).setIs_user_like(user_like);
			int like_count = like_info.getInt("like_count");
			JSONObject comment_info = object1.getJSONObject("comment_info");
			int comment_count = comment_info.getInt("comment_count");
			photos.get(tmp).setLike_count(like_count);
			photos.get(tmp).setComment_count(comment_count);
		}
	}

	/**
	 * bind image data for photos
	 * 
	 * @param oldPhotoSize
	 *            : old value of photos's size
	 * @param pos
	 *            : postion of photo need to set
	 * @param fqlresult
	 * @throws JSONException
	 */
	private void bindImageDataForPhoto(int oldPhotoSize, int pos, JSONArray fqlresult) throws JSONException {
		int temp_photo_count = fqlresult.length();
		for (int i = 0; i < temp_photo_count; i++) {
			JSONObject object1 = fqlresult.getJSONObject(i);
			JSONArray imgArray = object1.getJSONArray("images");
			String source = "";
			if (imgArray.length() > 0) {
				JSONObject mJson = imgArray.getJSONObject(0);
				source = mJson.getString("source");
			}
			String link = object1.getString("link");

			String caption = object1.getString("caption");
			String photo_id = object1.getString("object_id");

			int tmp = i + pos + oldPhotoSize;
			photos.get(tmp).setLink(link);
			photos.get(tmp).setId(String.valueOf(photo_id));
			photos.get(tmp).setName(caption);
			photos.get(tmp).setSource(source);
			int created = object1.getInt("created");
			photos.get(tmp).setCreated(created);
			String album_id = object1.getString("album_object_id");
			photos.get(tmp).setAlbum_object_id(album_id);

		}
	}

	/**
	 * init list cache photo for new data
	 * 
	 * @param array
	 *            : json array of data
	 */
	private void initCachePhotos(JSONArray array) {
		int objectCount = 0;
		try {
			int objectCountTmp = 0;
			for (int i = 0; i < Config.arr.length; i++) {
				int tmp = array.getJSONObject(i).getJSONArray("fql_result_set").length();
				objectCount += tmp;
				mObjectCounts[i] = objectCountTmp;
				objectCountTmp += tmp;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (objectCount > 0) {
			int tmp = 0;
			if (photos.size() == 0) {
				oldPhotoSize = 0;
				tmp = objectCount;
			} else {
				tmp = objectCount - 1;
				oldPhotoSize = photos.size() - 1;
			}

			for (int i = 0; i < tmp; i++) {
				Photo photo = new Photo();
				photos.add(photo);
			}
		}
	}

	/**
	 * init view
	 */
	private void initView(View view) {
		mAdView = (AdView) view.findViewById(R.id.adView);
		// mAdView.setAdSize(AdSize.BANNER);
		// mAdView.setAdUnitId("a1532d278f04383");

		slidingView = (SlidingView) view.findViewById(R.id.slide);
		mLoadingBg = (LinearLayout) view.findViewById(R.id.imgloading);
		slidingView.setContentView(R.layout.frame1, 0);
		slidingView.setBottomView(R.layout.frame2, getResources().getDimensionPixelSize(R.dimen.bottom_offset));
		slidingView.setBottomTouchAllow(getResources().getDimensionPixelSize(R.dimen.bottom_offset));

		slidingView.setControlEnable(true);
		slidingView.setControlDrawableId(R.drawable.toogle_icon);

		slidingView.setControlClickListenner(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				slidingView.toogle();
			}
		});

		PhotosFragment mainFragment = new PhotosFragment();
		getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame1, mainFragment).commit();

		CommentFragment commentFragment = new CommentFragment();
		getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame2, commentFragment).commit();

		slidingView.setOnPageChangeListenner(new PageChangeListenner() {

			@Override
			public void onPageSelected(int currentItem) {
				Log.e("", "onPageSelected " + currentItem);
				if (currentItem == 1) {
					Log.e("", "comeent frag display");
					CommentFragment commentFragment = (CommentFragment) getActivity().getSupportFragmentManager()
							.findFragmentById(R.id.frame2);
					if (commentFragment != null) {
						commentFragment.loadCommentToView();
					}
					backCount = 0;
				}
			}

			@Override
			public void onPageScrolled(float percentOpen) {

			}
		});
	}

	/**
	 * bind comment data to CommentFragment
	 * 
	 * @param photo
	 *            : photo data
	 * @param pos
	 *            : postion of photo
	 */
	public void bindCommentDataToView(Photo photo, int pos) {
		CommentFragment commentFragment = (CommentFragment) getActivity().getSupportFragmentManager().findFragmentById(
				R.id.frame2);
		if (commentFragment != null) {
			commentFragment.bindCommentInfo(photo, pos);
		}
		if (pos != 0 && pos % 25 == 0) {
			// Random random = new Random();
			// int tmp = random.nextInt(1);
			// if(tmp == 0 ){
			// showMobileCoreAds(null);
			// }
		}
	}

	/**
	 * set page display of sliding view
	 * 
	 * @param i
	 */
	public void setPage(int i) {
		int currentItem = slidingView.getCurrentItem();
		if (i == 1) {
			if (currentItem != i) {
				slidingView.toogle();
			}
		} else {
			if (currentItem != 0) {
				slidingView.toogle();
			}
		}
	}

	/**
	 * @param postionOfPhoto
	 * @param likeSuccess
	 */
	public void updatePhotoObject(int postionOfPhoto, boolean likeSuccess) {
		photos.get(postionOfPhoto).setIs_user_like(likeSuccess);
		photos.get(postionOfPhoto).setLike_count(photos.get(postionOfPhoto).getLike_count() + 1);
		PhotosFragment mainFragment = (PhotosFragment) getActivity().getSupportFragmentManager().findFragmentById(
				R.id.frame1);
		if (mainFragment != null) {
			int tmp = mainFragment.getCurrentPage();
			if (postionOfPhoto == tmp) {
				bindCommentDataToView(photos.get(postionOfPhoto), postionOfPhoto);
			}
		}
	}

	public int getBackCount() {
		return backCount;
	}

	public void setBackCount(int backCount) {
		this.backCount = backCount;
	}

	Handler handler = new Handler();

	Runnable hideActionBarRunnaable = new Runnable() {

		@Override
		public void run() {
			if (((MainActivity) getActivity()).getSupportActionBar() != null
					&& ((MainActivity) getActivity()).getSupportActionBar().isShowing()) {
				((MainActivity) getActivity()).getSupportActionBar().hide();
			}
		}
	};

	/**
	 * show or hide action using delay time
	 */
	public void toogleActionBar() {
		if (((MainActivity) getActivity()).getSupportActionBar().isShowing()) {
			handler.removeCallbacks(hideActionBarRunnaable);
			handler.postDelayed(hideActionBarRunnaable, 2000);
		} else {
			((MainActivity) getActivity()).getSupportActionBar().show();
			handler.postDelayed(hideActionBarRunnaable, 2000);
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.show_menu) {
			handler.removeCallbacks(hideActionBarRunnaable);
			return true;
		}
		return false;
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		handler.removeCallbacks(hideActionBarRunnaable);
		handler.postDelayed(hideActionBarRunnaable, 2000);
		super.onOptionsMenuClosed(menu);
	}

	/**
	 * @param postionOfPhoto
	 */
	public void updateCommentPhotoObject(int postionOfPhoto) {
		photos.get(postionOfPhoto).setComment_count(photos.get(postionOfPhoto).getComment_count() + 1);
		PhotosFragment mainFragment = (PhotosFragment) getActivity().getSupportFragmentManager().findFragmentById(
				R.id.frame1);
		if (mainFragment != null) {
			int tmp = mainFragment.getCurrentPage();
			if (postionOfPhoto == tmp) {
				bindCommentDataToView(photos.get(postionOfPhoto), postionOfPhoto);
			}
		}
	}

	public int getPosPage() {
		return posPage;
	}

	private boolean needChange = false;

	public void setPosPage(int posPage) {
		if (posPage != this.posPage) {
			this.posPage = posPage;
			needChange = true;
		} else {
			needChange = false;
		}

	}

	public void changepageIfNeed() {
		if (needChange) {
			loadData();
		}
	}
}
