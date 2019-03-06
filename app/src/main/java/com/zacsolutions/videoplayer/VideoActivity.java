package com.zacsolutions.videoplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.swipper.library.Swipper;


public class VideoActivity extends Swipper {

    private static final String TAG = VideoActivity.class.getName();
    private SimpleExoPlayer mExoPlayer;
    private VideoView mPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private SharedPreferences sharedPreferences;
    private String mCurrentVideo;
    private boolean mSwitch = true;
    ActionBar actionBar;
    ImageView btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "In create state");
        // remove title


        setContentView(R.layout.activity_video);


        // Initialize the player view.
        mPlayerView = (VideoView) findViewById(R.id.playerView);
btn=(ImageView) findViewById(R.id.btn);
        mPlayerView.setHorizontalScrollBarEnabled(true);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

changeScreenOrientation();
            }
        });




        sharedPreferences = getPreferences(MODE_PRIVATE);

        // Initialize the Media Session.
    //    initializeMediaSession();


        // Initialize the player.
        Intent i = getIntent();
        if (i != null) {
            if (i.getStringExtra("URL") != null) {
                setTitle(i.getStringExtra("TITLE"));
                mCurrentVideo = i.getStringExtra("URL");
                //initializePlayer(Uri.parse(i.getStringExtra("URL")));



            } else {
                setTitle("Select Video");
                //initializePlayer(Uri.parse("http://fs.evonetbd.com/Media/Movies/English%20Movies/2017/Wonder%20Woman%20%20(2017)/Sample.mp4"));

            }
        }
        //Log.i("SeeITNow",loadFileData("/sdcard/video/Beauty.and.the.Beast.2017.BRRip.XviD.AC3-iFT.srt"));

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_item, menu);
//        return true;
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_search:
//                Intent intent = new Intent(this, BrowseDirectoryActivity.class);
//                startActivity(intent);
//                break;
//            case android.R.id.home:
//                Intent goHomeIntent = new Intent(VideoActivity.this, BrowseDirectoryActivity.class);
//                startActivity(goHomeIntent);
//        }
//        return true;
//    }
//
//    /**
//     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
//     * and media controller.
//     */
//    private void initializeMediaSession() {
//
//        // Create a MediaSessionCompat.
//        mMediaSession = new MediaSessionCompat(this, TAG);
//
//        // Enable callbacks from MediaButtons and TransportControls.
//        mMediaSession.setFlags(
//                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
//                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
//
//        // Do not let MediaButtons restart the player when the app is not visible.
//        mMediaSession.setMediaButtonReceiver(null);
//
//        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
//        mStateBuilder = new PlaybackStateCompat.Builder()
//                .setActions(
//                        PlaybackStateCompat.ACTION_PLAY |
//                                PlaybackStateCompat.ACTION_PAUSE |
//                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
//                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
//
//        mMediaSession.setPlaybackState(mStateBuilder.build());
//
//
//        // MySessionCallback has methods that handle callbacks from a media controller.
//        mMediaSession.setCallback(new MySessionCallback());
//
//        // Start the Media Session since the activity is active.
//        mMediaSession.setActive(true);
//
//
//    }


    /**
     * Initialize ExoPlayer.
     *
//     * @param videoUri The URI of the sample to play.
//     */
//    private void initializePlayer(Uri videoUri) {
//        if (mExoPlayer == null) {
//            // Create an instance of the ExoPlayer.
//            TrackSelector trackSelector = new DefaultTrackSevlector();
//            LoadControl loadControl = new DefaultLoadControl();
//            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
//            mPlayerView.setVideoPath(mCurrentVideo);
//
//
//            mExoPlayer.addListener(this);
//
//            // Prepare the MediaSource.
//            String userAgent = Util.getUserAgent(this, "VideoPlayBack");
//            MediaSource videoSource = new ExtractorMediaSource(videoUri, new DefaultDataSourceFactory(
//                    this, userAgent), new DefaultExtractorsFactory(), null, null);
//
//            if (videoUri.toString().contains("Beauty.and.the.Beast")) {
//
//                Format textFormat = Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP,
//                        null, Format.NO_VALUE, Format.NO_VALUE, "en", null);
//
//                //place srt file of a video incase
//                //this could be done by allowing user to pic srt manually
//                Uri srtFile = Uri.parse("/sdcard/video/Beauty.and.the.Beast.2017.BRRip.XviD.AC3-iFT.srt");
//                MediaSource subTitleSource = new SingleSampleMediaSource(
//                        srtFile,
//                        new DefaultDataSourceFactory(this, userAgent),
//                        textFormat, C.TIME_UNSET);
//
//                MediaSource mediaSourceWithText = new MergingMediaSource(videoSource, subTitleSource);
//                mExoPlayer.prepare(mediaSourceWithText);
//                //for looping of a video
//                //LoopingMediaSource loopingSource = new LoopingMediaSource(source);
//            } else {
//                mExoPlayer.prepare(videoSource);
//            }
//
//            mExoPlayer.setPlayWhenReady(true);
//        }
//    }

