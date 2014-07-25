package com.bestfunforever.prettygirl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bestfunforever.activity.facebook.ILikeFacebook;
import com.bestfunforever.activity.facebook.ILoginFacebook;
import com.bestfunforever.activity.facebook.IPostCommentFacebook;
import com.bestfunforever.activity.facebook.IUserFaceBookListenner;
import com.bestfunforever.util.CommonUtils;
import com.bestfunforever.view.dialog.DialogUtil;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.thichngamgaixinh.model.Comment;
import com.thichngamgaixinh.model.Photo;
import com.thichngamgaixinh.model.User;

public class CommentFragment extends Fragment {
	private EditText edittextComment;
	private Button btnComment;
	private LinearLayout likeFb;
	private LinearLayout commentFb;
	private ImageView wallpapperFb;
	private ImageView shareFb;
	private TextView likeFbTextView;
	private TextView commentFbTextView;
	private TextView loadingText;
	private ListView mListView;
	private LinearLayout loadCommentLL;
	private LinearLayout loadmorecommentll;
	private ProgressBar loadmorecommentprogress;
	private TextView loadingmorecommenttxt;
	private CommentAdapter mAdapter;
	private String photoId;
	private boolean loadComment = false;
	private int commentCount;
	
	private int commentOffset;

	private String source;

	private ArrayList<Comment> comments = new ArrayList<Comment>();
	protected String linkPhoto;
	private int postionOfPhoto;

	/**
	 * like a comment listenner
	 */
	private OnClickListener likeCommentListenner = new OnClickListener() {

		@Override
		public void onClick(View view) {
			final int pos = (Integer) view.getTag();
			if (comments != null && comments.size() > pos) {
				final String commentId = comments.get(pos).getId();
				if (Session.getActiveSession() != null&&Session.getActiveSession().isOpened()) {
					likeComment(commentId, pos);
				} else {
					((MainActivity) getActivity()).loginFacebook(null,new String[] {
							"publish_stream" },
							new ILoginFacebook() {

								@Override
								public void onLoginFacebookSuccess() {
									likeComment(commentId, pos);
								}

								@Override
								public void onLoginFacebookFail(
										Session session, SessionState state,
										Exception exception) {

								}

							});
				}
			}

		}
	};

