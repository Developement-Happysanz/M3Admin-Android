package com.happysanz.m3admin.activity.piamodule;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.bean.pia.CenterVideo;
import com.happysanz.m3admin.bean.pia.Centers;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.CommonUtils;
import com.happysanz.m3admin.utils.DeveloperKey;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.PreferenceStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.util.Log.d;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class VideoGalleryActivity extends Activity implements YouTubePlayer.OnFullscreenListener, View.OnClickListener, DialogClickListener {

    /**
     * The duration of the animation sliding up the video in portrait.
     */
    private static final int ANIMATION_DURATION_MILLIS = 300;
    /**
     * The padding between the video list and the video in landscape orientation.
     */
    private static final int LANDSCAPE_VIDEO_PADDING_DP = 5;

    private VideoListFragment listFragment;
    private VideoFragment videoFragment;

    private View videoBox;
    private View closeButton;
    private ImageView ivBack;

    private boolean isFullscreen;
    Centers centers;

    private static final String TAG = VideoGalleryActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    protected ArrayList<CenterVideo> centerVideoArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.video_list_demo);
        centers = (Centers) getIntent().getSerializableExtra("cent");
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.add_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddVideoActivity.class);
                startActivity(intent);
            }
        });
        centerVideoArrayList = new ArrayList<>();
        /*ivBack = findViewById(R.id.back_tic_his);
        ivBack.setOnClickListener(this);*/

        listFragment = (VideoListFragment) getFragmentManager().findFragmentById(R.id.list_fragment);
        videoFragment =
                (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);

        videoBox = findViewById(R.id.video_box);
        closeButton = findViewById(R.id.close_button);

        videoBox.setVisibility(View.INVISIBLE);
//        ivBack = findViewById(R.id.back_tic_his);
        /*ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        layout();
//        viewCenterVideos();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        layout();
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;

        layout();
    }

    /**
     * Sets up the layout programatically for the three different states. Portrait, landscape or
     * fullscreen+landscape. This has to be done programmatically because we handle the orientation
     * changes ourselves in order to get fluent fullscreen transitions, so the xml layout resources
     * do not get reloaded.
     */
    private void layout() {
        boolean isPortrait =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

//        listFragment.getView().setVisibility(isFullscreen ? View.GONE : View.VISIBLE);
        listFragment.getView().setVisibility(View.VISIBLE);
        listFragment.setLabelVisibility(isPortrait);
        closeButton.setVisibility(isPortrait ? View.VISIBLE : View.GONE);

        if (isFullscreen) {
            videoBox.setTranslationY(0); // Reset any translation that was applied in portrait.
            setLayoutSize(videoFragment.getView(), MATCH_PARENT, MATCH_PARENT);
            setLayoutSizeAndGravity(videoBox, MATCH_PARENT, MATCH_PARENT, Gravity.TOP | Gravity.LEFT);
        } else if (isPortrait) {
            setLayoutSize(listFragment.getView(), MATCH_PARENT, MATCH_PARENT);
            setLayoutSize(videoFragment.getView(), MATCH_PARENT, WRAP_CONTENT);
            setLayoutSizeAndGravity(videoBox, MATCH_PARENT, WRAP_CONTENT, Gravity.BOTTOM);
        } else {
            videoBox.setTranslationY(0); // Reset any translation that was applied in portrait.
            int screenWidth = dpToPx(getResources().getConfiguration().screenWidthDp);
            setLayoutSize(listFragment.getView(), screenWidth / 4, MATCH_PARENT);
            int videoWidth = screenWidth - screenWidth / 4 - dpToPx(LANDSCAPE_VIDEO_PADDING_DP);
            setLayoutSize(videoFragment.getView(), videoWidth, WRAP_CONTENT);
            setLayoutSizeAndGravity(videoBox, videoWidth, WRAP_CONTENT,
                    Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        }
    }

    public void onClickClose(@SuppressWarnings("unused") View view) {
        listFragment.getListView().clearChoices();
        listFragment.getListView().requestLayout();
        videoFragment.pause();
        videoBox.animate()
                .translationYBy(videoBox.getHeight())
                .setDuration(ANIMATION_DURATION_MILLIS)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        videoBox.setVisibility(View.INVISIBLE);
                    }
                });
    }


    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            finish();
        }
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    /**
     * A fragment that shows a static list of videos.
     */
    public static final class VideoListFragment extends ListFragment implements IServiceListener, DialogClickListener {

        private List<VideoEntry> VIDEO_LIST;
        private static final String TAG = VideoListFragment.class.getName();
        private ServiceHelper serviceHelper;
        private ProgressDialogHelper progressDialogHelper;
        private Context context;
        private PageAdapter adapter;
        private View videoBox;
        private String res = "";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            serviceHelper = new ServiceHelper(getActivity());
            serviceHelper.setServiceListener(this);
            progressDialogHelper = new ProgressDialogHelper(getActivity());

            viewCenterVideos();
//            adapter = new PageAdapter(getActivity(), VIDEO_LIST);

        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            videoBox = getActivity().findViewById(R.id.video_box);
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            setListAdapter(adapter);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            final CharSequence[] options = {"View Video", "Delete Video", "Cancel"};
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
            builder.setTitle("Video");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("View Video")) {
                        String videoId = VIDEO_LIST.get(position).videoId;

                        VideoFragment videoFragment =
                                (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);
                        videoFragment.setVideoId(videoId);

                        // The videoBox is INVISIBLE if no video was previously selected, so we need to show it now.
                        if (videoBox.getVisibility() != View.VISIBLE) {
                            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                // Initially translate off the screen so that it can be animated in from below.
                                videoBox.setTranslationY(videoBox.getHeight());
                            }
                            videoBox.setVisibility(View.VISIBLE);
                        }

                        // If the fragment is off the screen, we animate it in.
                        if (videoBox.getTranslationY() > 0) {
                            videoBox.animate().translationY(0).setDuration(ANIMATION_DURATION_MILLIS);
                        }
                    } else if (options[item].equals("Delete Video")) {
                        String videoId = VIDEO_LIST.get(position).videoName;
                        deleteWishList(videoId);
                    } else if (options[item].equals("Cancel")) {

                        dialog.dismiss();
                    }
                }
            });
            builder.show();


        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();