//    public static String loadFileData(String srtFile){
//        //File sdcardDir = Environment.getExternalStorageDirectory();
//
//        File srt =new File(srtFile);
//        StringBuilder textFile = new StringBuilder();
//
//        try{
//            BufferedReader bufferedReader = new BufferedReader(new FileReader(srt));
//            String line;
//
//            while ((line = bufferedReader.readLine()) != null) {
//                textFile.append(line + '\n');
//            }
//            bufferedReader.close();
//        }catch(Exception e){
//
//        }
//        return textFile.toString();
//    }


    /**
     * Release ExoPlayer.
     */
//    private void releasePlayer() {
//        if (mExoPlayer != null) {
//            mExoPlayer.release();
//            mExoPlayer = null;
//        }
//    }
//
//    /**
//     * Release the player when the activity is destroyed.
//     */
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        releasePlayer();
//        mMediaSession.setActive(false);
//        Log.d(TAG, "In Destroy state");
//    }
//
//
//    // ExoPlayer Event Listeners
//
//    @Override
//    public void onTimelineChanged(Timeline timeline, Object manifest) {
//    }

//    @Override
//    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
//
//
//    }
//
//    @Override
//    public void onLoadingChanged(boolean isLoading) {
//    }

    /**
     * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
     * PlayBackState to keep in sync, and post the media notification.
     *
     * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
     * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
     *                      STATE_BUFFERING, or STATE_ENDED.
     */
//    @Override
//    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
//            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
//                    mExoPlayer.getCurrentPosition(), 1f);
//        } else if ((playbackState == ExoPlayer.STATE_READY)) {
//            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
//                    mExoPlayer.getCurrentPosition(), 1f);
//        }
//        mMediaSession.setPlaybackState(mStateBuilder.build());
//    }
//
//    @Override
//    public void onPlayerError(ExoPlaybackException error) {
//    }
//
//    @Override
//    public void onPositionDiscontinuity() {
//    }


    /**
     * Media Session Callbacks, where all external clients control the player.
     */
//    private class MySessionCallback extends MediaSessionCompat.Callback {
//        @Override
//        public void onPlay() {
//            mExoPlayer.setPlayWhenReady(true);
//        }
//
//        @Override
//        public void onPause() {
//            mExoPlayer.setPlayWhenReady(false);
//        }
//
//        @Override
//        public void onSkipToPrevious() {
//            mExoPlayer.seekTo(0);
//        }
//    }

    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
//    public static class MediaReceiver extends BroadcastReceiver {
//
//        public MediaReceiver() {
//        }
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            MediaButtonReceiver.handleIntent(mMediaSession, intent);
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.d(TAG, "In start state");
//        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
//        mCurrentVideo = sharedPreferences.getString("URL", "asset:///big_buggy_bunny.mp4");
//    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.d(TAG, "In stop state");
//        if (mExoPlayer != null) {
//            try {
//                mExoPlayer.stop();
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putLong("POINTER", mExoPlayer.getCurrentPosition());
//                editor.putString("URL", mCurrentVideo);
//                editor.commit();
//            } catch (Exception e) {
//
//            }
//        }
//    }

    private void changeScreenOrientation() {
        int orientation = VideoActivity.this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }

    }
}