	/**
	 * unlike a comment listenner
	 */
	private OnClickListener unlikeCommentListenner = new OnClickListener() {

		@Override
		public void onClick(View view) {
			final int pos = (Integer) view.getTag();
			if (comments != null && comments.size() > pos) {
				final String commentId = comments.get(pos).getId();
				if (Session.getActiveSession() != null&&Session.getActiveSession().isOpened()) {
					unLikeComment(commentId, pos);
				} else {
					((MainActivity) getActivity()).loginFacebook(null,new String[] {
					"publish_stream"} ,
							new ILoginFacebook() {

								@Override
								public void onLoginFacebookSuccess() {
									unLikeComment(commentId, pos);
								}

								@Override
								public void onLoginFacebookFail(
										Session session, SessionState state,
										Exception exception) {

								}

							});
				}
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.commentfragment, null);
		initView(view);
		return view;
	}

	/**
	 * unlike a comment
	 * 
	 * @param commentId
	 * @param pos
	 */
	protected void unLikeComment(String commentId,final int pos) {
		((MainActivity) getActivity()).unLikeFacebook(commentId,
				new ILikeFacebook() {

					@Override
					public void onLikeFacebookSuccess() {
						if (mListView != null) {
							View view = mListView.findViewWithTag(pos);
							if (view != null) {
								TextView txtThich = (TextView) view
										.findViewById(R.id.txtThich);
								TextView txtBoThich = (TextView) view
										.findViewById(R.id.txtBoThich);
								TextView txtLikeCOmmentCount = (TextView) view
										.findViewById(R.id.txtLikeCOmmentCount);
								txtThich.setVisibility(View.VISIBLE);
								txtBoThich.setVisibility(View.GONE);
								int count = (Integer) txtLikeCOmmentCount
										.getTag();
								txtLikeCOmmentCount.setText((count + 1) + "");
							}
						}
					}

					@Override
					public void onLikeFacebookFail() {

					}
				});
	}

	/**
	 * like a comment
	 * 
	 * @param commentId
	 * @param pos
	 */
	protected void likeComment(String commentId, final int pos) {
		((MainActivity) getActivity()).likeFacebook(commentId,
				new ILikeFacebook() {

					@Override
					public void onLikeFacebookSuccess() {
						if (mListView != null) {
							View view = mListView.findViewWithTag(pos);
							if (view != null) {
								TextView txtThich = (TextView) view
										.findViewById(R.id.txtThich);
								TextView txtBoThich = (TextView) view
										.findViewById(R.id.txtBoThich);
								TextView txtLikeCOmmentCount = (TextView) view
										.findViewById(R.id.txtLikeCOmmentCount);
								txtThich.setVisibility(View.GONE);
								txtBoThich.setVisibility(View.VISIBLE);
								int count = (Integer) txtLikeCOmmentCount
										.getTag();
								txtLikeCOmmentCount.setText((count + 1) + "");
							}
						}
					}

					@Override
					public void onLikeFacebookFail() {

					}
				});
	}

	private void initView(View view) {
		edittextComment = (EditText) view.findViewById(R.id.edittextComment);
		btnComment = (Button) view.findViewById(R.id.btnComment);
		likeFb = (LinearLayout) view.findViewById(R.id.imageLike);
		loadCommentLL = (LinearLayout) view.findViewById(R.id.loadcommentll);
		loadmorecommentll = (LinearLayout) view
				.findViewById(R.id.loadmorecommentll);
		commentFb = (LinearLayout) view.findViewById(R.id.imageComment);
		wallpapperFb = (ImageView) view.findViewById(R.id.wallpaper);
		shareFb = (ImageView) view.findViewById(R.id.shareFB);
		likeFbTextView = (TextView) view.findViewById(R.id.txtLike);
		loadingmorecommenttxt = (TextView) view
				.findViewById(R.id.loadingmorecommenttxt);
		commentFbTextView = (TextView) view.findViewById(R.id.txtComment);
		loadingText = (TextView) view.findViewById(R.id.loadingtxt);
		mListView = (ListView) view.findViewById(R.id.list);
		loadmorecommentprogress = (ProgressBar) view
				.findViewById(R.id.loadmorecommentprogress);
		
		btnComment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				final String msg = edittextComment.getText().toString();
				if(msg!=null&&!msg.equals("")){
					if (Session.getActiveSession() != null&&Session.getActiveSession().isOpened()) {
						if(((MainActivity) getActivity()).getActiveUserFacebook()!=null){
							postComment(msg);
						}else{
							((MainActivity) getActivity()).getUserInfo(new IUserFaceBookListenner(){

								@Override
								public void onGetUserInfoSuccess(GraphUser user) {
									postComment(msg);
								}
							});
						}
						
					} else {
						((MainActivity) getActivity()).loginFacebook(new String[] {"basic_info "},new String[] {
						"publish_stream"} ,
								new ILoginFacebook() {

									@Override
									public void onLoginFacebookSuccess() {
										postComment(msg);
									}

									@Override
									public void onLoginFacebookFail(
											Session session, SessionState state,
											Exception exception) {

									}

								});
					}
				}
			}
		});

		loadingmorecommenttxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (comments.size() < commentCount) {
					loadmorecommentll.setVisibility(View.VISIBLE);
					loadingmorecommenttxt.setText(getString(R.string.loading));
					loadmorecommentprogress.setVisibility(View.GONE);
					loadCommen();
				} else {
					loadmorecommentll.setVisibility(View.GONE);
				}
			}
		});

		likeFb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.e("", "click like fb ");
				if (Session.getActiveSession() != null&&Session.getActiveSession().isOpened()) {
					likePhoto();
				} else {
					((MainActivity) getActivity()).loginFacebook(new String[] {"basic_info "},new String[] {
					"publish_stream" },
							new ILoginFacebook() {

								@Override
								public void onLoginFacebookSuccess() {
									likePhoto();
								}

								@Override
								public void onLoginFacebookFail(
										Session session, SessionState state,
										Exception exception) {

								}

							});
				}
			}
		});

		commentFb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.e("", "click comment fb ");
				((MainActivity) getActivity()).setPage(1);
			}
		});

		wallpapperFb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.e("", "click wallpp fb ");
				showMessageConfirmSetWallPapper();
			}
		});

		shareFb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.e("", "click share fb ");
				((MainActivity) getActivity()).shareFacebook(
						getString(R.string.app_name), linkPhoto,source);
			}
		});
	}

	/**
	 * create a comment
	 * @param msg
	 */
	protected void postComment(final String msg) {
		((MainActivity) getActivity()).postCommentFacebook(photoId, msg, new IPostCommentFacebook() {

			@Override
			public void onPostCommentFacebookSuccess(String postId) {
				if(((MainActivity) getActivity()).getActiveUserFacebook()!=null){
					edittextComment.setText("");
					Comment comment = new Comment();
					comment.setUser_likes(false);
					comment.setMessage(msg);
					long unixTime = System.currentTimeMillis() / 1000L;
					comment.setCreated_time(unixTime+"");
					comment.setId(postId);
					User user = new User();
					user.setId(((MainActivity) getActivity()).getActiveUserFacebook().getId()+"");
					user.setName(((MainActivity) getActivity()).getActiveUserFacebook().getName()+"");
					comment.setUser(user);
					comments.add(comment);
					bindData(comments);
					
					((MainActivity) getActivity()).updateCommentPhotoObject(
							postionOfPhoto);
				}
				
			}

			@Override
			public void onPostCommentFacebookFail() {
				
			}
		});
	}

	/**
	 * like a photo
	 */
	protected void likePhoto() {
		((MainActivity) getActivity()).likeFacebook(photoId,
				new ILikeFacebook() {

					@Override
					public void onLikeFacebookSuccess() {
						((MainActivity) getActivity()).updatePhotoObject(
								postionOfPhoto, true);
						Toast.makeText(getActivity(),
								getString(R.string.likesuccess),
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onLikeFacebookFail() {
						Toast.makeText(getActivity(),
								getString(R.string.likefail),
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	/**
	 * show message confirm user set wall paper
	 */
	protected void showMessageConfirmSetWallPapper() {
		DialogUtil.showMessageDialog(getActivity(),
				R.string.wallpaper, R.string.confirm_set_wall_paper,
				R.string.Ok, new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						setWallPaper();
					}
				}, R.string.close, new OnClickListener() {

					@Override
					public void onClick(View arg0) {

					}
				});
	}

	/**
	 * set wall paper
	 */
	protected void setWallPaper() {
		if (source != null && !source.equals(""))
			ImageLoader.getInstance().loadImage(source,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							super.onLoadingComplete(imageUri, view, loadedImage);
							WallpaperManager myWallpaperManager = WallpaperManager
									.getInstance(getActivity()
											.getApplicationContext());
							myWallpaperManager.suggestDesiredDimensions(
									CommonUtils.getWidthScreen(getActivity()
											.getApplicationContext()),
									CommonUtils.getHeightScreen(getActivity()
											.getApplicationContext()));
							try {
								myWallpaperManager.setBitmap(loadedImage);
								Toast.makeText(
										getActivity(),
										getString(R.string.setwallpapersuccess),
										Toast.LENGTH_SHORT).show();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
	}

	/**
	 * process response of more comment
	 * 
	 * @param response
	 * @throws JSONException
	 */
	protected void processResponse(Response response) throws JSONException {
		GraphObject graphObject = response.getGraphObject();
		if (graphObject != null) {
			JSONObject grap = graphObject.getInnerJSONObject();
			JSONArray array = grap.getJSONArray("data");
			Log.e("",
					"comment size " + comments.size() + " data size "
							+ array.length());
			if (array.length() > 0) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					Comment comment = new Comment();
					String fromId = object.getString("fromid");
					String text = object.getString("text");
					String time = object.getString("time");
					String object_id = object.getString("object_id");
					int likes = object.getInt("likes");
					comment.setCreated_time(time);
					comment.setLike_count(likes);
					comment.setId(object_id);
					comment.setMessage(text);
					Log.e("", "object " + object.toString());
					User user = new User();
					user.setId(fromId);
					comment.setUser(user);
					comments.add(comment);
				}
				loadUserComment(array.length());
			} else {
				loadmorecommentll.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * load user for comments
	 * 
	 * @param size
	 *            : size of new comment
	 */
	private void loadUserComment(int size) {
		if (CommonUtils.checkNetworkAvaliable(getActivity()
				.getApplicationContext())) {
			Log.e("", "photo id " + photoId);
			String fqlquery = "select name from user where uid IN (";
			for (int i = commentOffset; i < commentOffset + size; i++) {
				if (i == commentOffset + size - 1) {
					fqlquery += comments.get(i).getUser().getId() + ")";
				} else {
					fqlquery += comments.get(i).getUser().getId() + ",";
				}
			}
			Bundle bundle = new Bundle();
			bundle.putString("q", fqlquery);

			Request request = new Request(null, "/fql", bundle, HttpMethod.GET,
					new Callback() {

						@Override
						public void onCompleted(Response response) {
							try {
								processUserResponse(response);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
			RequestAsyncTask asyncTask = new RequestAsyncTask(request);
			asyncTask.execute();
		} else {
			loadingText.setText(getString(R.string.loaderror));
		}
	}

	/**
	 * process user response
	 * 
	 * @param response
	 * @throws JSONException
	 */
	protected void processUserResponse(Response response) throws JSONException {
		GraphObject graphObject = response.getGraphObject();
		if (graphObject != null) {
			JSONObject grap = graphObject.getInnerJSONObject();
			JSONArray array = grap.getJSONArray("data");
			if (array.length() > 0) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					String name = object.getString("name");
					comments.get(commentOffset + i).getUser().setName(name);
				}
				commentOffset += array.length();
				bindData(comments);
			}
		}
	}

	/**
	 * bind comment info to view
	 * 
	 * @param photo
	 * @param postionOfPhoto
	 */
	public void bindCommentInfo(Photo photo, int postionOfPhoto) {
		likeFbTextView.setText(photo.getLike_count() + "");
		commentCount = photo.getComment_count();
		commentFbTextView.setText(commentCount + "");
		String photo_id = photo.getId();
		if(photo_id==null){
			photoId = "";
			loadComment = true;
			source = "";
			this.postionOfPhoto = postionOfPhoto;
			linkPhoto = "";
			loadComment = true;
			commentOffset = 0;
			comments.clear();
		}else{
			if (!photo_id.equals(photoId)) {
				photoId = photo_id;
				source = photo.getSource();
				this.postionOfPhoto = postionOfPhoto;
				linkPhoto = photo.getLink();
				loadComment = true;
				commentOffset = 0;
				comments.clear();
			} else {
				loadComment = false;
			}
		}
	}

	/**
	 * load current comment to view
	 */
	public void loadCommentToView() {
		Log.e("", "loadComment " + loadComment);
		if (loadComment) {
			loadCommentLL.setVisibility(View.VISIBLE);
			loadingText.setText(getString(R.string.loading));
			mListView.setVisibility(View.INVISIBLE);
			loadCommen();
		} else {
			loadCommentLL.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * send request load comment Fb
	 */
	private void loadCommen() {
		loadComment = false;
		if (CommonUtils.checkNetworkAvaliable(getActivity()
				.getApplicationContext())) {
			Log.e("", "photo id " + photoId);
			String fqlQuery = "SELECT fromid, text, time, object_id,time,likes FROM comment where object_id='"
					+ photoId
					+ "'  order by time desc limit 25 offset "
					+ commentOffset;
			Bundle bundle = new Bundle();
			bundle.putString("q", fqlQuery);

			Request request = new Request(null, "/fql", bundle, HttpMethod.GET,
					new Callback() {

						@Override
						public void onCompleted(Response response) {
							try {
								processResponse(response);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
			RequestAsyncTask asyncTask = new RequestAsyncTask(request);
			asyncTask.execute();
		} else {
			loadingText.setText(getString(R.string.loaderror));
		}
	}

	/**
	 * create list view data
	 * 
	 * @param list_comments
	 */
	private void bindData(ArrayList<Comment> list_comments) {
		Collections.sort(comments, new TimeCommentComparator());
		if (mAdapter == null) {
			mAdapter = new CommentAdapter(
					getActivity().getApplicationContext(),
					likeCommentListenner, unlikeCommentListenner);
			mListView.setAdapter(mAdapter);
		}
		mAdapter.setData(comments);
		loadCommentLL.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
		if (comments.size() < commentCount) {
			loadmorecommentll.setVisibility(View.VISIBLE);
		} else {
			loadmorecommentll.setVisibility(View.GONE);
		}
		loadmorecommentprogress.setVisibility(View.GONE);
		loadingmorecommenttxt.setText(getString(R.string.loadpreveouscomment));
	}

}