//            adapter.releaseLoaders();
        }

        public void setLabelVisibility(boolean visible) {
//            adapter.setLabelVisibility(visible);
        }

        @Override
        public void onAlertPositiveClicked(int tag) {

        }

        @Override
        public void onAlertNegativeClicked(int tag) {

        }

        @Override
        public void onResponse(JSONObject response) {
            progressDialogHelper.hideProgressDialog();

            if (validateSignInResponse(response)) {
                try {
                    if (res.equalsIgnoreCase("viewCenterVideos")) {
                        JSONArray getTimeTableArray = response.getJSONArray("centerVideos");
                        List<VideoEntry> list = new ArrayList<VideoEntry>();
                        if (getTimeTableArray != null && getTimeTableArray.length() > 0) {
                            for (int i = 0; i < getTimeTableArray.length(); i++) {
                                JSONObject jsonobj = getTimeTableArray.getJSONObject(i);
                                String videoTitle = "";
                                String videoURL = "";
                                String videoId = "";

                                videoTitle = jsonobj.getString("video_title");
                                videoURL = jsonobj.getString("video_url");
                                videoId = jsonobj.getString("video_id");

                                list.add(new VideoEntry(videoTitle, videoURL, videoId));
                            }
                        }

                        VIDEO_LIST = Collections.unmodifiableList(list);

                        adapter = new PageAdapter(getActivity(), VIDEO_LIST);

                        videoBox = getActivity().findViewById(R.id.video_box);
                        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        setListAdapter(adapter);
                    } else {
                        Toast.makeText(getActivity(), "Video has been deleted!", Toast.LENGTH_SHORT).show();
//                        Fragment currentFragment = getActivity().getFragmentManager().findFragmentById(R.id.video_fragment_container);
//                        if (currentFragment instanceof VideoListFragment) {
//                            FragmentTransaction fragTransaction =   (getActivity()).getFragmentManager().beginTransaction();
//                            fragTransaction.detach(currentFragment);
//                            fragTransaction.attach(currentFragment);
//                            fragTransaction.commit();}
                        getActivity().finish();
                        getActivity().startActivity(getActivity().getIntent());

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onError(String error) {
            progressDialogHelper.hideProgressDialog();
            AlertDialogHelper.showSimpleAlertDialog(getActivity(), error);
        }

        private boolean validateSignInResponse(JSONObject response) {
            boolean signInSuccess = false;
            if ((response != null)) {
                try {
                    String status = response.getString("status");
                    String msg = response.getString(M3AdminConstants.PARAM_MESSAGE);
                    d(TAG, "status val" + status + "msg" + msg);

                    if ((status != null)) {
                        if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                                (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                            signInSuccess = false;
                            d(TAG, "Show error dialog");
                            AlertDialogHelper.showSimpleAlertDialog(getActivity(), msg);

                        } else {
                            signInSuccess = true;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return signInSuccess;
        }

        private void viewCenterVideos() {
            res = "viewCenterVideos";
            JSONObject jsonObject = new JSONObject();
            String userId;
            if (PreferenceStorage.getUserId(getActivity()).equalsIgnoreCase("1")) {
                userId = PreferenceStorage.getPIAProfileId(getActivity());
            } else {
                userId = PreferenceStorage.getUserId(getActivity());
            }
            try {
                jsonObject.put(M3AdminConstants.KEY_USER_ID, userId);
                jsonObject.put(M3AdminConstants.PARAMS_CENTER_ID, PreferenceStorage.getCenterId(getActivity()));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = M3AdminConstants.BUILD_URL + M3AdminConstants.VIEW_VIDEO_GALLERY;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
        }

        private void deleteWishList(String eventId) {
            res = "eventDelete";
            JSONObject jsonObject = new JSONObject();
            String userId;
            if (PreferenceStorage.getUserId(getActivity()).equalsIgnoreCase("1")) {
                userId = PreferenceStorage.getPIAProfileId(getActivity());
            } else {
                userId = PreferenceStorage.getUserId(getActivity());
            }
            try {
                jsonObject.put(M3AdminConstants.KEY_USER_ID, userId);
                jsonObject.put(M3AdminConstants.PARAMS_VIDEO_ID, eventId);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = M3AdminConstants.BUILD_URL + M3AdminConstants.DELETE_VIDEO;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
        }

    }

    /**
     * Adapter for the video list. Manages a set of YouTubeThumbnailViews, including initializing each
     * of them only once and keeping track of the loader of each one. When the ListFragment gets
     * destroyed it releases all the loaders.
     */
    private static final class PageAdapter extends BaseAdapter {

        private final List<VideoEntry> entries;
        private final List<View> entryViews;
        private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
        private final LayoutInflater inflater;
        private final ThumbnailListener thumbnailListener;

        private boolean labelsVisible;

        public PageAdapter(Context context, List<VideoEntry> entries) {
            this.entries = entries;

            entryViews = new ArrayList<View>();
            thumbnailViewToLoaderMap = new HashMap<YouTubeThumbnailView, YouTubeThumbnailLoader>();
            inflater = LayoutInflater.from(context);
            thumbnailListener = new ThumbnailListener();

            labelsVisible = true;
        }

        public void releaseLoaders() {
            for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap.values()) {
                loader.release();
            }
        }

        public void setLabelVisibility(boolean visible) {
            labelsVisible = visible;
            for (View view : entryViews) {
//                view.findViewById(R.id.text).setVisibility(visible ? View.VISIBLE : View.GONE);
                view.findViewById(R.id.text).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getCount() {
            return entries.size();
        }

        @Override
        public VideoEntry getItem(int position) {
            return entries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            VideoEntry entry = entries.get(position);

            // There are three cases here
            if (view == null) {
                // 1) The view has not yet been created - we need to initialize the YouTubeThumbnailView.
                view = inflater.inflate(R.layout.video_list_item, parent, false);
                YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
                thumbnail.setTag(entry.videoId);
                thumbnail.initialize(DeveloperKey.DEVELOPER_KEY, thumbnailListener);
            } else {
                YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
                YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(thumbnail);
                if (loader == null) {
                    // 2) The view is already created, and is currently being initialized. We store the
                    //    current videoId in the tag.
                    thumbnail.setTag(entry.videoId);
                } else {
                    // 3) The view is already created and already initialized. Simply set the right videoId
                    //    on the loader.f
                    thumbnail.setImageResource(R.drawable.loading_thumbnail);
                    loader.setVideo(entry.videoId);
                }
            }
            TextView label = ((TextView) view.findViewById(R.id.text));
            label.setText(entry.text);
            label.setVisibility(labelsVisible ? View.VISIBLE : View.GONE);
            return view;
        }

        private final class ThumbnailListener implements
                YouTubeThumbnailView.OnInitializedListener,
                YouTubeThumbnailLoader.OnThumbnailLoadedListener {

            @Override
            public void onInitializationSuccess(
                    YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
                loader.setOnThumbnailLoadedListener(this);
                thumbnailViewToLoaderMap.put(view, loader);
                view.setImageResource(R.drawable.loading_thumbnail);
                String videoId = (String) view.getTag();
                loader.setVideo(videoId);
            }

            @Override
            public void onInitializationFailure(
                    YouTubeThumbnailView view, YouTubeInitializationResult loader) {
                view.setImageResource(R.drawable.no_thumbnail);
            }

            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {
            }

            @Override
            public void onThumbnailError(YouTubeThumbnailView view, YouTubeThumbnailLoader.ErrorReason errorReason) {
                view.setImageResource(R.drawable.no_thumbnail);
            }
        }

    }

    public static final class VideoFragment extends YouTubePlayerFragment
            implements YouTubePlayer.OnInitializedListener {

        private YouTubePlayer player;
        private String videoId;

        public static VideoFragment newInstance() {
            return new VideoFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            initialize(DeveloperKey.DEVELOPER_KEY, this);
        }

        @Override
        public void onDestroy() {
            if (player != null) {
                player.release();
            }
            super.onDestroy();
        }

        public void setVideoId(String videoId) {
            if (videoId != null && !videoId.equals(this.videoId)) {
                this.videoId = videoId;
                if (player != null) {
                    player.cueVideo(videoId);
                }
            }
        }

        public void pause() {
            if (player != null) {
                player.pause();
            }
        }

        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean restored) {
            this.player = player;
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
            player.setOnFullscreenListener((VideoGalleryActivity) getActivity());
            if (!restored && videoId != null) {
                player.cueVideo(videoId);
            }
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
            this.player = null;
        }

    }

    private static final class VideoEntry {
        private final String text;
        private final String videoId;
        private final String videoName;

        public VideoEntry(String text, String videoId, String videoName) {
            this.text = text;
            this.videoId = videoId;
            this.videoName = videoName;
        }

    }

    // Utility methods for layouting.

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private static void setLayoutSize(View view, int width, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    private static void setLayoutSizeAndGravity(View view, int width, int height, int gravity) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        params.gravity = gravity;
        view.setLayoutParams(params);
    }

}